package motorph.controller;

import motorph.model.Employee;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class EmployeeController {

    private final String         csvFilePath;
    private final List<Employee> employeeList = new ArrayList<>();

    public EmployeeController(String csvFilePath) {
        this.csvFilePath = csvFilePath;
        loadAll();
    }

    public List<Employee> loadAllEmployees() { loadAll(); return new ArrayList<>(employeeList); }

    public Employee findByNumber(String employeeNumber) {
        return employeeList.stream()
                .filter(e -> e.getEmployeeNumber().equals(employeeNumber))
                .findFirst().orElse(null);
    }

    public void createEmployee(Employee emp) { employeeList.add(emp); saveAll(); }

    public void updateEmployee(Employee updated) {
        for (int i = 0; i < employeeList.size(); i++) {
            if (employeeList.get(i).getEmployeeNumber().equals(updated.getEmployeeNumber())) {
                employeeList.set(i, updated); saveAll(); return;
            }
        }
    }

    public void deleteEmployee(String employeeNumber) {
        employeeList.removeIf(e -> e.getEmployeeNumber().equals(employeeNumber));
        saveAll();
    }

    private void loadAll() {
        employeeList.clear();
        try (CSVReader reader = new CSVReader(
                new InputStreamReader(new FileInputStream(csvFilePath), StandardCharsets.UTF_8))) {
            String[] row; boolean first = true;
            while ((row = reader.readNext()) != null) {
                if (first) { first = false; continue; }
                if (row.length < 19) continue;
                Employee emp = parseEmployee(row);
                if (emp != null) employeeList.add(emp);
            }
        } catch (Exception ex) {
            System.err.println("[EmployeeController] Load error: " + ex.getMessage());
        }
    }

    private Employee parseEmployee(String[] row) {
        try {
            return new Employee(
                row[0], row[1], row[2], row[3], row[4],
                row[5], row[6], row[7], row[8], row[9],
                row[10], row[11], row[12],
                num(row[13]), num(row[14]), num(row[15]),
                num(row[16]), num(row[17]), num(row[18])
            );
        } catch (Exception e) { return null; }
    }

    private void saveAll() {
        try (CSVWriter writer = new CSVWriter(new FileWriter(csvFilePath))) {
            writer.writeNext(new String[]{
                "Employee Number","Last Name","First Name","Birthday","Address",
                "Phone Number","SSS Number","PhilHealth Number","TIN",
                "Pag-IBIG Number","Status","Position","Immediate Supervisor",
                "Basic Salary","Rice Subsidy","Phone Allowance",
                "Clothing Allowance","Gross Semi-monthly Rate","Hourly Rate"
            });
            for (Employee e : employeeList) writer.writeNext(toRow(e));
        } catch (IOException ex) {
            System.err.println("[EmployeeController] Save error: " + ex.getMessage());
        }
    }

    private String[] toRow(Employee e) {
        return new String[]{
            e.getEmployeeNumber(), e.getLastName(), e.getFirstName(),
            e.getBirthday(), e.getAddress(), e.getPhoneNumber(),
            e.getSssNumber(), e.getPhilhealthNumber(), e.getTinNumber(),
            e.getPagibigNumber(), e.getStatus(), e.getPosition(),
            e.getImmediateSupervisor(),
            String.valueOf(e.getBasicSalary()), String.valueOf(e.getRiceSubsidy()),
            String.valueOf(e.getPhoneAllowance()), String.valueOf(e.getClothingAllowance()),
            String.valueOf(e.getGrossSemiMonthlyRate()), String.valueOf(e.getHourlyRate())
        };
    }

    private static double num(String val) {
        if (val == null || val.isBlank()) return 0.0;
        try { return Double.parseDouble(val.replace("\"","").replace(",","").trim()); }
        catch (NumberFormatException e) { return 0.0; }
    }
}