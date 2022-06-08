package wochenplan.verwaltung;

import java.util.Scanner;

public class Main {

	private static Scanner sc;

	public static void main(String[] args) {

		Termin[][] termine = new Termin[7][96];
		Wochenplan woche1 = new Wochenplan(termine);
		sc = new Scanner(System.in);

		menu();
		programm(termine, woche1);
		
	}

	public static void menu() {

		System.out.println("Was möchtest du tun?");
		System.out.println("Tippe A, um einen neuen Termin zu erstellen");
		System.out.println("Tippe B, um einen Termin zu löschen");
		System.out.println("Tippe C, um den Stundenplan auszugeben");
		System.out.println("Tippe D, um einen Termin umzubennen");
		System.out.println("Tippe E für Ende");
	}

	public static void programm(Termin[][] termine, Wochenplan woche) {

		boolean looping = true;

		while (looping) {
			String funct = sc.next();

			switch (funct.toUpperCase()) {
			case "A":
				System.out.printf(
						"Geben sie den Wochentag ein, an dem sie einen Termin hinzufügen wollen%n(Montag = 1, Dienstag = 2; Mittwoch = 3, Donnerstag = 4, Freitag = 5; Samstag = 6, Sonntag = 7)%n");
				int tag = sc.nextInt() - 1;

				System.out.println("Geben Sie die Start- und Enduhrzeit an (z.B. 13:30 Uhr als 13,5)");
				int beginn = (int) (sc.nextDouble() * 4);
				int ende = (int) (sc.nextDouble() * 4);

				System.out.println("Geben sie den Namen des Termins ein");
				String TerminBezeichner = sc.next();

				for (int i = beginn; i < ende; i++) {
					termine[tag][i] = addTermin(TerminBezeichner, beginn, ende);
				}
				break;
			case "B":
				System.out.println("Gebe Sie den Tag und die Startpunkt des Termins ein, welches Sie löschen möchten.");
				break;
			case "C":
				System.out.println("Möchten Sie eine Wochenübersicht oder Tagesübersicht?");
				System.out.println("w für Wochenübersicht oder andere Eingabe für Tagesübersicht");
				String in = sc.next();
				if (in.equals("w")) {
					printTermine(woche);
				} else {
					System.out.println("Geben Sie den gewünschten Wochentag ein");
					printTermine(woche.termine, sc.nextInt() - 1);
				}
				break;
			case "D":
				System.out.println("Gebe Sie den Tag und den Startpunkt des Termins ein, welches Sie umbennen möchten.");
				int tag1 = sc.nextInt() - 1;
				int uhrzeit = (int) (sc.nextDouble() * 4);

				if (woche.existsTermin(tag1, uhrzeit)) {
					System.out.println("Möchten Sie wirklich den folgenden Termin umbenennen?");
					System.out.println(woche.getTermin(tag1, uhrzeit));
					System.out.println("y für yes oder n für no");
					String input = sc.next();
					if (input.equals("y")) {
						System.out.println("Geben Sie den neuen Namen des Termins ein");
						woche.termine[tag1][uhrzeit].setName(sc.next());
					}
				} else {
					System.out.println("Termin exisitert nicht");
				}
				break;
			case "E":
				System.out.println("Thank you for using our software! Bye");
				looping = false;
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
			if(woche.existsTermin(tag))
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
}
