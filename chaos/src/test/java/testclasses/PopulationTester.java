package testclasses;

import java.io.FileNotFoundException;
import java.util.List;

import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import ontologies.populator.PopulationOperations;
import database.implementations.BatchImpl;
import domain.bo.population.Batch;
import exceptions.ChaosPopException;

public class PopulationTester {

	public static void main(String[] args) {
		BatchImpl bi = new BatchImpl();
		List<Batch> batches = bi.getAll();

		for(Batch b : batches){
			try {
			PopulationOperations po = new PopulationOperations(b);
			po.processBatch();
			}catch(FileNotFoundException | OWLOntologyStorageException | OWLOntologyCreationException | ChaosPopException exception) {
				exception.printStackTrace();
			}
		}
	}

}
