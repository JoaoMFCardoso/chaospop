package testclasses;

import ontologies.populator.PopulationOperations;
import database.implementations.BatchImpl;
import domain.bo.population.Batch;

public class PopulationTester {

	public static void main(String[] args) {
		BatchImpl bi = new BatchImpl();
		Batch b = bi.get("5672fe2c3594eccc4d67faa4");

		PopulationOperations po = new PopulationOperations(b);
		po.processBatch();
	}

}
