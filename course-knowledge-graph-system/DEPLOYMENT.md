# Deployment Guide

This guide outlines the general steps to deploy the AI Career Navigator and Lifelong Learning Partner system.

## 1. Backend (FastAPI + Neo4j)

### Prerequisites
*   Python 3.8+
*   Neo4j Database instance (accessible from the deployment environment)

### Setup Environment Variables
Create a `.env` file in the `api/` directory or set these as system/deployment environment variables:
*   `DASHSCOPE_API_KEY`: Your API key for Tongyi Qianwen LLM.
*   `NEO4J_URI`: Neo4j connection URI (e.g., `neo4j://localhost:7687`).
*   `NEO4J_USER`: Neo4j username.
*   `NEO4J_PASSWORD`: Neo4j password.

### Installation
Navigate to the `api/` directory and install dependencies:
```bash
pip install -r requirements.txt
```

### Database Initialization
Ensure your Neo4j database schema and initial data are set up:
```bash
python create_neo4j_schema.py
```

### Running the Backend
Start the FastAPI application. For production, use a process manager like Gunicorn with Uvicorn workers, or equivalent:
```bash
uvicorn main:app --host 0.0.0.0 --port 8001
```

## 2. Frontend (React + TypeScript + Vite)

### Prerequisites
*   Node.js (LTS version recommended)
*   npm or Yarn

### Setup Environment Variables
Create a `.env` file in the project root (`./`) or set these as system/deployment environment variables. These variables must be prefixed with `VITE_` to be exposed to the Vite build process:
*   `VITE_API_BASE_URL`: The base URL of your deployed FastAPI backend (e.g., `https://your-backend.example.com/api`).

### Installation
Navigate to the project root directory and install dependencies:
```bash
npm install
# or yarn install
```

### Building the Frontend
Build the production-ready frontend assets:
```bash
npm run build
# or yarn build
```
This will create a `dist/` directory containing the static files.

### Serving the Frontend
The contents of the `dist/` directory can be served by any static file server (e.g., Nginx, Apache, or integrated into a Node.js server).

## 3. Multi-end Release (General Considerations for Antbox-like Platforms)

For platforms like Antbox that support multi-end release (e.g., web, mobile web, mini-programs), consider the following:

*   **Environment Configuration**: Ensure `VITE_API_BASE_URL` is correctly configured for each target environment/platform.
*   **Responsive Design**: The frontend is built with Tailwind CSS, which facilitates responsive design. Verify responsiveness across target devices/viewports.
*   **Platform-Specific Adapters**: If Antbox provides specific APIs or SDKs for its multi-end ecosystem, you might need to implement platform-specific adapters in the frontend to interact with those. This project uses standard web technologies, so direct integration might require a wrapper or custom bridge for non-standard webview environments.
*   **Authentication/Authorization**: Integrate with the platform's authentication/authorization mechanisms if required.
*   **Performance Optimization**: Optimize frontend assets (e.g., image compression, code splitting) for faster loading on various platforms.

This `DEPLOYMENT.md` serves as a general guideline. Specific Antbox platform configurations would require consultation of Antbox's official documentation and tools.