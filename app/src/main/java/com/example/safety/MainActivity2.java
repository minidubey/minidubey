package com.example.safety;

import static android.Manifest.permission.CALL_PHONE;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

@SuppressWarnings("unused")
public class MainActivity2 extends AppCompatActivity {
    private static final String ACCESS_COARSE_LOCATION = null;
    private static final String ACCESS_FINE_LOCATION = null;
    private static final String  ACCESSFINELOCATION = null;
    Button b1,b2;
    private FusedLocationProviderClient client ;
    DataBaseHandler myDB;
    private final int REQUEST_CHECK_CODE = 8989;
    private CONTROL_LOCATION_UPDATES.Builder builder;
    String x="" , y="";
    private static final int Request_Location = 1;
    LocationManager locationManager;
    Intent mIntent;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        b1 = findViewById(R.id.btn1);
        b2 = findViewById(R.id.btn2);
        myDB =new DataBaseHandler(this);
        final MediaPlayer mp = MediaPlayer.create(getApplicationContext(),R.raw.alarm);
locationManager = (LocationManager)  getSystemService(LOCATION_SERVICE);
if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
    onGPS();

}
else{
    startTrack(mp);
}
b1.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent i = new Intent(getApplicationContext(), Register.class);
        startActivity(i);
    }
});
b2.setOnLongClickListener(new View.OnLongClickListener() {
    @Override
    public boolean onLongClick(View v) {
        mp.start();
        Toast.makeText(getApplicationContext(), "PANIC BUTTON STARTED", Toast.LENGTH_SHORT).show();
        return false;

    }
});
    }
    private void loadData(){
        ArrayList<String> theList = new ArrayList<>();
        Cursor data = myDB.getListContents();
        if(data.getCount()==0){
            Toast.makeText(this, "no content to show ", Toast.LENGTH_SHORT).show();
        }
        else{
            String msg="I NEED HELP LATITUDE:" +x+ "LONGITUDE:" +y;
            String number = "";


            while (data.moveToNext()){
                theList.add(data.getString(1));
                number = number+data.getString(1)+(data.isLast()?"":";");
                call();
            }
            if(!theList.isEmpty()){
                sendSms(number,msg,true);
            }
        }
    }

    private void sendSms(String number, String msg, boolean b) {
        Intent smsIntent = new  Intent(Intent.ACTION_SENDTO);
        Uri.parse("smsto:" +number);
        smsIntent.putExtra("smsbody",msg);
        startActivity(smsIntent);
    }

    private void call() {
        Intent i = new Intent(Intent.ACTION_CALL);
        i.setData(Uri.parse("tel:1090"));
        if(ContextCompat.checkSelfPermission(getApplicationContext(),CALL_PHONE)== PackageManager.PERMISSION_GRANTED){
            startActivity(i);
        }
        else{
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                requestPermissions(new String[]{CALL_PHONE},1);
            }
        }


    }


    private void startTrack(MediaPlayer mp) {
        String ACCESS_FINE_LOCATION = "";
        if((ActivityCompat.checkSelfPermission(MainActivity2.this, MainActivity2.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED)
                && (ActivityCompat.checkSelfPermission(MainActivity2.this,
                MainActivity2.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)){
            ActivityCompat.requestPermissions(this ,new String[]{ACCESSFINELOCATION },Request_Location);

        }
        else{
            Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (locationGPS!=null){
                double lat = locationGPS.getLatitude();
                double lon = locationGPS.getLongitude();
                x= String.valueOf(lat);
                y = String.valueOf(lon);

            }
            else{
                Toast.makeText(this, "Unable To Find Location", Toast.LENGTH_SHORT).show();
            }
        }

    }


    private void onGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("ENABLE GPS").setCancelable(false).setPositiveButton("yes",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){
                startActivity(new Intent (Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }
    }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();


    }

    private static class ACCESS_FINE_LOCATION {
    }

    private class ACCESS_COARSE_LOCATION {
    }

    private class FusedLocationProviderClient {
    }

    private static class ACCESSFINELOCATION {
    }
}