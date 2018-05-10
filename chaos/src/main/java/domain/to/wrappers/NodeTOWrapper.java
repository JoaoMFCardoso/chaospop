package domain.to.wrappers;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;

import domain.to.NodeTO;

/**
 * This class is a Wrapper for the NodeTO class
 * @author João M. F. Cardoso
 *
 */
@XmlRootElement
public class NodeTOWrapper {
	public ArrayList<NodeTO> nodesTO;
	
	public NodeTOWrapper() {
		this.nodesTO = new ArrayList<NodeTO>();
	}
}
