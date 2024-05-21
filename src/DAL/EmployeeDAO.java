package DAL;

import BE.Employee;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {
    private final ConnectionManager connectionManager;

    public EmployeeDAO() {
        this.connectionManager = new ConnectionManager();
    }



    public int createEmployee(Employee employee) throws SQLException {
        String sql = "INSERT INTO Employee (name, annualSalary, overheadMultPercent, fixedAnnualAmount, country, annualWorkingHours, utilizationPercentage, isOverHeadCost, dailyHours) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, employee.getName());
            preparedStatement.setDouble(2, employee.getAnnualSalary());
            preparedStatement.setDouble(3, employee.getOverheadMultPercent());
            preparedStatement.setDouble(4, employee.getFixedAnnualAmount());
            preparedStatement.setString(5, employee.getCountry());
            preparedStatement.setDouble(6, employee.getAnnualWorkingHours());
            preparedStatement.setDouble(7, employee.getUtilizationPercentage());
            preparedStatement.setBoolean(8, employee.isOverHeadCost());
            preparedStatement.setInt(9, employee.getDailyHours());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Inserting employee failed, no rows affected.");
            }

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Inserting employee failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error inserting employee: " + e.getMessage());
        }
    }

    public void updateEmployee(Employee employee) throws SQLException {
        String sql = "UPDATE Employee SET name = ?, annualSalary = ?, overheadMultPercent = ?, fixedAnnualAmount = ?, country = ?, annualWorkingHours = ?, utilizationPercentage = ?, isOverHeadCost = ?, dailyHours = ? WHERE id = ?";
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, employee.getName());
            preparedStatement.setDouble(2, employee.getAnnualSalary());
            preparedStatement.setDouble(3, employee.getOverheadMultPercent());
            preparedStatement.setDouble(4, employee.getFixedAnnualAmount());
            preparedStatement.setString(5, employee.getCountry());
            preparedStatement.setDouble(6, employee.getAnnualWorkingHours());
            preparedStatement.setDouble(7, employee.getUtilizationPercentage());
            preparedStatement.setBoolean(8, employee.isOverHeadCost());
            preparedStatement.setInt(9, employee.getDailyHours());
            preparedStatement.setInt(10, employee.getId());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating employee failed, no rows affected.");
            }

            System.out.println("Employee with ID " + employee.getId() + " updated successfully.");
        } catch (SQLException e) {
            throw new SQLException("Error updating employee: " + e.getMessage(), e);
        }
    }

    public void deleteEmployee(int employeeId) throws SQLException {
        String deleteEmployeeSql = "DELETE FROM Employee WHERE id = ?";
        String deleteEmployeeInTeamSql = "DELETE FROM EmployeeInTeam WHERE employee_id = ?";

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement deleteEmployeeStatement = connection.prepareStatement(deleteEmployeeSql);
             PreparedStatement deleteEmployeeInTeamStatement = connection.prepareStatement(deleteEmployeeInTeamSql)) {

            // Remove from EmployeeInTeam table
            deleteEmployeeInTeamStatement.setInt(1, employeeId);
            deleteEmployeeInTeamStatement.executeUpdate();

            // Remove from Employee table
            deleteEmployeeStatement.setInt(1, employeeId);
            int affectedRows = deleteEmployeeStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("No rows deleted from Employee table for employee ID: " + employeeId);
            }

            System.out.println("Employee with ID " + employeeId + " deleted successfully.");
        } catch (SQLException e) {
            throw new SQLException("Error deleting employee: " + e.getMessage(), e);
        }
    }

    public List<Employee> getEmployeeList() throws SQLServerException {
        List<Employee> employeeList = new ArrayList<>();
        String sql = "SELECT * FROM Employee";
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
               int employeeId = resultSet.getInt("id");
               String name = resultSet.getString("name");
               String annualSalary = String.valueOf(resultSet.getDouble("annualSalary"));
               String overheadMultPercent = String.valueOf(resultSet.getDouble("overheadMultPercent"));
               String fixedAnnualAmount = String.valueOf(resultSet.getDouble("fixedAnnualAmount"));
               String country = resultSet.getString("country");
               String annualWorkingHours = String.valueOf(resultSet.getDouble("annualWorkingHours"));
               String utilizationPercentage = String.valueOf(resultSet.getDouble("utilizationPercentage"));
               boolean isOverHeadCost = resultSet.getBoolean("isOverHeadCost");
               String dailyHours = String.valueOf(resultSet.getInt("dailyHours"));
               Employee employee = new Employee(employeeId, name, annualSalary, overheadMultPercent, fixedAnnualAmount, country, annualWorkingHours, utilizationPercentage, isOverHeadCost, dailyHours);
                employeeList.add(employee);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving employee list: " + e.getMessage(), e);
        }
        return employeeList;
    }

    public List<String> getTeamNamesByEmployeeId(int employeeId) throws SQLException {
        List<String> teamNames = new ArrayList<>();
        String sql = "SELECT Team.team_name FROM EmployeeInTeam " +
                "INNER JOIN Team ON EmployeeInTeam.team_id = Team.team_id " +
                "WHERE EmployeeInTeam.employee_id = ?";
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, employeeId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    teamNames.add(resultSet.getString("team_name"));
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error retrieving team names for employee: " + e.getMessage(), e);
        }
        return teamNames;
    }

    public List<String> getAllCountries() throws SQLException {
        String sql = "SELECT DISTINCT country FROM Employee";
        List<String> countries = new ArrayList<>();
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                countries.add(resultSet.getString("country"));
            }
        }
        return countries;
    }

    public List<Employee> getEmployeesByCountry(String country) throws SQLException {
        String sql = "SELECT * FROM Employee WHERE country = ?";
        List<Employee> employees = new ArrayList<>();
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, country);
            try (ResultSet resultSet = statement.executeQuery()) {
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
                    Employee employee = new Employee(id, name, annualSalary, overheadMultPercent, fixedAnnualAmount, annualWorkingHours, utilizationPercentage, isOverHeadCost, dailyHours);
                    employees.add(employee);
                }
            }
        }
        return employees;
    }



}