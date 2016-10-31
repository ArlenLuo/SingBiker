package sg.ntu.dataminers.singbiker.entity;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Trip implements Parcelable {

    private Date dateStarted;
    private Date datePauseHelper;
    private Date dateFinished;
    private Route routeSystemGenerated;
    private Route routeCycled;
    private double totalDistanceCycled;
    private long totalTimeCycled;
    private double averageSpeed;
    private int numberOfPauses;

    public Trip(Route systemGeneratedRoute) {
        this.routeSystemGenerated = systemGeneratedRoute;
        averageSpeed = -1;
        numberOfPauses = 0;
        totalTimeCycled = 0;
    }

    public void beginCycling(LatLng currentPosition) {
        routeCycled = new Route(currentPosition, currentPosition);
        routeCycled.setWaypoints(new ArrayList<LatLng>());

        dateStarted = new Date();
        continueCycling();
    }

    public void continueCycling() {
        datePauseHelper = new Date();
    }

    public void pauseCycling(LatLng newWaypoint) {
        long timeSinceLastPause = new Date().getTime() - datePauseHelper.getTime();
        totalTimeCycled += timeSinceLastPause;

        updateRouteCycled(newWaypoint);

        calculateRouteCycledDistance();
        calculateAverageSpeed();

        numberOfPauses++;
    }

    public void finishedCycling(LatLng newWaypoint) {
        pauseCycling(newWaypoint);
        dateFinished = new Date();

        numberOfPauses--;
    }

    public void updateRouteCycled(LatLng newWaypoint) {
        LatLng pointStart = routeCycled.getPointStart();
        LatLng pointEnd = routeCycled.getPointEnd();
        List<LatLng> listOfWaypoints = routeCycled.getWaypoints();

        routeCycled = new Route(pointStart, newWaypoint);
        listOfWaypoints.add(pointEnd);
        routeCycled.setWaypoints(listOfWaypoints);
    }

    private void calculateRouteCycledDistance() {

        totalDistanceCycled = 0;
        float[] results = new float[3];

        List<LatLng> list = routeCycled.getWaypoints();

        if (!list.isEmpty()) {
            Location.distanceBetween(routeCycled.getPointStart().latitude, routeCycled.getPointStart().longitude, list.get(0).latitude, list.get(0).longitude, results);
            totalDistanceCycled += results[0];

            for (int i = 1; i < list.size(); i++) {
                Location.distanceBetween(list.get(i - 1).latitude, list.get(i - 1).longitude, list.get(i).latitude, list.get(i).longitude, results);
                totalDistanceCycled += results[0];
            }

            Location.distanceBetween(list.get(list.size() - 1).latitude, list.get(list.size() - 1).longitude, routeCycled.getPointEnd().latitude, routeCycled.getPointEnd().longitude, results);
            totalDistanceCycled += results[0];
        }
        else {
            Location.distanceBetween(routeCycled.getPointStart().latitude, routeCycled.getPointEnd().longitude, routeCycled.getPointEnd().latitude, routeCycled.getPointEnd().longitude, results);
            totalDistanceCycled += results[0];
        }
    }

    private void calculateAverageSpeed() {
        averageSpeed = (totalDistanceCycled / 1000) / milliSecToHours(totalTimeCycled);
    }

    private double milliSecToHours(long milliSec) {

        double seconds = (milliSec / 1000) % 60 ;
        double minutes = ((milliSec / (1000*60)) % 60);
        double hours   = ((milliSec / (1000*60*60)) % 24);

        return (hours + (minutes / 60) + (seconds / 60 / 60));
    }

    public Date getDateStarted() {
        return dateStarted;
    }

    public Date getDateFinished() {
        return dateFinished;
    }

    public Route getRouteSystemGenerated() {
        return routeSystemGenerated;
    }

    public Route getRouteCycled() {
        return routeCycled;
    }

    // Returns distance in kilometers.
    public double getTotalDistanceCycled() {
        return (totalDistanceCycled / 1000);
    }

    // Returns average speed in km/h.
    public double getAverageSpeed() {
        return averageSpeed;
    }

    public int getNumberOfPauses() {
        return numberOfPauses;
    }

    // Methods needed when implementing the Parcelable interface.
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcelOut, int flags) {
        Gson gson = new Gson();
        parcelOut.writeString(gson.toJson(dateStarted));
        parcelOut.writeString(gson.toJson(dateFinished));
        parcelOut.writeString(gson.toJson(routeSystemGenerated));
        parcelOut.writeString(gson.toJson(routeCycled));
        parcelOut.writeDouble(totalDistanceCycled);
        parcelOut.writeDouble(averageSpeed);
    }

    public static final Parcelable.Creator<Trip> CREATOR = new Parcelable.Creator<Trip>() {
        public Trip createFromParcel(Parcel parcelIn) {
            return new Trip(parcelIn);
        }

        public Trip[] newArray(int size) {
            return new Trip[size];
        }
    };

    private Trip(Parcel parcelIn) {
        Gson gson = new Gson();
        dateStarted = gson.fromJson(parcelIn.readString(), Date.class);
        dateFinished = gson.fromJson(parcelIn.readString(), Date.class);
        routeSystemGenerated = gson.fromJson(parcelIn.readString(), Route.class);
        routeCycled = gson.fromJson(parcelIn.readString(), Route.class);
        totalDistanceCycled = parcelIn.readDouble();
        averageSpeed = parcelIn.readDouble();
    }
}
