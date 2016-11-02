package sg.ntu.dataminers.singbiker.control;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import sg.ntu.dataminers.singbiker.R;
import sg.ntu.dataminers.singbiker.entity.PcnPoint;
import sg.ntu.dataminers.singbiker.entity.Placemark;
import sg.ntu.dataminers.singbiker.entity.Point;

public class PcnManager {
	final static int SIZE=261;
	ArrayList<PcnPoint> pointList;
	Context context;
	Placemark[][] graph;
	public PcnManager(Context c){
		context=c;
		init();
	}


	public void readGraph(){
		try{
			Log.d("bikertag","entered reader graph");
			InputStream is=context.getResources().openRawResource(R.raw.graph);
			ObjectInputStream os=new ObjectInputStream(is);
			graph=(Placemark[][])os.readObject();
			os.close();
		}catch(Exception e){
			Log.d("bikertag", Log.getStackTraceString(new Exception()));
		}
		
	}
	public void init(){
		graph=new Placemark[SIZE][SIZE];
		for(int i=0;i<SIZE;i++){
			for(int j=0;j<SIZE;j++){
				graph[i][j]=null;
			}
		}
		pointList=new ArrayList<PcnPoint>();
		createPointList();
		readGraph();
	}

	public void resetGraph(){
		for(int i=0;i<SIZE;i++){
			graph[i][0].visited=false;
		}
	}
	public boolean isConnected(int root,int dest){
		resetGraph();
		if(root==dest)
			return true;
		boolean connected=false;
		Queue<Integer> q=new LinkedList<Integer>();
		q.add(root);
		graph[root][0].visited=true;
		while(!q.isEmpty()){
			int p=q.poll();
			for(int i=1;i<SIZE;i++){
				if(graph[p][i]!=null && !graph[i][0].visited){
					graph[i][0].prev=p;
					q.add(i);
					graph[i][0].visited=true;
					if(i==dest){
						return true;
					}
				}
			}
		}

		return connected;
	}
	public ArrayList<Integer> getPlacemarksInLoop(int root){
		ArrayList<Integer> list=new ArrayList<Integer>();
		resetGraph();
		boolean connected=false;
		Queue<Integer> q=new LinkedList<Integer>();
		list.add(root);
		q.add(root);
		graph[root][0].visited=true;
		while(!q.isEmpty()){
			int p=q.poll();
			for(int i=1;i<SIZE;i++){
				if(graph[p][i]!=null && !graph[i][0].visited){
					graph[i][0].prev=p;
					list.add(i);
					q.add(i);
					graph[i][0].visited=true;
				}
			}
		}

		return list;

	}
	public LatLng[] getExitPoints(PcnPoint startPcnPoint,PcnPoint endPcnPoint){
		int[] arr=new int[2];
		//use params to get placemarks in the two loops
		ArrayList<Integer> firstLoop=getPlacemarksInLoop(startPcnPoint.id);
		ArrayList<Integer> secondLoop=getPlacemarksInLoop(endPcnPoint.id);
		Log.d("bikertag","first loop: "+firstLoop);
		Log.d("bikertag","second loop: "+secondLoop);
		double[][] dist=new double[firstLoop.size()][secondLoop.size()];
		double min=Double.MAX_VALUE;
		for(int i=0;i<firstLoop.size();i++){
			Location a=new Location("a");
			a.setLatitude(graph[firstLoop.get(i)][0].connection.getLatitude());
			a.setLongitude(graph[firstLoop.get(i)][0].connection.getLongitude());
			for(int j=0;j<secondLoop.size();j++){
				Location b=new Location("b");
				b.setLatitude(graph[secondLoop.get(j)][0].connection.getLatitude());
				b.setLongitude(graph[secondLoop.get(j)][0].connection.getLongitude());
				double d=a.distanceTo(b);
				dist[i][j]=d;
				if(d<min){
					min=d;
					arr[0]=i;
					arr[1]=j;
				}
			}
		}
		LatLng[] llarr=new LatLng[2];
		llarr[0]=new LatLng(graph[firstLoop.get(arr[0])][0].connection.latitude,graph[firstLoop.get(arr[0])][0].connection.longitude);
		llarr[1]=new LatLng(graph[secondLoop.get(arr[1])][0].connection.latitude,graph[secondLoop.get(arr[1])][0].connection.longitude);
		return llarr;
	}
	public PcnPoint getNearestPcnPoint(LatLng s){
		Location start=new Location("start");
		start.setLongitude(s.longitude);
		start.setLatitude(s.latitude);
		Location end=new Location("end");
		for (PcnPoint p:pointList){
			end.setLatitude(p.ll.latitude);
			end.setLongitude(p.ll.longitude);
			p.diff=start.distanceTo(end);
		}
		Collections.sort(pointList);
		return pointList.get(0);

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
	public ArrayList<LatLng> getAllConnections(){
		ArrayList<LatLng> list=new ArrayList<LatLng>();
		for(int i=0;i<SIZE;i++){
			for(int j=1;j<SIZE;j++){
				if(graph[i][j]!=null){
					list.add(new LatLng(graph[i][j].connection.latitude,graph[i][j].connection.longitude));
				}
			}
		}

		return list;
	}
	public void createPointList(){
		
		try{
			StringBuilder sb=new StringBuilder();
			//read file
			InputStream is=context.getResources().openRawResource(R.raw.pcn);
			BufferedReader br=new BufferedReader(new InputStreamReader(is));
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
	
}
