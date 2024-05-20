package GUI.model;

import BE.Employee;
import BE.Team;
import BE.User;
import BLL.EmployeeManager;
import BLL.TeamManager;
import BLL.UserManager;

import java.sql.SQLException;
import java.util.List;


public class Model {
    private final EmployeeManager employeeManager = new EmployeeManager();
    private final TeamManager teamManager = new TeamManager();
    private final UserManager userManager = new UserManager();


    public int createEmployeeInDB(Employee employee) throws SQLException {
        return employeeManager.createEmployee(employee);
    }

    public void updateEmployeeInDB(Employee employee) throws SQLException {
        employeeManager.updateEmployee(employee);
    }

    public List<Employee> getEmployeesFromDB() throws SQLException {
        return employeeManager.getAllEmployees();
    }

    public List<String> GetTeamsFromDBUsingEmployee(int employeeId) throws SQLException {
        return employeeManager.getTeamNames(employeeId);
    }

    public List<String> getAllCountriesFromDB() throws SQLException {
        return employeeManager.getAllCountries();
    }

    public List<Employee> getEmployeesFromCountryInDB(String country) throws SQLException {
        return employeeManager.getEmployeesByCountry(country);
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

    public void addEmployeeToTeamInDB(int employeeId, int teamId) throws SQLException {
        teamManager.addEmployeeToTeam(employeeId, teamId);
    }

    public List<Employee> getEmployeesFromTeamInDB(int teamId) throws SQLException {
        return teamManager.getEmployeesFromTeam(teamId);
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

}
