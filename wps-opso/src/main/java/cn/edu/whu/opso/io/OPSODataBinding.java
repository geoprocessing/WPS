package cn.edu.whu.opso.io;

import com.geojmodelbuilder.core.data.IComplexData;

public class OPSODataBinding implements org.n52.wps.io.data.IComplexData{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private IComplexData data;
	
	public OPSODataBinding(IComplexData data){
		this.data = data;
	}
	
	@Override
	public IComplexData getPayload() {
		return this.data;
	}

	@Override
	public Class<?> getSupportedClass() {
		return IComplexData.class;
	}

	@Override
	public void dispose() {
	}

}
