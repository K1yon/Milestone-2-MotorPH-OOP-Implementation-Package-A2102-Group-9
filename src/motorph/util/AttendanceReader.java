package motorph.util;

import motorph.model.WorkHours;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class AttendanceReader {

    private static final String CSV_FILE    = "attendance_record.csv";
    private static final String BACKUP_FILE = "attendance_record_backup.csv";
    private static final String HEADER      = "Employee #,Last Name,First Name,Date,Log In,Log Out";

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("H:mm");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    private final List<WorkHours> allAttendance = new ArrayList<>();
    private final String csvPath;
    private final String backupPath;

    public AttendanceReader() {
        this.csvPath    = FilePathResolver.resolve(CSV_FILE);
        this.backupPath = FilePathResolver.resolve(BACKUP_FILE);
        createBackupIfAbsent();
        load();
    }

    private void load() {
        allAttendance.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(csvPath))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                try {
                    String[] v = line.split(",", -1);
                    if (v.length < 5) continue;
                    String empNum = v[0].trim();
                    String date   = v[3].trim();
                    String logIn  = v[4].trim();
                    String logOut = v.length >= 6 ? v[5].trim() : "";
                    double hours  = calcHours(logIn, logOut);
                    WorkHours wh  = new WorkHours(date,
                        (int) Math.min(hours, 8),
                        (int) Math.max(hours - 8, 0),
                        empNum);
                    wh.setLogIn(logIn.isEmpty()  ? null : logIn);
                    wh.setLogOut(logOut.isEmpty() ? null : logOut);
                    allAttendance.add(wh);
                } catch (Exception ignored) {}
            }
        } catch (IOException e) {
            System.err.println("[Attendance] Cannot load: " + e.getMessage());
        }
    }

    public List<WorkHours> getAttendanceForMonth(String empNum, int month) {
        return allAttendance.stream()
            .filter(w -> w.getEmployeeNumber().equals(empNum)
                      && extractMonth(w.getDate()) == month)
            .collect(Collectors.toList());
    }

    public List<WorkHours> getAllForEmployee(String empNum) {
        return allAttendance.stream()
            .filter(w -> w.getEmployeeNumber().equals(empNum))
            .collect(Collectors.toList());
    }

    public String clockIn(String empNum, String lastName,
                          String firstName, String timeStr) {
        String today = LocalDate.now().format(DATE_FMT);
        List<String> lines = readAllLines();

        for (int i = 1; i < lines.size(); i++) {
            String[] p = lines.get(i).split(",", -1);
            if (p.length >= 4 && p[0].trim().equals(empNum)
                    && p[3].trim().equals(today))
                return "You have already clocked in today (" + today + ").";
        }

        String newRow = empNum + "," + lastName + "," + firstName + ","
                      + today + "," + timeStr + ",";
        try (PrintWriter pw = new PrintWriter(new FileWriter(csvPath, true))) {
            pw.println(newRow);
        } catch (IOException e) {
            return "Failed to save Time In: " + e.getMessage();
        }
        load();
        return null;
    }

    public String clockOut(String empNum, String timeStr) {
        String today = LocalDate.now().format(DATE_FMT);
        List<String> lines = readAllLines();

        boolean hasClockedIn  = false;
        boolean hasClockedOut = false;
        int     targetIndex   = -1;

        for (int i = 1; i < lines.size(); i++) {
            String[] p = lines.get(i).split(",", -1);
            if (p.length >= 4 && p[0].trim().equals(empNum)
                    && p[3].trim().equals(today)) {
                hasClockedIn = true;
                if (p.length >= 6 && !p[5].trim().isEmpty())
                    hasClockedOut = true;
                else
                    targetIndex = i;
            }
        }

        if (!hasClockedIn)   return "No clock-in record found for today (" + today + ").";
        if (hasClockedOut)   return "You have already clocked out today (" + today + ").";
        if (targetIndex < 0) return "Could not find today's open clock-in record.";

        String[] p  = lines.get(targetIndex).split(",", -1);
        String   ln = p.length >= 2 ? p[1].trim() : "";
        String   fn = p.length >= 3 ? p[2].trim() : "";
        String   li = p.length >= 5 ? p[4].trim() : "";

        lines.set(targetIndex,
            p[0].trim() + "," + ln + "," + fn + "," + today + "," + li + "," + timeStr);

        writeAllLines(lines);
        load();
        return null;
    }

    public String getTodayClockIn(String empNum) {
        String today = LocalDate.now().format(DATE_FMT);
        List<String> lines = readAllLines();
        for (int i = 1; i < lines.size(); i++) {
            String[] p = lines.get(i).split(",", -1);
            if (p.length >= 5 && p[0].trim().equals(empNum)
                    && p[3].trim().equals(today))
                return p[4].trim();
        }
        return null;
    }

    public String getTodayClockOut(String empNum) {
        String today = LocalDate.now().format(DATE_FMT);
        List<String> lines = readAllLines();
        for (int i = 1; i < lines.size(); i++) {
            String[] p = lines.get(i).split(",", -1);
            if (p.length >= 6 && p[0].trim().equals(empNum)
                    && p[3].trim().equals(today)
                    && !p[5].trim().isEmpty())
                return p[5].trim();
        }
        return null;
    }

    public String revertToBackup() {
        try {
            if (!Files.exists(Paths.get(backupPath)))
                return "Backup file not found: " + backupPath;
            Files.copy(Paths.get(backupPath), Paths.get(csvPath),
                       StandardCopyOption.REPLACE_EXISTING);
            load();
            return null;
        } catch (IOException e) {
            return "Revert failed: " + e.getMessage();
        }
    }

    private void createBackupIfAbsent() {
        try {
            Path src  = Paths.get(csvPath);
            Path dest = Paths.get(backupPath);
            if (!Files.exists(src))
                Files.write(src, Collections.singletonList(HEADER));
            if (!Files.exists(dest)) {
                Files.copy(src, dest);
                System.out.println("[Attendance] Backup created: " + backupPath);
            }
        } catch (IOException e) {
            System.err.println("[Attendance] Could not create backup: " + e.getMessage());
        }
    }

    private List<String> readAllLines() {
        try { return new ArrayList<>(Files.readAllLines(Paths.get(csvPath))); }
        catch (IOException e) { return new ArrayList<>(); }
    }

    private void writeAllLines(List<String> lines) {
        try { Files.write(Paths.get(csvPath), lines); }
        catch (IOException e) {
            System.err.println("[Attendance] Write failed: " + e.getMessage());
        }
    }

    private double calcHours(String logIn, String logOut) {
        try {
            if (logIn.isEmpty() || logOut.isEmpty()) return 0;
            long minutes = LocalTime.parse(logIn, TIME_FMT)
                .until(LocalTime.parse(logOut, TIME_FMT), ChronoUnit.MINUTES);
            return Math.max((minutes - 60) / 60.0, 0);
        } catch (Exception e) { return 0; }
    }

    private int extractMonth(String date) {
        try { return Integer.parseInt(date.split("/")[0]); }
        catch (Exception e) { return -1; }
    }
}