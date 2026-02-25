package motorph.view;

import motorph.model.Employee;
import motorph.model.LeaveRequest;
import motorph.model.Payslip;
import motorph.service.LeaveService;
import motorph.ui.AppTheme;
import motorph.util.AttendanceReader;
import motorph.util.PayrollCalculator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;
import java.util.List;

public class EmployeeDetailsFrame extends JFrame {

    private final Employee         employee;
    private final AttendanceReader attendanceReader  = new AttendanceReader();
    private final PayrollCalculator payrollCalc      = new PayrollCalculator();
    private final LeaveService     leaveService      = new LeaveService();

    private JComboBox<String> monthCombo;
    private JTextArea         payrollArea;

    public EmployeeDetailsFrame(JFrame parent, Employee emp) {
        this.employee = emp;
        setTitle("Employee Details — " + emp.getFullName());
        setSize(680, 820);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        buildUI();
        notifyLeaveUpdates();
        setVisible(true);
    }

    private void buildUI() {
        JPanel main = new JPanel(new BorderLayout(0, 0));
        main.setBackground(AppTheme.CONTENT_BG);
        main.add(buildHeader(),  BorderLayout.NORTH);
        main.add(buildInfo(),    BorderLayout.CENTER);
        main.add(buildPayroll(), BorderLayout.SOUTH);
        add(main);
    }

    private JPanel buildHeader() {
        JPanel header = AppTheme.pageHeader(employee.getFullName(),
                "#" + employee.getEmployeeNumber() + "  ·  " + employee.getPosition());

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setBackground(AppTheme.CARD_WHITE);

        monthCombo = new JComboBox<>(new String[]{
            "January","February","March","April","May","June",
            "July","August","September","October","November","December"
        });
        monthCombo.setFont(AppTheme.FONT_BODY);

        JButton computeBtn  = AppTheme.primaryButton("Compute Payslip");
        JButton leaveBtn    = AppTheme.actionButton("📋 Leave History", AppTheme.BLUE_BTN);
        JButton closeBtn    = AppTheme.actionButton("✕  Close", new Color(100, 100, 110));

        computeBtn.addActionListener(e -> computePayroll());
        leaveBtn.addActionListener(e   -> showLeaveDialog());
        closeBtn.addActionListener(e   -> dispose());

        actions.add(AppTheme.boldLabel("Month:"));
        actions.add(monthCombo);
        actions.add(computeBtn);
        actions.add(leaveBtn);
        actions.add(closeBtn);

        header.add(actions, BorderLayout.EAST);
        return header;
    }

    private JScrollPane buildInfo() {
        JPanel cards = new JPanel();
        cards.setLayout(new BoxLayout(cards, BoxLayout.Y_AXIS));
        cards.setOpaque(false);
        cards.setBorder(new EmptyBorder(16, 16, 0, 16));

        cards.add(infoCard("👤  Personal Information", new String[][]{
            {"Employee ID",  employee.getEmployeeNumber()},
            {"Full Name",    employee.getFullName()},
            {"Birthday",     employee.getBirthday()},
            {"Phone",        employee.getPhoneNumber()},
            {"Address",      employee.getAddress()},
        }));
        cards.add(Box.createVerticalStrut(12));
        cards.add(infoCard("🏛  Government IDs", new String[][]{
            {"SSS Number",     employee.getSssNumber()},
            {"PhilHealth No.", employee.getPhilhealthNumber()},
            {"TIN",            employee.getTinNumber()},
            {"Pag-IBIG No.",   employee.getPagibigNumber()},
        }));
        cards.add(Box.createVerticalStrut(12));
        cards.add(infoCard("💼  Employment", new String[][]{
            {"Status",     employee.getStatus()},
            {"Position",   employee.getPosition()},
            {"Supervisor", employee.getImmediateSupervisor()},
        }));
        cards.add(Box.createVerticalStrut(12));
        cards.add(infoCard("💰  Compensation", new String[][]{
            {"Basic Salary",       fmt(employee.getBasicSalary())},
            {"Hourly Rate",        fmt(employee.getHourlyRate())},
            {"Rice Subsidy",       fmt(employee.getRiceSubsidy())},
            {"Phone Allowance",    fmt(employee.getPhoneAllowance())},
            {"Clothing Allowance", fmt(employee.getClothingAllowance())},
            {"Gross Semi-monthly", fmt(employee.getGrossSemiMonthlyRate())},
        }));

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(cards, BorderLayout.NORTH);

        JScrollPane sp = new JScrollPane(wrapper);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(AppTheme.CONTENT_BG);
        return sp;
    }

    private JPanel buildPayroll() {
        payrollArea = new JTextArea(14, 0);
        payrollArea.setEditable(false);
        payrollArea.setFont(AppTheme.FONT_MONO);
        payrollArea.setBackground(new Color(22, 24, 34));
        payrollArea.setForeground(new Color(185, 235, 185));
        payrollArea.setBorder(new EmptyBorder(14, 14, 14, 14));
        payrollArea.setText("\n   Select a month above and click 'Compute Payslip'.");

        JScrollPane sp = new JScrollPane(payrollArea);
        sp.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT, 1));
        sp.setPreferredSize(new Dimension(0, 240));

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBorder(new EmptyBorder(8, 16, 16, 16));
        wrapper.setBackground(AppTheme.CONTENT_BG);
        wrapper.add(sp, BorderLayout.CENTER);
        return wrapper;
    }

    private void computePayroll() {
        String month    = (String) monthCombo.getSelectedItem();
        int    monthNum = monthCombo.getSelectedIndex() + 1;
        Payslip slip    = payrollCalc.buildPayslip(employee, month,
                attendanceReader.getAttendanceForMonth(employee.getEmployeeNumber(), monthNum));
        payrollArea.setText(slip.toFormattedText());
        payrollArea.setCaretPosition(0);
    }

    private void showLeaveDialog() {
        try {
            List<LeaveRequest> all = leaveService.getRequestsByEmployee(employee.getEmployeeNumber());
            if (all.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "No leave requests on record for this employee.",
                    "Leave History", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            String[] cols = {"Leave Type & Dates", "Status"};
            Object[][] data = new Object[all.size()][2];
            for (int i = 0; i < all.size(); i++) {
                data[i][0] = all.get(i).getLeaveType();
                data[i][1] = all.get(i).getStatusString();
            }
            JTable t = new JTable(data, cols) {
                public boolean isCellEditable(int r, int c) { return false; }
            };
            AppTheme.styleTable(t);
            JScrollPane sp = new JScrollPane(t);
            sp.setPreferredSize(new Dimension(520, 200));
            JOptionPane.showMessageDialog(this, sp,
                "Leave History — " + employee.getFullName(), JOptionPane.PLAIN_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void notifyLeaveUpdates() {
        try {
            List<LeaveRequest> all = leaveService.getRequestsByEmployee(employee.getEmployeeNumber());
            long approved = all.stream().filter(r -> r.getStatus() == LeaveRequest.LeaveStatus.APPROVED).count();
            long denied   = all.stream().filter(r -> r.getStatus() == LeaveRequest.LeaveStatus.DENIED).count();
            if (approved > 0 || denied > 0) {
                JOptionPane.showMessageDialog(this,
                    String.format("Leave Summary:\n  Approved: %d\n  Denied: %d", approved, denied),
                    "Leave Status", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IOException ignored) {}
    }

    private JPanel infoCard(String title, String[][] rows) {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(AppTheme.CARD_WHITE);
        card.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT, 1));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        GridBagConstraints gc = new GridBagConstraints();
        gc.fill    = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;

        JLabel titleLabel = AppTheme.boldLabel(title);
        titleLabel.setFont(AppTheme.FONT_HEADING);
        titleLabel.setBorder(new EmptyBorder(12, 16, 8, 16));
        gc.gridx = 0; gc.gridy = 0; gc.gridwidth = 2;
        card.add(titleLabel, gc);

        JSeparator sep = new JSeparator();
        sep.setForeground(AppTheme.BORDER_LIGHT);
        gc.gridy = 1;
        card.add(sep, gc);

        gc.gridwidth = 1;
        for (int i = 0; i < rows.length; i++) {
            gc.gridy = i + 2;

            gc.gridx   = 0; gc.weightx = 0.35;
            gc.insets  = new Insets(4, 16, 4, 8);
            card.add(AppTheme.mutedLabel(rows[i][0]), gc);

            gc.gridx   = 1; gc.weightx = 0.65;
            gc.insets  = new Insets(4, 0, 4, 16);
            String val = (rows[i][1] != null && !rows[i][1].isBlank()) ? rows[i][1] : "—";
            card.add(AppTheme.boldLabel(val), gc);
        }
        return card;
    }

    private String fmt(double v) { return String.format("₱%,.2f", v); }
}