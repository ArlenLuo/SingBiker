package sg.ntu.dataminers.singbiker.control;

import android.content.Context;
import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.kml.KmlLayer;

import java.util.List;

import sg.ntu.dataminers.singbiker.R;
import sg.ntu.dataminers.singbiker.entity.Route;

public class MapManager {

    public void drawRoute(GoogleMap map, Route route,int color) {
        PolylineOptions po=new PolylineOptions();
        po.width(10);
        po.color(color);
        List<LatLng> list=route.getWaypoints();

        po.add(route.getPointStart());

        for (int i=0;i<list.size();i++){
            po.add(list.get(i));
        }

        po.add(route.getPointEnd());

        map.addPolyline(po);
    }

    public void drawPcnRoutes(GoogleMap map,Context context){
        try{
            KmlLayer kmlLayer = new KmlLayer(map, R.raw.pcn, context);
            kmlLayer.addLayerToMap();
        }catch(Exception e){

        }
    }
}
