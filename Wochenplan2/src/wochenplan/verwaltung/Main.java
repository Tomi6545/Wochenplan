package wochenplan.verwaltung;

import java.io.File;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import wochenplan.verwaltung.exceptions.InvalidTimeException;
import wochenplan.verwaltung.exceptions.TerminAddException;
import wochenplan.verwaltung.exceptions.TerminExistenceException;
import wochenplan.verwaltung.termin.Termin;
import wochenplan.verwaltung.termin.TerminEditOptions;
import wochenplan.verwaltung.termin.TerminZeit;
import wochenplan.verwaltung.termin.Hinweg;
import wochenplan.verwaltung.termin.Rueckweg;

public class Main {

	private static Scanner sc;

	public static void main(String[] args) {
		Wochenplan woche = new Wochenplan(new Termin[7][96]);
		sc = new Scanner(System.in);

		menu();
		programm(woche);
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
			case "MENU":
				menu();
				break;
			case "ADD":
				try {
					System.out.println("Geben Sie den Wochentag ein, an dem sie einen Termin hinzufügen wollen");
					showTagOptions();
					int tag = TerminZeit.convertTagToInt(getStringValue("Montag", "Dienstag", "Mittwoch", "Donnerstag",
							"Freitag", "Samstag", "Sonntag"));

					System.out.println("Geben Sie die Start- und Enduhrzeit an (z.B. 13:30 Uhr als 13,5)");

					int beginn = TerminZeit.convertTimeToTimeSlot(getDoubleValue());
					int ende = TerminZeit.convertTimeToTimeSlot(getDoubleValue());

					System.out.println("Geben Sie den Namen des Termins ein");
					String terminBezeichner = sc.next();

					woche.addTermin(terminBezeichner, tag, beginn, ende);
					System.out.println("Termin wurde erfolgreich hinzugefügt");
					System.out.println("Möchten Sie noch den Hin- oder Rückweg eintragen?");
					showConfirmOptions();
					if (getStringValue("y", "n").equals("y")) {
						System.out.printf("%-4s  %s%n", "h", "für den Hinweg");
						System.out.printf("%-4s  %s%n", "r", "für den Rückweg");
						System.out.printf("%-4s  %s%n", "hr", "für den Hin- und Rückweg");
						String weg = getStringValue("h", "r", "hr");
						System.out.println("Geben Sie die Dauer des Weges in Minuten ein");
						int wegDauer = (int) Math.ceil(getIntegerValue() / 15.0);
						if (weg.equals("h")) {
							woche.addHinweg(tag, beginn, ende, wegDauer);
							System.out.println("Hinweg wurde erfolgreich hinzugefügt");
						} else if (weg.equals("r")) {
							woche.addRückweg(tag, beginn, ende, wegDauer);
							System.out.println("Rückweg wurde erfolgreich hinzugefügt");
						} else {
							woche.addHinweg(tag, beginn, ende, wegDauer);
							woche.addRückweg(tag, beginn, ende, wegDauer);
							System.out.println("Hin- und Rückweg wurden erfolgreich hinzugefügt");
						}
					}
				} catch (TerminAddException e) {
					System.out.println("Termin existiert bereits an diesem Zeitpunkt");
				} catch (InvalidTimeException e) {
					System.out.println("Ungültige Zeit!");
				}

				break;
			case "REMOVE":
				editTermin(woche, TerminEditOptions.REMOVE);
				break;
			case "PRINT":
				System.out.println("Geben Sie den gewünschten Wochentag ein");
				showTagOptions();
				int tag = 0;
				try {
					tag = TerminZeit.convertTagToInt(getStringValue("Montag", "Dienstag", "Mittwoch", "Donnerstag",
							"Freitag", "Samstag", "Sonntag"));
				} catch (InvalidTimeException e) {
					System.out.println("Ungültige Zeit");
				}
				if (woche.existsTermin(tag)) {
					boolean weg = false;
					System.out.println("Möchten Sie die eingetragenen Wegzeiten auch ausgeben?");
					System.out.println("y für yes oder n für no");
					if (getStringValue("y", "n").equals("y")) {
						weg = true;
					}
					System.out.println(woche.printTermine(tag, weg));
				} else {
					System.out.println("An diesem Tag sind noch keine Termine eingetragen");
				}

				break;
			case "PRINTALL":
				if(woche.existsTermine()) {
					boolean withWeg = false;
					System.out.println("Möchten Sie die eingetragenen Wegzeiten auch ausgeben?");
					System.out.println("y für yes oder n für no");
					if (getStringValue("y", "n").equals("y")) {
						withWeg = true;
					}
					System.out.println(woche.printTermine(withWeg));
				} else {
					System.out.println("Es sind noch keine Termine eingetragen");
				}
				
				break;
			case "RENAME":
				editTermin(woche, TerminEditOptions.RENAME);
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
				showConfirmOptions();
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
						searchTag = TerminZeit.convertTagToInt(getStringValue("Montag", "Dienstag", "Mittwoch",
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
				System.out.println("Geben Sie einen Tag ein");
				showTagOptions();
				try {
					int findTag = TerminZeit.convertTagToInt(getStringValue("Montag", "Dienstag", "Mittwoch",
							"Donnerstag", "Freitag", "Samstag", "Sonntag"));
					System.out.println("Geben Sie eine Termindauer in Minuten ein");
					int findDauer = (int) Math.ceil(getIntegerValue() / 15.0);

					List<Integer> findResult = woche.findEmptyZeitSlots(findTag, findDauer);

					while (true) {
						if (findResult.isEmpty()) {
							System.out.println("Es wurden keine möglichen Zeitpunkte gefunden");
							break;
						}

						System.out.println("Mögliche Zeitpunkte am " + TerminZeit.convertIntToTag(findTag) + ":");
						for (int findSlots : findResult)
							System.out.println(TerminZeit.formatTime(findSlots) + " - "
									+ TerminZeit.formatTime(findSlots + findDauer));
						System.out.println(
								"Möchten Sie schauen welche Zeitpunkte in einem anderen Wochenplan auch frei sind?");
						showConfirmOptions();
						if (getStringValue("y", "n").equals("y")) {
							System.out.println("Geben Sie einen Dateinamen ein");
							String findOtherFile = sc.next();
							try {
								Wochenplan other = Wochenplan.fromFile(new File(findOtherFile));
								findResult.retainAll(other.findEmptyZeitSlots(findTag, findDauer));
							} catch (IOException e) {
								System.out.println("Die Datei konnte nicht geladen werden");
								break;
							}
						} else
							break;

					}

				} catch (InvalidTimeException e) {
					System.out.println("Ungültige Zeit");
				}

				break;
			case "DEBUG":
				System.out.println(woche.printArray());
				break;
			default:
				System.out.println("Keine gültige Eingabe!");
				System.out.println("Geben Sie HELP für Hilfe ein");
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

	public static void showConfirmOptions() {
		System.out.println("y für yes oder n für no");
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

	public static int getIntegerValue(int min, int max) {
		while (true) {
			try {
				int value = sc.nextInt();
				if (min <= value && value <= max) {
					return value;
				} else {
					System.out.println("Die Zahl muss zwischen " + min + " und " + max + " liegen");
				}
			} catch (InputMismatchException e) {
				System.out.println("Geben Sie eine gültige Zahl ein");
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

	private static void editTermin(Wochenplan woche, TerminEditOptions option) {
		System.out.println("Möchten Sie den Namen oder den Zeitpunkt des Termins angeben?");
		System.out.printf("%-7s  %s%n", "name", "mit dem Namen");
		System.out.printf("%-7s  %s%n", "time", "mit dem Zeitpunkt");
		String select = getStringValue("name", "time");
		int tag = 0;
		int beginn = 0;
		Termin termin;
		TerminZeit zeit;

		try {
			if (select.equals("name")) {
				System.out
						.println("Geben Sie den Namen des Termins ein, welchen Sie " + option.getAction() + " möchten");
				String oldName = sc.next();
				if (woche.existsTermin(oldName)) {
					if (woche.getTermine(oldName).size() == 1) {
						termin = woche.getTermine(oldName).get(0);
						zeit = woche.getTerminDuration(termin);
						beginn = zeit.getStart();
						tag = zeit.getTag();
					} else {
						System.out.println("Welchen der folgenden Termine möchten Sie " + option.getAction() + "?");
						int index = 0;
						for (Termin s : woche.getTermine(oldName)) {
							System.out.println("(" + index + ") " + woche.printTerminWithDay(s));
							index++;
						}
						System.out
								.println("Geben Sie die in der Klammer stehende Zahl ein, um den Termin auszuwählen ");
						int choice = getIntegerValue(0, woche.getTermine(oldName).size() - 1);
						termin = woche.getTermine(oldName).get(choice);
						zeit = woche.getTerminDuration(termin);
						beginn = zeit.getStart();
						tag = zeit.getTag();
					}
				}
			} else {
				System.out.println("Gebe Sie den Tag und den Zeitpunkt des Termins ein, welches Sie "
						+ option.getAction() + " möchten.");
				showTagOptions();
				tag = TerminZeit.convertTagToInt(getStringValue("Montag", "Dienstag", "Mittwoch", "Donnerstag",
						"Freitag", "Samstag", "Sonntag"));
				beginn = TerminZeit.convertTimeToTimeSlot(getDoubleValue());
			}

			if (!woche.existsTermin(tag, beginn)) {
				throw new TerminExistenceException();
			}

			termin = woche.getTermin(tag, beginn);
			zeit = woche.getTerminDuration(termin);
			System.out.println("Möchten Sie wirklich den folgenden Termin " + option.getAction() + "?");
			System.out.println(woche.printTerminWithDay(termin));
			System.out.println("y für yes oder n für no");
			if (getStringValue("y", "n").equals("y")) {
				if (option == TerminEditOptions.RENAME) {
					System.out.println("Geben Sie den neuen Namen des Termins ein");
					String newName = sc.next();
					woche.renameTermin(tag, beginn, newName);
					System.out.println("Termin erfolgreich umbenannt");
				} else {
					woche.removeTermin(tag, beginn);
					if (woche.getTermin(tag, beginn - 1) instanceof Hinweg) {
						woche.removeTermin(tag, beginn - 1);
					}
					if (woche.getTermin(tag, zeit.getEnde() + 1) instanceof Rueckweg) {
						woche.removeTermin(tag, zeit.getEnde() + 1);
					}
					System.out.println("Termin erfolgreich gelöscht");
				}
			} else {
				System.out.println("Vorgang abgrebrochen");
			}
		} catch (TerminExistenceException e) {
			System.out.println("Termin existiert nicht");
		} catch (InvalidTimeException e) {
			System.out.println("Ungültige Zeit");
		}
	}
}
