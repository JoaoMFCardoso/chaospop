package testclasses;

import java.util.List;

import ontologies.populator.PopulationOperations;
import database.implementations.BatchImpl;
import domain.bo.population.Batch;

public class PopulationTester {

	public static void main(String[] args) {
		BatchImpl bi = new BatchImpl();
		List<Batch> batches = bi.getAll();

		for(Batch b : batches){
			PopulationOperations po = new PopulationOperations(b);
			po.processBatch();
		}
	}

}
