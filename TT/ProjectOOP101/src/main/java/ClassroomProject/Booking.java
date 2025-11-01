package ClassroomProject;

import java.time.LocalDate;

public class Booking {
    private Teacher teacher;
    private String course;
    private String code;
    private LocalDate date;
    private TimeSlot timeSlot;
    private String reservationID;  // 🔹 รหัสการจองเฉพาะแต่ละครั้ง
    private String room;

    public Booking(Teacher teacher, LocalDate date, TimeSlot timeSlot, String course, String code) {
        this.teacher = teacher;
        this.date = date;
        this.timeSlot = timeSlot;
        this.course = course;
        this.code = code;
    }
    public Booking(Teacher teacher, LocalDate date, TimeSlot timeSlot, String course, String code, String room) {
        this.teacher = teacher;
        this.date = date;
        this.timeSlot = timeSlot;
        this.course = course;
        this.code = code;
        this.room = room;
        this.reservationID = "R" + System.currentTimeMillis();
    }

    public Teacher getTeacher() { return teacher; }
    public LocalDate getDate() { return date; }
    public TimeSlot getTimeSlot() { return timeSlot; }
    public String getCourse() { return course; }
    public String getCode() { return code; }
    public String getReservationID() { return reservationID; }
    public String getRoom() { return room; }

    @Override
    public String toString() {
        return "Booking on " + date + " for " + timeSlot + " by " + teacher.getName();
    }
}