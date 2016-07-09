package com.example.brazz.mediapaly_test.Control.data.BroadcastReceiVer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class LockScreenReciver extends BroadcastReceiver {
    public LockScreenReciver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_SCREEN_OFF)) {
           // Intent lockscreen = new Intent(context, LockScreen.class);
        //    lockscreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
          //  context.startActivity(lockscreen);
            System.out.println("locked");
        }
    }
}
