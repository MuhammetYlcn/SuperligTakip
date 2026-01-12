# 🏆 Süper Lig Takip Uygulaması

**Süper Lig Takip**, Türk futbolunu yakından takip etmek isteyen kullanıcılar için tasarlanmış, yüksek performanslı bir JavaFX masaüstü uygulamasıdır. 

---

## ✨ Öne Çıkan Özellikler

* **📊 Dinamik Puan Durumu:** Şampiyonlar Ligi, Avrupa Ligi ve Küme Düşme potalarını özel renkli simgelerle gösteren canlı sıralama. 
* **🗓️ Akıllı Fikstür:** Haftalar arası hızlı dolaşım, geçmiş maç sonuçları ve gelecek maçların detaylı takvimi. 
* **📈 Gelişmiş Maç İstatistikleri:** Topla oynama, şut ve pas isabeti gibi verilerin dinamik progress bar'lar ile görselleştirilmesi. 
* **⏱️ Kronolojik Maç Olayları:** Gol, kart ve oyuncu değişikliklerinin dakika bazlı "Event Tree" yapısında gösterimi. 
* **🛡️ Takım & Oyuncu Analizi:** Mevkilere göre gruplandırılmış kadro yapısı ve rating bazlı oyuncu performans analizleri. 

---

## 🛠️ Teknik Altyapı

Uygulama, sürdürülebilir ve modüler bir yapı için modern tasarım desenleri üzerine inşa edilmiştir. 

| Teknoloji | Kullanım Amacı |
| :--- | :--- |
| **Java** | [cite_start]Ana Programlama Dili  |
| **JavaFX & CSS** | [cite_start]Modern ve Dinamik Kullanıcı Arayüzü |
| **SQLite** | [cite_start]Hızlı ve Yerel Veri Depolama |
| **All Sports API** | [cite_start]Gerçek Zamanlı Veri Kaynağı |


---

## 🧠 Akıllı Veri Yönetimi (Cache)

Uygulama, API limitlerini korumak ve hızı artırmak için **"Önce Yerel Veri"** stratejisini kullanır: 
* **Hız:** Her sayfa geçişinde ağ gecikmesi yaşanmaması için veriler önce yerel veritabanından (`superlig.db`) okunur. 
* **Verimlilik:** Belirlenen güncelleme süresi (1-2 saat) dolmadan API'ye tekrar istek atılmaz. 

---

## 📐 Veritabanı Mimarisi

Sistem, toplam **11 ilişkisel tablo** üzerinden veri tutarlılığını sağlar: 
* **Kadrolar:** `Teams`, `Players` ve `MatchLineups`. 
* **Maç Detayları:** `Fixtures`, `MatchStatistics` ve `Events`. 
* **Takip:** `Standings` ve `LastUpdates`. 

---

## 🚀 Zorluklar ve Çözümler

* **Kota ve Veri Sorunu:** API-Football kısıtlamaları nedeniyle daha geniş kapsamlı olan **All Sports API** platformuna geçiş yapılmıştır. 
* **Veri Temizleme (Smart ID Mapping):** Bazı maçların mükerrer (çift) gelmesini engellemek için Ev Sahibi + Deplasman + Hafta kontrolü yapan özel bir algoritma geliştirilmiştir. 

---

## 👥 Geliştirme Ekibi
* **Muhammet Ali Yalçın** 
* **Ömer Kerem Çataklı** 
* **Fatima Al Zahraa Alamer** 

---
> *Bu proje bir üniversite eğitim çalışması kapsamında geliştirilmiştir.*
