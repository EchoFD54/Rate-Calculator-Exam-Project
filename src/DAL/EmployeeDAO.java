package DAL;

import BE.Employee;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import javafx.fxml.FXML;

import java.awt.*;
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
        String sql = "INSERT INTO Employee (name, annualSalary, overheadMultPercent, fixedAnnualAmount, country, employeeTeam, annualWorkingHours, utilizationPercentage, isOverHeadCost) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, employee.getName());
            preparedStatement.setInt(2, employee.getAnnualSalary());
            preparedStatement.setInt(3, employee.getOverheadMultPercent());
            preparedStatement.setInt(4, employee.getFixedAnnualAmount());
            preparedStatement.setString(5, employee.getCountry());
            preparedStatement.setString(6, employee.getEmployeeTeam());
            preparedStatement.setInt(7, employee.getAnnualWorkingHours());
            preparedStatement.setInt(8, employee.getUtilizationPercentage());
            preparedStatement.setBoolean(9, employee.isOverHeadCost());

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
        String sql = "UPDATE Employee SET name = ?, annualSalary = ?, overheadMultPercent = ?, fixedAnnualAmount = ?, country = ?, employeeTeam = ?, annualWorkingHours = ?, utilizationPercentage = ?, isOverHeadCost = ? WHERE id = ?";
        try (Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, employee.getName());
            preparedStatement.setInt(2, employee.getAnnualSalary());
            preparedStatement.setInt(3, employee.getOverheadMultPercent());
            preparedStatement.setInt(4, employee.getFixedAnnualAmount());
            preparedStatement.setString(5, employee.getCountry());
            preparedStatement.setString(6, employee.getEmployeeTeam());
            preparedStatement.setInt(7, employee.getAnnualWorkingHours());
            preparedStatement.setInt(8, employee.getUtilizationPercentage());
            preparedStatement.setBoolean(9, employee.isOverHeadCost());
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
               String annualSalary = String.valueOf(resultSet.getInt("annualSalary"));
               String overheadMultPercent = String.valueOf(resultSet.getInt("overheadMultPercent"));
               String fixedAnnualAmount = String.valueOf(resultSet.getInt("fixedAnnualAmount"));
               String country = resultSet.getString("country");
               String employeeTeam = String.valueOf(resultSet.getString("employeeTeam"));
               String annualWorkingHours = String.valueOf(resultSet.getInt("annualWorkingHours"));
               String utilizationPercentage = String.valueOf(resultSet.getInt("utilizationPercentage"));
               boolean isOverHeadCost = resultSet.getBoolean("isOverHeadCost");
               Employee employee = new Employee(employeeId, name, annualSalary, overheadMultPercent, fixedAnnualAmount, country, employeeTeam, annualWorkingHours, utilizationPercentage, isOverHeadCost);
                employeeList.add(employee);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving employee list: " + e.getMessage(), e);
        }
        return employeeList;
    }



}