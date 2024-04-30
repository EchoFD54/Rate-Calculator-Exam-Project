package BE;

public class Team {
    private int TeamId;
    private String name;

    public Team(int TeamId, String name) {
        this.TeamId = TeamId;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTeamId() {
        return TeamId;
    }

    public void setTeamId(int teamId) {
        TeamId = teamId;
    }
}
