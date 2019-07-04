package com.golriz.gpstracker.Core;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class TimerService extends Service {


    Handler handler = new Handler();
    int delay = 1000; //milliseconds


    public TimerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        handler.postDelayed(new Runnable() {
            public void run() {
                Log.i("handler", "run: handler.....");
                handler.postDelayed(this, delay);
            }
        }, delay);
        return Service.START_STICKY;
    }

    @Override
    public boolean stopService(Intent name) {
        return super.stopService(name);

    }
}
