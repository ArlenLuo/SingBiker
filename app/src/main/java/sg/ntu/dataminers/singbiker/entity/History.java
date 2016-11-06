package sg.ntu.dataminers.singbiker.entity;

import java.util.Date;

public class History {

    private long id;
    private Trip trip;
    private Date date;


    public History(Trip r, Date d) {
        this(r, d, 0);
    }

    public History(Trip r, Date d, long id) {
        this.id = id;
        trip = r;
        date = d;
    }

    public Trip getTrip() {
        return trip;
    }

    public Date getDate() { return date; }

    public long getDBId() { return id; }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public void setDate(Date d) {
        this.date = d;
    }

}
