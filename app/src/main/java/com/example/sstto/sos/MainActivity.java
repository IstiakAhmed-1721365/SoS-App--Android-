/**
 * Initiate background process
 * Listeners and content for UI
 * Listener for message from background
 * Manage pop up
 * Created by Istiak Ahmed.
 */
package com.example.sstto.sos;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;

public class MainActivity extends AppCompatActivity {

    double latitude;
    double longitude;
    int status=0,bluetooth_on=0,sms_sent=0;
    String tip="You are safe\n";
    String safeZone,alert="SAFE";
    String contact,name;
    static final Integer LOCATION = 0x1;
    SharedPreferences sharedpreferences;
    GPSLocation gpsLocation;
    ImageView imageView;
    Button safe,settings,appinfo,exSOS;  //add new button
    TextView tip_text,address_text,safeZone_text,alert_text;
    Trigger trigger;
    Handler handler;
    private int[] image={R.drawable.safety,R.drawable.earthquake,R.drawable.blizzard,R.drawable.fire,R.drawable.cyclone};
    /**
     * Initiate GPS location manager
     * Initiate background thread
     * Listeners
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        settings = (Button) findViewById(R.id.settings);
        safe = (Button)findViewById(R.id.safe);
         // connect new button
        exSOS = (Button)findViewById(R.id.exSOS);
        appinfo = (Button)findViewById(R.id.appinfo);
        alert_text= (TextView)findViewById(R.id.alert_text);
        tip_text = (TextView)findViewById(R.id.tip);
        address_text = (TextView)findViewById(R.id.address_text);
        safeZone_text = (TextView)findViewById(R.id.safeZone);
        imageView = (ImageView)findViewById(R.id.logoView);
        sharedpreferences = getSharedPreferences("SOS_MEM", Context.MODE_PRIVATE);
        contact = sharedpreferences.getString("mobile", "9029894202");
        name = sharedpreferences.getString("name", " ");
        /*Check for permission and ask again*/
        try
        {
            askForPermission(android.Manifest.permission.ACCESS_FINE_LOCATION, LOCATION);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
            Toast.makeText(this, "Permission not granted: Might lead to unexpected behaviour", Toast.LENGTH_LONG).show();
        }
        /*Initiate location manager*/
        gpsLocation = new GPSLocation(this);
        safe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                trigger.cancel(true);
                if (sms_sent==1)
                    trigger.sendSMS(contact, "Hi "+name+",I am safe, Thank you");
                trigger.bluetooth_off();
                finish();
                System.exit(0);
            }
        });
        /*Initiate background thread*/
        trigger=new Trigger(this);
        trigger.execute();
        display();
        /*Handle message interrupt from background*/
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                /*Reload screen or Error display*/
                if ("REFRESH".equalsIgnoreCase(message.obj.toString()))
                {
                    display();
                    return;
                }
                Toast.makeText(MainActivity.this,message.obj.toString(),Toast.LENGTH_SHORT).show();
            }
        };
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                settingDialog();
            }
        });
        exSOS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                status=1;
                trigger.cancel(true); //stops background checking
                trigger.sendSMS(contact, "Hi "+name+", Please send help. My location is: "+latitude +"," +longitude+"-");
                display();
            }
        });
        appinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                appinfoDialog();
            }
        });
    }
    /**
     * Set Main screen display
     */
    private void display(){
        tip_text.setText(tip);
        safeZone_text.setText(safeZone);
        alert_text.setText(alert);
        /*Safe or Connection error*/
        if (status<=0)
        {
            /*Disable I-AM-SAFE*/
            safe.setEnabled(false);
            safe.setVisibility(View.GONE);
            /*Enable setting & app info*/
            settings.setEnabled(true);
            settings.setVisibility(View.VISIBLE);
            appinfo.setEnabled(true);
            appinfo.setVisibility(View.VISIBLE);
            /*Disable safe zone*/
            address_text.setEnabled(false);
            address_text.setVisibility(View.GONE);
            safeZone_text.setEnabled(false);
            safeZone_text.setVisibility(View.GONE);
            /*Load appropriate image*/
            if (status==-1)
                imageView.setImageResource(R.drawable.error);
            else
                imageView.setImageResource(image[status]);
        }
        /*Emergency state*/
        else
        {
            /*Enable I-AM-SAFE*/
            safe.setEnabled(true);
            safe.setVisibility(View.VISIBLE);
            exSOS.setVisibility(View.GONE);
            /*Disable setting & app info*/
            settings.setEnabled(false);
            settings.setVisibility(View.GONE);
            appinfo.setEnabled(false);
            appinfo.setVisibility(View.GONE);
            /*Enable safe zone*/
            address_text.setEnabled(true);
            address_text.setVisibility(View.VISIBLE);
            safeZone_text.setEnabled(true);
            safeZone_text.setVisibility(View.VISIBLE);
            /*Load appropriate image*/
            imageView.setImageResource(image[status]);
        }
    }
    /**
     * Update emergency contact
     */
    private void settingDialog(){
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.setting_popup);
        dialog.setTitle("Edit Profile");
        final EditText editname=(EditText)dialog.findViewById(R.id.nameEditext);
        editname.setText(name);
        final EditText mobile=(EditText)dialog.findViewById(R.id.mobileEditext) ;
        mobile.setText(contact);
        dialog.show();
        Button submitButton = (Button) dialog.findViewById(R.id.submitButton);
        /*Update values in memory and variables*/
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String smobile = mobile.getText().toString();
                String sname = editname.getText().toString();
                SharedPreferences.Editor editor = sharedpreferences.edit();
                if(smobile!=null && smobile.length()>0)
                {
                    editor.putString("mobile", smobile);
                    contact=smobile;
                }
                if(sname!=null && sname.length()>0)
                {
                    editor.putString("name", sname);
                    name=sname;
                }
                editor.commit();
                dialog.dismiss();
            }
        });
    }
    /**
     *Display static textual information about app
     */
    private void appinfoDialog(){
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.appinfo_popup);
        dialog.setTitle("App Info");
        TextView infopage = (TextView) dialog.findViewById(R.id.info);
        infopage.setText("Developed for CSE299 project, FALL 2020, NSU.\n" +
                "This SOS app is your personal safety in times of any unfortunate events\n" +
                "Auto-enable flashlight and Bluetooth for easy discovery\n" +
                "\n" +
                "More instructions to add");
        dialog.show();
        Button closeButton = (Button) dialog.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
    /**
     *Send interrupt to stop background thread
     */
    @Override
    protected void onDestroy() {
        trigger.bluetooth_off();
        trigger.cancel(true);
        super.onDestroy();
    }

    /**
     * Request for missing permission
     */
    private void askForPermission(String permission, Integer requestCode) throws InterruptedException {
        //checking if permission is already granted. if not then it will ask for permission
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {

            //if permission was denied before
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                showRationale(permission, requestCode);
            } else {

                ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
            }
        }
    }
    /**
     * Disclaimer for missing user permission
     */
    private void showRationale(final String permission, final Integer requestCode) {
        //Rationale to be displayed
        String message = "The app requires Location to run effectively. Failure to do so will lead app to work inappropriately";
        //Listeners for Dialog Interface
        DialogInterface.OnClickListener okListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
            }
        };
        DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(MainActivity.this, "This will lead to App crash at some point", Toast.LENGTH_LONG).show();
            }
        };
        //Show the rationale for Permission
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("ok", okListener)
                .setNegativeButton("Cancel", cancelListener)
                .create()
                .show();
    }
}











