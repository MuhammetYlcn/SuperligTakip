🏆 Süper Lig Takip Uygulaması
Süper Lig futbol verilerini anlık olarak takip eden, kullanıcı dostu arayüze sahip ve yüksek performanslı bir masaüstü uygulamasıdır. Türk futbolunu yakından takip etmek isteyen futbolseverler ve istatistik meraklıları için geliştirilmiştir.



🚀 Özellikler

Anlık Puan Durumu: Şampiyonlar Ligi, Avrupa Ligi ve küme düşme potalarını gösteren görsel göstergelerle güncel sıralama.



Dinamik Fikstür: Haftalar arası kolay geçiş, geçmiş maç skorları ve gelecek maç takvimi.



Detaylı Maç İstatistikleri: Topla oynama, şut sayıları ve pas isabeti gibi verilerin "Progress Bar"lar ile görselleştirilmesi.


Maç Olay Ağacı: Goller, kartlar ve oyuncu değişikliklerinin kronolojik gösterimi.


Takım ve Oyuncu Analizi: Takım kadroları, oyuncu mevkileri ve detaylı performans istatistikleri (yaş, gol, asist, rating vb.).


İstatistik Liderleri: Gol ve asist krallığında zirvedeki oyuncuların takibi.

🛠️ Teknik Mimari ve Teknolojiler
Uygulama, modern yazılım prensipleri ve katmanlı mimari kullanılarak inşa edilmiştir.



Programlama Dili: Java.


Arayüz: JavaFX & CSS (Modern ve dinamik kullanıcı deneyimi için).


Veritabanı: SQLite (Hızlı yerel depolama ve çevrimdışı erişim desteği).



Veri Kaynağı: All Sports API.



Tasarım Desenleri: MVC (Model-View-Controller), DAO (Data Access Object) ve DTO (Data Transfer Object).

🧠 Akıllı Veri Yönetimi (Cache Sistemi)
Uygulama, "Önce Yerel Veri" ilkesini kullanarak ağ gecikmelerini minimize eder ve API limitlerini verimli kullanır:


Performans: Her sayfa geçişinde API'ye istek atmak yerine veriler SQLite üzerinden çekilir.


Optimizasyon: Uygulama açılışında veriler senkronize edilerek yerel veritabanına kaydedilir.


Güncelleme Mantığı: Belirlenen süre (1-2 saat) dolmadan yeni API isteği atılmaz, böylece kota korunur.

📊 Veritabanı Şeması
Sistemde toplam 11 adet ilişkisel tablo bulunmaktadır:


Teams & Players: Takım ve oyuncu bilgileri.


Fixtures & Events: Maç takvimi ve maç içi önemli olaylar.



MatchLineups & Statistics: Kadrolar ve detaylı maç sonu istatistikleri.


LastUpdates: Veri güncelliğini takip eden kontrol tablosu.

🛠️ Karşılaşılanan Zorluklar ve Çözümler

Kota Yönetimi: Ücretsiz API kısıtlamalarını aşmak için daha kapsamlı veri sağlayan All Sports API'ye geçiş yapılarak veri doğruluğu artırılmıştır.



Veri Çoğullaması: API'den gelen mükerrer maç kayıtlarını önlemek için "Smart ID Mapping" algoritması geliştirilmiştir. Bu algoritma Ev Sahibi, Deplasman ve Hafta bilgilerini kontrol ederek veritabanındaki veri kirliliğini engeller.
