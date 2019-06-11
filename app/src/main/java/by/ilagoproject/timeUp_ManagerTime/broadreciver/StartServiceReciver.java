package by.ilagoproject.timeUp_ManagerTime.broadreciver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import by.ilagoproject.timeUp_ManagerTime.service.RemindService;

public class StartServiceReciver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent arg1) {
//        Toast.makeText(context, "reciver start", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(context, RemindService.class);
        context.startService(intent);
//        throw new UnsupportedOperationException("Not yet implemented");
    }
}
