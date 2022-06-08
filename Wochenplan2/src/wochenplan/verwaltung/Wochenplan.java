package wochenplan.verwaltung;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import wochenplan.verwaltung.exceptions.InvalidTimeException;
import wochenplan.verwaltung.exceptions.TerminAddException;
import wochenplan.verwaltung.exceptions.TerminRemoveException;

public class Wochenplan {

	Termin[][] termine = new Termin[7][96];

	public Wochenplan() {
		//this(null);
	}
	
	public Wochenplan(Termin[][] termine) {
		super();
		this.termine = termine;
	}

	public Wochenplan(Wochenplan plan) {
		if (plan == null)
			return;
		
		this.termine = plan.termine;
	}
	
	
	/**
	 * Versucht einen @Termin hinzuzufügen
	 * Wenn an der Stelle schon ein Termin existiert tritt eine @TerminAddException auf
	 */
	public void addTermin(String name, int tag, int beginn, int ende) throws TerminAddException {
		Termin[][] copy = termine.clone();
		Termin termin = new Termin(name, beginn, ende); 
		for (int i = beginn; i < ende; i++) {
			if(existsTermin(tag, i))
					throw new TerminAddException();
			copy[tag][i] = termin;
		}
		
		termine = copy;
	}

	/**
	 * Versucht einen @Termin zu entfernen
	 * Wenn dieser nicht existiert tritt eine @TerminRemoveException auf
	 */
	public void removeTermin(int tag, int zeitslot) throws TerminRemoveException {
		if(!existsTermin(tag, zeitslot))
			throw new TerminRemoveException();
		
		Termin termin = termine[tag][zeitslot];
		
		// ALLE EINTRÄGE DES TERMINS DIE SPÄTER SIND ENTFERNEN
		int slot = zeitslot;
		while (slot < termine[tag].length) {
			if (termine[tag][slot] != termin)
				break;

			termine[tag][slot] = null;
			slot++;
		}

		// ALLE EINTRÄGE DER TERMINS DIE FRÜHER SIND ENTFERNEN
		slot = zeitslot - 1;
		while (slot >= 0) {
			if (termine[tag][slot] != termin)
				break;

			termine[tag][slot] = null;
			slot--;
		}
	}

	/**
	 * Returnt einen Termin falls vorhanden, sonst null
	 */
	public Termin getTermin(int tag, int zeitslot) {
		return existsTermin(tag, zeitslot) ? termine[tag][zeitslot] : null;
	}

	/**
	 * Returnt true, wenn ein Termin an dieser Stelle eingetragen ist
	 */
	public boolean existsTermin(int tag, int zeitslot) {
		return termine[tag][zeitslot] != null;
	}
	
	/**
	 * Returnt true, wenn an diesem Tag mindestens ein Termin eingetragen ist
	 */
	public boolean existsTermin(int tag) {
		for(int i = 0; i < termine[tag].length; i++) {
			if(existsTermin(tag, i)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returnt true, wenn ein Termin mit diesem Namen eingetragen ist
	 */
	public boolean existsTermin(String name) {
		for (int i = 0; i < termine.length; i++)
			for (int j = 0; j < termine[i].length; j++)
				if (existsTermin(i, j) && getTermin(i, j).getName().equals(name))
					return true;

		return false;
	}
	
	/**
	 * Returnt die TerminZeit falls dieser Termin existiert.
	 * Ansonsten wird null returnt
	 */
	public TerminZeit getDuration(Termin termin) {
		int tag = -1;
		int start = -1;
		int ende = -1;
		boolean foundTermin = false;
		DURATIONCHECK: for (int i = 0; i < termine.length; i++)
			for (int j = 0; j < termine[i].length; j++) {
				
				if(!foundTermin && termin == getTermin(i, j)) {
					foundTermin = true;
					tag = i;
					start = j;
				} else if(foundTermin && termin != getTermin(i, j)) {
					ende = j-1;
					break DURATIONCHECK;
				}
			}
		
		try {
			return TerminZeit.create(tag, start, ende);
		} catch (InvalidTimeException e) {
			return null;
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		boolean anyTermin = false;
		for (int tag = 0; tag < 7; tag++) {
			for (int i = 0; i < 96; i++) {
				if (termine[tag][i] != null) {
					builder.append(termine[tag][i] + "\n");
					System.out.println();
					i += termine[tag][i].dauer;
					anyTermin = true;
				}
			}
		}

		if (!anyTermin)
			builder.append("Es wurden noch keine Termine eingetragen");

		return builder.toString();
	}
	
	// TODO
	public File saveAsFile(String filename) throws IOException {
		File file = new File(filename);

		if (!file.exists())
			file.createNewFile();

		FileWriter writer = new FileWriter(file);

		for (int i = 0; i < termine.length; i++)
			for (int j = 0; j < termine[i].length; j++) {
				if (existsTermin(i, j))
					writer.write(i + "/" + j + ":" + getTermin(i, j).getName() + "\n");
			}

		writer.close();
		return file;
	}

	// TODO
	public static Wochenplan fromFile(File file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));

		Wochenplan wochenplan = new Wochenplan();
		String line;
		while ((line = reader.readLine()) != null) {
			try {
				String[] splittedLine = line.split(":");

				int tag = Integer.parseInt(splittedLine[0].split("/")[0]);
				int zeitslot = Integer.parseInt(splittedLine[0].split("/")[1]);
				String name = splittedLine[1];

				wochenplan.addTermin(name, tag, zeitslot, zeitslot + 15);
			} catch (Exception e) {
			}
		}
		reader.close();
		return wochenplan;
	}
}
