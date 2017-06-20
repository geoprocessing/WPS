package cn.edu.whu.opso;

import java.io.IOException;
import java.io.InputStream;

import net.opengis.wps.x100.ProcessDescriptionType;

import org.apache.xmlbeans.XmlException;

public class TemplateUtil {
	private ProcessDescriptionType processDescriptionTemplate;
	private String process_description_template = "templates/ProcessDescribeDocument.xml";
	private static TemplateUtil instance;
	
	private TemplateUtil(){}
	
	public static TemplateUtil getInstance(){
		if (instance == null) 
			instance = new TemplateUtil();
			
		return instance;
	}
	
	public ProcessDescriptionType getProcessDescriptionTemplate(){
		if(processDescriptionTemplate != null)
			return this.processDescriptionTemplate;
		
		InputStream in = TemplateUtil.class.getClassLoader().getResourceAsStream(process_description_template);
		if(in == null)
			return null;
		
		try {
			this.processDescriptionTemplate = ProcessDescriptionType.Factory.parse(in);
//			this.processDescriptionTemplate.setDataInputs();
		} catch (XmlException | IOException e) {
			e.printStackTrace();
		}
		
		return this.processDescriptionTemplate;
	}
	
	public static void main(String[] args){
		ProcessDescriptionType descriptionType = TemplateUtil.getInstance().getProcessDescriptionTemplate();
		System.out.println(descriptionType.getIdentifier());
		System.out.println(descriptionType.getDataInputs());
		System.out.println(descriptionType.getProcessOutputs());
	}
}
