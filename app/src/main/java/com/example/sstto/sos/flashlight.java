/**
 * Enable and Disable flash light
 * Play beep sound
 * Created by shail on 11/28/2017.
 */
package com.example.sstto.sos;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Message;

public class flashlight {
    public MainActivity mainActivity;
    flashlight(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    /**
     * Enable flash light
     * Play beep sound
     */
    public void flashLight() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            CameraManager camManager = (CameraManager) mainActivity.getSystemService(Context.CAMERA_SERVICE);
            String cameraId = null;
            try {
                try {
                    cameraId = camManager.getCameraIdList()[0];
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
                camManager.setTorchMode(cameraId, true);
                MediaPlayer ring= MediaPlayer.create(mainActivity,R.raw.beep);
                ring.start();
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * Disable flash light
     */
    public void flashLoff() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            CameraManager camManager = (CameraManager) mainActivity.getSystemService(Context.CAMERA_SERVICE);
            String cameraId = null;
            try {
                try {
                    cameraId = camManager.getCameraIdList()[0];
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
                camManager.setTorchMode(cameraId, false);
                Thread.sleep(80);
            } catch (InterruptedException|CameraAccessException e) {
                e.printStackTrace();
                Message message1 = mainActivity.handler.obtainMessage(0, "This will lead to App crash at some point");
                message1.sendToTarget();
            }
        }
    }
}
