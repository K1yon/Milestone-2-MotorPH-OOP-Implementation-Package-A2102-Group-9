package motorph.controller;

import motorph.model.Employee;
import motorph.model.Role;
import motorph.model.User;

public class LoginController {

    public static final String HR_MANAGER_ID = "10006";
    private static final int   MAX_ATTEMPTS  = 3;

    private final DbAuthenticator    authService;
    private final EmployeeController employeeController;
    private       int                attemptCount = 0;

    public LoginController() {
        this.authService        = new DbAuthenticator();
        this.employeeController = new EmployeeController();
    }

    public boolean handleLogin(String employeeNumber, String password) {
        if (isLocked()) return false;
        attemptCount++;
        boolean ok = authService.authenticate(employeeNumber, password);
        if (ok) {
            Employee profile = employeeController.findByNumber(employeeNumber);
            Role role = deriveRole(profile);
            UserSession.getInstance().login(new User(employeeNumber, role, profile));
        }
        return ok;
    }

    private Role deriveRole(Employee profile) {
        if (profile == null) return Role.EMPLOYEE;
        String pos = profile.getPosition();
        if (pos == null) return Role.EMPLOYEE;
        String p = pos.toLowerCase();

        if (p.contains("chief executive") || p.contains("chief operating"))
            return Role.ADMIN;
        if (p.contains("chief finance") || p.contains("payroll")
                || p.contains("accounting"))
            return Role.FINANCE;
        if (p.contains("it operations"))
            return Role.IT;
        if (p.contains("hr") || p.contains("human resource"))
            return Role.HR_ADMIN;

        return Role.EMPLOYEE;
    }

    public void    resetAttempts()      { attemptCount = 0; }
    public boolean isLocked()           { return attemptCount >= MAX_ATTEMPTS; }
    public int     getAttemptCount()    { return attemptCount; }
    public int     getMaxAttempts()     { return MAX_ATTEMPTS; }
    public EmployeeController getEmployeeController() { return employeeController; }
}