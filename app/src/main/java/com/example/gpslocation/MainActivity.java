package com.example.gpslocation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements LocationListener {

    public Location location;
    private NotificationManagerCompat notificationManager;
    public TextView textViewResult;
    public TextView textView;
    public ProgressBar progressBar;
    final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(180, TimeUnit.SECONDS)
            .connectTimeout(180, TimeUnit.SECONDS)
            .build();
    Retrofit retrofit=new Retrofit.Builder()
            .baseUrl("http://192.168.1.106:5000/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        notificationManager =NotificationManagerCompat.from(this);

        textViewResult = findViewById(R.id.textViewResult);
        textView=findViewById(R.id.textView);
        progressBar=findViewById(R.id.progressBar);


        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }else{
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 500, this);

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults){
        switch (requestCode){
            case 1: {
                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if (ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();


                    }
                }else{
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }

            }
        }
    }
    @Override
    public void onLocationChanged(@NonNull Location location) {
        Log.i("test", String.valueOf(location.getLatitude()));

        recommenderApi recommenderApi=retrofit.create(recommenderApi.class);
        Call<List<response>> call =recommenderApi.getResponse(1,location.getLatitude(),location.getLongitude());
        call.enqueue(new Callback<List<response>>() {
            @Override
            public void onResponse(Call<List<response>> call, Response<List<response>> response) {
                 String content="";
                if (!response.isSuccessful()) {
                    textView.setText("code:" + response.code());
                    return;
                }
                List<response> responses = response.body();
                for (response resp : responses) {

                    Log.i("test", resp.getTitle());

                    content += "Item :" + resp.getTitle()+" ";
                    content += "Supplier : " + resp.getStore() + " ";
                    content+="lat :" +resp.getLat()+" ";
                    content+="Long : "+resp.getLongi()+" ";

                }
                if(content.length()>0) {
                    createNotification(content);
                    textView.setText("Find some Items !!");
                    progressBar.setEnabled(false);
                    progressBar.setVisibility(View.GONE);
                }

            }



            @Override
            public void onFailure(Call<List<response>> call, Throwable t) {
                    textView.setText(t.getMessage());


            }
        });



    }

    public void createNotification(String content)
    {
        Intent notifyIntent = new Intent(this,ResultActivity.class);
        notifyIntent.putExtra("values",content);
// Set the Activity to start in a new, empty task
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
// Create the PendingIntent
        PendingIntent notifyPendingIntent = PendingIntent.getActivity(
                this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT
        );

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Notification notification=new NotificationCompat.Builder(this,app.CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_1)
                .setContentTitle("HELLO")
                .setContentText("HIII")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setSound(soundUri)
                .setAutoCancel(true)
                .setContentIntent(notifyPendingIntent)
                .build();
        notificationManager.notify(1,notification);



    }
    @Override
    public void onProviderEnabled(String arg0) {

        Log.i("test","enabled");
    }

    @Override
    public void onProviderDisabled(String arg0) {

        Log.i("test","Disabled");
    }
     
   @Override
    public void onPause() {

       super.onPause();
       textView.setText("We are searching for some interesting items");
       progressBar.setEnabled(true);
       progressBar.setVisibility(View.VISIBLE);

   }

    @Override
    public void onResume() {

        super.onResume();
        textView.setText("We are searching for some interesting items");
        progressBar.setEnabled(true);
        progressBar.setVisibility(View.VISIBLE);


    }

}