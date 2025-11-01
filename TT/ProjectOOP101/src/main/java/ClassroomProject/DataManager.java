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

    // โหลดข้อมูลของอาจารย์จากไฟล์ CSV (โค้ดนี้ถูกต้องแล้ว ถ้า addBooking ถูก)
    public List<Booking> loadBookings(Teacher teacher) {
        List<Booking> bookings = new ArrayList<>();
        File file = new File(BASE_PATH, teacher.getID() + ".csv");

        // 🔹 ถ้าไม่มีไฟล์ -> สร้างใหม่พร้อม header แต่ไม่โหลดอะไร
        if (!file.exists()) {
            createEmptyCSV(file);
            System.out.println("📁 สร้างไฟล์ใหม่ให้ " + teacher.getID());
            return bookings; // กลับ list ว่าง
        }

        // 🔹 ถ้ามีไฟล์ -> โหลดข้อมูลทั้งหมด
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("ReservationID")) continue; // skip header

                String[] parts = line.split(",");
                if (parts.length < 10) continue;

                // ReservationID,Room,Course,Code,Day,StartTime,EndTime,Type,Month,Year
                String reservationID = parts[0];
                String room = parts[1];
                String course = parts[2];
                String code = parts[3];
                // FIX: การอ่าน DayOfWeek, StartTime, EndTime ถูกต้องแล้ว ตราบใดที่การบันทึกถูก
                DayOfWeek day = DayOfWeek.valueOf(parts[4].toUpperCase());
                LocalTime start = LocalTime.parse(parts[5]);
                LocalTime end = LocalTime.parse(parts[6]);
                String type = parts[7];
                Month month = Month.valueOf(parts[8].toUpperCase());
                int year = Integer.parseInt(parts[9]);

                TimeSlot slot = new TimeSlot(day, start, end);
                LocalDate date = LocalDate.of(year, month, 15);

                bookings.add(new Booking(teacher, date, slot, course, code));
            }
            System.out.println("📂 โหลดข้อมูล " + teacher.getID() + " สำเร็จ (" + bookings.size() + " รายการ)");
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
                        "NORMAL", // ใช้ค่า Type ชั่วคราว
                        booking.getDate().getMonth().toString(),         // ✅ FIX: ใช้เดือนจริง
                        String.valueOf(booking.getDate().getYear())      // ✅ FIX: ใช้ปีจริง
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
                bw.write("ReservationID,Room,Course,Code,Day,StartTime,EndTime,Type,Month,Year");
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("❌ ไม่สามารถสร้างไฟล์ใหม่ได้: " + e.getMessage());
        }
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