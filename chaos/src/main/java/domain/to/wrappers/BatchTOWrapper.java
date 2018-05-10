package domain.to.wrappers;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;

import domain.to.BatchTO;

/**
 * This class is a Wrapper for the BatchTO class
 * @author João M. F. Cardoso
 *
 */
@XmlRootElement
public class BatchTOWrapper {
	public ArrayList<BatchTO> batchesTO;
	
	public BatchTOWrapper() {
		this.batchesTO = new ArrayList<BatchTO>();
	}
}
