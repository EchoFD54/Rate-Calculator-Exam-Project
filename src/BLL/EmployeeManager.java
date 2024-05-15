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

    public void updateEmployee(Employee e) throws SQLException {
        employeeDAO.updateEmployee(e);
    }

    public void deleteEmployee(int employeeId) throws SQLException {
        employeeDAO.deleteEmployee(employeeId);
    }

    public List<Employee> getAllEmployees() throws SQLException {
        return employeeDAO.getEmployeeList();
    }

    public List<String> getTeamNames(int employeeId) throws SQLException {
        return employeeDAO.getTeamNamesByEmployeeId(employeeId);
    }

    public List<String> getAllCountries() throws SQLException {
        return employeeDAO.getAllCountries();
    }

    public List<Employee> getEmployeesByCountry(String country) throws SQLException {
        return employeeDAO.getEmployeesByCountry(country);
    }
}
