package com.grupdort.superligtakip.controller;

import com.grupdort.superligtakip.dto.statistic.MatchFullDetailDTO;
import com.grupdort.superligtakip.model.*;
import com.grupdort.superligtakip.service.MatchDetailService;
import com.grupdort.superligtakip.util.ImageCache;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;

public class MatchDetailController extends BaseController {

    @FXML private VBox mainContent, eventsContainer, statsContainer;
    @FXML private VBox paneLineups;
    @FXML private ScrollPane paneEvents, paneStats;
    @FXML private Label homeName, awayName, scoreLabel, statusLabel;
    @FXML private ImageView homeLogo, awayLogo;
    @FXML private Button btnEvents, btnLineups, btnStats, btnBack, menuButton;

    @FXML private Button btnLineupHome, btnLineupAway;
    @FXML private VBox containerHomeLineup, containerAwayLineup;
    @FXML private ScrollPane scrollLineups;

    @FXML private SidebarController sidebarComponentController;
    @FXML private AnchorPane menuOverlay;
    @FXML private VBox loadingOverlay;

    private final MatchDetailService service = new MatchDetailService();

    private int currentMatchId;

    // --- NAVİGASYON HAFIZASI ---
    private int sourceWeek = -1;    // Fikstürden gelindiyse hafta no
    private Team sourceTeam = null; // Takım sayfasından gelindiyse takım objesi

    public void setSourceWeek(int week) {
        this.sourceWeek = week;
        this.sourceTeam = null; // Fikstürden geldiysek takımı unut
    }

    public void setSourceTeam(Team team) {
        this.sourceTeam = team;
        this.sourceWeek = -1; // Takımdan geldiysek haftayı unut
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
        this.currentMatchId = matchId;
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
                    statusLabel.setText("Veri Alınamadı");
                }
            });
        }, () -> {});
    }

    private void updateHeader(Fixture f) {
        homeName.setText(f.getHome_team().getTeam_name());
        awayName.setText(f.getAway_team().getTeam_name());
        scoreLabel.setText(f.getHome_score() + " - " + f.getAway_score());
        statusLabel.setText(f.getStatus());

        if(btnLineupHome != null) btnLineupHome.setText(f.getHome_team().getTeam_name());
        if(btnLineupAway != null) btnLineupAway.setText(f.getAway_team().getTeam_name());

        try {
            String dbHomeLogo = null;
            try { dbHomeLogo = service.getLogoUrl(f.getHome_team().getTeam_ID()); } catch (Exception e) {}
            if (dbHomeLogo != null && !dbHomeLogo.isEmpty()) {
                homeLogo.setImage(ImageCache.getImage(dbHomeLogo));
            } else {
                String hUrl = f.getHome_team().getLogo();
                if (hUrl != null && !hUrl.isEmpty()) homeLogo.setImage(ImageCache.getImage(hUrl));
            }

            String dbAwayLogo = null;
            try { dbAwayLogo = service.getLogoUrl(f.getAway_team().getTeam_ID()); } catch (Exception e) {}
            if (dbAwayLogo != null && !dbAwayLogo.isEmpty()) {
                awayLogo.setImage(ImageCache.getImage(dbAwayLogo));
            } else {
                String aUrl = f.getAway_team().getLogo();
                if (aUrl != null && !aUrl.isEmpty()) awayLogo.setImage(ImageCache.getImage(aUrl));
            }
        } catch (Exception ignored) {}
    }

    private void updateStats(MatchStatistic s) {
        statsContainer.getChildren().clear();
        createMackolikStatRow("Topla Oynama", "%" + s.getHome_possession(), "%" + s.getAway_possession(), s.getHome_possession(), s.getAway_possession());
        createMackolikStatRow("Toplam Şut", String.valueOf(s.getHome_total_shots()), String.valueOf(s.getAway_total_shots()), s.getHome_total_shots(), s.getAway_total_shots());
        createMackolikStatRow("İsabetli Şut", String.valueOf(s.getHome_shots_on_target()), String.valueOf(s.getAway_shots_on_target()), s.getHome_shots_on_target(), s.getAway_shots_on_target());
        createMackolikStatRow("Pas %", "%" + (int)s.getHome_pass_accuracy_perc(), "%" + (int)s.getAway_pass_accuracy_perc(), s.getHome_pass_accuracy_perc(), s.getAway_pass_accuracy_perc());
        createMackolikStatRow("Toplam Pas", String.valueOf(s.getHome_total_passes()), String.valueOf(s.getAway_total_passes()), s.getHome_total_passes(), s.getAway_total_passes());
        createMackolikStatRow("Korner", String.valueOf(s.getHome_corners()), String.valueOf(s.getAway_corners()), s.getHome_corners(), s.getAway_corners());
        createMackolikStatRow("Ofsayt", String.valueOf(s.getHome_offsides()), String.valueOf(s.getAway_offsides()), s.getHome_offsides(), s.getAway_offsides());
        createMackolikStatRow("Faul", String.valueOf(s.getHome_fouls()), String.valueOf(s.getAway_fouls()), s.getHome_fouls(), s.getAway_fouls());
        createMackolikStatRow("Sarı Kart", String.valueOf(s.getHome_yellow_cards()), String.valueOf(s.getAway_yellow_cards()), s.getHome_yellow_cards(), s.getAway_yellow_cards());
        int hRed = s.getHome_direct_red_cards() + s.getHome_red_card_from_yellows();
        int aRed = s.getAway_direct_red_cards() + s.getAway_red_card_from_yellows();
        if (hRed > 0 || aRed > 0) {
            createMackolikStatRow("Kırmızı Kart", String.valueOf(hRed), String.valueOf(aRed), hRed, aRed);
        }
    }

    private void createMackolikStatRow(String title, String homeText, String awayText, double homeVal, double awayVal) {
        double total = homeVal + awayVal;
        double hRatio = (total == 0) ? 0.5 : (homeVal / total);
        double aRatio = (total == 0) ? 0.5 : (awayVal / total);
        if (total > 0) {
            if (hRatio < 0.1 && hRatio > 0) hRatio = 0.1;
            if (aRatio < 0.1 && aRatio > 0) aRatio = 0.1;
            double newTotal = hRatio + aRatio;
            if (newTotal > 1.0) { hRatio /= newTotal; aRatio /= newTotal; }
        }
        Label lblTitle = new Label(title);
        lblTitle.setStyle("-fx-text-fill: #b3b3b3; -fx-font-size: 10px; -fx-padding: 0 0 3 0;");
        HBox container = new HBox(8);
        container.setAlignment(Pos.CENTER);
        Label lblH = new Label(homeText);
        lblH.setPrefWidth(35);
        lblH.setAlignment(Pos.CENTER_RIGHT);
        lblH.setStyle("-fx-text-fill: #3498db; -fx-font-weight: bold; -fx-font-size: 12px;");
        HBox barBox = new HBox(4);
        barBox.setAlignment(Pos.CENTER);
        HBox.setHgrow(barBox, Priority.ALWAYS);
        Region barHome = new Region();
        barHome.setStyle("-fx-background-color: #3498db; -fx-background-radius: 4 0 0 4;");
        barHome.setPrefHeight(6); barHome.setMaxHeight(6);
        HBox.setHgrow(barHome, Priority.ALWAYS);
        barHome.maxWidthProperty().bind(barBox.widthProperty().multiply(hRatio));
        Region barAway = new Region();
        barAway.setStyle("-fx-background-color: #e74c3c; -fx-background-radius: 0 4 4 0;");
        barAway.setPrefHeight(6); barAway.setMaxHeight(6);
        HBox.setHgrow(barAway, Priority.ALWAYS);
        barAway.maxWidthProperty().bind(barBox.widthProperty().multiply(aRatio));
        barBox.getChildren().addAll(barHome, barAway);
        Label lblA = new Label(awayText);
        lblA.setPrefWidth(35);
        lblA.setAlignment(Pos.CENTER_LEFT);
        lblA.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 12px;");
        container.getChildren().addAll(lblH, barBox, lblA);
        VBox row = new VBox(0, lblTitle, container);
        row.setAlignment(Pos.CENTER);
        row.setStyle("-fx-padding: 6 0 6 0; -fx-border-color: #222222; -fx-border-width: 0 0 1 0;");
        statsContainer.getChildren().add(row);
    }

    private void updateLineups(ArrayList<MatchLineup> lineups, Fixture f, MatchStatistic stats) {
        List<MatchLineup> homeXI = new ArrayList<>();
        List<MatchLineup> homeSubs = new ArrayList<>();
        List<MatchLineup> awayXI = new ArrayList<>();
        List<MatchLineup> awaySubs = new ArrayList<>();
        for (MatchLineup ml : lineups) {
            boolean isHome = ml.getTeam().getTeam_ID() == f.getHome_team().getTeam_ID();
            if (isHome) { if (ml.getIs_substitute() == 0) homeXI.add(ml); else homeSubs.add(ml); }
            else { if (ml.getIs_substitute() == 0) awayXI.add(ml); else awaySubs.add(ml); }
        }
        fillLineupContainer(containerHomeLineup, homeXI, homeSubs, stats.getHome_manager());
        fillLineupContainer(containerAwayLineup, awayXI, awaySubs, stats.getAway_manager());
    }

    private void fillLineupContainer(VBox container, List<MatchLineup> xi, List<MatchLineup> subs, String coachName) {
        container.getChildren().clear();
        Label lblCoach = new Label("T.D: " + (coachName != null ? coachName : "-"));
        lblCoach.getStyleClass().add("coach-label");
        lblCoach.setMaxWidth(Double.MAX_VALUE);
        lblCoach.setAlignment(Pos.CENTER);
        container.getChildren().add(lblCoach);
        Label lblHeaderXI = new Label("İLK 11");
        lblHeaderXI.getStyleClass().add("sub-header");
        lblHeaderXI.setMaxWidth(Double.MAX_VALUE);
        container.getChildren().add(lblHeaderXI);
        for (MatchLineup p : xi) container.getChildren().add(createPlayerRow(p));
        Label lblHeaderSubs = new Label("YEDEKLER");
        lblHeaderSubs.getStyleClass().add("sub-header");
        lblHeaderSubs.setMaxWidth(Double.MAX_VALUE);
        VBox.setMargin(lblHeaderSubs, new javafx.geometry.Insets(10, 0, 0, 0));
        container.getChildren().add(lblHeaderSubs);
        for (MatchLineup p : subs) container.getChildren().add(createPlayerRow(p));
    }

    private HBox createPlayerRow(MatchLineup p) {
        HBox row = new HBox(10);
        row.getStyleClass().add("player-row");
        row.setAlignment(Pos.CENTER_LEFT);
        Label lblNum = new Label(p.getNumber() > 0 ? String.valueOf(p.getNumber()) : "-");
        lblNum.setStyle("-fx-text-fill: #b3b3b3; -fx-font-weight: bold; -fx-min-width: 25; -fx-alignment: CENTER_RIGHT;");
        Label lblName = new Label(p.getPlayer().getPlayer_name());
        lblName.setStyle("-fx-text-fill: white; -fx-font-size: 13px;");
        row.getChildren().addAll(lblNum, lblName);
        return row;
    }

    @FXML private void showHomeLineup() { if(containerHomeLineup == null) return; containerHomeLineup.setVisible(true); containerHomeLineup.setManaged(true); containerAwayLineup.setVisible(false); containerAwayLineup.setManaged(false); updateToggleButtons(btnLineupHome, btnLineupAway); }
    @FXML private void showAwayLineup() { if(containerAwayLineup == null) return; containerAwayLineup.setVisible(true); containerAwayLineup.setManaged(true); containerHomeLineup.setVisible(false); containerHomeLineup.setManaged(false); updateToggleButtons(btnLineupAway, btnLineupHome); }
    private void updateToggleButtons(Button active, Button inactive) { if (active == null || inactive == null) return; active.getStyleClass().removeAll("lineup-toggle-inactive"); active.getStyleClass().add("lineup-toggle-active"); inactive.getStyleClass().removeAll("lineup-toggle-active"); inactive.getStyleClass().add("lineup-toggle-inactive"); }
    @FXML private void showEvents() { if(paneEvents != null) paneEvents.setVisible(true); if(paneLineups != null) paneLineups.setVisible(false); if(paneStats != null) paneStats.setVisible(false); updateTabButtons(btnEvents); }
    @FXML private void showLineups() { if(paneEvents != null) paneEvents.setVisible(false); if(paneLineups != null) paneLineups.setVisible(true); if(paneStats != null) paneStats.setVisible(false); updateTabButtons(btnLineups); }
    @FXML private void showStats() { if(paneEvents != null) paneEvents.setVisible(false); if(paneLineups != null) paneLineups.setVisible(false); if(paneStats != null) paneStats.setVisible(true); updateTabButtons(btnStats); }
    private void updateTabButtons(Button activeBtn) { if(btnEvents != null) btnEvents.getStyleClass().remove("active-tab"); if(btnLineups != null) btnLineups.getStyleClass().remove("active-tab"); if(btnStats != null) btnStats.getStyleClass().remove("active-tab"); if(activeBtn != null) activeBtn.getStyleClass().add("active-tab"); }

    private void updateEvents(ArrayList<Event> events, Fixture f) {
        eventsContainer.getChildren().clear();
        boolean halfTimeAdded = false;

        for (Event e : events) {
            int minute = parseMinute(e.getTime());

            // Devre arası çizgisi
            if (!halfTimeAdded && minute > 45) {
                addHalfTimeSeparator();
                halfTimeAdded = true;
            }

            // Ev sahibi mi Deplasman mı kontrolü
            boolean isHome = e.getTeam().getTeam_ID() == f.getHome_team().getTeam_ID();
            // Eğer takım ID 0 ise (yabancı takım) ve ev sahibi ID'siyle eşleşmiyorsa deplasman kabul et
            if (e.getTeam().getTeam_ID() == 0) isHome = false;

            // --- DETAY METNİ MANTIĞI (GÜNCELLENDİ) ---
            String detailText = e.getInfo(); // Varsayılan (Kartlar vs. için)

            if ("goal".equalsIgnoreCase(e.getType())) {
                detailText = e.getScore();
            }
            else if ("substitution".equalsIgnoreCase(e.getType())) {
                // Info yerine RelatedPlayer (Çıkan Oyuncu) verisini kullanıyoruz
                if (e.getRelatedPlayer() != null && e.getRelatedPlayer().getPlayer_name() != null && !e.getRelatedPlayer().getPlayer_name().isEmpty()) {
                    detailText = "Çıkan: " + e.getRelatedPlayer().getPlayer_name();
                } else {
                    // Eğer relatedPlayer boşsa info'dan temizlemeyi dene veya boş bırak
                    detailText = "";
                }
            }
            // ----------------------------------------

            addTimelineEvent(e.getTime(), e.getPlayer().getPlayer_name(), e.getType(), isHome, detailText);
        }
    }

    private void addHalfTimeSeparator() {
        Line topLine = new Line(0, 0, 0, 15); topLine.setStroke(Color.web("#333333")); topLine.setStrokeWidth(2);
        Label lblText = new Label("DEVRE ARASI"); lblText.setStyle("-fx-text-fill: #b3b3b3; -fx-font-weight: bold; -fx-font-size: 10px;");
        StackPane pill = new StackPane(lblText); pill.setStyle("-fx-background-color: #1f1f1f; -fx-background-radius: 15; -fx-border-color: #333333; -fx-border-radius: 15; -fx-padding: 5 15 5 15;"); pill.setMaxWidth(Region.USE_PREF_SIZE);
        Line bottomLine = new Line(0, 0, 0, 15); bottomLine.setStroke(Color.web("#333333")); bottomLine.setStrokeWidth(2);
        VBox container = new VBox(0, topLine, pill, bottomLine); container.setAlignment(Pos.CENTER);
        eventsContainer.getChildren().add(container);
    }

    private int parseMinute(String timeStr) { try { if (timeStr == null || timeStr.isEmpty()) return 0; String clean = timeStr.replace("'", "").trim(); if (clean.contains("+")) clean = clean.split("\\+")[0]; return Integer.parseInt(clean); } catch (Exception e) { return 0; } }

    private void addTimelineEvent(String time, String player, String type, boolean isHome, String detail) {
        HBox row = new HBox(); row.setAlignment(Pos.CENTER); row.setSpacing(10);
        StackPane minuteNode = new StackPane();
        Circle c = new Circle(12, Color.web("#333333")); c.setStroke(Color.web("#555555"));
        Label lblTime = new Label(time + "'"); lblTime.setStyle("-fx-text-fill: white; -fx-font-size: 10px; -fx-font-weight: bold;");
        minuteNode.getChildren().addAll(c, lblTime);
        VBox contentBox = new VBox(); contentBox.getStyleClass().add("event-card");
        Label lblPlayer = new Label(player); lblPlayer.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        String titleText = mapEventType(type);
        if (detail != null && !detail.isEmpty() && !detail.equals("null")) { if ("goal".equalsIgnoreCase(type)) titleText += "  " + detail; else titleText += " (" + detail + ")"; }
        Label lblInfo = new Label(titleText); lblInfo.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 10px;");
        contentBox.getChildren().addAll(lblPlayer, lblInfo);
        if (isHome) { contentBox.setAlignment(Pos.CENTER_RIGHT); row.getChildren().addAll(contentBox, minuteNode, new Region()); HBox.setHgrow(row.getChildren().get(2), Priority.ALWAYS); HBox.setHgrow(contentBox, Priority.ALWAYS); }
        else { contentBox.setAlignment(Pos.CENTER_LEFT); row.getChildren().addAll(new Region(), minuteNode, contentBox); HBox.setHgrow(row.getChildren().get(0), Priority.ALWAYS); HBox.setHgrow(contentBox, Priority.ALWAYS); }
        eventsContainer.getChildren().add(row);
        Line connector = new Line(0, 0, 0, 15); connector.setStroke(Color.web("#333333")); connector.setStrokeWidth(2);
        eventsContainer.getChildren().add(connector);
    }

    private String mapEventType(String apiType) { if (apiType == null) return ""; switch (apiType.toLowerCase()) { case "goal": return "GOL ⚽"; case "card": return "KART 🟨🟥"; case "substitution": return "OYUNCU DEĞİŞİKLİĞİ 🔄"; default: return apiType; } }

    // --- AKILLI GERİ TUŞU ---
    @FXML
    private void handleBack() {
        try {
            // 1. TAKIM SAYFASINA DÖNÜŞ (Eğer Takımdan Geldiyse)
            if (sourceTeam != null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/grupdort/superligtakip/view/TeamView/Team.fxml"));
                Parent root = loader.load();
                TeamController controller = loader.getController();
                controller.setTeamData(sourceTeam); // Takım bilgisini geri yükle
                Stage stage = (Stage) btnBack.getScene().getWindow();
                stage.setScene(new Scene(root));
                return;
            }

            // 2. FİKSTÜR SAYFASINA DÖNÜŞ (Varsayılan veya Fikstürden Geldiyse)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/grupdort/superligtakip/view/FixtureView/Fixture.fxml"));
            Parent root = loader.load();
            if (sourceWeek != -1) {
                FixtureController controller = loader.getController();
                controller.setTargetWeek(sourceWeek); // Hafta bilgisini geri yükle
            }
            Stage stage = (Stage) btnBack.getScene().getWindow();
            stage.setScene(new Scene(root));

        } catch (Exception e) { e.printStackTrace(); }
    }
}