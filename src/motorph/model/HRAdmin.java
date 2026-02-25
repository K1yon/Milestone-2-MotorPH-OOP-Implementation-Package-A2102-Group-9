package motorph.model;

public class HRAdmin {
    private String accessLevel;
    private String employeeNumber; 
    private String fullName;

    public HRAdmin(String employeeNumber, String fullName, String accessLevel) {
        this.employeeNumber = employeeNumber;
        this.fullName = fullName;
        this.accessLevel = accessLevel;
    }

    public boolean canManageEmployees() {
        return true;
    }

    public boolean canApproveLeaves() {
        return true;
    }

    public void manageEmployeeInfo() {

    }
    
    public Employee searchEmployee(String keyword) {
        return null; 
    }

    public String getAccessLevel() { return accessLevel; }
    public void setAccessLevel(String accessLevel) { this.accessLevel = accessLevel; }
    public String getEmployeeNumber() { return employeeNumber; }
    public String getFullName() { return fullName; }
    
    @Override
    public String toString() {
        return "HR Admin: " + fullName + " (" + employeeNumber + ") - " + accessLevel;
    }
}