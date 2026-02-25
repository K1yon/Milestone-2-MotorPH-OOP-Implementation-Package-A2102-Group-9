package motorph.view;

import motorph.controller.EmployeeController;
import motorph.model.Employee;
import motorph.ui.AppTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class NewEmployeeFrame extends JFrame {

    private static final String[] LABELS = {
        "Employee Number*", "Last Name*", "First Name*", "Birthday",
        "Address", "Phone Number", "SSS Number", "PhilHealth Number",
        "TIN", "Pag-IBIG Number", "Status*", "Position*",
        "Immediate Supervisor", "Basic Salary*", "Rice Subsidy",
        "Phone Allowance", "Clothing Allowance",
        "Gross Semi-monthly Rate*", "Hourly Rate"
    };

    private final HRDashboardFrame  parentFrame;
    private final EmployeeController controller;
    private final JTextField[]       fields = new JTextField[LABELS.length];

    public NewEmployeeFrame(HRDashboardFrame parentFrame) {
        this.parentFrame = parentFrame;
        this.controller  = parentFrame.getEmployeeController();

        setTitle("New Employee");
        setSize(520, 680);
        setLocationRelativeTo(parentFrame);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        buildUI();
        setVisible(true);
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(AppTheme.CONTENT_BG);
        root.add(AppTheme.pageHeader("Add New Employee",
                "Fields marked * are required"), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(LABELS.length, 2, 8, 6));
        form.setBackground(AppTheme.CARD_WHITE);
        form.setBorder(new EmptyBorder(20, 24, 20, 24));

        for (int i = 0; i < LABELS.length; i++) {
            JLabel label = AppTheme.boldLabel(LABELS[i] + ":");
            fields[i] = new JTextField();
            fields[i].setFont(AppTheme.FONT_BODY);
            form.add(label);
            form.add(fields[i]);
        }

        JScrollPane scroll = new JScrollPane(form);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        root.add(scroll, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        footer.setBackground(AppTheme.CARD_WHITE);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, AppTheme.BORDER_LIGHT));

        JButton cancelBtn = AppTheme.actionButton("Cancel", new Color(110, 110, 120));
        JButton addBtn    = AppTheme.primaryButton("Add Employee");

        cancelBtn.addActionListener(e -> dispose());
        addBtn.addActionListener(e    -> submit());

        footer.add(cancelBtn);
        footer.add(addBtn);
        root.add(footer, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private void submit() {
        // Required field indices: 0(empNum), 1(last), 2(first), 10(status), 11(position), 13(salary)
        int[] required = {0, 1, 2, 10, 11, 13};
        for (int idx : required) {
            if (fields[idx].getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Please fill all required fields (*).",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
                fields[idx].requestFocus();
                return;
            }
        }

        try {
            Employee emp = new Employee(
                text(0), text(1), text(2), text(3), text(4),
                text(5), text(6), text(7), text(8), text(9),
                text(10), text(11), text(12),
                num(13), num(14), num(15), num(16), num(17), num(18)
            );
            controller.createEmployee(emp);
            parentFrame.refreshEmployeeList();
            JOptionPane.showMessageDialog(this, "Employee added successfully!");
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String text(int i) { return fields[i].getText().trim(); }

    private double num(int i) {
        String v = text(i).replace(",", "");
        if (v.isEmpty()) return 0.0;
        try { return Double.parseDouble(v); }
        catch (NumberFormatException e) { return 0.0; }
    }
}