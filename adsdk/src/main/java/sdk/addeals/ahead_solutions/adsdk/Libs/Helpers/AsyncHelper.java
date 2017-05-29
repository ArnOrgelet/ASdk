package sdk.addeals.ahead_solutions.adsdk.Libs.Helpers;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Created by ArnOr on 21/05/2017.
 */

public class AsyncHelper {
    public static <T> Future<T> execute(ExecutorService executor, Callable<T> fn){
        return executor.submit(fn);
    }
    public static <T> T await(ExecutorService executor, Callable<T> fn){
        try{
            return execute(executor, fn).get();
        }
        catch(java.lang.InterruptedException ex){}
        catch(java.util.concurrent.ExecutionException ex) {}
        return null;
    }
    public static void await(ExecutorService executor, Runnable fn){
        Callable fnCallable = new Callable() {
            @Override
            public Void call() throws Exception {
                fn.run();
                return null;
            }
        };
        try{
            execute(executor, fnCallable).get();
        }
        catch(java.lang.InterruptedException ex){}
        catch(java.util.concurrent.ExecutionException ex) {}
    }
}
