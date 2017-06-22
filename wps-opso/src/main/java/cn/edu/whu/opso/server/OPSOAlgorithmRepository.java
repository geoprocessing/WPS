package cn.edu.whu.opso.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import net.opengis.wps.x100.ProcessDescriptionType;

import org.n52.wps.server.IAlgorithm;
import org.n52.wps.server.IAlgorithmRepository;

import cn.edu.whu.opso.OPSOConfig;
import cn.edu.whu.opso.algorithm.OPSOInstanceAlgorithm;

public class OPSOAlgorithmRepository implements IAlgorithmRepository {

	private String repositoryPath = null;
	private Properties repositoryProperties;
	private boolean changed = false;
	private List<IAlgorithm> algorithms;
	private Map<String, ProcessDescriptionType> descriptionMap;

	public OPSOAlgorithmRepository() {
		this.repositoryPath = OPSOConfig.getInstance().getAlgorithmRepositoy();
		this.repositoryProperties = new Properties();
		this.algorithms = new ArrayList<IAlgorithm>();
		this.descriptionMap = new HashMap<String, ProcessDescriptionType>();

		File file = new File(this.repositoryPath);
		FileInputStream fileInput;
		try {
			fileInput = new FileInputStream(file);
			try {
				this.repositoryProperties.loadFromXML(fileInput);
			} catch (InvalidPropertiesFormatException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public Collection<String> getAlgorithmNames() {
		if (this.repositoryProperties == null)
			return null;

		Collection<String> algorithms = new ArrayList<String>();
		for (Object key : repositoryProperties.keySet()) {
			algorithms.add(key.toString());
		}

		return algorithms;
	}

	public void addAlgorithm(String name) {
		changed = true;
		this.repositoryProperties.put(name, name);
	}

	@Override
	public IAlgorithm getAlgorithm(String processID) {
		for (IAlgorithm algorithm : this.algorithms) {
			if (algorithm.getWellKnownName().equals(processID))
				return algorithm;
		}
		OPSOInstanceAlgorithm algorithm = new OPSOInstanceAlgorithm(processID);
		this.algorithms.add(algorithm);
		return algorithm;
	}

	@Override
	public ProcessDescriptionType getProcessDescription(String processID) {
		ProcessDescriptionType descriptionType = this.descriptionMap
				.get(processID);
		if (descriptionType != null)
			return descriptionType;

		IAlgorithm algorithm = getAlgorithm(processID);
		descriptionType = algorithm.getDescription();
		descriptionMap.put(processID, descriptionType);

		return descriptionType;
	}

	@Override
	public boolean containsAlgorithm(String processID) {
		if (this.repositoryProperties.containsKey(processID))
			return true;

		return false;
	}

	@Override
	public void shutdown() {
		if (!changed)
			return;

		File file = new File(this.repositoryPath);
		FileOutputStream fileOut;
		try {
			fileOut = new FileOutputStream(file);
			repositoryProperties.storeToXML(fileOut, "Algorithm");
			fileOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
