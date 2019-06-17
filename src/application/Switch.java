package application;

public class Switch {

	private String Name;
	private int InterfaceID;
	private String Tag;
	private String MacAdresse;
	
	public Switch (String Name, int InterfaceID, String Tag, String MacAdresse) {
		this.Name = Name;
		this.InterfaceID = InterfaceID;
		this.Tag = Tag;
		this.MacAdresse = MacAdresse;
	}

	
	public String getName(){
		this.Name = Name.replaceAll(" ", "-");
		this.Name = Name.replaceAll("/", "-");
		this.Name = Name.replaceAll("ä", "ae");
		this.Name = Name.replaceAll("ü", "ue");
		this.Name = Name.replaceAll("ö", "oe");
		this.Name = Name.replaceAll("ß", "ss");
		return this.Name;
	}
	
	public int getID(){
		return this.InterfaceID;
	}
	
	public String getTag(){
		return this.Tag;
	}
	
	public String getMacAdresse(){
		return this.MacAdresse;
	}
	
	public void setID(int id){
	this.InterfaceID = id;
	}
	
	public void setMacAdresse(String mac){
	this.MacAdresse = mac;
	}
	
	public String toString(){ 
	    return "Name: " + Name + " ID: "+ InterfaceID + " Tag: " + Tag + " Mac Adresse: " + MacAdresse; 
	} 
	
}
