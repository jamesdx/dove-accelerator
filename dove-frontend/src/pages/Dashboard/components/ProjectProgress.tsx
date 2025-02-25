// Dashboard/components/ProjectProgress.tsx
import React from 'react';
import { Progress, Table } from 'antd';
import type { ColumnsType } from 'antd/es/table';

interface ProjectData {
  id: string;
  name: string;
  progress: number;
  status: string;
  dueDate: string;
}

const ProjectProgress: React.FC = () => {
  const columns: ColumnsType<ProjectData> = [
    {
      title: 'Project Name',
      dataIndex: 'name',
      key: 'name',
    },
    {
      title: 'Progress',
      dataIndex: 'progress',
      key: 'progress',
      render: (progress: number) => (
        <Progress percent={progress} size="small" />
      ),
    },
    {
      title: 'Status',
      dataIndex: 'status',
      key: 'status',
      render: (status: string) => {
        const colors: Record<string, string> = {
          'IN_PROGRESS': 'text-primary',
          'COMPLETED': 'text-success',
          'PAUSED': 'text-warning',
        };
        return <span className={colors[status]}>{status}</span>;
      },
    },
    {
      title: 'Due Date',
      dataIndex: 'dueDate',
      key: 'dueDate',
    },
  ];

  const data: ProjectData[] = [
    {
      id: '1',
      name: 'E-commerce AI Assistant',
      progress: 75,
      status: 'IN_PROGRESS',
      dueDate: '2024-02-28',
    },
    {
      id: '2',
      name: 'Customer Service Bot',
      progress: 100,
      status: 'COMPLETED',
      dueDate: '2024-02-15',
    },
    {
      id: '3',
      name: 'Data Analysis Pipeline',
      progress: 30,
      status: 'IN_PROGRESS',
      dueDate: '2024-03-15',
    },
    {
      id: '4',
      name: 'Recommendation Engine',
      progress: 0,
      status: 'PAUSED',
      dueDate: '2024-04-01',
    },
  ];

  return (
    <Table
      columns={columns}
      dataSource={data}
      pagination={false}
      size="middle"
      rowKey="id"
    />
  );
};

export default ProjectProgress;