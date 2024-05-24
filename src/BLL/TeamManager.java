package BLL;

import BE.Employee;
import BE.EmployeeInTeam;
import BE.Team;
import DAL.TeamDAO;

import java.sql.SQLException;
import java.util.List;

public class TeamManager {
    TeamDAO teamDAO = new TeamDAO();
    public int createTeam(Team team) throws SQLException {
        return teamDAO.createTeam(team);
    }

    public List<Team> getAllTeams() throws SQLException {
        return teamDAO.getTeamList();
    }

    public void updateTeam(Team team) throws SQLException {
        teamDAO.updateTeam(team);
    }

    public void deleteTeam(int teamId) throws SQLException {
        teamDAO.deleteTeam(teamId);
    }

    public void addEmployeeToTeam(int employeeId, int teamId, double hours, double costPercentage) throws SQLException {
        teamDAO.addEmployeeToTeam(employeeId, teamId, hours, costPercentage);
    }

    public List<Employee> getEmployeesFromTeam(int teamId) throws SQLException {
        return teamDAO.getEmployeesByTeam(teamId);
    }

    public void removeEmployeeFromTeam(int employeeId, int teamId) throws SQLException {
        teamDAO.removeEmployeeFromTeam(employeeId, teamId);
    }

    public List<EmployeeInTeam> getEmployeesInTeam(int teamId) throws SQLException {
        return teamDAO.getEmployeesInTeam(teamId);
    }

    public double getTotalCostPercentageForEmployee(int employeeId) throws SQLException {
        return teamDAO.getTotalCostPercentageForEmployee(employeeId);
    }

    public double getTotalHoursForEmployee(int employeeId) throws SQLException {
        return teamDAO.getTotalHoursForEmployee(employeeId);
    }

    public void updateEmployeeInTeam(int employeeId, int teamId, double hours, double costPercentage) throws SQLException {
        teamDAO.updateEmployeeInTeam(employeeId, teamId, hours, costPercentage);
    }

    public boolean employeeExistsInTeam(int employeeId, int teamId) throws SQLException {
        return teamDAO.employeeExistsInTeam(employeeId, teamId);
    }




}
