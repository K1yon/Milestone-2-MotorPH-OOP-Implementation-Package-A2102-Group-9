package motorph.util;

import motorph.model.Employee;
import motorph.model.Payslip;
import motorph.model.WorkHours;

import java.util.List;

public class PayrollCalculator {

    public Payslip buildPayslip(Employee emp, String monthName, List<WorkHours> attendance) {
        int regularHrs = 0, overtimeHrs = 0;
        for (WorkHours wh : attendance) {
            regularHrs  += wh.getRegularHours();
            overtimeHrs += wh.getOvertimeHours();
        }

        if (attendance.isEmpty()) {
            return new Payslip(
                emp.getEmployeeNumber(), emp.getFullName(), monthName,
                0, 0, 0, 0,
                0, 0, 0,
                0, 0, 0, 0
            );
        }

        double regularPay  = regularHrs  * emp.getHourlyRate();
        double overtimePay = overtimeHrs * emp.getHourlyRate() * 1.25;
        double grossPay    = regularPay + overtimePay + emp.getTotalAllowances();

        return new Payslip(
            emp.getEmployeeNumber(), emp.getFullName(), monthName,
            regularHrs, overtimeHrs, regularPay, overtimePay,
            emp.getRiceSubsidy(), emp.getPhoneAllowance(), emp.getClothingAllowance(),
            calculateSSS(emp.getBasicSalary()),
            calculatePhilHealth(emp.getBasicSalary()),
            calculatePagIbig(emp.getBasicSalary()),
            calculateWithholdingTax(grossPay)
        );
    }

    public double calculateSSS(double s) {
        if (s <  4250) return 180.00; if (s <  4750) return 202.50;
        if (s <  5250) return 225.00; if (s <  5750) return 247.50;
        if (s <  6250) return 270.00; if (s <  6750) return 292.50;
        if (s <  7250) return 315.00; if (s <  7750) return 337.50;
        if (s <  8250) return 360.00; if (s <  8750) return 382.50;
        if (s <  9250) return 405.00; if (s <  9750) return 427.50;
        if (s < 10250) return 450.00; if (s < 10750) return 472.50;
        if (s < 11250) return 495.00; if (s < 11750) return 517.50;
        if (s < 12250) return 540.00; if (s < 12750) return 562.50;
        if (s < 13250) return 585.00; if (s < 13750) return 607.50;
        if (s < 14250) return 630.00; if (s < 14750) return 652.50;
        if (s < 15250) return 675.00; if (s < 15750) return 697.50;
        if (s < 16250) return 720.00; if (s < 16750) return 742.50;
        if (s < 17250) return 765.00; if (s < 17750) return 787.50;
        if (s < 18250) return 810.00; if (s < 18750) return 832.50;
        if (s < 19250) return 855.00; if (s < 19750) return 877.50;
        return 900.00;
    }

    public double calculatePhilHealth(double s) {
        return Math.max(10_000, Math.min(s, 100_000)) * 0.05 * 0.50;
    }

    public double calculatePagIbig(double s) {
        return s <= 1_500 ? s * 0.01 : s * 0.02;
    }

    public double calculateWithholdingTax(double monthlyGross) {
        double a = monthlyGross * 12, tax;
        if      (a <=   250_000) tax = 0;
        else if (a <=   400_000) tax = (a -   250_000) * 0.15;
        else if (a <=   800_000) tax = 22_500  + (a -   400_000) * 0.20;
        else if (a <= 2_000_000) tax = 102_500 + (a -   800_000) * 0.25;
        else if (a <= 8_000_000) tax = 402_500 + (a - 2_000_000) * 0.30;
        else                     tax = 2_202_500 + (a - 8_000_000) * 0.35;
        return tax / 12;
    }
}