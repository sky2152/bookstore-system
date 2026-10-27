// src/lib/api.ts

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8001/api"; // Our FastAPI backend runs on port 8001

// Define interfaces for API request/response bodies
export interface GeneratePathRequest {
  user_id: string;
  career_goal_id: string;
}

export interface GeneratePathResponse {
  user_name: string;
  career_goal: string;
  llm_path_suggestion: string;
  detailed_resources_from_kg: any[]; // Assuming it's an array of any type for now
  error?: string; // Optional error field
}

export interface ChatRequest {
  message: string;
}

export interface ChatResponse {
  response: string;
  error?: string;
}

// New Pydantic models for scenario simulation requests and responses
export interface ScenarioRequest {
  scenario_type: string;
  user_message: string;
}

export interface ScenarioResponse {
  ai_response: string;
  feedback: string;
  error?: string;
}

// Generic function to make API calls
async function callApi<T>(
  endpoint: string,
  method: string,
  data?: any
): Promise<T> {
  const headers: HeadersInit = {
    "Content-Type": "application/json",
    Accept: "application/json",
  };

  const config: RequestInit = {
    method,
    headers,
    body: data ? JSON.stringify(data) : undefined,
  };

  try {
    const response = await fetch(`${API_BASE_URL}${endpoint}`, config);
    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(
        `API call failed: ${response.status} ${response.statusText} - ${
          errorData.detail || JSON.stringify(errorData)
        }`
      );
    }
    return await response.json();
  } catch (error) {
    console.error("API call error:", error);
    throw error;
  }
}

// Specific API functions
export const generateCareerPath = async (
  request: GeneratePathRequest
): Promise<GeneratePathResponse> => {
  return callApi<GeneratePathResponse>("/generate_path", "POST", request);
};

export const chatWithLLM = async (
  request: ChatRequest
): Promise<ChatResponse> => {
  return callApi<ChatResponse>("/chat", "POST", request);
};

export const simulateScenario = async (
  request: ScenarioRequest
): Promise<ScenarioResponse> => {
  return callApi<ScenarioResponse>("/simulate_scenario", "POST", request);
};

export const testNeo4jConnection = async (): Promise<any> => {
  return callApi<any>("/neo4j/test", "GET");
};

export const getHelloMessage = async (): Promise<{ message: string }> => {
  return callApi<{ message: string }>("/hello", "GET");
};
