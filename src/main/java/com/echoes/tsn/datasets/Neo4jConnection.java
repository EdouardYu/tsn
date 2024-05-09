package com.echoes.tsn.datasets;

import org.neo4j.driver.Session;

/**
 * Interface with set-up the connection to the neo4j server
 */
public interface Neo4jConnection extends AutoCloseable {
	Session getSession();
	boolean verifyConnectivity();
}
