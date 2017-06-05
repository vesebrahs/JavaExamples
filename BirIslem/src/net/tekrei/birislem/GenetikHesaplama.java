/*
 * BirIslem Java ile yazilmis ve Genetik Algoritma kullanimini ornekleme
 * amaci guden bir ozgur yazilimdir. 
 * Copyright (C) 2007 T. E. KALAYCI (http://kodveus.blogspot.com)
 *  
 * BirIslem is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package net.tekrei.birislem;

import java.util.Arrays;

import net.tekrei.birislem.araclar.SayiArac;

/**
 * Genetik hesaplama islemlerinin yapildigi ve singleton tasarim deseni ile
 * gerceklestirilmis sinif, bu sinifta sadece bir metot (hesapla(int[],int))
 * disaridan erisilebiliyor (elbette ayrica getInstance() metodu ornek olusturup
 * donduruyor)
 * 
 * Her bir Kromozom bir bireydir.
 * 
 * @author emre
 * 
 */
public class GenetikHesaplama {

	// Singleton nesnemiz
	private static GenetikHesaplama instance = null;

	/**
	 * dis erisime kapali yapici
	 */
	private GenetikHesaplama() {

	}

	/**
	 * Singleton sinifin ornegini donduren metot
	 * 
	 * @return sinifin tek ornegi
	 */
	public static GenetikHesaplama getInstance() {
		if (instance == null) {
			instance = new GenetikHesaplama();
		}
		return instance;
	}

	/**
	 * Disaridan erisilebilen yegane metot Bu metodun amaci kendisine parametre
	 * olarak verilen hesaplama sayilarini genetik algoritmada isletip hedef
	 * sayisina en yakin cozumu bulmak ve bu cozumu cagiran yere dondurmektir.
	 * 
	 * @param sayilar
	 *            Hesaplamada kullanilacak olan sayilar
	 * @param hedefSayi
	 *            Ulasmak istedigimiz sayi
	 * @return En iyi cozum
	 */
	public Kromozom hesapla(int[] sayilar, int hedefSayi) {
		// FIX Uygun sezgilerle ve incelemeyle ise yaradigi bulundu
		// artik 1. nesil disindaki durumlarda da bulunabiliyor

		// Ilk olarak sayi aracimizi ilkliyoruz
		// Bu arac sayilarla ilgili bir cok islemi yapiyor
		SayiArac.getInstance().initialize(sayilar, hedefSayi);
		// 1000 bireyden olusan rastgele genlere sahip bireylerden
		// olusan toplum olusturuluyor
		Kromozom[] toplum = rastgeleToplumOlustur(1000);

		// nesil sayacimiz
		int nesil = 0;

		// Toplumu uygunluklarina gore (azalan sirada) siraliyoruz
		Arrays.sort(toplum);

		// Siralama islemi sonunda en iyi bireyimiz en uygun bireyimizdir
		// bu da ilk bireydir
		Kromozom enIyi = toplum[0].clone();

		// ne kadar surdugunu tutacagiz
		long baslangic = System.currentTimeMillis();

		// Genetik hesaplamayi 1000 nesil icin yapacagiz
		while (nesil < 1000) {
			// nesil sayacini arttiralim
			nesil++;

			// toplumda caprazlama yapalim
			// TODO caprazlama ise yariyor mu denemek lazim
			toplum = caprazla(toplum);
			// toplumda mutasyon yapalim
			toplum = mutasyon(toplum);

			// Toplumu uygunluklarina gore (azalan sirada) siraliyoruz
			Arrays.sort(toplum);

			// Eger elimizde hedef sayiyi tam olarak veren bir birey varsa
			// en iyi sonuc odur ve genetik hesaplamaya gerek yoktur
			// hemen en iyi kromozomu dondurebiliriz
			Kromozom tamSonuc = tamSonuc(toplum);
			if (tamSonuc != null) {
				enIyi = tamSonuc;
				break;
			}

			// Eger yeni toplumdaki en uygun birey elimizdeki en iyi bireyden
			// daha uygun ise en iyi bireyi yeni toplumdaki en uygun bireyle
			// degistiriyoruz
			if (enIyi.compareTo(toplum[0]) > 0) {
				enIyi = toplum[0].clone();
			}
			// En iyi bireyimiz tam sonuc ise cikiyoruz
			// TODO Aslinda yukaridaki tam sonuc kontrolu bunu yakalayacagi icin
			// bu satira gerek olmayabilir?
			if (tamSonucmu(enIyi)) {
				break;
			}
		}
		// Genetik hesaplama ile ilgili bilgileri verelim
		System.out.println("NESİL:" + nesil + " GEÇEN SÜRE:"
				+ (System.currentTimeMillis() - baslangic) + " ms" + " En İyi:"
				+ enIyi.toString());
		// En iyi sonucumuzu dondurelim
		return enIyi;
	}

	/**
	 * Girdi olarak verilen toplumdaki bireyler arasinda hedef sayiyi tam olarak
	 * veren birey varsa sonuc olarak dondurelecek, yoksa null dondurelecek
	 * 
	 * @param toplum
	 *            kontrol edilecek olan toplum
	 * @return tam sonuc varsa ilgili birey, yoksa null
	 */
	private Kromozom tamSonuc(Kromozom[] toplum) {
		for (Kromozom kromozom : toplum) {
			if (tamSonucmu(kromozom)) {
				return kromozom;
			}
		}
		return null;
	}

	/**
	 * Girdi olarak verilen bireyin tam sonuc uretip uretmedigini kontrol eden
	 * metot
	 * 
	 * @param birey
	 *            kontrol edilecek olan birey
	 * @return tam sonuc ise true, degilse false
	 */
	private boolean tamSonucmu(Kromozom birey) {
		// Eger bir birey tam sonuc uretiyorsa uygunlugu 0 olacaktir
		if (birey.uygunluk() == 0) {
			return true;
		}
		return false;
	}

	/**
	 * Asagidaki metod mutasyon islemini toplum uzerinde uygular
	 * 
	 * @param toplum
	 * @return
	 */
	private Kromozom[] mutasyon(Kromozom[] toplum) {
		for (int i = 0; i < toplum.length; i++) {
			try {
				Kromozom birey = toplum[i].clone();
				// Sayi Mutasyonu
				boolean mutasyon = false;
				// Toplumun sadece %2'sinde sayi mutasyonu oluyor
				if (SayiArac.getInstance().nextFloat() < 0.2) {
					sayiMutasyon(birey);
					mutasyon = true;
				}
				// Operator mutasyonu
				// Toplumun sadece %1'inde operator mutasyonu oluyor
				if (SayiArac.getInstance().nextFloat() < 0.1) {
					operatorMutasyon(birey);
					mutasyon = true;
				}
				// sadece daha iyi mutasyonlari saklayalim
				// Eger bir mutasyon daha kotu ise saklanmiyor
				if (mutasyon) {
					if (birey.uygunluk() < toplum[i].uygunluk()) {
						toplum[i] = birey;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// Degisen toplum donduruluyor
		return toplum;
	}

	/**
	 * Sayilar arasindaki mutasyon islemi
	 * 
	 * @param birey
	 */
	private void sayiMutasyon(Kromozom birey) {
		// Rastgele iki sayi secilip yerleri degistiriliyor
		int birinci = SayiArac.getInstance().nextInt(6);
		int ikinci = SayiArac.getInstance().nextInt(6);
		int temp = birey.getSayi(birinci);
		birey.setSayi(birinci, birey.getSayi(ikinci));
		birey.setSayi(ikinci, temp);
	}

	/**
	 * Parametreler arasindaki mutasyon
	 * 
	 * @param birey
	 */
	private void operatorMutasyon(Kromozom birey) {
		// Rastgele iki parametre secilip yerleri degistiriliyor
		int birinci = SayiArac.getInstance().nextInt(5);
		int ikinci = SayiArac.getInstance().nextInt(5);
		int temp = birey.getOperator(birinci);
		birey.setOperator(birinci, birey.getOperator(ikinci));
		birey.setOperator(ikinci, temp);
	}

	/**
	 * Asagidaki metod toplum uzerinde caprazlama islemini uygular
	 * 
	 * @param toplum
	 * @return
	 */
	private Kromozom[] caprazla(Kromozom[] toplum) {
		for (int i = 0; i < toplum.length - 1; i++) {
			try {
				// Toplumun sadece %90'i uzerinde caprazlama yapiliyor
				if (SayiArac.getInstance().nextFloat() < 0.9) {
					// Sadece toplum[i] degistiriliyor
					// esle(toplum[i], toplum[i + 1]);
					// eslemenin yapilacagi ikinci birey rastgele seciliyor
					esle(toplum[i], toplum[SayiArac.getInstance().nextInt(
							toplum.length)]);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// Degisen toplumu dondur
		return toplum;
	}

	/**
	 * Iki birey arasinda caprazlamayi yapan metot
	 * 
	 * @param ilk
	 *            birinci (degisecek olan) birey
	 * @param ikinci
	 *            caprazlama yardimcisi birey
	 */
	private void esle(Kromozom ilk, Kromozom ikinci) {

		// Caprazlama sonuclarini saklamak icin
		int[] yeniSayilar = new int[6];
		int[] yeniOperatorler = new int[5];

		// Sayilar caprazlansin
		for (int i = 0; i < 6; i++) {
			// Uniform (bir,iki,bir,iki,bir,iki)
			if (i % 2 == 0) {
				yeniSayilar[i] = ilk.getSayi(i);
			} else {
				yeniSayilar[i] = ikinci.getSayi(i);
			}
		}
		// Degisecek bireyin sayilarini yeni sayilar olarak atiyoruz
		ilk.setSayilar(duzeltme(yeniSayilar));

		// Operatorler caprazlansin
		for (int i = 0; i < 5; i++) {
			// uniform (bir,iki,bir,iki,bir)
			if (i % 2 == 0) {
				yeniOperatorler[i] = ilk.getOperator(i);
			} else {
				yeniOperatorler[i] = ikinci.getOperator(i);
			}
		}
		// Degisecek bireyin operatorlerini yeni operatorler olarak atiyoruz
		ilk.setOperatorler(duzeltme(yeniOperatorler));
	}

	/**
	 * Caprazlama sonucunda bozuk kromozomlar olusabilir bunu onlemek icin
	 * tekrar eden sayilari kaldirmak, yerine hic kullanilmayan sayilari koymak
	 * icin bu metot kullaniliyor Ornek
	 * 
	 * Birey1 : 1 0 3 4 5 2
	 * 
	 * Birey2 : 0 1 2 5 3 4
	 * 
	 * CaprazlamaSonucuBirey1 : 1 1 3 5 5 4
	 * 
	 * DuzeltmeSonucuBirey1 : 1 0 3 5 2 4 (ikinci 1 yerine olmayan en kucuk 0,
	 * ikinci 5 yerine olmayan en kucuk iki koyuldu)
	 * 
	 * @param genes
	 *            bozuk genler
	 * @return duzeltilmis genler
	 */
	private int[] duzeltme(int[] genes) {
		int boyut = genes.length;
		boolean[] var = new boolean[boyut];

		for (int i = 0; i < boyut; i++) {
			if (var[genes[i]]) {
				genes[i] = varolmayan(var);
			}

			var[genes[i]] = true;
		}

		return genes;
	}

	/**
	 * Kromozomda bulunmayan en kucuk geni dondurur
	 * 
	 * @param genesStatus
	 * @return olmayan en kucuk gen
	 */
	private int varolmayan(boolean[] genesStatus) {
		for (int i = 0; i < genesStatus.length; i++) {
			if (!genesStatus[i]) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * Parametre olarak girilen birey sayisi kadar rastgele genlere sahip toplum
	 * olusturulur ve dondurulur
	 * 
	 * @param toplumBuyuklugu
	 *            toplumdaki birey sayisi
	 * @return olusturulan toplum
	 */
	private Kromozom[] rastgeleToplumOlustur(int toplumBuyuklugu) {
		Kromozom[] toplum = new Kromozom[toplumBuyuklugu];
		for (int i = 0; i < toplumBuyuklugu; i++) {
			toplum[i] = new Kromozom(SayiArac.getInstance()
					.getRastgeleSayilar(), SayiArac.getInstance()
					.getRastgeleOperatorler());
		}
		return toplum;
	}
}
