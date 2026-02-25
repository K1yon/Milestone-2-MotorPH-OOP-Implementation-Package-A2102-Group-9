package motorph.controller;

import java.io.*;
import java.util.*;

public class Authenticator implements AuthService {
    private Map<String, String> credentials = new HashMap<>();

    public Authenticator(String usersFilePath) throws IOException {
        loadCredentials(usersFilePath);
    }

    private void loadCredentials(String filePath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length >= 2) {
                    String empNum = parts[0].trim();
                    String password = parts[1].trim();
                    credentials.put(empNum, password);
                }
            }
        }
    }

    @Override
    public boolean authenticate(String employeeNumber, String password) {
        String storedPassword = credentials.get(employeeNumber);
        return storedPassword != null && password.equals(storedPassword);
    }

    public boolean userExists(String employeeNumber) {
        return credentials.containsKey(employeeNumber);
    }

    /**
     * Returns count of loaded users (for debugging/setup verification).
     */
    public int getUserCount() {
        return credentials.size();
    }
}