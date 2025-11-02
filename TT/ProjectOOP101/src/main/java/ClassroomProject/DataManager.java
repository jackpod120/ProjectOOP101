package ClassroomProject;

import java.io.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

public class DataManager {
    // ✅ path ชี้ไปยังโฟลเดอร์ data
    private static final String BASE_PATH = System.getProperty("user.dir") + File.separator + "data";
    private ReservationSystem reservationSystem;

    public DataManager(ReservationSystem reservationSystem) {
        this.reservationSystem = reservationSystem;
    }
    public DataManager() {

    }

    // 🟢 FIX: แก้ไข loadBookings ทั้งหมด
    public List<Booking> loadBookings(Teacher teacher, ReservationSystem reservationSystem) {
        this.reservationSystem = reservationSystem;
        List<Booking> bookings = new ArrayList<>(); // 1. 🟢 สร้าง List ที่จะ return
        File file = new File(BASE_PATH, teacher.getID() + ".csv");

        if (!file.exists()) {
            System.out.println("ℹ️ ไม่มีไฟล์ข้อมูลสำหรับ " + teacher.getID() + ", ข้ามการโหลด...");
            return bookings; // คืนค่า List ว่าง
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            br.readLine(); // ข้าม Header

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length < 11) continue;

                try {
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

                    ReservationType reservationType = ReservationType.valueOf(type);

                    // 2. 🟢 หา Classroom
                    Classroom classroom = reservationSystem.findClassroomByName(room);

                    if (classroom == null) {
                        System.err.println("❌ ไม่พบห้อง " + room + " ขณะโหลดข้อมูล, ข้ามรายการนี้");
                        continue;
                    }

                    // 3. 🟢 ตรวจสอบว่ามีใน memory แล้วหรือยัง
                    boolean exists = reservationSystem.getBookings().stream()
                            .anyMatch(b -> b.getReservationID().equals(reservationID));
                    if (exists) continue;

                    // 4. 🟢 สร้าง Object (ห้ามเรียก makeReservation!)
                    TimeSlot slot = new TimeSlot(day, start, end);
                    LocalDate date = LocalDate.of(year, month, dateNumber); // สร้าง LocalDate
                    Booking newBooking = new Booking(teacher, date, slot, course, code, room, reservationID, reservationType);

                    // 5. 🟢 เพิ่มเข้า Memory โดยตรง
                    classroom.addBooking(newBooking);
                    reservationSystem.addBookingInternal(newBooking); // ใช้เมธอดใหม่
                    bookings.add(newBooking); // เพิ่มเข้า List ที่จะ return

                } catch (Exception e) {
                    System.err.println("❌ เกิดข้อผิดพลาดในการอ่านบรรทัด: " + line + " - " + e.getMessage());
                }
            }

            System.out.println("📂 โหลดข้อมูล " + teacher.getID() + " สำเร็จ (" +
                    bookings.size() + " รายการ)");
        } catch (IOException e) {
            System.err.println("❌ โหลดข้อมูล " + teacher.getID() + " ไม่สำเร็จ: " + e.getMessage());
        }

        return bookings; // 6. 🟢 คืนค่า List ที่โหลดได้
    }

    // ✅ บันทึกการจองใหม่ลงไฟล์ CSV
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
                        booking.getTimeSlot().getDayOfWeek().toString(),
                        booking.getTimeSlot().getStartTime().toString(),
                        booking.getTimeSlot().getEndTime().toString(),
                        booking.getType().toString(),
                        booking.getDate().getMonth().toString(),
                        String.valueOf(booking.getDate().getYear()),
                        String.valueOf(booking.getDate().getDayOfMonth())
                ) + "\n");
            }
        } catch (IOException e) {
            System.err.println("❌ Error saving booking: " + e.getMessage());
        }
    }

    // ✅ สร้างไฟล์ใหม่ (พร้อม header)
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

    // ✅ ลบการจองด้วย ReservationID
    // 🟢 FIX: แก้ไข deleteBooking ทั้งหมด
    public void deleteBooking(Teacher teacher, String reservationIDToDelete) {
        // 1. 🟢 หา List การจองของครูคนนี้ "จากใน Memory"
        List<Booking> allTeacherBookings = new ArrayList<>();
        for (Booking b : reservationSystem.getBookings()) {
            if (b.getTeacher().equals(teacher)) {
                allTeacherBookings.add(b);
            }
        }

        // 2. 🟢 ลบรายการที่ต้องการออกจาก List ชั่วคราวนี้
        allTeacherBookings.removeIf(booking -> booking.getReservationID().equals(reservationIDToDelete));

        // 3. 🟢 ล้างไฟล์ CSV (สร้างไฟล์เปล่าทับ)
        File file = new File(BASE_PATH, teacher.getID() + ".csv");
        createEmptyCSV(file); // สร้างไฟล์ใหม่พร้อม Header

        // 4. 🟢 "เขียนใหม่" เฉพาะข้อมูลที่เหลืออยู่
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) { // true = append ต่อจาก header
            for (Booking booking : allTeacherBookings) {
                bw.write(String.join(",",
                        booking.getReservationID(),
                        booking.getRoom(),
                        booking.getCourse(),
                        booking.getCode(),
                        booking.getTimeSlot().getDayOfWeek().toString(),
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

    // ✅ ล้างข้อมูลทั้งหมดของอาจารย์ (เมธอดนี้ไม่ได้ใช้ใน delete แต่มีประโยชน์)
    public void clearBookings(Teacher teacher) {
        File file = new File(BASE_PATH, teacher.getID() + ".csv");
        if (file.exists()) {
            file.delete();
            System.out.println("🗑️ ลบข้อมูลทั้งหมดของ " + teacher.getID());
        }
        createEmptyCSV(file);
    }
}