package sg.ntu.dataminers.singbiker.entity;

import java.util.Date;

public class History {

    private Trip trip;
    private Date date;


    public History(Trip r, Date d) {
        trip = r;
        date = d;
    }

    public Trip getTrip() {
        return trip;
    }

    public Date getDate() { return date; }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public void setDate(Date d) {
        this.date = d;
    }

}
