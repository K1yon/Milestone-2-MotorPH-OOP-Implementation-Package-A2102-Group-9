package motorph.util;

import motorph.model.WorkHours;
import motorph.model.WorkHoursEntity;
import motorph.repository.WorkHoursRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

public class AttendanceReader {

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("H:mm");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    private final WorkHoursRepository repo = new WorkHoursRepository();

    public AttendanceReader() {}

    public List<WorkHours> getAttendanceForMonth(String empNum, int month) {
        return repo.findByEmployeeAndMonth(empNum, month).stream()
                .map(WorkHoursEntity::toWorkHours)
                .collect(Collectors.toList());
    }

    public List<WorkHours> getAllForEmployee(String empNum) {
        return repo.findByEmployee(empNum).stream()
                .map(WorkHoursEntity::toWorkHours)
                .collect(Collectors.toList());
    }

    public String getTodayClockIn(String empNum) {
        String today = LocalDate.now().format(DATE_FMT);
        WorkHoursEntity whe = repo.findByEmployeeAndDate(empNum, today);
        return whe != null ? whe.getLogIn() : null;
    }

    public String getTodayClockOut(String empNum) {
        String today = LocalDate.now().format(DATE_FMT);
        WorkHoursEntity whe = repo.findByEmployeeAndDate(empNum, today);
        return (whe != null && whe.getLogOut() != null && !whe.getLogOut().isEmpty())
                ? whe.getLogOut() : null;
    }

    public String clockIn(String empNum, String lastName,
                          String firstName, String timeStr) {
        String today = LocalDate.now().format(DATE_FMT);
        if (repo.findByEmployeeAndDate(empNum, today) != null)
            return "You have already clocked in today (" + today + ").";
        WorkHoursEntity whe = new WorkHoursEntity(empNum, today, timeStr, null, 0, 0);
        repo.save(whe);
        return null;
    }

    public String clockOut(String empNum, String timeStr) {
        String today = LocalDate.now().format(DATE_FMT);
        WorkHoursEntity whe = repo.findByEmployeeAndDate(empNum, today);
        if (whe == null)
            return "No clock-in record found for today (" + today + ").";
        if (whe.getLogOut() != null && !whe.getLogOut().isEmpty())
            return "You have already clocked out today (" + today + ").";
        double hours = calcHours(whe.getLogIn(), timeStr);
        whe.setLogOut(timeStr);
        whe.setRegularHours((int) Math.min(hours, 8));
        whe.setOvertimeHours((int) Math.max(hours - 8, 0));
        repo.update(whe);
        return null;
    }

    public String revertToBackup() {
        return "Revert is not available after database migration.";
    }

    private double calcHours(String logIn, String logOut) {
        try {
            if (logIn == null || logOut == null || logIn.isEmpty() || logOut.isEmpty())
                return 0;
            long minutes = LocalTime.parse(logIn, TIME_FMT)
                .until(LocalTime.parse(logOut, TIME_FMT), ChronoUnit.MINUTES);
            return Math.max((minutes - 60) / 60.0, 0);
        } catch (Exception e) { return 0; }
    }
}