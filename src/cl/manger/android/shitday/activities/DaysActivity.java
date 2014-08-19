package cl.manger.android.shitday.activities;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import cl.manger.android.shitday.R;
import cl.manger.android.shitday.customviews.ShitDayListViewAdapter;
import cl.manger.android.shitday.models.DatabaseHandler;
import cl.manger.android.shitday.models.ShitDay;

public class DaysActivity extends Activity 
{
	private Context thisActivityContext;
	
	// Activity views
	public Button loadPreviousMonthButton;
	public Button loadNextMonthButton;
	public Button applyFiltersButton;
	public ProgressDialog loading;
	public ListView listView;
	public Spinner monthsSpinner;
	public Spinner yearsSpinner;
		
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {   	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_days);

        thisActivityContext = this;
        
        // Activity views
        loadPreviousMonthButton = (Button) findViewById(R.id.button_loadPreviousMonth);
        loadNextMonthButton = (Button) findViewById(R.id.button_loadNextMonth);
        applyFiltersButton = (Button) findViewById(R.id.button_applyFilters);
        listView = (ListView) findViewById(R.id.listView_shitDays);
        monthsSpinner = (Spinner) findViewById(R.id.spinner_months);
        yearsSpinner = (Spinner) findViewById(R.id.spinner_years);
        
        // We'll be using now
        DateTime now = DateTime.now();
        
        // Fill spinners
        ArrayAdapter<CharSequence> monthsAdapter = ArrayAdapter.createFromResource(this, R.array.months_array, android.R.layout.simple_spinner_item);
        monthsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthsSpinner.setAdapter(monthsAdapter);
        monthsSpinner.setSelection(now.getMonthOfYear() - 1);
        
        List<String> years = new ArrayList<String>();
        for (int year = 2014; year <= now.getYear(); year++)
        {
        	years.add(Integer.toString(year));
        }
        
        ArrayAdapter<String> yearsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, years);
        yearsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearsSpinner.setAdapter(yearsAdapter);
        yearsSpinner.setSelection(yearsAdapter.getPosition(Integer.toString(now.getYear())));
        
        // Button handler
        applyFiltersButton.setOnClickListener(new OnClickListener() 
        {
        	@Override
        	public void onClick(View arg0) 
        	{
        		int selectedMonth = monthsSpinner.getSelectedItemPosition() + 1;
        		int selectedYear = Integer.parseInt(yearsSpinner.getSelectedItem().toString());
        		
				populateListview(selectedMonth, selectedYear);	
			}
        });

        // Button handler
        loadPreviousMonthButton.setOnClickListener(new OnClickListener() 
        {
        	@Override
        	public void onClick(View arg0) 
        	{  
        		int selectedMonth = monthsSpinner.getSelectedItemPosition();
        		
        		if (selectedMonth == 0)
        		{
        			monthsSpinner.setSelection(11);
        		}
        		else 
        		{
        			monthsSpinner.setSelection(selectedMonth - 1);
        		}
        		
        		applyFiltersButton.performClick();
        	}
        });
        
        // Button handler
        loadNextMonthButton.setOnClickListener(new OnClickListener() 
        {
        	@Override
        	public void onClick(View arg0) 
        	{  
        		int selectedMonth = monthsSpinner.getSelectedItemPosition();
        		
        		if (selectedMonth == 11)
        		{
        			monthsSpinner.setSelection(1);
        		}
        		else 
        		{
        			monthsSpinner.setSelection(selectedMonth + 1);
        		}
        		
        		applyFiltersButton.performClick();
        	}
        });
        
        // Day selected
        listView.setOnItemClickListener(new OnItemClickListener() 
        {
			@Override
			public void onItemClick(AdapterView<?> adapter, View clickedItemView, int position, long id) 
			{				
    			Intent intent = new Intent(thisActivityContext, DayActivity.class);
    			intent.putExtra("date", clickedItemView.getTag().toString());   			
    			startActivityForResult(intent, 1);
			}

        });
        
        // Apply filters the first time
        applyFiltersButton.performClick();

    }
    
    private void populateListview(int month, int year) 
    {
    	DateTime date = new DateTime(year, month, 1, 0, 0, 0);
    	
        new AsyncTask<DateTime, Void, Void>()
        {
        	DateTime monthAndYear;
        	Map<Integer, ShitDay> monthShitDays = new LinkedHashMap<Integer, ShitDay>();
        	
        	@Override
        	protected void onPreExecute()
        	{
        		loading = ProgressDialog.show(thisActivityContext, "", getString(R.string.loading));
        	}
        	
            @Override
            protected Void doInBackground(DateTime... params) 
            {            	
            	this.monthAndYear = params[0];
            	
            	DateTimeFormatter format = DateTimeFormat.forPattern("YYYY'-'MM");
            	String monthAndYearFilter = this.monthAndYear.toString(format);           	
            	Log.i("DaysActivity", "Filtering by " + monthAndYearFilter);
            	            	           	
                DatabaseHandler db = new DatabaseHandler(thisActivityContext);
                List<ShitDay> shitDays = db.getAll("strftime('%Y-%m', date) = '" + monthAndYearFilter + "'", "date DESC", 32, 0);

                // Store SQL results in a hashtable
                for (ShitDay shitDay : shitDays) 
                {

                	monthShitDays.put(shitDay.getDate().getDayOfMonth(), shitDay);
                }
                               
                // Create un-rated days (eg. the rest of the month)
                for (int day = 1; day <= this.monthAndYear.dayOfMonth().getMaximumValue(); day++)
                {
                	if (!monthShitDays.containsKey(day)) 
                	{
                		ShitDay shitDay = new ShitDay();
                		shitDay.setShitDayId(-1);
                		shitDay.setDate(new LocalDate(this.monthAndYear.getYear(), this.monthAndYear.getMonthOfYear(), day));
                		shitDay.setRate(ShitDay.UNDEFINED);

                		monthShitDays.put(day, shitDay);
                	}
                }

                return null;
            }
            
			@Override
			protected void onPostExecute(Void result)
			{	
				// Sorted list
				List<ShitDay> shitDays = new ArrayList<ShitDay>();
				
                for (int day = 1; day <= monthAndYear.dayOfMonth().getMaximumValue(); day++)
                {
                	shitDays.add(monthShitDays.get(day));
                }
				
                // Pass list to adapter and use adapter
                ShitDayListViewAdapter adapter = new ShitDayListViewAdapter((Activity)thisActivityContext, shitDays);
                listView.setAdapter(adapter);
                
                // Scroll to today's row if looking at this month
                DateTime now = DateTime.now();
                
                if (monthAndYear.getMonthOfYear() == now.getMonthOfYear() && monthAndYear.getYear() == now.getYear()) 
                {
                	if (now.getDayOfMonth() == 1)
                	{
                		listView.setSelection(now.getDayOfMonth() - 1);
                	}
                	else 
                	{
                		// Make yesterday visible
                		listView.setSelection(now.getDayOfMonth() - 1 - 1);
                	}                	
                }
                
                loading.dismiss();
			}
			
        }.execute(date, null, null); // Passing date here
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        return false;
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) 
    {
        // Check which request we're responding to
        if (requestCode == 1) 
        {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) 
            {
            	applyFiltersButton.performClick();
            }
        }
    }
}
