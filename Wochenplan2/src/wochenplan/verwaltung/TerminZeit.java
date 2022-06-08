package wochenplan.verwaltung;

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
		return convertTagToString() + start + " - " + ende;
	}
	
	private String convertTagToString() {
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
				return "";
		}
	}
	
	/**
	 * returnt true wenn es eine gültige Zeit für einen Termin ist
	 */
	public static boolean isValidTime(int tag, int zeitslot) {
		return 0 <= tag && tag <= 6 && 0 <= zeitslot && zeitslot <= 47;
	}
	
	public static TerminZeit create(int tag, int start, int ende) throws InvalidTimeException {
		if(!isValidTime(tag, start) || !isValidTime(tag, ende))
			throw new InvalidTimeException();
		
		if(start >= ende)
			throw new InvalidTimeException();
			
		return new TerminZeit(tag, start, ende);
	}


}
