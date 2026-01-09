package com.grupdort.superligtakip.controller;

import com.grupdort.superligtakip.dao.TeamDAO;
import com.grupdort.superligtakip.dto.team.TeamSquad;
import com.grupdort.superligtakip.model.Fixture;
import com.grupdort.superligtakip.model.Player;
import com.grupdort.superligtakip.model.Team;
import com.grupdort.superligtakip.service.FixtureService;
import com.grupdort.superligtakip.service.TeamService;
import com.grupdort.superligtakip.util.ImageCache;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class TeamController extends BaseController {

    @FXML private ImageView imgTeamLogo;
    @FXML private Label lblTeamName;
    @FXML private ComboBox<String> seasonComboBox;
    @FXML private ToggleButton tabMatches;
    @FXML private ToggleButton tabSquad;

    @FXML private VBox contentContainer;
    @FXML private VBox fixedHeaderContainer;

    @FXML private VBox loadingOverlay;
    @FXML private Button btnBack;

    private Team currentTeam;
    private final FixtureService fixtureService = new FixtureService();
    private final TeamService teamService = new TeamService();
    private final TeamDAO teamDAO = new TeamDAO();

    private int selectedSeasonYear = 2025;
    private int sourceMatchId = -1;

    public void setSourceMatchId(int matchId) {
        this.sourceMatchId = matchId;
    }

    @FXML
    public void initialize() {
        tabMatches.setOnAction(e -> { if (tabMatches.isSelected()) loadMatches(); });
        tabSquad.setOnAction(e -> { if (tabSquad.isSelected()) loadSquad(); });
        setupSeasonComboBox();
    }

    public void setTeamData(Team team) {
        Team fullDataTeam = teamDAO.getTeamById(team.getTeam_ID());
        if (fullDataTeam != null) {
            this.currentTeam = fullDataTeam;
        } else {
            this.currentTeam = team;
        }

        lblTeamName.setText(this.currentTeam.getTeam_name());

        try {
            if (this.currentTeam.getLogo() != null && !this.currentTeam.getLogo().isEmpty()) {
                imgTeamLogo.setImage(ImageCache.getImage(this.currentTeam.getLogo()));
            }
        } catch (Exception e) {}

        tabMatches.setSelected(true);
        loadMatches();
    }
    // FİKSTÜR YÜKLEME
    private void loadMatches() {
        if (currentTeam == null) return;
        contentContainer.getChildren().clear();
        if (fixedHeaderContainer != null) fixedHeaderContainer.getChildren().clear();

        runAsync(loadingOverlay,
                () -> {
                    List<Fixture> allMatches = fixtureService.getFixturesByTeam(currentTeam.getTeam_ID());
                    List<Fixture> filteredMatches = allMatches;

                    if (allMatches != null) {
                        filteredMatches = allMatches.stream()
                                .filter(f -> {
                                    if (f.getWeek() != null && f.getWeek().getSeason() != null) {
                                        try {
                                            String seasonName = f.getWeek().getSeason().getSeason_name();
                                            int startYear = Integer.parseInt(seasonName.split("/")[0]);
                                            return startYear == selectedSeasonYear;
                                        } catch (Exception e) { return true; }
                                    }
                                    return true;
                                })
                                .sorted(Comparator.comparing(Fixture::getMatch_date))
                                .collect(Collectors.toList());
                    }

                    final List<Fixture> finalMatches = filteredMatches;
                    Platform.runLater(() -> renderMatches(finalMatches));
                },
                () -> {}
        );
    }

    private void renderMatches(List<Fixture> matches) {
        if (matches == null || matches.isEmpty()) {
            addStandardWarning("Bu sezona ait maç verisi bulunamadı.");
            return;
        }

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d MMM", new Locale("tr"));

        for (Fixture f : matches) {
            HBox matchCard = new HBox();
            matchCard.getStyleClass().add("match-card");
            matchCard.setAlignment(Pos.CENTER);
            matchCard.setSpacing(5);
            matchCard.setPrefHeight(45);
            matchCard.setStyle("-fx-cursor: hand;");

            matchCard.setOnMouseClicked(e -> openMatchDetail(f));

            String dateStr = (f.getMatch_date() != null) ? f.getMatch_date().format(dateFormatter) : "-";
            Label lblDate = new Label(dateStr);
            lblDate.getStyleClass().add("date-label");
            lblDate.setMinWidth(50);
            lblDate.setPrefWidth(50);
            lblDate.setMaxWidth(50);
            lblDate.setAlignment(Pos.CENTER);

            HBox homeBox = createTeamBox(f.getHome_team().getTeam_name(), f.getHome_team().getLogo(), true);
            HBox.setHgrow(homeBox, Priority.ALWAYS);
            homeBox.setPrefWidth(1);

            String scoreText = "-";
            if ("Finished".equalsIgnoreCase(f.getStatus()) || "FT".equalsIgnoreCase(f.getStatus()) ||
                    (f.getStatus() != null && f.getStatus().contains(":"))) {
                scoreText = f.getHome_score() + " - " + f.getAway_score();
            } else if (f.getFinal_result() != null) {
                scoreText = f.getFinal_result();
            }
            Label lblScore = new Label(scoreText);
            lblScore.getStyleClass().add("score-label");
            lblScore.setMinWidth(40);
            lblScore.setPrefWidth(40);
            lblScore.setAlignment(Pos.CENTER);

            HBox awayBox = createTeamBox(f.getAway_team().getTeam_name(), f.getAway_team().getLogo(), false);
            HBox.setHgrow(awayBox, Priority.ALWAYS);
            awayBox.setPrefWidth(1);

            Button starBtn = createStarButton();
            starBtn.setOnMouseClicked(e -> e.consume());

            matchCard.getChildren().addAll(lblDate, homeBox, lblScore, awayBox, starBtn);
            contentContainer.getChildren().add(matchCard);
        }
    }

    private void openMatchDetail(Fixture f) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/grupdort/superligtakip/view/MatchDetailView/MatchDetail.fxml"));
            Parent root = loader.load();
            MatchDetailController controller = loader.getController();
            controller.setPreloadedFixture(f);
            controller.loadMatchData(f.getFixture_ID());
            controller.setSourceTeam(this.currentTeam);
            Stage stage = (Stage) lblTeamName.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) { e.printStackTrace(); }
    }

    // KADRO YÜKLEME
    private void loadSquad() {
        if (currentTeam == null) return;
        contentContainer.getChildren().clear();
        if (fixedHeaderContainer != null) fixedHeaderContainer.getChildren().clear();

        if (selectedSeasonYear != 2025) {
            addStandardWarning("Bu yıla ait kadro kaydı yok.");
            return;
        }

        runAsync(loadingOverlay,
                () -> {
                    TeamSquad squad = teamService.getTeamSquadSorted(currentTeam.getTeam_ID());
                    Platform.runLater(() -> renderSquad(squad));
                },
                () -> {}
        );
    }

    private void renderSquad(TeamSquad squad) {
        if (squad == null) {
            addStandardWarning("Kadro bilgisi bulunamadı.");
            return;
        }

        if (fixedHeaderContainer != null) {
            fixedHeaderContainer.getChildren().add(createSquadHeaderRow());
        }

        if (!squad.getGoalkeepers().isEmpty()) {
            addSectionHeader("Kaleciler");
            for (Player p : squad.getGoalkeepers()) addPlayerRow(p, "KL");
        }
        if (!squad.getDefenders().isEmpty()) {
            addSectionHeader("Defans");
            for (Player p : squad.getDefenders()) addPlayerRow(p, "DF");
        }
        if (!squad.getMidfielders().isEmpty()) {
            addSectionHeader("Orta Saha");
            for (Player p : squad.getMidfielders()) addPlayerRow(p, "OS");
        }
        if (!squad.getForwards().isEmpty()) {
            addSectionHeader("Forvet");
            for (Player p : squad.getForwards()) addPlayerRow(p, "FV");
        }

        addSectionHeader("Teknik Direktör");
        if (squad.getCoachName() != null) {
            HBox coachRow = new HBox(5);
            coachRow.getStyleClass().add("player-row");
            coachRow.setAlignment(Pos.CENTER_LEFT);
            Label lblPos = new Label("TD");
            lblPos.getStyleClass().add("player-pos-circle");
            Label lblName = new Label(squad.getCoachName());
            lblName.getStyleClass().add("player-name");
            coachRow.getChildren().addAll(lblPos, lblName);
            contentContainer.getChildren().add(coachRow);
        }
    }

    // SIKIŞTIRILMIŞ HEADER METODU
    private HBox createSquadHeaderRow() {
        HBox row = new HBox(2); // Aralıklar sıkılaştırıldı (5 -> 2)
        // Sağ padding azaltıldı (28 -> 10) çünkü scrollbar yok
        row.setPadding(new javafx.geometry.Insets(0, 10, 0, 10));
        row.setAlignment(Pos.CENTER_LEFT);

        Label lblPlaceholder = new Label("");
        lblPlaceholder.setMinWidth(25);
        lblPlaceholder.setPrefWidth(25);
        lblPlaceholder.setMaxWidth(25);
        row.getChildren().add(lblPlaceholder);

        Label lblName = new Label("OYUNCU");
        lblName.setStyle("-fx-text-fill: #666; -fx-font-size: 9px; -fx-font-weight: bold;");
        lblName.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(lblName, Priority.ALWAYS);

        row.getChildren().add(lblName);

        row.getChildren().addAll(
                createHeaderLabel("YŞ", 24),
                createHeaderLabel("MÇ", 24),
                createHeaderLabel("GL", 24),
                createHeaderLabel("AS", 24),
                createHeaderLabel("SK", 24),
                createHeaderLabel("KK", 24),
                createHeaderLabel("RT", 32)
        );

        return row;
    }

    private Label createHeaderLabel(String text, double width) {
        Label lbl = new Label(text);
        lbl.setPrefWidth(width);
        lbl.setMinWidth(width);
        lbl.setAlignment(Pos.CENTER);
        lbl.setStyle("-fx-text-fill: #666; -fx-font-size: 9px; -fx-font-weight: bold;");
        return lbl;
    }

    // SIKIŞTIRILMIŞ SATIR METODU
    private void addPlayerRow(Player p, String posShort) {
        if (p == null) return;

        HBox row = new HBox(2);
        row.getStyleClass().add("player-row");
        // Paddingler CSS'te ama burda garanti olsun diye
        row.setPadding(new javafx.geometry.Insets(4, 8, 4, 8));
        row.setAlignment(Pos.CENTER_LEFT);

        Label lblPos = new Label(posShort);
        lblPos.getStyleClass().add("player-pos-circle");
        lblPos.setMinWidth(25);
        lblPos.setPrefWidth(25);
        lblPos.setAlignment(Pos.CENTER);

        Label lblName = new Label(p.getPlayer_name());
        lblName.getStyleClass().add("player-name");
        lblName.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(lblName, Priority.ALWAYS);

        Label lblAge = createStatLabel(String.valueOf(p.getAge()), 24, "stat-normal");
        Label lblApp = createStatLabel(String.valueOf(p.getAppearances()), 24, "stat-highlight");

        String goalClass = (p.getGoal() > 0) ? "stat-goal" : "stat-normal";
        Label lblGoal = createStatLabel(String.valueOf(p.getGoal()), 24, goalClass);

        Label lblAssist = createStatLabel(String.valueOf(p.getAssist()), 24, "stat-assist");
        Label lblYellow = createStatLabel(String.valueOf(p.getYellow_card()), 24, "stat-yellow");
        Label lblRed = createStatLabel(String.valueOf(p.getRed_card()), 24, "stat-red");

        String rateStr = String.format(Locale.US, "%.1f", p.getTotal_rating());
        if (p.getTotal_rating() == 0) rateStr = "-";

        Label lblRate = createStatLabel(rateStr, 32, "stat-rating");

        row.getChildren().addAll(lblPos, lblName, lblAge, lblApp, lblGoal, lblAssist, lblYellow, lblRed, lblRate);
        contentContainer.getChildren().add(row);
    }

    private Label createStatLabel(String text, double width, String styleClass) {
        Label lbl = new Label(text);
        lbl.setPrefWidth(width);
        lbl.setMinWidth(width);
        lbl.setAlignment(Pos.CENTER);
        lbl.getStyleClass().add(styleClass);
        return lbl;
    }

    private void addStandardWarning(String message) {
        Label lbl = new Label(message);
        lbl.getStyleClass().add("no-data-label");
        lbl.setMaxWidth(Double.MAX_VALUE);
        lbl.setAlignment(Pos.CENTER);
        contentContainer.getChildren().add(lbl);
    }

    private void addSectionHeader(String title) {
        Label lbl = new Label(title);
        lbl.getStyleClass().add("squad-header");
        // Başlık boşluğu azaltıldı
        VBox.setMargin(lbl, new javafx.geometry.Insets(10, 0, 2, 0));
        contentContainer.getChildren().add(lbl);
    }

    private HBox createTeamBox(String name, String logoUrl, boolean isHome) {
        HBox box = new HBox(5);
        box.setAlignment(isHome ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        box.setMaxWidth(Double.MAX_VALUE);
        box.setMinWidth(0);

        Label nameLabel = new Label(name);
        nameLabel.getStyleClass().add("team-name-label");
        nameLabel.setWrapText(false);
        nameLabel.setEllipsisString("..");
        nameLabel.setAlignment(isHome ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        HBox.setHgrow(nameLabel, Priority.ALWAYS);

        ImageView logo = new ImageView();
        logo.setFitWidth(20);
        logo.setFitHeight(20);
        logo.setPreserveRatio(true);
        try { if (logoUrl != null) logo.setImage(ImageCache.getImage(logoUrl)); } catch (Exception e) {}

        if (isHome) box.getChildren().addAll(nameLabel, logo);
        else box.getChildren().addAll(logo, nameLabel);
        return box;
    }

    private Button createStarButton() {
        Button btn = new Button();
        btn.getStyleClass().add("star-button");
        SVGPath star = new SVGPath();
        star.setContent("M12 17.27L18.18 21l-1.64-7.03L22 9.24l-7.19-.61L12 2 9.19 8.63 2 9.24l5.46 4.73L5.82 21z");
        star.setFill(Color.TRANSPARENT);
        star.setStroke(Color.web("#666666"));
        star.setStrokeWidth(1.5);
        star.setScaleX(0.8);
        star.setScaleY(0.8);
        btn.setGraphic(star);
        btn.setOnAction(e -> {
            if (star.getFill() == Color.TRANSPARENT) {
                star.setFill(Color.GOLD);
                star.setStroke(Color.GOLD);
            } else {
                star.setFill(Color.TRANSPARENT);
                star.setStroke(Color.web("#666666"));
            }
        });
        return btn;
    }

    @FXML
    private void handleBack() {
        try {
            if (sourceMatchId != -1) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/grupdort/superligtakip/view/MatchDetailView/MatchDetail.fxml"));
                Parent root = loader.load();
                MatchDetailController controller = loader.getController();
                controller.loadMatchData(sourceMatchId);
                Stage stage = (Stage) btnBack.getScene().getWindow();
                stage.setScene(new Scene(root));
            }
            else {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/grupdort/superligtakip/view/StandingView/Standing.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) lblTeamName.getScene().getWindow();
                stage.setScene(new Scene(root));
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void setupSeasonComboBox() {
        if (seasonComboBox == null) return;
        seasonComboBox.getItems().clear();
        for (int startYear = 2025; startYear >= 2000; startYear--) {
            int endYear = startYear + 1;
            seasonComboBox.getItems().add(startYear + "/" + endYear);
        }
        seasonComboBox.getSelectionModel().selectFirst();
        seasonComboBox.setOnAction(e -> {
            String selectedSeason = seasonComboBox.getSelectionModel().getSelectedItem();
            if (selectedSeason != null) {
                try {
                    selectedSeasonYear = Integer.parseInt(selectedSeason.split("/")[0]);
                    if (tabMatches.isSelected()) loadMatches();
                    else if (tabSquad.isSelected()) loadSquad();
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        });
    }
}