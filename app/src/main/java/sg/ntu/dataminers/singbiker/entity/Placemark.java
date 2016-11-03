package sg.ntu.dataminers.singbiker.entity;

import java.io.Serializable;
import java.util.ArrayList;

public class Placemark implements Serializable{
	public int id;
	public String name;
	public ArrayList<Point> waypoints;
	public Point connectionone;
	public Point connectiontwo;
	public boolean visited=false;
	public int prev;

}
