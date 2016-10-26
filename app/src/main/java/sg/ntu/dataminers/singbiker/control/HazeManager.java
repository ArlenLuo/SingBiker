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
import sg.ntu.dataminers.singbiker.entity.Haze;

public class HazeManager {
	Haze haze;
	ArrayList<Haze> hazeList;
	String[] zonelist={"north","south","east","west","central"};
	String lastUpdated;
	public HazeManager(){
		hazeList=new ArrayList<Haze>();
		retrieveHazeInfo();
	}
	public ArrayList<Haze> getHazeInfo(){
		return hazeList;
	}

	public String getLastUpdated(){
		return lastUpdated;
	}
	
	
	private void retrieveHazeInfo(){
		//use api to get hazeinfo
		String data=getRawData();
		//parse data that is retrieved from URL
		try {
			JSONObject jo = new JSONObject(data);
			JSONArray arr = jo.getJSONArray("items");
			lastUpdated = arr.getJSONObject(0).getString("update_timestamp");
			lastUpdated = lastUpdated.replace("T", " ");
			lastUpdated = lastUpdated.substring(0, lastUpdated.lastIndexOf("+"));
			for (int i = 0; i < zonelist.length; i++) {
				haze = new Haze();
				haze.setZone(zonelist[i]);
				haze.setPsiLevel((int) arr.getJSONObject(0).getJSONObject("readings").getJSONObject("psi_three_hourly").get(zonelist[i]));
				hazeList.add(haze);
			}

			for (int i = 0; i < hazeList.size(); i++) {
				System.out.println(hazeList.get(i));
			}
		}catch(Exception e){

		}
	}
	
	private String getRawData(){
		String data="";
		try{
			URL url=new URL(UrlList.HAZE);
			HttpURLConnection con=(HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("api-key", ApiKeyList.DATA_GOV);
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
		HazeManager hm=new HazeManager();
		
	}
	
	
	
	
}
