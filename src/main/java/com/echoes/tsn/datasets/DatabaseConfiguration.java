package com.echoes.tsn.datasets;

interface DatabaseConfiguration {
	String getMongoUri();

	String getMongoDatabaseName();

	String getNeo4jUri();

	String getNeo4jUsername();

	String getNeo4jPassword();

	int getNeo4jConnectionAcquisitionTimeout();

	int getNeo4jConnectionTimeout();
}
