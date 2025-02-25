// Dashboard/components/StatsCard.tsx
import React from 'react';
import { Card, Tooltip } from 'antd';
import {
  ProjectOutlined,
  TaskOutlined,
  TeamOutlined,
  CheckCircleOutlined,
  ArrowUpOutlined,
  ArrowDownOutlined,
} from '@ant-design/icons';

interface StatsCardProps {
  title: string;
  value: number | string;
  icon: 'project' | 'task' | 'agent' | 'rate';
  trend: number;
}

const StatsCard: React.FC<StatsCardProps> = ({ title, value, icon, trend }) => {
  const renderIcon = () => {
    switch (icon) {
      case 'project':
        return <ProjectOutlined className="text-2xl text-primary" />;
      case 'task':
        return <TaskOutlined className="text-2xl text-warning" />;
      case 'agent':
        return <TeamOutlined className="text-2xl text-success" />;
      case 'rate':
        return <CheckCircleOutlined className="text-2xl text-secondary" />;
      default:
        return null;
    }
  };

  return (
    <Card bodyStyle={{ padding: '20px' }}>
      <div className="flex items-center justify-between">
        <div>
          <p className="text-secondary mb-1">{title}</p>
          <h3 className="text-2xl font-semibold">{value}</h3>
        </div>
        <div>{renderIcon()}</div>
      </div>
      <div className="mt-2">
        <Tooltip title="Compared to last month">
          <span className={`flex items-center ${trend >= 0 ? 'text-success' : 'text-danger'}`}>
            {trend >= 0 ? <ArrowUpOutlined /> : <ArrowDownOutlined />}
            <span className="ml-1">{Math.abs(trend)}%</span>
          </span>
        </Tooltip>
      </div>
    </Card>
  );
};

export default StatsCard;