package cl.manger.android.shitday.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import cl.manger.android.shitday.R;

public class AboutActivity extends Activity 
{
	private Context thisActivityContext;
	
	// Activity views
	//public Button shitDayButton;
		
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        thisActivityContext = this;
        
        // Activity views
        //shitDayButton = (Button) findViewById(R.id.button_shitDay);

    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        return false;
    }
    
}
