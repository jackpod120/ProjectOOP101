package ClassroomProject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Classroom {
    private String name;
    private String subject;
    private List<Booking> bookings; // เก็บการจองทั้งหมดของห้องนี้

    public Classroom(String name, String subject) {
        this.name = name;
        this.subject = subject;
        this.bookings = new ArrayList<>();
    }

    public String getName() { return name; }

    // เช็คว่าเวลาที่ต้องการจอง ว่างหรือไม่
    public boolean isAvailable(LocalDate date, TimeSlot timeSlot) {
        for (Booking existingBooking : bookings) {
            // เช็คว่าวันเดียวกันหรือไม่
            if (existingBooking.getDate().isEqual(date)) {
                // ถ้าวันเดียวกัน ให้เช็คว่าเวลาทับซ้อนกันหรือไม่
                if (existingBooking.getTimeSlot().overlapsWith(timeSlot)) {
                    return false; // ไม่ว่าง
                }
            }
        }
        return true; // ว่าง
    }

    public void addBooking(Booking newBooking) {
        this.bookings.add(newBooking);
    }

    public void displaySchedule() {
        System.out.println("--- ตารางสอนห้อง " + name + " ---");
        if (bookings.isEmpty()) {
            System.out.println("ไม่มีการจอง");
        } else {
            // เรียงลำดับการจองตามวันที่และเวลา
            bookings.stream()
                    .sorted((b1, b2) -> b1.getDate().isBefore(b2.getDate()) ? -1 : 1)
                    .forEach(System.out::println);
        }
        System.out.println("--------------------------");
    }
}