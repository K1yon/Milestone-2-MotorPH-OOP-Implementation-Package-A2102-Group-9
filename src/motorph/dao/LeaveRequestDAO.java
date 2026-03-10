package motorph.dao;

import motorph.model.LeaveRequest;
import motorph.util.FilePathResolver;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class LeaveRequestDAO {

    private static final String FILE     = "leave_requests.csv";
    private static final String TEMP     = "leave_requests_tmp.csv";

    private final String filePath;

    public LeaveRequestDAO() {
        this.filePath = FilePathResolver.resolve(FILE);
    }

    public void save(LeaveRequest request) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filePath, true))) {
            pw.println(toCsv(request));
        }
    }

    public List<LeaveRequest> findAll() throws IOException {
        List<LeaveRequest> list = new ArrayList<>();
        File f = new File(filePath);
        if (!f.exists()) return list;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                LeaveRequest r = parseLine(line.trim());
                if (r != null) list.add(r);
            }
        }
        return list;
    }

    public void updateStatus(String requestId, String newStatus) throws IOException {
        List<LeaveRequest> all  = findAll();
        File               temp = new File(TEMP);

        try (PrintWriter pw = new PrintWriter(new FileWriter(temp))) {
            for (LeaveRequest r : all) {
                if (r.getRequestId().equals(requestId)) {
                    r.setStatus(newStatus);
                }
                pw.println(toCsv(r));
            }
        }

        File original = new File(filePath);
        original.delete();
        temp.renameTo(original);
    }

    private String toCsv(LeaveRequest r) {

        String desc = r.getDescription().replace(",", ";");
        return r.getRequestId() + "," + r.getEmployeeId() + ","
             + r.getLeaveType() + "," + desc + "," + r.getStatusString();
    }

    private LeaveRequest parseLine(String line) {
        if (line.isEmpty()) return null;
        String[] d = line.split(",", 5);
        if (d.length < 4) return null;
        if (d.length == 4) {
 
            return new LeaveRequest(d[0].trim(), d[1].trim(), d[2].trim(), d[3].trim());
        }
 
        String desc = d[3].trim().replace(";", ","); 
        return new LeaveRequest(d[0].trim(), d[1].trim(), d[2].trim(), desc, d[4].trim());
    }
}