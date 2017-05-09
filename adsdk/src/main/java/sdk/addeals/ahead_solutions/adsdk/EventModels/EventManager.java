package sdk.addeals.ahead_solutions.adsdk.EventModels;

/**
 * Created by ArnOr on 09/05/2017.
 */

public class EventManager {
    protected Object sender;
    protected Observable<IEventListener> obs;
    public EventManager(Observable<IEventListener> _obs/*Object _sender*/){
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
