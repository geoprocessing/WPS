package cn.edu.whu.opso.io;

import java.io.IOException;
import java.io.InputStream;

import org.n52.wps.io.data.IData;
import org.n52.wps.io.datahandler.generator.AbstractGenerator;

public class OPSOGenerator extends AbstractGenerator {

	public OPSOGenerator(){
		super();
		supportedIDataTypes.add(OPSODataBinding.class);
	}
	
	@Override
	public boolean isSupportedSchema(String schema) {
		//no schema checks
		return true;
	}
	
	@Override
	public InputStream generateStream(IData data, String mimeType, String schema)
			throws IOException {
		// TODO Auto-generated method stub
		System.out.println();
		return null;
	}

	public String getHref(IData data){
		if(data instanceof OPSODataBinding){
			return ((OPSODataBinding)data).getPayload().getValue().toString();
		}
		
		return null;
	}
}
