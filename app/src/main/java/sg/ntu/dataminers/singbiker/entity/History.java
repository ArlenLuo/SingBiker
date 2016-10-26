package sg.ntu.dataminers.singbiker.entity;

import java.util.Date;

public class History {

    private Route route;
    private Date date;


    public History(Route r, Date d) {
        route = r;
        date = d;
    }

    public Route getRoute() {
        return route;
    }

    public Date getDate() { return date; }

    public void setRoute(Route route) {
        this.route = route;
    }

    public void setDate(Date d) {
        this.date = d;
    }

}
