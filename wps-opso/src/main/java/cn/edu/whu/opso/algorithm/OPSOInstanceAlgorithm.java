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
import cn.edu.whu.opso.io.OPSODataBinding;

/**
 * Refer to AbstractObservableAlgorithm.
 */
public class OPSOInstanceAlgorithm implements IAlgorithm {

	private String identifier;
	private ProcessDescriptionType processDescription;
	private List<String> errList = new ArrayList<String>();
	
	public OPSOInstanceAlgorithm(String identifier) {
		this.identifier = identifier;
	}

	@Override
	public Map<String, IData> run(Map<String, List<IData>> inputData)
			throws ExceptionReport {
		for(String key:inputData.keySet()){
			System.out.println(key);
		}
		
		return null;
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

	@Override
	public Class<?> getOutputDataType(String id) {
		return OPSODataBinding.class;
	}
}
