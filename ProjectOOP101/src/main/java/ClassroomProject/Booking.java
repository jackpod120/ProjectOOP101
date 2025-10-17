package ClassroomProject;

import java.time.LocalDate;

public class Booking {
    private Teacher teacher;
    private String course;
    private String code;
    private LocalDate date;
    private TimeSlot timeSlot;

    public Booking(Teacher teacher, LocalDate date, TimeSlot timeSlot, String course, String code) {
        this.teacher = teacher;
        this.date = date;
        this.timeSlot = timeSlot;
        this.course = course;
        this.code = code;
    }

    public Teacher getTeacher() { return teacher; }
    public LocalDate getDate() { return date; }
    public TimeSlot getTimeSlot() { return timeSlot; }

    @Override
    public String toString() {
        return "Booking on " + date + " for " + timeSlot + " by " + teacher.getName();
    }
}