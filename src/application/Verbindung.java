package application;

public class Verbindung {

		private String DeviceA;
		private String DeviceB;
		private String PortA;
		private String PortB;
		private String MacAdresse;
		
		
		public Verbindung (String DeviceA, String PortA, String DeviceB, String PortB, String MacAdresse) {
			this.DeviceA = DeviceA;
			this.PortA = PortA;
			this.DeviceB = DeviceB;
			this.PortB = PortB;
			this.MacAdresse = MacAdresse;
		}
		
		public String getDeviceA(){
			this.DeviceA = DeviceA.replaceAll(" ", "-");
			this.DeviceA = DeviceA.replaceAll("/", "-");
			this.DeviceA = DeviceA.replaceAll("ä", "ae");
			this.DeviceA = DeviceA.replaceAll("ü", "ue");
			this.DeviceA = DeviceA.replaceAll("ö", "oe");
			this.DeviceA = DeviceA.replaceAll("ß", "ss");
			return this.DeviceA;
		}
		
		public void setDeviceA(String devicea){
			this.DeviceA = devicea;
			}
		
		public String getDeviceB(){
			this.DeviceB = DeviceB.replaceAll(" ", "-");
			this.DeviceB = DeviceB.replaceAll("/", "-");
			this.DeviceB = DeviceB.replaceAll("ä", "ae");
			this.DeviceB = DeviceB.replaceAll("ü", "ue");
			this.DeviceB = DeviceB.replaceAll("ö", "oe");
			this.DeviceB = DeviceB.replaceAll("ß", "ss");
			return this.DeviceB;
		}
		
		public void setDeviceB(String deviceb){
			this.DeviceB = deviceb;
			}
		
		public String getPortA(){
			this.PortA = PortA.replaceAll("/", "-");
			return this.PortA;
		}
		
		public void setPortA(String porta){
			this.PortA = porta;
			}
		
		public String getPortB(){
			this.PortB = PortB.replaceAll("/", "-");
			return this.PortB;
		}
		
		public void setPortB(String portb){
			this.PortB = portb;
		}
		
		public void setMacAdresse(String mac){
			this.MacAdresse = mac;
		}

		public String toString(){ 
			if (MacAdresse.isEmpty() || MacAdresse.equals("")) {
				return "\"" + DeviceA + "\"" + ":" + "\"" + PortA + "\"" + " -- " + "\"" + DeviceB + "\"" + ":" + "\"" + PortB + "\"";
			}
			else {
				return "\"" + DeviceA + "\"" + ":" + "\"" + PortA + "\"" + " -- " + "\"" + DeviceB + "\"" + ":" + "\"" + PortB + "\"" + " [left_mac=\"" + MacAdresse + "\"]";
			}
		} 

}
