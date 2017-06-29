package cn.edu.whu.opso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.opengis.wps.x100.DescriptionType;
import net.opengis.wps.x100.GetCapabilitiesDocument.GetCapabilities;
import net.opengis.wps.x100.InputDescriptionType;
import net.opengis.wps.x100.OutputDescriptionType;
import net.opengis.wps.x100.ProcessDescriptionType;

import org.geojmodelbuilder.semantic.deserialization.RDF2Instance;

import com.geojmodelbuilder.core.IDataFlow;
import com.geojmodelbuilder.core.ILink;
import com.geojmodelbuilder.core.instance.IInputParameter;
import com.geojmodelbuilder.core.instance.IOutputParameter;
import com.geojmodelbuilder.core.instance.IParameter;
import com.geojmodelbuilder.core.instance.IProcessInstance;
import com.geojmodelbuilder.core.instance.IWorkflowInstance;
import com.geojmodelbuilder.core.resource.ogc.wps.WPSProcess;

public class WorkflowInstanceToProcess {
	private String processName = "";
	private String workflowFile="";
	private StringBuffer errBuf;
	private ProcessDescriptionType workflowDescriptionType;
	private IWorkflowInstance workflowInstance;
	
	public WorkflowInstanceToProcess(String file) {
		this.workflowFile = file;
		this.errBuf = new StringBuffer();
	}
	
	
	public void setProcessName(String name){
		this.processName = name;
	}
	
	public ProcessDescriptionType getWorkflowDescriptionType(){
		if(this.workflowDescriptionType == null)
			generateDescriptionType();
		
		return this.workflowDescriptionType;
	}
	
	//
	private boolean isValid(IWorkflowInstance workflowInstance){
		
		// all the processes contained in this workflow should come from the WPS.
		for(IProcessInstance process:workflowInstance.getProcesses()){
			if(process instanceof WPSProcess)
				continue;
			
			this.errBuf.append(process.getName() + "is not a process from WPS.");
			return false;
		}
		
		return true;
	}
	
	//parse the OPSO documet to workflow instance
	public IWorkflowInstance getWorkflowInstance(){
		if(this.workflowInstance == null){
			RDF2Instance rdf2Instance = new RDF2Instance();
			this.workflowInstance= rdf2Instance.parse(this.workflowFile);	
		}
		return this.workflowInstance;
	}
	
	public List<IInputParameter> getAllValidInputs(){
		List<IInputParameter> inputs = new ArrayList<IInputParameter>();
		
		if (getWorkflowInstance() == null) 
			return null;
		
		
		for(IProcessInstance processInstance:getWorkflowInstance().getProcesses()){
			if(!(processInstance instanceof WPSProcess))
				continue;
			
			WPSProcess wpsProcess = (WPSProcess)processInstance;
			
			List<IInputParameter> wpsInputs = getValidInputs(wpsProcess);
			if(wpsInputs.size()!=0){
				inputs.addAll(wpsInputs);
			}
		}
		
		return inputs;
	}
	
	public List<IOutputParameter> getAllValidOutputs(){
		List<IOutputParameter> outputs = new ArrayList<IOutputParameter>();
		
		if (getWorkflowInstance() == null) 
			return null;
		
		
		for(IProcessInstance processInstance:getWorkflowInstance().getProcesses()){
			if(!(processInstance instanceof WPSProcess))
				continue;
			
			WPSProcess wpsProcess = (WPSProcess)processInstance;
			
			List<IOutputParameter> wpsInputs = getValidOutputs(wpsProcess);
			if(wpsInputs.size()!=0){
				outputs.addAll(wpsInputs);
			}
		}
		
		return outputs;
	}
	
	//identify the inputs and outputs
	//not consider the situation that parameters from different processes have the same name.
	private boolean generateDescriptionType(){
		//initialize when the server starts
		
		if(!isValid(getWorkflowInstance()))
			return false;
		
		Map<WPSProcess, List<IParameter>> processParamMap = new HashMap<WPSProcess, List<IParameter>>();
		boolean hasInput = false;
		boolean hasOutput = false;
		for(IProcessInstance processInstance:workflowInstance.getProcesses()){
			List<IParameter> parameters = new ArrayList<IParameter>();
			if(!(processInstance instanceof WPSProcess))
				continue;
			
			WPSProcess wpsProcess = (WPSProcess)processInstance;
			
			List<IInputParameter> wpsInputs = getValidInputs(wpsProcess);
			if(wpsInputs.size()!=0){
				parameters.addAll(wpsInputs);
				hasInput = true;
			}
			
			List<IOutputParameter> wpsOutputs = getValidOutputs(wpsProcess);
			if(wpsOutputs.size() != 0){
				parameters.addAll(wpsOutputs);
				hasOutput = true;
			}
			
			if(parameters.size()!=0)
				processParamMap.put(wpsProcess, parameters);
		}
			
		if(!hasInput){
			this.errBuf.append("There is no input.");
			return false;
		}
		
		if(!hasOutput){
			this.errBuf.append("There is no output.");
			return false;
		}
		
		ProcessDescriptionType processDescriptionType = TemplateUtil.getInstance().getProcessDescriptionTemplate();
		if(processDescriptionType == null){
			this.errBuf.append("failed to get the process description template.");
			return false;
		}
		
		List<InputDescriptionType> inputDescriptionTypes = new ArrayList<InputDescriptionType>();
		List<OutputDescriptionType> outputDescriptionTypes = new ArrayList<OutputDescriptionType>();
		
		for(WPSProcess wpsProcess:processParamMap.keySet()){
			ProcessDescriptionType descriptionType = wpsProcess.getProcessDescriptionType();
			if (descriptionType == null) {
				this.errBuf.append("Failed to get the description of " + wpsProcess.getName());
				return false;
			}
			List<IParameter> parameters = processParamMap.get(wpsProcess);
			InputDescriptionType[] inputTypes = descriptionType.getDataInputs().getInputArray();
			OutputDescriptionType[] outputTypes = descriptionType.getProcessOutputs().getOutputArray();
			
			for(IParameter parameter:parameters){
				if(parameter instanceof IInputParameter){
					InputDescriptionType inputType = (InputDescriptionType)getDescriptionTypeByName(inputTypes, parameter.getName());
					inputDescriptionTypes.add(inputType);
				}else if (parameter instanceof IOutputParameter) {
					OutputDescriptionType outputType = (OutputDescriptionType)getDescriptionTypeByName(outputTypes, parameter.getName());
					outputDescriptionTypes.add(outputType);
				}
			}
		}
		
		InputDescriptionType[] dataInputs = new InputDescriptionType[inputDescriptionTypes.size()];
		for(int i=0;i<inputDescriptionTypes.size();i++){
			dataInputs[i] = inputDescriptionTypes.get(i);
		}
		processDescriptionType.addNewDataInputs().setInputArray(dataInputs);
		
		OutputDescriptionType[] processOutputs = new OutputDescriptionType[outputDescriptionTypes.size()];
		for(int i=0;i<outputDescriptionTypes.size();i++){
			processOutputs[i] = outputDescriptionTypes.get(i);
		}
		processDescriptionType.addNewProcessOutputs().setOutputArray(processOutputs);
		
		processDescriptionType.getIdentifier().setStringValue(this.processName);
		this.workflowDescriptionType = processDescriptionType;
		return true;
	}
	
	public boolean save(File file){
		ProcessDescriptionType descriptionType = getWorkflowDescriptionType();
		if(descriptionType == null)
			return false;
		
		try {
			descriptionType.save(file);
		} catch (IOException e) {
			e.printStackTrace();
			this.errBuf.append("Failed to save to "+file.getAbsolutePath());
		}
		
		return true;
	}
	private DescriptionType getDescriptionTypeByName(DescriptionType[] descriptionTypes,String name){
		for(DescriptionType descriptionType:descriptionTypes){
			if (descriptionType.getIdentifier().getStringValue().equals(name)) 
				return descriptionType;
		}
		return null;
	}
	
	private List<IInputParameter> getValidInputs(WPSProcess wpsProcess){
		List<IInputParameter> inputs = new ArrayList<IInputParameter>();
		boolean valid = true;
		for(IInputParameter input:wpsProcess.getInputs()){
			valid = true;
			for(ILink link:wpsProcess.getLinks()){
				if(!(link instanceof IDataFlow))
					continue;
				
				IDataFlow dataFlow = (IDataFlow)link;
				if(dataFlow.getTargetExchange() != input)
					continue;
				
				valid = false;
			}
			
			if(valid)
				inputs.add(input);
		}
		
		return inputs;
	}
	
	private List<IOutputParameter> getValidOutputs(WPSProcess wpsProcess){
		List<IOutputParameter> outputs = new ArrayList<IOutputParameter>();
		boolean valid = true;
		for(IOutputParameter output:wpsProcess.getOutputs()){
			valid = true;
			for(ILink link:wpsProcess.getLinks()){
				if(!(link instanceof IDataFlow))
					continue;
				
				IDataFlow dataFlow = (IDataFlow)link;
				
				if(dataFlow.getSourceExchange() != output)
					continue;
				
				valid = false;
			}
			
			if(valid)
				outputs.add(output);
		}
		return outputs;
	}
	
	public String getErrInfo(){
		return this.errBuf.toString();
	}
	
	
	public static void main(String[] args){
		String instance = WorkflowInstanceToProcess.class.getResource("/examples/water_extraction_instance.rdf").getPath();
		System.out.println(instance);
		WorkflowInstanceToProcess toProcess = new WorkflowInstanceToProcess(instance);
		toProcess.setProcessName("WaterExtraction");
		boolean flag = toProcess.save(new File("c:/workspace/water_extraction_process_description.xml"));
		if (flag) {
			System.out.println("Successfully");
		}else {
			System.err.println("Failed to save. "+ toProcess.getErrInfo());
		}
	}
}
