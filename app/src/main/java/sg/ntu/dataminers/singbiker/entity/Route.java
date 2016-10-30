package sg.ntu.dataminers.singbiker.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class Route implements Parcelable{

    private LatLng pointStart;
    private LatLng pointEnd;
    private List<LatLng> waypoints;
    private double distanceInMeters;
    private boolean avoidHighway;

    protected Route(Parcel in) {
        pointStart = in.readParcelable(LatLng.class.getClassLoader());
        pointEnd = in.readParcelable(LatLng.class.getClassLoader());
        waypoints = in.createTypedArrayList(LatLng.CREATOR);
        distanceInMeters = in.readDouble();
        avoidHighway = in.readByte() != 0;
    }

    public static final Creator<Route> CREATOR = new Creator<Route>() {
        @Override
        public Route createFromParcel(Parcel in) {
            return new Route(in);
        }

        @Override
        public Route[] newArray(int size) {
            return new Route[size];
        }
    };

    public boolean isAvoidHighway() {
        return avoidHighway;
    }

    public void setAvoidHighway(boolean avoidHighway) {
        this.avoidHighway = avoidHighway;
    }



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

    public List<LatLng> getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(List<LatLng> waypoints) {
        this.waypoints = waypoints;
    }

    public double getDistanceInMeters() {
        return distanceInMeters;
    }

    public void setDistanceInMeters(double distanceInMeters) {
        this.distanceInMeters = distanceInMeters;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(pointStart, flags);
        dest.writeParcelable(pointEnd, flags);
        dest.writeTypedList(waypoints);
        dest.writeDouble(distanceInMeters);
        dest.writeByte((byte) (avoidHighway ? 1 : 0));
    }
}
