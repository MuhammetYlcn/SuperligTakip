package com.grupdort.superligtakip.dto.statistic;

public class MatchEventDTO {
    private int eventId;
    private int fixtureId;       // Hangi maça ait olduğu
    private int teamId;          // Olayı gerçekleştiren takımın ID'si
    private int playerId;        // Ana aktör (Atan, Kart gören, Çıkan)
    private int relatedPlayerId; // Yardımcı aktör (Asist yapan, Giren)
    private String scoreTime;    // "1st Half" veya "2nd Half"
    private String score;        // O andaki maç skoru (Örn: "2 - 1")
    private String time;         // Dakika (Örn: "90+3")
    private String type;         // "goal", "card", "substitution"
    private String info;         // Detay (Örn: "Yellow Card", "Penalty", "in: E. B. Toure")

    // Boş Constructor (Jackson ve Genel kullanım için)
    public MatchEventDTO() {
    }

    // --- GETTER VE SETTERLAR ---

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public int getFixtureId() {
        return fixtureId;
    }

    public void setFixtureId(int fixtureId) {
        this.fixtureId = fixtureId;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getRelatedPlayerId() {
        return relatedPlayerId;
    }

    public void setRelatedPlayerId(int relatedPlayerId) {
        this.relatedPlayerId = relatedPlayerId;
    }

    public String getScoreTime() {
        return scoreTime;
    }

    public void setScoreTime(String scoreTime) {
        this.scoreTime = scoreTime;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    // Debug işlemlerinde kolaylık sağlaması için toString() (Opsiyonel)
    @Override
    public String toString() {
        return "MatchEventDTO{" +
                "time='" + time + '\'' +
                ", type='" + type + '\'' +
                ", score='" + score + '\'' +
                ", info='" + info + '\'' +
                '}';
    }
}