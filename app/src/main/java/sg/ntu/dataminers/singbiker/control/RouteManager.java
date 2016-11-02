package sg.ntu.dataminers.singbiker.control;

import android.content.Context;
import android.content.SyncStatusObserver;
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
        ArrayList<LatLng> pcnRouteWp=new ArrayList<LatLng>();
        //route connected to pcn
        PcnPoint startpp=pcnm.getNearestPcnPoint(start);
        LatLng startPcnPoint=new LatLng(startpp.ll.getLatitude(),startpp.ll.getLongitude());
        PcnPoint endpp=pcnm.getNearestPcnPoint(end);
        LatLng endPcnPoint=new LatLng(endpp.ll.getLatitude(),endpp.ll.getLongitude());
        bufList=plotDirectRoutes(start,startPcnPoint,true,false);//start-->startpcn
        for(Route r:bufList){
            pcnRouteWp.addAll(r.getWaypoints());
        }
        boolean connected=pcnm.isConnected(startpp.id,endpp.id);
        if(connected){
            ArrayList<Integer> path=pcnm.getPath(startpp.id,endpp.id);//startpcn-->endpcn
        }
        else{
            LatLng[] arr=pcnm.getExitPoints(startpp,endpp);

            bufList=plotDirectRoutes(startPcnPoint,arr[0],true,false);//startpcn-->startloopexit
            for(Route r:bufList){
                pcnRouteWp.addAll(r.getWaypoints());
            }

            bufList=plotDirectRoutes(arr[0],arr[1],true,false);//startloopexit-->endloopexit
            for(Route r:bufList){
               pcnRouteWp.addAll(r.getWaypoints());
            }
            bufList=plotDirectRoutes(arr[1],endPcnPoint,true,false);//endloopexit-->endpcn
            for(Route r:bufList){
                pcnRouteWp.addAll(r.getWaypoints());
            }
        }
        bufList=plotDirectRoutes(endPcnPoint,end,true,false);//endpcn-->end
        for(Route r:bufList){
            pcnRouteWp.addAll(r.getWaypoints());
        }
        pcnRoute.setWaypoints(pcnRouteWp);
        list.add(pcnRoute);

        //direct routes avoiding highways
        bufList=plotDirectRoutes(start,end,true,true);
       // list.addAll(bufList);
        //direct routes without avoiding highways
        bufList=plotDirectRoutes(start,end,false,true);
       // list.addAll(bufList);
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
                Route r=new Route(start,end);
                r.setWaypoints(waypoints);
                r.setAvoidHighway(avoidHighway);
                Log.d("bikertag","adding route to list");
                rList.add(r);
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        return rList;
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
