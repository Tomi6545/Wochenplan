package wochenplan.verwaltung;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import wochenplan.verwaltung.exceptions.InvalidTimeException;
import wochenplan.verwaltung.exceptions.TerminAddException;
import wochenplan.verwaltung.exceptions.TerminExistenceException;

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
	public void addTermin(String name, int tag, int beginn, int ende, Class<? extends Termin> terminClass) throws TerminAddException, InvalidTimeException {
		if (!TerminZeit.isValidTime(tag, beginn, ende))
			throw new InvalidTimeException();
		Termin[][] copy = termine.clone();
		
		Termin termin;
		try {
			termin = terminClass.getDeclaredConstructor(String.class).newInstance(name);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException| NoSuchMethodException | SecurityException e) {
			termin = new Termin(name);
		}
		for (int i = beginn; i < ende; i++) {
			if (existsTermin(tag, i))
				throw new TerminAddException();
			copy[tag][i] = termin;
		}

		termine = copy;
	}
	
	public void addTermin(String name, int tag, int beginn, int ende) throws TerminAddException, InvalidTimeException {
		addTermin(name, tag, beginn, ende, Termin.class);
	}

	public void addHinweg(int tag, int beginn, int ende, int wegDauer) throws TerminAddException, InvalidTimeException {

		if (!TerminZeit.isValidTime(tag, beginn - wegDauer, beginn))
			throw new InvalidTimeException();

		Termin[][] copy = termine.clone();
		Termin termin = new Weg("Hinweg");
		for (int i = beginn - wegDauer; i < beginn; i++) {
			if (existsTermin(tag, i))
				throw new TerminAddException();
			copy[tag][i] = termin;
		}

		termine = copy;
	}

	public void addRückweg(int tag, int beginn, int ende, int wegDauer)
			throws TerminAddException, InvalidTimeException {

		if (!TerminZeit.isValidTime(tag, ende, ende + wegDauer))
			throw new InvalidTimeException();

		Termin[][] copy = termine.clone();
		Termin termin = new Weg("Rückweg");
		for (int i = ende; i < ende + wegDauer ; i++) {
			if (existsTermin(tag, i))
				throw new TerminAddException();
			copy[tag][i] = termin;
		}

		termine = copy;
	}

	/**
	 * Versucht einen @Termin zu entfernen Wenn dieser nicht existiert tritt
	 * eine @TerminRemoveException auf
	 */
	public void removeTermin(int tag, int zeitslot) throws TerminExistenceException, InvalidTimeException {
		if (!TerminZeit.isValidTime(tag, zeitslot))
			throw new InvalidTimeException();
		if (!existsTermin(tag, zeitslot))
			throw new TerminExistenceException();

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

	public void renameTermin(int tag, int zeitslot, String newName)
			throws TerminExistenceException, InvalidTimeException {
		if (!TerminZeit.isValidTime(tag, zeitslot))
			throw new InvalidTimeException();
		if (!existsTermin(tag, zeitslot))
			throw new TerminExistenceException();

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

	public List<Termin> getTermine(int tag, String name) throws InvalidTimeException {
		List<Termin> termine = new ArrayList<>();

		if (!TerminZeit.isValidTime(tag))
			throw new InvalidTimeException();

		for (int i = 0; i < this.termine[tag].length; i++) {
			if (existsTermin(tag, i) && (name == null || getTermin(tag, i).getName().equals(name))) {
				Termin termin = getTermin(tag, i);
				TerminZeit dauer = getTerminDuration(termin);
				termine.add(termin);

				i += dauer.getDauer();
			}

		}

		return termine;
	}

	public List<Termin> getTermine(int tag) throws InvalidTimeException {
		return getTermine(tag, null);
	}

	public List<Termin> getTermine(String name) throws InvalidTimeException {
		List<Termin> termine = new ArrayList<>();

		for (int day = 0; day < this.termine.length; day++) {
			termine.addAll(getTermine(day, name));
		}

		return termine;
	}

	public List<Termin> getTermine() throws InvalidTimeException {
		return getTermine(null);
	}

	public Termin getNextTermin(int tag, int zeitslot, String name) throws InvalidTimeException {
		if (!TerminZeit.isValidTime(tag, zeitslot))
			throw new InvalidTimeException();

		for (int i = tag; i < termine.length; i++)
			for (int j = zeitslot; j < termine[i].length; j++) {
				if (existsTermin(i, j) && (name == null || getTermin(i, j).getName().equals(name)))
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
		return TerminZeit.isValidTime(tag, zeitslot) && termine[tag][zeitslot] != null;
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
					+ TerminZeit.formatTime(dauer.getEnde() + 1) + ": " + termin.toString();

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

	public String printTerminWithDay(Termin termin) {
		String output = "";
		TerminZeit duration = getTerminDuration(termin);
		try {
			output += TerminZeit.convertIntToTag(duration.getTag()) + ":"
					+ printTermin(duration.getTag(), duration.getStart());
		} catch (InvalidTimeException e) {
			output += "???";
		}
		return !output.isEmpty() ? output : "Es wurden noch keine Termine eingetragen";
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
			try {
				output += TerminZeit.convertIntToTag(tag) + "\n";
			} catch (InvalidTimeException e) {
				return "???\n";
			}

			boolean anyTermin = false;
			for (int i = 0; i < 96; i++) {
				if (existsTermin(tag, i)) {
					output += i + ":" + getTermin(tag, i).getName() + " ";
					anyTermin = true;
				}
			}

			if (!anyTermin)
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
					
					boolean isWeg = termin instanceof Weg;
					
					writer.write((isWeg ? "w_" : "") + dauer.getTag() + "/" + dauer.getStart() + "-" + dauer.getEnde() + ":"
							+ termin.getName() + "\n");

					j += dauer.getDauer();
				}
			}

		writer.close();
		return file;
	}

	public static Wochenplan fromFile(File file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));

		Wochenplan wochenplan = new Wochenplan();
		String line;
		while ((line = reader.readLine()) != null) {
			try {
				String[] splittedLineAll = line.split(":");

				String name = splittedLineAll[1];

				String[] splittedLineValues = splittedLineAll[0].split("/");
				
				String before = splittedLineValues[0];
				boolean isWeg = before.contains("w_");
				
				if(isWeg)
					before = before.replaceAll("w_", "");

				int tag = Integer.parseInt(before);

				int start = Integer.parseInt(splittedLineValues[1].split("-")[0]);
				int ende = Integer.parseInt(splittedLineValues[1].split("-")[1]);

				wochenplan.addTermin(name, tag, start, ende + 1, isWeg ? Weg.class : Termin.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		reader.close();
		return wochenplan;
	}
}
