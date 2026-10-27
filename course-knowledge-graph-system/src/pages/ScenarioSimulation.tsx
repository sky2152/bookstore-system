import React, { useState } from "react";
import { simulateScenario } from "@/lib/api";
import ReactMarkdown from "react-markdown";

export default function ScenarioSimulation() {
  const [scenarioType, setScenarioType] = useState("remote_collaboration"); // Default scenario
  const [userMessage, setUserMessage] = useState("");
  const [aiResponse, setAiResponse] = useState("");
  const [feedback, setFeedback] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSimulate = async () => {
    setLoading(true);
    setError(null);
    setAiResponse("");
    setFeedback("");
    try {
      const response = await simulateScenario({ scenario_type: scenarioType, user_message: userMessage });
      setAiResponse(response.ai_response);
      setFeedback(response.feedback);
    } catch (err) {
      console.error("Error simulating scenario:", err);
      setError(`Failed to simulate scenario: ${(err as Error).message}`);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-100 p-8">
      <div className="max-w-4xl mx-auto bg-white p-6 rounded-lg shadow-md">
        <h1 className="text-3xl font-bold text-gray-800 mb-6">职场场景模拟训练</h1>

        <div className="mb-4">
          <label htmlFor="scenarioType" className="block text-gray-700 text-sm font-bold mb-2">
            场景类型
          </label>
          <select
            id="scenarioType"
            className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
            value={scenarioType}
            onChange={(e) => setScenarioType(e.target.value)}
          >
            <option value="remote_collaboration">远程协作</option>
            <option value="ai_office">AI辅助办公</option>
            <option value="cross_functional_communication">跨岗位沟通</option>
            <option value="client_negotiation">客户谈判</option>
            <option value="performance_review">绩效评估</option>
          </select>
        </div>

        <div className="mb-6">
          <label htmlFor="userMessage" className="block text-gray-700 text-sm font-bold mb-2">
            你的消息
          </label>
          <textarea
            id="userMessage"
            className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline h-32"
            value={userMessage}
            onChange={(e) => setUserMessage(e.target.value)}
            placeholder="输入你在场景中想说的话..."
          />
        </div>

        <button
          onClick={handleSimulate}
          className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline disabled:opacity-50"
          disabled={loading || !scenarioType || !userMessage}
        >
          {loading ? "模拟中..." : "模拟场景"}
        </button>

        {error && <p className="text-red-500 mt-4">错误: {error}</p>}

        {aiResponse && (
          <div className="mt-8 p-4 bg-blue-50 border border-blue-200 rounded-lg">
            <h2 className="text-xl font-semibold text-blue-800 mb-2">AI 回复:</h2>
            <div className="prose max-w-none"><ReactMarkdown>{aiResponse}</ReactMarkdown></div>
          </div>
        )}

        {feedback && (
          <div className="mt-4 p-4 bg-green-50 border border-green-200 rounded-lg">
            <h2 className="text-xl font-semibold text-green-800 mb-2">AI 反馈:</h2>
            <div className="prose max-w-none"><ReactMarkdown>{feedback}</ReactMarkdown></div>
          </div>
        )}
      </div>
    </div>
  );
}
