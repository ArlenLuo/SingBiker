package sg.ntu.dataminers.singbiker.entity;

import com.google.android.gms.maps.GoogleMap;

public class Haze {
	int psiLevel;
	String zone;
	public double getPsiLevel() {
		return psiLevel;
	}
	public void setPsiLevel(int psiLevel) {
		this.psiLevel = psiLevel;
	}
	public String getZone() {
		return zone;
	}
	public void setZone(String zone) {
		this.zone = zone;
	}
	
	public String toString(){
		return zone+"\t"+psiLevel;
	}
}
