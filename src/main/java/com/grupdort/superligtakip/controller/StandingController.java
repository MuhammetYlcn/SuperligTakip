package com.grupdort.superligtakip.controller;

import com.grupdort.superligtakip.model.Standing;
import com.grupdort.superligtakip.model.Team;
import com.grupdort.superligtakip.service.StandingService;
import com.grupdort.superligtakip.util.ImageCache;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class StandingController extends BaseController {

    // --- MENÜ BAĞLANTILARI ---
    @FXML private VBox mainContent; // Blur yapılacak alan
    @FXML private Button menuButton; // Sol üstteki hamburger butonu

    @FXML private SidebarController sidebarComponentController;

    // --- SAYFA BİLEŞENLERİ ---
    @FXML private VBox loadingOverlay;
    @FXML private ComboBox<String> seasonComboBox;
    @FXML private TableView<Standing> standingsTable;

    // --- TABLO SÜTUNLARI ---
    @FXML private TableColumn<Standing, Integer> colRank;
    @FXML private TableColumn<Standing, Team> colTeam;
    @FXML private TableColumn<Standing, Integer> colPlayed;
    @FXML private TableColumn<Standing, Integer> colWon;
    @FXML private TableColumn<Standing, Integer> colDrawn;
    @FXML private TableColumn<Standing, Integer> colLost;
    @FXML private TableColumn<Standing, Integer> colGF;
    @FXML private TableColumn<Standing, Integer> colGA;
    @FXML private TableColumn<Standing, Integer> colAV;
    @FXML private TableColumn<Standing, Integer> colPoints;

    private final StandingService standingService = new StandingService();
    private final ObservableList<Standing> standingList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setupTableColumns();
        setupSeasonComboBox();

        // --- MENÜ ENTEGRASYONU ---
        if (sidebarComponentController != null) {
            sidebarComponentController.setMainContent(mainContent);
        }

        standingsTable.setItems(standingList);
        loadStandingsData(2025);

        // --- YENİ EKLENEN: SATIR TIKLAMA OLAYI (TAKIM SAYFASINA GİT) ---
        standingsTable.setRowFactory(tv -> {
            TableRow<Standing> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1 && (!row.isEmpty())) {
                    Standing rowData = row.getItem();
                    // Takım sayfasına geçiş yap
                    openTeamDetailPage(rowData.getTeam());
                }
            });
            return row;
        });
    }

    // --- TAKIM DETAY SAYFASINI AÇAN METOD ---
    private void openTeamDetailPage(Team team) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/grupdort/superligtakip/view/TeamView/Team.fxml"));
            Parent root = loader.load();

            // Controller'a seçilen takımı gönder
            TeamController controller = loader.getController();
            controller.setTeamData(team);

            // Sahneyi değiştir
            Stage stage = (Stage) standingsTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();

        } catch (Exception e) {
            System.err.println("Takım sayfası açılırken hata oluştu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Sol üstteki ☰ butonuna basınca çalışır
    @FXML
    private void handleMenuButton() {
        if (sidebarComponentController != null) {
            sidebarComponentController.toggle();
        }
    }

    // --- VERİ YÜKLEME ---
    private void loadStandingsData(int seasonId) {
        standingsTable.setPlaceholder(null);
        List<Standing> fetchedData = new ArrayList<>();
        VBox overlay = (loadingOverlay != null) ? loadingOverlay : new VBox();

        runAsync(overlay,
                () -> {
                    var data = standingService.getStandingsForTable(seasonId);
                    if (data != null) fetchedData.addAll(data);
                },
                () -> {
                    if (fetchedData.isEmpty()) {
                        standingsTable.setPlaceholder(new Label("Bu sezon için veri bulunamadı."));
                        standingList.clear();
                    } else {
                        standingList.setAll(fetchedData);
                    }
                }
        );
    }

    // --- TABLO AYARLARI ---
    private void setupTableColumns() {
        // İstatistik Sütunları
        colPlayed.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getPlayed()).asObject());
        colWon.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getWon()).asObject());
        colDrawn.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getDrawn()).asObject());
        colLost.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getLost()).asObject());
        colGF.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getGoal_for()).asObject());
        colGA.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getGoal_against()).asObject());
        colPoints.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getPoints()).asObject());
        colAV.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getGoal_for() - d.getValue().getGoal_against()).asObject());

        // Takım Sütunu (Logo + İsim)
        colTeam.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().getTeam()));
        colTeam.setCellFactory(column -> new TableCell<Standing, Team>() {
            @Override
            protected void updateItem(Team team, boolean empty) {
                super.updateItem(team, empty);
                if (empty || team == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    HBox box = new HBox(10);
                    box.setAlignment(Pos.CENTER_LEFT);
                    ImageView logo = new ImageView();
                    logo.setFitWidth(20);
                    logo.setFitHeight(20);
                    try {
                        if (team.getLogo() != null) logo.setImage(ImageCache.getImage(team.getLogo()));
                    } catch (Exception e) { }

                    Label nameLabel = new Label(team.getTeam_name());
                    nameLabel.setTextFill(Color.WHITE);
                    nameLabel.setStyle("-fx-font-weight: bold;");

                    box.getChildren().addAll(logo, nameLabel);
                    setGraphic(box);
                }
            }
        });

        // Sıra Sütunu (Dinamik Renkler)
        colRank.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getRank()).asObject());
        colRank.setCellFactory(column -> new TableCell<Standing, Integer>() {
            @Override
            protected void updateItem(Integer rank, boolean empty) {
                super.updateItem(rank, empty);
                if (empty || rank == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    HBox box = new HBox(5);
                    box.setAlignment(Pos.CENTER);
                    Circle c = new Circle(3, Color.TRANSPARENT);

                    int totalRows = getTableView().getItems().size();

                    if (rank == 1) c.setFill(Color.web("#2ecc71"));
                    else if (rank == 2) c.setFill(Color.web("#f1c40f"));
                    else if (rank == 3) c.setFill(Color.DEEPSKYBLUE);
                    else if (rank == 4) c.setFill(Color.web("#9b59b6"));
                    else if (rank > (totalRows - 3) && totalRows > 0) c.setFill(Color.RED);

                    Label lbl = new Label(String.valueOf(rank));
                    lbl.setTextFill(Color.web("#b3b3b3"));
                    if (c.getFill() != Color.TRANSPARENT) box.getChildren().add(c);
                    box.getChildren().add(lbl);
                    setGraphic(box);
                }
            }
        });
    }

    // --- SEZON SEÇİMİ ---
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
                    loadStandingsData(Integer.parseInt(selectedSeason.split("/")[0]));
                } catch (NumberFormatException e) { e.printStackTrace(); }
            }
        });
    }
}