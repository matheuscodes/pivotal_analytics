package org.arkanos.pivotal_analytics.pivotal;

import java.util.HashMap;

import org.json.simple.JSONObject;

public class UserLookup {
	HashMap<String,String> users = null;
	
	public UserLookup(){
		users = new HashMap<String,String>();
	}
	
	public void addUser(JSONObject jo){
		System.out.println(jo.get("id").toString());
		users.put(jo.get("id").toString(),(String)jo.get("name"));
	}
	
	public String getUser(String key){
		return users.get(key);
	}
}
