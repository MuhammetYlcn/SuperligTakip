package com.grupdort.superligtakip.dto.statistic;

import com.grupdort.superligtakip.model.Event;
import com.grupdort.superligtakip.model.MatchLineup;
import com.grupdort.superligtakip.model.MatchStatistic;

import java.util.ArrayList;

public class MatchFullDetailDTO {
    private MatchStatistic statistics;
    private ArrayList<Event> events;
    private ArrayList<MatchLineup> lineups;

    public MatchFullDetailDTO(MatchStatistic statistics, ArrayList<Event> events, ArrayList<MatchLineup> lineups) {
        this.statistics = statistics;
        this.events = events;
        this.lineups = lineups;
    }

    // GETTERS
    public MatchStatistic getStatistics() { return statistics; }
    public ArrayList<Event> getEvents() { return events; }
    public ArrayList<MatchLineup> getLineups() { return lineups; }

    // SETTERS
    public void setStatistics(MatchStatistic statistics) { this.statistics = statistics; }
    public void setEvents(ArrayList<Event> events) { this.events = events; }
    public void setLineups(ArrayList<MatchLineup> lineups) { this.lineups = lineups; }
}