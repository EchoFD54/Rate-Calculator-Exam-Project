package BE;

import java.text.DecimalFormat;

public class Employee {
    private int id;
    private String name;
    private double annualSalary;
    private double overheadMultPercent;
    private double fixedAnnualAmount;
    private String country;
    private double annualWorkingHours;
    private double utilizationPercentage;
    private boolean isOverHeadCost;
    private int dailyHours;

    public Employee(String name, String annSalary, String multPer, String fixedAnnAmt, String country,  String workHours, String utilization, Boolean isOverHeadCost, String dailyHours) {
        this.name = name;
        this.annualSalary = Double.parseDouble(annSalary);
        this.overheadMultPercent = Double.parseDouble(multPer);
        this.fixedAnnualAmount = Double.parseDouble(fixedAnnAmt);
        this.country = country;
        this.annualWorkingHours = Double.parseDouble(workHours);
        this.utilizationPercentage = Double.parseDouble(utilization);
        this.isOverHeadCost = isOverHeadCost;
        this.dailyHours = Integer.parseInt(dailyHours);
    }

    public Employee(int employeeId, String name, String annSalary, String multPer, String fixedAnnAmt, String country, String workHours, String utilization, Boolean isOverHeadCost, String dailyHours){
        this.id = employeeId;
        this.name = name;
        this.annualSalary = Double.parseDouble(annSalary);
        this.overheadMultPercent = Double.parseDouble(multPer);
        this.fixedAnnualAmount = Double.parseDouble(fixedAnnAmt);
        this.country = country;
        this.annualWorkingHours = Double.parseDouble(workHours);
        this.utilizationPercentage = Double.parseDouble(utilization);
        this.isOverHeadCost = isOverHeadCost;
        this.dailyHours = Integer.parseInt(dailyHours);
    }

    public Employee(int employeeId, String name, String annSalary, String multPer, String fixedAnnAmt,  String workHours, String utilization, Boolean isOverHeadCost, String dailyHours){
        this.id = employeeId;
        this.name = name;
        this.annualSalary = Double.parseDouble(annSalary);
        this.overheadMultPercent = Double.parseDouble(multPer);
        this.fixedAnnualAmount = Double.parseDouble(fixedAnnAmt);
        this.annualWorkingHours = Double.parseDouble(workHours);
        this.utilizationPercentage = Double.parseDouble(utilization);
        this.isOverHeadCost = isOverHeadCost;
        this.dailyHours =   Integer.parseInt(dailyHours);
    }



    public Employee(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Employee() {

    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isOverHeadCost() {
        return isOverHeadCost;
    }

    public void setOverHeadCost(boolean overHeadCost) {
        isOverHeadCost = overHeadCost;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAnnualSalary() {
        return annualSalary;
    }

    public void setAnnualSalary(double annualSalary) {
        this.annualSalary = annualSalary;
    }

    public double getOverheadMultPercent() {
        return overheadMultPercent;
    }

    public void setOverheadMultPercent(double overheadMultPercent) {
        this.overheadMultPercent = overheadMultPercent;
    }

    public double getFixedAnnualAmount() {
        return fixedAnnualAmount;
    }

    public void setFixedAnnualAmount(double fixedAnnualAmount) {
        this.fixedAnnualAmount = fixedAnnualAmount;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public double getAnnualWorkingHours() {
        return annualWorkingHours;
    }

    public void setAnnualWorkingHours(double annualWorkingHours) {
        this.annualWorkingHours = annualWorkingHours;
    }

    public double getUtilizationPercentage() {
        return utilizationPercentage;
    }

    public void setUtilizationPercentage(double utilizationPercentage) {
        this.utilizationPercentage = utilizationPercentage;
    }

    public int getDailyHours() {
        return dailyHours;
    }

    public void setDailyHours(int dailyHours) {
        this.dailyHours = dailyHours;
    }
}
