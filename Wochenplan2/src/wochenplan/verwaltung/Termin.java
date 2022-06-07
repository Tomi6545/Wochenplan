package wochenplan.verwaltung;

public class Termin {

	public String name;
	public int beginn;
	public int ende;
	public int dauer;

	public Termin(String name, int beginn, int ende) {
		super();
		this.name = name;
		this.beginn = beginn;
		this.ende = ende;
		dauer = ende - beginn;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getBeginn() {
		return beginn;
	}

	public void setBeginn(int beginn) {
		this.beginn = beginn;
	}

	public int getEnde() {
		return ende;
	}

	public void setEnde(int ende) {
		this.ende = ende;
	}

	@Override
	public String toString() {
		int beginnStunden = (int) (beginn / 4);
		int endeStunden = (int) (ende / 4);
		int beginnMinuten = (int) (((double) (beginn) / 4 % 1) * 60);
		int endeMinuten = (int) (((double) (ende) / 4 % 1) * 60);
		String bMinuten = String.valueOf(beginnMinuten);
		String eMinuten = String.valueOf(endeMinuten);
		
		if (bMinuten.equals("0")) {
			bMinuten += "0";
		}
		if (eMinuten.equals("0")) {
			eMinuten += "0";
		}
		
		return beginnStunden + ":" + bMinuten + "-" + endeStunden + ":" + eMinuten + " Uhr: " + name;
	}
}
