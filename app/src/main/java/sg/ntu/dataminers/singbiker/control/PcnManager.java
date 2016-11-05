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
	Placemark[] placemarkList;
	Placemark[][] graph;
	public PcnManager(Context c){
		context=c;
		init();
	}

	public void readPointList(){
		try{
			InputStream is=context.getResources().openRawResource(R.raw.pointlist);
			ObjectInputStream os=new ObjectInputStream(is);
			pointList=(ArrayList<PcnPoint>)os.readObject();
			os.close();
		}catch(Exception e){
			Log.d("bikertag", Log.getStackTraceString(new Exception()));
		}
	}
	public void readGraph(){
		try{
			InputStream is=context.getResources().openRawResource(R.raw.placemarklist);
			ObjectInputStream os=new ObjectInputStream(is);
			placemarkList=(Placemark[])os.readObject();
			os.close();
		}catch(Exception e){
			Log.d("bikertag", Log.getStackTraceString(new Exception()));
		}
		
	}
	public void readPlacemarkList(){
		try{
			InputStream is=context.getResources().openRawResource(R.raw.graph);
			ObjectInputStream os=new ObjectInputStream(is);
			graph=(Placemark[][])os.readObject();
			os.close();
		}catch(Exception e){
			Log.d("bikertag", Log.getStackTraceString(new Exception()));
		}
	}
	public void init(){
		readPointList();
		readGraph();
		readPlacemarkList();
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
	public boolean isConnectedAlternative(int root,int dest){
		resetGraph();
		if(root==dest)
			return true;
		boolean connected=false;
		Queue<Integer> q=new LinkedList<Integer>();
		q.add(root);
		placemarkList[root].visited=true;
		while(!q.isEmpty()){
			int p=q.poll();
			for(int i=SIZE-1;i>=0;i--){
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
	public ArrayList<Integer> getPath(int start,int end,boolean alternative){
		if(alternative)
			isConnectedAlternative(start,end);
		else
			isConnected(start,end);
		System.out.println("Getting path for "+start+" and "+end);
		ArrayList<Integer> path=new ArrayList<Integer>();
		path.add(end);
		if(start==end){
			return path;
		}
		int prev=placemarkList[end].prev;
		while(prev!=start){
			path.add(prev);
			prev=placemarkList[prev].prev;
		}
		path.add(start);
		Collections.reverse(path);
		return path;
	}
	public ArrayList<Integer> getPlacemarksInLoop(int root){
		ArrayList<Integer> list=new ArrayList<Integer>();
		resetGraph();
		boolean connected=false;
		Queue<Integer> q=new LinkedList<Integer>();
		list.add(root);
		q.add(root);
		placemarkList[root].visited=true;
		while(!q.isEmpty()){
			int p=q.poll();
			for(int i=0;i<SIZE;i++){
				if(graph[p][i]!=null && !placemarkList[i].visited){
					placemarkList[i].prev=p;
					list.add(i);
					q.add(i);
					placemarkList[i].visited=true;
				}
			}
		}

		return list;

	}
	public int[] getExitPoints(PcnPoint startPcnPoint,PcnPoint endPcnPoint){
		int[] arr=new int[2];
		//use params to get placemarks in the two loops
		ArrayList<Integer> firstLoop=getPlacemarksInLoop(startPcnPoint.id);
		ArrayList<Integer> secondLoop=getPlacemarksInLoop(endPcnPoint.id);
		double[][] dist=new double[firstLoop.size()][secondLoop.size()];
		double min=Double.MAX_VALUE;
		for(int i=0;i<firstLoop.size();i++){
			Location a=new Location("a");
			a.setLatitude(placemarkList[firstLoop.get(i)].connectionone.getLatitude());
			a.setLongitude(placemarkList[firstLoop.get(i)].connectionone.getLongitude());
			for(int j=0;j<secondLoop.size();j++){
				Location b=new Location("b");
				b.setLatitude(placemarkList[secondLoop.get(j)].connectionone.getLatitude());
				b.setLongitude(placemarkList[secondLoop.get(j)].connectionone.getLongitude());
				double d=a.distanceTo(b);
				dist[i][j]=d;
				if(d<min){
					min=d;
					arr[0]=firstLoop.get(i);
					arr[1]=secondLoop.get(j);
				}
			}
		}
		LatLng[] llarr=new LatLng[2];
		llarr[0]=new LatLng(placemarkList[arr[0]].connectionone.latitude,placemarkList[arr[0]].connectionone.longitude);
		llarr[1]=new LatLng(placemarkList[arr[1]].connectionone.latitude,placemarkList[arr[1]].connectionone.longitude);
		return arr;
	}
	public PcnPoint getNearestPcnPoint(LatLng s,ArrayList<Integer> activatedPlacemarks){
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
		if(activatedPlacemarks==null)
			return pointList.get(0);
		for(int i=0;i<pointList.size();i++){
			if(notInActivatedPlacemarks(pointList.get(i).id,activatedPlacemarks)){
				return pointList.get(i);
			}
		}
		return null;

	}
	private boolean notInActivatedPlacemarks(int placemark,ArrayList<Integer> activatedPlacemarks){
		for(int i=0;i<activatedPlacemarks.size();i++){
			if(activatedPlacemarks.get(i)==placemark)
				return false;
		}
		return true;
	}
	public double distance(Point start,Point end){
		Location a=new Location("a");
		a.setLatitude(start.latitude);
		a.setLongitude(start.longitude);
		Location b=new Location("b");
		b.setLatitude(end.latitude);
		b.setLongitude(end.longitude);
		return a.distanceTo(b);
	}
	public boolean startIsNearerToCon(int first,int second){
		System.out.println(first+"|||||"+second);
		if( distance(placemarkList[first].connectionone,graph[first][second].connectionone) > distance(placemarkList[first].connectiontwo,graph[first][second].connectionone))
			return false;
		else
			return true;
	}
	public boolean startIsNearerToPoint(int first,Point p){
		System.out.println(first+"\t"+p);
		if( distance(placemarkList[first].connectionone,p) > distance(placemarkList[first].connectiontwo,p))
			return false;
		else
			return true;
	}
	public LatLng getLatLngOfPlacemark(int placemark,int bound){
		if(bound==0)
			return new LatLng(placemarkList[placemark].connectionone.latitude,placemarkList[placemark].connectionone.longitude);
		else if(bound==1)
			return new LatLng(placemarkList[placemark].connectiontwo.latitude,placemarkList[placemark].connectiontwo.longitude);
		else
			return null;
	}
	public PcnPoint getPcnPointOfPlacemark(int placemark,int bound){
		PcnPoint pp=new PcnPoint();
		pp.id=placemark;
		if(bound==0)
			pp.ll=placemarkList[placemark].connectionone;
		else if(bound ==1)
			pp.ll=placemarkList[placemark].connectiontwo;
		return pp;
	}

	public ArrayList<LatLng> getWaypointsOfPlacemark(int placemark){
		ArrayList<LatLng> waypoints=new ArrayList<LatLng>();
		ArrayList<Point> tempList=placemarkList[placemark].waypoints;
		for(Point p:tempList){
			waypoints.add(new LatLng(p.latitude,p.longitude));
		}
		return waypoints;
	}
	public LatLng[] getConnectionBetweenPlacemarks(int start,int end){
		LatLng conone=null;
		LatLng contwo=null;
		if(start==end){
			conone=new LatLng(placemarkList[start].connectionone.latitude,placemarkList[start].connectionone.longitude);
			contwo=new LatLng(placemarkList[start].connectionone.latitude,placemarkList[start].connectionone.longitude);
		}
		else if(end==0){
			conone=new LatLng(graph[end][start].connectionone.latitude,graph[end][start].connectionone.longitude);
			contwo=new LatLng(graph[end][start].connectiontwo.latitude,graph[end][start].connectiontwo.longitude);
		}
		else{
			conone=new LatLng(graph[start][end].connectionone.latitude,graph[start][end].connectionone.longitude);
			contwo=new LatLng(graph[start][end].connectiontwo.latitude,graph[start][end].connectiontwo.longitude);
		}

		LatLng[] arr={conone,contwo};
		return arr;
	}
	public Placemark[] getPlacemarks(){
		return placemarkList;
	}

}
