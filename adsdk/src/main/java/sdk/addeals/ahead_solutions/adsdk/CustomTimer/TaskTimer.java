package sdk.addeals.ahead_solutions.adsdk.CustomTimer;

import android.os.Handler;

import java.util.Timer;

/**
 * Created by ArnOr on 26/05/2017.
 */

public class MyTimer {
    Timer myTimer = new Timer();
    Handler myTimerHandler = new Handler();
    Runnable myTimerAction;
    boolean changeTimerPeriod = false;
    private void stopTimer(){
        myTimer.cancel();
        myTimer = new Timer();
    }
    private void startTimer(long period){
        startTimer(0, period);
    }
    private void startTimer(long delay, long period){
        if(delay > 0)
            myTimer.scheduleAtFixedRate(refreshTimerTask, delay, period);
    }
    private void startDelay(long period){
        startDelay(0, period);
    }
    private void startDelay(long delay, long period){
        if(delay > 0)
            myTimer.schedule(refreshTimerTask, delay, period);
    }
}
