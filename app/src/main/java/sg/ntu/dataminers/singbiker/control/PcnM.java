package sg.ntu.dataminers.singbiker.control;

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
		//p.printList(p.pointList,"pplist");
		p.createPlacemarkList();
		p.createGraph();
		p.printGraph();
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
	public void readGraph(){
		String filename="C:/users/muham/documents/graph.txt";
		try{
			ObjectInputStream is=new ObjectInputStream(new FileInputStream(filename));
			graph=(Placemark[][])is.readObject();
			is.close();
		}catch(Exception e){

		}


	}


	public void createPlacemarkList(){
		int cur=-1;
		Placemark pm=null;
		for(int i=0;i<pointList.size();i++){
			PcnPoint pp=pointList.get(i);
			if(pp.id!=cur){
				if(cur!=-1)
					graph[cur][0]=pm;
				pm=new Placemark();
				pm.id=pp.id;
				pm.name=pp.name;
				pm.connection=pp.ll;
				pm.waypoints=new ArrayList<Point>();
				cur=pp.id;
			}
			pm.waypoints.add(pp.ll);
			if(i==pointList.size()-1)
				graph[260][0]=pm;
		}
	}
	public void createGraph(){
		for (int i=0;i<pointList.size();i++){
			for(int j=0;j<pointList.size();j++){
				if(Math.abs(pointList.get(i).ll.latitude-pointList.get(j).ll.latitude)<0.005
						&& Math.abs(pointList.get(i).ll.longitude-pointList.get(j).ll.longitude)<0.0005
						&& pointList.get(i).id!=pointList.get(j).id
						){
					double latdiff=Math.abs(pointList.get(i).ll.latitude-pointList.get(j).ll.latitude);
					double longdiff=Math.abs(pointList.get(i).ll.longitude-pointList.get(j).ll.longitude);
					condiff[pointList.get(i).id][pointList.get(j).id].list.add(new ConDiff(latdiff,longdiff,pointList.get(j).ll));

				}
			}
		}
		for (int i=0;i<SIZE;i++){
			for (int j=1;j<SIZE;j++){
				if(condiff[i][j].list.size()>0){
					Collections.sort(condiff[i][j].list);
					ConDiff leastDiff=condiff[i][j].list.get(0);
					Placemark pm=new Placemark();
					pm.connection=leastDiff.con;
					System.out.println("adding edge at "+i+"||"+j);
					graph[i][j]=pm;
				}

			}
		}

	}
	public void resetGraph(){
		for(int i=0;i<SIZE;i++){
			graph[i][0].visited=false;
		}
	}
	public boolean isConnected(int root,int dest){
		resetGraph();
		boolean connected=false;
		Queue<Integer> q=new LinkedList<Integer>();
		q.add(root);
		graph[root][0].visited=true;
		while(!q.isEmpty()){
			int p=q.poll();
			for(int i=1;i<SIZE;i++){
				if(graph[p][i]!=null && !graph[i][0].visited){
					graph[i][0].prev=p;
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
			for(int j=1;j<SIZE;j++){
				if(graph[i][j]!=null){
					System.out.print("-->"+j);
				}
			}
			System.out.println();
		}
	}
	public ArrayList<Integer> getPath(int start,int end){
		ArrayList<Integer> path=new ArrayList<Integer>();
		int prev=graph[end][0].prev;
		while(prev!=start){
			path.add(prev);
			prev=graph[prev][0].prev;
		}
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
							lat=Double.parseDouble(cList[i].substring(2, cList[i].length()-1));
						else
							lat=Double.parseDouble(cList[i]);
						longitude=Double.parseDouble(cList[i+1]);
						PcnPoint p=new PcnPoint();
						String tempId=e.parent().parent().parent().attr("id");
						tempId=tempId.substring(3, tempId.length());
						p.id=Integer.parseInt(tempId);
						p.name=e.parent().parent().parent().select("name").text();
						p.ll=new Point(longitude,lat);
						pointList.add(p);
					}
				}
			}	
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	class ConDiffList{
		ArrayList<ConDiff> list=new ArrayList<ConDiff>();

	}
	class ConDiff implements Comparable<ConDiff>{
		double latdiff;
		double longdiff;
		Point con;

		public ConDiff(double latdiff,double longdiff, Point con) {
			this.longdiff = longdiff;
			this.con = con;
			this.latdiff = latdiff;
		}

		@Override
		public int compareTo(ConDiff other) {
			if(this.latdiff+this.longdiff>other.latdiff+other.longdiff)
				return 1;
			else if(this.latdiff+this.longdiff<other.latdiff+other.longdiff)
				return -1;
			else
				return 0;
		}
	}
	
}
