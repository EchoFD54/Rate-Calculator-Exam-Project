package BE;

public class Team {
    private int teamId;
    private String teamName;
    private double teamDailyRate;

    public Team(int teamId, String teamName) {
        this.teamId = teamId;
        this.teamName = teamName;
    }

    public Team(String teamName) {
        this.teamName = teamName;
    }

    public Team() {

    }

    public String getName() {
        return teamName;
    }

    public void setName(String teamName) {
        this.teamName = teamName;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public double getTeamDailyRate() {
        return teamDailyRate;
    }

    public void setTeamDailyRate(double dailyRate) {
        this.teamDailyRate = dailyRate;
    }

    @Override
    public String toString() {
        return teamName;
    }
}
