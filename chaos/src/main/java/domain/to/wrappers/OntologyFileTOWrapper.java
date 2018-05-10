package domain.to.wrappers;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;

import domain.to.OntologyFileTO;

/**
 * This class is a Wrapper for the OntologyFileTO class
 * @author João M. F. Cardoso
 *
 */
@XmlRootElement
public class OntologyFileTOWrapper {
	public ArrayList<OntologyFileTO> ontologyFilesTO;
	
	public OntologyFileTOWrapper() {
		this.ontologyFilesTO = new ArrayList<OntologyFileTO>();
	}
}
