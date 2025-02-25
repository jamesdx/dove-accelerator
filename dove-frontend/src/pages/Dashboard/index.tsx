// Dashboard/index.tsx
import React, { useEffect, useState } from 'react';
import { Row, Col, Card } from 'antd';
import { projectApi, taskApi, agentApi } from '../../services/api';
import StatsCard from './components/StatsCard';
import ActivityTimeline from './components/ActivityTimeline';
import ProjectProgress from './components/ProjectProgress';

const Dashboard: React.FC = () => {
  const [stats, setStats] = useState({
    totalProjects: 0,
    activeTasks: 0,
    activeAgents: 0,
    completionRate: 0,
  });

  useEffect(() => {
    const fetchDashboardData = async () => {
      try {
        const [projects, tasks, agents] = await Promise.all([
          projectApi.getProjects(),
          taskApi.getTasks(),
          agentApi.getAgents(),
        ]);
        
        setStats({
          totalProjects: projects.data.length,
          activeTasks: tasks.data.filter((t: any) => t.status === 'IN_PROGRESS').length,
          activeAgents: agents.data.filter((a: any) => a.status === 'ACTIVE').length,
          completionRate: calculateCompletionRate(tasks.data),
        });
      } catch (error) {
        console.error('Failed to fetch dashboard data:', error);
      }
    };

    fetchDashboardData();
  }, []);

  const calculateCompletionRate = (tasks: any[]) => {
    const completed = tasks.filter(t => t.status === 'COMPLETED').length;
    return tasks.length > 0 ? (completed / tasks.length) * 100 : 0;
  };

  return (
    <div>
      <h2 className="text-2xl font-semibold mb-6">Dashboard</h2>
      <Row gutter={[16, 16]}>
        <Col xs={24} sm={12} lg={6}>
          <StatsCard
            title="Total Projects"
            value={stats.totalProjects}
            icon="project"
            trend={10}
          />
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <StatsCard
            title="Active Tasks"
            value={stats.activeTasks}
            icon="task"
            trend={5}
          />
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <StatsCard
            title="Active Agents"
            value={stats.activeAgents}
            icon="agent"
            trend={2}
          />
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <StatsCard
            title="Completion Rate"
            value={`${stats.completionRate.toFixed(1)}%`}
            icon="rate"
            trend={15}
          />
        </Col>
      </Row>

      <Row gutter={[16, 16]} className="mt-6">
        <Col xs={24} lg={16}>
          <Card title="Project Progress" className="h-full">
            <ProjectProgress />
          </Card>
        </Col>
        <Col xs={24} lg={8}>
          <Card title="Recent Activities" className="h-full">
            <ActivityTimeline />
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default Dashboard;