package motorph.service;

import motorph.controller.LeaveRequestDAO;
import motorph.model.LeaveRequest;
import motorph.model.LeaveRequest.LeaveStatus;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class LeaveService {

    private final LeaveRequestDAO dao;

    public LeaveService() {
        this.dao = new LeaveRequestDAO();
    }

    public void submitRequest(String employeeId, String leaveType,
                              String fromDate, String toDate,
                              String description) throws IOException {
        String displayType = leaveType + " (" + fromDate + " to " + toDate + ")";
        LeaveRequest req   = new LeaveRequest(
                UUID.randomUUID().toString(),
                employeeId,
                displayType,
                description,
                LeaveStatus.PENDING.name()
        );
        dao.save(req);
    }

    public void submitRequest(String employeeId, String leaveType,
                              String fromDate, String toDate) throws IOException {
        submitRequest(employeeId, leaveType, fromDate, toDate, "");
    }

    public List<LeaveRequest> getAllRequests() throws IOException {
        return dao.findAll();
    }

    public List<LeaveRequest> getRequestsByEmployee(String employeeId) throws IOException {
        return dao.findAll().stream()
                .filter(r -> r.getEmployeeId().equals(employeeId))
                .collect(Collectors.toList());
    }

    public List<LeaveRequest> getPendingRequests() throws IOException {
        return dao.findAll().stream()
                .filter(LeaveRequest::isPending)
                .collect(Collectors.toList());
    }

    public void updateStatus(String requestId, String status) throws IOException {
        dao.updateStatus(requestId, status);
    }
}