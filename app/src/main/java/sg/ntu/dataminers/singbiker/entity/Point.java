package sg.ntu.dataminers.singbiker.entity;

public class Point {
    private double latitude;
    private double longitude;

    public Point(double lat, double lon) {
        latitude = lat;
        longitude = lon;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLatitude(double l) {
        latitude = l;
    }

    public void setLongitude(double l) {
        longitude = l;
    }
}