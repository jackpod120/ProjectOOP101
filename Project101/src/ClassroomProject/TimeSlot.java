package ClassroomProject;

import java.time.DayOfWeek;
import java.time.LocalTime;

public class TimeSlot {
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;

    public TimeSlot(DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {
        if (startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("Start time must be before end time.");
        }
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public DayOfWeek getDayOfWeek() { return dayOfWeek; }
    public LocalTime getStartTime() { return startTime; }
    public LocalTime getEndTime() { return endTime; }

    // เมธอดสำหรับเช็คว่าเวลาทับซ้อนกันหรือไม่
    public boolean overlapsWith(TimeSlot other) {
        if (this.dayOfWeek != other.dayOfWeek) {
            return false; // คนละวัน ไม่ทับซ้อนแน่นอน
        }
        // เวลาจะทับซ้อนกันถ้า (StartA < EndB) และ (StartB < EndA)
        return this.startTime.isBefore(other.endTime) && other.startTime.isBefore(this.endTime);
    }

    @Override
    public String toString() {
        return dayOfWeek + " (" + startTime + " - " + endTime + ")";
    }
}