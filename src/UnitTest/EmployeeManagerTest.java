package UnitTest;

import BE.Employee;
import BLL.EmployeeManager;
import DAL.EmployeeDAO;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class EmployeeManagerTest {
    private Employee employee;
    @Mock
    private EmployeeDAO employeeDAO;

    @InjectMocks
    private EmployeeManager employeeManager;

    @Before
    public void setUp() {
        Employee employee = new Employee();
        this.employee =employee;
        employee.setId(100);
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateEmployee() throws SQLException {
        Employee newEmployee = new Employee(200, "carlos", "50000", "50", "2000", "venezuela", "1900","90", true, "9");
       when(employeeDAO.createEmployee(any(Employee.class))).thenReturn(200);
       int employeeId = employeeManager.createEmployee(newEmployee);
        assertEquals(200, employeeId);

        verify(employeeDAO).createEmployee(any(Employee.class));
    }

    @Test
    public void testDeleteEmployee() throws SQLException {
        doNothing().when(employeeDAO).deleteEmployee(100);

        employeeManager.deleteEmployee(100);

        verify(employeeDAO, times(1)).deleteEmployee(100);
    }

    @Test
    public void testUpdateEmployee() throws SQLException {
        doNothing().when(employeeDAO).updateEmployee(employee);
        employeeManager.updateEmployee(employee);
        verify(employeeDAO, times(1)).updateEmployee(employee);
    }

    @Test
    public void testGetAllEmployees() throws SQLException {
        List<Employee> employees = Arrays.asList(employee);
        when(employeeDAO.getEmployeeList()).thenReturn(employees);

        List<Employee> result = employeeManager.getAllEmployees();

        assertEquals(employees, result);
    }

    @Test
    public void testGetAllCountries() throws SQLException {
        List<String> countries = Arrays.asList("USA", "Canada", "Mexico");
        when(employeeDAO.getAllCountries()).thenReturn(countries);

        List<String> result = employeeManager.getAllCountries();

        assertEquals(countries, result);
    }

    @Test
    public void testGetEmployeesByCountry() throws SQLException {
        List<Employee> employees = Arrays.asList(employee);
        when(employeeDAO.getEmployeesByCountry("USA")).thenReturn(employees);

        List<Employee> result = employeeManager.getEmployeesByCountry("USA");

        assertEquals(employees, result);
    }

    @Test
    public void testGetTeamNames() throws SQLException {
        List<String> teamNames = Arrays.asList("Team A", "Team B");
        when(employeeDAO.getTeamNamesByEmployeeId(100)).thenReturn(teamNames);

        List<String> result = employeeManager.getTeamNames(100);

        assertEquals(teamNames, result);
    }
}
