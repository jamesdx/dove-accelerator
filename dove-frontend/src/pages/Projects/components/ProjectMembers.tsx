// Projects/components/ProjectMembers.tsx
import React, { useState } from 'react';
import { Table, Button, Avatar, Modal, Select, Space, Tag } from 'antd';
import { UserAddOutlined } from '@ant-design/icons';
import { Agent } from '../../../types';
import { agentApi } from '../../../services/api';

interface ProjectMembersProps {
  projectId: string;
  members: Agent[];
}

const ProjectMembers: React.FC<ProjectMembersProps> = ({ projectId, members }) => {
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [availableAgents, setAvailableAgents] = useState<Agent[]>([]);
  const [selectedAgent, setSelectedAgent] = useState<string>();

  const columns = [
    {
      title: 'Agent',
      dataIndex: 'name',
      key: 'name',
      render: (text: string, record: Agent) => (
        <Space>
          <Avatar>{text[0]}</Avatar>
          <span>{text}</span>
        </Space>
      ),
    },
    {
      title: 'Role',
      dataIndex: 'role',
      key: 'role',
    },
    {
      title: 'Model Type',
      dataIndex: 'modelType',
      key: 'modelType',
    },
    {
      title: 'Skills',
      dataIndex: 'skills',
      key: 'skills',
      render: (skills: string[]) => (
        <>
          {skills.map(skill => (
            <Tag key={skill} color="blue">
              {skill}
            </Tag>
          ))}
        </>
      ),
    },
    {
      title: 'Experience',
      dataIndex: 'experienceYears',
      key: 'experienceYears',
      render: (years: number) => `${years} years`,
    },
    {
      title: 'Status',
      dataIndex: 'status',
      key: 'status',
      render: (status: string) => (
        <Tag color={status === 'ACTIVE' ? 'green' : 'red'}>
          {status}
        </Tag>
      ),
    },
  ];

  const handleAddMember = async () => {
    if (!selectedAgent) return;
    try {
      // Add member API call here
      setIsModalVisible(false);
    } catch (error) {
      console.error('Failed to add member:', error);
    }
  };

  return (
    <div>
      <div className="mb-4">
        <Button
          type="primary"
          icon={<UserAddOutlined />}
          onClick={() => setIsModalVisible(true)}
        >
          Add Team Member
        </Button>
      </div>

      <Table
        columns={columns}
        dataSource={members}
        rowKey="id"
      />

      <Modal
        title="Add Team Member"
        open={isModalVisible}
        onOk={handleAddMember}
        onCancel={() => setIsModalVisible(false)}
      >
        <Select
          style={{ width: '100%' }}
          placeholder="Select an AI Agent"
          onChange={value => setSelectedAgent(value)}
        >
          {availableAgents.map(agent => (
            <Select.Option key={agent.id} value={agent.id}>
              {agent.name} - {agent.role}
            </Select.Option>
          ))}
        </Select>
      </Modal>
    </div>
  );
};

export default ProjectMembers;