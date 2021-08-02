package data;

public class Car {

	public Car(String marke, String modell, int preis, int hubraum, int ps, String kraftstoff) {
		this.marke = marke;
		this.modell = modell;
		this.preis = preis;
		this.hubraum = hubraum;
		this.ps = ps;
		this.kraftstoff = kraftstoff;
	}

	private String marke;
	private String modell;
	private int preis;
	private int hubraum;
	private int ps;
	private String kraftstoff;
	private double similarity; 
	

	public double getSimilarity() {
		return similarity;
	}

	public void setSimilarity(double similarity) {
		this.similarity = similarity;
	}

	public String getMarke() {
		return marke;
	}
	
	public String getModell() {
		return modell;
	}

	public int getPreis() {
		return preis;
	}
	
	public int getHubraum() {
		return hubraum;
	}
	
	public int getPs() {
		return ps;
	}

	public String getKraftstoff() {
		return kraftstoff;
	}

}
