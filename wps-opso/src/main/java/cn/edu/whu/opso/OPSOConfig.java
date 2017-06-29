package cn.edu.whu.opso;

import java.io.File;

import org.n52.wps.commons.WPSConfig;

public class OPSOConfig {
	private static OPSOConfig instance;
	private String opso_folder = "opso";
	private String repository_name = "Algorithms.properties";
	
	//the location of the web app.
	private String localPath;
	
	private OPSOConfig(){
		this.localPath = WPSConfig.getConfigDir() + opso_folder;
	}
	
	public static OPSOConfig getInstance(){
		if(instance == null)
			instance = new OPSOConfig();
		
		return instance;
	}
	
	public File getDescription(String name){
		String pathname = this.localPath + File.separator + name +".xml";
		return new File(pathname);
	}
	
	public String getOPSOInstance(String name){
		return this.localPath + File.separator + name +".rdf";
	}
	
	public String getDescriptionPath(){
		return this.localPath;
	}
	
	public String getAlgorithmRepositoy(){
		return this.localPath + File.separator + this.repository_name;
	}
}
