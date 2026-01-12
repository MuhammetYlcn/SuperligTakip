package com.grupdort.superligtakip.model;

public class MatchStatistic {
    private Fixture fixture;
    private int home_possession;
    private int away_possession;
    private double home_pressure_index;
    private double away_pressure_index;
    private int home_total_shots;
    private int away_total_shots;
    private int home_shots_on_target;
    private int away_shots_on_target;
    private int home_shots_off_target;
    private int away_shots_off_target;
    private int home_blocked_shots;
    private int away_blocked_shots;
    private int home_saves;
    private int away_saves;
    private int home_total_passes;
    private int away_total_passes;
    private int home_accurate_passes;
    private int away_accurate_passes;
    private double home_pass_accuracy_perc;
    private double away_pass_accuracy_perc;
    private int home_corners;
    private int away_corners;
    private int home_offsides;
    private int away_offsides;
    private int home_fouls;
    private int away_fouls;
    private int home_yellow_cards;
    private int away_yellow_cards;
    private int home_red_card_from_yellows;
    private int away_red_card_from_yellows;
    private int home_direct_red_cards;
    private int away_direct_red_cards;
    private String home_manager;
    private String away_manager;

    public MatchStatistic(){
        this.home_possession=0;
        this.away_possession=0;
        this.home_pressure_index=0.0;
        this.away_pressure_index=0.0;
        this.home_total_shots=0;
        this.away_total_shots=0;
        this.home_shots_on_target=0;
        this.away_shots_on_target=0;
        this.home_shots_off_target=0;
        this.away_shots_off_target=0;
        this.home_blocked_shots=0;
        this.away_blocked_shots=0;
        this.home_saves=0;
        this.away_saves=0;
        this.home_total_passes=0;
        this.away_total_passes=0;
        this.home_accurate_passes=0;
        this.away_accurate_passes=0;
        this.home_pass_accuracy_perc=0.0;
        this.away_pass_accuracy_perc=0.0;
        this.home_corners=0;
        this.away_corners=0;
        this.home_offsides=0;
        this.away_offsides=0;
        this.home_fouls=0;
        this.away_fouls=0;
        this.home_yellow_cards=0;
        this.away_yellow_cards=0;
        this.home_red_card_from_yellows=0;
        this.away_red_card_from_yellows=0;
        this.home_direct_red_cards=0;
        this.away_direct_red_cards=0;
    }
    public MatchStatistic(Fixture fixture,int home_possession,int away_possession,double home_pressure_index,double away_pressure_index,int home_total_shots,int away_total_shots,int home_shots_on_target,int away_shots_on_target,int home_shots_off_target,int away_shots_off_target,int home_blocked_shots,int away_blocked_shots,int home_saves,int away_saves,int home_total_passes,int away_total_passes,int home_accurate_passes,int away_accurate_passes,double home_pass_accuracy_perc,double away_pass_accuracy_perc,int home_corners,int away_corners,int home_offsides,int away_offsides,int home_fouls,int away_fouls,int home_yellow_cards,int away_yellow_cards,int home_red_card_from_yellows,int away_red_card_from_yellows,int home_direct_red_cards,int away_direct_red_cards,String home_manager,String away_manager){
        this.fixture=fixture;
        this.home_possession=home_possession;
        this.away_possession=away_possession;
        this.home_pressure_index=home_pressure_index;
        this.away_pressure_index=away_pressure_index;
        this.home_total_shots=home_total_shots;
        this.away_total_shots=away_total_shots;
        this.home_shots_on_target=home_shots_on_target;
        this.away_shots_on_target=away_shots_on_target;
        this.home_shots_off_target=home_shots_off_target;
        this.away_shots_off_target=away_shots_off_target;
        this.home_blocked_shots=home_blocked_shots;
        this.away_blocked_shots=away_blocked_shots;
        this.home_saves=home_saves;
        this.away_saves=away_saves;
        this.home_total_passes=home_total_passes;
        this.away_total_passes=away_total_passes;
        this.home_accurate_passes=home_accurate_passes;
        this.away_accurate_passes=away_accurate_passes;
        this.home_pass_accuracy_perc=home_pass_accuracy_perc;
        this.away_pass_accuracy_perc=away_pass_accuracy_perc;
        this.home_corners=home_corners;
        this.away_corners=away_corners;
        this.home_offsides=home_offsides;
        this.away_offsides=away_offsides;
        this.home_fouls=home_fouls;
        this.away_fouls=away_fouls;
        this.home_yellow_cards=home_yellow_cards;
        this.away_yellow_cards=away_yellow_cards;
        this.home_red_card_from_yellows=home_red_card_from_yellows;
        this.away_red_card_from_yellows=away_red_card_from_yellows;
        this.home_direct_red_cards=home_direct_red_cards;
        this.away_direct_red_cards=away_direct_red_cards;
        this.home_manager=home_manager;
        this.away_manager=away_manager;

    }

    public Fixture getFixture() {
        return fixture;
    }
    public void setFixture(Fixture fixture) {
        this.fixture = fixture;
    }
    public int getHome_possession() {
        return home_possession;
    }
    public void setHome_possession(int home_possession) {
        this.home_possession = home_possession;
    }
    public int getAway_possession() {
        return away_possession;
    }
    public void setAway_possession(int away_possession) {
        this.away_possession = away_possession;
    }

    public double getHome_pressure_index() {
        return home_pressure_index;
    }
    public void setHome_pressure_index(double home_pressure_index) {
        this.home_pressure_index = home_pressure_index;
    }
    public double getAway_pressure_index() {
        return away_pressure_index;
    }
    public void setAway_pressure_index(double away_pressure_index) {
        this.away_pressure_index = away_pressure_index;
    }

    public int getHome_total_shots() {
        return home_total_shots;
    }
    public void setHome_total_shots(int home_total_shots) {
        this.home_total_shots = home_total_shots;
    }
    public int getAway_total_shots() {
        return away_total_shots;
    }
    public void setAway_total_shots(int away_total_shots) {
        this.away_total_shots = away_total_shots;
    }
    public int getHome_shots_on_target() {
        return home_shots_on_target;
    }
    public void setHome_shots_on_target(int home_shots_on_target) {
        this.home_shots_on_target = home_shots_on_target;
    }
    public int getAway_shots_on_target() {
        return away_shots_on_target;
    }
    public void setAway_shots_on_target(int away_shots_on_target) {
        this.away_shots_on_target = away_shots_on_target;
    }
    public int getHome_shots_off_target() {
        return home_shots_off_target;
    }
    public void setHome_shots_off_target(int home_shots_off_target) {
        this.home_shots_off_target = home_shots_off_target;
    }
    public int getAway_shots_off_target() {
        return away_shots_off_target;
    }
    public void setAway_shots_off_target(int away_shots_off_target) {
        this.away_shots_off_target = away_shots_off_target;
    }
    public int getHome_blocked_shots() {
        return home_blocked_shots;
    }
    public void setHome_blocked_shots(int home_blocked_shots) {
        this.home_blocked_shots = home_blocked_shots;
    }
    public int getAway_blocked_shots() {
        return away_blocked_shots;
    }
    public void setAway_blocked_shots(int away_blocked_shots) {
        this.away_blocked_shots = away_blocked_shots;
    }

    public int getHome_saves() {
        return home_saves;
    }

    public void setHome_saves(int home_saves) {
        this.home_saves = home_saves;
    }

    public int getAway_saves() {
        return away_saves;
    }

    public void setAway_saves(int away_saves) {
        this.away_saves = away_saves;
    }

    public int getHome_total_passes() {
        return home_total_passes;
    }

    public void setHome_total_passes(int home_total_passes) {
        this.home_total_passes = home_total_passes;
        hesaplaHome_pass_accuracy_perc();
    }

    public int getAway_total_passes() {
        return away_total_passes;
    }

    public void setAway_total_passes(int away_total_passes) {
        this.away_total_passes = away_total_passes;
        hesaplaAway_pass_accuracy_perc();
    }

    public int getHome_accurate_passes() {
        return home_accurate_passes;
    }

    public void setHome_accurate_passes(int home_accurate_passes) {
        this.home_accurate_passes = home_accurate_passes;
        hesaplaHome_pass_accuracy_perc();
    }

    public int getAway_accurate_passes() {
        return away_accurate_passes;
    }

    public void setAway_accurate_passes(int away_accurate_passes) {
        this.away_accurate_passes = away_accurate_passes;
        hesaplaAway_pass_accuracy_perc();
    }

    public double getHome_pass_accuracy_perc() {
        return home_pass_accuracy_perc;
    }
    public void hesaplaHome_pass_accuracy_perc(){
        this.home_pass_accuracy_perc=((double) this.home_accurate_passes / this.home_total_passes)*100;
    }

    public double getAway_pass_accuracy_perc() {
        return away_pass_accuracy_perc;
    }
    public void hesaplaAway_pass_accuracy_perc(){
        this.away_pass_accuracy_perc=((double) this.away_accurate_passes / this.away_total_passes)*100;
    }

    public int getHome_corners() {
        return home_corners;
    }
    public void setHome_corners(int home_corners) {
        this.home_corners = home_corners;
    }
    public int getAway_corners() {
        return away_corners;
    }
    public void setAway_corners(int away_corners) {
        this.away_corners = away_corners;
    }
    public int getHome_offsides() {
        return home_offsides;
    }
    public void setHome_offsides(int home_offsides) {
        this.home_offsides = home_offsides;
    }
    public int getAway_offsides() {
        return away_offsides;
    }
    public void setAway_offsides(int away_offsides) {
        this.away_offsides = away_offsides;
    }
    public int getHome_fouls() {
        return home_fouls;
    }
    public void setHome_fouls(int home_fouls) {
        this.home_fouls = home_fouls;
    }
    public int getAway_fouls() {
        return away_fouls;
    }
    public void setAway_fouls(int away_fouls) {
        this.away_fouls = away_fouls;
    }
    public int getHome_yellow_cards() {
        return home_yellow_cards;
    }
    public void setHome_yellow_cards(int home_yellow_cards) {
        this.home_yellow_cards = home_yellow_cards;
    }
    public int getAway_yellow_cards() {
        return away_yellow_cards;
    }
    public void setAway_yellow_cards(int away_yellow_cards) {
        this.away_yellow_cards = away_yellow_cards;
    }
    public int getHome_red_card_from_yellows() {
        return home_red_card_from_yellows;
    }
    public void setHome_red_card_from_yellows(int home_red_card_from_yellows) {
        this.home_red_card_from_yellows = home_red_card_from_yellows;
    }
    public int getAway_red_card_from_yellows() {
        return away_red_card_from_yellows;
    }
    public void setAway_red_card_from_yellows(int away_red_card_from_yellows) {
        this.away_red_card_from_yellows = away_red_card_from_yellows;
    }
    public int getHome_direct_red_cards() {
        return home_direct_red_cards;
    }
    public void setHome_direct_red_cards(int home_direct_red_cards) {
        this.home_direct_red_cards = home_direct_red_cards;
    }
    public int getAway_direct_red_cards() {
        return away_direct_red_cards;
    }
    public void setAway_direct_red_cards(int away_direct_red_cards) {
        this.away_direct_red_cards = away_direct_red_cards;
    }

    public String getHome_manager() {
        return home_manager;
    }

    public void setHome_manager(String home_manager) {
        this.home_manager = home_manager;
    }

    public String getAway_manager() {
        return away_manager;
    }

    public void setAway_manager(String away_manager) {
        this.away_manager = away_manager;
    }

    @Override
    public String toString() {
        return "MatchStatistic Detail:\n" +
                "--------------------------------------------------\n" +
                "Fixture ID: " + (fixture != null ? fixture.getFixture_ID() : "N/A") + "\n" +
                "General: \n" +
                "  Possession: Home %" + home_possession + " - Away %" + away_possession + "\n" +
                "  Pressure Index: Home " + home_pressure_index + " - Away " + away_pressure_index + "\n" +
                "Shots: \n" +
                "  Total Shots: " + home_total_shots + " - " + away_total_shots + "\n" +
                "  Shots On Target: " + home_shots_on_target + " - " + away_shots_on_target + "\n" +
                "  Shots Off Target: " + home_shots_off_target + " - " + away_shots_off_target + "\n" +
                "  Blocked Shots: " + home_blocked_shots + " - " + away_blocked_shots + "\n" +
                "  Saves: " + home_saves + " - " + away_saves + "\n" +
                "Passes: \n" +
                "  Total Passes: " + home_total_passes + " - " + away_total_passes + "\n" +
                "  Accurate Passes: " + home_accurate_passes + " - " + away_accurate_passes + "\n" +
                "  Pass Accuracy: %" + String.format("%.2f", home_pass_accuracy_perc) + " - %" + String.format("%.2f", away_pass_accuracy_perc) + "\n" +
                "Standard Stats: \n" +
                "  Corners: " + home_corners + " - " + away_corners + "\n" +
                "  Offsides: " + home_offsides + " - " + away_offsides + "\n" +
                "  Fouls: " + home_fouls + " - " + away_fouls + "\n" +
                "Discipline: \n" +
                "  Yellow Cards: " + home_yellow_cards + " - " + away_yellow_cards + "\n" +
                "  Red from Yellow: " + home_red_card_from_yellows + " - " + away_red_card_from_yellows + "\n" +
                "  Direct Red: " + home_direct_red_cards + " - " + away_direct_red_cards + "\n" +
                "--------------------------------------------------" +
                "Managers: h "+home_manager+" a "+away_manager;

    }
}
