package GUI.model;

import BE.Employee;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.List;

public class RateCalculator {

    private final Model model = new Model();

    public  double calculateHourlyDate(Employee employee){
        double totalAnnualSalary = employee.getAnnualSalary() + employee.getFixedAnnualAmount();
        double effectiveHourlyRate = totalAnnualSalary / employee.getAnnualWorkingHours();
        double totalUtilization = employee.getUtilizationPercentage() /100.0;
        double hourlyRate = effectiveHourlyRate * totalUtilization * employee.getOverheadMultPercent();
        DecimalFormat df = new DecimalFormat("#.##");
        hourlyRate = Double.parseDouble(df.format(hourlyRate));
        return hourlyRate;
    }

    public double calculateDailyRate(Employee employee){
        int hoursInADay = employee.getDailyHours();
        double hourlyRate = calculateHourlyDate(employee);
        double dailyRate = hourlyRate * hoursInADay;dailyRate = Math.round(dailyRate * 100.0) / 100.0;
        return dailyRate;
    }

    public double calculateTotalDayRateByCountry(String country) throws SQLException {
        List<Employee> employees = model.getEmployeesFromCountryInDB(country);
        double totalDayRate = 0;
        for (Employee employee : employees) {
            totalDayRate += calculateDailyRate(employee);
        }
        return totalDayRate;
    }

    public double calculateTeamDailyRate(int teamId) throws SQLException {
        List<Employee> employees = model.getEmployeesFromTeamInDB(teamId);
        double totalDayRate = 0;
        for (Employee employee : employees) {
            totalDayRate += calculateDailyRate(employee);
        }
        return totalDayRate;
    }
}
