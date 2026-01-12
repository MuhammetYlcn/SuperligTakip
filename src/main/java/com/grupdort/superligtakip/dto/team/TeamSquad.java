package com.grupdort.superligtakip.dto.team;
import com.grupdort.superligtakip.model.Player;

import java.util.List;

public class TeamSquad {
    private String coachName;
    private List<Player> goalkeepers;
    private List<Player> defenders;
    private List<Player> midfielders;
    private List<Player> forwards;

    // Constructor, Getter ve Setter'lar
    public TeamSquad(String coachName, List<Player> goalkeepers, List<Player> defenders,
                     List<Player> midfielders, List<Player> forwards) {
        this.coachName = coachName;
        this.goalkeepers = goalkeepers;
        this.defenders = defenders;
        this.midfielders = midfielders;
        this.forwards = forwards;
    }

    public String getCoachName() { return coachName; }
    public List<Player> getGoalkeepers() { return goalkeepers; }
    public List<Player> getDefenders() { return defenders; }
    public List<Player> getMidfielders() { return midfielders; }
    public List<Player> getForwards() { return forwards; }
}