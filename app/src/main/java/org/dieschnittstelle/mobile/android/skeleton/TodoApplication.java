package org.dieschnittstelle.mobile.android.skeleton;

import android.app.Application;
import android.util.Log;

import org.dieschnittstelle.mobile.android.skeleton.model.ITodoCRUDOperations;
import org.dieschnittstelle.mobile.android.skeleton.model.RetrofitRemoteTodoCRUDOperations;
import org.dieschnittstelle.mobile.android.skeleton.model.RoomLocalTodoCRUDOperations;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class TodoApplication extends Application {

    private ITodoCRUDOperations crudOperations;

    @Override
    public void onCreate(){
        super.onCreate();
        try {
            if (checkConnectivity().get()) {
                this.crudOperations = new RetrofitRemoteTodoCRUDOperations();
            } else {
                this.crudOperations = new RoomLocalTodoCRUDOperations(this);
            }
        }catch (Exception e){
            throw new RuntimeException("Got exception trying to run future for checking connectivity: " + e);
        }

    }

    public ITodoCRUDOperations getCrudOperations(){
        return  this.crudOperations;
    }

    public Future<Boolean> checkConnectivity(){
        //flugmodus... wlan.. whatever
        CompletableFuture<Boolean> result = new CompletableFuture<>();
        new Thread(() -> {
            try {
                HttpURLConnection con = (HttpURLConnection) new URL("http://10.0.2.2:8080/api/todos").openConnection();
                con.setReadTimeout(500);
                con.setConnectTimeout(500);
                con.setRequestMethod("GET");
                con.setDoInput(true);
                con.connect();
                con.getInputStream();
                result.complete(true);
            }catch (Exception e) {
                Log.e("TodoApplication", "Got exception trying to check connectivity: " + e);
                result.complete(false);
            }}).start();
        return result;
    }
}
