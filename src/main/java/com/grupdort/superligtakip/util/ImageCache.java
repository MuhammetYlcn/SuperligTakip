package com.grupdort.superligtakip.util;

import javafx.scene.image.Image;
import java.util.HashMap;
import java.util.Map;

public class ImageCache {
    // Resimleri hafızada tutacak havuz
    private static final Map<String, Image> cache = new HashMap<>();

    /**
     * Verilen URL için resmi getirir.
     * Eğer resim daha önce yüklendiyse hafızadan verir.
     * İlk kez isteniyorsa yükler, hafızaya atar ve öyle verir.
     */
    public static Image getImage(String url) {
        if (url == null || url.isEmpty()) return null;

        // Eğer havuzda yoksa yükle ve ekle
        if (!cache.containsKey(url)) {
            // Arayüz donmasın diye "true" parametresini yine kullanıyoruz
            cache.put(url, new Image(url, true));
        }

        // Havuzdan döndür
        return cache.get(url);
    }
}