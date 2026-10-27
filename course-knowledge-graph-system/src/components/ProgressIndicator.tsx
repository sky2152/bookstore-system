import React from 'react';

interface ProgressIndicatorProps {
  percentage: number; // 0 to 100
  label?: string; // e.g., "学习进度", "匹配度"
  color?: string; // Tailwind CSS color class, e.g., "bg-blue-500"
}

export default function ProgressIndicator({ percentage, label, color = "bg-blue-500" }: ProgressIndicatorProps) {
  const clampedPercentage = Math.max(0, Math.min(100, percentage));

  return (
    <div className="w-full mb-4">
      {label && <div className="text-gray-700 text-sm font-bold mb-1">{label}: {clampedPercentage}%</div>}
      <div className="w-full bg-gray-200 rounded-full h-2.5">
        <div
          className={`${color} h-2.5 rounded-full`}
          style={{ width: `${clampedPercentage}%` }}
        ></div>
      </div>
    </div>
  );
}
