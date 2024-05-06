package DAL;

import BE.Employee;
import BE.Team;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TeamDAO {
    private final ConnectionManager connectionManager;

    public TeamDAO() {
        this.connectionManager =  new ConnectionManager();
    }



    public int createTeam(Team team)  throws SQLException {
        String sql = "INSERT INTO teams (team_name) " + "VALUES (?)";
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, team.getName());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Inserting Team failed, no rows affected.");
            }

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Inserting team failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error inserting team: " + e.getMessage());
        }
    }

    public void updateTeam(Team team)  throws SQLException {
        String sql = "UPDATE teams SET team_name = ? WHERE team_id = ?";
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, team.getName());
            preparedStatement.setInt(2, team.getTeamId());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating team failed, no rows affected.");
            }

            System.out.println("TEam with ID " + team.getTeamId() + " updated successfully.");
        } catch (SQLException e) {
            throw new SQLException("Error updating team: " + e.getMessage(), e);
        }
    }

    public List<Team> getTeamList() throws SQLException {
        List<Team> teamList = new ArrayList<>();
        String sql = "SELECT * FROM teams";
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                int teamId = resultSet.getInt("team_id");
                String name = resultSet.getString("team_name");
                Team team = new Team(teamId, name);
                teamList.add(team);

            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving employee list: " + e.getMessage(), e);
        }
        return teamList;
    }

    public void deleteTeam(int teamId) throws SQLException {
        String sql = "DELETE FROM teams WHERE team_id = ?";
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, teamId);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Team with ID " + teamId + " not found.");
            }

            System.out.println("Team with ID " + teamId + " deleted successfully.");
        } catch (SQLException e) {
            throw new SQLException("Error deleting team: " + e.getMessage());
        }
    }


    public void addEmployeeToTeam(int employeeId, int teamId) throws SQLException {
        String sql = "UPDATE Employee SET team_id = ? WHERE id = ?";
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, teamId);
            preparedStatement.setInt(2, employeeId);

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Adding employee to team failed, no rows affected.");
            }

            System.out.println("Employee with ID " + employeeId + " added to team with ID " + teamId + " successfully.");
        } catch (SQLException e) {
            throw new SQLException("Error adding employee to team: " + e.getMessage(), e);
        }
    }

    public List<Employee> getEmployeesByTeam(int teamId) throws SQLException {
        List<Employee> employeeList = new ArrayList<>();
        String sql = "SELECT * FROM Employee WHERE id = ?";
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, teamId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    // Construct Employee objects and add them to the list
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error retrieving employees by team: " + e.getMessage(), e);
        }
        return employeeList;
    }



}
