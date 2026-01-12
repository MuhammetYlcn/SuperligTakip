package com.grupdort.superligtakip.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

// BaseController'dan miras alıyoruz ki animasyon ve navigasyon özelliklerini kullansın
public class SidebarController extends BaseController {

    @FXML private AnchorPane menuOverlay;
    @FXML private VBox sidebar;

    @FXML private Button btnPuanDurumu;
    @FXML private Button btnFikstur;
    @FXML private Button btnGolKralligi;
    @FXML private Button btnAsistKralligi;

    // Ana ekrandaki içeriği (Tabloyu) blur yapmak için referans tutacağız
    private VBox mainContentRef;

    @FXML
    public void initialize() {
        // Overlay'e tıklayınca kapanma özelliği
        super.setupMenuOverlay(sidebar, menuOverlay, mainContentRef);

        // Butonlara tıklayınca sayfa değiştirme özelliği
        super.setupNavigation(btnPuanDurumu, btnFikstur, btnGolKralligi, btnAsistKralligi);
    }

    // Ana Controller'dan (StandingController) blur yapılacak alanı buraya göndereceğiz
    public void setMainContent(VBox mainContent) {
        this.mainContentRef = mainContent;
        // Referans güncellendiği için overlay ayarını tazele
        super.setupMenuOverlay(sidebar, menuOverlay, mainContentRef);
    }

    // Menüyü açıp kapatan metod (Dışarıdan çağrılacak)
    public void toggle() {
        super.toggleMenu(sidebar, menuOverlay, mainContentRef);
    }
}