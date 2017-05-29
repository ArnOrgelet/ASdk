package sdk.addeals.ahead_solutions.adsdk.EventModels;

/**
 * Created by ArnOr on 09/05/2017.
 */

public class EventManager<T extends IEventListener> {
    protected Object sender;
    protected Observable<T> obs;
    public EventManager(Observable<T> _obs/*Object _sender*/){
        /*sender = _sender;*/
        obs = _obs;
    }
    public void trigger(Event event/*, Observable receiver*/){
        obs.notifyAll(event);
    }
    public Object getSender(){
        return sender;
    }
}
