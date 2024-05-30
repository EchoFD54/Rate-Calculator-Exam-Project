package DAL;

import BE.Employee;
import BE.EmployeeInTeam;
import BE.Team;

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
        String sql = "INSERT INTO Team (team_name) " + "VALUES (?)";
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
        String sql = "UPDATE Team SET team_name = ? WHERE team_id = ?";
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

    public void updateTeamMultipliers(Team team)  throws SQLException {
        String sql = "UPDATE Team SET markup = ?, gm = ? WHERE team_id = ?";
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setDouble(1, team.getTeamMarkup());
            preparedStatement.setDouble(2, team.getTeamGm());
            preparedStatement.setInt(3, team.getTeamId());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating team failed, no rows affected.");
            }
            System.out.println("TEam with ID " + team.getTeamId() + "  multipliers updated successfully.");
        } catch (SQLException e) {
            throw new SQLException("Error updating team: " + e.getMessage(), e);
        }
    }

    public List<Team> getTeamList() throws SQLException {
        List<Team> teamList = new ArrayList<>();
        String sql = "SELECT * FROM Team";
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                int teamId = resultSet.getInt("team_id");
                String name = resultSet.getString("team_name");
                String markup = String.valueOf(resultSet.getDouble("markup"));
                String gm = String.valueOf(resultSet.getDouble("gm"));

                Team team = new Team(teamId, name, markup,gm);
                teamList.add(team);

            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving employee list: " + e.getMessage(), e);
        }
        return teamList;
    }

    public void deleteTeam(int teamId) throws SQLException {
        String deleteEmployeeInTeamSql = "DELETE FROM EmployeeInTeam WHERE team_id = ?";
        String deleteTeamSql = "DELETE FROM Team WHERE team_id = ?";

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement deleteEmployeeInTeamStatement = connection.prepareStatement(deleteEmployeeInTeamSql);
             PreparedStatement deleteTeamStatement = connection.prepareStatement(deleteTeamSql)) {
            deleteEmployeeInTeamStatement.setInt(1, teamId);
            deleteEmployeeInTeamStatement.executeUpdate();
            deleteTeamStatement.setInt(1, teamId);
            deleteTeamStatement.executeUpdate();

            System.out.println("Team with ID " + teamId + " deleted successfully.");
        } catch (SQLException e) {
            throw new SQLException("Error deleting team: " + e.getMessage());
        }
    }

    public void addEmployeeToTeam(int employeeId, int teamId, double hours, double costPercentage) throws SQLException {
        String sql = "INSERT INTO EmployeeInTeam (employee_id, team_id, hours, cost_Percentage) VALUES (?, ?, ?, ?)";
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, employeeId);
            preparedStatement.setInt(2, teamId);
            preparedStatement.setDouble(3, hours);
            preparedStatement.setDouble(4, costPercentage);

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Adding employee to team failed, no rows affected.");
            }

            System.out.println("Employee with ID " + employeeId + " added to team with ID " + teamId + " successfully.");
        } catch (SQLException e) {
            throw new SQLException("Error adding employee to team: " + e.getMessage(), e);
        }
    }

    public List<EmployeeInTeam> getEmployeesInTeam(int teamId) throws SQLException {
        String sql = "SELECT e.*, eit.hours, eit.cost_Percentage FROM Employee e INNER JOIN EmployeeInTeam eit ON e.id = eit.employee_Id WHERE eit.team_Id = ?";
        List<EmployeeInTeam> employeesInTeam = new ArrayList<>();
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, teamId);
            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String name = resultSet.getString("name");
                    String annualSalary = String.valueOf(resultSet.getDouble("annualSalary"));
                    String overheadMultPercent = String.valueOf(resultSet.getDouble("overheadMultPercent"));
                    String fixedAnnualAmount = String.valueOf(resultSet.getDouble("fixedAnnualAmount"));
                    String annualWorkingHours = String.valueOf(resultSet.getDouble("annualWorkingHours"));
                    String utilizationPercentage = String.valueOf(resultSet.getDouble("utilizationPercentage"));
                    boolean isOverHeadCost = resultSet.getBoolean("isOverHeadCost");
                    String dailyHours = String.valueOf(resultSet.getInt("dailyHours"));
                    String country = String.valueOf(resultSet.getString("country"));
                    Employee employee = new Employee(id, name, annualSalary, overheadMultPercent, fixedAnnualAmount, country, annualWorkingHours, utilizationPercentage, isOverHeadCost, dailyHours);
                    EmployeeInTeam employeeInTeam = new EmployeeInTeam(
                            teamId,
                            employee,
                            resultSet.getDouble("hours"),
                            resultSet.getDouble("cost_Percentage")
                    );
                    employeesInTeam.add(employeeInTeam);
                }
            }
        }catch (SQLException e) {
            throw new SQLException("Error retrieving employees by team: " + e.getMessage(), e);
        }
        return employeesInTeam;
    }

    public void removeEmployeeFromTeam(int employeeId, int teamId) throws SQLException {
        String sql = "DELETE FROM EmployeeInTeam WHERE employee_id = ? AND team_id = ?";
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, employeeId);
            preparedStatement.setInt(2, teamId);
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Removing employee from team failed, no rows affected.");
            }
            System.out.println("Employee with ID " + employeeId + " removed from team " + teamId + " successfully.");
        } catch (SQLException e) {
            throw new SQLException("Error removing employee from team: " + e.getMessage(), e);
        }
    }


    public double getTotalCostPercentageForEmployee(int employeeId) throws SQLException {
        String sql = "SELECT SUM(cost_Percentage) as totalCostPercentage FROM EmployeeInTeam WHERE employee_id = ?";
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, employeeId);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("totalCostPercentage");
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error retrieving total cost percentage: " + e.getMessage(), e);
        }
        return 0;
    }

    public void updateEmployeeInTeam(int teamId, int employeeId, double hours, double costPercentage) throws SQLException {
        String sql = "UPDATE EmployeeInTeam SET hours = ?, cost_Percentage = ? WHERE team_Id = ? AND employee_Id = ?";
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDouble(1, hours);
            pstmt.setDouble(2, costPercentage);
            pstmt.setInt(3, teamId);
            pstmt.setInt(4, employeeId);
            pstmt.executeUpdate();
        }
    }


    public boolean employeeExistsInTeam(int employeeId, int teamId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM EmployeeInTeam WHERE employee_Id = ? AND team_Id = ?";
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, employeeId);
            pstmt.setInt(2, teamId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public double getTotalHoursForEmployee(int employeeId) throws SQLException {
        String sql = "SELECT SUM(hours) as totalEmployeeHours FROM EmployeeInTeam WHERE employee_id = ?";
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, employeeId);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("totalEmployeeHours");
                }
            }

        } catch (SQLException e) {
            throw new SQLException("Error retrieving total hours: " + e.getMessage(), e);
        }
        return 0;
    }




}
