package sg.ntu.dataminers.singbiker.control;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import sg.ntu.dataminers.singbiker.ApiKeyList;
import sg.ntu.dataminers.singbiker.UrlList;
import sg.ntu.dataminers.singbiker.entity.Incident;
import sg.ntu.dataminers.singbiker.entity.Point;

public class IncidentManager {
	ArrayList<Incident> incidentList;
	Incident incident;
	public IncidentManager(){
		incidentList=new ArrayList<Incident>();
		retrieveIncidents();
	}

	public ArrayList<Incident> getIncidents(){
		return incidentList;
	}

	private void retrieveIncidents(){
		String data=getRawData();
		try {
			JSONObject jo = new JSONObject(data);
			JSONArray arr = jo.getJSONArray("value");
			for (int i = 0; i < arr.length(); i++) {
				incident = new Incident();
				incident.setType(arr.getJSONObject(i).getString("Type"));
				incident.setDescription(arr.getJSONObject(i).getString("Message"));
				incident.setLocation(new Point(arr.getJSONObject(i).getDouble("Latitude"), arr.getJSONObject(i).getDouble("Longitude")));
				incidentList.add(incident);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private String getRawData(){
		String data="";
		try{
			URL url=new URL(UrlList.TRAFFIC_INCIDENTS);
			HttpURLConnection con=(HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("AccountKey", ApiKeyList.LTA_DATAMALL);
			BufferedReader br=new BufferedReader(new InputStreamReader(con.getInputStream()));
			String x="";
			while((x=br.readLine())!=null){
				data=data.concat(x);
			}
		}catch(Exception e){

		}
		return data;
	}

	public static void main(String[] args){
		IncidentManager im=new IncidentManager();
		im.getIncidents();
	}
}
