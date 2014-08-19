package cl.manger.android.shitday.services;

import org.joda.time.DateTime;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import cl.manger.android.shitday.constants.SharedPreferencesKeys;
import cl.manger.android.shitday.listeners.AlarmListener;
 
public class AlarmSetterService extends IntentService
{
    public AlarmSetterService() 
    {
		super("AlarmSetterService");
	}

	@Override
    protected void onHandleIntent(Intent intent)
	{
		Log.i("AlarmSetterService", "Setting next alarm because of boot or because settings changed or because we've consumed last alarm");
		
        // Fetch current alarm configuration
		SharedPreferences sharedPreferences = getSharedPreferences(SharedPreferencesKeys.FILE, Context.MODE_PRIVATE);
		
		int alarmHour = sharedPreferences.getInt(SharedPreferencesKeys.ASK_TIME_HOUR, -1);
		int alarmMinute = sharedPreferences.getInt(SharedPreferencesKeys.ASK_TIME_MINUTE, -1);
		
		// Fetch incoming data
		boolean forceTomorrow = intent.getBooleanExtra("forceTomorrow", false);
		
	    // Setup next alarm
	    DateTime now = DateTime.now();
	    DateTime nextAlarm;
	    
	    // Set for tomorrow
	    if (forceTomorrow || (now.getHourOfDay() > alarmHour && now.getMinuteOfHour() > alarmMinute)) 
	    {
	    	nextAlarm = new DateTime(now.plusDays(1).getYear(), now.plusDays(1).getMonthOfYear(), now.plusDays(1).getDayOfMonth(), alarmHour, alarmMinute);
	    }
	    // Set for today
	    else 
	    {
	    	nextAlarm = new DateTime(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), alarmHour, alarmMinute);
	    }
		    
	    int privateCode = 0;
    	Intent listenerIntent = new Intent(this, AlarmListener.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, privateCode, listenerIntent, 0);
	    
	    AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
	    alarmManager.set(AlarmManager.RTC, nextAlarm.getMillis(), pendingIntent);
		
    }

}