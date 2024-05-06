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
        String sql = "INSERT INTO Employee (name, annualSalary, overheadMultPercent, fixedAnnualAmount, country, annualWorkingHours, utilizationPercentage, isOverHeadCost) " +
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
        String sql = "UPDATE Employee SET name = ?, annualSalary = ?, overheadMultPercent = ?, fixedAnnualAmount = ?, country = ?, annualWorkingHours = ?, utilizationPercentage = ?, isOverHeadCost = ? WHERE id = ?";
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
            preparedStatement.setInt(9, employee.getId());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating employee failed, no rows affected.");
            }

            System.out.println("Employee with ID " + employee.getId() + " updated successfully.");
        } catch (SQLException e) {
            throw new SQLException("Error updating employee: " + e.getMessage(), e);
        }
    }

    // Method to delete an employee from the database
    public void deleteEmployee(int employeeId) throws SQLException {
        String sql = "DELETE FROM Employee WHERE id = ?";

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, employeeId);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Employee with ID " + employeeId + " not found.");
            }

            System.out.println("Employee with ID " + employeeId + " deleted successfully.");
        } catch (SQLException e) {
            throw new SQLException("Error deleting employee: " + e.getMessage());
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
               Employee employee = new Employee(employeeId, name, annualSalary, overheadMultPercent, fixedAnnualAmount, country, annualWorkingHours, utilizationPercentage, isOverHeadCost);
                employeeList.add(employee);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving employee list: " + e.getMessage(), e);
        }
        return employeeList;
    }

    public String getTeamNameByEmployeeId(int employeeId) throws SQLException {
        String sql = "SELECT teams.team_name FROM Employee " +
                "INNER JOIN teams ON Employee.team_id = teams.team_id " +
                "WHERE Employee.id = ?";
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, employeeId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("team_name");
                } else {
                    return "No Team Assigned";
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error retrieving team name for employee: " + e.getMessage(), e);
        }
    }



}