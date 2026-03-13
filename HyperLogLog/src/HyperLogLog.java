import java.util.Objects;

/**
 * HyperLogLog Cardinality Estimator
 *
 * Öğrenci: Gözde Aydoğdu
 * No: 1240505030
 *
 * Büyük Veri Analitiğinde Olasılıksal Veri Yapıları
 */

public class HyperLogLog {

    private final int p;
    private final int m;
    private final byte[] registers;
    private final double alpha;

    public HyperLogLog(int p) {
        this.p = p;
        this.m = 1 << p;
        this.registers = new byte[m];
        this.alpha = calculateAlpha(m);
    }

    private double calculateAlpha(int m) {
        if (m == 16) return 0.673;
        if (m == 32) return 0.697;
        if (m == 64) return 0.709;
        return 0.7213 / (1 + 1.079 / m);
    }

    /**
     * 64-bit kaliteli hash üretimi
     */
    private long improvedHash(String item) {

        long h = Double.doubleToLongBits(Objects.hashCode(item));

        h ^= (h >>> 33);
        h *= 0xff51afd7ed558ccdL;

        h ^= (h >>> 33);
        h *= 0xc4ceb9fe1a85ec53L;

        h ^= (h >>> 33);

        return h;
    }

    /**
     * Veri ekleme
     */
    public void add(String item) {

        if (item == null) return;

        long x = improvedHash(item);

        // Bucketing
        int j = (int) (x >>> (64 - p));

        // Sıfır sayma
        long w = (x << p) | (1L << (p - 1));

        int rho = Long.numberOfLeadingZeros(w) + 1;

        registers[j] = (byte) Math.max(registers[j], rho);
    }

    /**
     * Cardinality tahmini
     */
    public long estimate() {

        double sum = 0.0;

        for (int i = 0; i < m; i++) {
            sum += Math.pow(2.0, -registers[i]);
        }

        double estimate = alpha * m * m / sum;

        // Küçük veri düzeltmesi
        if (estimate <= 2.5 * m) {

            int zeros = 0;

            for (byte r : registers) {
                if (r == 0) zeros++;
            }

            if (zeros > 0) {
                estimate = m * Math.log((double) m / zeros);
            }
        }

        // Büyük veri düzeltmesi
        double TWO_32 = Math.pow(2, 32);

        if (estimate > (TWO_32 / 30.0)) {
            estimate = -TWO_32 * Math.log(1 - (estimate / TWO_32));
        }

        return Math.round(estimate);
    }

    /**
     * İki HLL yapısını birleştirme (Merge)
     */
    public void merge(HyperLogLog other) {

        if (this.m != other.m) {
            throw new IllegalArgumentException("HLL boyutları aynı olmalıdır");
        }

        for (int i = 0; i < m; i++) {
            this.registers[i] =
                    (byte) Math.max(this.registers[i], other.registers[i]);
        }
    }

    /**
     * Teorik hata oranı
     */
    public double theoreticalError() {
        return 1.04 / Math.sqrt(m);
    }

    /**
     * Algoritma testi
     */
    public static void runExperiment(int precision) {

        HyperLogLog hll = new HyperLogLog(precision);

        int real = 50000;

        for (int i = 0; i < real; i++) {
            hll.add("user_" + i);
        }

        long estimate = hll.estimate();

        double error =
                Math.abs(estimate - real) / (double) real * 100;

        System.out.println("--------------------------------");
        System.out.println("Precision (p): " + precision);
        System.out.println("Bucket sayısı (m): " + (1 << precision));
        System.out.println("Gerçek değer: " + real);
        System.out.println("Tahmin: " + estimate);
        System.out.println("Gerçek hata: %" + String.format("%.2f", error));
        System.out.println("Teorik hata: %" +
                String.format("%.2f", hll.theoreticalError() * 100));
    }

    public static void main(String[] args) {

        System.out.println("HyperLogLog Analizi Başlatılıyor\n");

        // Farklı kova sayıları için analiz

        runExperiment(10);
        runExperiment(12);
        runExperiment(14);

        System.out.println("\nMerge Testi");

        HyperLogLog hll1 = new HyperLogLog(14);
        HyperLogLog hll2 = new HyperLogLog(14);

        for (int i = 0; i < 25000; i++) {
            hll1.add("user_" + i);
        }

        for (int i = 25000; i < 50000; i++) {
            hll2.add("user_" + i);
        }

        hll1.merge(hll2);

        System.out.println("Birleştirilmiş tahmin: " + hll1.estimate());
    }
}
