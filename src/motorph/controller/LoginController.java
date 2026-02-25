package motorph.controller;

import motorph.model.Employee;
import motorph.model.Role;
import motorph.model.User;
import motorph.util.FilePathResolver;

import javax.swing.JOptionPane;
import java.io.IOException;

/**
 * OOP: Single Responsibility (login attempts only).
 * Dependency Inversion: depends on AuthService interface, not CsvAuthenticator.
 */
public class LoginController {

    public static final String HR_MANAGER_ID = "10006";
    private static final int   MAX_ATTEMPTS  = 3;

    private final AuthService        authService;
    private final EmployeeController employeeController;
    private       int                attemptCount = 0;

    public LoginController() {
        String usersPath  = FilePathResolver.resolve("users.csv");
        String empCsvPath = FilePathResolver.resolve("employee_data_full.csv");

        AuthService auth;
        try {
            auth = new CsvAuthenticator(usersPath);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                "Cannot load users.csv — place it in the application directory.\n" + e.getMessage(),
                "Critical Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
            auth = null;
        }
        this.authService        = auth;
        this.employeeController = new EmployeeController(empCsvPath);
    }

    public boolean handleLogin(String employeeNumber, String password) {
        if (isLocked()) return false;
        attemptCount++;
        boolean ok = authService.authenticate(employeeNumber, password);
        if (ok) {
            Employee profile = employeeController.findByNumber(employeeNumber);
            Role     role    = HR_MANAGER_ID.equals(employeeNumber) ? Role.HR_ADMIN : Role.EMPLOYEE;
            UserSession.getInstance().login(new User(employeeNumber, role, profile));
        }
        return ok;
    }

    public void resetAttempts()      { attemptCount = 0; }
    public boolean isLocked()        { return attemptCount >= MAX_ATTEMPTS; }
    public int     getAttemptCount() { return attemptCount; }
    public int     getMaxAttempts()  { return MAX_ATTEMPTS; }
    public EmployeeController getEmployeeController() { return employeeController; }
}