package GUI.model;

import BE.Employee;
import BE.EmployeeInTeam;

import java.sql.SQLException;
import java.util.List;

public class RateCalculator {

    private final Model model = new Model();

    public  double calculateHourlyRate(Employee employee){
        double totalAnnualSalary = employee.getAnnualSalary() + employee.getFixedAnnualAmount();
        double effectiveHourlyRate = totalAnnualSalary / employee.getAnnualWorkingHours();
        double overheadMultiplier = 1 + employee.getOverheadMultPercent() / 100.0;
        double hourlyRate = effectiveHourlyRate * overheadMultiplier;
        double utilizationPercentage = employee.getUtilizationPercentage() / 100.0;
        hourlyRate = hourlyRate * utilizationPercentage; // Adjust for utilization percentage
        hourlyRate = Math.round(hourlyRate * 100.0) / 100.0;
        return hourlyRate;
    }

    public double calculateDailyRate(Employee employee){
        int hoursInADay = employee.getDailyHours();
        double hourlyRate = calculateHourlyRate(employee);
        double dailyRate = hourlyRate * hoursInADay;
        dailyRate = Math.round(dailyRate * 100.0) / 100.0;
        return dailyRate;
    }

    public double calculateTeamDailyRate(int teamId) throws SQLException {
        List<EmployeeInTeam> employeesInTeam = model.getEmployeesInTeamFromDB(teamId);
        double totalDailyRate = 0;

        for (EmployeeInTeam employeeInTeam : employeesInTeam) {
            double hoursAssigned = employeeInTeam.getHours();
            if (hoursAssigned > 0) {
                double hourlyRate = calculateHourlyRate(employeeInTeam.getEmployee());
                totalDailyRate += hourlyRate * hoursAssigned;
            }
        }

        return totalDailyRate;
    }

    public double applyMarkup(double rate, double markupPercentage) {
        return rate * (1 + markupPercentage / 100.0);
    }

    public double applyGrossMargin(double rate, double grossMarginPercentage) {
        return rate / (1 - grossMarginPercentage / 100.0);
    }

    public double calculateEmployeeCost(Employee employee) {
        double totalAnnualSalary = employee.getAnnualSalary() + employee.getFixedAnnualAmount();
        double overheadMultiplier = 1 + employee.getOverheadMultPercent() / 100.0;
        return totalAnnualSalary * overheadMultiplier;
    }

    public double calculateEmployeeRevenue(Employee employee) {
        double hourlyRate = calculateHourlyRate(employee);
        return hourlyRate * employee.getAnnualWorkingHours();
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

    public double calculateTeamCostWithMarkup(List<EmployeeInTeam> employeesInTeam, double markupPercentage) {
        double baseCost = calculateTeamCost(employeesInTeam);
        return applyMarkup(baseCost, markupPercentage);
    }

    public double calculateTeamRevenueWithGM(List<EmployeeInTeam> employeesInTeam, double grossMarginPercentage) {
        double totalRevenue = 0;
        for (EmployeeInTeam employeeInTeam : employeesInTeam) {
            double employeeRevenue = calculateEmployeeRevenue(employeeInTeam.getEmployee());
            double revenueWithGM = applyGrossMargin(employeeRevenue, grossMarginPercentage);
            totalRevenue += revenueWithGM;
        }
        return totalRevenue;
    }

    public double calculateTeamRevenueWithoutMultipliers(List<EmployeeInTeam> employeesInTeam) {
        double totalRevenue = 0;
        for (EmployeeInTeam employeeInTeam : employeesInTeam) {
            double employeeRevenue = calculateEmployeeRevenue(employeeInTeam.getEmployee());
            totalRevenue += employeeRevenue;
        }
        return totalRevenue;
    }

    public double calculateTeamDailyRateWithMultipliers(int teamId, double markupPercentage, double grossMarginPercentage) throws SQLException {
        List<EmployeeInTeam> employeesInTeam = model.getEmployeesInTeamFromDB(teamId);
        double totalDailyRate = 0;

        for (EmployeeInTeam employeeInTeam : employeesInTeam) {
            double hoursAssigned = employeeInTeam.getHours();
            if (hoursAssigned > 0) {
                double hourlyRate = calculateHourlyRate(employeeInTeam.getEmployee());
                double dailyRate = hourlyRate * hoursAssigned;
                double dailyRateWithMarkup = applyMarkup(dailyRate, markupPercentage);
                double dailyRateWithGM = applyGrossMargin(dailyRateWithMarkup, grossMarginPercentage);
                totalDailyRate += dailyRateWithGM;
            }
        }

        return totalDailyRate;
    }


    public double calculateTeamHourlyRate(int teamId) throws SQLException {
        List<EmployeeInTeam> employeesInTeam = model.getEmployeesInTeamFromDB(teamId);
        double totalWeightedRate = 0;
        double totalHours = 0;
        for (EmployeeInTeam employeeInTeam : employeesInTeam) {
            double hoursAssigned = employeeInTeam.getHours();
            if (employeeInTeam.getHours() > 0) {
                double hourlyRate = calculateHourlyRate(employeeInTeam.getEmployee());
                totalWeightedRate += hourlyRate * hoursAssigned;
                totalHours += hoursAssigned;
            }
        }
        return totalHours > 0 ? totalWeightedRate / totalHours : 0;
    }


    public double calculateTeamHourlyRateWithMultipliers(int teamId, double markupPercentage, double grossMarginPercentage) throws SQLException {
        List<EmployeeInTeam> employeesInTeam = model.getEmployeesInTeamFromDB(teamId);
        double totalWeightedRate = 0;
        double totalHours = 0;

        for (EmployeeInTeam employeeInTeam : employeesInTeam) {
            double hoursAssigned = employeeInTeam.getHours();
            if (hoursAssigned > 0) {
                double hourlyRate = calculateHourlyRate(employeeInTeam.getEmployee());
                double hourlyRateWithMarkup = applyMarkup(hourlyRate, markupPercentage);
                double hourlyRateWithGM = applyGrossMargin(hourlyRateWithMarkup, grossMarginPercentage);
                totalWeightedRate += hourlyRateWithGM * hoursAssigned;
                totalHours += hoursAssigned;
            }
        }

        return totalHours > 0 ? totalWeightedRate / totalHours : 0;
    }

}
