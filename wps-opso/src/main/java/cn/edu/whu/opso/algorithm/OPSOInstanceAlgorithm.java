package cn.edu.whu.opso.algorithm;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.opengis.wps.x100.ProcessDescriptionType;

import org.apache.xmlbeans.XmlException;
import org.n52.wps.io.data.IData;
import org.n52.wps.server.ExceptionReport;
import org.n52.wps.server.IAlgorithm;

import cn.edu.whu.opso.OPSOConfig;
import cn.edu.whu.opso.WorkflowInstanceToProcess;
import cn.edu.whu.opso.io.OPSODataBinding;

import com.geojmodelbuilder.core.data.IComplexData;
import com.geojmodelbuilder.core.instance.IInputParameter;
import com.geojmodelbuilder.core.instance.IOutputParameter;
import com.geojmodelbuilder.core.instance.IWorkflowInstance;

/**
 * Refer to AbstractObservableAlgorithm.
 */
public class OPSOInstanceAlgorithm implements IAlgorithm {

	private String identifier;
	private ProcessDescriptionType processDescription;
	private List<String> errList = new ArrayList<String>();
	private IWorkflowInstance workflowInstance = null;
	private WorkflowInstanceToProcess workflowInstanceToProcess;
	
	public OPSOInstanceAlgorithm(String identifier) {
		this.identifier = identifier;
	}

	@Override
	public Map<String, IData> run(Map<String, List<IData>> inputData)
			throws ExceptionReport {
		List<IInputParameter> inputs = getAllInputs();
		List<IOutputParameter> outputs = getAllOutputs();
		
		if(getWorkflowInstance() == null)
			return null;
		
 		for(String key:inputData.keySet()){
			System.out.println(key);
			for(IInputParameter inputParameter:inputs){
				if (inputParameter.getName().equals(key)) {
					assignValue(inputParameter, inputData.get(key).get(0));
					break;
				}
			}
		}
		
 		WorkflowExecThread execThread = new WorkflowExecThread(getWorkflowInstance());
 		execThread.start();
 		try {
			execThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

 		System.out.println("finished");
 		
 		for(IOutputParameter output:outputs){
 			System.out.println(output.getData().getValue());
 		}
		return null;
	}
	
	
	private boolean assignValue(IInputParameter input,IData data){
		if(!(data instanceof OPSODataBinding)){
			return false;
		}
		
		IComplexData complexData = ((OPSODataBinding)data).getPayload();
		
		com.geojmodelbuilder.core.data.IData data2 = input.getData();
		data2.setType(complexData.getType());
		data2.setValue(complexData.getValue());
		
		if(data2 instanceof IComplexData){
			((IComplexData)data2).setMimeType(complexData.getMimeType());
		}
		
		return true;
	}

	@Override
	public List<String> getErrors() {
		return this.errList;
	}

	@Override
	public ProcessDescriptionType getDescription() {
		if (this.processDescription == null) {
			File descriptionFile = OPSOConfig.getInstance().getDescription(
					this.identifier);
			try {
				this.processDescription = ProcessDescriptionType.Factory
						.parse(descriptionFile);
			} catch (XmlException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return this.processDescription;
	}

	@Override
	public String getWellKnownName() {
		return this.identifier;
	}

	@Override
	public boolean processDescriptionIsValid() {
		return true;
	}

	@Override
	public Class<?> getInputDataType(String id) {
		return OPSODataBinding.class;
	}

	private WorkflowInstanceToProcess getInstanceToProcess(){
		if(this.workflowInstanceToProcess == null)
			workflowInstanceToProcess = new WorkflowInstanceToProcess(OPSOConfig.getInstance().getOPSOInstance(this.identifier));
		
		return this.workflowInstanceToProcess;
	}
	
	private IWorkflowInstance getWorkflowInstance(){
		if (this.workflowInstance == null) {
			this.workflowInstance = getInstanceToProcess().getWorkflowInstance();
		}
		return this.workflowInstance;
	}
	
	private List<IInputParameter> getAllInputs(){
		return this.getInstanceToProcess().getAllValidInputs();
	}
	
	private List<IOutputParameter> getAllOutputs(){
		return this.getInstanceToProcess().getAllValidOutputs();
	}
	
	
	@Override
	public Class<?> getOutputDataType(String id) {
		return OPSODataBinding.class;
	}

}
