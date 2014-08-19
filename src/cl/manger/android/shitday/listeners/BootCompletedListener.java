package cl.manger.android.shitday.listeners;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import cl.manger.android.shitday.services.AlarmSetterService;
 
public class BootCompletedListener extends BroadcastReceiver
{   
    @Override
    public void onReceive(Context context, Intent intent)
    {
       Intent alarmSetterService = new Intent(context, AlarmSetterService.class);
       context.startService(alarmSetterService);
    }   
}