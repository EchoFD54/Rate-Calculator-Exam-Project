package GUI.model;

import BE.Employee;
import BE.EmployeeInTeam;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.List;

public class RateCalculator {

    private final Model model = new Model();

    public  double calculateHourlyRate(Employee employee){
        double totalAnnualSalary = employee.getAnnualSalary() + employee.getFixedAnnualAmount();
        double effectiveHourlyRate = totalAnnualSalary / employee.getAnnualWorkingHours();
        double overheadMultiplier = 1 + employee.getOverheadMultPercent() / 100.0;
        double hourlyRate = effectiveHourlyRate * overheadMultiplier;
         hourlyRate = Math.round(hourlyRate * 100.0) / 100.0;
        return hourlyRate;
    }

    public double calculateDailyRate(Employee employee){
        int hoursInADay = employee.getDailyHours();
        double hourlyRate = calculateHourlyRate(employee);
        double dailyRate = hourlyRate * hoursInADay;dailyRate = Math.round(dailyRate * 100.0) / 100.0;
        return dailyRate;
    }

    public double calculateTeamDailyRate(int teamId) throws SQLException {
        List<Employee> employees = model.getEmployeesFromTeamInDB(teamId);
        double totalDayRate = 0;
        for (Employee employee : employees) {
            totalDayRate += calculateDailyRate(employee);
        }
        return totalDayRate;
    }

    public double applyMarkup(double rate, double markupPercentage) {
        return rate * (1 + markupPercentage / 100.0);
    }

    public double calculateRateWithGrossMargin(double rate, double grossMarginPercentage) {
        return rate / (1 - grossMarginPercentage / 100.0);
    }

    public double calculateEmployeeCost(Employee employee) {
        double totalAnnualSalary = employee.getAnnualSalary() + employee.getFixedAnnualAmount();
        double overheadMultiplier = 1 + employee.getOverheadMultPercent() / 100.0;
        return totalAnnualSalary * overheadMultiplier;
    }

    public double calculateEmployeeRevenue(Employee employee, double markupPercentage, double grossMarginPercentage) {
        double hourlyRate = calculateHourlyRate(employee);
        double markedUpRate = applyMarkup(hourlyRate, markupPercentage);
        double rateWithMargin = calculateRateWithGrossMargin(markedUpRate, grossMarginPercentage);
        double utilizationFactor = employee.getUtilizationPercentage() / 100.0;

        return rateWithMargin * employee.getAnnualWorkingHours() * utilizationFactor;
    }

    public double calculateTeamCost(List<EmployeeInTeam> employeesInTeam) {
        double totalCost = 0;
        for (EmployeeInTeam employeeInTeam : employeesInTeam) {
            double employeeCost = calculateEmployeeCost(employeeInTeam.getEmployee());
            double costPercentage = employeeInTeam.getCostPercentage() / 100.0;
            totalCost += employeeCost * costPercentage;
        }
        return totalCost;
    }

}
