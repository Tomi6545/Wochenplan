package wochenplan.verwaltung;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Wochenplan {

	Termin[][] termine = new Termin[7][48];

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

	public void addTermin(String name, int tag, int beginn, int ende) {
		for (int i = beginn; i < ende; i++) {
			termine[tag][i] = new Termin(name, beginn, ende);
		}
	}

	/**
	 * Return true wenn der Termin erfolgreich entfernt wurde. Andernfalls wird
	 * false returnt
	 */
	public boolean removeTermin(int tag, int zeitslot) {
		if (!existsTermin(tag, zeitslot))
			return false;

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

		return true;
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

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		boolean anyTermin = false;
		for (int tag = 0; tag < 7; tag++) {
			for (int i = 0; i < 48; i++) {
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
					;
				writer.write(i + "/" + j + ":" + getTermin(i, j).getName());
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

				wochenplan.addTermin(name, tag, zeitslot, zeitslot + 30);
			} catch (Exception e) {
			}
		}
		reader.close();
		return wochenplan;
	}
}
