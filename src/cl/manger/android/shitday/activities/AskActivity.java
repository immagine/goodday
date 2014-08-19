package cl.manger.android.shitday.activities;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import cl.manger.android.shitday.R;
import cl.manger.android.shitday.models.DatabaseHandler;
import cl.manger.android.shitday.models.ShitDay;

public class AskActivity extends Activity 
{
	private Context thisActivityContext;
	
	// Activity views
	public Button shitDayButton;
	public Button notShitDayButton;
	public ProgressDialog loading;
		
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask);

        thisActivityContext = this;
        
        // Activity views
        shitDayButton = (Button) findViewById(R.id.button_shitDay);
        notShitDayButton = (Button) findViewById(R.id.button_notShitDay);

        // Button handler
        shitDayButton.setOnClickListener(new OnClickListener() 
        {
        	@Override
        	public void onClick(View arg0) 
        	{       		
        		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(thisActivityContext);
        		dialogBuilder.setTitle(getString(R.string.confirm_shitday));
        		dialogBuilder.setMessage(getString(R.string.confirm_shitday_text));
        		dialogBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        saveShitDay(ShitDay.SHITDAY);
                    }

                });
        		dialogBuilder.setNegativeButton("Cancel", null);
                dialogBuilder.show();
        	}
        });
        
        // Button handler
        notShitDayButton.setOnClickListener(new OnClickListener() 
        {
        	@Override
        	public void onClick(View arg0) 
        	{       		
        		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(thisActivityContext);
        		dialogBuilder.setTitle(getString(R.string.confirm_not_shitday));
        		dialogBuilder.setMessage(getString(R.string.confirm_not_shitday_text));
        		dialogBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    	saveShitDay(ShitDay.NOT_SHITDAY);
                    }

                });
        		dialogBuilder.setNegativeButton("Cancel", null);
                dialogBuilder.show();
        	}
        });
    }
    
    public void saveShitDay(int rate)
    {
    	Log.i("AskActivity", "Saving shit day");
    	   	
        new AsyncTask<Integer, Void, Void>()
        {        	
        	@Override
        	protected void onPreExecute()
        	{
        		loading = ProgressDialog.show(thisActivityContext, "", getString(R.string.saving));
        	}
        	
            @Override
            protected Void doInBackground(Integer... params) 
            {
            	DatabaseHandler db = new DatabaseHandler(thisActivityContext);
            	
            	// Check if we need to insert or update
            	ShitDay shitDay = db.getOneOrDefault(LocalDate.now().toString());
            	
            	// Insert
            	if (shitDay == null)
            	{
                	shitDay = new ShitDay();
                	shitDay.setDate(LocalDate.now());
                	shitDay.setRate(params[0]);
                	shitDay.setLastModification(DateTime.now());
                	
            		db.insert(shitDay);
            	}
            	else
            	{
            		shitDay.setRate(params[0]);
            		shitDay.setLastModification(DateTime.now());	
            		
            		db.update(shitDay);
            	}

                return null;
            }
            
			@Override
			protected void onPostExecute(Void result)
			{	
		    	// Toast
				Toast toast = Toast.makeText(thisActivityContext, getString(R.string.shitday_saved), Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
				toast.show();
		    	
		    	finish();
                
                loading.dismiss();
			}
			
        }.execute(rate, null, null);
        
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        return false;
    }
    
}
