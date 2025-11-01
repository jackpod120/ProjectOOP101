package ClassroomProject;


import javax.swing.*;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.Month;

public class Main {
    public static void main(String[] args) {
        AuthSystem auth = new AuthSystem();
        ReservationSystem reservationSys = new ReservationSystem();
        // ✅ เรียก LoginUI แสดงตอนเริ่มโปรแกรม
        SwingUtilities.invokeLater(() -> {
            new LoginUI(auth, reservationSys).setVisible(true);
        });

        // --- 1. ส่วนของ Sign Up ---
        System.out.println("--- ระบบลงทะเบียน ---");
        auth.signUp("สมชาย ใจดี", "somchai.j@example.com", "T001", "pass1234");
        auth.signUp("สมหญิง เก่งมาก", "somying.k@example.com", "T002", "pass5678");
        auth.signUp("สมชาย ใจดี", "somchai.j@example.com", "T001", "pass1234"); // ลองสมัครซ้ำ
        System.out.println();

        // --- 2. ส่วนของ Sign In ---
        System.out.println("--- ระบบเข้าสู่ระบบ ---");
        Teacher teacherSomchai = auth.signIn("somchai.j@example.com", "pass1234"); // สำเร็จ
        Teacher invalidTeacher = auth.signIn("somchai.j@example.com", "wrongpass"); // ล้มเหลว
        Teacher teacherSomying = auth.signIn("somying.k@example.com", "pass5678"); // สำเร็จ
        System.out.println();

        // --- 3. การสร้างห้องเรียนและ TimeSlots ---
        Classroom room101 = reservationSys.findClassroomByName("Room 101");
        TimeSlot mondayMorning = new TimeSlot(DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(11, 0));
        TimeSlot mondayMorningOverlap = new TimeSlot(DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(12, 0));
        TimeSlot wednesdayAfternoon = new TimeSlot(DayOfWeek.WEDNESDAY, LocalTime.of(13, 0), LocalTime.of(15, 0));

        // --- 4. การจองห้องเรียน ---
        System.out.println("--- ระบบการจอง ---");
        if (teacherSomchai != null) {
            // อาจารย์สมชายจองวันจันทร์เช้าทั้งเดือนสิงหาคม
            System.out.println(teacherSomchai.getName() + " กำลังพยายามจอง...");
            reservationSys.makeReservation(teacherSomchai, room101, mondayMorning, ReservationType.MONTHLY, 2025, Month.AUGUST, 0,"Mathematics","123456789");
        }

        if (teacherSomying != null) {
            System.out.println("\n" + teacherSomying.getName() + " กำลังพยายามจองเวลาทับซ้อน...");
            reservationSys.makeReservation(teacherSomying, room101, mondayMorningOverlap, ReservationType.MONTHLY, 2025, Month.AUGUST, 0,"English","987654321");

            System.out.println("\n" + teacherSomying.getName() + " กำลังพยายามจองทั้งเทอม...");
            reservationSys.makeReservation(teacherSomying, room101, wednesdayAfternoon, ReservationType.TERM, 2025, Month.AUGUST, 0, "Science", "456789123");
        }

        // 5. แสดงตารางสอน
        System.out.println();
        room101.displaySchedule();
    }
}