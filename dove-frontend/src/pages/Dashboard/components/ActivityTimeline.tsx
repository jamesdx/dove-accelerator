// Dashboard/components/ActivityTimeline.tsx
import React from 'react';
import { Timeline } from 'antd';
import {
  ProjectOutlined,
  TaskOutlined,
  TeamOutlined,
  MessageOutlined,
} from '@ant-design/icons';

const ActivityTimeline: React.FC = () => {
  const activities = [
    {
      id: 1,
      type: 'project',
      content: 'New project "E-commerce AI Assistant" created',
      user: 'John Doe',
      time: '2 hours ago',
    },
    {
      id: 2,
      type: 'task',
      content: 'Task "User Flow Analysis" completed',
      user: 'Alice Smith',
      time: '4 hours ago',
    },
    {
      id: 3,
      type: 'agent',
      content: 'New AI Agent "Data Analyst" joined the team',
      user: 'System',
      time: '5 hours ago',
    },
    {
      id: 4,
      type: 'message',
      content: 'Code review completed for "Payment Integration"',
      user: 'Bob Johnson',
      time: '1 day ago',
    },
  ];

  const getIcon = (type: string) => {
    switch (type) {
      case 'project':
        return <ProjectOutlined className="text-primary" />;
      case 'task':
        return <TaskOutlined className="text-warning" />;
      case 'agent':
        return <TeamOutlined className="text-success" />;
      case 'message':
        return <MessageOutlined className="text-secondary" />;
      default:
        return null;
    }
  };

  return (
    <Timeline
      items={activities.map(activity => ({
        dot: getIcon(activity.type),
        children: (
          <div key={activity.id}>
            <p className="mb-0">{activity.content}</p>
            <p className="text-secondary text-sm">
              {activity.user} • {activity.time}
            </p>
          </div>
        ),
      }))}
    />
  );
};

export default ActivityTimeline;