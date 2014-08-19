package cl.manger.android.shitday.activities;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import cl.manger.android.shitday.R;
import cl.manger.android.shitday.models.DatabaseHandler;
import cl.manger.android.shitday.models.ShitDay;

import com.ocpsoft.pretty.time.PrettyTime;

public class DayActivity extends Activity 
{
	private Context thisActivityContext;
	
	// Activity views
	public Button saveButton;
	public Button cancelButton;
	public TextView dateTextView;
	public TextView relativeDateTextView;
	public ProgressDialog loading;
	public RadioButton shitdayRadioButton;
	public RadioButton notShitDayRadioButton;
	public RadioButton undefinedRadioButton;
	public RadioGroup rateRadioGroup;
	
	// Activity data
	String date;
		
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {   	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day);

        thisActivityContext = this;
        
        // Activity views
        saveButton = (Button) findViewById(R.id.button_saveDay);
        cancelButton = (Button) findViewById(R.id.button_cancelDayUpdate);
        dateTextView = (TextView) findViewById(R.id.textView_date);
        relativeDateTextView = (TextView) findViewById(R.id.textView_date_relative);
        shitdayRadioButton = (RadioButton) findViewById(R.id.radio_shitday);
        notShitDayRadioButton = (RadioButton) findViewById(R.id.radio_notShitday);
        undefinedRadioButton = (RadioButton) findViewById(R.id.radio_undefined);
        rateRadioGroup = (RadioGroup) findViewById(R.id.radioGroup_rate);
        
        // Date
        this.date = this.getIntent().getExtras().getString("date");
        
        LocalDate localDate = new LocalDate(this.date);
        LocalDate now = LocalDate.now();
        
        // Future date?
		if (localDate.compareTo(now) > 0) 
		{
			saveButton.setVisibility(View.GONE);
			cancelButton.setVisibility(View.GONE);
			
			shitdayRadioButton.setEnabled(false);
			notShitDayRadioButton.setEnabled(false);
			undefinedRadioButton.setEnabled(false);
		}
        
        // H1
        DateTimeFormatter format = DateTimeFormat.forPattern("YYYY'-'MM'-'dd");
        dateTextView.setText(localDate.toString(format));
        
        // H2
        PrettyTime prettyTime = new PrettyTime();
        relativeDateTextView.setText(prettyTime.format(localDate.toDate()));
                
        // Button handler
        saveButton.setOnClickListener(new OnClickListener() 
        {
        	int rate = ShitDay.UNDEFINED;
        	
        	@Override
        	public void onClick(View clickedView) 
        	{
        		int selectedRadioId = rateRadioGroup.getCheckedRadioButtonId();
        		
        		switch (selectedRadioId) 
        		{
        			case R.id.radio_shitday:
        				rate = ShitDay.SHITDAY;
        				break;
        				
        			case R.id.radio_notShitday:
        				rate = ShitDay.NOT_SHITDAY;
        				break;
        				
        			case R.id.radio_undefined:
        				rate = ShitDay.UNDEFINED;
        				break;
        		}
        		
        		// Update or create async
                new AsyncTask<Void, Void, Void>()
                {                	
                	@Override
                	protected void onPreExecute()
                	{
                		loading = ProgressDialog.show(thisActivityContext, "", getString(R.string.saving));
                	}
                	
                    @Override
                    protected Void doInBackground(Void... params) 
                    {            	            	           	            	           	
                        DatabaseHandler db = new DatabaseHandler(thisActivityContext);
                        ShitDay fetchedShitDay = db.getOneOrDefault(date);
                        
                        if (fetchedShitDay == null)
                        {
                        	ShitDay shitDay = new ShitDay();
                        	shitDay.setDate(new LocalDate(date));
                        	shitDay.setRate(rate);
                        	shitDay.setLastModification(DateTime.now());

                        	db.insert(shitDay);
                        }
                        else
                        {
                        	fetchedShitDay.setRate(rate);
                        	fetchedShitDay.setLastModification(DateTime.now());
                        	
                        	db.update(fetchedShitDay);
                        }
                       
                        return null;
                    }
                    
        			@Override
        			protected void onPostExecute(Void result)
        			{
                        loading.dismiss();
                        
        				Toast toast = Toast.makeText(thisActivityContext, getString(R.string.shitday_saved), Toast.LENGTH_SHORT);
        				toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
        				toast.show();
                        
        				setResult(RESULT_OK);
                        finish();
        			}
        			
                }.execute(null, null, null); // Passing date here
			}
        });

        // Button handler
        cancelButton.setOnClickListener(new OnClickListener() 
        {
        	@Override
        	public void onClick(View clickedView) 
        	{
        		finish();
			}
        });
        
        // Load day async
        new AsyncTask<Void, Void, Void>()
        {
        	ShitDay fetchedShitDay;
        	
        	@Override
        	protected void onPreExecute()
        	{
        		loading = ProgressDialog.show(thisActivityContext, "", getString(R.string.loading));
        	}
        	
            @Override
            protected Void doInBackground(Void... params) 
            {            	            	           	            	           	
                DatabaseHandler db = new DatabaseHandler(thisActivityContext);
                fetchedShitDay = db.getOneOrDefault(date);
               
                return null;
            }
            
			@Override
			protected void onPostExecute(Void result)
			{
                loading.dismiss();
                
                if (fetchedShitDay == null)
                {
                	undefinedRadioButton.setChecked(true);
                }               
                else if (fetchedShitDay.getRate() == ShitDay.SHITDAY)
                {
                	shitdayRadioButton.setChecked(true);
                }
                else if (fetchedShitDay.getRate() == ShitDay.NOT_SHITDAY)
                {
                	notShitDayRadioButton.setChecked(true);
                }
			}
			
        }.execute(null, null, null); // Passing date here
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        return false;
    }
    
}
