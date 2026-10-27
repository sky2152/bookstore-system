import React from 'react';
import {
  Radar, RadarChart, PolarGrid, PolarAngleAxis, PolarRadiusAxis, Legend, ResponsiveContainer
} from 'recharts';

interface SkillData {
  subject: string;
  A: number; // e.g., Current proficiency
  B: number; // e.g., Target proficiency
  fullMark: number; // Max score for the radar axis
}

interface RadarChartProps {
  data: SkillData[];
  title?: string;
}

export default function CustomRadarChart({ data, title }: RadarChartProps) {
  return (
    <div className="w-full h-80">
      {title && <h3 className="text-lg font-semibold text-gray-800 text-center mb-4">{title}</h3>}
      <ResponsiveContainer width="100%" height="100%">
        <RadarChart cx="50%" cy="50%" outerRadius="80%" data={data}>
          <PolarGrid />
          <PolarAngleAxis dataKey="subject" />
          <PolarRadiusAxis angle={90} domain={[0, 100]} /> {/* Assuming scores from 0 to 100 */}
          <Radar name="当前能力" dataKey="A" stroke="#8884d8" fill="#8884d8" fillOpacity={0.6} />
          <Radar name="目标能力" dataKey="B" stroke="#82ca9d" fill="#82ca9d" fillOpacity={0.6} />
          <Legend />
        </RadarChart>
      </ResponsiveContainer>
    </div>
  );
}
