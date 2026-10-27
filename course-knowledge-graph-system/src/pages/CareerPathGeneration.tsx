import React, { useState } from 'react';
import { generateCareerPath } from '../lib/api';
import ReactMarkdown from 'react-markdown';
import CustomRadarChart from '../components/RadarChart'; // Added import
import ProgressIndicator from '../components/ProgressIndicator'; // Added import

export default function CareerPathGeneration() {
  const [userId, setUserId] = useState('');
  const [careerGoalId, setCareerGoalId] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [pathResult, setPathResult] = useState<any>(null);

  // Dummy data for Radar Chart and Progress Indicator for demonstration
  // In a real application, this data would be derived from user profile and generated path
  const dummyRadarData = [
    { subject: '编程', A: 80, B: 100, fullMark: 100 },
    { subject: '沟通', A: 60, B: 90, fullMark: 100 },
    { subject: '项目管理', A: 70, B: 95, fullMark: 100 },
    { subject: '数据分析', A: 50, B: 85, fullMark: 100 },
    { subject: 'AI/机器学习', A: 40, B: 100, fullMark: 100 },
    { subject: '创新思维', A: 75, B: 90, fullMark: 100 },
  ];

  const dummyProgress = 65; // 65% completion for the career path

  const handleGeneratePath = async () => {
    setLoading(true);
    setError(null);
    setPathResult(null);
    try {
      const result = await generateCareerPath({ user_id: userId, career_goal_id: careerGoalId });
      setPathResult(result);
      // Here you would typically process result to update radarData and progress
    } catch (err) {
      console.error('Error generating career path:', err);
      setError(`Failed to generate path: ${(err as Error).message}`);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-100 p-8">
      <div className="max-w-4xl mx-auto bg-white p-6 rounded-lg shadow-md">
        <h1 className="text-3xl font-bold text-gray-800 mb-6">个性化职业路径生成</h1>

        <div className="mb-4">
          <label htmlFor="userId" className="block text-gray-700 text-sm font-bold mb-2">
            用户ID
          </label>
          <input
            type="text"
            id="userId"
            className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
            value={userId}
            onChange={(e) => setUserId(e.target.value)}
            placeholder="输入用户ID (e.g., u1)"
          />
        </div>

        <div className="mb-6">
          <label htmlFor="careerGoalId" className="block text-gray-700 text-sm font-bold mb-2">
            职业目标ID
          </label>
          <input
            type="text"
            id="careerGoalId"
            className="shadow appearance-none border rounded w-full py-2 px-3 text-gray-700 leading-tight focus:outline-none focus:shadow-outline"
            value={careerGoalId}
            onChange={(e) => setCareerGoalId(e.target.value)}
            placeholder="输入职业目标ID (e.g., cg1)"
          />
        </div>

        <button
          onClick={handleGeneratePath}
          className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline disabled:opacity-50"
          disabled={loading || !userId || !careerGoalId}
        >
          {loading ? '生成中...' : '生成职业路径'}
        </button>

        {error && <p className="text-red-500 mt-4">错误: {error}</p>}

        {pathResult && (
          <div className="mt-8">
            <h2 className="text-xl font-semibold text-gray-800 mb-4">生成的职业路径:</h2>
            
            {/* Radar Chart for Skill Comparison */}
            <div className="mb-6 p-4 bg-white border border-gray-200 rounded-lg shadow-sm">
              <CustomRadarChart data={dummyRadarData} title="能力画像 (当前 vs. 目标)" />
            </div>

            {/* Progress Indicator */}
            <div className="mb-6 p-4 bg-white border border-gray-200 rounded-lg shadow-sm">
              <ProgressIndicator percentage={dummyProgress} label="路径完成度" color="bg-green-500" />
            </div>

            <div className="mb-6 p-4 bg-blue-50 border border-blue-200 rounded-lg">
              <h3 className="text-lg font-medium text-blue-800 mb-2">AI 路径建议:</h3>
              <div className="prose max-w-none">
                <ReactMarkdown>{pathResult.llm_path_suggestion}</ReactMarkdown>
              </div>
            </div>
            {pathResult.detailed_resources_from_kg && pathResult.detailed_resources_from_kg.length > 0 && (
              <div className="p-4 bg-green-50 border border-green-200 rounded-lg">
                <h3 className="text-lg font-medium text-green-800 mb-2">知识图谱中的详细资源:</h3>
                {pathResult.detailed_resources_from_kg.map((item: any, index: number) => (
                  <div key={index} className="mb-2">
                    <p className="font-semibold">技能: {item.skill}</p>
                    {item.suggested_resources.map((res: any, resIndex: number) => (
                      <p key={resIndex} className="ml-4">
                        - {res.unit_name} ({res.unit_type}): <a href={res.resource_url} target="_blank" rel="noopener noreferrer" className="text-blue-600 hover:underline">{res.resource_name}</a>
                      </p>
                    ))}
                  </div>
                ))}
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
}