package sg.ntu.dataminers.singbiker.control;

import android.content.Context;
import android.content.SyncStatusObserver;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
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
import sg.ntu.dataminers.singbiker.entity.Placemark;
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
    private ArrayList<Integer> activatedPlacemarks;
    public RouteManager(LatLng start,LatLng end,Context c){
        this.start=start;
        this.end=end;
        context=c;
        list=new ArrayList<Route>();
        activatedPlacemarks=new ArrayList<Integer>();
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
        Route one=plotPcnRoute(start,end,false);
        one.setIsPcnRoute(true);
        list.add(one);
        //Route two=plotPcnRoute(start,end,true);
        //two.setIsPcnRoute(true);

        //if(one.getDistanceInMeters()!=two.getDistanceInMeters())
            //list.add(two);

        //direct routes avoiding highways
        bufList=plotDirectRoutes(start,end,true,false);
        list.addAll(bufList);

    }
    public Route plotPcnRoute(LatLng start,LatLng end,boolean alternative){
        System.out.println("Plotting pcn route");
        ArrayList<Route> bufList=new ArrayList<Route>();
        Route pcnRoute=new Route(start,end);
        pcnRoute.setIsPcnRoute(true);
        ArrayList<LatLng> pcnRouteWp=new ArrayList<LatLng>();
        //route connected to pcn
        PcnPoint startpp=pcnm.getNearestPcnPoint(start,activatedPlacemarks);
        PcnPoint endpp=pcnm.getNearestPcnPoint(end,activatedPlacemarks);
        if(startpp==null || endpp==null){
            System.out.println("No pcn route left.Returning direct route");
            bufList=plotDirectRoutes(start,end,true,false);//startloopexit-->endloopexit
            for(Route r:bufList){
                pcnRouteWp.addAll(r.getWaypoints());
                return pcnRoute;
            }
        }
        LatLng startPcnPoint=new LatLng(startpp.ll.getLatitude(),startpp.ll.getLongitude());
        LatLng endPcnPoint=new LatLng(endpp.ll.getLatitude(),endpp.ll.getLongitude());
        activatedPlacemarks.addAll(pcnm.getPlacemarksInLoop(startpp.id));
        if(!pcnm.isConnected(startpp.id,endpp.id)){
            activatedPlacemarks.addAll(pcnm.getPlacemarksInLoop(endpp.id));
        }

        bufList=plotDirectRoutes(start,startPcnPoint,true,false);//start-->startpcn
        for(Route r:bufList){
            pcnRouteWp.addAll(r.getWaypoints());
            //list.add(r);
        }
        boolean connected=pcnm.isConnected(startpp.id,endpp.id);
        Route temp=null;
        if(connected){
            System.out.println("STARTPCNPOINT AND ENDPCNPOINT CONNECTED");
            temp=plotIntraLoopRoute(startpp,endpp,alternative);//startpcn-->endpcn
            //list.add(temp);
            pcnRouteWp.addAll(temp.getWaypoints());
            System.out.println(temp.getWaypoints());
        }
        else{
            System.out.println("STARTPCNPOINT AND ENDPCNPOINT NOT CONNECTED");
            int[] arr=pcnm.getExitPoints(startpp,endpp);
            temp=plotIntraLoopRoute(startpp,pcnm.getPcnPointOfPlacemark(arr[0],0),alternative);//startpcn-->startloopexit
            pcnRouteWp.addAll(temp.getWaypoints());
            pcnRouteWp.addAll(plotPcnRoute(pcnm.getLatLngOfPlacemark(arr[0],0),pcnm.getLatLngOfPlacemark(arr[1],0),alternative).getWaypoints());//startloopexit-->endloopexit
            temp=plotIntraLoopRoute(pcnm.getPcnPointOfPlacemark(arr[1],0),endpp,alternative);;//endloopexit-->endpcn
            pcnRouteWp.addAll(temp.getWaypoints());
            //list.add(temp);
        }

        bufList=plotDirectRoutes(endPcnPoint,end,true,false);//endpcn-->end
        for(Route r:bufList){
            pcnRouteWp.addAll(r.getWaypoints());
            //list.add(r);
        }
        pcnRoute.setWaypoints(pcnRouteWp);
        pcnRoute.setDistanceInMeters(getRouteDistance(pcnRouteWp));
        return pcnRoute;
    }
    private Route plotIntraLoopRoute(PcnPoint startpp,PcnPoint endpp,boolean alternative){
        System.out.println("Plotting intra route");
        Route route=new Route(new LatLng(startpp.ll.latitude,startpp.ll.longitude) ,new LatLng(endpp.ll.latitude,endpp.ll.longitude));
        Route temp;
        ArrayList<Integer> path=pcnm.getPath(startpp.id,endpp.id,alternative);//startpcn-->endpcn
        PcnPoint curPoint=startpp;
        int pos=-1;
        int conpos=-1;
        int sign=1;
        LatLng[] conlist=null;
        int next=0;
        //finding position of point in waypoints
        for(int i=0;i<path.size();i++){
            ArrayList<LatLng> waypoints=pcnm.getWaypointsOfPlacemark(curPoint.id);
            //find pos of curpoint
            //System.out.println("Finding pos of curpoint :"+curPoint+"\tID: "+curPoint.id);
            for(int j=0;j<waypoints.size();j++){
                //System.out.println(waypoints.get(j)+"\t"+curPoint.ll);
                if(waypoints.get(j).latitude == curPoint.ll.latitude && waypoints.get(j).longitude == curPoint.ll.longitude){
                    pos=j;
                    break;
                }
            }
            //System.out.println("Found at :"+pos);
            if(i==path.size()-1){
                conlist=new LatLng[2];
                conlist[0]=new LatLng(endpp.ll.latitude,endpp.ll.longitude);
                conlist[1]=new LatLng(endpp.ll.latitude,endpp.ll.longitude);
            }
            else{
                next=path.get(i+1);
                //get conpoint
               // System.out.println("Getting connection for "+curPoint.id+" and "+next);
                conlist=pcnm.getConnectionBetweenPlacemarks(curPoint.id,next);
            }
            //traverse add points
            for(int z=0;z<waypoints.size();z++){
                if(waypoints.get(z).latitude == conlist[0].latitude && waypoints.get(z).longitude == conlist[0].longitude){
                    curPoint=new PcnPoint();
                    curPoint.id=next;
                    curPoint.ll=new Point(conlist[1].latitude,conlist[1].longitude);
                    conpos=z;
                    break;
                }
                if(waypoints.get(z).latitude == conlist[1].latitude && waypoints.get(z).longitude == conlist[1].longitude){
                    curPoint=new PcnPoint();
                    curPoint.id=next;
                    curPoint.ll=new Point(conlist[0].latitude,conlist[0].longitude);
                    conpos=z;
                    break;
                }
            }
            //System.out.println("Conpos ="+conpos+"\tPos ="+pos);
            if(pos>=conpos){
                for(int z=pos;conpos<z;z--){
                    route.addSingleWaypoint(waypoints.get(z));
                }
            }
            else{
                for(int z=pos;z<conpos;z++){
                    route.addSingleWaypoint(waypoints.get(z));
                }
            }
        }
        return route;
    }
    private boolean noLoopInBetween(LatLng start,LatLng end){
        LatLngBounds.Builder pg=new LatLngBounds.Builder();
        pg.include(start);
        pg.include(new LatLng(start.latitude,end.longitude));
        pg.include(end);
        pg.include(new LatLng(end.latitude,start.longitude));
        LatLngBounds bounds=pg.build();
        Placemark[] arr=pcnm.getPlacemarks();
        for(int i=0;i<arr.length;i++){
            if(notInActivatedPlacemarks(arr[i])){
                LatLng ll=new LatLng(arr[i].connectionone.latitude,arr[i].connectionone.longitude);
                if(bounds.contains(ll)){
                    return false;
                }
            }

        }
        return true;
    }
    private boolean notInActivatedPlacemarks(Placemark m){
        for(int i=0;i<activatedPlacemarks.size();i++){
            if(activatedPlacemarks.get(i)==m.id)
                return false;
        }
        return true;
    }
    private ArrayList<Route> plotDirectRoutes(LatLng start,LatLng end,boolean avoidHighway,boolean alt){
        ArrayList<Route> rList=new ArrayList<Route>();
        String data=getRawData(start,end,avoidHighway,alt);
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
                r.setIsPcnRoute(false);
                rList.add(r);
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        return rList;
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
        return null;
    }


}
