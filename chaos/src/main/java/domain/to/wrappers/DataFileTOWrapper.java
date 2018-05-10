package domain.to.wrappers;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;

import domain.to.DataFileTO;

/**
 * This class is a Wrapper for the DataFileTO class
 * @author João M. F. Cardoso
 *
 */
@XmlRootElement
public class DataFileTOWrapper {
	public ArrayList<DataFileTO> dataFilesTO;
	
	public DataFileTOWrapper() {
		this.dataFilesTO = new ArrayList<DataFileTO>();
	}
}
