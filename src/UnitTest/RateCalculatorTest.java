package UnitTest;

import BE.Employee;
import BE.EmployeeInTeam;
import GUI.model.Model;
import GUI.model.RateCalculator;
import org.junit.Test;
import org.junit.Before;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class RateCalculatorTest {
    private Employee employee1;
    private Employee employee2;
    private EmployeeInTeam employeeInTeam1;
    private EmployeeInTeam employeeInTeam2;

    @Mock
    private Model model;

    @InjectMocks
    private RateCalculator rateCalculator;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        Employee employee1 = new Employee();
        this.employee1 = employee1;
        employee1.setAnnualSalary(50000);
        employee1.setFixedAnnualAmount(2000);
        employee1.setAnnualWorkingHours(2000);
        employee1.setOverheadMultPercent(0);
        employee1.setUtilizationPercentage(80);
        employee1.setDailyHours(8);
        Employee employee2 = new Employee();
        this.employee2 = employee2;
        employee2.setAnnualSalary(15000);
        employee2.setFixedAnnualAmount(0);
        employee2.setAnnualWorkingHours(1890);
        employee2.setOverheadMultPercent(15);
        employee2.setUtilizationPercentage(100);
        employee2.setDailyHours(8);

        EmployeeInTeam employeeInTeam1 = new EmployeeInTeam(employee1, 7, 50);
        this.employeeInTeam1 = employeeInTeam1;
        EmployeeInTeam employeeInTeam2 = new EmployeeInTeam(employee2, 1, 10);
        this.employeeInTeam2 = employeeInTeam2;
    }

    @Test
    public void testCalculateHourlyRate() {
        double expectedRate = 20.8;
        double actualRate = rateCalculator.calculateHourlyRate(employee1);

        assertEquals(expectedRate, actualRate, 0.01);
    }

    @Test
    public void testCalculateTeamDailyRate() throws SQLException {
        List<EmployeeInTeam> employeesInTeam = Arrays.asList(employeeInTeam1, employeeInTeam2);
        when(model.getEmployeesInTeamFromDB(1042)).thenReturn(employeesInTeam);
        double expectedDailyRate = 154.73;
        double actualDailyRate = rateCalculator.calculateTeamDailyRate(1042);
        assertEquals(expectedDailyRate, actualDailyRate, 0.01);
    }

    @Test
    public void testCalculateTeamDailyRateWithMultipliers() throws SQLException {
        List<EmployeeInTeam> employeesInTeam = Arrays.asList(employeeInTeam1, employeeInTeam2);
        when(model.getEmployeesInTeamFromDB(1042)).thenReturn(employeesInTeam);
        double markup = 20;
        double gm = 50;
        double expectedDailyRate = 371.35;
        double actualDailyRate = rateCalculator.calculateTeamDailyRateWithMultipliers(1042, markup, gm);
        assertEquals(expectedDailyRate, actualDailyRate, 0.01);
    }

@Test
    public void testCalculateTeamHourlyRate() throws SQLException {
        List<EmployeeInTeam> employeesInTeam = Arrays.asList(employeeInTeam1, employeeInTeam2);
        when(model.getEmployeesInTeamFromDB(1042)).thenReturn(employeesInTeam);
        double expectedHourlyRate = 19.34;
        double actualHourlyRate = rateCalculator.calculateTeamHourlyRate(1042);
        assertEquals(expectedHourlyRate, actualHourlyRate, 0.01);
    }

    @Test
    public void testCalculateTeamHourlyRateWithMultipliers() throws SQLException {
        List<EmployeeInTeam> employeesInTeam = Arrays.asList(employeeInTeam1, employeeInTeam2);
        when(model.getEmployeesInTeamFromDB(1042)).thenReturn(employeesInTeam);
        double markup = 20;
        double gm = 50;
        double expectedHourlyRate = 46.42;
        double actualHourlyRate = rateCalculator.calculateTeamHourlyRateWithMultipliers(1042, markup, gm);
        assertEquals(expectedHourlyRate, actualHourlyRate, 0.01);
    }

    @Test
    public void testApplyMarkup() {
        double rate = 100.0;
        double markupPercentage = 20.0;

        double expectedRate = 120.0;
        double actualRate = rateCalculator.applyMarkup(rate, markupPercentage);

        assertEquals(expectedRate, actualRate, 0.01);
    }

    @Test
    public void testApplyGrossMargin() {
        double rate = 100.0;
        double grossMarginPercentage = 20.0;

        double expectedRate = 125.0;
        double actualRate = rateCalculator.applyGrossMargin(rate, grossMarginPercentage);

        assertEquals(expectedRate, actualRate, 0.01);
    }

    @Test
    public void testCalculateEmployeeCost() {
        Employee employee = new Employee();
        employee.setAnnualSalary(60000);
        employee.setFixedAnnualAmount(5000);
        employee.setOverheadMultPercent(20);

        double expectedCost = 78000.0;
        double actualCost = rateCalculator.calculateEmployeeCost(employee);

        assertEquals(expectedCost, actualCost, 0.01);
    }

    @Test
    public void testCalculateEmployeeRevenue() {
        Employee employee = new Employee();
        employee.setAnnualSalary(60000);
        employee.setFixedAnnualAmount(2000);
        employee.setAnnualWorkingHours(2080);
        employee.setOverheadMultPercent(0);
        employee.setUtilizationPercentage(80);
        employee.setDailyHours(8);

        double expectedRevenue = 49608;
        double actualRevenue = rateCalculator.calculateEmployeeRevenue(employee);

        assertEquals(expectedRevenue, actualRevenue, 0.01);
    }

    @Test
    public void testCalculateTeamCost() {
        List<EmployeeInTeam> employeesInTeam = Arrays.asList(employeeInTeam1, employeeInTeam2);

        double expectedCost = 27725;
        double actualCost = rateCalculator.calculateTeamCost(employeesInTeam);

        assertEquals(expectedCost, actualCost, 0.01);
    }

    @Test
    public void testCalculateTeamCostWithMultipliers() {
        List<EmployeeInTeam> employeesInTeam = Arrays.asList(employeeInTeam1, employeeInTeam2);
        double markup = 20;
        double expectedCost = 33270;
        double actualCost = rateCalculator.calculateTeamCostWithMarkup(employeesInTeam,markup);
        assertEquals(expectedCost, actualCost, 0.01);
    }

    @Test
    public void testCalculateTeamRevenue() {
        List<EmployeeInTeam> employeesInTeam = Arrays.asList(employeeInTeam1, employeeInTeam2);
        double expectedRevenue = 38556.96;
        double actualRevenue = rateCalculator.calculateTeamRevenueWithoutMultipliers(employeesInTeam);
        assertEquals(expectedRevenue, actualRevenue, 0.01);
    }

    @Test
    public void testCalculateTeamRevenueWithMultipliers() {
    List<EmployeeInTeam> employeesInTeam = Arrays.asList(employeeInTeam1, employeeInTeam2);
    double gm = 50;
    double expectedRevenue = 77113.92;
    double actualRevenue = rateCalculator.calculateTeamRevenueWithGM(employeesInTeam, gm);
    assertEquals(expectedRevenue, actualRevenue, 0.01);
    }


}
