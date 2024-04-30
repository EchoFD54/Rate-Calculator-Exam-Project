package BE;

public class Employee {
    private int id;
    private String name;
    private int annualSalary;
    private int overheadMultPercent;
    private int fixedAnnualAmount;
    private String country;
    private String employeeTeam;
    private int annualWorkingHours;
    private int utilizationPercentage;
    private boolean isOverHeadCost;

    public Employee(String name, String annSalary, String multPer, String fixedAnnAmt, String country, String team, String workHours, String utilization) {
        this.name = name;
        this.annualSalary = Integer.parseInt(annSalary);
        this.overheadMultPercent = Integer.parseInt(multPer);
        this.fixedAnnualAmount = Integer.parseInt(fixedAnnAmt);
        this.country = country;
        this.employeeTeam = team;
        this.annualWorkingHours = Integer.parseInt(workHours);
        this.utilizationPercentage = Integer.parseInt(utilization);
        this.isOverHeadCost = false;
    }

    public Employee(int employeeId, String name, String annSalary, String multPer, String fixedAnnAmt, String country, String team, String workHours, String utilization, Boolean isOverHeadCost){
        this.id = employeeId;
        this.name = name;
        this.annualSalary = Integer.parseInt(annSalary);
        this.overheadMultPercent = Integer.parseInt(multPer);
        this.fixedAnnualAmount = Integer.parseInt(fixedAnnAmt);
        this.country = country;
        this.employeeTeam = team;
        this.annualWorkingHours = Integer.parseInt(workHours);
        this.utilizationPercentage = Integer.parseInt(utilization);
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

    public int getAnnualSalary() {
        return annualSalary;
    }

    public void setAnnualSalary(int annualSalary) {
        this.annualSalary = annualSalary;
    }

    public int getOverheadMultPercent() {
        return overheadMultPercent;
    }

    public void setOverheadMultPercent(int overheadMultPercent) {
        this.overheadMultPercent = overheadMultPercent;
    }

    public int getFixedAnnualAmount() {
        return fixedAnnualAmount;
    }

    public void setFixedAnnualAmount(int fixedAnnualAmount) {
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

    public int getAnnualWorkingHours() {
        return annualWorkingHours;
    }

    public void setAnnualWorkingHours(int annualWorkingHours) {
        this.annualWorkingHours = annualWorkingHours;
    }

    public int getUtilizationPercentage() {
        return utilizationPercentage;
    }

    public void setUtilizationPercentage(int utilizationPercentage) {
        this.utilizationPercentage = utilizationPercentage;
    }



}
