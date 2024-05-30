package GUI.model;

import BE.Employee;
import BE.EmployeeInTeam;
import BE.Team;
import BE.User;
import BLL.EmployeeManager;
import BLL.TeamManager;
import BLL.UserManager;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Model {
    private static Model instance;

    private final EmployeeManager employeeManager = new EmployeeManager();
    private final TeamManager teamManager = new TeamManager();
    private final UserManager userManager = new UserManager();

    private Model(){

    }

    public static Model getInstance() {
        if (instance == null) {
            synchronized (Model.class) {
                if (instance == null) {
                    instance = new Model();
                }
            }
        }
        return instance;
    }

    public int createEmployeeInDB(Employee employee) throws SQLException {
        return employeeManager.createEmployee(employee);
    }

    public void updateEmployeeInDB(Employee employee) throws SQLException {
        employeeManager.updateEmployee(employee);
    }

    public List<Employee> getEmployeesFromDB() throws SQLException {
        return employeeManager.getAllEmployees();
    }

    public List<String> getTeamsFromDBUsingEmployee(int employeeId) throws SQLException {
        return employeeManager.getTeamNames(employeeId);
    }
    
    public void deleteEmployeeFromDB(int employeeId) throws SQLException {
        employeeManager.deleteEmployee(employeeId);
    }

    public int createTeamInDB(Team team) throws SQLException {
        return teamManager.createTeam(team);
    }

    public List<Team> getTeamsFromDB() throws SQLException {
        return teamManager.getAllTeams();
    }

    public void updateTeamInDB(Team team) throws SQLException {
        teamManager.updateTeam(team);
    }

    public void updateTeamMultipliersInDB(Team team) throws SQLException {
        teamManager.updateTeamMultipliers(team);
    }

    public void addEmployeeToTeamInDB(int employeeId, int teamId, double hours, double costPercentage) throws SQLException {
        teamManager.addEmployeeToTeam(employeeId, teamId, hours, costPercentage);
    }

    public void deleteEmployeeFromTeamInDB(int employeeId, int teamId) throws SQLException {
        teamManager.removeEmployeeFromTeam(employeeId, teamId);
    }


    public void  deleteTeamFromDB(int teamId) throws SQLException {
        teamManager.deleteTeam(teamId);
    }

    public List<User> getUsersFromDB() throws SQLException {
       return userManager.getAllUsers();
    }

    public List<EmployeeInTeam> getEmployeesInTeamFromDB(int teamId) throws SQLException {
        return teamManager.getEmployeesInTeam(teamId);
    }

    public double getTotalCostPercentageForEmployee(int employeeId) throws SQLException {
        return teamManager.getTotalCostPercentageForEmployee(employeeId);
    }

    public double getTotalHoursForEmployee(int employeeId) throws SQLException {
        return teamManager.getTotalHoursForEmployee(employeeId);
    }

    public void updateEmployeeInTeam(int teamId, EmployeeInTeam employeeInTeam) throws SQLException {
        if (teamManager.employeeExistsInTeam(employeeInTeam.getEmployee().getId(), teamId)) {
            teamManager.updateEmployeeInTeam(teamId, employeeInTeam.getEmployee().getId(), employeeInTeam.getHours(), employeeInTeam.getCostPercentage());
        } else {
            teamManager.addEmployeeToTeam(teamId, employeeInTeam.getEmployee().getId(), employeeInTeam.getHours(), employeeInTeam.getCostPercentage());
        }
    }

    public double getTeamDailyHours(int teamId) throws SQLException {
        List<EmployeeInTeam> employeesInTeam = teamManager.getEmployeesInTeam(teamId);
        double dailyTotalHours = 0;
        for (EmployeeInTeam employeeInTeam : employeesInTeam) {
            double dailyHours = employeeInTeam.getHours();
            dailyTotalHours += dailyHours;
        }
        return dailyTotalHours;
    }

    public Set<String> getCountriesOfEmployeesInTeam(int teamId) throws SQLException {
        List<EmployeeInTeam> employeesInTeam = teamManager.getEmployeesInTeam(teamId);
        Set<String> countries = new HashSet<>();
        for (EmployeeInTeam employeeInTeam : employeesInTeam) {
            Employee employee = employeeInTeam.getEmployee();
            countries.add(employee.getCountry());
        }
        return countries;
    }

}
