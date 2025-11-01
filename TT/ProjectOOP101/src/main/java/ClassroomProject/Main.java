package ClassroomProject;


import javax.swing.*;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.Month;

public class Main {
    public static void main(String[] args) {
        ReservationSystem reservationSys = new ReservationSystem();
        AuthSystem auth = new AuthSystem(reservationSys);
        // ✅ เรียก LoginUI แสดงตอนเริ่มโปรแกรม
        SwingUtilities.invokeLater(() -> {
            new LoginUI(auth, reservationSys).setVisible(true);
        });
        Classroom room101 = reservationSys.findClassroomByName("Room 101");
        room101.displaySchedule();
    }
}