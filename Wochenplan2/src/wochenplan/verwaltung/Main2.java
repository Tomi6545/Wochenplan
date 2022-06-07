package wochenplan.verwaltung;

import java.util.Scanner;

public class Main2 {
	
	private static Wochenplan wochenplan;
	private static Scanner sc;
	
	public static void main(String[] args) {
		
		wochenplan = new Wochenplan();
		sc = new Scanner(System.in);
		
		programm();
	}
	
	public static void printMenu() {

		System.out.println("Was möchtest du tun?");
		System.out.println("Tippe A, um einen neuen Termin zu erstellen");
		System.out.println("Tippe B, um einen Termin zu löschen");
		System.out.println("Tippe C, um den Stundenplan auszugeben");
		System.out.println("Tippe D, um einen Termin umzubennen");
		System.out.println("Tippe E für Ende");
	}
	
	public static void programm() {
	WHILE: while(true) {
			printMenu();
			String funct = sc.next();
			
			switch(funct.toUpperCase()) {
				case "A":
					System.out.printf(
							"Geben sie den Wochentag ein, an dem sie einen Termin hinzufügen wollen%n(Montag = 1, Dienstag = 2; Mittwoch = 3, Donnerstag = 4, Freitag = 5; Samstag = 6, Sonntag = 7)%n");
					int tag = sc.nextInt() - 1;
	
					System.out.println("Geben Sie die Start- und Enduhrzeit an (z.B. 13:30 Uhr als 13,5)");
					int beginn = (int) (sc.nextDouble() * 2);
					int ende = (int) (sc.nextDouble() * 2);
	
					System.out.println("Geben sie den Namen des Termins ein");
					String name = sc.next();
					
					wochenplan.addTermin(name, tag, beginn, ende);
					break;
				case "B":
					System.out.println("Gebe Sie den Tag und die Uhrzeit des Termins ein, welches Sie löschen möchten.");
					break;
				case "C":
					System.out.println(wochenplan.toString());
					break;
				case "D":
					System.out.println("Gebe Sie den Tag und die Uhrzeit des Termins ein, welches Sie umbennen möchten.");
					int tag1 = sc.nextInt();
					int uhrzeit = (int) (sc.nextDouble() * 2);
					System.out.println("Geben Sie den neuen Namen des Termins ein");
					String name1 = sc.next();
					
					Termin termin = wochenplan.getTermin(tag1, uhrzeit);
					termin.setName(name1);
					break;
				case "E":
					System.out.println("Thank you for using our software! Bye");
					System.exit(0);
					break WHILE;
				default:
					System.out.println("Keine gültige Eingabe!");
					break;
			}
		}
	}

}
