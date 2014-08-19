package cl.manger.android.shitday.models;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper 
{
    public DatabaseHandler(Context context)
    {
        super(context, "shitDaysDatabase", null, 1);
    }
	
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) 
    {
        String createTable = "CREATE TABLE shitdays ("
        		+ "shitday_id INTEGER PRIMARY KEY,"
        		+ "date TEXT,"
                + "last_modification INTEGER,"
        		+ "rate INTEGER)";
        
        db.execSQL(createTable);

    }
 
    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
    {
        // Act like migrations in ASP.NET MVC
    	// if (oldVersion == 1 && newVersion == 2)
    	// ...
    }
    
    public long insert(ShitDay shitDay) 
    {
        SQLiteDatabase db = this.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put("last_modification", shitDay.getLastModification().getMillis());
        values.put("rate", shitDay.getRate());
        values.put("date", shitDay.getDate().toString());
     
        // Inserting Row
        long id = db.insert("shitdays", null, values);
        db.close();
        
        // Assigning id to the object
        shitDay.setShitDayId(id);
        
        return id;
    }
     
    public ShitDay getOneOrDefault(String date)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        
        String selectQuery = "SELECT shitday_id, last_modification, rate, date"
        		             + " FROM shitdays"
		             		 + " WHERE date = '"+ date +"'";
        
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        ShitDay shitDay = null;
        
        if (cursor.getCount() > 0)
        {
            cursor.moveToFirst();

            shitDay = new ShitDay();
            shitDay.setShitDayId(cursor.getLong(0));
            shitDay.setLastModification(new DateTime(cursor.getLong(1)));
            shitDay.setRate(cursor.getInt(2));
            shitDay.setDate(new LocalDate(cursor.getString(3)));
        }
        
        cursor.close();
        db.close();

        return shitDay;
    }
     
    public List<ShitDay> getAll(String rawWhere, String rawOrder, int limit, int offset)
    {
    	if (rawWhere == null) 
    	{
    		rawWhere = "1 = 1";
    	}
    	
    	if (rawOrder == null)
    	{
    		rawOrder = "date DESC";
    	}
    	   	
        List<ShitDay> shitDays = new ArrayList<ShitDay>();
        
        String selectQuery = "SELECT shitday_id, last_modification, rate, date"
        					+ " FROM shitdays"
        		            + " WHERE " + rawWhere
		            		+ " ORDER BY " + rawOrder
		            		+ " LIMIT " + limit + " OFFSET " + offset;
     
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
     
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) 
        {
            do 
            {
                ShitDay shitDay = new ShitDay();
                shitDay.setShitDayId(cursor.getLong(0));
                shitDay.setLastModification(new DateTime(cursor.getLong(1)));
                shitDay.setRate(cursor.getInt(2));
                shitDay.setDate(new LocalDate(cursor.getString(3)));

                shitDays.add(shitDay);
            } 
            while (cursor.moveToNext());
        }
     
        cursor.close();
        db.close();
        
        return shitDays;
    }
     
    public int getCount(String rawWhere) 
    {
    	if (rawWhere == null) 
    	{
    		rawWhere = "1 = 1";
    	}
    	
        String countQuery = "SELECT shitday_id FROM shitdays WHERE "+ rawWhere;
        
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
 
    	int count = cursor.getCount();
    	
		cursor.close();
		db.close();
        
        return count;
    }
    
    public int update(ShitDay shitDay)
    {
    	SQLiteDatabase db = this.getWritableDatabase();
    	ContentValues values = new ContentValues();
    	values.put("rate", shitDay.getRate());
    	values.put("last_modification", shitDay.getLastModification().getMillis());
    	
    	int result = db.update("shitdays", values, "shitday_id = "+ shitDay.getShitDayId(), null);
    	
    	db.close();
    	
    	return result;
    }
     
    public int delete(ShitDay shitDay)
    {
    	SQLiteDatabase db = this.getWritableDatabase();
    	int deletedRows = db.delete("shitdays", "shitday_id = "+ shitDay.getShitDayId(), null);
    	
    	db.close();
    	
    	return deletedRows;
    }
   


}
