package ClassroomProject;

import java.time.LocalDate;

public class Booking {
    private Teacher teacher;
    private LocalDate date;
    private TimeSlot timeSlot;

    public Booking(Teacher teacher, LocalDate date, TimeSlot timeSlot) {
        this.teacher = teacher;
        this.date = date;
        this.timeSlot = timeSlot;
    }

    public Teacher getTeacher() { return teacher; }
    public LocalDate getDate() { return date; }
    public TimeSlot getTimeSlot() { return timeSlot; }

    @Override
    public String toString() {
        return "Booking on " + date + " for " + timeSlot + " by " + teacher.getName();
    }
}