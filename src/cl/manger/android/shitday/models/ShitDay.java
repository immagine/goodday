package cl.manger.android.shitday.models;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

public class ShitDay
{
	public final static int UNDEFINED = -1;
	public final static int NOT_SHITDAY = 0;
	public final static int SHITDAY = 1;
	
	public final static int MIN_ANSWERABLE_HOUR = 13;
	
	private Long shitDayId;
	private DateTime lastModification;
	private LocalDate date;
	private Integer rate;
	 
	public ShitDay()
	{
	}

	public Long getShitDayId() 
	{
		return shitDayId;
	}

	public void setShitDayId(long shitDayId) 
	{
		this.shitDayId = shitDayId;
	}

	public DateTime getLastModification() {
		return lastModification;
	}

	public void setLastModification(DateTime date) {
		this.lastModification = date;
	}

	public int getRate() {
		return rate;
	}

	public void setRate(int rate) {
		this.rate = rate;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	 
}
