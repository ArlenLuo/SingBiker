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
import sg.ntu.dataminers.singbiker.entity.Incident;
import sg.ntu.dataminers.singbiker.entity.Route;

public class MapManager {

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
}
