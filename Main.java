
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        BookingSystem system = new BookingSystem();
        system.initializeSampleData();

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n=== Boost Physio Clinic Booking System ===");
            System.out.println("1. Add Patient");
            System.out.println("2. Remove Patient");
            System.out.println("3. Book Appointment");
            System.out.println("4. Change/Cancel Appointment");
            System.out.println("5. Attend Appointment");
            System.out.println("6. Generate Report");
            System.out.println("7. Exit");
            System.out.print("Select an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    system.addPatientFromInput(scanner);
                    break;
                case 2:
                    system.removePatientFromInput(scanner);
                    break;
                case 3:
                    system.bookAppointmentFromInput(scanner);
                    break;
                case 4:
                    system.changeOrCancelAppointmentFromInput(scanner);
                    break;
                case 5:
                    system.attendAppointmentFromInput(scanner);
                    break;
                case 6:
                    system.generateReport();
                    break;
                case 7:
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }

        scanner.close();
        System.out.println("System exited. Goodbye!");
    }
}