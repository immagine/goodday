package cl.manger.android.shitday.activities;

import java.text.DecimalFormat;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
//import android.support.v7.widget.GridLayout;
import android.widget.TextView;
import cl.manger.android.shitday.R;
import cl.manger.android.shitday.constants.SharedPreferencesKeys;
import cl.manger.android.shitday.models.DatabaseHandler;
import cl.manger.android.shitday.models.ShitDay;

public class MenuActivity extends Activity 
{
	private Context thisActivityContext;
	
	// Activity views
	public Button goToSettingsButton;
	public Button goToListButton;
	public Button goToAboutButton;
	public ProgressDialog loading;
	public TextView last7DaysTextView;
	public TextView last30DaysTextView;
	//public GridLayout horizontalCalendar;
	
	@Override
	protected void onResume() 
	{
		super.onResume();
		displayQuickStats();
	};
		
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        thisActivityContext = this;
        
        // Activity views
        goToSettingsButton = (Button) findViewById(R.id.button_goToSettings);
        goToListButton = (Button) findViewById(R.id.button_goToList);
        goToAboutButton = (Button) findViewById(R.id.button_goToAbout);
        last7DaysTextView = (TextView) findViewById(R.id.textView_last7daysValue);
        last30DaysTextView = (TextView) findViewById(R.id.textView_last30daysValue);
        //horizontalCalendar = (GridLayout) findViewById(R.id.gridLayout_horizontalCalendar);
        
        // Setup horizontal calendar
        int daysInMonth = DateTime.now().dayOfMonth().getMaximumValue();
        
        //horizontalCalendar.setRowCount(2);
        //horizontalCalendar.setColumnCount(daysInMonth);
        
        // Button handler
        goToSettingsButton.setOnClickListener(new OnClickListener() 
        {
        	@Override
        	public void onClick(View arg0) 
        	{  
		    	// Redirect
    			Intent intent = new Intent(thisActivityContext, SettingsActivity.class);
    			startActivity(intent);
        	}
        });
        
        // Button handler
        goToListButton.setOnClickListener(new OnClickListener() 
        {
        	@Override
        	public void onClick(View arg0) 
        	{  
		    	// Redirect
    			Intent intent = new Intent(thisActivityContext, DaysActivity.class);
    			startActivity(intent);
        	}
        });
        
        // Button handler
        goToAboutButton.setOnClickListener(new OnClickListener() 
        {
        	@Override
        	public void onClick(View arg0) 
        	{  
		    	// Redirect
    			Intent intent = new Intent(thisActivityContext, AboutActivity.class);
    			startActivity(intent);
        	}
        });
        
    	displayQuickStats();
        
        
        // Check (async) if alarm is set
        /*
        new AsyncTask<Void, Void, Void>()
        {       	
        	@Override
        	protected void onPreExecute()
        	{
        	}
        	
            @Override
            protected Void doInBackground(Void... params) 
            {
                SharedPreferences sharedPreferences = getSharedPreferences(SharedPreferencesKeys.FILE, Context.MODE_PRIVATE);
                Editor editSharedPreferences = sharedPreferences.edit();
                
                int askTimeHour = sharedPreferences.getInt(SharedPreferencesKeys.ASK_TIME_HOUR, -1);
                
        		// Check if askTimeHour hasn't been defined
        		if (askTimeHour == -1)
        		{
        		    editSharedPreferences.putInt(SharedPreferencesKeys.ASK_TIME_HOUR, 20);
        		    editSharedPreferences.putInt(SharedPreferencesKeys.ASK_TIME_MINUTE, 0);
        		    editSharedPreferences.commit();
        		    
        		    Intent intent = new Intent(thisActivityContext, SettingsActivity.class);
            		startActivity(intent);
        		}

                return null;
            }
            
			@Override
			protected void onPostExecute(Void result)
			{			

			}
			
        }.execute(null, null, null);
        */
    }
    
    private void displayQuickStats()
    {
        // Fill quick stats in AsyncTask
        new AsyncTask<Void, Void, Void>()
        {
        	int sevenDaysAgoCount = 0;
        	int thirtyDaysAgoCount = 0;
        	
        	@Override
        	protected void onPreExecute()
        	{
        	}
        	
            @Override
            protected Void doInBackground(Void... params) 
            {
            	String today = LocalDate.now().toString();
            	String sevenDaysAgo = LocalDate.now().minusDays(6).toString();
            	String thirtyDaysAgo = LocalDate.now().minusDays(29).toString();
            	           	
                DatabaseHandler db = new DatabaseHandler(thisActivityContext);
                sevenDaysAgoCount = db.getCount("date BETWEEN '" + sevenDaysAgo + "' AND '"+ today + "' AND rate = " + ShitDay.SHITDAY);
                thirtyDaysAgoCount = db.getCount("date BETWEEN '" + thirtyDaysAgo + "' AND '" + today + "' AND rate = " + ShitDay.SHITDAY);
                               
                return null;
            }
            
			@Override
			protected void onPostExecute(Void result)
			{			
				DecimalFormat format = new DecimalFormat("0");

		        last7DaysTextView.setText(sevenDaysAgoCount + "/7 shit days (" + format.format((double) sevenDaysAgoCount / 7 * 100) + " %)");
		        last30DaysTextView.setText(thirtyDaysAgoCount + "/30 shit days (" + format.format((double) thirtyDaysAgoCount / 30 * 100) + " %)");
			}
			
        }.execute(null, null, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        return false;
    }
    
}
