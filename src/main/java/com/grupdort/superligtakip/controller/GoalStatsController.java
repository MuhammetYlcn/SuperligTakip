package com.grupdort.superligtakip.controller;

import com.grupdort.superligtakip.model.Player;
import com.grupdort.superligtakip.service.PlayerService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import java.util.List;

public class GoalStatsController extends BaseController {

    @FXML private VBox mainContent;
    @FXML private VBox listContainer;
    @FXML private SidebarController sidebarComponentController;

    private final PlayerService playerService = new PlayerService();

    @FXML
    public void initialize() {
        if (sidebarComponentController != null) {
            sidebarComponentController.setMainContent(mainContent);
        }
        loadData();
    }

    @FXML
    private void handleMenuButton() {
        if (sidebarComponentController != null) sidebarComponentController.toggle();
    }

    private void loadData() {
        listContainer.getChildren().clear();
        // KRİTİK DÜZELTME: listContainer'ı overlay olarak göndermiyoruz (null geçiyoruz)
        // Çünkü runAsync işlemi bitince bu parametreyi setVisible(false) yapar.
        runAsync(null, () -> {
            List<Player> players = playerService.getTopScorers();
            Platform.runLater(() -> renderList(players));
        }, () -> {});
    }

    private void renderList(List<Player> players) {
        listContainer.getChildren().clear();
        if (players == null || players.isEmpty()) {
            return;
        }

        int rank = 1;
        for (Player p : players) {
            HBox row = new HBox(15);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setMinHeight(70);
            row.setPrefHeight(70);
            row.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(row, Priority.ALWAYS);
            row.getStyleClass().add("player-card");

            // Sıra No
            Label lblRank = new Label(String.valueOf(rank));
            lblRank.setMinWidth(40);
            lblRank.setAlignment(Pos.CENTER);
            lblRank.setStyle("-fx-text-fill: #888; -fx-font-weight: bold;");

            // İsim ve Takım
            VBox nameBox = new VBox(2);
            nameBox.setAlignment(Pos.CENTER_LEFT);
            Label lblName = new Label(p.getPlayer_name());
            lblName.getStyleClass().add("player-name");

            Label lblTeam = new Label(p.getTeam() != null ? p.getTeam().getTeam_name() : "-");
            lblTeam.getStyleClass().add("player-team");

            nameBox.getChildren().addAll(lblName, lblTeam);
            HBox.setHgrow(nameBox, Priority.ALWAYS);

            // Değerler
            Label lblMatch = new Label(String.valueOf(p.getAppearances()));
            lblMatch.setMinWidth(50);
            lblMatch.setAlignment(Pos.CENTER);
            lblMatch.setStyle("-fx-text-fill: #b3b3b3;"); // CSS yerine garanti çözüm

            Label lblStat = new Label(String.valueOf(p.getGoal()));
            lblStat.setMinWidth(50);
            lblStat.setAlignment(Pos.CENTER);
            lblStat.getStyleClass().add("stat-goals");

            row.getChildren().addAll(lblRank, nameBox, lblMatch, lblStat);
            listContainer.getChildren().add(row);
            rank++;
        }

        Platform.runLater(() -> {
            listContainer.applyCss();
            listContainer.layout();
        });
    }
}