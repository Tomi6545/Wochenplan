package wochenplan.verwaltung;

import java.io.File;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;

import wochenplan.verwaltung.exceptions.InvalidTimeException;
import wochenplan.verwaltung.exceptions.TerminAddException;
import wochenplan.verwaltung.exceptions.TerminExistenceException;

public class Main {

	private static Scanner sc;

	public static void main(String[] args) {

		Wochenplan woche1 = new Wochenplan(new Termin[7][96]);
		sc = new Scanner(System.in);
		try {
			woche1.addTermin("Mathe", 2, 36, 42);
		} catch (TerminAddException | InvalidTimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		menu();
		programm(woche1);

		for (int tag = 0; tag < 7; tag++) {
			for (int i = 0; i < 96; i++) {
				if (woche1.getTermin(tag, i) != null)
					System.out.println("Index: " + tag + ", " + i + woche1.printTermin(tag, i));
			}
		}
	}

	public static void menu() {

		System.out.println("Was möchten Sie tun? Sie haben folgende Optionen:");
		System.out.printf("%-8s  %s%n", "add", "einen neuen Termin erstellen");
		System.out.printf("%-8s  %s%n", "remove", "einen Termin löschen");
		System.out.printf("%-8s  %s%n", "rename", "einen Termin umbenennen");
		System.out.printf("%-8s  %s%n", "print", "den Tagesplan ausgeben");
		System.out.printf("%-8s  %s%n", "printall", "den Wochenplan ausgeben");
		System.out.printf("%-8s  %s%n", "search", "den nächsten Termin suchen");
		System.out.printf("%-8s  %s%n", "find", "mögliche Zeitpunkte für Termin finden");
		System.out.printf("%-8s  %s%n", "save", "den Wochenplan als Datei speichern");
		System.out.printf("%-8s  %s%n", "load", "einen Wochenplan aus einer Datei laden");
		System.out.printf("%-8s  %s%n", "help", "die Optionen erneut anzuzeigen");
		System.out.printf("%-8s  %s%n", "debug", "Option zum Testen");
		System.out.printf("%-8s  %s%n", "end", "das Programm beenden");

	}

	public static void programm(Wochenplan woche) {

		boolean looping = true;

		while (looping) {
			String funct = sc.next();

			switch (funct.toUpperCase()) {
			case "HELP":
				menu();
				break;
			case "ADD":
				try {
					System.out.println("Geben Sie den Wochentag ein, an dem sie einen Termin hinzufügen wollen");
					System.out.println("Montag, Dienstag, Mittwoch, Donnersta, Freitag; Samstag, Sonntag");
					int tag = TerminZeit.convertTagToInt(sc.next());

					System.out.println("Geben Sie die Start- und Enduhrzeit an (z.B. 13:30 Uhr als 13,5)");
					int beginn = (int) (getDoubleValue() * 4);
					int ende = (int) (getDoubleValue() * 4);

					System.out.println("Geben Sie den Namen des Termins ein");
					String TerminBezeichner = sc.next();

					woche.addTermin(TerminBezeichner, tag, beginn, ende);
					System.out.println("Termin wurde erfolgreich hinzugefügt");
					System.out.println("Möchten Sie noch den Hin- oder Rückweg eintragen?");
					System.out.println("y für yes oder n für no");
					String input = getStringValue("y", "n");
					if (input.equals("y")) {
						System.out.printf("%-4s  %s%n", "h", "für den Hinweg");
						System.out.printf("%-4s  %s%n", "r", "für den Rückweg");
						System.out.printf("%-4s  %s%n", "hr", "für den Hin- und Rückweg");
						String weg = getStringValue("h", "r");
						if (weg.equals("h")) {
							System.out.println("Geben Sie die Dauer des Weges in Minuten ein");
							int wegDauer = getIntegerValue() / 15;
							woche.addHinweg(tag, beginn, ende, wegDauer);
							System.out.println("Hinweg wurde erfolgreich hinzugefügt");
						}
					}
				} catch (TerminAddException e1) {
					System.out.println("Termin existiert bereits an diesem Zeitpunkt");
				} catch (InvalidTimeException e) {
					System.out.println("Ungültige Zeit!");
				}

				break;
			case "REMOVE":
				editTermin(woche, "löschen");
				break;
			case "PRINT":
				System.out.println("Geben Sie den gewünschten Wochentag ein");
				System.out.println(
						"Montag = 1, Dienstag = 2; Mittwoch = 3, Donnerstag = 4, Freitag = 5; Samstag = 6, Sonntag = 7");
				System.out.println(woche.printTermine(getIntegerValue() - 1));
				break;
			case "PRINTALL":
				System.out.println(woche.printTermine());
				break;
			case "RENAME":
				editTermin(woche, "umbenennen");
				break;
			case "END":
				System.out.println("Programm erfolgreich beendet");
				looping = false;
				break;
			case "SAVE":
				System.out.println("Geben Sie einen Namen für die Datei an");
				String filename = sc.next();
				try {
					woche.saveAsFile(filename);
					System.out.println("Wochenplan wurde erfolgreich als Datei abgespeichert!");
				} catch (IOException e) {
					System.out.println("Ein Fehler beim Speichern der Datei " + filename + " ist aufgetreten!");
				}
				break;
			case "LOAD":
				System.out.println("Geben Sie den Namen der Datei an");
				String filename2 = sc.next();
				try {
					woche = Wochenplan.fromFile(new File(filename2));
					System.out.println("Wochenplan wurde erfolgreich aus der Datei geladen!");
				} catch (IOException e) {
					System.out.println("Ein Fehler beim Laden der Datei " + filename2 + " ist aufgetreten!");
				}
				break;
			case "SEARCH":
				System.out.println("Möchten Sie einen bestimmten Termin mit Namen suchen?");
				System.out.println("y für yes oder n für no");
				String searchName = null;
				if (getStringValue("y", "n").equals("y")) {
					System.out.println("Geben Sie den Namen ein");
					searchName = sc.next();
				}

				System.out.println("Wählen Sie eine Option aus");
				System.out.printf("%-7s  %s%n", "now", "ab der aktuellen Systemzeit suchen");
				System.out.printf("%-7s  %s%n", "time", "ab einer bestimmten Zeit suchen");
				System.out.printf("%-7s  %s%n", "all", "im ganzen Wochenplan suchen");
				String searchOption = getStringValue("now", "time", "all");

				int searchTag;
				int searchZeitSlot;

				switch (searchOption.toLowerCase()) {
				case "now":
					searchTag = TerminZeit.getTodayAsInt();
					searchZeitSlot = TerminZeit.getCurrentTimeAsTimeSlot();
					break;
				case "time":
					System.out.println("Geben Sie einen Tag ein");
					showTagOptions();
					try {
						searchTag = TerminZeit.convertTagToInt(getStringValue("Montag", "Dienstag", "Mitwoch",
								"Donnerstag", "Freitag", "Samstag", "Sonntag"));
					} catch (InvalidTimeException e) {
						searchTag = -1;
					}
					System.out.println("Geben Sie eine Uhrzeit ein");
					showZeitOptions();
					searchZeitSlot = TerminZeit.convertTimeToTimeSlot(getDoubleValue());
					break;
				default:
					searchTag = 0;
					searchZeitSlot = 0;
					break;
				}

				try {
					Termin termin = woche.getNextTermin(searchTag, searchZeitSlot, searchName);
					System.out.println(woche.printTermin(termin));
				} catch (InvalidTimeException e) {
					System.out.println("Ungültige Zeit");
				}
				break;
			case "FIND":
				break;
			case "DEBUG":
				System.out.println(woche.printArray());
				break;
			default:
				System.out.println("Keine gültige Eingabe!");
				System.out.println("Geben sie HELP für Hilfe ein");
				break;
			}
		}

	}

	public static void showTagOptions() {
		System.out.println("Montag, Dienstag, Mittwoch, Donnerstag, Freitag, Samstag, Sonntag");
	}

	public static void showZeitOptions() {
		System.out.println("z.B. 13:30 Uhr als 13,5");
	}

	public static int getIntegerValue() {
		while (true) {
			try {
				int value = sc.nextInt();
				return value;
			} catch (InputMismatchException e) {
				System.out.println("Geben Sie eine gültige Zahl ein");
				sc.next();
			}
		}
	}

	public static double getDoubleValue() {
		while (true) {
			try {
				double value = sc.nextDouble();
				return value;
			} catch (InputMismatchException e) {
				System.out.println("Geben Sie eine gültige Zahl ein");
				sc.next();
			}
		}
	}

	public static String getStringValue(String... values) {
		String value;
		while (true) {
			value = sc.next();

			for (String v : values)
				if (value.equals(v))
					return value;

			System.out.println("Ungültige Eingabe");
			System.out.println("Sie haben folgende Optionen:");
			System.out.println(String.join(", ", values));
		}
	}

	public static void editTermin(Wochenplan woche, String option) {
		System.out.println("Möchten Sie den Namen oder den Zeitpunkt des Termins angeben?");
		System.out.printf("%-7s  %s%n", "name", "mit dem Namen");
		System.out.printf("%-7s  %s%n", "time", "mit dem Zeitpunkt");
		String s2 = getStringValue("name", "time");
		int tag2;
		int beginn2;
		if (s2.equals("name")) {
			System.out.println("Geben Sie den Namen des Termins ein, welches Sie " + option + " möchten");
			String oldName = sc.next();
			beginn2 = woche.getTerminTime(oldName);
			tag2 = woche.getTerminDay(oldName);
		} else {
			System.out
					.println("Gebe Sie den Tag und den Zeitpunkt des Termins ein, welches Sie " + option + " möchten.");
			tag2 = getIntegerValue() - 1;
			beginn2 = (int) (getDoubleValue() * 4);
		}

		try {
			if (!woche.existsTermin(tag2, beginn2)) {
				throw new TerminExistenceException();
				
			}
			if (woche.existsTermin(tag2, beginn2)) {
				System.out.println("Möchten Sie wirklich den folgenden Termin " + option + "?");
				System.out.println(woche.printTermin(tag2, beginn2));
				System.out.println("y für yes oder n für no");
				String input2 = getStringValue("y", "n");
				if (input2.equals("y")) {
					if (option.equals("umbenennen")) {
						System.out.println("Geben Sie den neuen Namen des Termins ein");
						String newName = sc.next();
						woche.renameTermin(tag2, beginn2, newName);
						System.out.println("Termin erfolgreich umbenannt");
					} else {
						woche.removeTermin(tag2, beginn2);
						System.out.println("Termin erfolgreich gelöscht");
					}
				} else {
					System.out.println("Vorgang abgrebrochen");
				}
			} else {
				System.out.println("Termin exisitert nicht");
			}
		} catch (TerminExistenceException e) {
			System.out.println("Termin existiert nicht");
		} catch (InvalidTimeException | IndexOutOfBoundsException e) {
			System.out.println("Ungültige Zeit");
		}

	}
}
