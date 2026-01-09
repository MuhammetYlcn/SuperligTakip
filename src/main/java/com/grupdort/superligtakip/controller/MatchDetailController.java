package com.grupdort.superligtakip.controller;

import com.grupdort.superligtakip.dto.statistic.MatchFullDetailDTO;
import com.grupdort.superligtakip.model.*;
import com.grupdort.superligtakip.service.FixtureService;
import com.grupdort.superligtakip.service.MatchDetailService;
import com.grupdort.superligtakip.util.ImageCache;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MatchDetailController extends BaseController {

    @FXML private VBox mainContent, eventsContainer, statsContainer;
    @FXML private VBox paneLineups;
    @FXML private ScrollPane paneEvents, paneStats;
    @FXML private Label homeName, awayName, scoreLabel, statusLabel;

    @FXML private ImageView homeLogo;
    @FXML private ImageView awayLogo;

    @FXML private Button btnEvents, btnLineups, btnStats, btnBack;

    @FXML private Button btnLineupHome, btnLineupAway;
    @FXML private VBox containerHomeLineup, containerAwayLineup;

    @FXML private SidebarController sidebarComponentController;
    @FXML private VBox loadingOverlay;

    private final MatchDetailService service = new MatchDetailService();
    private final FixtureService fixtureService = new FixtureService();

    private int sourceWeek = -1;
    private Team sourceTeam = null;
    private Fixture preloadedFixture;

    // AKILLI NAVİGASYON İÇİN
    private int currentMatchId = -1;

    public void setPreloadedFixture(Fixture fixture) {
        this.preloadedFixture = fixture;
        updateHeader(fixture);
    }

    public void setSourceWeek(int week) {
        this.sourceWeek = week;
        this.sourceTeam = null;
    }

    public void setSourceTeam(Team team) {
        this.sourceTeam = team;
        this.sourceWeek = -1;
    }

    @FXML
    public void initialize() {
        if (sidebarComponentController != null) {
            sidebarComponentController.setMainContent(mainContent);
        }
        showEvents();
    }

    @FXML private void handleMenuButton() {
        if (sidebarComponentController != null) sidebarComponentController.toggle();
    }

    public void loadMatchData(int matchId) {
        this.currentMatchId = matchId; // ID'yi kaydet

        runAsync(loadingOverlay, () -> {
            MatchFullDetailDTO data = service.getAndProcessMatchDetails(matchId);
            Platform.runLater(() -> {
                if (data != null) {
                    Fixture fixture = data.getStatistics().getFixture();
                    ArrayList<Event> events = data.getEvents();

                    if (events != null && !events.isEmpty()) {
                        String lastRealScore = null;
                        for (Event e : events) {
                            if ("goal".equalsIgnoreCase(e.getType())) lastRealScore = e.getScore();
                        }
                        if (lastRealScore != null && lastRealScore.contains("-")) {
                            try {
                                String[] parts = lastRealScore.split("-");
                                fixture.setHome_score(Integer.parseInt(parts[0].trim()));
                                fixture.setAway_score(Integer.parseInt(parts[1].trim()));
                                if ("oynanmadi".equalsIgnoreCase(fixture.getStatus()) || "Not Started".equalsIgnoreCase(fixture.getStatus())) {
                                    fixture.setStatus("MS");
                                }
                            } catch (Exception ignored) {}
                        }
                    }

                    updateHeader(fixture);
                    updateLineups(data.getLineups(), fixture, data.getStatistics());
                    updateStats(data.getStatistics());
                    updateEvents(data.getEvents(), fixture);

                    showHomeLineup();
                } else {
                    if (statusLabel != null) statusLabel.setText("Veri Alınamadı");
                }
            });
        }, () -> {});
    }

    private void updateHeader(Fixture apiFixture) {
        Fixture f = (this.preloadedFixture != null) ? this.preloadedFixture : apiFixture;

        try {
            if (homeName != null) {
                homeName.setText(f.getHome_team().getTeam_name());
                homeName.setAlignment(Pos.CENTER);
                homeName.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
                // Tıklanabilirlik ekledik
                makeClickable(homeName, f.getHome_team());
            }

            if (awayName != null) {
                awayName.setText(f.getAway_team().getTeam_name());
                awayName.setAlignment(Pos.CENTER);
                awayName.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
                // Tıklanabilirlik ekledik
                makeClickable(awayName, f.getAway_team());
            }

            if (scoreLabel != null) scoreLabel.setText(f.getHome_score() + " - " + f.getAway_score());
            if (statusLabel != null) statusLabel.setText(f.getStatus());
            if (btnLineupHome != null) btnLineupHome.setText(f.getHome_team().getTeam_name());
            if (btnLineupAway != null) btnLineupAway.setText(f.getAway_team().getTeam_name());
        } catch (Exception e) { e.printStackTrace(); }

        if (homeLogo != null && f.getHome_team() != null) {
            loadLogoFromService(homeLogo, f.getHome_team().getTeam_ID());
            makeClickable(homeLogo, f.getHome_team());
        }

        if (awayLogo != null && f.getAway_team() != null) {
            loadLogoFromService(awayLogo, f.getAway_team().getTeam_ID());
            makeClickable(awayLogo, f.getAway_team());
        }
    }

    // LOGOYU SERVİSTEN GÜNCEL ÇEKME
    private void loadLogoFromService(ImageView imgView, int teamId) {
        runAsync(null, () -> {
            // Service üzerinden güncel logoyu al
            String url = service.getLogoUrl(teamId);
            if (url != null && !url.trim().isEmpty()) {
                Platform.runLater(() -> imgView.setImage(ImageCache.getImage(url.trim())));
            }
        }, () -> {});
    }

    // TAKIM SAYFASINA GİTME VE ID AKTARMA
    private void makeClickable(Node node, Team team) {
        node.setStyle("-fx-cursor: hand;");
        node.setOnMouseClicked(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/grupdort/superligtakip/view/TeamView/Team.fxml"));
                Parent root = loader.load();

                TeamController controller = loader.getController();
                controller.setTeamData(team);
                // Mevcut maçın ID'sini Takım sayfasına gönderiyoruz
                controller.setSourceMatchId(this.currentMatchId);

                Stage stage = (Stage) node.getScene().getWindow();
                stage.setScene(new Scene(root));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }

    private void updateEvents(ArrayList<Event> events, Fixture f) {
        eventsContainer.getChildren().clear();
        eventsContainer.getStyleClass().add("timeline-container");

        boolean halfTimeAdded = false;

        for (Event e : events) {
            int minute = parseMinute(e.getTime());

            if (!halfTimeAdded && minute > 45) {
                addHalfTimeSeparator();
                halfTimeAdded = true;
            }

            boolean isHome = e.getTeam().getTeam_ID() == f.getHome_team().getTeam_ID();
            if (e.getTeam().getTeam_ID() == 0) isHome = false;

            String detailText = "";
            String currentScore = "";

            if ("goal".equalsIgnoreCase(e.getType())) {
                currentScore = e.getScore();
                if (e.getRelatedPlayer() != null && !e.getRelatedPlayer().isEmpty()) {
                    detailText = "Asist: " + e.getRelatedPlayer();
                }

            } else if ("substitution".equalsIgnoreCase(e.getType())) {
                if (e.getRelatedPlayer() != null && !e.getRelatedPlayer().isEmpty()) {
                    detailText = "Giren: " + e.getRelatedPlayer();
                }
            } else if ("card".equalsIgnoreCase(e.getType())) {
                detailText = e.getInfo();
            }

            String playerName = (e.getPlayer() != null) ? e.getPlayer() : "";

            addEventRow(e.getTime(), playerName, e.getType(), isHome, detailText, currentScore);
        }
    }

    private void addEventRow(String time, String playerName, String type, boolean isHome, String detail, String score) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER);
        row.getStyleClass().add("event-row");
        row.setMaxWidth(Double.MAX_VALUE);
        row.setPrefWidth(Region.USE_COMPUTED_SIZE);

        HBox leftPane = new HBox(10);
        leftPane.setAlignment(Pos.CENTER_RIGHT);
        HBox.setHgrow(leftPane, Priority.ALWAYS);
        leftPane.setPrefWidth(0); leftPane.setMinWidth(0);

        StackPane centerPane = new StackPane();
        centerPane.setPrefWidth(46); centerPane.setMinWidth(46); centerPane.setMaxWidth(46);
        centerPane.setAlignment(Pos.CENTER);

        HBox rightPane = new HBox(10);
        rightPane.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(rightPane, Priority.ALWAYS);
        rightPane.setPrefWidth(0); rightPane.setMinWidth(0);

        Label lblTime = new Label(time + "'");
        lblTime.getStyleClass().add("event-time");
        lblTime.setMinWidth(55);
        lblTime.setPrefWidth(55);
        lblTime.setAlignment(Pos.CENTER);
        HBox.setHgrow(lblTime, Priority.NEVER);

        HBox nameScoreBox = new HBox(6);
        nameScoreBox.setMinWidth(0);
        nameScoreBox.setMaxWidth(Double.MAX_VALUE);

        Label lblPlayer = new Label(playerName);
        lblPlayer.getStyleClass().add("event-player");
        lblPlayer.setWrapText(false);
        lblPlayer.setMinWidth(Region.USE_PREF_SIZE);

        double fontSize = 13.0;
        int len = playerName.length();
        if (len > 25) fontSize = 9.0;
        else if (len > 20) fontSize = 10.0;
        else if (len > 15) fontSize = 11.5;
        lblPlayer.setStyle("-fx-font-size: " + fontSize + "px;");

        if (score != null && !score.isEmpty()) {
            Label lblScore = new Label(score);
            lblScore.setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold; -fx-font-size: 13px;");
            lblScore.setMinWidth(Region.USE_PREF_SIZE);
            nameScoreBox.getChildren().addAll(lblPlayer, lblScore);
        } else {
            nameScoreBox.getChildren().add(lblPlayer);
        }

        VBox textBox = new VBox(0);
        textBox.setMinWidth(0);
        textBox.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(textBox, Priority.ALWAYS);
        textBox.setTranslateY(2.5);

        textBox.getChildren().add(nameScoreBox);

        if (detail != null && !detail.isEmpty() && !detail.equals("null")) {
            Label lblDetail = new Label(detail);
            lblDetail.getStyleClass().add("event-detail");
            if (detail.length() > 25) lblDetail.setStyle("-fx-font-size: 9px;");
            lblDetail.setMinWidth(0);
            textBox.getChildren().add(lblDetail);
        }

        Node iconNode = createIcon(type, detail);
        if (iconNode != null) centerPane.getChildren().add(iconNode);

        if (isHome) {
            nameScoreBox.setAlignment(Pos.CENTER_RIGHT);
            textBox.setAlignment(Pos.CENTER_RIGHT);
            for(Node n : textBox.getChildren()) { if(n instanceof Label) ((Label)n).setAlignment(Pos.CENTER_RIGHT); }
            leftPane.getChildren().addAll(lblTime, textBox);
        } else {
            nameScoreBox.setAlignment(Pos.CENTER_LEFT);
            textBox.setAlignment(Pos.CENTER_LEFT);
            for(Node n : textBox.getChildren()) { if(n instanceof Label) ((Label)n).setAlignment(Pos.CENTER_LEFT); }
            rightPane.getChildren().addAll(textBox, lblTime);
        }

        row.getChildren().addAll(leftPane, centerPane, rightPane);
        eventsContainer.getChildren().add(row);
    }

    private Node createIcon(String type, String detail) {
        StackPane container = new StackPane();
        container.setPrefSize(32, 32);

        Circle bg = new Circle(15);
        bg.setFill(Color.web("#222222"));
        bg.setStroke(Color.web("#444444"));
        bg.setStrokeWidth(1);

        Node iconShape = null;

        if ("goal".equalsIgnoreCase(type)) {
            SVGPath ball = new SVGPath();
            ball.setContent("M12,2 C6.47,2 2,6.47 2,12 C2,17.53 6.47,22 12,22 C17.53,22 22,17.53 22,12 C22,6.47 17.53,2 12,2 Z M15.86,5.34 L13.79,9.44 L9.21,9.44 L7.14,5.34 L11.5,2.06 L15.86,5.34 Z M4.66,9.59 L8.29,11.89 L8.29,16.21 L3.61,13.56 L3.61,9.59 L4.66,9.59 Z M12,20.94 L8.89,17.34 L10.5,13.5 L13.5,13.5 L15.11,17.34 L12,20.94 Z M19.39,13.56 L15.71,16.21 L15.71,11.89 L19.34,9.59 L20.39,9.59 L20.39,13.56 Z");
            ball.setFill(Color.WHITE);
            ball.setScaleX(0.7); ball.setScaleY(0.7);
            iconShape = ball;

        } else if ("substitution".equalsIgnoreCase(type)) {
            SVGPath sub = new SVGPath();
            sub.setContent("M12,6V9L16,5L12,1V4C7.58,4 4,7.58 4,12C4,13.57 4.46,15.03 5.24,16.26L6.7,14.8C6.25,13.97 6,13.01 6,12C6,8.69 8.69,6 12,6M18.76,7.74L17.3,9.2C17.74,10.04 18,11 18,12C18,15.31 15.31,18 12,18V15L8,19L12,23V20C16.42,20 20,16.42 20,12C20,10.43 19.54,8.97 18.76,7.74Z");
            sub.setFill(Color.web("#3498db"));
            sub.setScaleX(0.7); sub.setScaleY(0.7);
            iconShape = sub;

        } else if ("card".equalsIgnoreCase(type)) {
            Rectangle card = new Rectangle(10, 14);
            card.setArcWidth(2); card.setArcHeight(2);
            if (detail != null && detail.toLowerCase().contains("yellow")) {
                card.setFill(Color.web("#f1c40f"));
            } else {
                card.setFill(Color.web("#e74c3c"));
            }
            iconShape = card;
        }

        container.getChildren().add(bg);
        if (iconShape != null) container.getChildren().add(iconShape);
        return container;
    }

    private void addHalfTimeSeparator() {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER);
        Label sep = new Label("DEVRE ARASI");
        sep.setStyle("-fx-text-fill: #888; -fx-font-weight:bold; -fx-font-size: 11px; -fx-padding: 10;");
        row.getChildren().add(sep);
        eventsContainer.getChildren().add(row);
    }

    private int parseMinute(String timeStr) { try { if (timeStr == null || timeStr.isEmpty()) return 0; String clean = timeStr.replace("'", "").trim(); if (clean.contains("+")) clean = clean.split("\\+")[0]; return Integer.parseInt(clean); } catch (Exception e) { return 0; } }

    @FXML private void showEvents() { if(paneEvents != null) paneEvents.setVisible(true); if(paneLineups != null) paneLineups.setVisible(false); if(paneStats != null) paneStats.setVisible(false); updateTabButtons(btnEvents); }
    @FXML private void showLineups() { if(paneEvents != null) paneEvents.setVisible(false); if(paneLineups != null) paneLineups.setVisible(true); if(paneStats != null) paneStats.setVisible(false); updateTabButtons(btnLineups); }
    @FXML private void showStats() { if(paneEvents != null) paneEvents.setVisible(false); if(paneLineups != null) paneLineups.setVisible(false); if(paneStats != null) paneStats.setVisible(true); updateTabButtons(btnStats); }
    private void updateTabButtons(Button activeBtn) { if(btnEvents != null) btnEvents.getStyleClass().remove("active-tab"); if(btnLineups != null) btnLineups.getStyleClass().remove("active-tab"); if(btnStats != null) btnStats.getStyleClass().remove("active-tab"); if(activeBtn != null) activeBtn.getStyleClass().add("active-tab"); }

    private void updateStats(MatchStatistic s) {
        statsContainer.getChildren().clear();

        createSuperligStatRow("Topla Oynama", "%" + s.getHome_possession(), "%" + s.getAway_possession(), s.getHome_possession(), s.getAway_possession());

        createSuperligStatRow("Baskı Yüzdesi",
                String.format("%.0f", s.getHome_pressure_index()),
                String.format("%.0f", s.getAway_pressure_index()),
                s.getHome_pressure_index(), s.getAway_pressure_index());

        createSuperligStatRow("Toplam Şut", String.valueOf(s.getHome_total_shots()), String.valueOf(s.getAway_total_shots()), s.getHome_total_shots(), s.getAway_total_shots());
        createSuperligStatRow("İsabetli Şut", String.valueOf(s.getHome_shots_on_target()), String.valueOf(s.getAway_shots_on_target()), s.getHome_shots_on_target(), s.getAway_shots_on_target());
        createSuperligStatRow("İsabetsiz Şut", String.valueOf(s.getHome_shots_off_target()), String.valueOf(s.getAway_shots_off_target()), s.getHome_shots_off_target(), s.getAway_shots_off_target());
        createSuperligStatRow("Engellenen Şut", String.valueOf(s.getHome_blocked_shots()), String.valueOf(s.getAway_blocked_shots()), s.getHome_blocked_shots(), s.getAway_blocked_shots());
        createSuperligStatRow("Kurtarış", String.valueOf(s.getHome_saves()), String.valueOf(s.getAway_saves()), s.getHome_saves(), s.getAway_saves());
        createSuperligStatRow("Toplam Pas", String.valueOf(s.getHome_total_passes()), String.valueOf(s.getAway_total_passes()), s.getHome_total_passes(), s.getAway_total_passes());
        createSuperligStatRow("İsabetli Pas", String.valueOf(s.getHome_accurate_passes()), String.valueOf(s.getAway_accurate_passes()), s.getHome_accurate_passes(), s.getAway_accurate_passes());
        createSuperligStatRow("Pas Başarısı", "%" + (int)s.getHome_pass_accuracy_perc(), "%" + (int)s.getAway_pass_accuracy_perc(), s.getHome_pass_accuracy_perc(), s.getAway_pass_accuracy_perc());
        createSuperligStatRow("Korner", String.valueOf(s.getHome_corners()), String.valueOf(s.getAway_corners()), s.getHome_corners(), s.getAway_corners());
        createSuperligStatRow("Ofsayt", String.valueOf(s.getHome_offsides()), String.valueOf(s.getAway_offsides()), s.getHome_offsides(), s.getAway_offsides());
        createSuperligStatRow("Faul", String.valueOf(s.getHome_fouls()), String.valueOf(s.getAway_fouls()), s.getHome_fouls(), s.getAway_fouls());
        createSuperligStatRow("Sarı Kart", String.valueOf(s.getHome_yellow_cards()), String.valueOf(s.getAway_yellow_cards()), s.getHome_yellow_cards(), s.getAway_yellow_cards());
        int hRed = s.getHome_direct_red_cards() + s.getHome_red_card_from_yellows();
        int aRed = s.getAway_direct_red_cards() + s.getAway_red_card_from_yellows();
        createSuperligStatRow("Kırmızı Kart", String.valueOf(hRed), String.valueOf(aRed), hRed, aRed);
    }

    private void createSuperligStatRow(String title, String homeText, String awayText, double homeVal, double awayVal) {
        double total = homeVal + awayVal; double hRatio = (total == 0) ? 0.5 : (homeVal / total); double aRatio = (total == 0) ? 0.5 : (awayVal / total);
        if (total > 0) { if (hRatio < 0.1 && hRatio > 0) hRatio = 0.1; if (aRatio < 0.1 && aRatio > 0) aRatio = 0.1; double newTotal = hRatio + aRatio; if (newTotal > 1.0) { hRatio /= newTotal; aRatio /= newTotal; } }
        Label lblTitle = new Label(title); lblTitle.setStyle("-fx-text-fill: #b3b3b3; -fx-font-size: 10px; -fx-padding: 0 0 3 0;");
        HBox container = new HBox(8); container.setAlignment(Pos.CENTER);
        Label lblH = new Label(homeText); lblH.setPrefWidth(35); lblH.setAlignment(Pos.CENTER_RIGHT); lblH.setStyle("-fx-text-fill: #3498db; -fx-font-weight: bold; -fx-font-size: 12px;");
        HBox barBox = new HBox(4); barBox.setAlignment(Pos.CENTER); HBox.setHgrow(barBox, Priority.ALWAYS);
        Region barHome = new Region(); barHome.setStyle("-fx-background-color: #3498db; -fx-background-radius: 4 0 0 4;"); barHome.setPrefHeight(6); barHome.setMaxHeight(6); HBox.setHgrow(barHome, Priority.ALWAYS); barHome.maxWidthProperty().bind(barBox.widthProperty().multiply(hRatio));
        Region barAway = new Region(); barAway.setStyle("-fx-background-color: #e74c3c; -fx-background-radius: 0 4 4 0;"); barAway.setPrefHeight(6); barAway.setMaxHeight(6); HBox.setHgrow(barAway, Priority.ALWAYS); barAway.maxWidthProperty().bind(barBox.widthProperty().multiply(aRatio));
        barBox.getChildren().addAll(barHome, barAway);
        Label lblA = new Label(awayText); lblA.setPrefWidth(35); lblA.setAlignment(Pos.CENTER_LEFT); lblA.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 12px;");
        container.getChildren().addAll(lblH, barBox, lblA);
        VBox row = new VBox(0, lblTitle, container); row.setAlignment(Pos.CENTER); row.setStyle("-fx-padding: 6 0 6 0; -fx-border-color: #222222; -fx-border-width: 0 0 1 0;");
        statsContainer.getChildren().add(row);
    }
    private void updateLineups(ArrayList<MatchLineup> lineups, Fixture f, MatchStatistic stats) {
        List<MatchLineup> homeXI = new ArrayList<>(); List<MatchLineup> homeSubs = new ArrayList<>();
        List<MatchLineup> awayXI = new ArrayList<>(); List<MatchLineup> awaySubs = new ArrayList<>();
        for (MatchLineup ml : lineups) {
            boolean isHome = ml.getTeam().getTeam_ID() == f.getHome_team().getTeam_ID();
            if (isHome) { if (ml.getIs_substitute() == 0) homeXI.add(ml); else homeSubs.add(ml); }
            else { if (ml.getIs_substitute() == 0) awayXI.add(ml); else awaySubs.add(ml); }
        }
        fillLineupContainer(containerHomeLineup, homeXI, homeSubs, stats.getHome_manager());
        fillLineupContainer(containerAwayLineup, awayXI, awaySubs, stats.getAway_manager());
    }
    private void fillLineupContainer(VBox container, List<MatchLineup> xi, List<MatchLineup> subs, String coachName) {
        container.getChildren().clear(); Label lblCoach = new Label("T.D: " + (coachName != null ? coachName : "-")); lblCoach.getStyleClass().add("coach-label"); lblCoach.setMaxWidth(Double.MAX_VALUE); lblCoach.setAlignment(Pos.CENTER); container.getChildren().add(lblCoach);
        Label lblHeaderXI = new Label("İLK 11"); lblHeaderXI.getStyleClass().add("sub-header"); lblHeaderXI.setMaxWidth(Double.MAX_VALUE); container.getChildren().add(lblHeaderXI);
        for (MatchLineup p : xi) container.getChildren().add(createPlayerRow(p));
        Label lblHeaderSubs = new Label("YEDEKLER"); lblHeaderSubs.getStyleClass().add("sub-header"); lblHeaderSubs.setMaxWidth(Double.MAX_VALUE); VBox.setMargin(lblHeaderSubs, new javafx.geometry.Insets(10, 0, 0, 0)); container.getChildren().add(lblHeaderSubs);
        for (MatchLineup p : subs) container.getChildren().add(createPlayerRow(p));
    }
    private HBox createPlayerRow(MatchLineup p) { HBox row = new HBox(10); row.getStyleClass().add("player-row"); row.setAlignment(Pos.CENTER_LEFT); Label lblNum = new Label(p.getNumber() > 0 ? String.valueOf(p.getNumber()) : "-"); lblNum.setStyle("-fx-text-fill: #b3b3b3; -fx-font-weight: bold; -fx-min-width: 25; -fx-alignment: CENTER_RIGHT;"); Label lblName = new Label(p.getPlayer().getPlayer_name()); lblName.setStyle("-fx-text-fill: white; -fx-font-size: 13px;"); row.getChildren().addAll(lblNum, lblName); return row; }
    @FXML private void showHomeLineup() { if(containerHomeLineup == null) return; containerHomeLineup.setVisible(true); containerHomeLineup.setManaged(true); containerAwayLineup.setVisible(false); containerAwayLineup.setManaged(false); updateToggleButtons(btnLineupHome, btnLineupAway); }
    @FXML private void showAwayLineup() { if(containerAwayLineup == null) return; containerAwayLineup.setVisible(true); containerAwayLineup.setManaged(true); containerHomeLineup.setVisible(false); containerHomeLineup.setManaged(false); updateToggleButtons(btnLineupAway, btnLineupHome); }
    private void updateToggleButtons(Button active, Button inactive) { if (active == null || inactive == null) return; active.getStyleClass().removeAll("lineup-toggle-inactive"); active.getStyleClass().add("lineup-toggle-active"); inactive.getStyleClass().removeAll("lineup-toggle-active"); inactive.getStyleClass().add("lineup-toggle-inactive"); }

    @FXML private void handleBack() {
        try {
            if (sourceTeam != null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/grupdort/superligtakip/view/TeamView/Team.fxml"));
                Parent root = loader.load();
                TeamController controller = loader.getController();
                controller.setTeamData(sourceTeam);
                Stage stage = (Stage) btnBack.getScene().getWindow();
                stage.setScene(new Scene(root));
                return;
            }
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/grupdort/superligtakip/view/FixtureView/Fixture.fxml"));
            Parent root = loader.load();
            if (sourceWeek != -1) {
                FixtureController controller = loader.getController();
                controller.setTargetWeek(sourceWeek);
            }
            Stage stage = (Stage) btnBack.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) { e.printStackTrace(); }
    }
}