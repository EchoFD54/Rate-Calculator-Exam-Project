package DAL;

import BE.Employee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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



}