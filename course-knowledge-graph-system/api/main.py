import os
from fastapi import FastAPI
from pydantic import BaseModel
from .neo4j_utils import get_db_session, close_neo4j_driver
from .llm_utils import get_llm_response
from .career_path_generator import CareerPathGenerator
from fastapi.middleware.cors import CORSMiddleware # Added this line

app = FastAPI()

# Added CORS Middleware configuration
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # Allow all origins for development
    allow_credentials=True,
    allow_methods=["*"],  # Allow all methods
    allow_headers=["*"],  # Allow all headers
)

# Neo4j Connection Configuration (now using environment variables)
NEO4J_URI = os.getenv("NEO4J_URI", "neo4j://localhost:7687")
NEO4J_USER = os.getenv("NEO4J_USER", "neo4j")
NEO4J_PASSWORD = os.getenv("NEO4J_PASSWORD", "123456789") # Using provided password as default

# Initialize CareerPathGenerator
career_path_generator = CareerPathGenerator()

@app.on_event("startup")
async def startup_event():
    print("Application startup: Initializing services...")
    pass

@app.on_event("shutdown")
async def shutdown_event():
    print("Application shutdown: Closing services...")
    close_neo4j_driver()

# Pydantic model for chat requests
class ChatRequest(BaseModel):
    message: str

# Pydantic model for Neo4j query requests
class CypherQueryRequest(BaseModel):
    query: str

# Pydantic model for generating career path requests
class GeneratePathRequest(BaseModel):
    user_id: str
    career_goal_id: str

# Pydantic models for scenario simulation requests and responses
class ScenarioRequest(BaseModel):
    scenario_type: str # e.g., "remote_collaboration", "ai_office", "cross_functional_communication"
    user_message: str

class ScenarioResponse(BaseModel):
    ai_response: str
    feedback: str

@app.get("/api/hello")
async def hello():
    return {"message": "Hello from FastAPI!"}

@app.get("/api/neo4j/test")
async def neo4j_test():
    try:
        with get_db_session() as session:
            result = session.run("CALL db.info()")
            db_info = [record.data() for record in result]
        return {"message": "Successfully connected to Neo4j", "db_info": db_info}
    except Exception as e:
        return {"error": f"Failed to query Neo4j: {e}"}

@app.post("/api/chat")
async def chat_with_llm(request: ChatRequest):
    """
    Endpoint to interact with the Large Language Model.
    """
    prompt = request.message
    llm_response = get_llm_response(prompt)
    return {"response": llm_response}

@app.post("/api/neo4j/run_query")
async def run_cypher_query(request: CypherQueryRequest):
    """
    Endpoint to run arbitrary Cypher queries against the Neo4j database.
    (For development/testing purposes - use with caution in production!)
    """
    try:
        with get_db_session() as session:
            result = session.run(request.query)
            data = [record.data() for record in result]
        return {"message": "Query executed successfully", "results": data}
    except Exception as e:
        return {"error": f"Failed to execute Cypher query: {e}"}

@app.post("/api/generate_path")
async def generate_career_path_api(request: GeneratePathRequest):
    """
    Endpoint to generate a personalized career development path.
    """
    try:
        path = career_path_generator.generate_path(request.user_id, request.career_goal_id)
        return path
    except Exception as e:
        return {"error": f"Failed to generate career path: {e}"}

@app.post("/api/simulate_scenario", response_model=ScenarioResponse)
async def simulate_scenario_api(request: ScenarioRequest):
    """
    Endpoint to simulate typical workplace scenarios.
    """
    try:
        scenario_prompts = {
            "remote_collaboration": "You are a project manager leading a remote team meeting. The user is a team member. Respond to the user's message as the project manager, and then provide concise feedback on the user's communication style and effectiveness in a remote setting.",
            "ai_office": "You are an AI assistant in a modern AI-powered office. The user is interacting with you for a task. Respond as the AI assistant, and then provide concise feedback on the user's clarity and efficiency in using AI tools.",
            "cross_functional_communication": "You are a lead engineer in a cross-functional team. The user is a colleague from a different department trying to communicate a technical requirement. Respond as the lead engineer, focusing on clarifying the technical aspects, and then provide concise feedback on the user's ability to bridge communication gaps.",
            "client_negotiation": "You are a key account manager in a client negotiation meeting. The user is the client. Respond to the client's proposal/query, and then provide concise feedback on the user's negotiation tactics and clarity.",
            "performance_review": "You are a team lead conducting a performance review. The user is your team member. Respond to their self-assessment or query about their performance, and then provide concise feedback on their self-awareness and communication during a review."
        }
        
        # Get scenario-specific prompt, default to a general one if not found
        base_prompt = scenario_prompts.get(request.scenario_type, "You are an AI workplace mentor. Simulate a workplace scenario. Respond to the user's message, and then provide concise feedback on the user's communication.")

        system_prompt = f"{base_prompt} User says: '{request.user_message}'."
        llm_full_response = get_llm_response(system_prompt)

        # For simplicity, we'll split the LLM response into AI response and feedback
        # In a real application, you might use more sophisticated parsing or structured LLM output
        if "Feedback:" in llm_full_response:
            ai_response, feedback = llm_full_response.split("Feedback:", 1)
        else:
            ai_response = llm_full_response
            feedback = "(No specific feedback provided by AI for this turn.)"

        return ScenarioResponse(ai_response=ai_response.strip(), feedback=feedback.strip())
    except Exception as e:
        return ScenarioResponse(ai_response="Error in simulation", feedback=f"Failed to simulate scenario: {e}")
