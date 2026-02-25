package motorph.controller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CsvAuthenticator implements AuthService {

    private final Map<String, String> credentials = new HashMap<>();

    public CsvAuthenticator(String usersFilePath) throws IOException {
        loadCredentials(usersFilePath);
    }

    private void loadCredentials(String filePath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",", -1);
                if (parts.length >= 2) {
                    credentials.put(parts[0].trim(), parts[1].trim());
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

    public int getUserCount() { return credentials.size(); }
}