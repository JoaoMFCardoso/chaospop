package domain.to.wrappers;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;

import domain.to.IndividualMappingTO;

/**
 * This class is a Wrapper for the IndividualMappingTO class
 * @author João M. F. Cardoso
 *
 */
@XmlRootElement
public class IndividualMappingTOWrapper {
	public ArrayList<IndividualMappingTO> individualMappingsTO;
	
	public IndividualMappingTOWrapper() {
		this.individualMappingsTO = new ArrayList<IndividualMappingTO>();
	}
}
