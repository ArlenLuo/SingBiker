package sg.ntu.dataminers.singbiker.entity;

import java.io.Serializable;

public class Point implements Serializable{
	public double latitude;
	public double longitude;
	
	public Point(double latitude,double longitude){
		this.latitude=latitude;
		this.longitude=longitude;
	}
	public double getLatitude(){
		return latitude;
	}
	public double getLongitude(){
		return longitude;
	}
	@Override
	public String toString() {
		return "LatLng [latitude=" + latitude + ", longitude=" + longitude + "]";
	}
	}
