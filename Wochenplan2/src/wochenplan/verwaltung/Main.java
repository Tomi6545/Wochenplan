package wochenplan.verwaltung;

import java.io.File;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;

import wochenplan.verwaltung.exceptions.TerminAddException;
import wochenplan.verwaltung.exceptions.TerminRemoveException;

public class Main {

	private static Scanner sc;

	public static void main(String[] args) {

		Wochenplan woche1 = new Wochenplan(new Termin[7][96]);
		sc = new Scanner(System.in);
		try {
			woche1.addTermin("Mathe", 2, 36, 42);
		} catch (TerminAddException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		menu();
		programm(woche1);

		for (int tag = 0; tag < 7; tag++) {
			for (int i = 0; i < 96; i++) {
				if (woche1.getTermin(tag, i) != null)
					System.out.println(woche1.getTermin(tag, i));
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
		System.out.printf("%-8s  %s%n", "save", "den Wochenplan als Datei speichern");
		System.out.printf("%-8s  %s%n", "load", "einen Wochenplan aus einer Datei laden");
		System.out.printf("%-8s  %s%n", "help", "die Optionen erneut anzuzeigen");
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
				int tag = sc.nextInt() - 1;

				System.out.println("Geben Sie die Start- und Enduhrzeit an (z.B. 13:30 Uhr als 13,5)");
				int beginn = (int) (sc.nextDouble() * 4);
				int ende = (int) (sc.nextDouble() * 4);

				System.out.println("Geben Sie den Namen des Termins ein");
				String TerminBezeichner = sc.next();

				for (int i = beginn; i < ende; i++) {
					woche.termine[tag][i] = addTermin(TerminBezeichner, beginn, ende);
				}
				break;
			case "REMOVE":
				System.out.println("Gebe Sie den Tag und den Zeitpunkt des Termins ein, welchen Sie löschen möchten.");
				int tag1 = sc.nextInt() - 1;
				int beginn1 = (int) (sc.nextDouble() * 4);

				try {
					if (!woche.existsTermin(tag1, beginn1)) {
						throw new TerminRemoveException();
					}
					System.out.println("Möchten Sie wirklich den folgenden Termin löschen?");
					System.out.println(woche.getTermin(tag1, beginn1));
					System.out.println("y für yes oder n für no");
					String input = sc.next();
					if (input.equals("y")) {
						woche.removeTermin(tag1, beginn1);
						System.out.println("Termin wurde erfolgreich gelöscht");
					}
				} catch (TerminRemoveException e) {
					System.out.println("Termin existiert nicht");
				}
				break;
			case "PRINT":
				System.out.println("Geben Sie den gewünschten Wochentag ein");
				System.out.println(
						"Montag = 1, Dienstag = 2; Mittwoch = 3, Donnerstag = 4, Freitag = 5; Samstag = 6, Sonntag = 7");
				printTermine(woche.termine, sc.nextInt() - 1);
				break;
			case "PRINTALL":
				printTermine(woche);
				break;
			case "RENAME":
				System.out.println("Möchten Sie den Namen oder den Zeitpunkt des Termins angeben?");
				System.out.printf("%-7s  %s%n", "name", "mit dem Namen");
				System.out.printf("%-7s  %s%n", "time", "mit dem Zeitpunkt");
				String s = sc.next();
				int tag2;
				int beginn2;
				if (s.toUpperCase().equals("NAME")) {
					System.out.println("Geben Sie den Namen des Termins ein, welches Sie umbenennen möchten");
					String oldName = sc.next();
					beginn2 = woche.getTermin(oldName).beginn;
					tag2 = 4;
				} else {
					System.out.println(
							"Gebe Sie den Tag und den Zeitpunkt des Termins ein, welches Sie umbennen möchten.");
					tag2 = sc.nextInt() - 1;
					beginn2 = (int) (sc.nextDouble() * 4);
				}

				try {
					if (!woche.existsTermin(tag2, beginn2)) {
						throw new TerminRemoveException();
					}
					System.out.println("Möchten Sie wirklich den folgenden Termin umbenennen?");
					System.out.println(woche.getTermin(tag2, beginn2));
					System.out.println("y für yes oder n für no");
					String input2 = sc.next();
					if (input2.equals("y")) {
						System.out.println("Geben Sie den neuen Namen des Termins ein");
						String newName = sc.next();
						woche.renameTermin(tag2, beginn2, newName);
						System.out.println("Termin erfolgreich umbenannt");
					}
				} catch (TerminRemoveException e) {
					System.out.println("Termin existiert nicht");
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
			default:
				System.out.println("Keine gültige Eingabe!");
				break;
			}
		}

	}

	public static Termin addTermin(String name, int beginn, int ende) {
		return new Termin(name, beginn, ende);
	}

	public static void printTermine(Wochenplan woche) {
		boolean anyTermin = false;
		for (int tag = 0; tag < 7; tag++) {
			if (woche.existsTermin(tag))
				switch (tag) {
				case 0:
					System.out.println("Montag:");
					break;
				case 1:
					System.out.println("Dienstag:");
					break;
				case 2:
					System.out.println("Mittwoch:");
					break;
				case 3:
					System.out.println("Donnerstag:");
					break;
				case 4:
					System.out.println("Freitag:");
					break;
				case 5:
					System.out.println("Samstag:");
					break;
				case 6:
					System.out.println("Sonntag:");
					break;
				}
			for (int i = 0; i < 96; i++) {
				if (woche.termine[tag][i] != null) {
					System.out.println(woche.termine[tag][i]);
					i += woche.termine[tag][i].dauer;
					anyTermin = true;
				}
			}
		}

		if (!anyTermin)
			System.out.println("Es wurden noch keine Termine eingetragen");
	}

	public static void printTermine(Termin[][] termine, int tag) {
		boolean anyTermin = false;
		for (int i = 0; i < 96; i++) {
			if (termine[tag][i] != null) {
				System.out.println(termine[tag][i]);
				i += termine[tag][i].dauer - 1;
				anyTermin = true;
			}
		}

		if (!anyTermin)
			System.out.println("Es wurden noch keine Termine eingetragen");
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
}
