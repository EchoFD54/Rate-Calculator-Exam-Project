package BE;

public class Employee {
    private int id;
    private String name;
    private double annualSalary;
    private double overheadMultPercent;
    private double fixedAnnualAmount;
    private String country;
    private String employeeTeam;
    private double annualWorkingHours;
    private double utilizationPercentage;
    private boolean isOverHeadCost;

    public Employee(String name, String annSalary, String multPer, String fixedAnnAmt, String country, String team, String workHours, String utilization, Boolean isOverHeadCost) {
        this.name = name;
        this.annualSalary = Double.parseDouble(annSalary);
        this.overheadMultPercent = Double.parseDouble(multPer);
        this.fixedAnnualAmount = Double.parseDouble(fixedAnnAmt);
        this.country = country;
        this.employeeTeam = team;
        this.annualWorkingHours = Double.parseDouble(workHours);
        this.utilizationPercentage = Double.parseDouble(utilization);
        this.isOverHeadCost = isOverHeadCost;
    }

    public Employee(int employeeId, String name, String annSalary, String multPer, String fixedAnnAmt, String country, String team, String workHours, String utilization, Boolean isOverHeadCost){
        this.id = employeeId;
        this.name = name;
        this.annualSalary = Double.parseDouble(annSalary);
        this.overheadMultPercent = Double.parseDouble(multPer);
        this.fixedAnnualAmount = Double.parseDouble(fixedAnnAmt);
        this.country = country;
        this.employeeTeam = team;
        this.annualWorkingHours = Double.parseDouble(workHours);
        this.utilizationPercentage = Double.parseDouble(utilization);
        this.isOverHeadCost = isOverHeadCost;
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

    public String getEmployeeTeam() {
        return employeeTeam;
    }

    public void setEmployeeTeam(String employeeTeam) {
        this.employeeTeam = employeeTeam;
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



}
