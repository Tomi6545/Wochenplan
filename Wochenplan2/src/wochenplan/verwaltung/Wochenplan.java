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

	private Termin[][] termine;

	public Wochenplan() {
		this(new Termin[7][96]);
	}

	public Wochenplan(Termin[][] termine) {
		this.termine = termine;
	}

	public Wochenplan(Wochenplan plan) {
		this(plan.termine.clone());
	}

	/**
	 * Versucht einen @Termin hinzuzufügen Wenn an der Stelle schon ein Termin
	 * existiert tritt eine @TerminAddException auf
	 */
	public void addTermin(String name, int tag, int beginn, int ende) throws TerminAddException, InvalidTimeException {
		if (!TerminZeit.isValidTime(tag, beginn, ende))
			throw new InvalidTimeException();

		Termin[][] copy = termine.clone();
		Termin termin = new Termin(name);
		for (int i = beginn; i <= ende; i++) {
			if (existsTermin(tag, i))
				throw new TerminAddException();
			copy[tag][i] = termin;
		}

		termine = copy;
	}

	public void addHinweg(int tag, int beginn, int ende, int wegDauer) throws TerminAddException, InvalidTimeException {
		
			addTermin("Weg", tag, beginn - wegDauer, beginn-1);
		}

	/**
	 * Versucht einen @Termin zu entfernen Wenn dieser nicht existiert tritt
	 * eine @TerminRemoveException auf
	 */
	public void removeTermin(int tag, int zeitslot) throws TerminRemoveException, InvalidTimeException {
		if (!TerminZeit.isValidTime(tag, zeitslot))
			throw new InvalidTimeException();
		if (!existsTermin(tag, zeitslot))
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

	public void renameTermin(int tag, int zeitslot, String newName) throws TerminRemoveException, InvalidTimeException {
		if (!TerminZeit.isValidTime(tag, zeitslot))
			throw new InvalidTimeException();
		if (!existsTermin(tag, zeitslot))
			throw new TerminRemoveException();

		Termin termin = termine[tag][zeitslot];

		// ALLE EINTRÄGE DES TERMINS DIE SPÄTER SIND ENTFERNEN
		int slot = zeitslot;
		while (slot < termine[tag].length) {
			if (termine[tag][slot] != termin)
				break;

			termine[tag][slot].setName(newName);
			;
			slot++;
		}

		// ALLE EINTRÄGE DER TERMINS DIE FRÜHER SIND ENTFERNEN
		slot = zeitslot - 1;
		while (slot >= 0) {
			if (termine[tag][slot] != termin)
				break;

			termine[tag][slot].setName(newName);
			;
			slot--;
		}
	}

	/**
	 * Returnt einen @Termin falls vorhanden, sonst null
	 */
	public Termin getTermin(int tag, int zeitslot) {
		return existsTermin(tag, zeitslot) ? termine[tag][zeitslot] : null;
	}
	
	public Termin getNextTermin(int tag, int zeitslot, String name) throws InvalidTimeException {
		if(!TerminZeit.isValidTime(tag, zeitslot))
			throw new InvalidTimeException();
		
		for(int i = tag; i < termine.length; i++)
			for(int j = zeitslot; j < termine[i].length; j++) {
				if(existsTermin(i, j) && (name == null || getTermin(i, j).getName().equals(name)))
					return getTermin(i, j);
			}
		
		return null;
	}
	
	public Termin gextNextTermin(int tag, int zeitslot) throws InvalidTimeException {
		return getNextTermin(tag, zeitslot, null);
	}
	
	
	/**
	 * Returnt die @TerminZeit falls dieser @Termin existiert. Ansonsten wird null
	 * returnt
	 */
	public TerminZeit getTerminDuration(Termin termin) {
		int tag = -1;
		int start = -1;
		int ende = -1;
		boolean foundTermin = false;
		DURATIONCHECK: for (int i = 0; i < termine.length; i++)
			for (int j = 0; j < termine[i].length; j++) {

				if (!foundTermin && termin == getTermin(i, j)) {
					foundTermin = true;
					tag = i;
					start = j;
				} else if (foundTermin && termin != getTermin(i, j)) {
					ende = j - 1;
					break DURATIONCHECK;
				}
			}

		try {
			return TerminZeit.create(tag, start, ende);
		} catch (InvalidTimeException e) {
			return null;
		}
	}
	
	public int getTerminDay(String name) {
		for (int i = 0; i < termine.length; i++)
			for (int j = 0; j < termine[i].length; j++)
				if (existsTermin(i, j) && getTermin(i, j).getName().equals(name))
					return i;
		return 0;
	}

	public int getTerminTime(String name) {
		for (int i = 0; i < termine.length; i++)
			for (int j = 0; j < termine[i].length; j++)
				if (existsTermin(i, j) && getTermin(i, j).getName().equals(name))
					return j;
		return 0;
	}

	/**
	 * Returnt true, wenn ein @Termin an dieser Stelle eingetragen ist
	 */
	public boolean existsTermin(int tag, int zeitslot) {
		return termine[tag][zeitslot] != null;
	}

	/**
	 * Returnt true, wenn an diesem Tag mindestens ein @Termin eingetragen ist
	 */
	public boolean existsTermin(int tag) {
		for (int i = 0; i < termine[tag].length; i++) {
			if (existsTermin(tag, i)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returnt true, wenn ein @Termin mit diesem Namen eingetragen ist
	 */
	public boolean existsTermin(String name) {
		for (int i = 0; i < termine.length; i++)
			for (int j = 0; j < termine[i].length; j++)
				if (existsTermin(i, j) && getTermin(i, j).getName().equals(name))
					return true;

		return false;
	}
	
	public String printTermin(int tag, int zeitslot) {
		String output = "";
		if (existsTermin(tag, zeitslot)) {
			Termin termin = getTermin(tag, zeitslot);
			TerminZeit dauer = getTerminDuration(termin);
			output = output += "\n" + TerminZeit.formatTime(dauer.getStart()) + " - "
					+ TerminZeit.formatTime(dauer.getEnde()) + ": " + termin.toString();

		}
		return !output.isEmpty() ? output : "Es wurde kein Termin eingetragen";
	}

	public String printTermine(int tag) {
		String output = "";
		if (existsTermin(tag)) {
			try {
				output += TerminZeit.convertIntToTag(tag);
			} catch (InvalidTimeException e) {
				output += "???";
			}
			for (int i = 0; i < termine[tag].length; i++) {
				if (existsTermin(tag, i)) {
					Termin termin = getTermin(tag, i);
					TerminZeit dauer = getTerminDuration(termin);
					output += printTermin(tag, i);
					i += dauer.getDauer();
				}
			}
		}

		return !output.isEmpty() ? output : "Es wurden noch keine Termine eingetragen";
	}
	
	public String printTermin(Termin termin) {
		TerminZeit duration = getTerminDuration(termin);
		return printTermin(duration.getTag(), duration.getStart());
	}

	public String printTermine() {
		String output = "";
		for (int i = 0; i < termine.length; i++) {
			if (!existsTermin(i))
				continue;

			output += printTermine(i) + "\n";
		}

		return !output.isEmpty() ? output : "Es wurden noch keine Termine eingetragen";
	}

	@Override
	public String toString() {
		return printTermine();
	}
	
	public String printArray() {
		String output = "";
		
		for (int tag = 0; tag < 7; tag++) {
			try  {
				output += TerminZeit.convertIntToTag(tag)+"\n";
			} catch(InvalidTimeException e) {
				return "???\n";
			}
			
			boolean anyTermin = false;
			for (int i = 0; i < 96; i++) {
				if(existsTermin(tag, i)) {
					output += i + ":" + getTermin(tag, i).getName() + " ";
					anyTermin = true;
				}
			}
			
			if(!anyTermin)
				output += "/";
			
			output += "\n";
		}

		
		return output;
	}

	// TODO
	public File saveAsFile(String filename) throws IOException {
		File file = new File(filename);

		if (!file.exists())
			file.createNewFile();

		FileWriter writer = new FileWriter(file);

		for (int i = 0; i < termine.length; i++)
			for (int j = 0; j < termine[i].length; j++) {
				if (existsTermin(i, j)) {
					Termin termin = getTermin(i, j);
					TerminZeit dauer = getTerminDuration(termin);
					writer.write(dauer.getTag() + "/" + dauer.getStart() + "-" + dauer.getEnde() + ":" + termin.getName() + "\n");
					
					j += dauer.getDauer();
					
					System.out.println("START" + dauer.getStart());
					System.out.println("ENDE" + dauer.getEnde());
				}
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
				String[] splittedLineAll = line.split(":");
				
				String name = splittedLineAll[1];
				
				String[] splittedLineValues = splittedLineAll[0].split("/");

				int tag = Integer.parseInt(splittedLineValues[0]);
				
				int start = Integer.parseInt(splittedLineValues[1].split("-")[0]);
				int ende = Integer.parseInt(splittedLineValues[1].split("-")[1]);

				wochenplan.addTermin(name, tag, start, ende);
			} catch (Exception e) {
			}
		}
		reader.close();
		return wochenplan;
	}
}
