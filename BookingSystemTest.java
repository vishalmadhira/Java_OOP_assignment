import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;

public class BookingSystemTest {
    private BookingSystem system;
    private Physiotherapist physio;
    private Patient patient;

    @BeforeEach
    void setUp() {
        system = new BookingSystem();

        // Set up a sample physiotherapist and patient
        physio = new Physiotherapist(1, "Dr. Test", "Clinic Road", "123-4567");
        physio.addExpertise("Physiotherapy");

        patient = new Patient(101, "Test Patient", "Patient Street", "987-6543");

        // Add them manually (assumes helper access or public list in BookingSystem)
        system.getPhysiotherapists().add(physio);
        system.getPatients().add(patient);

        // Add an available appointment
        Appointment appt = new Appointment(
                1,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(1).plusHours(1),
                "Physiotherapy",
                physio,
                null,
                AppointmentStatus.AVAILABLE
        );
        system.getAppointments().add(appt);
    }

    @Test
    void testAddAndRetrievePatient() {
        Patient fetched = system.getPatientById(101);
        assertNotNull(fetched);
        assertEquals("Test Patient", fetched.getName());
    }

    @Test
    void testBookAppointment() {
        Appointment appt = system.getAppointments().get(0);
        appt.bookAppointment(patient);

        assertEquals(AppointmentStatus.BOOKED, appt.getStatus());
        assertEquals(patient, appt.getPatient());
    }

    @Test
    void testCancelAppointment() {
        Appointment appt = system.getAppointments().get(0);
        appt.bookAppointment(patient);
        appt.cancelAppointment();

        assertEquals(AppointmentStatus.CANCELLED, appt.getStatus());
        assertNull(appt.getPatient());
    }

    @Test
    void testAttendAppointment() {
        Appointment appt = system.getAppointments().get(0);
        appt.bookAppointment(patient);
        appt.attendAppointment();

        assertEquals(AppointmentStatus.ATTENDED, appt.getStatus());
    }

    @Test
    void testTimeConflictDetection() {
        Appointment first = system.getAppointments().get(0);
        first.bookAppointment(patient);

        Appointment overlapping = new Appointment(
                2,
                first.getStartTime().plusMinutes(30),
                first.getEndTime().plusMinutes(30),
                "Massage",
                physio,
                null,
                AppointmentStatus.AVAILABLE
        );

        boolean conflict = system.hasTimeConflict(
                patient,
                overlapping.getStartTime(),
                overlapping.getEndTime()
        );

        assertTrue(conflict);
    }
}
