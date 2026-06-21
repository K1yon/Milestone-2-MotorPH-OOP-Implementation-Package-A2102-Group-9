package motorph.service;

import motorph.model.LeaveRequest;
import motorph.model.LeaveRequest.LeaveStatus;
import motorph.model.LeaveRequestEntity;
import motorph.repository.LeaveRequestRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class LeaveService {

    private final LeaveRequestRepository repo = new LeaveRequestRepository();

    public void submitRequest(String employeeId, String leaveType,
                              String fromDate, String toDate,
                              String description) {
        String displayType = leaveType + " (" + fromDate + " to " + toDate + ")";
        LeaveRequest req = new LeaveRequest(
                UUID.randomUUID().toString(),
                employeeId,
                displayType,
                description,
                LeaveStatus.PENDING.name()
        );
        repo.save(LeaveRequestEntity.from(req));
    }

    public void submitRequest(String employeeId, String leaveType,
                              String fromDate, String toDate) {
        submitRequest(employeeId, leaveType, fromDate, toDate, "");
    }

    public List<LeaveRequest> getAllRequests() {
        return repo.findAll().stream()
                .map(LeaveRequestEntity::toLeaveRequest)
                .collect(Collectors.toList());
    }

    public List<LeaveRequest> getRequestsByEmployee(String employeeId) {
        return repo.findByEmployee(employeeId).stream()
                .map(LeaveRequestEntity::toLeaveRequest)
                .collect(Collectors.toList());
    }

    public List<LeaveRequest> getPendingRequests() {
        return repo.findAll().stream()
                .map(LeaveRequestEntity::toLeaveRequest)
                .filter(LeaveRequest::isPending)
                .collect(Collectors.toList());
    }

    public void updateStatus(String requestId, String status) {
        repo.updateStatus(requestId, LeaveStatus.fromString(status));
    }
}