package motorph.model;

public enum Role {
    HR_ADMIN ("HR Manager"),
    FINANCE  ("Finance"),
    IT       ("IT"),
    ADMIN    ("Admin"),
    EMPLOYEE ("Employee");

    private final String displayName;

    Role(String displayName) { this.displayName = displayName; }

    public String getDisplayName() { return displayName; }

    public boolean canProcessPayroll() {
        return this == FINANCE || this == ADMIN;
    }

    public boolean canManageEmployees() {
        return this == HR_ADMIN || this == IT || this == ADMIN;
    }

    public boolean canApproveLeaves() {
        return this == HR_ADMIN || this == ADMIN;
    }

    public boolean canRevertAttendance() {
        return this == IT || this == ADMIN;
    }

    @Override
    public String toString() { return displayName; }
}