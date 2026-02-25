package motorph.view;

import motorph.controller.EmployeeController;
import motorph.controller.LoginController;
import motorph.controller.UserSession;
import motorph.model.Employee;
import motorph.ui.AppTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;

public class HRDashboardFrame extends BaseAppFrame {

    private final LoginController    loginController;
    private final EmployeeController employeeController;

    private List<Employee>            employeeList = new ArrayList<>();
    private DefaultTableModel         tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTable                    table;
    private JTextField                searchField;
    private JComboBox<String>         positionCombo;
    private JComboBox<String>         statusCombo;
    private JButton                   btnView, btnNew, btnEdit, btnDelete;
    private JLabel                    lblCount;

    public HRDashboardFrame(LoginController loginController) {
        super("MotorPH — HR Dashboard", 1360, 780);
        this.loginController    = loginController;
        this.employeeController = loginController.getEmployeeController();

        initFrame();
        reloadEmployees();
    }

    @Override
    protected void buildNavItems() {
        sidebarNavPanel.add(navItem("👥", "Employees",      true,  null));
        sidebarNavPanel.add(navItem("📋", "Leave Requests", false, () -> new HRLeaveFrame(this)));
    }

    @Override
    protected JPanel buildContent() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(AppTheme.CONTENT_BG);
        panel.add(buildTopBar(),   BorderLayout.NORTH);
        panel.add(buildTableCard(), BorderLayout.CENTER);
        return panel;
    }

    @Override protected String getUserAvatarText()  { return "HR"; }
    @Override protected String getUserDisplayName() {
        Employee hr = employeeController.findByNumber(LoginController.HR_MANAGER_ID);
        return hr != null ? hr.getFullName() : "HR Manager";
    }
    @Override protected String getUserSubtitle()    { return "HR Manager · #10006"; }

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout(20, 0));
        bar.setBackground(AppTheme.CARD_WHITE);
        bar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, AppTheme.BORDER_LIGHT),
            new EmptyBorder(14, 24, 14, 24)
        ));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        left.setOpaque(false);
        JLabel title = new JLabel("Employee Management");
        title.setFont(AppTheme.FONT_TITLE);
        title.setForeground(AppTheme.TEXT_PRIMARY);
        lblCount = AppTheme.mutedLabel("  ·  0 employees");
        left.add(title);
        left.add(lblCount);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);

        searchField = new JTextField(22);
        searchField.setFont(AppTheme.FONT_BODY);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppTheme.BORDER_LIGHT, 1, true),
            new EmptyBorder(6, 10, 6, 10)
        ));

        positionCombo = new JComboBox<>();
        positionCombo.addItem("All Positions");
        AppTheme.styleCombo(positionCombo);

        statusCombo = new JComboBox<>();
        statusCombo.addItem("All Statuses");
        AppTheme.styleCombo(statusCombo);

        right.add(new JLabel("🔍 "));
        right.add(searchField);
        right.add(positionCombo);
        right.add(statusCombo);

        bar.add(left,  BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { applyFilters(); }
            public void removeUpdate(DocumentEvent e) { applyFilters(); }
            public void changedUpdate(DocumentEvent e){ applyFilters(); }
        });
        positionCombo.addActionListener(e -> applyFilters());
        statusCombo.addActionListener(e -> applyFilters());

        return bar;
    }

    private JPanel buildTableCard() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(AppTheme.CONTENT_BG);
        wrapper.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel card = AppTheme.card();
        card.setLayout(new BorderLayout());

        String[] cols = {"#", "Employee ID", "Last Name", "First Name",
                         "Position", "Status", "Basic Salary", "Supervisor"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        table  = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);
        AppTheme.styleTable(table);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setBorder(new EmptyBorder(0, 12, 0, 12));
                if (sel) {
                    setBackground(AppTheme.ROW_SELECTED);
                    setForeground(AppTheme.TEXT_PRIMARY);
                    setFont(AppTheme.FONT_BODY);
                } else {
                    setBackground(row % 2 == 0 ? AppTheme.CARD_WHITE : AppTheme.TABLE_STRIPE);
                    if (col == 5) { // Status column
                        String status = v != null ? v.toString() : "";
                        setForeground(status.equalsIgnoreCase("Regular") ? AppTheme.GREEN_TEXT
                                   : status.equalsIgnoreCase("Probationary") ? AppTheme.AMBER_TEXT
                                   : AppTheme.TEXT_MUTED);
                        setFont(AppTheme.FONT_BOLD_SM);
                    } else {
                        setForeground(AppTheme.TEXT_PRIMARY);
                        setFont(AppTheme.FONT_BODY);
                    }
                }
                return this;
            }
        });

        int[] widths = {40, 110, 130, 130, 200, 110, 130, 180};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(AppTheme.CARD_WHITE);
        card.add(scroll, BorderLayout.CENTER);
        card.add(buildActionBar(), BorderLayout.SOUTH);

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) openDetails();
            }
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            boolean sel = table.getSelectedRow() >= 0;
            btnView.setEnabled(sel);
            btnEdit.setEnabled(sel);
            btnDelete.setEnabled(sel);
        });

        wrapper.add(card, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel buildActionBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(AppTheme.CARD_WHITE);
        bar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, AppTheme.BORDER_LIGHT),
            new EmptyBorder(10, 16, 10, 16)
        ));

        JPanel left  = new JPanel(new FlowLayout(FlowLayout.LEFT,  8, 0));
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        left.setOpaque(false);
        right.setOpaque(false);

        btnView   = AppTheme.actionButton("👁  View",     AppTheme.BLUE_BTN);
        btnNew    = AppTheme.actionButton("＋ New",       AppTheme.GREEN_TEXT);
        btnEdit   = AppTheme.actionButton("✎  Edit",     AppTheme.AMBER_TEXT);
        btnDelete = AppTheme.actionButton("🗑  Delete",   AppTheme.RED_TEXT);
        JButton btnLeave = AppTheme.actionButton("📋 Leave Requests", new Color(90, 60, 150));

        btnView.setEnabled(false);
        btnEdit.setEnabled(false);
        btnDelete.setEnabled(false);

        left.add(btnView);
        left.add(btnNew);
        left.add(btnEdit);
        left.add(btnDelete);
        right.add(btnLeave);

        bar.add(left,  BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);

        // Wire button actions
        btnView.addActionListener(e -> openDetails());
        btnNew.addActionListener(e -> new NewEmployeeFrame(this));
        btnEdit.addActionListener(e -> openEditDialog());
        btnDelete.addActionListener(e -> deleteSelected());
        btnLeave.addActionListener(e -> new HRLeaveFrame(this));

        return bar;
    }

    private void reloadEmployees() {
        employeeList = employeeController.loadAllEmployees();
        refreshTable();
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        int row = 1;
        for (Employee e : employeeList) {
            tableModel.addRow(new Object[]{
                row++,
                e.getEmployeeNumber(),
                e.getLastName(),
                e.getFirstName(),
                e.getPosition(),
                e.getStatus(),
                String.format("₱%,.2f", e.getBasicSalary()),
                e.getImmediateSupervisor()
            });
        }
        lblCount.setText("  ·  " + employeeList.size() + " employees");
        populateFilterCombos();
    }

    private void populateFilterCombos() {
        positionCombo.removeAllItems();
        statusCombo.removeAllItems();
        positionCombo.addItem("All Positions");
        statusCombo.addItem("All Statuses");
        Set<String> positions = new TreeSet<>();
        Set<String> statuses  = new TreeSet<>();
        for (Employee e : employeeList) {
            if (e.getPosition() != null && !e.getPosition().isBlank()) positions.add(e.getPosition());
            if (e.getStatus()   != null && !e.getStatus().isBlank())   statuses.add(e.getStatus());
        }
        positions.forEach(positionCombo::addItem);
        statuses.forEach(statusCombo::addItem);
    }

    private void applyFilters() {
        String search   = searchField.getText().trim();
        String position = (String) positionCombo.getSelectedItem();
        String status   = (String) statusCombo.getSelectedItem();

        List<RowFilter<Object, Object>> filters = new ArrayList<>();
        if (!search.isEmpty())
            filters.add(RowFilter.regexFilter("(?i)" + Pattern.quote(search), 1, 2, 3));
        if (position != null && !position.equals("All Positions"))
            filters.add(RowFilter.regexFilter("^" + Pattern.quote(position) + "$", 4));
        if (status != null && !status.equals("All Statuses"))
            filters.add(RowFilter.regexFilter("^" + Pattern.quote(status)   + "$", 5));

        sorter.setRowFilter(filters.isEmpty() ? null : RowFilter.andFilter(filters));
    }

    private Employee getSelectedEmployee() {
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) return null;
        String empId = tableModel.getValueAt(
                table.convertRowIndexToModel(viewRow), 1).toString();
        return employeeList.stream()
                .filter(e -> e.getEmployeeNumber().equals(empId))
                .findFirst().orElse(null);
    }

    private void openDetails() {
        Employee emp = getSelectedEmployee();
        if (emp != null) new EmployeeDetailsFrame(this, emp);
    }

    private void deleteSelected() {
        Employee emp = getSelectedEmployee();
        if (emp == null) return;
        int c = JOptionPane.showConfirmDialog(this,
            "Delete " + emp.getFullName() + "?\nThis cannot be undone.",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (c == JOptionPane.YES_OPTION) {
            employeeController.deleteEmployee(emp.getEmployeeNumber());
            reloadEmployees();
            JOptionPane.showMessageDialog(this, "Employee deleted successfully.");
        }
    }

    private void openEditDialog() {
        Employee emp = getSelectedEmployee();
        if (emp == null) return;

        JTextField fFirst  = new JTextField(emp.getFirstName());
        JTextField fLast   = new JTextField(emp.getLastName());
        JTextField fPos    = new JTextField(emp.getPosition());
        JTextField fStatus = new JTextField(emp.getStatus());
        JTextField fSalary = new JTextField(String.valueOf(emp.getBasicSalary()));

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 8));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setPreferredSize(new Dimension(420, 210));
        panel.add(AppTheme.boldLabel("First Name:")); panel.add(fFirst);
        panel.add(AppTheme.boldLabel("Last Name:"));  panel.add(fLast);
        panel.add(AppTheme.boldLabel("Position:"));   panel.add(fPos);
        panel.add(AppTheme.boldLabel("Status:"));     panel.add(fStatus);
        panel.add(AppTheme.boldLabel("Basic Salary:")); panel.add(fSalary);

        int res = JOptionPane.showConfirmDialog(this, panel,
            "Edit — " + emp.getFullName(), JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            try {
                emp.setFirstName(fFirst.getText().trim());
                emp.setLastName(fLast.getText().trim());
                emp.setPosition(fPos.getText().trim());
                emp.setStatus(fStatus.getText().trim());
                emp.setBasicSalary(Double.parseDouble(
                        fSalary.getText().trim().replace(",", "")));
                employeeController.updateEmployee(emp);
                reloadEmployees();
                JOptionPane.showMessageDialog(this, "Employee updated successfully.");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid salary value.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void refreshEmployeeList() { reloadEmployees(); }

    public EmployeeController getEmployeeController() { return employeeController; }
}