# HyperLogLog Cardinality Estimator

**Öğrenci:** Gözde Aydoğdu  
**Öğrenci No:** 1240505030  

Bu proje, **HyperLogLog (HLL)** olasılıksal veri yapısının sıfırdan gerçeklenmesini içermektedir.  
Amaç, büyük veri kümelerinde **benzersiz eleman sayısını (cardinality)** düşük bellek kullanarak tahmin etmektir.

Proje **Büyük Veri Analitiği dersi final ödevi** kapsamında hazırlanmıştır.

---

# 1. Cardinality Estimation Problemi

Cardinality estimation, bir veri kümesinde bulunan **farklı eleman sayısını** tahmin etme problemidir.

Örnek kullanım alanları:

- Web sitesindeki benzersiz ziyaretçi sayısı
- Farklı IP adresleri
- Streaming veri sistemlerinde event sayısı
- Arama sorgularındaki benzersiz kelimeler

Klasik veri yapıları kullanıldığında bu işlem çok fazla bellek gerektirir.

| Yöntem | Bellek Kullanımı |
|------|------|
HashSet | Çok yüksek |
Bitmap | Yüksek |
HyperLogLog | Çok düşük |

Bu nedenle büyük veri sistemlerinde **HyperLogLog** tercih edilir.

---

# 2. HyperLogLog Algoritması

HyperLogLog algoritması, veri elemanlarının **hash değerlerini** kullanarak yaklaşık cardinality tahmini yapar.

Algoritmanın temel bileşenleri:

1. Hash fonksiyonu
2. Bucketing mekanizması
3. Register yapısı
4. Leading zero sayma
5. Harmonik ortalama hesaplama
6. Küçük ve büyük veri düzeltmeleri

---

# 3. Hash Fonksiyonu

Her veri elemanı önce **64-bit hash değerine** dönüştürülür.

Bu projede kullanılan hash fonksiyonu bit karıştırma (bit mixing) teknikleri kullanarak kaliteli bir dağılım sağlar.

```java
private long improvedHash(String item)
