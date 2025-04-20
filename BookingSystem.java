
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class BookingSystem {
    private List<Physiotherapist> physiotherapists;
    private List<Patient> patients;
    private List<Appointment> appointments;
    private int nextBookingId;

    public BookingSystem() {
        physiotherapists = new ArrayList<>();
        patients = new ArrayList<>();
        appointments = new ArrayList<>();
        nextBookingId = 1;
    }

    public void initializeSampleData() {
        // Sample physiotherapists
        Physiotherapist pt1 = new Physiotherapist(1, "Dr. Smith", "123 Main St", "555-1234");
        pt1.addExpertise("Physiotherapy");
        pt1.addExpertise("Rehabilitation");

        Physiotherapist pt2 = new Physiotherapist(2, "Dr. Johnson", "456 Oak Ave", "555-5678");
        pt2.addExpertise("Osteopathy");
        pt2.addExpertise("Massage Therapy");

        physiotherapists.add(pt1);
        physiotherapists.add(pt2);

        // Sample patients
        patients.add(new Patient(101, "John Doe", "789 Elm St", "555-9012"));
        patients.add(new Patient(102, "Jane Smith", "321 Pine Rd", "555-3456"));

        // Sample appointments (4-week timetable)
        LocalDateTime now = LocalDateTime.now();
        for (int i = 0; i < 4; i++) {
            LocalDateTime time1 = now.plusWeeks(i).withHour(10).withMinute(0);
            LocalDateTime time2 = now.plusWeeks(i).withHour(11).withMinute(0);
            LocalDateTime time3 = now.plusWeeks(i).withHour(14).withMinute(0);

            appointments.add(new Appointment(nextBookingId++, time1, time1.plusHours(1),
                    "Neural Mobilisation", pt1, null, AppointmentStatus.AVAILABLE));
            appointments.add(new Appointment(nextBookingId++, time2, time2.plusHours(1),
                    "Acupuncture", pt1, null, AppointmentStatus.AVAILABLE));
            appointments.add(new Appointment(nextBookingId++, time3, time3.plusHours(1),
                    "Mobilisation of Spine", pt2, null, AppointmentStatus.AVAILABLE));
        }
    }

    // Patient management methods
    public void addPatientFromInput(Scanner scanner) {
        System.out.println("\n--- Add New Patient ---");
        System.out.print("Enter patient ID: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        if (getPatientById(id) != null) {
            System.out.println("Error: Patient ID already exists.");
            return;
        }

        System.out.print("Enter full name: ");
        String name = scanner.nextLine();

        System.out.print("Enter address: ");
        String address = scanner.nextLine();

        System.out.print("Enter telephone number: ");
        String phone = scanner.nextLine();

        Patient patient = new Patient(id, name, address, phone);
        patients.add(patient);
        System.out.println("Patient added successfully!");
    }

    public void removePatientFromInput(Scanner scanner) {
        System.out.println("\n--- Remove Patient ---");
        System.out.print("Enter patient ID to remove: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        Patient patient = getPatientById(id);
        if (patient == null) {
            System.out.println("Error: Patient not found.");
            return;
        }

        // Check if patient has any appointments
        if (hasBookedAppointments(patient)) {
            System.out.println("Error: Patient has active appointments. Cancel them first.");
            return;
        }

        patients.remove(patient);
        System.out.println("Patient removed successfully!");
    }

    // Appointment booking methods
    public void bookAppointmentFromInput(Scanner scanner) {
        System.out.println("\n--- Book Appointment ---");
        System.out.print("Enter patient ID: ");
        int patientId = scanner.nextInt();
        scanner.nextLine();

        Patient patient = getPatientById(patientId);
        if (patient == null) {
            System.out.println("Error: Patient not found.");
            return;
        }

        System.out.println("Search by: 1) Expertise 2) Physiotherapist Name");
        System.out.print("Select option: ");
        int searchOption = scanner.nextInt();
        scanner.nextLine();

        List<Appointment> availableAppointments = new ArrayList<>();

        if (searchOption == 1) {
            System.out.print("Enter expertise: ");
            String expertise = scanner.nextLine();
            availableAppointments = getAvailableAppointmentsByExpertise(expertise);
        } else if (searchOption == 2) {
            System.out.print("Enter physiotherapist name: ");
            String name = scanner.nextLine();
            availableAppointments = getAvailableAppointmentsByPhysiotherapist(name);
        } else {
            System.out.println("Invalid option.");
            return;
        }

        if (availableAppointments.isEmpty()) {
            System.out.println("No available appointments found.");
            return;
        }

        System.out.println("\nAvailable Appointments:");
        for (int i = 0; i < availableAppointments.size(); i++) {
            Appointment appt = availableAppointments.get(i);
            System.out.printf("%d. %s - %s (%s) with %s\n",
                    i+1,
                    appt.getStartTime().format(DateTimeFormatter.ofPattern("EEE dd MMM yyyy, HH:mm")),
                    appt.getTreatmentName(),
                    appt.getPhysiotherapist().getExpertiseString(),
                    appt.getPhysiotherapist().getName());
        }

        System.out.print("Select appointment to book: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice < 1 || choice > availableAppointments.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        Appointment selected = availableAppointments.get(choice-1);

        // Check for time conflicts
        if (hasTimeConflict(patient, selected.getStartTime(), selected.getEndTime())) {
            System.out.println("Error: Patient already has an appointment at this time.");
            return;
        }

        selected.bookAppointment(patient);
        System.out.println("Appointment booked successfully! Booking ID: " + selected.getBookingId());
    }

    // Report generation
    public void generateReport() {
        System.out.println("\n=== Boost Physio Clinic Report ===");
        System.out.println("=== Appointment Summary ===");

        // Group appointments by physiotherapist
        Map<Physiotherapist, List<Appointment>> appointmentsByPhysio = new HashMap<>();
        for (Appointment appt : appointments) {
            appointmentsByPhysio.computeIfAbsent(appt.getPhysiotherapist(), k -> new ArrayList<>()).add(appt);
        }

        // Print appointments for each physiotherapist
        for (Physiotherapist physio : appointmentsByPhysio.keySet()) {
            System.out.println("\nPhysiotherapist: " + physio.getName());
            System.out.println("Expertise: " + physio.getExpertiseString());

            List<Appointment> physioAppts = appointmentsByPhysio.get(physio);
            for (Appointment appt : physioAppts) {
                String patientName = appt.getPatient() != null ? appt.getPatient().getName() : "None";
                System.out.printf("- %s: %s (%s) - Status: %s\n",
                        appt.getStartTime().format(DateTimeFormatter.ofPattern("EEE dd MMM yyyy, HH:mm")),
                        appt.getTreatmentName(),
                        patientName,
                        appt.getStatus());
            }
        }

        // Print physiotherapist ranking by attended appointments
        System.out.println("\n=== Physiotherapist Ranking by Attended Appointments ===");
        physiotherapists.stream()
                .sorted((p1, p2) -> Integer.compare(
                        countAttendedAppointments(p2),
                        countAttendedAppointments(p1)))
                .forEach(physio -> System.out.printf("%s: %d attended appointments\n",
                        physio.getName(),
                        countAttendedAppointments(physio)));
    }

    // Helper methods
    public Patient getPatientById(int id) {
        return patients.stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElse(null);
    }

    private boolean hasBookedAppointments(Patient patient) {
        return appointments.stream()
                .anyMatch(a -> a.getPatient() != null && a.getPatient().equals(patient)
                        && (a.getStatus() == AppointmentStatus.BOOKED || a.getStatus() == AppointmentStatus.ATTENDED));
    }

    private List<Appointment> getAvailableAppointmentsByExpertise(String expertise) {
        return appointments.stream()
                .filter(a -> a.getStatus() == AppointmentStatus.AVAILABLE)
                .filter(a -> a.getPhysiotherapist().hasExpertise(expertise))
                .toList();
    }

    private List<Appointment> getAvailableAppointmentsByPhysiotherapist(String name) {
        return appointments.stream()
                .filter(a -> a.getStatus() == AppointmentStatus.AVAILABLE)
                .filter(a -> a.getPhysiotherapist().getName().equalsIgnoreCase(name))
                .toList();
    }

    public boolean hasTimeConflict(Patient patient, LocalDateTime start, LocalDateTime end) {
        return appointments.stream()
                .anyMatch(a -> a.getPatient() != null && a.getPatient().equals(patient)
                        && a.getStatus() != AppointmentStatus.CANCELLED
                        && a.getStartTime().isBefore(end)
                        && a.getEndTime().isAfter(start));
    }

    private int countAttendedAppointments(Physiotherapist physio) {
        return (int) appointments.stream()
                .filter(a -> a.getPhysiotherapist().equals(physio))
                .filter(a -> a.getStatus() == AppointmentStatus.ATTENDED)
                .count();
    }

    public List<Patient> getPatients() {
        return patients;
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }

    public List<Physiotherapist> getPhysiotherapists() {
        return physiotherapists;
    }

    public void changeOrCancelAppointmentFromInput(Scanner scanner) {
        System.out.print("Enter booking ID to modify: ");
        int bookingId = scanner.nextInt();
        scanner.nextLine(); // consume newline

        Appointment appointment = null;
        for (Appointment appt : appointments) {
            if (appt.getBookingId() == bookingId) {
                appointment = appt;
                break;
            }
        }

        if (appointment == null) {
            System.out.println("No appointment found with ID " + bookingId);
            return;
        }

        System.out.println("1. Cancel Appointment");
        System.out.println("2. Reschedule Appointment");
        System.out.print("Choose option: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice == 1) {
            appointment.cancelAppointment();
            System.out.println("Appointment cancelled.");
        } else if (choice == 2) {
            System.out.print("Enter new start time (yyyy-MM-ddTHH:mm): ");
            String startInput = scanner.nextLine();
            System.out.print("Enter new end time (yyyy-MM-ddTHH:mm): ");
            String endInput = scanner.nextLine();
            try {
                LocalDateTime newStart = LocalDateTime.parse(startInput);
                LocalDateTime newEnd = LocalDateTime.parse(endInput);

                if (hasTimeConflict(appointment.getPatient(), newStart, newEnd)) {
                    System.out.println("Conflict detected with existing appointments. Try a different time.");
                } else {
                    appointment.cancelAppointment(); // Optional: mark old one cancelled
                    Appointment newAppt = new Appointment(
                            appointments.size() + 1, newStart, newEnd,
                            appointment.getTreatmentName(), appointment.getPhysiotherapist(),
                            appointment.getPatient(), AppointmentStatus.BOOKED
                    );
                    appointments.add(newAppt);
                    System.out.println("Appointment rescheduled.");
                }
            } catch (Exception e) {
                System.out.println("Invalid date format.");
            }
        } else {
            System.out.println("Invalid choice.");
        }
    }

    public void attendAppointmentFromInput(Scanner scanner) {
        System.out.print("Enter booking ID to mark as attended: ");
        int bookingId = scanner.nextInt();
        scanner.nextLine(); // consume newline

        Appointment appointment = null;
        for (Appointment appt : appointments) {
            if (appt.getBookingId() == bookingId) {
                appointment = appt;
                break;
            }
        }

        if (appointment == null) {
            System.out.println("No appointment found with ID " + bookingId);
            return;
        }

        if (appointment.getStatus() == AppointmentStatus.BOOKED) {
            appointment.attendAppointment();
            System.out.println("Appointment marked as attended.");
        } else {
            System.out.println("Only booked appointments can be marked as attended.");
        }
    }








}