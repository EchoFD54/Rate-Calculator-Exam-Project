package BE;

public class Team {
    private int teamId;
    private String teamName;
    private double teamMarkup;
    private double teamGm;


    public Team(int teamId, String teamName) {
        this.teamId = teamId;
        this.teamName = teamName;
    }

    public Team(int teamId, String teamName,  String teamMarkup, String teamGm) {
        this.teamId = teamId;
        this.teamName = teamName;
        this.teamMarkup = Double.parseDouble(teamMarkup);
        this.teamGm = Double.parseDouble(teamGm);
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

    @Override
    public String toString() {
        return teamName;
    }

    public double getTeamMarkup() {
        return teamMarkup;
    }

    public void setTeamMarkup(double teamMarkup) {
        this.teamMarkup = teamMarkup;
    }

    public double getTeamGm() {
        return teamGm;
    }

    public void setTeamGm(double teamGm) {
        this.teamGm = teamGm;
    }
}
