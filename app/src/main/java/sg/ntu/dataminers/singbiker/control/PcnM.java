package sg.ntu.dataminers.singbiker.control;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import sg.ntu.dataminers.singbiker.entity.PcnPoint;
import sg.ntu.dataminers.singbiker.entity.Placemark;
import sg.ntu.dataminers.singbiker.entity.Point;

public class PcnM {
	final static int SIZE=261;
	String filename="C:/users/muham/documents/pcn.kml";
	static String logFile="C:/users/muham/documents/log.txt";
	ArrayList<PcnPoint> pointList;
	Placemark[] placemarkList;
 	Placemark[][] graph;
	ConDiffList[][] condiff;
	public PcnM(){
		init();
	}
	public static void main(String[] args){
		PcnM p=new PcnM();
		try{
			System.setOut(new PrintStream(logFile));
		}catch(Exception e){
			
		}
		p.createPointList();
		p.printList(p.pointList,"pplist");
		p.createPlacemarkList();
		for(Placemark x:p.placemarkList){
			System.out.println(x);
		}
		p.createGraph();
		p.printGraph();
		p.writePointList();
		p.writePlacemarkList();
		p.writeGraph();
		//p.readGraph();
		Scanner sc=new Scanner(System.in);
//		while(true){
//			try{
//			System.out.println("enter query: ");
//			String x=sc.nextLine();
//			String[] list=x.split(" ");
//			int start=Integer.parseInt(list[0]);
//			int end=Integer.parseInt(list[1]);
//			boolean connected=p.isConnected(start, end);
//			if(connected){
//				ArrayList<Integer> path=p.getPath(start, end);
//				p.printList(path, "path");
//			}
//			else{
//				System.out.println("not connected");
//			}
//			System.out.println();
//			}catch(Exception e){
//				e.printStackTrace();
//				continue;
//			}
//
//
//		}
		
		
	}
	public void init(){
		placemarkList=new Placemark[SIZE];
		graph=new Placemark[SIZE][SIZE];
		condiff=new ConDiffList[SIZE][SIZE];
		pointList=new ArrayList<PcnPoint>();
		//null graph
		for(int i=0;i<SIZE;i++){
			for(int j=0;j<SIZE;j++){
				graph[i][j]=null;
				condiff[i][j]=new ConDiffList();
			}
		}

	}
	public void writePointList(){
		String filename="C:/users/muham/documents/pointlist.txt";
		try{
			ObjectOutputStream os=new ObjectOutputStream(new FileOutputStream(filename));
			os.writeObject(pointList);
			os.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public void writePlacemarkList(){
		String filename="C:/users/muham/documents/placemarklist.txt";
		try{
			ObjectOutputStream os=new ObjectOutputStream(new FileOutputStream(filename));
			os.writeObject(placemarkList);
			os.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public void writeGraph(){
		String filename="C:/users/muham/documents/graph.txt";
		try{
			ObjectOutputStream os=new ObjectOutputStream(new FileOutputStream(filename));
			os.writeObject(graph);
			os.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void createPlacemarkList(){
		int cur=-1;
		Placemark pm=null;
		for(int i=0;i<pointList.size();i++){
			PcnPoint pp=pointList.get(i);
			if(pp.id!=cur){
				if(cur!=-1)
					placemarkList[cur]=pm;
				pm=new Placemark();
				pm.id=pp.id;
				pm.name=pp.name;
				pm.waypoints=new ArrayList<Point>();
				cur=pp.id;
			}
			pm.waypoints.add(pp.ll);
			if(i==pointList.size()-1)
				placemarkList[260]=pm;
		}
		initPlacemarkPoints();
	}
	public void initPlacemarkPoints(){
		for(int i=0;i<SIZE;i++){
			placemarkList[i].connectionone=placemarkList[i].waypoints.get(0);
			placemarkList[i].connectiontwo=placemarkList[i].waypoints.get(placemarkList[i].waypoints.size()-1);
		}
	}

	public void createGraph(){
		for (int i=0;i<pointList.size();i++){
			for(int j=0;j<pointList.size();j++){
				if(pointList.get(i).id!=pointList.get(j).id){
					double diff=distance(pointList.get(i).ll,pointList.get(j).ll);
					if(diff<0.04){
						condiff[pointList.get(i).id][pointList.get(j).id].list.add(new ConDiff(diff,pointList.get(j).ll,pointList.get(i).ll));
					}


				}
			}
		}
		for (int i=0;i<SIZE;i++){
			for (int j=0;j<SIZE;j++){
				if(i!=j && condiff[i][j].list.size()>0){
					Collections.sort(condiff[i][j].list);
					ConDiff leastDiff=condiff[i][j].list.get(0);
					Placemark pm=new Placemark();
					pm.connectionone=leastDiff.con;
					pm.connectiontwo=leastDiff.contwo;
					System.out.println("adding edge at "+i+"||"+j+" ConOne == "+pm.connectionone+" ConTwo == "+pm.connectiontwo);
					graph[i][j]=pm;
				}

			}
		}

	}
	public void resetGraph(){
		for(int i=0;i<SIZE;i++){
			placemarkList[i].visited=false;
		}
	}
	public boolean isConnected(int root,int dest){
		resetGraph();
		if(root==dest)
			return true;
		boolean connected=false;
		Queue<Integer> q=new LinkedList<Integer>();
		q.add(root);
		placemarkList[root].visited=true;
		while(!q.isEmpty()){
			int p=q.poll();
			for(int i=0;i<SIZE;i++){
				if(graph[p][i]!=null && !placemarkList[i].visited){
					placemarkList[i].prev=p;
					q.add(i);
					placemarkList[i].visited=true;
					if(i==dest){
						return true;
					}
				}
			}
		}

		return connected;
	}
	
	public <T> void printList(ArrayList<T> list,String x){
		System.out.println("Printing "+x);
		for (Object o:list){
			System.out.println(o);
		}
	}
	public void printGraph(){
		for (int i=0;i<SIZE;i++){
			System.out.print("|"+i+"|");
			for(int j=0;j<SIZE;j++){
				if(graph[i][j]!=null){
					System.out.print("-->"+j);
				}
			}
			System.out.println();
		}
	}
	public ArrayList<Integer> getPath(int start,int end){
		isConnected(start,end);
		System.out.println("Getting path for "+start+"||||"+end);
		ArrayList<Integer> path=new ArrayList<Integer>();
		path.add(start);
		if(start==end){
			path.add(end);
			return path;
		}
		int prev=placemarkList[end].prev;
		while(prev!=start){
			path.add(prev);
			prev=placemarkList[prev].prev;
		}
		path.add(end);
		Collections.reverse(path);
		return path;
	}
	public Placemark getPlacemark(Point point){
		for (int i=0;i<SIZE;i++){
			ArrayList<Point> waypoints=graph[i][0].waypoints;
			for(Point p:waypoints){
				if(point.latitude == p.latitude && point.longitude == p.longitude){
					return graph[i][0];
				}
			}
		}
		return null;
	}
	public void createPointList(){
		
		try{
			StringBuilder sb=new StringBuilder();
			//read file
			BufferedReader br=new BufferedReader(new FileReader(filename));
			String l=br.readLine();
			while(l!=null){
				sb.append(l);
				l=br.readLine();
			}
			br.close();
			
			Document doc=Jsoup.parse(sb.toString(), "", Parser.xmlParser());
			Elements list=doc.select("Coordinates");
			for (Element e:list){
				String coords=e.text();
				String[] cList=coords.split(",");
				for (int i=0;i<cList.length;i+=2){
					if(i<cList.length-1){
						double lat=0;
						double longitude=0;
						if(i!=0)
							longitude=Double.parseDouble(cList[i].substring(2));
						else
							longitude=Double.parseDouble(cList[i]);
						lat=Double.parseDouble(cList[i+1]);
						PcnPoint p=new PcnPoint();
						String tempId=e.parent().parent().parent().attr("id");
						tempId=tempId.substring(3, tempId.length());
						p.id=Integer.parseInt(tempId);
						p.name=e.parent().parent().parent().select("name").text();
						p.ll=new Point(lat,longitude);
						pointList.add(p);
					}
				}
			}	
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	public double distance(Point one,Point two) {
		double p = 0.017453292519943295;    // Math.PI / 180
		double a = 0.5 - Math.cos((two.latitude - one.latitude) * p)/2 +
				Math.cos(one.latitude * p) * Math.cos(two.latitude * p) *
						(1 - Math.cos((two.longitude - one.longitude) * p))/2;

		return 12742 * Math.asin(Math.sqrt(a)); // 2 * R; R = 6371 km
	}
	class ConDiffList{
		ArrayList<ConDiff> list=new ArrayList<ConDiff>();

	}
	class ConDiff implements Comparable<ConDiff>{
		double diff;
		Point con;
		Point contwo;

		public ConDiff(double diff, Point con,Point contwo) {
			this.con = con;
			this.contwo=contwo;
			this.diff = diff;
		}

		@Override
		public int compareTo(ConDiff other) {
			if(this.diff>other.diff)
				return 1;
			else if(this.diff<other.diff)
				return -1;
			else
				return 0;
		}
	}

	
}
