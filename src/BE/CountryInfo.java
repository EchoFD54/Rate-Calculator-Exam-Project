package BE;

public class CountryInfo {
    private String countryName;
    private String employeesInCountry;
    private Double countryDailyRate;

    public CountryInfo(String countryName, String employeesInCountry, Double countryDailyRate) {
        this.countryName = countryName;
        this.employeesInCountry = employeesInCountry;
        this.countryDailyRate = countryDailyRate;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getEmployeesInCountry() {
        return employeesInCountry;
    }

    public void setEmployeesInCountry(String employeesInCountry) {
        this.employeesInCountry = employeesInCountry;
    }

    public Double getCountryDailyRate() {
        return countryDailyRate;
    }

    public void setCountryDailyRate(Double countryDailyRate) {
        this.countryDailyRate = countryDailyRate;
    }
}
