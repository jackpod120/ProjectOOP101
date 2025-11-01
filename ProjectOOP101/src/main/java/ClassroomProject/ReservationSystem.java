package ClassroomProject;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

public class ReservationSystem {
    private List<Classroom> classrooms = new ArrayList<>();

    // This is from your new 'Main.java' file. I am adding it here
    // so the ReservationSystem is prepopulated when created.
    public ReservationSystem() {
        this.classrooms.add(new Classroom("Room 101"));
        this.classrooms.add(new Classroom("Room 102"));
        this.classrooms.add(new Classroom("Room 205"));
        this.classrooms.add(new Classroom("Computer Lab"));
    }

    public void addClassroom(Classroom classroom) {
        this.classrooms.add(classroom);
    }

    public Classroom findClassroomByName(String name) {
        for(Classroom room : classrooms){
            if(room.getName().equals(name)){
                return room;
            }
        }
        return null;
    }
    public List<Classroom> getClassrooms() {
        return classrooms;
    }

    public boolean makeReservation(Teacher teacher, Classroom classroom, TimeSlot timeSlot, ReservationType type, int year, Month month, int day, String course, String code) {
        List<LocalDate> datesToBook = new ArrayList<>();
        DayOfWeek selectedDayOfWeek = timeSlot.getDayOfWeek();

        switch (type) {

            case DAILY:
                try {
                    // Use the specific date from the UI
                    LocalDate specificDate = LocalDate.of(year, month, day);

                    // Check: Does the selected date (e.g., 8th) match the DayOfWeek (e.g., SATURDAY)?
                    if (specificDate.getDayOfWeek() != selectedDayOfWeek) {
                        System.err.println("❌ Error: The selected date " + specificDate + " is not a " + selectedDayOfWeek);
                        return false;
                    }
                    datesToBook.add(specificDate);
                } catch (Exception e) {
                    // This catches invalid dates, e.g., "February 30th"
                    System.err.println("❌ Error: Invalid date created: " + year + "-" + month + "-" + day);
                    return false;
                }
                break;

            case MONTHLY:
                YearMonth yearMonth = YearMonth.of(year, month);
                LocalDate dayInMonth;

                if (day > 0) {
                    // User selected a specific start date (e.g., "Start on the 8th")
                    dayInMonth = LocalDate.of(year, month, day);
                } else {
                    // User selected "All" (day == 0)
                    dayInMonth = yearMonth.atDay(1).with(TemporalAdjusters.firstInMonth(selectedDayOfWeek));
                }

                while (dayInMonth.getMonth() == month) {
                    datesToBook.add(dayInMonth);
                    dayInMonth = dayInMonth.with(TemporalAdjusters.next(selectedDayOfWeek));
                }
                break;

            case TERM:
                YearMonth startYearMonth = YearMonth.of(year, month);

                for (int i = 0; i < 4; i++) {
                    YearMonth termYearMonth = startYearMonth.plusMonths(i);
                    Month currentMonth = termYearMonth.getMonth();

                    LocalDate termDay;
                    if (i == 0 && day > 0) {
                        termDay = LocalDate.of(year, month, day);
                    } else {
                        termDay = termYearMonth.atDay(1).with(TemporalAdjusters.firstInMonth(selectedDayOfWeek));
                    }

                    while (termDay.getMonth() == currentMonth) {
                        datesToBook.add(termDay);
                        termDay = termDay.with(TemporalAdjusters.next(selectedDayOfWeek));
                    }
                }
                break;
        }

        for (LocalDate date : datesToBook) {
            if (!classroom.isAvailable(date, timeSlot)) {
                System.out.println("❌ การจองล้มเหลว: วันที่ " + date + " เวลา " + timeSlot + " ในห้อง " + classroom.getName() + " ไม่ว่าง");
                return false;
            }
        }

        for (LocalDate date : datesToBook) {
            classroom.addBooking(new Booking(teacher, date, timeSlot, course, code));
        }

        System.out.println("✅ การจองแบบ " + type + " สำเร็จ! จำนวน " + datesToBook.size() + " ครั้ง ในห้อง " + classroom.getName());
        return true;
    }
}