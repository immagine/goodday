package cl.manger.android.shitday.activities;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import cl.manger.android.shitday.R;
import cl.manger.android.shitday.constants.SharedPreferencesKeys;

public class MainActivity extends Activity 
{
	private Context thisActivityContext;
		
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        thisActivityContext = this;
                      
        // Clear all possible notifications
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
               
        // Initial setup
        SharedPreferences sharedPreferences = getSharedPreferences(SharedPreferencesKeys.FILE, Context.MODE_PRIVATE);
        Editor editSharedPreferences = sharedPreferences.edit();
        
        int appVersion = sharedPreferences.getInt(SharedPreferencesKeys.APP_VERSION, -1);
        int askTimeHour = sharedPreferences.getInt(SharedPreferencesKeys.ASK_TIME_HOUR, -1);
               
        // Check if appVersion in sharedPreferences is different from this app running
		if (appVersion != getAppVersion())
		{       	
        	// Update sharedPreferences
			editSharedPreferences.putInt(SharedPreferencesKeys.APP_VERSION, getAppVersion());
			editSharedPreferences.commit();
		}
		
		// Redirect
		Intent intent = new Intent(thisActivityContext, MenuActivity.class);
			
		// Check if askTimeHour hasn't been defined
		if (askTimeHour == -1)
		{
		    editSharedPreferences.putInt(SharedPreferencesKeys.ASK_TIME_HOUR, 20);
		    editSharedPreferences.putInt(SharedPreferencesKeys.ASK_TIME_MINUTE, 0);
		    editSharedPreferences.commit();
		    
		    intent = new Intent(thisActivityContext, SettingsActivity.class);
		}
			
		startActivity(intent);
		finish();
		
		// No transition to the next activity
		overridePendingTransition(0, 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        return false;
    }
    
    private int getAppVersion()
    {
        try 
        {
            PackageInfo packageInfo = thisActivityContext.getPackageManager().getPackageInfo(thisActivityContext.getPackageName(), 0);
            return packageInfo.versionCode;    
        } 
        catch (NameNotFoundException e) 
        {
            // Should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }
    
}
