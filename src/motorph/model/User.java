package motorph.model;
public class User {

    private final String   employeeNumber;
    private final Role     role;
    private final Employee employeeProfile;

    public User(String employeeNumber, Role role, Employee employeeProfile) {
        this.employeeNumber  = employeeNumber;
        this.role            = role;
        this.employeeProfile = employeeProfile;
    }

    public boolean isHRAdmin()           { return role == Role.HR_ADMIN; }
    public boolean isRegularEmployee()   { return role == Role.EMPLOYEE; }
    public boolean canApproveLeaves()    { return isHRAdmin(); }
    public boolean canManageEmployees()  { return isHRAdmin(); }

    public String   getEmployeeNumber()  { return employeeNumber; }
    public Role     getRole()            { return role; }
    public Employee getEmployeeProfile() { return employeeProfile; }

    public String getDisplayName() {
        return employeeProfile != null ? employeeProfile.getFullName() : employeeNumber;
    }

    @Override
    public String toString() {
        return String.format("User[%s] %s (%s)", employeeNumber, getDisplayName(), role.getDisplayName());
    }
}