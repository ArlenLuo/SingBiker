package sg.ntu.dataminers.singbiker.control;

import java.util.ArrayList;

import sg.ntu.dataminers.singbiker.entity.History;

public class HistoryManager {

    private ArrayList<History> historyList;

    public HistoryManager() {
        historyList = new ArrayList<History>();
    }

    public void addHistory(History h) {
        historyList.add(h);
    }

    public boolean deleteHistory(History h) {
        return historyList.remove(h);
    }

}
