package sg.ntu.dataminers.singbiker.control;

import java.util.ArrayList;

import sg.ntu.dataminers.singbiker.entity.History;

public class HistoryManager {

    private HistoryDatabaseManager db;
    private ArrayList<History> historyList;

    public HistoryManager() {
        db = new HistoryDatabaseManager();
        historyList = new ArrayList<History>();
    }

    public void addHistory(History h) {
        historyList.add(h);
    }

    public void removeHistory(History h) {}

    public boolean deleteHistory(History h) {
        return historyList.remove(h);
    }

    public ArrayList<History> getHistoryList() {
        return historyList;
    }

    public boolean checkEmpty() {
        return historyList.isEmpty();
    }

    public int getListSize() {
        return historyList.size();
    }

    public History getHistory(int index) {
        return historyList.get(index);
    }

}
