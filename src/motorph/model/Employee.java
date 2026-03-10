package motorph.model;
public class Employee extends Person {

    private String employeeNumber;
    private String address;
    private String phoneNumber;
    private String sssNumber;
    private String philhealthNumber;
    private String tinNumber;
    private String pagibigNumber;
    private String status;
    private String position;
    private String immediateSupervisor;
    private double basicSalary;
    private double riceSubsidy;
    private double phoneAllowance;
    private double clothingAllowance;
    private double grossSemiMonthlyRate;
    private double hourlyRate;

    public Employee(String employeeNumber,
                    String lastName, String firstName, String birthday,
                    String address, String phoneNumber,
                    String sssNumber, String philhealthNumber,
                    String tinNumber, String pagibigNumber,
                    String status, String position, String immediateSupervisor,
                    double basicSalary, double riceSubsidy,
                    double phoneAllowance, double clothingAllowance,
                    double grossSemiMonthlyRate, double hourlyRate) {
        super(firstName, lastName, birthday);
        this.employeeNumber       = employeeNumber;
        this.address              = address;
        this.phoneNumber          = phoneNumber;
        this.sssNumber            = sssNumber;
        this.philhealthNumber     = philhealthNumber;
        this.tinNumber            = tinNumber;
        this.pagibigNumber        = pagibigNumber;
        this.status               = status;
        this.position             = position;
        this.immediateSupervisor  = immediateSupervisor;
        this.basicSalary          = basicSalary;
        this.riceSubsidy          = riceSubsidy;
        this.phoneAllowance       = phoneAllowance;
        this.clothingAllowance    = clothingAllowance;
        this.grossSemiMonthlyRate = grossSemiMonthlyRate;
        this.hourlyRate           = hourlyRate;
    }

    public Employee() { super(); }
    public double getTotalAllowances() {
        return riceSubsidy + phoneAllowance + clothingAllowance;
    }

    public String getEmployeeNumber()      { return employeeNumber; }
    public void   setEmployeeNumber(String v) { this.employeeNumber = v; }
    public String getAddress()             { return address; }
    public void   setAddress(String v)     { this.address = v; }
    public String getPhoneNumber()         { return phoneNumber; }
    public void   setPhoneNumber(String v) { this.phoneNumber = v; }
    public String getSssNumber()           { return sssNumber; }
    public void   setSssNumber(String v)   { this.sssNumber = v; }
    public String getPhilhealthNumber()    { return philhealthNumber; }
    public void   setPhilhealthNumber(String v) { this.philhealthNumber = v; }
    public String getTinNumber()           { return tinNumber; }
    public void   setTinNumber(String v)   { this.tinNumber = v; }
    public String getPagibigNumber()       { return pagibigNumber; }
    public void   setPagibigNumber(String v) { this.pagibigNumber = v; }
    public String getStatus()              { return status; }
    public void   setStatus(String v)      { this.status = v; }
    public String getPosition()            { return position; }
    public void   setPosition(String v)    { this.position = v; }
    public String getImmediateSupervisor() { return immediateSupervisor; }
    public void   setImmediateSupervisor(String v) { this.immediateSupervisor = v; }
    public double getBasicSalary()         { return basicSalary; }
    public void   setBasicSalary(double v) { this.basicSalary = v; }
    public double getRiceSubsidy()         { return riceSubsidy; }
    public void   setRiceSubsidy(double v) { this.riceSubsidy = v; }
    public double getPhoneAllowance()      { return phoneAllowance; }
    public void   setPhoneAllowance(double v) { this.phoneAllowance = v; }
    public double getClothingAllowance()   { return clothingAllowance; }
    public void   setClothingAllowance(double v) { this.clothingAllowance = v; }
    public double getGrossSemiMonthlyRate()       { return grossSemiMonthlyRate; }
    public void   setGrossSemiMonthlyRate(double v) { this.grossSemiMonthlyRate = v; }
    public double getHourlyRate()          { return hourlyRate; }
    public void   setHourlyRate(double v)  { this.hourlyRate = v; }

    @Override
    public String toString() {
        return String.format("Employee[%s] %s — %s", employeeNumber, getFullName(), position);
    }
}