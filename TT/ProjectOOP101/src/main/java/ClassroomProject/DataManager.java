package ClassroomProject;

import java.io.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

public class DataManager {
    // ✅ ตรงนี้คือ path ถาวร ที่ชี้ไปยังโฟลเดอร์ data ในโปรเจกต์
    private static final String BASE_PATH = System.getProperty("user.dir") + File.separator + "data";
    private ReservationSystem reservationSystem;

    public List<Booking> loadBookings(Teacher teacher,ReservationSystem reservationSystem) {
        this.reservationSystem = reservationSystem;
        List<Booking> bookings = new ArrayList<>();
        File file = new File(BASE_PATH, teacher.getID() + ".csv");

        if (!file.exists()) {
            createEmptyCSV(file);
            System.out.println("📁 สร้างไฟล์ใหม่ให้ " + teacher.getID());
            return bookings;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("ReservationID")) continue;

                String[] parts = line.split(",");
                if (parts.length < 10) continue;
                String reservationID = parts[0];
                String room = parts[1];
                String course = parts[2];
                String code = parts[3];
                DayOfWeek day = DayOfWeek.valueOf(parts[4].toUpperCase());
                LocalTime start = LocalTime.parse(parts[5]);
                LocalTime end = LocalTime.parse(parts[6]);
                String type = parts[7];
                Month month = Month.valueOf(parts[8].toUpperCase());
                int year = Integer.parseInt(parts[9]);
                int dateNumber = Integer.parseInt(parts[10]);
                ReservationType reservationType = switch (type) {
                    case "DAILY" -> ReservationType.DAILY;
                    case "MONTHLY" -> ReservationType.MONTHLY;
                    case "TERM" -> ReservationType.TERM;
                    default -> null;
                };
                Classroom classroom = null;
                for(Classroom i : reservationSystem.getClassrooms()) {
                    if(i.getName().equals(room)) {
                        classroom = i;
                    }
                }
                TimeSlot slot = new TimeSlot(day, start, end);
                assert reservationType != null;
                reservationSystem.makeReservation(teacher,classroom,slot,reservationType,year,month,dateNumber,course,code);
            }

            System.out.println("📂 โหลดข้อมูล " + teacher.getID() + " สำเร็จ (" + reservationSystem.getBookings().size() + " รายการ)");
        } catch (IOException e) {
            System.err.println("❌ โหลดข้อมูลไม่สำเร็จ: " + e.getMessage());
        }

        return bookings;
    }

    // 🔴 FIX: แก้ไขเมธอด addBooking ให้บันทึกข้อมูลถูกต้อง
    public void addBooking(Teacher teacher, Booking booking) {
        try {
            String fileName = teacher.getID() + ".csv";
            File file = new File(BASE_PATH, fileName);
            file.getParentFile().mkdirs();

            try (FileWriter fw = new FileWriter(file, true)) {
                fw.write(String.join(",",
                        booking.getReservationID(),
                        booking.getRoom(),
                        booking.getCourse(),
                        booking.getCode(),
                        booking.getTimeSlot().getDayOfWeek().toString(), // ✅ FIX: บันทึกเฉพาะชื่อวัน (e.g., MONDAY)
                        booking.getTimeSlot().getStartTime().toString(), // ✅ FIX: บันทึกเวลาเริ่มต้น (e.g., 08:00)
                        booking.getTimeSlot().getEndTime().toString(),   // ✅ FIX: บันทึกเวลาสิ้นสุด (e.g., 08:30)
                        booking.getType().toString(), // ✅ FIX: type
                        booking.getDate().getMonth().toString(),         // ✅ FIX: ใช้เดือนจริง
                        String.valueOf(booking.getDate().getYear()),      // ✅ FIX: ใช้ปีจริง
                        String.valueOf(booking.getDate().getDayOfMonth())
                ) + "\n");
            }
            System.out.println("✅ Booking saved to " + file.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("❌ Error saving booking: " + e.getMessage());
        }
    }

    // ✅ สร้างไฟล์ใหม่ (มี header) - Header นี้ถูกต้องแล้ว
    private void createEmptyCSV(File file) {
        try {
            file.getParentFile().mkdirs();
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                bw.write("ReservationID,Room,Course,Code,Day,StartTime,EndTime,Type,Month,Year,Date");
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("❌ ไม่สามารถสร้างไฟล์ใหม่ได้: " + e.getMessage());
        }
    }

    public void deleteBooking(Teacher teacher, String reservationIDToDelete) {

        List<Booking> existingBookings = loadBookings(teacher,reservationSystem);

        existingBookings.removeIf(booking -> booking.getReservationID().equals(reservationIDToDelete));

            clearBookings(teacher);

            File file = new File(BASE_PATH, teacher.getID() + ".csv");

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) { // ใช้ 'true' สำหรับ append
                for (Booking booking : existingBookings) {

                    bw.write(String.join(",",
                            booking.getReservationID(),
                            booking.getRoom(),
                            booking.getCourse(),
                            booking.getCode(),
                            booking.getDate().getDayOfWeek().toString(),
                            booking.getTimeSlot().getStartTime().toString(),
                            booking.getTimeSlot().getEndTime().toString(),
                            booking.getType().toString(),
                            booking.getDate().getMonth().toString(),
                            String.valueOf(booking.getDate().getYear()),
                            String.valueOf(booking.getDate().getDayOfMonth())
                    ) + "\n");
                }
            } catch (IOException e) {
                System.err.println("❌ Error re-saving bookings after deletion: " + e.getMessage());
            }
            System.out.println("✅ ลบการจอง ID " + reservationIDToDelete + " และบันทึกข้อมูลที่เหลือสำเร็จ");

        }

    // ✅ ฟังก์ชันล้างข้อมูลทั้งหมด
    public void clearBookings(Teacher teacher) {
        File file = new File(BASE_PATH, teacher.getID() + ".csv");
        if (file.exists()) {
            file.delete();
            System.out.println("🗑️ ลบข้อมูลทั้งหมดของ " + teacher.getID());
        }
        createEmptyCSV(file);
    }
}