package cl.manger.android.shitday.activities;

import org.joda.time.DateTime;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.TimePicker.OnTimeChangedListener;
import cl.manger.android.shitday.R;
import cl.manger.android.shitday.constants.SharedPreferencesKeys;
import cl.manger.android.shitday.listeners.AlarmListener;
import cl.manger.android.shitday.models.ShitDay;
import cl.manger.android.shitday.services.AlarmSetterService;

public class SettingsActivity extends Activity 
{
	private Context thisActivityContext;
	
	// Activity views
	public Button saveSettingsButton;
	public TimePicker timePicker;
	public ProgressDialog loading;
	public TextView settingsText;
	
	// Shared preferences
    SharedPreferences sharedPreferences;
		
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        thisActivityContext = this;
        
        // Activity views
        saveSettingsButton = (Button) findViewById(R.id.button_saveSettings);
        timePicker = (TimePicker) findViewById(R.id.timePicker);
        settingsText = (TextView) findViewById(R.id.textView_settingsText);
        
    	// Time picker without AM/PM
    	timePicker.setIs24HourView(true);
        
        // Text with parameter
        settingsText.setText(getString(R.string.settings_text).replaceAll("MIN_ANSWERABLE_HOUR", Integer.toString(ShitDay.MIN_ANSWERABLE_HOUR)));
        
        // Set timepicker value to the one in shared preferences
        sharedPreferences = getSharedPreferences(SharedPreferencesKeys.FILE, Context.MODE_PRIVATE);
        
        timePicker.setCurrentHour(sharedPreferences.getInt(SharedPreferencesKeys.ASK_TIME_HOUR, 23));
        timePicker.setCurrentMinute(sharedPreferences.getInt(SharedPreferencesKeys.ASK_TIME_MINUTE, 59));
        
        // Timepicker's time changed
        timePicker.setOnTimeChangedListener(new OnTimeChangedListener() 
        {
			@Override
			public void onTimeChanged(TimePicker view, int hourOfDay, int minute) 
			{
				if (hourOfDay < ShitDay.MIN_ANSWERABLE_HOUR) 
				{
					view.setCurrentHour(ShitDay.MIN_ANSWERABLE_HOUR);
				}
			}
		});
        
        // Button handler
        saveSettingsButton.setOnClickListener(new OnClickListener() 
        {
        	@Override
        	public void onClick(View arg0) 
        	{  
        		int selectedHour = timePicker.getCurrentHour();
        		int selectedMinute = timePicker.getCurrentMinute();
        		
        		// Selected time is not in any of the two ranges
        		if (selectedHour < ShitDay.MIN_ANSWERABLE_HOUR)
        		{
        			AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(thisActivityContext);
        			dialogBuilder.setTitle(getString(R.string.pick_another_time));
        			dialogBuilder.setMessage(getString(R.string.pick_another_time_text));
        			dialogBuilder.setPositiveButton("OK", null);
        	        dialogBuilder.show();
        			
        			return;
        		}
        		
        		// Save
        		Editor editor = sharedPreferences.edit();
        		editor.putInt(SharedPreferencesKeys.ASK_TIME_HOUR, selectedHour);
        		editor.putInt(SharedPreferencesKeys.ASK_TIME_MINUTE, selectedMinute);
    		    editor.commit();
    		    
		    	// Toast
				Toast toast = Toast.makeText(thisActivityContext, getString(R.string.settings_saved), Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
				toast.show();
    		    
    		    // Setup next alarm
				Intent alarmSetterService = new Intent(thisActivityContext, AlarmSetterService.class);
				startService(alarmSetterService);

		    	// Redirect
    			Intent intent = new Intent(thisActivityContext, MenuActivity.class);
    			startActivity(intent);
    			finish();
        	}
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        return false;
    }
    
}
