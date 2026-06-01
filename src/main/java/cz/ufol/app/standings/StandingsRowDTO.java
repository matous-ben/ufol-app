package cz.ufol.app.standings;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StandingsRowDTO {
    private String teamName;
    private Long teamId;
    private int played;
    private int wins;
    private int draws;
    private int losses;
    private int goalsFor;
    private int goalsAgainst;
    private int points;
    private String logoFile;

    public int getGoalDifference() {
        return goalsFor - goalsAgainst;
    }

    public String getScore() {
        return goalsFor + ":" + goalsAgainst;
    }
}
