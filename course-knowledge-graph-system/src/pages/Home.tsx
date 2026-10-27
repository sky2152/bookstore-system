import React, { useState } from "react";
import { generateCareerPath, GeneratePathResponse } from "@/lib/api";
import ReactMarkdown from 'react-markdown'; // Assuming react-markdown is installed

export default function Home() {
  const [userId, setUserId] = useState("u1"); // Default to u1 for easy testing
  const [careerGoalId, setCareerGoalId] = useState("cg1"); // Default to cg1
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [pathData, setPathData] = useState<GeneratePathResponse | null>(null);

  const handleGeneratePath = async () => {
    setLoading(true);
    setError(null);
    setPathData(null);
    try {
      const response = await generateCareerPath({ user_id: userId, career_goal_id: careerGoalId });
      if (response.error) {
        setError(response.error);
      } else {
        setPathData(response);
      }
    } catch (err) {
      console.error("Failed to generate career path:", err);
      setError("Failed to generate career path. Please check console for details.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container mx-auto p-4">
      <h1 className="text-3xl font-bold mb-6 text-center">AI职业导航与学习伙伴</h1>

      <div className="bg-white p-6 rounded-lg shadow-md mb-8">
        <h2 className="text-xl font-semibold mb-4">生成职业发展路径</h2>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-4">
          <div>
            <label htmlFor="userId" className="block text-sm font-medium text-gray-700">用户ID:</label>
            <input
              type="text"
              id="userId"
              value={userId}
              onChange={(e) => setUserId(e.target.value)}
              className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
              placeholder="例如: u1"
            />
          </div>
          <div>
            <label htmlFor="careerGoalId" className="block text-sm font-medium text-gray-700">职业目标ID:</label>
            <input
              type="text"
              id="careerGoalId"
              value={careerGoalId}
              onChange={(e) => setCareerGoalId(e.target.value)}
              className="mt-1 block w-full rounded-md border-gray-300 shadow-sm focus:border-indigo-500 focus:ring-indigo-500 sm:text-sm"
              placeholder="例如: cg1"
            />
          </div>
        </div>
        <button
          onClick={handleGeneratePath}
          disabled={loading}
          className="w-full py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 disabled:opacity-50"
        >
          {loading ? "生成中..." : "生成个性化职业路径"}
        </button>

        {error && (
          <div className="mt-4 text-red-600 bg-red-100 p-3 rounded-md">
            错误: {error}
          </div>
        )}
      </div>

      {pathData && (
        <div className="bg-white p-6 rounded-lg shadow-md">
          <h2 className="text-xl font-semibold mb-4">您的职业发展路径建议</h2>
          <p className="mb-2"><strong>用户:</strong> {pathData.user_name}</p>
          <p className="mb-4"><strong>职业目标:</strong> {pathData.career_goal}</p>
          
          <div className="prose lg:prose-xl max-w-none">
            <ReactMarkdown>{pathData.llm_path_suggestion}</ReactMarkdown>
          </div>

          {pathData.detailed_resources_from_kg && pathData.detailed_resources_from_kg.length > 0 && (
            <div className="mt-6">
              <h3 className="text-lg font-semibold mb-2">知识图谱推荐资源:</h3>
              {/* Render detailed resources here */}
            </div>
          )}
        </div>
      )}
    </div>
  );
}
