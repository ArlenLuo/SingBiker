package sg.ntu.dataminers.singbiker.entity;

public class Route {

    private Point pointStart;
    private Point pointEnd;
    private String waypoints;
    private double distanceInMeters;

    public Route(Point startPoint, Point endPoint, String waypoints) {
        pointStart = startPoint;
        pointEnd = endPoint;
        this.waypoints = waypoints;
    }

    public Point getPointStart() {
        return pointStart;
    }

    public Point getPointEnd() {
        return pointEnd;
    }

    public String getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(String waypoints) {
        this.waypoints = waypoints;
    }

    public double getDistanceInMeters() {
        return distanceInMeters;
    }

    public void setDistanceInMeters(double distanceInMeters) {
        this.distanceInMeters = distanceInMeters;
    }
}
