import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

enum AppointmentStatus {
    AVAILABLE, BOOKED, CANCELLED, ATTENDED
}

// Person base class
abstract class Person {
    private int id;
    private String name;
    private String address;
    private String telephone;

    public Person(int id, String name, String address, String telephone) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.telephone = telephone;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getTelephone() { return telephone; }
}

// Patient class
class Patient extends Person {
    public Patient(int id, String name, String address, String telephone) {
        super(id, name, address, telephone);
    }
}

// Physiotherapist class
class Physiotherapist extends Person {
    private List<String> expertiseAreas = new ArrayList<>();

    public Physiotherapist(int id, String name, String address, String telephone) {
        super(id, name, address, telephone);
    }

    public void addExpertise(String expertise) {
        if (!expertiseAreas.contains(expertise)) {
            expertiseAreas.add(expertise);
        }
    }

    public boolean hasExpertise(String expertise) {
        return expertiseAreas.contains(expertise);
    }

    public String getExpertiseString() {
        return String.join(", ", expertiseAreas);
    }
}

// Appointment class
class Appointment {
    private int bookingId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String treatmentName;
    private Physiotherapist physiotherapist;
    private Patient patient;
    private AppointmentStatus status;

    public Appointment(int bookingId, LocalDateTime startTime, LocalDateTime endTime,
                       String treatmentName, Physiotherapist physiotherapist,
                       Patient patient, AppointmentStatus status) {
        this.bookingId = bookingId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.treatmentName = treatmentName;
        this.physiotherapist = physiotherapist;
        this.patient = patient;
        this.status = status;
    }

    // Getters
    public int getBookingId() { return bookingId; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public String getTreatmentName() { return treatmentName; }
    public Physiotherapist getPhysiotherapist() { return physiotherapist; }
    public Patient getPatient() { return patient; }
    public AppointmentStatus getStatus() { return status; }

    // Business methods
    public void bookAppointment(Patient patient) {
        this.patient = patient;
        this.status = AppointmentStatus.BOOKED;
    }

    public void cancelAppointment() {
        this.status = AppointmentStatus.CANCELLED;
        this.patient = null;
    }

    public void attendAppointment() {
        if (this.status == AppointmentStatus.BOOKED) {
            this.status = AppointmentStatus.ATTENDED;
        }
    }
}