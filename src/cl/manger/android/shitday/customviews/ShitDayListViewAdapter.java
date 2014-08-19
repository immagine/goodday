package cl.manger.android.shitday.customviews;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cl.manger.android.shitday.R;
import cl.manger.android.shitday.models.ShitDay;

public class ShitDayListViewAdapter extends BaseAdapter
{
    private Activity activity;
    private List<ShitDay> shitDays;
    private static LayoutInflater inflater = null;
 
    public ShitDayListViewAdapter(Activity activity, List<ShitDay> shitDays) 
    {
        this.activity = activity;
        this.shitDays = shitDays;
        inflater = (LayoutInflater)this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
 
    public int getCount() 
    {
        return shitDays.size();
    }
 
    @Override
    public Object getItem(int position) 
    {
        return shitDays.get(position);
    }
 
    @Override
    public long getItemId(int position) 
    {
    	return shitDays.get(position).getShitDayId();
    }
   
    // Returns a ListViewItem, defined by the layout R.layout.message_listviewitem
    @Override
    public View getView(int position, View convertView, ViewGroup parent) 
    {
        View view = convertView;
               
        if (convertView == null) 
        {
            view = inflater.inflate(R.layout.listviewitem_day, null);
        }
 
        TextView dateTextView = (TextView)view.findViewById(R.id.listViewItemDay_textView_date);
        TextView isShitDayTextView = (TextView)view.findViewById(R.id.listViewItemDay_textView_isShitDay);
 
        ShitDay shitDay = null;
        shitDay = shitDays.get(position);
        
        view.setTag(shitDay.getDate());
 
        // Setting all values in contained views
        DateTimeFormatter format = DateTimeFormat.forPattern("MMM' 'd, E");
               
        dateTextView.setText(shitDay.getDate().toString(format));
        
        switch (shitDay.getRate())
        {
        	case ShitDay.UNDEFINED:
        		isShitDayTextView.setText(activity.getString(R.string.undefined_label));
        		isShitDayTextView.setTypeface(null, Typeface.NORMAL);
        		isShitDayTextView.setTextColor(Color.parseColor("#A1A1A1"));
        		break;
        		
        	case ShitDay.SHITDAY:
        		isShitDayTextView.setText(activity.getString(R.string.shit_label));
            	isShitDayTextView.setTypeface(null, Typeface.BOLD);
            	isShitDayTextView.setTextColor(Color.parseColor("#ff5050"));
        		break;
        		
        	case ShitDay.NOT_SHITDAY:
        		isShitDayTextView.setText(activity.getString(R.string.not_shit_label));
        		isShitDayTextView.setTypeface(null, Typeface.NORMAL);
        		isShitDayTextView.setTextColor(Color.parseColor("#000000"));
        		break;
        }
        
        return view;
    }
}
