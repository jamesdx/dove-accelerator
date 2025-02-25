// ProjectPhases.tsx
import React, { useState, useEffect } from 'react';
import { Table, Button, Progress, Modal, Space, Tag, message } from 'antd';
import { PlusOutlined } from '@ant-design/icons';
import { projectApi } from '../../../services/api';
import PhaseForm from './PhaseForm';

interface Phase {
  id: string;
  name: string;
  description: string;
  status: 'PLANNED' | 'IN_PROGRESS' | 'COMPLETED';
  startDate: string;
  endDate: string;
  progress: number;
  milestones: string[];
  dependencies: string[];
}

interface ProjectPhasesProps {
  projectId: string;
}

const ProjectPhases: React.FC<ProjectPhasesProps> = ({ projectId }) => {
  const [phases, setPhases] = useState<Phase[]>([]);
  const [loading, setLoading] = useState(false);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [editingPhase, setEditingPhase] = useState<Phase | null>(null);

  useEffect(() => {
    fetchPhases();
  }, [projectId]);

  const fetchPhases = async () => {
    setLoading(true);
    try {
      const response = await projectApi.getProjectPhases(projectId);
      setPhases(response.data);
    } catch (error) {
      message.error('Failed to fetch project phases');
    } finally {
      setLoading(false);
    }
  };

  const handleAddOrUpdatePhase = async (phase: Phase) => {
    try {
      if (editingPhase) {
        await projectApi.updateProjectPhase(projectId, phase.id, phase);
      } else {
        await projectApi.createProjectPhase(projectId, phase);
      }
      fetchPhases();
      setIsModalVisible(false);
      setEditingPhase(null);
      message.success(`Phase ${editingPhase ? 'updated' : 'created'} successfully`);
    } catch (error) {
      message.error('Failed to save phase');
    }
  };

  const columns = [
    {
      title: 'Phase Name',
      dataIndex: 'name',
      key: 'name',
    },
    {
      title: 'Status',
      dataIndex: 'status',
      key: 'status',
      render: (status: string) => {
        const colors = {
          PLANNED: 'blue',
          IN_PROGRESS: 'green',
          COMPLETED: 'purple',
        };
        return <Tag color={colors[status as keyof typeof colors]}>{status}</Tag>;
      },
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
      title: 'Timeline',
      key: 'timeline',
      render: (_: any, record: Phase) => (
        <span>{record.startDate} ~ {record.endDate}</span>
      ),
    },
    {
      title: 'Milestones',
      dataIndex: 'milestones',
      key: 'milestones',
      render: (milestones: string[]) => (
        <Space direction="vertical">
          {milestones.map((milestone, index) => (
            <Tag key={index} color="geekblue">{milestone}</Tag>
          ))}
        </Space>
      ),
    },
    {
      title: 'Actions',
      key: 'actions',
      render: (_: any, record: Phase) => (
        <Space>
          <Button type="link" onClick={() => {
            setEditingPhase(record);
            setIsModalVisible(true);
          }}>
            Edit
          </Button>
        </Space>
      ),
    },
  ];

  return (
    <div>
      <div className="mb-4">
        <Button
          type="primary"
          icon={<PlusOutlined />}
          onClick={() => {
            setEditingPhase(null);
            setIsModalVisible(true);
          }}
        >
          Add Phase
        </Button>
      </div>

      <Table
        columns={columns}
        dataSource={phases}
        rowKey="id"
        loading={loading}
      />

      <Modal
        title={editingPhase ? 'Edit Phase' : 'Add Phase'}
        open={isModalVisible}
        onCancel={() => {
          setIsModalVisible(false);
          setEditingPhase(null);
        }}
        footer={null}
        width={720}
      >
        <PhaseForm
          phase={editingPhase}
          phases={phases}
          onSubmit={handleAddOrUpdatePhase}
        />
      </Modal>
    </div>
  );
};

export default ProjectPhases;