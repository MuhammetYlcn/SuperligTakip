# 🏆 Süper Lig Takip Uygulaması

**Süper Lig Takip**, Türk futbolunu yakından takip etmek isteyen kullanıcılar için tasarlanmış, yüksek performanslı bir JavaFX masaüstü uygulamasıdır. [cite: 8, 9]

---

## ✨ Öne Çıkan Özellikler

* **📊 Dinamik Puan Durumu:** Şampiyonlar Ligi, Avrupa Ligi ve Küme Düşme potalarını özel renkli simgelerle gösteren canlı sıralama. [cite: 107, 108]
* **🗓️ Akıllı Fikstür:** Haftalar arası hızlı dolaşım, geçmiş maç sonuçları ve gelecek maçların detaylı takvimi. [cite: 70, 80]
* **📈 Gelişmiş Maç İstatistikleri:** Topla oynama, şut ve pas isabeti gibi verilerin dinamik progress bar'lar ile görselleştirilmesi. [cite: 183]
* **⏱️ Kronolojik Maç Olayları:** Gol, kart ve oyuncu değişikliklerinin dakika bazlı "Event Tree" yapısında gösterimi. [cite: 182]
* **🛡️ Takım & Oyuncu Analizi:** Mevkilere göre gruplandırılmış kadro yapısı ve rating bazlı oyuncu performans analizleri. [cite: 117, 118]

---

## 🛠️ Teknik Altyapı

Uygulama, sürdürülebilir ve modüler bir yapı için modern tasarım desenleri üzerine inşa edilmiştir. [cite: 52]

| Teknoloji | Kullanım Amacı |
| :--- | :--- |
| **Java** | [cite_start]Ana Programlama Dili [cite: 48] |
| **JavaFX & CSS** | [cite_start]Modern ve Dinamik Kullanıcı Arayüzü [cite: 49] |
| **SQLite** | [cite_start]Hızlı ve Yerel Veri Depolama [cite: 51] |
| **All Sports API** | [cite_start]Gerçek Zamanlı Veri Kaynağı [cite: 51, 278] |


---

## 🧠 Akıllı Veri Yönetimi (Cache)

Uygulama, API limitlerini korumak ve hızı artırmak için **"Önce Yerel Veri"** stratejisini kullanır: [cite: 57]
* **Hız:** Her sayfa geçişinde ağ gecikmesi yaşanmaması için veriler önce yerel veritabanından (`superlig.db`) okunur. [cite: 59, 60]
* **Verimlilik:** Belirlenen güncelleme süresi (1-2 saat) dolmadan API'ye tekrar istek atılmaz. [cite: 61]

---

## 📐 Veritabanı Mimarisi

Sistem, toplam **11 ilişkisel tablo** üzerinden veri tutarlılığını sağlar: [cite: 255]
* **Kadrolar:** `Teams`, `Players` ve `MatchLineups`. [cite: 256, 257, 262]
* **Maç Detayları:** `Fixtures`, `MatchStatistics` ve `Events`. [cite: 260, 263, 264]
* **Takip:** `Standings` ve `LastUpdates`. [cite: 265, 266]

---

## 🚀 Zorluklar ve Çözümler

* **Kota ve Veri Sorunu:** API-Football kısıtlamaları nedeniyle daha geniş kapsamlı olan **All Sports API** platformuna geçiş yapılmıştır. [cite: 275, 278]
* **Veri Temizleme (Smart ID Mapping):** Bazı maçların mükerrer (çift) gelmesini engellemek için Ev Sahibi + Deplasman + Hafta kontrolü yapan özel bir algoritma geliştirilmiştir. [cite: 283, 284]
