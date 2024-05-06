package BLL;

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

    public void addEmployeeToTeam(int employeeId, int teamId) throws SQLException {
        teamDAO.addEmployeeToTeam(employeeId, teamId);
    }


}
