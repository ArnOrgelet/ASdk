package sdk.addeals.ahead_solutions.adsdk.EventModels;

/**
 * Created by ArnOr on 09/05/2017.
 */

public class EventListener implements IEventListener{
    public EventListener(){}

    @Override
    public void update(Event event) {
        event.action(this);
    }
}
