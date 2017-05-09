package sdk.addeals.ahead_solutions.adsdk.EventModels;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Created by ArnOr on 09/05/2017.
 */

public class Observable<T extends Observer> {
    private List<T> listeners = new ArrayList<T>();;
    public Observable(){
        /*listeners = new ArrayList<T>();*/
    }
    public void subscribe(T observer){
        listeners.add(observer);
    }
    public void unsubscribe(T observer){
        /*int idx = listeners.indexOf(observer);
        if(idx >= 0)
            listeners.remove(idx);*/
        listeners.remove(observer);
    }
    public void unsubscribeAll(){
        listeners.clear();
    }
    public void notifyAll(Event evt){
        for(T subscriber : listeners) {
            if(subscriber.getClass().isAssignableFrom(evt.getClass()))
                subscriber.update(evt);
        }
    }
    /*
    protected T findSubscriber(String codeIsIn) {
        listeners.contains()
        Predicate<T> predicate = observer -> observer.getCodeIsin.equals(codeIsin);
        Carnet  obj = list.stream().filter(predicate).findFirst().get();
        return obj;
        for(T subscriber : listeners) {
            if(subscriber.getCodeIsIn().equals(codeIsIn)) {
                return carnet;
            }
        }
        return null;
    }*/
}
