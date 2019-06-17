package application;


import java.io.BufferedReader; 
import java.io.File; 
import java.io.FileInputStream; 
import java.io.FileNotFoundException; 
import java.io.FileWriter; 
import java.io.IOException; 
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.google.gson.Gson; 
import com.google.gson.GsonBuilder;


public class Config { 

    // Hier kommen alle Attribute rein, die in der Datei gespeichert werden sollen 
    private String URL; 
    private String Token;
    private String Version;
    private ArrayList<Tags> tags; 
    

    public Config() { 
        // Hier die Standardwerte der Attribute, falls diese noch nicht
        // in der Datei vorhanden sind. Datei wird mit diesen Standardwerten neu erstellt 
           this.URL = "http://netbox"; 
           this.Token = "";   
           this.Version = "3.7.6";
           this.tags = new ArrayList<Tags>();

           tags.add(new Tags("leaf", "CumulusCommunity/cumulus-vx", "512", "./helper_scripts/extra_switch_config.sh", "lab.yml"));
           tags.add(new Tags("spine", "CumulusCommunity/cumulus-vx", "512", "./helper_scripts/extra_switch_config.sh", "lab.yml"));
           tags.add(new Tags("mgmt", "CumulusCommunity/cumulus-vx", "512", "./helper_scripts/extra_switch_config.sh", "lab.yml"));
           tags.add(new Tags("oob-switch", "CumulusCommunity/cumulus-vx", "512", "./helper_scripts/oob_switch_config.sh", "lab.yml"));
           tags.add(new Tags("oob-server", "cumulus-VAGRANTSLASH-ts", "4096", "./helper_scripts/OOB_Server_Config.sh", "netq.yml"));
    } 
    
    public String getURL() {
    	return instance.URL;
    }
    
    public String getToken() {
    	return instance.Token;
    }

    public String getVersion() {
    	return instance.Version;
    }
    
    public Tags getTag(String tag) {
    	if (tag.equalsIgnoreCase("leaf")) {
    		return tags.get(0);
    	}
    	if (tag.equalsIgnoreCase("spine")) {
    		return tags.get(1);
    	}
    	if (tag.equalsIgnoreCase("mgmt")) {
    		return tags.get(2);
    	}
    	if (tag.equalsIgnoreCase("oob-switch")) {
    		return tags.get(3);
    	}
    	if (tag.equalsIgnoreCase("oob-server")) {
    		return tags.get(4);
    	}
    	else return tags.get(0);
    }
    
    public Tags getleaf() {
    	return tags.get(0);
    }
    
    public Tags getspine() {
    	return tags.get(1);
    }
    
    public Tags getmgmt() {
    	return tags.get(2);
    }

	public void setURL(String url){
		if (url.contains("http://")){
			instance.URL = url;
		}
		else {
			instance.URL = "http://" + url;
		}
		
	}
	
	public void setToken(String t){
		instance.Token = t;
	}
	
	public void setVersion(String v){
		instance.Version = v;
	}


// hier nichts ändern 
    private static Config instance;

    public static Config getInstance() { 
        if (instance == null) { 
            instance = fromDefaults(); 
        } 
        return instance; 
    } 

    public static void load(File file) { 
        instance = fromFile(file); 

        // wenn keine config datei gefunden wird, werden Default Werte genommen
        if (instance == null) { 
            instance = fromDefaults(); 
        } 
    } 

    public static void load(String file) { 
        load(new File(file)); 
    } 

    private static Config fromDefaults() { 
        Config config = new Config(); 
        return config; 
    } 

    public void toFile(String file) { 
        toFile(new File(file)); 
    } 

    public void toFile(File file) { 
        Gson gson = new GsonBuilder().setPrettyPrinting().create(); 
        String jsonConfig = gson.toJson(this); 
        FileWriter writer; 
        try { 
            writer = new FileWriter(file); 
            writer.write(jsonConfig); 
            writer.flush(); 
            writer.close(); 
        } catch (IOException e) { 
            e.printStackTrace(); 
        } 
    } 

    private static Config fromFile(File configFile) { 
        try { 
            Gson gson = new GsonBuilder().setPrettyPrinting().create(); 
            BufferedReader reader = new BufferedReader(new InputStreamReader( 
            new FileInputStream(configFile))); 
            return gson.fromJson(reader, Config.class); 
        } catch (FileNotFoundException e) { 
            return null; 
        } 
    } 

    @Override 
    public String toString() { 
        Gson gson = new GsonBuilder().setPrettyPrinting().create(); 
        return gson.toJson(this); 
    } 
}