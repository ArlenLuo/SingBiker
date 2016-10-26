package sg.ntu.dataminers.singbiker.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

import java.util.Date;

public class Trip implements Parcelable {

    private Date dateStarted;
    private Date dateFinished;
    private Route routeSystemGenerated;
    private Route routeCycled;
    private double averageSpeed;
    private int numberOfPauses;

    public Trip(Route systemGeneratedRoute) {
        dateStarted = new Date();
        routeSystemGenerated = systemGeneratedRoute;
        averageSpeed = -1;
        numberOfPauses = 0;
    }

    public Date getDateStarted() {
        return dateStarted;
    }

    public Date getDateFinished() {
        return dateFinished;
    }

    public void setDateFinished(Date dateFinished) {
        this.dateFinished = dateFinished;
    }

    public Route getRouteSystemGenerated() {
        return routeSystemGenerated;
    }

    public Route getRouteCycled() {
        return routeCycled;
    }

    public void setRouteCycled(Route routeCycled) {
        this.routeCycled = routeCycled;
    }

    public double getAverageSpeed() {
        return averageSpeed;
    }

    public void calculateAverageSpeed() {
        double distanceInKMs = routeCycled.getDistanceInMeters() / 1000;
        double timeInMilliSec = Long.valueOf(dateFinished.getTime() - dateStarted.getTime()).doubleValue();
        double timeInHours = timeInMilliSec / (1000 * 60 * 60);

        averageSpeed = distanceInKMs / timeInHours;
    }

    public int getNumberOfPauses() {
        return numberOfPauses;
    }

    public void setNumberOfPauses(int numberOfPauses) {
        this.numberOfPauses = numberOfPauses;
    }

    // Method needed when implementing the Parcelable interface.
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
        parcelOut.writeDouble(averageSpeed);
        parcelOut.writeInt(numberOfPauses);
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
        averageSpeed = parcelIn.readDouble();
        numberOfPauses = parcelIn.readInt();
    }
}
