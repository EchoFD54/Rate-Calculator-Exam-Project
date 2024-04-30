package BLL;

import BE.Employee;
import DAL.EmployeeDAO;

import java.sql.SQLException;
import java.util.List;

public class EmployeeManager {
    EmployeeDAO employeeDAO = new EmployeeDAO();

    public int createEmployee(Employee e) throws SQLException {
        return employeeDAO.createEmployee(e);
    }

    public void deleteEmployee(int employeeId) throws SQLException {
        employeeDAO.deleteEmployee(employeeId);
    }

    public List<Employee> getAllEmployees() throws SQLException {
        return employeeDAO.getEmployeeList();
    }
}
