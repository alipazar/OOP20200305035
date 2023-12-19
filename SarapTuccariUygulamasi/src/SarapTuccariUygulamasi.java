import java.io.*;
import java.util.*;
import java.util.function.Predicate;

// 1. Kalıtım: Sarap sınıfı
abstract class Sarap implements Serializable {
    protected String isim;
    protected String tedarikci;
    protected int miktar;

    public Sarap(String isim, String tedarikci, int miktar) {
        this.isim = isim;
        this.tedarikci = tedarikci;
        this.miktar = miktar;
    }

    public abstract void bilgiGoster();

    public String getIsim() {
        return isim;
    }

    public String getTedarikci() {
        return tedarikci;
    }

    public int getMiktar() {
        return miktar;
    }
}

// Beyaz Sarap
class BeyazSarap extends Sarap {
    public BeyazSarap(String tedarikci, int miktar) {
        super("Beyaz Sauvignon", tedarikci, miktar);
    }

    @Override
    public void bilgiGoster() {
        System.out.println(miktar + " şişe " + isim + " türünden, tedarikçi: " + tedarikci);
    }
}

// Kırmızı Sarap
class KirmiziSarap extends Sarap {
    public KirmiziSarap(String tedarikci, int miktar) {
        super("Kırmızı Merlot", tedarikci, miktar);
    }

    @Override
    public void bilgiGoster() {
        System.out.println(miktar + " şişe " + isim + " türünden, tedarikçi: " + tedarikci);
    }
}

// Rose Sarap
class RoseSarap extends Sarap {
    public RoseSarap(String tedarikci, int miktar) {
        super("Rose Zinfandel", tedarikci, miktar);
    }

    @Override
    public void bilgiGoster() {
        System.out.println(miktar + " şişe " + isim + " türünden, tedarikçi: " + tedarikci);
    }
}

// 2. Arayüz: Fatura arayüzü
interface Fatura {
    void faturaOlustur(List<SiparisDetay<? extends Sarap>> siparisler, Predicate<SiparisDetay<? extends Sarap>> filter);
}

// 3. Polimorfizm: Fatura sınıfı
class FaturaOlusturucu implements Fatura {
    @Override
    public void faturaOlustur(List<SiparisDetay<? extends Sarap>> siparisler, Predicate<SiparisDetay<? extends Sarap>> filter) {
        System.out.println("Fatura:");
        for (SiparisDetay<? extends Sarap> siparis : siparisler) {
            if (filter.test(siparis)) {
                siparis.detaylariGoster();
            }
        }
    }
}

// Sipariş Detayları
class SiparisDetay<T extends Sarap> implements Serializable {
    private static final long serialVersionUID = 1L;

    private T sarap;
    private String aciklama;

    public SiparisDetay(T sarap, String aciklama) {
        this.sarap = sarap;
        this.aciklama = aciklama;
    }

    public T getSarap() {
        return sarap;
    }

    public void detaylariGoster() {
        System.out.println(
                sarap.getMiktar() + " şişe " +
                        sarap.getIsim() + " türünden, tedarikçi: " +
                        sarap.getTedarikci() + " - " + aciklama
        );
    }
}

// Veritabanı sınıfı
class SiparisVeritabani {
    private static final String VERITABANI_DOSYASI = "siparisler.txt";

    @SuppressWarnings("unchecked")
    public static void siparisleriKaydet(List<? extends SiparisDetay<? extends Sarap>> siparisler) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(VERITABANI_DOSYASI))) {
            oos.writeObject(siparisler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static List<? extends SiparisDetay<? extends Sarap>> siparisleriYukle() {
        List<? extends SiparisDetay<? extends Sarap>> siparisler = new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(VERITABANI_DOSYASI))) {
            Object obj = ois.readObject();
            if (obj instanceof List) {
                siparisler = (List<? extends SiparisDetay<? extends Sarap>>) obj;
            }
        } catch (IOException | ClassNotFoundException e) {
            // Dosya bulunamazsa veya okunamazsa hata almayı bekleyebiliriz.
            // e.printStackTrace();
        }
        return siparisler;
    }
}
// Jenerik Sınıf
class SiparisJenerik<T extends Sarap> {
    private List<SiparisDetay<T>> siparisDetayListesi = new ArrayList<>();

    public void siparisEkle(T sarap, String aciklama) {
        SiparisDetay<T> detay = new SiparisDetay<>(sarap, aciklama);
        siparisDetayListesi.add(detay);
    }

    public void detaylariGoster() {
        for (SiparisDetay<T> detay : siparisDetayListesi) {
            detay.detaylariGoster();
        }
    }
}

public class SarapTuccariUygulamasi {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        Set<SiparisDetay<? extends Sarap>> siparisDetaySet = new HashSet<>();

        String devamEt = "evet";

        do {
            System.out.print("Sarap türünü girin (Beyaz, Kırmızı, Rose): ");
            String sarapTuru = scanner.nextLine();

            System.out.print("Miktarı girin: ");
            int miktar = scanner.nextInt();
            scanner.nextLine();

            Sarap sarap;
            switch (sarapTuru.toLowerCase()) {
                case "beyaz":
                    sarap = new BeyazSarap("Örnek Tedarikçi", miktar);
                    break;
                case "kırmızı":
                    sarap = new KirmiziSarap("Örnek Tedarikçi", miktar);
                    break;
                case "rose":
                    sarap = new RoseSarap("Örnek Tedarikçi", miktar);
                    break;
                default:
                    System.out.println("Geçersiz sarap türü. Satış siparişi işlenmedi.");
                    continue;
            }

            System.out.print("Açıklamayı girin: ");
            String aciklama = scanner.nextLine();

            SiparisDetay<? extends Sarap> siparisDetay = new SiparisDetay<>(sarap, aciklama);
            siparisDetaySet.add(siparisDetay);

            System.out.print("Başka bir satış siparişi girmek istiyor musunuz? (Evet için 'evet', hayır için 'hayir' yazın): ");
            devamEt = scanner.nextLine();

        } while (devamEt.equalsIgnoreCase("evet"));

        SiparisJenerik<Sarap> sarapSiparisler = new SiparisJenerik<>();
        sarapSiparisler.siparisEkle(new BeyazSarap("Örnek Tedarikçi", 5), "Yeni beyaz şarap siparişi");
        sarapSiparisler.detaylariGoster();

        Fatura faturaOlusturucu = new FaturaOlusturucu();
        faturaOlusturucu.faturaOlustur(new ArrayList<>(siparisDetaySet), siparis -> siparis.getSarap().getMiktar() > 3);

        SiparisVeritabani.siparisleriKaydet(new ArrayList<>(siparisDetaySet));

        System.out.println("Program sonlandırıldı.");
    }
}