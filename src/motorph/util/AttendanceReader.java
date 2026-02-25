package motorph.util;

import motorph.model.WorkHours;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AttendanceReader {

    private static final String           CSV_FILE = "attendance_record.csv";
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("H:mm");
    private final List<WorkHours> allAttendance = new ArrayList<>();

    public AttendanceReader() { load(); }

    private void load() {
        String path = FilePathResolver.resolve(CSV_FILE);
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            boolean first = true; String line; int loaded = 0;
            while ((line = br.readLine()) != null) {
                if (first) { first = false; continue; }
                try {
                    String[] v = line.split(",");
                    if (v.length < 6) continue;
                    double hours   = calcHours(v[4].trim(), v[5].trim());
                    int    regular = (int) Math.min(hours, 8);
                    int    ot      = (int) Math.max(hours - 8, 0);
                    allAttendance.add(new WorkHours(v[3].trim(), regular, ot, v[0].trim()));
                    loaded++;
                } catch (Exception ignore) {}
            }
            System.out.println("[Attendance] Loaded " + loaded + " records.");
        } catch (IOException e) {
            System.err.println("[Attendance] Cannot load: " + e.getMessage());
        }
    }

    private double calcHours(String logIn, String logOut) {
        try {
            long minutes = LocalTime.parse(logIn, TIME_FMT)
                                    .until(LocalTime.parse(logOut, TIME_FMT), ChronoUnit.MINUTES);
            return Math.max((minutes - 60) / 60.0, 0);
        } catch (Exception e) { return 0; }
    }

    public List<WorkHours> getAttendanceForMonth(String empNum, int month) {
        return allAttendance.stream()
                .filter(wh -> wh.getEmployeeNumber().equals(empNum) && extractMonth(wh.getDate()) == month)
                .collect(Collectors.toList());
    }

    public List<WorkHours> getAllForEmployee(String empNum) {
        return allAttendance.stream()
                .filter(wh -> wh.getEmployeeNumber().equals(empNum))
                .collect(Collectors.toList());
    }

    private int extractMonth(String date) {
        try { return Integer.parseInt(date.split("/")[0]); }
        catch (Exception e) { return -1; }
    }
}