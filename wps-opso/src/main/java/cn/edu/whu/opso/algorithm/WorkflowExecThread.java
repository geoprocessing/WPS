package cn.edu.whu.opso.algorithm;

import com.geojmodelbuilder.core.instance.IWorkflowInstance;
import com.geojmodelbuilder.core.provenance.IWorkflowProv;
import com.geojmodelbuilder.engine.IListener;
import com.geojmodelbuilder.engine.IProcessEvent;
import com.geojmodelbuilder.engine.IProcessEvent.EventType;
import com.geojmodelbuilder.engine.impl.RecorderImpl;
import com.geojmodelbuilder.engine.impl.WorkflowEngine;

public class WorkflowExecThread extends Thread implements IListener{


	private WorkflowEngine workflowEngine;
	private Object blocked = new Object();
	public WorkflowExecThread(IWorkflowInstance workflowPlan){
		this.workflowEngine = new WorkflowEngine(workflowPlan, new RecorderImpl());
		this.workflowEngine.subscribe(this, EventType.Stopped);
	}
	
	@Override
	public void run() {
		super.run();
		this.workflowEngine.execute();
		synchronized (blocked) {
			try {
				blocked.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("run finished");
	}
	
	public void onEvent(IProcessEvent event) {
		if (event.getType().equals(EventType.Stopped)) {
			IWorkflowProv workflowTrace = this.workflowEngine.getWorkflowTrace();
			boolean succeeded = workflowTrace.getStatus();
			if(!succeeded){
				System.err.println("this workflow failed to execute.");
			}else {
				System.out.println("Successful!");
			}
			synchronized (blocked) {
				blocked.notify();
			}
		}
	}

}
