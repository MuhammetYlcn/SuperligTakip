package com.grupdort.superligtakip.controller;

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
    @FXML private VBox loadingOverlay;
    @FXML private Button btnBack;

    private Team currentTeam;
    private final FixtureService fixtureService = new FixtureService();
    private final TeamService teamService = new TeamService();

    private int selectedSeasonYear = 2025;

    @FXML
    public void initialize() {
        tabMatches.setOnAction(e -> { if (tabMatches.isSelected()) loadMatches(); });
        tabSquad.setOnAction(e -> { if (tabSquad.isSelected()) loadSquad(); });
        setupSeasonComboBox();
    }

    public void setTeamData(Team team) {
        this.currentTeam = team;
        lblTeamName.setText(team.getTeam_name());
        try {
            if (team.getLogo() != null) imgTeamLogo.setImage(ImageCache.getImage(team.getLogo()));
        } catch (Exception e) {}

        tabMatches.setSelected(true);
        loadMatches();
    }

    // =========================================================================
    // 1. FİKSTÜR YÜKLEME
    // =========================================================================
    private void loadMatches() {
        if (currentTeam == null) return;
        contentContainer.getChildren().clear();

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
            matchCard.setOnMouseClicked(e -> openMatchDetail(f.getFixture_ID()));

            String dateStr = (f.getMatch_date() != null) ? f.getMatch_date().format(dateFormatter) : "-";
            Label lblDate = new Label(dateStr);
            lblDate.getStyleClass().add("date-label");
            lblDate.setMinWidth(45);
            lblDate.setPrefWidth(45);
            lblDate.setMaxWidth(45);
            lblDate.setAlignment(Pos.CENTER);

            HBox homeBox = createTeamBox(f.getHome_team().getTeam_name(), f.getHome_team().getLogo(), true);
            HBox.setHgrow(homeBox, Priority.ALWAYS);

            String scoreText = "-";
            if ("Finished".equalsIgnoreCase(f.getStatus()) || "FT".equalsIgnoreCase(f.getStatus()) ||
                    (f.getStatus() != null && f.getStatus().contains(":"))) {
                scoreText = f.getHome_score() + " - " + f.getAway_score();
            } else if (f.getFinal_result() != null) {
                scoreText = f.getFinal_result();
            }
            Label lblScore = new Label(scoreText);
            lblScore.getStyleClass().add("score-label");

            HBox awayBox = createTeamBox(f.getAway_team().getTeam_name(), f.getAway_team().getLogo(), false);
            HBox.setHgrow(awayBox, Priority.ALWAYS);

            Button starBtn = createStarButton();
            starBtn.setOnMouseClicked(e -> e.consume()); // Yıldız detaya gitmesin

            matchCard.getChildren().addAll(lblDate, homeBox, lblScore, awayBox, starBtn);
            contentContainer.getChildren().add(matchCard);
        }
    }

    private void openMatchDetail(int matchId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/grupdort/superligtakip/view/MatchDetailView/MatchDetail.fxml"));
            Parent root = loader.load();

            MatchDetailController controller = loader.getController();
            controller.loadMatchData(matchId);

            // "Ben bu takımdan geliyorum" diye bildiriyoruz
            controller.setSourceTeam(this.currentTeam);

            Stage stage = (Stage) lblTeamName.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) { e.printStackTrace(); }
    }
    // ------------------------------------------------

    // =========================================================================
    // 2. KADRO YÜKLEME
    // =========================================================================
    private void loadSquad() {
        if (currentTeam == null) return;
        contentContainer.getChildren().clear();

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

        addSectionHeader("Teknik Direktör");
        addPlayerRow(squad.getCoachName(), "TD");

        if (!squad.getGoalkeepers().isEmpty()) {
            addSectionHeader("Kaleciler");
            for (Player p : squad.getGoalkeepers()) addPlayerRow(p.getPlayer_name(), "KL");
        }
        if (!squad.getDefenders().isEmpty()) {
            addSectionHeader("Defans");
            for (Player p : squad.getDefenders()) addPlayerRow(p.getPlayer_name(), "DF");
        }
        if (!squad.getMidfielders().isEmpty()) {
            addSectionHeader("Orta Saha");
            for (Player p : squad.getMidfielders()) addPlayerRow(p.getPlayer_name(), "OS");
        }
        if (!squad.getForwards().isEmpty()) {
            addSectionHeader("Forvet");
            for (Player p : squad.getForwards()) addPlayerRow(p.getPlayer_name(), "FV");
        }
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
        VBox.setMargin(lbl, new javafx.geometry.Insets(15, 0, 5, 0));
        contentContainer.getChildren().add(lbl);
    }

    private void addPlayerRow(String name, String posShort) {
        if (name == null || name.isEmpty()) return;
        HBox row = new HBox(10);
        row.getStyleClass().add("player-row");
        row.setAlignment(Pos.CENTER_LEFT);
        Label lblPos = new Label(posShort);
        lblPos.getStyleClass().add("player-pos-circle");
        Label lblName = new Label(name);
        lblName.getStyleClass().add("player-name");
        row.getChildren().addAll(lblPos, lblName);
        contentContainer.getChildren().add(row);
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/grupdort/superligtakip/view/StandingView/Standing.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) lblTeamName.getScene().getWindow();
            stage.setScene(new Scene(root));
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