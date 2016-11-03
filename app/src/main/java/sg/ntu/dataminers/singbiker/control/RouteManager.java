package sg.ntu.dataminers.singbiker.control;

import android.content.Context;
import android.content.SyncStatusObserver;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import sg.ntu.dataminers.singbiker.ApiKeyList;
import sg.ntu.dataminers.singbiker.UrlList;
import sg.ntu.dataminers.singbiker.entity.PcnPoint;
import sg.ntu.dataminers.singbiker.entity.Point;
import sg.ntu.dataminers.singbiker.entity.Route;

/**
 * Created by mukhtar on 10/27/2016.
 */

public class RouteManager extends AsyncTask<Void,Void,Void>{
    private LatLng start;
    private LatLng end;
    private ArrayList<Route> list;
    private boolean done=false;
    private Context context;
    private PcnManager pcnm;

    public RouteManager(LatLng start,LatLng end,Context c){
        this.start=start;
        this.end=end;
        context=c;
        list=new ArrayList<Route>();
        pcnm=new PcnManager(context);
    }
    public boolean isDone(){
        return done;
    }
    public ArrayList<Route> getRoutes(){
        return list;
    }

    private void plotAllRoutes(){
        ArrayList<Route> bufList=new ArrayList<Route>();
        Route pcnRoute=new Route(start,end);
        pcnRoute.setIsPcnRoute(true);
        ArrayList<LatLng> pcnRouteWp=new ArrayList<LatLng>();
        //route connected to pcn
        PcnPoint startpp=pcnm.getNearestPcnPoint(start);
        LatLng startPcnPoint=new LatLng(startpp.ll.getLatitude(),startpp.ll.getLongitude());
        PcnPoint endpp=pcnm.getNearestPcnPoint(end);
        LatLng endPcnPoint=new LatLng(endpp.ll.getLatitude(),endpp.ll.getLongitude());
        bufList=plotDirectRoutes(start,startPcnPoint,true,false);//start-->startpcn
        for(Route r:bufList){
            pcnRouteWp.addAll(r.getWaypoints());
            //list.add(r);
        }
        boolean connected=pcnm.isConnected(startpp.id,endpp.id);
        Route temp=null;
        if(connected){
            System.out.println("INTRA LOOP IS CONNECTED");
            temp=plotIntraLoopRoute(startpp,endpp);//startpcn-->endpcn
            //list.add(temp);
            pcnRouteWp.addAll(temp.getWaypoints());
            System.out.println(temp.getWaypoints());
        }
        else{
            System.out.println("INTRA LOOP NOT CONNECTED");
            int[] arr=pcnm.getExitPoints(startpp,endpp);
            temp=plotIntraLoopRoute(startpp,pcnm.getPcnPointOfPlacemark(arr[0],0));//startpcn-->startloopexit
            pcnRouteWp.addAll(temp.getWaypoints());
            bufList=plotDirectRoutes(pcnm.getLatLngOfPlacemark(arr[0],0),pcnm.getLatLngOfPlacemark(arr[1],0),true,false);//startloopexit-->endloopexit
            for(Route r:bufList){
                pcnRouteWp.addAll(r.getWaypoints());
            }
            temp=plotIntraLoopRoute(pcnm.getPcnPointOfPlacemark(arr[1],0),endpp);;//endloopexit-->endpcn
            pcnRouteWp.addAll(temp.getWaypoints());
            //list.add(temp);
            System.out.println(temp.getWaypoints());
        }

        bufList=plotDirectRoutes(endPcnPoint,end,true,false);//endpcn-->end
        for(Route r:bufList){
            pcnRouteWp.addAll(r.getWaypoints());
            //list.add(r);
        }
        pcnRoute.setWaypoints(pcnRouteWp);
        pcnRoute.setDistanceInMeters(getRouteDistance(pcnRouteWp));
        list.add(pcnRoute);

        //direct routes avoiding highways
        bufList=plotDirectRoutes(start,end,true,false);
        list.addAll(bufList);

    }



    private ArrayList<Route> plotDirectRoutes(LatLng start,LatLng end,boolean avoidHighway,boolean alt){
        ArrayList<Route> rList=new ArrayList<Route>();
        String data=getRawData(start,end,avoidHighway,alt);
        Log.d("bikertag",data);
        try{
            JSONObject jo=new JSONObject(data);
            JSONArray arr=jo.getJSONArray("routes");
            for (int i=0;i<arr.length();i++){
                JSONObject route=arr.getJSONObject(i);
                JSONObject polylineObj=route.getJSONObject("overview_polyline");
                List<LatLng> waypoints=PolyUtil.decode(polylineObj.getString("points"));
                JSONArray legsArr=route.getJSONArray("legs");
                Route r=new Route(start,end);
                r.setWaypoints((ArrayList)waypoints);
                r.setDistanceInMeters(Double.parseDouble(legsArr.getJSONObject(0).getJSONObject("distance").get("value").toString()));
                Log.d("bikertag","adding route to list");
                rList.add(r);
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        return rList;
    }
    private Route plotIntraLoopRoute(PcnPoint startpp,PcnPoint endpp){
        System.out.println("NEW METHOD");
        Route route=new Route(new LatLng(startpp.ll.latitude,startpp.ll.longitude) ,new LatLng(endpp.ll.latitude,endpp.ll.longitude));
        Route temp;
        ArrayList<Integer> path=pcnm.getPath(startpp.id,endpp.id);//startpcn-->endpcn
        ArrayList<LatLng> waypoints=pcnm.getWaypointsOfPlacemark(startpp.id);
        int cur=startpp.id;
        int pos=-1;
        int sign=1;
        //finding position of point in waypoints
        for(int i=0;i<waypoints.size();i++){
            System.out.println(waypoints.get(i)+"\t"+startpp.ll);
            if(waypoints.get(i).latitude == startpp.ll.latitude && waypoints.get(i).longitude == startpp.ll.longitude){
                pos=i;
                break;
            }
        }
        if(startpp.id==endpp.id){
            //points are on the same placemark
            System.out.println("points are on the same placemark");
            if(pcnm.startIsNearerToPoint(startpp.id,endpp.ll)){
                sign=-1;
            }
            else{
                sign=1;
            }
            while(waypoints.get(pos).latitude!=endpp.ll.latitude && waypoints.get(pos).longitude!=endpp.ll.longitude){
                route.addSingleWaypoint(waypoints.get(pos));
                pos=pos+sign;
            }
            return route;
        }
        System.out.println("The start and end point is "+startpp.id+" || "+endpp.id);
        System.out.println("The path for intra loop is "+path);
        if(path.size()==0){
            //the two placemarks are connected
            if(pcnm.startIsNearerToCon(startpp.id,endpp.id)){
                //startpoint is the connection
                System.out.println("Startpoint is the connection");
                sign=-1;
            }
            else{
                System.out.println("Endpoint is the connection");
                sign=1;
            }
            LatLng[] conList=pcnm.getConnectionBetweenPlacemarks(startpp.id,endpp.id);
            System.out.println("THE POS "+pos+" THE SIZE "+waypoints.size());
            while(true){
                route.addSingleWaypoint(waypoints.get(pos));
                if(waypoints.get(pos).latitude==conList[0].latitude && waypoints.get(pos).longitude==conList[0].longitude
                        || waypoints.get(pos).latitude==conList[1].latitude && waypoints.get(pos).longitude==conList[1].longitude){
                    break;
                }
                pos=pos+sign;
            }

        }
        else{
            System.out.println("the two placemarks are not directly connected");
            if(pcnm.startIsNearerToCon(startpp.id,path.get(0))){
                //startpoint is the connection
                System.out.println("Startpoint is the connection");
               sign=-1;
            }
            else{
                System.out.println("Endpoint is the connection");
                sign=1;
            }

            for(int i=0;i<path.size();i++){
                int next=path.get(i);
                LatLng[] conList=pcnm.getConnectionBetweenPlacemarks(cur,next);
                System.out.println("THE CUR "+cur+" THE NEXT "+next);
                System.out.println("THE CONNECTIONS "+conList[0]+"||||"+conList[1]);
                System.out.println("CUR "+cur+"\t"+waypoints.size());
                while(true){
                    System.out.println(waypoints.get(pos)+"\t"+conList[0]);
                    route.addSingleWaypoint(waypoints.get(pos));
                    if(waypoints.get(pos).latitude==conList[0].latitude && waypoints.get(pos).longitude==conList[0].longitude
                            || waypoints.get(pos).latitude==conList[1].latitude && waypoints.get(pos).longitude==conList[1].longitude){
                        break;
                    }
                    pos=pos+sign;

                }
                cur=next;
                waypoints=pcnm.getWaypointsOfPlacemark(cur);
                pos=0;
                sign=1;
            }

        }

        waypoints=pcnm.getWaypointsOfPlacemark(endpp.id);
        for(int i=0;i<waypoints.size();i++){
            if(endpp.ll.latitude == waypoints.get(i).latitude && endpp.ll.longitude == waypoints.get(i).longitude)
                break;
            route.addSingleWaypoint(waypoints.get(i));
        }
        return route;
    }
    public double getRouteDistance(ArrayList<LatLng> list){
        double dist=0;
        Location a=new Location("a");
        Location b=new Location("b");
        for(int i=0;i<list.size();i++){
            if(i!=list.size()-1){
                a.setLatitude(list.get(i).latitude);
                a.setLatitude(list.get(i).longitude);
                b.setLatitude(list.get(i+1).latitude);
                b.setLatitude(list.get(i+1).longitude);
                dist+=a.distanceTo(b);
            }
        }
        return dist;
    }
    private String getRawData(LatLng start,LatLng end,boolean avoidHighway,boolean alt){
        String urlString= UrlList.GOOGLE_DIRECTIONS;
        urlString+="origin="+start.latitude+","+start.longitude;
        urlString+="&destination="+end.latitude+","+end.longitude;
        urlString+="&region=SG";
        urlString+="&alternatives="+alt;
        urlString+="&key="+ ApiKeyList.GOOGLE_MAPS;
        if(avoidHighway){
            urlString+="&avoid=highways";
        }
        String data="";
        Log.d("bikertag",urlString);
        try{
            URL url=new URL(urlString);
            HttpURLConnection con=(HttpURLConnection) url.openConnection();
            BufferedReader br=new BufferedReader(new InputStreamReader(con.getInputStream()));
            String x="";
            while((x=br.readLine())!=null){
                data=data.concat(x);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return data;
    }

    @Override
    protected Void doInBackground(Void... params) {
        plotAllRoutes();
        done=true;
        Log.d("bikertag","finished bg task");
        return null;
    }


}
