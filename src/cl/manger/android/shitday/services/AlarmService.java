package cl.manger.android.shitday.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import cl.manger.android.shitday.activities.AskActivity;
 
public class AlarmService extends IntentService
{
    public AlarmService() 
    {
		super("AlarmService");
	}

	@Override
    protected void onHandleIntent(Intent intent)
	{	    
		// Start AskActivity
		Log.i("AlarmService", "Starting AskActivity");
		
		Intent askActivity = new Intent(this, AskActivity.class);
		askActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		this.startActivity(askActivity);
		
	    // We've consumed this alarm, so we need to setup next alarm
		Intent alarmSetterService = new Intent(this, AlarmSetterService.class);
		alarmSetterService.putExtra("forceTomorrow", true);
		this.startService(alarmSetterService);
    }

}