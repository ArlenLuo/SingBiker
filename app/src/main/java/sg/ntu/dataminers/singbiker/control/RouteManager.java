package sg.ntu.dataminers.singbiker.control;

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

    public RouteManager(LatLng start,LatLng end){
        this.start=start;
        this.end=end;
    }
    public boolean isDone(){
        return done;
    }
    public ArrayList<Route> getRoutes(){
        return list;
    }

    private void plotAllRoutes(){
        list=new ArrayList<Route>();

        //routes connected to pcn
        LatLng startPcnPoint=getNearestPcnPoint(start);
        LatLng endPcnPoint=getNearestPcnPoint(end);

        //routes avoiding highways
        plotDirectRoutes(true);

        //routs without avoiding highways
        plotDirectRoutes(false);

    }

    private LatLng getNearestPcnPoint(LatLng p){
        LatLng ll=null;
        //get nearest pcn point to the start or end point
        return ll;
    }

    private void plotDirectRoutes(boolean avoidHighway){
        String data=getRawData(avoidHighway);
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
                list.add(r);
            }

        }catch(Exception e){
            e.printStackTrace();
        }

    }

    private String getRawData(boolean avoidHighway){
        String urlString= UrlList.GOOGLE_DIRECTIONS;
        urlString+="origin="+start.latitude+","+start.longitude;
        urlString+="&destination="+end.latitude+","+end.longitude;
        urlString+="&region=SG";
        urlString+="&alternatives=true";
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
