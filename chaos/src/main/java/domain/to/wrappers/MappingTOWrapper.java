package domain.to.wrappers;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;

import domain.to.MappingTO;

/**
 * This class is a Wrapper for the MappingTO class
 * @author João M. F. Cardoso
 *
 */
@XmlRootElement
public class MappingTOWrapper {
	public ArrayList<MappingTO> mappingsTO;
	
	public MappingTOWrapper() {
		this.mappingsTO = new ArrayList<MappingTO>();
	}
}
