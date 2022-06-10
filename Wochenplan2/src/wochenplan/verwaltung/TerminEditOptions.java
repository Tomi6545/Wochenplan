package wochenplan.verwaltung;

public enum TerminEditOptions {
	
	RENAME("umbennen"),
	REMOVE("entfernen");
	
	private String action;
	
	TerminEditOptions(String action) {
		this.action = action;
	}
	
	public String getAction( ) {
		return action;
	}

}
