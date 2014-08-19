package cl.manger.android.shitday.listeners;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import cl.manger.android.shitday.services.AlarmService;
 
public class AlarmListener extends BroadcastReceiver
{   
    @Override
    public void onReceive(Context context, Intent intent)
    {
       Intent alarmService = new Intent(context, AlarmService.class);
       context.startService(alarmService);
    }   
}