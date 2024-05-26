package BE;

public class EmployeeInTeam {
    private int teamId;
    private Employee employee;
    private double hours;
    private double costPercentage;

    public EmployeeInTeam(int teamId, Employee employee, double hours, double costPercentage) {
        this.teamId = teamId;
        this.employee = employee;
        this.hours = hours;
        this.costPercentage = costPercentage;
    }

    public EmployeeInTeam(Employee employee, double hours, double costPercentage) {
        this.employee = employee;
        this.hours = hours;
        this.costPercentage = costPercentage;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public double getHours() {
        return hours;
    }

    public void setHours(double hours) {
        this.hours = hours;
    }

    public double getCostPercentage() {
        return costPercentage;
    }

    public void setCostPercentage(double costPercentage) {
        this.costPercentage = costPercentage;
    }
}
