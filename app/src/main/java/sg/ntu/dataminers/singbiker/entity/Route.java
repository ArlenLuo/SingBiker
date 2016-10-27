package sg.ntu.dataminers.singbiker.entity;

import com.google.android.gms.maps.model.LatLng;

public class Route {

    private LatLng pointStart;
    private LatLng pointEnd;
    private LatLng[] waypoints;
    private double distanceInMeters;

    public Route(LatLng startPoint, LatLng endPoint) {
        pointStart = startPoint;
        pointEnd = endPoint;
    }

    public LatLng getPointStart() {
        return pointStart;
    }

    public LatLng getPointEnd() {
        return pointEnd;
    }

    public LatLng[] getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(LatLng[] waypoints) {
        this.waypoints = waypoints;
    }

    public double getDistanceInMeters() {
        return distanceInMeters;
    }

    public void setDistanceInMeters(double distanceInMeters) {
        this.distanceInMeters = distanceInMeters;
    }
}
