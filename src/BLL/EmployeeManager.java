package BLL;

import BE.Employee;
import DAL.EmployeeDAO;

import java.sql.SQLException;

public class EmployeeManager {
    EmployeeDAO employeeDAO = new EmployeeDAO();

    public int createEmployee(Employee e) throws SQLException {
        return employeeDAO.createEmployee(e);
    }

    public void deleteEmployee(int employeeId) throws SQLException {
        employeeDAO.deleteEmployee(employeeId);
    }
}
