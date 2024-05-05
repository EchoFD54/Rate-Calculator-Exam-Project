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
}
