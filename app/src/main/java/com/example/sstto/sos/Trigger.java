/**
 * Background thread
 * Periodic web request
 * Enable/perform SMS, Bluetooth, Flash and Call
  */
package com.example.sstto.sos;

import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;

import java.util.ArrayList;

public class Trigger extends AsyncTask<String, Void, String> {

    MainActivity mainActivity;
    flashlight flashLight;
    /*Change the interval(ms) value to appropriate time frame*/
    int interval=10000,prev_status=0;
    Connect con;

    Trigger(MainActivity mainActivity)
    {
        this.mainActivity=mainActivity;
        con = new Connect(mainActivity);
    }

    /**
     * Background Task
     * Periodic web request
     * Enable/perform SMS, Bluetooth, Flash and Call
     */
    public String doInBackground(String... strings)
    {
        flashLight = new flashlight(mainActivity);
        while (true)
        {
            /*Check for interrupt*/
            if (isCancelled())
            {
                bluetooth_off();
                break;
            }
            /*Make Web request*/
            con.request();
            /*Validate change in status*/
            if (mainActivity.status!=prev_status)
            {
                /*Message to UI, Refresh with new info from web response*/
                Message message1 = mainActivity.handler.obtainMessage(0, "REFRESH");
                message1.sendToTarget();
            }
            /*Validate status change, Check for new emergency status*/
            if (mainActivity.status>0 && mainActivity.status!=prev_status)
            {
                try
                {
                    /*Alert protocol: SMS, Bluetooth, Flash, Call*/
                    Thread.sleep(interval);
                    sendSMS(mainActivity.contact, "Hi "+mainActivity.name+",Help me"+" Current Location:"+mainActivity.latitude+", "+mainActivity.longitude+"-"+mainActivity.name);
                    mainActivity.sms_sent=1;
                    Thread.sleep(interval);
                    bluetooth();
                    mainActivity.bluetooth_on=1;
                    blink();
                    Thread.sleep(interval);
                    call();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            /*Validate status change, check for new safe status*/
            else if (mainActivity.status==0 && mainActivity.status!=prev_status)
            {
                /*Send safe msg and disable bluetooth*/
                if (mainActivity.sms_sent==1)
                    sendSMS(mainActivity.contact, "Hi "+mainActivity.name+",I am safe, Thank you");
                mainActivity.sms_sent=0;
                bluetooth_off();
                mainActivity.bluetooth_on=0;
            }
            /*Status remain emergency, enable flashlight*/
            else if (mainActivity.status>0)
            {
                flash();
            }
            try
            {
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            prev_status=mainActivity.status;
        }
        return null;
    }
    /**
     * Send SMS
     * Two input parameter
     *      contact number
     *      text message
     */
    protected void sendSMS(String phoneNumber, String message) {
        ArrayList<PendingIntent> sentPendingIntents = new ArrayList<PendingIntent>();
        ArrayList<PendingIntent> deliveredPendingIntents = new ArrayList<PendingIntent>();
        /*Set services to handle sending and delivery status*/
        PendingIntent sentPI = PendingIntent.getBroadcast(mainActivity, 0,
                new Intent(mainActivity, SmsSentReceiver.class), 0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(mainActivity, 0,
                new Intent(mainActivity, SmsDeliveredReceiver.class), 0);
        try {
            SmsManager sms = SmsManager.getDefault();
            ArrayList<String> mSMSMessage = sms.divideMessage(message);
            for (int i = 0; i < mSMSMessage.size(); i++) {
                sentPendingIntents.add(i, sentPI);
                deliveredPendingIntents.add(i, deliveredPI);
            }
            sms.sendMultipartTextMessage(phoneNumber, null, mSMSMessage,
                    sentPendingIntents, deliveredPendingIntents);
        }
        catch (Exception e) {
            e.printStackTrace();
            Message message1 = mainActivity.handler.obtainMessage(0, "SMS sending failed...");
            message1.sendToTarget();
        }
    }
    /**
     * Enable bluetooth
     * Wait till enabled and set name
     */
    private void bluetooth() {
        BluetoothAdapter mBluetoothAdapter;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            return;
        }
        if (mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.disable();
        }
        mBluetoothAdapter.enable();
        while (!mBluetoothAdapter.isEnabled()) {
            try
            {
                Thread.sleep(50);
            }catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        /*Set bluetooth name*/
        mBluetoothAdapter.setName("HelpMe");
        while (!("HelpMe".equalsIgnoreCase(mBluetoothAdapter.getName()))) {
            try
            {
                Thread.sleep(10);
            }catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * Disable bluetooth, if enabled by app
     */
    protected void bluetooth_off() {
        BluetoothAdapter mBluetoothAdapter;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter !=null && mainActivity.bluetooth_on==1) {
            mBluetoothAdapter.disable();
        }
    }
    /**
     * Turn On and Off flashlight, beep
     * Fast blinks for first time alert
     */
    private void blink() {
        for (int i=0;i<20;i++) {
            try {
                Thread.sleep(100);
                flashLight.flashLight();
                Thread.sleep(100);
                flashLight.flashLoff();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * Turn On and Off flashlight, beep
     * Slow blinks to beacon rescue
     */
    private void flash() {
        for (int i=0;i<3;i++) {
            try {
                Thread.sleep(100);//Turn ON
                flashLight.flashLight();
                Thread.sleep(100);//Turn ON
                flashLight.flashLoff();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * Initiate call to the emergency contact provided
     */
    private void call() {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + mainActivity.contact));

        if (ActivityCompat.checkSelfPermission(mainActivity,
                android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mainActivity.startActivity(callIntent);
    }
}
