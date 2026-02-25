package motorph.model;
public class WorkHours {

    private String date;
    private int    regularHours;
    private int    overtimeHours;
    private String employeeNumber;

    public WorkHours(String date, int regularHours, int overtimeHours, String employeeNumber) {
        this.date           = date;
        this.regularHours   = regularHours;
        this.overtimeHours  = overtimeHours;
        this.employeeNumber = employeeNumber;
    }

    public int getTotalHours() { return regularHours + overtimeHours; }

    public String getDate()              { return date; }
    public void   setDate(String v)      { this.date = v; }
    public int    getRegularHours()      { return regularHours; }
    public void   setRegularHours(int v) { this.regularHours = v; }
    public int    getOvertimeHours()     { return overtimeHours; }
    public void   setOvertimeHours(int v){ this.overtimeHours = v; }
    public String getEmployeeNumber()    { return employeeNumber; }
    public void   setEmployeeNumber(String v) { this.employeeNumber = v; }

    @Override
    public String toString() {
        return String.format("WorkHours[Emp#%s | %s | %dh reg + %dh OT]",
                employeeNumber, date, regularHours, overtimeHours);
    }
}