package by.ilagoproject.timeUp_ManagerTime.broadreciver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.widget.Toast;

import by.ilagoproject.timeUp_ManagerTime.service.RemindService;

public class StartServiceReciver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent arg1) {
//        Toast.makeText(context, "reciver start", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(context, RemindService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        }
        else{
            context.startService(intent);
        }
        context.getApplicationContext().registerReceiver( new StartNewDate() ,new IntentFilter("android.intent.action.DATE_CHANGED"));
//        throw new UnsupportedOperationException("Not yet implemented");
    }
}
