package wochenplan.verwaltung;

import java.util.Calendar;
import java.util.Date;

import wochenplan.verwaltung.exceptions.InvalidTimeException;

public class TerminZeit {
	
	private int tag;
	private int start;
	private int ende;
	
	private TerminZeit(int tag, int start, int ende) {
		this.tag = tag;
		this.start = start;
		this.ende = ende;
	}
	
	public int getTag() {
		return tag;
	}
	
	public int getStart() {
		return start;
	}
	
	public int getEnde() {
		return ende;
	}
	
	public int getDauer() {
		return ende - start;
	}
	
	@Override
	public String toString() {
		try {
			return convertIntToTag(tag) + " " + formatTime(start) + " - " + formatTime(ende);
		} catch (InvalidTimeException e) {
			return "Ungültige Zeit";
		}
	}
	
	public static String convertIntToTag(int tag) throws InvalidTimeException {
		switch(tag) {
			case 0:
				return "Montag";
			case 1:
				return "Dienstag";
			case 2:
				return "Mittwoch";
			case 3:
				return "Donnerstag";
			case 4:
				return "Freitag";
			case 5:
				return "Samstag";
			case 6:
				return "Sonntag";
			default:
				throw new InvalidTimeException();
		}
	}
	
	public static int convertTagToInt(String tag) throws InvalidTimeException {
		switch(tag.toLowerCase()) {
			case "montag":
				return 0;
			case "dienstag":
				return 1;
			case "mittwoch":
				return 2;
			case "donnerstag":
				return 3;
			case "freitag":
				return 4;
			case "samstag":
				return 5;
			case "sonntag":
				return 6;
			default:
				throw new InvalidTimeException();
		}
	}
	
	public static String formatTime(int time) {
		int stunden = (int) (time / 4);
		int minuten = (int) (((double) (time) / 4 % 1) * 60);
		String bMinuten = String.valueOf(minuten);
		String bStunden = String.valueOf(stunden);
		
		if(stunden < 10) {
			bStunden = "0" + bStunden;
		}
		
		if (bMinuten.equals("0")) {
			bMinuten = "00";
		}
		
		return bStunden + ":" + bMinuten;
	}
	
	public static int convertTimeToTimeSlot(double time) {
		return (int) (time * 4);
	}
	
	public static int getTodayAsInt() {
		Calendar calendar = Calendar.getInstance();
		int output = calendar.get(Calendar.DAY_OF_WEEK) - 2;
		
		if(output < 0)
			output += 7;
			
		return output;
	}
	
	public static int getCurrentTimeAsTimeSlot() {
		Calendar calendar = Calendar.getInstance();
		
		int hours = calendar.get(Calendar.HOUR_OF_DAY);
		int minutes = calendar.get(Calendar.MINUTE);
		
		return (int) ((hours * 4) + (Math.ceil(minutes / 15.0) + 1));
	} 
	
	/**
	 * returnt true wenn es eine gültige Zeit für einen Termin ist
	 */
	public static boolean isValidTime(int tag, int zeitslot) {
		return isValidTime(tag) && 0 <= zeitslot && zeitslot <= 95;
	}
	
	public static boolean isValidTime(int tag) {
		return 0 <= tag && tag <= 6;
	}
	
	public static boolean isValidTime(int tag, int start, int ende) {
		return isValidTime(tag, start) && isValidTime(tag, ende) && start < ende;
	}
	
	public static TerminZeit create(int tag, int start, int ende) throws InvalidTimeException {
		if(!isValidTime(tag, start, ende))
			throw new InvalidTimeException();
			
		return new TerminZeit(tag, start, ende);
	}

}
