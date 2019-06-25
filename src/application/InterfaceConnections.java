package application;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.json.JSONArray;
import org.json.JSONObject;
import application.Config;

public class InterfaceConnections {
	
	static String servername = "";
	static String token = "";
	static String version = "";
	static boolean update = true;
	static boolean skipoob = false;
			
	public static void updatetopology(String server, String token, String version) {
		ArrayList<String> connectionslist = new ArrayList<String>();
		ArrayList<Verbindung> connectionslist_roh = new ArrayList<Verbindung>();
		ArrayList<Verbindung> helplist = new ArrayList<Verbindung>();
		ArrayList<Switch> switchlist = new ArrayList<Switch>();
		ArrayList<String> namensliste = new ArrayList<String>();
		
		try {
			HTTPQuery netbox = new HTTPQuery(servername, token);	
			
			JSONObject obj2 = new JSONObject(netbox.query("api/dcim/devices/?limit=0&?brief=1"));
			JSONArray array2 = obj2.getJSONArray("results");
			
			for (int i = 0; i < array2.length(); i++) {
				String name = array2.getJSONObject(i).optString("name");
				String tag = array2.getJSONObject(i).getJSONArray("tags").optString(0);
				if (tag.equalsIgnoreCase("leaf") || tag.equalsIgnoreCase("mgmt") || tag.equalsIgnoreCase("spine")) {
					Switch s = new Switch(name, 0, tag, "");
					switchlist.add(s);
					namensliste.add(name);
				}
			}
			
			JSONObject obj = new JSONObject(netbox.query("api/dcim/interface-connections/?limit=0&?brief=1"));
			JSONArray array = obj.getJSONArray("results");
			
			for (int i = 0; i < array.length(); i++) {
				String InterfaceA_deviceName = array.getJSONObject(i).getJSONObject("interface_a").getJSONObject("device").optString("name");
				String InterfaceB_deviceName = array.getJSONObject(i).getJSONObject("interface_b").getJSONObject("device").optString("name");
				String InterfaceA_portName = array.getJSONObject(i).getJSONObject("interface_a").optString("name");
				String InterfaceB_portName = array.getJSONObject(i).getJSONObject("interface_b").optString("name");
				int AID = array.getJSONObject(i).getJSONObject("interface_a").optInt("id");
				int BID = array.getJSONObject(i).getJSONObject("interface_b").optInt("id");
				
				for (int j = 0; j < switchlist.size(); j++) {
					if (switchlist.get(j).getName().equals(InterfaceA_deviceName) && switchlist.get(j).getID() == 0) {
						switchlist.get(j).setID(AID);
					}
					if (switchlist.get(j).getName().equals(InterfaceB_deviceName) && switchlist.get(j).getID() == 0) {
						switchlist.get(j).setID(BID);
					}
				}
				
				Verbindung x = new Verbindung(InterfaceA_deviceName, InterfaceA_portName, InterfaceB_deviceName, InterfaceB_portName, "");
				connectionslist_roh.add(x);
				
				if (!namensliste.contains(InterfaceA_deviceName)) {
					namensliste.add(InterfaceA_deviceName);
					}
				if (!namensliste.contains(InterfaceB_deviceName)) {
					namensliste.add(InterfaceB_deviceName);
				}
			}
			
			Collections.sort(namensliste);

			for (int j = 0; j < switchlist.size(); j++) {
				if (namensliste.contains(switchlist.get(j).getName())){
					namensliste.remove(switchlist.get(j).getName());
				}
			}
			for (int i = 0; i < namensliste.size(); i++) {
				String fakeswitch = namensliste.get(i).toString();
				Switch s = new Switch(fakeswitch, 0, "fake", "");
				switchlist.add(s);
			}
			
			
			for (int i = 0; i < switchlist.size(); i++) {
				if(switchlist.get(i).getID() != 0) {
					JSONObject obj3 = new JSONObject(netbox.query("api/dcim/interfaces/" + switchlist.get(i).getID() + "/?format=json"));
					String macadress = obj3.optString("mac_address");
					if (!macadress.isEmpty() || !macadress.equals(""))
						switchlist.get(i).setMacAdresse(macadress);
				}		
			}
			
			for (int i = 0; i < connectionslist_roh.size(); i++) {
				String deviceA = connectionslist_roh.get(i).getDeviceA();
				String deviceB = connectionslist_roh.get(i).getDeviceB();
				if (namensliste.contains(deviceA) && namensliste.contains(deviceB)) {
					//wenn beide drin sind, sind es fake zu fake verbindungen
				}
				else {
					helplist.add(connectionslist_roh.get(i));
				}

				for (int j = 0; j < switchlist.size(); j++) {
					String portnameverbindungsliste = connectionslist_roh.get(i).getPortA();
					String nameswitchliste = switchlist.get(j).getName();
					if (deviceA.equals(nameswitchliste) && portnameverbindungsliste.equals("eth0")){
						helplist.remove(connectionslist_roh.get(i));
						String mac = switchlist.get(j).getMacAdresse();
						connectionslist_roh.get(i).setMacAdresse(mac);
						helplist.add(connectionslist_roh.get(i));
					}	

				}
				
			}
			
			Collections.sort(helplist, new Comparator<Verbindung>() {
			    @Override
			    public int compare(Verbindung first, Verbindung second) {
			        return first.getDeviceA().compareTo(second.getDeviceA());
			    }
			});


			int zaehler = 0;
			for (int i = 0; i < helplist.size(); i++) {
				if (helplist.get(i).getPortA().equals("eth0")) {
					helplist.get(i).setDeviceB("oob-mgmt-switch");
					helplist.get(i).setPortB("swp" +zaehler);
					zaehler++;
				}
				if (helplist.get(i).getPortB().equals("eth0")) {
					helplist.get(i).setDeviceA("oob-mgmt-switch");
					helplist.get(i).setPortA("swp" +zaehler);
					zaehler++;
				}
				connectionslist.add(helplist.get(i).toString());
			}
			
			
			Collections.sort(connectionslist);
			
			FileWriter fw = new FileWriter("topology.dot");
			BufferedWriter bw = new BufferedWriter(fw);
			
			bw.write("graph vx {");
			bw.newLine();
			bw.newLine();
			
			for (int i = 0; i < switchlist.size(); i++) {
				String name = switchlist.get(i).getName();
				String tag = switchlist.get(i).getTag();
				String os = Config.getInstance().getTag(tag).getOs();
				String memory = Config.getInstance().getTag(tag).getMemory();
				String config = Config.getInstance().getTag(tag).getConfig();
				String playbook = Config.getInstance().getTag(tag).getPlaybook();
				if (!tag.equalsIgnoreCase("fake")) {
					bw.write("\""+ name +"\" [function=\"" + tag + "\" os=\"" + os + "\" version=\"" + version + "\" memory=\"" + memory + "\" config=\"" + config + "\" playbook=\"" + playbook + "\"]");	
					bw.newLine();
				}
			}
			if (skipoob) {
				bw.write("\"oob-mgmt-switch\" [function=\"fake\"]\n"
						+ "\"oobmgmtserver\" [function=\"fake\"]\n");
			}
			else {
				bw.write("\"oob-mgmt-switch\" [function=\"oob-switch\" vagrant=\"eth0\" os=\"" + Config.getInstance().getTag("oob-switch").getOs() + "\" version=\"" + version + "\" memory=\"" + Config.getInstance().getTag("oob-switch").getMemory() + "\" config=\"" + Config.getInstance().getTag("oob-switch").getConfig() + "\" playbook=\"" + Config.getInstance().getTag("oob-switch").getPlaybook() + "\"]\n"
				+ "\"oobmgmtserver\" [function=\"oob-server\" os=\"" + Config.getInstance().getTag("oob-server").getOs() + "\" version=\"" + version + "\" remap=\"false\" memory=\"" + Config.getInstance().getTag("oob-server").getMemory() + "\" config=\"" + Config.getInstance().getTag("oob-server").getConfig() + "\" playbook=\"" + Config.getInstance().getTag("oob-server").getPlaybook() + "\"]\n");
			}
			for (int i = 0; i < switchlist.size(); i++) {
				String name = switchlist.get(i).getName();
				String tag = switchlist.get(i).getTag();
				if (tag.equalsIgnoreCase("fake")) {
					bw.write("\"" + name +"\" [function=\"" + tag + "\"]");
					bw.newLine();
				}
			}
			
			bw.newLine();
			bw.newLine();
			
			bw.write("\"oob-mgmt-switch\":\"eth0\" -- \"oobmgmtserver\":\"swp1\"\n");
			for (int i = 0; i < connectionslist.size(); i++) {
				bw.write(connectionslist.get(i).toString());
				bw.newLine();
			}
			
			bw.newLine();
			bw.write("}");
			bw.close();
			
			System.out.println("topology.dot successfully updated");
		
		} catch (Exception e) {
			System.out.println(e);
//			e.printStackTrace();
			System.out.println("\nConnection to Server failed. \nPlease check the passed parameters and the settings in your config.json file.");
		}
		}

	public static void main(String[] args)  {
		Config.load("config.json");
		
		Options options = new Options();

		options.addOption("s", "servername", true, "change servername");
		options.addOption("t", "token", true, "change API Token");
		options.addOption("v", "version", true, "change version");
		options.addOption("c", "configuration", false, "show configuration");
		options.addOption("h", "help", false, "show help");
		options.addOption("skip", "skipoob", false, "set function of oobserver, obswitch to fake");
		
		Option PlaybookLink = new Option("pb", "playbook", true, "set playbook link");
		PlaybookLink.setArgs(2);
		PlaybookLink.setArgName("tag|all> <playbook link");
		options.addOption(PlaybookLink);
		
		Option OsLink = new Option("os", "oslink", true, "set os-link");
		OsLink.setArgs(2);
		OsLink.setArgName("tag|all> <os link");
		options.addOption(OsLink);
		
		Option Configlink = new Option("cl", "configlink", true, "set config-link");
		Configlink.setArgs(2);
		Configlink.setArgName("tag|all> <config link");
		options.addOption(Configlink);
		
		Option Memory = new Option("m", "memory", true, "set memory in MB");
		Memory.setArgs(2);
		Memory.setArgName("tag|all> <memory");
		options.addOption(Memory);
		
		CommandLineParser parser = new DefaultParser();
		
		try {
			CommandLine cmd = parser.parse( options, args);
			
			if(cmd.hasOption("s")) {
				Config.getInstance().setURL(cmd.getOptionValue("s"));
				System.out.println("\nNew Servername: " + Config.getInstance().getURL());
				servername = Config.getInstance().getURL();
				}
			else {
				servername = Config.getInstance().getURL();
			}
			if(cmd.hasOption("t")) {
	        	Config.getInstance().setToken(cmd.getOptionValue("t"));
	        	System.out.println("\nNew Token: " + Config.getInstance().getToken());
	        	token = Config.getInstance().getToken();
			}
			else {
				token = Config.getInstance().getToken();
			}
			if(cmd.hasOption("v")) {
	        	Config.getInstance().setVersion(cmd.getOptionValue("v"));
	        	System.out.println("\nNew Version: " + Config.getInstance().getVersion());
	        	version = cmd.getOptionValue("v");
				}
			else {
				version = Config.getInstance().getVersion();
			}
			if(cmd.hasOption("c")) {
				update = false;
	            System.out.println("\nConfiguration\nServer:\t\t" + Config.getInstance().getURL() + "\n"
	    		+ "Token:\t\t" + Config.getInstance().getToken() + "\n"
	    		+ "Version:\t" + Config.getInstance().getVersion()); 
				}
			if(cmd.hasOption("skip")) {
				skipoob = true;
			}
			if(cmd.hasOption("pb")) {
				String[] eingabe = cmd.getOptionValues("pb");
				String switch_funktion = eingabe[0];
				String playbook = eingabe[1];
				if (switch_funktion.equals("leaf") || switch_funktion.equals("mgmt") || switch_funktion.equals("spine") || switch_funktion.equals("oob-server") || switch_funktion.equals("oob-switch")) {
					Config.getInstance().getTag(switch_funktion).setPlaybook(playbook);
					System.out.println("\nPlaybook for " +  switch_funktion + ": " + playbook);
				}
				if (switch_funktion.equals("all")) {
					Config.getInstance().getleaf().setPlaybook(playbook);
					Config.getInstance().getmgmt().setPlaybook(playbook);
					Config.getInstance().getspine().setPlaybook(playbook);
					System.out.println("\nPlaybook for leaf, spine and mgmt switches: " + playbook);
				}
			}
			if(cmd.hasOption("os")) {
				String[] eingabe = cmd.getOptionValues("os");
				String switch_funktion = eingabe[0];
				String os = eingabe[1];
				if (switch_funktion.equals("leaf") || switch_funktion.equals("mgmt") || switch_funktion.equals("spine") || switch_funktion.equals("oob-server") || switch_funktion.equals("oob-switch")) {
					Config.getInstance().getTag(switch_funktion).setOs(os);;
					System.out.println("\nos-Link for " +  switch_funktion + ": " + os);
				}
				if (switch_funktion.equals("all")) {
					Config.getInstance().getleaf().setOs(os);
					Config.getInstance().getmgmt().setOs(os);
					Config.getInstance().getspine().setOs(os);
					System.out.println("\nos-Link for leaf, spine and mgmt switches: " + os);
				}
			}
			if(cmd.hasOption("m")) {
				String[] eingabe = cmd.getOptionValues("m");
				String switch_funktion = eingabe[0];
				String memory = eingabe[1];
				if (switch_funktion.equals("leaf") || switch_funktion.equals("mgmt") || switch_funktion.equals("spine") || switch_funktion.equals("oob-server") || switch_funktion.equals("oob-switch")) {
					Config.getInstance().getTag(switch_funktion).setMemory(memory);
					System.out.println("\nMemory for " +  switch_funktion + ": " + memory);
				}
				if (switch_funktion.equals("all")) {
					Config.getInstance().getleaf().setMemory(memory);
					Config.getInstance().getmgmt().setMemory(memory);
					Config.getInstance().getspine().setMemory(memory);
					System.out.println("\nMemory for leaf, spine and mgmt switches: " + memory);
				}
			}
			if(cmd.hasOption("cl")) {
				String[] eingabe = cmd.getOptionValues("cl");
				String switch_funktion = eingabe[0];
				String config = eingabe[1];
				if (switch_funktion.equals("leaf") || switch_funktion.equals("mgmt") || switch_funktion.equals("spine") || switch_funktion.equals("oob-server") || switch_funktion.equals("oob-switch")) {
					Config.getInstance().getTag(switch_funktion).setConfig(config);
					System.out.println("\nConfig-Link for " +  switch_funktion + ": " + config);
				}
				if (switch_funktion.equals("all")) {
					Config.getInstance().getleaf().setConfig(config);
					Config.getInstance().getmgmt().setConfig(config);
					Config.getInstance().getspine().setConfig(config);
					System.out.println("\nConfig-Link for leaf, spine and mgmt switches: " + config);
				}
			}
			if(cmd.hasOption("help")) {
				update = false;
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp( "java -jar netbox2vagrant.jar [options] ", options );
				}
		
		} catch (org.apache.commons.cli.ParseException e) {
			update = false;
			System.out.println(e);
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp( "java -jar netbox2vagrant.jar [options] ", options );
		}
		
		if(update) {
		updatetopology(servername, token, version);
		}
		
		Config.getInstance().toFile("config.json"); 
		}
		
	}