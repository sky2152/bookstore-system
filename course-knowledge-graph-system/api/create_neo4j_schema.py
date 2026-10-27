from neo4j import GraphDatabase

NEO4J_URI = "bolt://localhost:7687"  # Assuming default Neo4j URI
NEO4J_USER = "neo4j"
NEO4J_PASSWORD = "123456789" # Using provided password as default

def create_neo4j_constraints(driver):
    """
    Creates unique constraints for nodes in the Neo4j database.
    """
    with driver.session() as session:
        # User
        session.run("CREATE CONSTRAINT IF NOT EXISTS FOR (u:User) REQUIRE u.id IS UNIQUE")
        # CareerGoal
        session.run("CREATE CONSTRAINT IF NOT EXISTS FOR (cg:CareerGoal) REQUIRE cg.id IS UNIQUE")
        # Skill
        session.run("CREATE CONSTRAINT IF NOT EXISTS FOR (s:Skill) REQUIRE s.id IS UNIQUE")
        # LearningUnit
        session.run("CREATE CONSTRAINT IF NOT EXISTS FOR (lu:LearningUnit) REQUIRE lu.id IS UNIQUE")
        # JobRole
        session.run("CREATE CONSTRAINT IF NOT EXISTS FOR (jr:JobRole) REQUIRE jr.id IS UNIQUE")
        # Industry
        session.run("CREATE CONSTRAINT IF NOT EXISTS FOR (i:Industry) REQUIRE i.id IS UNIQUE")
        # Resource
        session.run("CREATE CONSTRAINT IF NOT EXISTS FOR (r:Resource) REQUIRE r.id IS UNIQUE")
        # WorkplaceScenario
        session.run("CREATE CONSTRAINT IF NOT EXISTS FOR (ws:WorkplaceScenario) REQUIRE ws.id IS UNIQUE")
    print("Neo4j constraints created successfully.")

def create_initial_data(driver):
    """
    Creates some initial data for testing purposes.
    """
    with driver.session() as session:
        # Create some sample skills
        session.run("MERGE (:Skill {id: 's1', name: 'Python编程', category: '编程语言'})")
        session.run("MERGE (:Skill {id: 's2', name: '数据分析', category: '数据科学'})")
        session.run("MERGE (:Skill {id: 's3', name: '机器学习', category: '人工智能'})")
        session.run("MERGE (:Skill {id: 's4', name: '沟通能力', category: '软技能'})")
        session.run("MERGE (:Skill {id: 's5', name: '项目管理', category: '管理'})")

        # Create some sample career goals
        session.run("MERGE (:CareerGoal {id: 'cg1', name: '成为高级数据科学家', description: '在数据科学领域深入发展，负责复杂的数据分析和模型构建'})")
        session.run("MERGE (:CareerGoal {id: 'cg2', name: '成为AI产品经理', description: '负责AI产品的规划、设计和落地'})")

        # Create a sample user
        session.run("MERGE (:User {id: 'u1', name: '张三', interests: ['数据科学', '人工智能'], experience: ['数据分析师']})")

        # Connect user to career goal
        session.run("MATCH (u:User {id: 'u1'}), (cg:CareerGoal {id: 'cg1'}) MERGE (u)-[:HAS_GOAL {start_date: '2023-01-01', target_date: '2028-01-01'}]->(cg)")

        # Connect career goal to required skills
        session.run("MATCH (cg:CareerGoal {id: 'cg1'}), (s:Skill {id: 's1'}) MERGE (cg)-[:REQUIRES_SKILL {importance: 5}]->(s)")
        session.run("MATCH (cg:CareerGoal {id: 'cg1'}), (s:Skill {id: 's2'}) MERGE (cg)-[:REQUIRES_SKILL {importance: 4}]->(s)")
        session.run("MATCH (cg:CareerGoal {id: 'cg1'}), (s:Skill {id: 's3'}) MERGE (cg)-[:REQUIRES_SKILL {importance: 5}]->(s)")
        session.run("MATCH (cg:CareerGoal {id: 'cg1'}), (s:Skill {id: 's4'}) MERGE (cg)-[:REQUIRES_SKILL {importance: 3}]->(s)")

        # User possesses some skills
        session.run("MATCH (u:User {id: 'u1'}), (s:Skill {id: 's1'}) MERGE (u)-[:POSSESSES_SKILL {level: 3, last_assessed: '2023-09-01'}]->(s)")
        session.run("MATCH (u:User {id: 'u1'}), (s:Skill {id: 's2'}) MERGE (u)-[:POSSESSES_SKILL {level: 2, last_assessed: '2023-09-01'}]->(s)")

        print("Neo4j initial data created successfully.")


def main():
    driver = None
    try:
        driver = GraphDatabase.driver(NEO4J_URI, auth=(NEO4J_USER, NEO4J_PASSWORD))
        driver.verify_connectivity()
        print("Neo4j connection successful.")
        create_neo4j_constraints(driver)
        create_initial_data(driver)
    except Exception as e:
        print(f"Error connecting to Neo4j or creating schema: {e}")
    finally:
        if driver:
            driver.close()
            print("Neo4j connection closed.")

if __name__ == "__main__":
    main()
