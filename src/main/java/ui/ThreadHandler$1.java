package ui;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadHandler$1 {

    private static ExecutorService executorService;
    public void start(){
        executorService = Executors.newSingleThreadExecutor();
        executorService.submit(new WebManager());
    }
    public void stop(){ executorService.shutdownNow(); }

}
