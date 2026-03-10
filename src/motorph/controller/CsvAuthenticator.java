package motorph.controller;

import motorph.model.Role;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CsvAuthenticator implements AuthService {

    private final Map<String, String> credentials = new HashMap<>();
    private final Map<String, Role>   roles       = new HashMap<>();

    public CsvAuthenticator(String usersFilePath) throws IOException {
        loadCredentials(usersFilePath);
    }

    private void loadCredentials(String filePath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",", -1);
                if (parts.length >= 2) {
                    String empNum = parts[0].trim();
                    String pass   = parts[1].trim();
                    credentials.put(empNum, pass);

                    Role role = Role.EMPLOYEE;
                    if (parts.length >= 3 && !parts[2].trim().isEmpty()) {
                        try { role = Role.valueOf(parts[2].trim()); }
                        catch (IllegalArgumentException ignored) {}
                    }
                    roles.put(empNum, role);
                }
            }
        }
        System.out.println("[Auth] Loaded " + credentials.size() + " user credentials.");
    }

    @Override
    public boolean authenticate(String employeeNumber, String password) {
        String stored = credentials.get(employeeNumber);
        return stored != null && stored.equals(password);
    }

    @Override
    public boolean userExists(String employeeNumber) {
        return credentials.containsKey(employeeNumber);
    }

    public Role getRoleFor(String employeeNumber) {
        return roles.getOrDefault(employeeNumber, Role.EMPLOYEE);
    }

    public int getUserCount() { return credentials.size(); }
}