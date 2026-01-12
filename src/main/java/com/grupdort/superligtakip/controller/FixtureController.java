package com.grupdort.superligtakip.controller;

import com.grupdort.superligtakip.model.Fixture;
import com.grupdort.superligtakip.service.FixtureService;
import com.grupdort.superligtakip.util.ImageCache;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class FixtureController extends BaseController {

    @FXML private VBox mainContent;
    @FXML private Button menuButton;
    @FXML private SidebarController sidebarComponentController;
    @FXML private VBox loadingOverlay;
    @FXML private ComboBox<String> seasonComboBox;
    @FXML private Label lblCurrentWeek;
    @FXML private VBox matchListContainer;

    private final FixtureService fixtureService = new FixtureService();
    private int currentWeek;
    private final int MAX_WEEKS = 34;
    private int selectedSeasonYear = 2025;

    private boolean isTargetWeekSet = false;

    @FXML
    public void initialize() {
        if (sidebarComponentController != null) {
            sidebarComponentController.setMainContent(mainContent);
        }
        setupSeasonComboBox();

        Platform.runLater(() -> {
            if (!isTargetWeekSet) {
                currentWeek = fixtureService.findCurrentWeek();
                loadWeekData(currentWeek);
            }
        });
    }

    @FXML
    private void handleMenuButton() {
        if (sidebarComponentController != null) sidebarComponentController.toggle();
    }

    @FXML
    private void handlePrevWeek() {
        if (currentWeek > 1) {
            currentWeek--;
            loadWeekData(currentWeek);
        }
    }

    @FXML
    private void handleNextWeek() {
        if (currentWeek < MAX_WEEKS) {
            currentWeek++;
            loadWeekData(currentWeek);
        }
    }

    private void loadWeekData(int week) {
        lblCurrentWeek.setText(week + ". Hafta");
        matchListContainer.getChildren().clear();

        runAsync(loadingOverlay,
                () -> {
                    List<Fixture> allFixtures = fixtureService.getFixturesForWeek(week);
                    List<Fixture> filteredFixtures = allFixtures;
                    if (allFixtures != null) {
                        filteredFixtures = allFixtures.stream()
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
                                .sorted(Comparator.comparing(Fixture::getMatch_date)
                                        .thenComparing(Fixture::getMatch_time))
                                .collect(Collectors.toList());
                    }
                    final List<Fixture> finalFixtures = filteredFixtures;
                    Platform.runLater(() -> renderFixtures(finalFixtures));
                },
                () -> {}
        );
    }

    private void renderFixtures(List<Fixture> fixtures) {
        if (fixtures == null || fixtures.isEmpty()) {
            Label emptyLabel = new Label("Bu sezon ve hafta için maç verisi bulunamadı.");
            emptyLabel.getStyleClass().add("no-data-label");
            emptyLabel.setMaxWidth(Double.MAX_VALUE);
            emptyLabel.setAlignment(Pos.CENTER);
            matchListContainer.getChildren().add(emptyLabel);
            return;
        }

        boolean isAllSunday = fixtures.stream()
                .allMatch(f -> f.getMatch_date() != null && f.getMatch_date().getDayOfWeek() == DayOfWeek.SUNDAY);

        LocalDate lastDate = LocalDate.MIN;
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy - EEEE", new Locale("tr"));
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        for (Fixture f : fixtures) {
            if (f.getMatch_date() != null && !f.getMatch_date().equals(lastDate)) {
                Label dateHeader = new Label(f.getMatch_date().format(dateFormatter));
                dateHeader.getStyleClass().add("date-header-label");
                matchListContainer.getChildren().add(dateHeader);
                lastDate = f.getMatch_date();
            }

            HBox matchCard = new HBox();
            matchCard.getStyleClass().add("match-card");
            matchCard.setAlignment(Pos.CENTER);
            matchCard.setSpacing(5);
            matchCard.setStyle("-fx-cursor: hand;");
            matchCard.setOnMouseClicked(event -> openMatchDetail(f.getFixture_ID()));

            String timeText = "";
            if (f.getMatch_time() != null) {
                if (!isAllSunday) {
                    timeText = f.getMatch_time().format(timeFormatter);
                }
                if (f.getMatch_time().getHour() == 0 && f.getMatch_time().getMinute() == 0) {
                    timeText = "";
                }
            }

            Label timeLbl = new Label(timeText);
            timeLbl.getStyleClass().add("match-time");
            timeLbl.setMinWidth(35);
            timeLbl.setAlignment(Pos.CENTER);

            HBox homeBox = createTeamBox(f.getHome_team().getTeam_name(), f.getHome_team().getLogo(), true);
            HBox.setHgrow(homeBox, Priority.ALWAYS);

            String scoreText = "-";
            if ("Finished".equalsIgnoreCase(f.getStatus()) || "FT".equalsIgnoreCase(f.getStatus()) ||
                    (f.getStatus() != null && f.getStatus().contains(":"))) {
                scoreText = f.getHome_score() + " - " + f.getAway_score();
            } else if (f.getFinal_result() != null) {
                scoreText = f.getFinal_result();
            }

            Label scoreLbl = new Label(scoreText);
            scoreLbl.getStyleClass().add("score-label");

            HBox awayBox = createTeamBox(f.getAway_team().getTeam_name(), f.getAway_team().getLogo(), false);
            HBox.setHgrow(awayBox, Priority.ALWAYS);

            Button starBtn = createStarButton();
            starBtn.setOnMouseClicked(e -> e.consume());

            matchCard.getChildren().addAll(timeLbl, homeBox, scoreLbl, awayBox, starBtn);
            matchListContainer.getChildren().add(matchCard);
        }
    }

    private HBox createTeamBox(String name, String logoUrl, boolean isHome) {
        HBox box = new HBox(6);
        box.setAlignment(isHome ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        box.setMaxWidth(Double.MAX_VALUE);

        Label nameLabel = new Label(name);
        nameLabel.getStyleClass().add("team-name-label");
        nameLabel.setWrapText(false);
        nameLabel.setEllipsisString("..");
        nameLabel.setAlignment(isHome ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        HBox.setHgrow(nameLabel, Priority.ALWAYS);

        ImageView logo = new ImageView();
        logo.setFitWidth(22);
        logo.setFitHeight(22);
        logo.setPreserveRatio(true);
        try {
            if (logoUrl != null) logo.setImage(ImageCache.getImage(logoUrl));
        } catch (Exception e) {}

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

    private void openMatchDetail(int matchId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/grupdort/superligtakip/view/MatchDetailView/MatchDetail.fxml"));
            Parent root = loader.load();

            MatchDetailController controller = loader.getController();
            controller.loadMatchData(matchId);

            // --- KRİTİK NOKTA: "Ben Fikstürden geldim, haftam bu" diyoruz ---
            controller.setSourceWeek(this.currentWeek);

            Stage stage = (Stage) mainContent.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            System.err.println("Maç detay sayfası açılamadı!");
            e.printStackTrace();
        }
    }
    public void setTargetWeek(int week) {
        this.isTargetWeekSet = true;
        this.currentWeek = week;
        if(seasonComboBox != null) seasonComboBox.getSelectionModel().selectFirst();
        loadWeekData(week);
    }

    private void setupSeasonComboBox() {
        if (seasonComboBox == null) return;
        seasonComboBox.getItems().clear();
        for (int startYear = 2025; startYear >= 2000; startYear--) {
            int endYear = startYear + 1;
            seasonComboBox.getItems().add(startYear + "/" + endYear);
        }
        seasonComboBox.getSelectionModel().selectFirst();
        seasonComboBox.setOnAction(event -> {
            String selectedSeason = seasonComboBox.getSelectionModel().getSelectedItem();
            if (selectedSeason != null) {
                try {
                    selectedSeasonYear = Integer.parseInt(selectedSeason.split("/")[0]);
                    loadWeekData(currentWeek);
                } catch (NumberFormatException e) { e.printStackTrace(); }
            }
        });
    }
}