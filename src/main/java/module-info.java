module com.grupdort.superligtakip {
    // JavaFX modülleri
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base; // TableView veriye erişebilsin diye şart

    // Veritabanı ve API modülleri
    requires java.sql;
    requires java.net.http;

    // Jackson JSON kütüphaneleri
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.core;

    // --- AÇILMASI GEREKEN PAKETLER (OPENS) ---

    // FXML dosyalarının Controller'a erişimi
    opens com.grupdort.superligtakip.controller to javafx.fxml;

    // TableView'ın model nesnelerine (Getter'lara) erişimi
    opens com.grupdort.superligtakip.model to javafx.base;

    // Jackson'ın JSON'dan nesne üretirken (Reflection) senin sınıflarına erişimi
    // Buradaki paket yollarının doğruluğundan emin ol (dto.standings mi yoksa dto mu?)
    opens com.grupdort.superligtakip.dto.standing to com.fasterxml.jackson.databind;

    // Jackson'ın JSON'u nesneye dönüştürebilmesi için BURASI ŞART:
    opens com.grupdort.superligtakip.dto.fixture to com.fasterxml.jackson.databind;

    opens com.grupdort.superligtakip.dto.team to com.fasterxml.jackson.databind;

    // --- DIŞARIYA SUNULAN PAKETLER (EXPORTS) ---
    exports com.grupdort.superligtakip;
    exports com.grupdort.superligtakip.controller;
    exports com.grupdort.superligtakip.model;
    exports com.grupdort.superligtakip.service; // Service katmanını dışarı açıyoruz
    exports com.grupdort.superligtakip.dto.fixture;
}