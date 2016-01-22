package testclasses;

import ontologies.populator.PopulationOperations;
import database.implementations.BatchImpl;
import domain.bo.population.Batch;

public class PopulationTester {

	public static void main(String[] args) {
		BatchImpl bi = new BatchImpl();
		Batch b = bi.get("56a201bf4a92b7f3b429347f");

		PopulationOperations po = new PopulationOperations(b);
		po.processBatch();
	}

}
