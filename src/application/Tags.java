package application;

public class Tags {
	
	private String name;
	private String os;
	private String memory;
	private String config;
	private String playbook;
	
	public Tags (String name, String os, String memory, String config, String playbook) {
		this.name = name;
		this.os = os;
		this.memory = memory;
		this.config = config;
		this.playbook = playbook;
	}

    public String getName() {
    	return this.name;
    }
    
	public void setName(String name){
	this.name = name;
	}
    
    public String getOs() {
    	return this.os;
    }
    
	public void setOs(String os){
	this.os = os;
	}
    
    public String getMemory() {
    	return this.memory;
    }
    
	public void setMemory(String memory){
	this.memory = memory;
	}
    
    public String getConfig() {
    	return this.config;
    }
    
	public void setConfig(String config){
	this.config = config;
	}
    
    public String getPlaybook() {
    	return this.playbook;
    }
    
	public void setPlaybook(String playbook){
	this.playbook = playbook;
	}
	
}
