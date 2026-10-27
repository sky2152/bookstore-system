import os
from neo4j import GraphDatabase

# Configuration for Neo4j connection
NEO4J_URI = os.getenv("NEO4J_URI", "bolt://localhost:7687")
NEO4J_USER = os.getenv("NEO4J_USER", "neo4j")
NEO4J_PASSWORD = os.getenv("NEO4J_PASSWORD", "123456789") # Using provided password as default

# Initialize the Neo4j driver
def get_neo4j_driver():
    driver = None
    try:
        driver = GraphDatabase.driver(NEO4J_URI, auth=(NEO4J_USER, NEO4J_PASSWORD))
        driver.verify_connectivity()
        print("Neo4j driver initialized successfully.")
    except Exception as e:
        print(f"Error initializing Neo4j driver: {e}")
        if driver:
            driver.close()
        driver = None
    return driver

# Global driver instance
neo4j_driver = get_neo4j_driver()

def get_db_session():
    """
    Returns a new Neo4j session from the global driver.
    """
    if not neo4j_driver:
        raise ConnectionError("Neo4j driver is not initialized.")
    return neo4j_driver.session()

def close_neo4j_driver():
    """
    Closes the global Neo4j driver.
    """
    global neo4j_driver
    if neo4j_driver:
        neo4j_driver.close()
        print("Neo4j driver closed.")
        neo4j_driver = None


# Example usage (for testing purposes, can be removed later)
if __name__ == "__main__":
    try:
        with get_db_session() as session:
            result = session.run("RETURN 'Neo4j connection test successful!' AS message")
            print(result.single()["message"])
    except Exception as e:
        print(f"Test failed: {e}")
    finally:
        close_neo4j_driver()
