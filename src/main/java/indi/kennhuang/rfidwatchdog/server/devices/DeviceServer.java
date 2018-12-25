package indi.kennhuang.rfidwatchdog.server.devices;

import indi.kennhuang.rfidwatchdog.server.util.logging.LogType;
import indi.kennhuang.rfidwatchdog.server.util.logging.WatchDogLogger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DeviceServer  implements Runnable {
    public static int serverPort;

    private static ServerSocket server = null;
    private static boolean shutdown = false;
    private ExecutorService threadExecutor;
    private List<Future> futures = new ArrayList<>();
    private Object lockFutures = new Object();

    public DeviceServer(){
        serverPort = 6083;
    }

    public DeviceServer(int port){
        serverPort = port;
    }

    @Override
    public void run() {
        Thread.currentThread().setName("DeviceServer");
        threadExecutor = Executors.newCachedThreadPool();

        WatchDogLogger logger = new WatchDogLogger(LogType.HardwareServer);

        new Thread(() -> {
            while(true){
                List<Future> futures;
                synchronized (lockFutures){
                    futures = this.futures;
                }
                for(Future future:futures){
                    try {
                        if(future.get() == null){
                            futures.remove(future);
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        try {
            logger.info("device Server Starting on Port "+serverPort);
            server = new ServerSocket(serverPort);
            while (!shutdown) {
                Socket socket = server.accept();
                synchronized (lockFutures){
                    futures.add(threadExecutor.submit(new DeviceHandler(socket)));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (threadExecutor != null)
                threadExecutor.shutdown();
            if (server != null)
                try {
                    server.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    public int getStatus(){
        if(threadExecutor == null){
            return 1;
        }
        else if(threadExecutor.isShutdown()){
            return 2;
        } else if(threadExecutor.isTerminated()){
            return 3;
        }
        else if(threadExecutor.isTerminated()&&threadExecutor.isShutdown()){
            return 4;
        }

        return 0;
    }

    public int getConnectionsCount(){
        synchronized (lockFutures){
            return futures.size();
        }
    }
}
