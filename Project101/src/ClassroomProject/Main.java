package ClassroomProject;


import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.Month;

public class Main {
    public static void main(String[] args) {
        AuthSystem auth = new AuthSystem();
        ReservationSystem reservationSys = new ReservationSystem();

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

        // --- 3. เตรียมห้องเรียนและช่วงเวลา ---
        Classroom room101 = new Classroom("101", "คณิตศาสตร์");
        reservationSys.addClassroom(room101);

        TimeSlot mondayMorning = new TimeSlot(DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(11, 0));
        TimeSlot mondayMorningOverlap = new TimeSlot(DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(12, 0));
        TimeSlot wednesdayAfternoon = new TimeSlot(DayOfWeek.WEDNESDAY, LocalTime.of(13, 0), LocalTime.of(15, 0));

        // --- 4. การจองห้องเรียน ---
        System.out.println("--- ระบบการจอง ---");
        if (teacherSomchai != null) {
            // อาจารย์สมชายจองวันจันทร์เช้าทั้งเดือนสิงหาคม
            System.out.println(teacherSomchai.getName() + " กำลังพยายามจอง...");
            reservationSys.makeReservation(teacherSomchai, room101, mondayMorning, ReservationType.MONTHLY, 2025, Month.AUGUST);
        }

        if (teacherSomying != null) {
            // อาจารย์สมหญิงพยายามจองวันจันทร์เช้า (เวลาทับซ้อน) ในเดือนสิงหาคม -> ควรล้มเหลว
            System.out.println("\n" + teacherSomying.getName() + " กำลังพยายามจองเวลาทับซ้อน...");
            reservationSys.makeReservation(teacherSomying, room101, mondayMorningOverlap, ReservationType.MONTHLY, 2025, Month.AUGUST);

            // อาจารย์สมหญิงจองวันพุธบ่ายทั้งเทอม (เริ่มเดือนสิงหาคม) -> ควรสำเร็จ
            System.out.println("\n" + teacherSomying.getName() + " กำลังพยายามจองใหม่...");
            reservationSys.makeReservation(teacherSomying, room101, wednesdayAfternoon, ReservationType.TERM, 2025, Month.AUGUST);
        }
        System.out.println();

        // --- 5. แสดงตารางสอนของห้อง ---
        room101.displaySchedule();
    }
}