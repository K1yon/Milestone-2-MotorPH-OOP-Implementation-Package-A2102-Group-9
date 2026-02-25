package motorph.view;

import motorph.model.LeaveRequest;
import motorph.service.LeaveService;
import motorph.ui.AppTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.List;

public class HRLeaveFrame extends JFrame {

    private final LeaveService        leaveService = new LeaveService();
    private       List<LeaveRequest>  requests;
    private       DefaultTableModel   tableModel;
    private       JTable              table;

    public HRLeaveFrame(JFrame parent) {
        setTitle("MotorPH — Leave Request Management");
        setSize(740, 530);
        setMinimumSize(new Dimension(620, 420));
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        buildUI();
        reload();
        setVisible(true);
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(AppTheme.CONTENT_BG);

        root.add(AppTheme.pageHeader("📋  Leave Request Management",
                "Review and respond to employee leave requests"), BorderLayout.NORTH);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(AppTheme.CONTENT_BG);
        wrapper.setBorder(new EmptyBorder(20, 20, 20, 20));
        wrapper.add(buildTableCard(), BorderLayout.CENTER);

        root.add(wrapper, BorderLayout.CENTER);
        setContentPane(root);
    }

    private JPanel buildTableCard() {
        JPanel card = AppTheme.card();
        card.setLayout(new BorderLayout());

        String[] cols = {"Employee ID", "Leave Type & Dates", "Reason / Description", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        AppTheme.styleTable(table);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setBorder(new EmptyBorder(0, 12, 0, 12));
                if (!sel) {
                    setBackground(row % 2 == 0 ? AppTheme.CARD_WHITE : AppTheme.TABLE_STRIPE);
                    if (col == 3 && v != null) {
                        String s = v.toString();
                        setForeground(s.equals("APPROVED") ? AppTheme.GREEN_TEXT
                                   : s.equals("DENIED")   ? AppTheme.RED_TEXT
                                   :                         AppTheme.AMBER_TEXT);
                        setFont(AppTheme.FONT_BOLD_SM);
                    } else {
                        setForeground(AppTheme.TEXT_PRIMARY);
                        setFont(AppTheme.FONT_BODY);
                    }
                }
                return this;
            }
        });

        table.getColumnModel().getColumn(0).setPreferredWidth(100);
        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        table.getColumnModel().getColumn(2).setPreferredWidth(280);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        card.add(scroll, BorderLayout.CENTER);
        card.add(buildActionBar(), BorderLayout.SOUTH);

        return card;
    }

    private JPanel buildActionBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bar.setBackground(AppTheme.CARD_WHITE);
        bar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, AppTheme.BORDER_LIGHT));

        JButton refreshBtn = AppTheme.actionButton("↺  Refresh", AppTheme.BLUE_BTN);
        JButton approveBtn = AppTheme.actionButton("✓  Approve", AppTheme.GREEN_TEXT);
        JButton denyBtn    = AppTheme.actionButton("✗  Deny",    AppTheme.RED_TEXT);

        refreshBtn.addActionListener(e -> reload());
        approveBtn.addActionListener(e -> updateSelected(LeaveRequest.LeaveStatus.APPROVED.name()));
        denyBtn.addActionListener(e   -> updateSelected(LeaveRequest.LeaveStatus.DENIED.name()));

        bar.add(refreshBtn);
        bar.add(approveBtn);
        bar.add(denyBtn);
        return bar;
    }

    private void reload() {
        tableModel.setRowCount(0);
        try {
            requests = leaveService.getAllRequests();
            for (LeaveRequest r : requests) {
                tableModel.addRow(new Object[]{
                    r.getEmployeeId(),
                    r.getLeaveType(),
                    r.getDescription().isEmpty() ? "—" : r.getDescription(),
                    r.getStatusString()
                });
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                "Failed to load leave requests: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateSelected(String status) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                "Please select a leave request first.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            leaveService.updateStatus(requests.get(row).getRequestId(), status);
            reload();
            JOptionPane.showMessageDialog(this,
                "Leave request marked as " + status + ".", "Updated",
                JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                "Failed to update request: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}