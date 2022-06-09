package wochenplan.verwaltung;

import java.text.DecimalFormat;

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
			case "Montag":
				return 0;
			case "Dienstag":
				return 1;
			case "Mittwoch":
				return 2;
			case "Donnerstag":
				return 3;
			case "Freitag":
				return 4;
			case "Samstag":
				return 5;
			case "Sonntag":
				return 6;
			default:
				throw new InvalidTimeException();
		}
	}
	
	public static String formatTime(int time) {
		int stunden = (int) (time / 4);
		int minuten = (int) (((double) (time) / 4 % 1) * 60);
		String bMinuten = String.valueOf(minuten);
		
		if (bMinuten.equals("0")) {
			bMinuten += "0";
		}
		
		return stunden + ":" + bMinuten;
	}
	
	
	/**
	 * returnt true wenn es eine gültige Zeit für einen Termin ist
	 */
	public static boolean isValidTime(int tag, int zeitslot) {
		return 0 <= tag && tag <= 6 && 0 <= zeitslot && zeitslot <= 95;
	}
	
	public static TerminZeit create(int tag, int start, int ende) throws InvalidTimeException {
		if(!isValidTime(tag, start) || !isValidTime(tag, ende))
			throw new InvalidTimeException();
		
		if(start >= ende)
			throw new InvalidTimeException();
			
		return new TerminZeit(tag, start, ende);
	}


}
