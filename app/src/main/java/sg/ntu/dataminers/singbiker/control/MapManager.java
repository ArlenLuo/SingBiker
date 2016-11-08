package sg.ntu.dataminers.singbiker.control;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.kml.KmlLayer;

import java.util.ArrayList;
import java.util.List;

import sg.ntu.dataminers.singbiker.R;
import sg.ntu.dataminers.singbiker.entity.Haze;
import sg.ntu.dataminers.singbiker.entity.Incident;
import sg.ntu.dataminers.singbiker.entity.Route;

public class MapManager {
    private static LatLng North, South, East, West, Central;
    public static Polyline drawRoute(GoogleMap map, Route route,int color) {
        PolylineOptions po=new PolylineOptions();
        po.width(12);
        po.color(color);
        ArrayList<LatLng> list=route.getWaypoints();

        //po.add(route.getPointStart());

        for (int i=0;i<list.size();i++){
            po.add(list.get(i));
        }
        PolygonOptions pg=new PolygonOptions();
        pg.add(route.getPointStart());
        pg.add(new LatLng(route.getPointStart().latitude,route.getPointEnd().longitude));
        pg.add(route.getPointEnd());
        pg.add(new LatLng(route.getPointEnd().latitude,route.getPointStart().longitude));
        pg.fillColor(Color.RED);
        //map.addPolygon(pg);
        //po.add(route.getPointEnd());

        return map.addPolyline(po);
    }

    public static KmlLayer drawPcnRoutes(GoogleMap map,Context context){
        KmlLayer kmlLayer=null;
        try{
            kmlLayer = new KmlLayer(map, R.raw.pcn, context);
            kmlLayer.addLayerToMap();
        }catch(Exception e){
            e.printStackTrace();
        }
        return kmlLayer;
    }

    public static void drawIncidents(GoogleMap map,Context context){
        GetData data=new GetData(map);
        data.execute();
    }
    public static void drawHaze(GoogleMap mMap,Context context){
            DrawHazeData dh=new DrawHazeData(mMap);
            dh.execute();

    }
    static class GetData extends AsyncTask<Void,Void,ArrayList<Incident>> {
        GoogleMap map;
        public GetData(GoogleMap map){
            this.map=map;
        }
        protected ArrayList<Incident> doInBackground(Void... params){
            IncidentManager im=new IncidentManager();
            return im.getIncidents();
        }

        @Override
        protected void onPostExecute(ArrayList<Incident> list) {
            MarkerOptions mo=new MarkerOptions();
            for (Incident i:list){
                mo.position(new LatLng(i.getLocation().getLatitude(),i.getLocation().getLongitude()));
                mo.title(i.getType());
                mo.snippet(i.getDescription());
                mo.icon(BitmapDescriptorFactory.fromResource(R.drawable.incident_icon));
                map.addMarker(mo);
            }
        }
    }
    static class DrawHazeData extends AsyncTask<Void,Void,ArrayList<Haze>> {
        GoogleMap mMap;
        public DrawHazeData(GoogleMap map){
            mMap=map;
        }
        protected ArrayList<Haze> doInBackground(Void... params){
            HazeManager hm = new HazeManager();
            ArrayList<Haze> hazeList = hm.getHazeInfo();
            return hazeList;
        }

        @Override
        protected void onPostExecute(ArrayList<Haze> hazeList) {
            North = new LatLng(1.41803,103.82);
            mMap.addMarker(new MarkerOptions().position(North)
                    .title("North")
                    .snippet(Double.toString(hazeList.get(0).getPsiLevel()))
                    .icon(BitmapDescriptorFactory.defaultMarker(psi(hazeList.get(0).getPsiLevel()))));

            South = new LatLng(1.29587,103.82);
            mMap.addMarker(new MarkerOptions().position(South)
                    .title("South")
                    .snippet(Double.toString(hazeList.get(1).getPsiLevel()))
                    .icon(BitmapDescriptorFactory.defaultMarker(psi(hazeList.get(1).getPsiLevel()))));

            East =  new LatLng(1.35735,103.94);
            mMap.addMarker(new MarkerOptions().position(East)
                    .title("East")
                    .snippet(Double.toString(hazeList.get(2).getPsiLevel()))
                    .icon(BitmapDescriptorFactory.defaultMarker(psi(hazeList.get(2).getPsiLevel()))));

            West = new LatLng(1.35735,103.7);
            mMap.addMarker(new MarkerOptions().position(West)
                    .title("West")
                    .snippet(Double.toString(hazeList.get(3).getPsiLevel()))
                    .icon(BitmapDescriptorFactory.defaultMarker(psi(hazeList.get(3).getPsiLevel()))));

            Central = new LatLng(1.35735,103.82);
            mMap.addMarker(new MarkerOptions().position(Central)
                    .title("Central")
                    .snippet(Double.toString(hazeList.get(4).getPsiLevel()))
                    .icon(BitmapDescriptorFactory.defaultMarker(psi(hazeList.get(4).getPsiLevel()))));
        }
        private Float psi(double PSIvalue) {
            if (PSIvalue > 300) {
                return BitmapDescriptorFactory.HUE_RED;} //0.0
            else if (PSIvalue > 200) {
                return BitmapDescriptorFactory.HUE_ORANGE;} //30.0
            else if (PSIvalue > 100) {
                return BitmapDescriptorFactory.HUE_YELLOW;} //60.0
            else if (PSIvalue >50) {
                return BitmapDescriptorFactory.HUE_AZURE;} //210.0
            else  {
                return BitmapDescriptorFactory.HUE_GREEN;} //120.0
        }
    }
}
