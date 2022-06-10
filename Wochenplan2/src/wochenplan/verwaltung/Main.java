package wochenplan.verwaltung;

import java.io.File;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;

import wochenplan.verwaltung.exceptions.InvalidTimeException;
import wochenplan.verwaltung.exceptions.TerminAddException;
import wochenplan.verwaltung.exceptions.TerminRemoveException;

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
				System.out.println("Geben Sie den Wochentag ein, an dem sie einen Termin hinzufügen wollen");
				System.out.println(
						"Montag = 1, Dienstag = 2; Mittwoch = 3, Donnerstag = 4, Freitag = 5; Samstag = 6, Sonntag = 7");
				int tag = getIntegerValue() - 1;

				System.out.println("Geben Sie die Start- und Enduhrzeit an (z.B. 13:30 Uhr als 13,5)");
				int beginn = (int) (getDoubleValue() * 4);
				int ende = (int) (getDoubleValue() * 4);

				System.out.println("Geben Sie den Namen des Termins ein");
				String TerminBezeichner = sc.next();

				try {
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
				System.out.println("Möchten Sie den Namen oder den Zeitpunkt des Termins angeben?");
				System.out.printf("%-7s  %s%n", "name", "mit dem Namen");
				System.out.printf("%-7s  %s%n", "time", "mit dem Zeitpunkt");
				String s = getStringValue("name", "time");
				int tag1;
				int beginn1;
				if (s.equals("name")) {
					System.out.println("Geben Sie den Namen des Termins ein, welches Sie löschen möchten");
					String oldName = sc.next();
					beginn1 = woche.getTerminTime(oldName);
					tag1 = woche.getTerminDay(oldName);
				} else {
					System.out.println(
							"Gebe Sie den Tag und den Zeitpunkt des Termins ein, welches Sie löschen möchten.");
					tag1 = getIntegerValue() - 1;
					beginn1 = (int) (getDoubleValue() * 4);
				}

				try {
					if (!woche.existsTermin(tag1, beginn1)) {
						throw new TerminRemoveException();
					}
					System.out.println("Möchten Sie wirklich den folgenden Termin löschen?");
					System.out.println(woche.printTermin(tag1, beginn1));
					System.out.println("y für yes oder n für no");
					String input1 = getStringValue("y", "n");
					if (input1.equals("y")) {
						woche.removeTermin(tag1, beginn1);
						System.out.println("Termin erfolgreich gelöscht");
					} else {
						System.out.println("Vorgang abgrebrochen");
					}
				} catch (TerminRemoveException e) {
					System.out.println("Termin existiert nicht");
				} catch (InvalidTimeException | IndexOutOfBoundsException e) {
					System.out.println("Ungültige Zeit!");
				}
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
				System.out.println("Möchten Sie den Namen oder den Zeitpunkt des Termins angeben?");
				System.out.printf("%-7s  %s%n", "name", "mit dem Namen");
				System.out.printf("%-7s  %s%n", "time", "mit dem Zeitpunkt");
				String s2 = getStringValue("name", "time");
				int tag2;
				int beginn2;
				if (s2.toUpperCase().equals("NAME")) {
					System.out.println("Geben Sie den Namen des Termins ein, welches Sie umbenennen möchten");
					String oldName = sc.next();
					beginn2 = woche.getTerminTime(oldName);
					tag2 = woche.getTerminDay(oldName);
				} else {
					System.out.println(
							"Gebe Sie den Tag und den Zeitpunkt des Termins ein, welches Sie umbennen möchten.");
					tag2 = getIntegerValue() - 1;
					beginn2 = (int) (getDoubleValue() * 4);
				}

				try {
					if (!woche.existsTermin(tag2, beginn2)) {
						throw new TerminRemoveException();
					}
					if (woche.existsTermin(tag2, beginn2)) {
						System.out.println("Möchten Sie wirklich den folgenden Termin umbenennen?");
						System.out.println(woche.printTermin(tag2, beginn2));
						System.out.println("y für yes oder n für no");
						String input2 = getStringValue("y", "n");
						if (input2.equals("y")) {
							System.out.println("Geben Sie den neuen Namen des Termins ein");
							String newName = sc.next();
							woche.renameTermin(tag2, beginn2, newName);
							System.out.println("Termin erfolgreich umbenannt");
						} else {
							System.out.println("Vorgang abgrebrochen");
						}
					} else {
						System.out.println("Termin exisitert nicht");
					}
				} catch (TerminRemoveException e) {
					System.out.println("Termin existiert nicht");
				} catch (InvalidTimeException | IndexOutOfBoundsException e) {
					System.out.println("Ungültige Zeit");
				}

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
				break;	
			case "FIND":
				break;
			case "DEBUG":
				System.out.println(woche.printArray());
				break;
			default:
				System.out.println("Keine gültige Eingabe!");
				break;
			}
		}

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

	public static String getStringValue(String s1, String s2) {
		String value;
		while (true) {
			value = sc.next();
			if (value.equals(s1)) {
				return s1;
			} else if (value.equals(s2)) {
				return s2;
			} else {
				System.out.println("Ungültige Eingabe");
			}
		}
	}
}
