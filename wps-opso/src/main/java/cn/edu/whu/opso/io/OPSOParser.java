package cn.edu.whu.opso.io;

import java.io.InputStream;

import org.n52.wps.io.datahandler.parser.AbstractParser;

import com.geojmodelbuilder.core.data.impl.ComplexData;

public class OPSOParser extends AbstractParser {
	
	public OPSOParser(){
		super();
		supportedIDataTypes.add(OPSODataBinding.class);
	}
	
	public OPSODataBinding parse(String reference, String mimeType, String schema) {
		ComplexData complexData = new ComplexData();
		complexData.setSchema(schema);
		complexData.setMimeType(mimeType);

		complexData.setValue(reference);
		
		OPSODataBinding opsoDataBinding = new OPSODataBinding(complexData);
		return opsoDataBinding;
	}
	
	@Override
	public OPSODataBinding parse(InputStream input, String mimeType, String schema) {
		return null;
	}
}
