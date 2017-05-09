package sdk.addeals.ahead_solutions.adsdk.EventModels;

/**
 * Created by ArnOr on 09/05/2017.
 */

public abstract class /*interface*/ Event<E extends IEventListener> {
    private Object Source = null;
    public abstract void action(E listener);//    public Object Source = null;
    public void setSource(Object source){
        Source = source;
    }
    public Object getSource(){
        return Source;
    }
}
