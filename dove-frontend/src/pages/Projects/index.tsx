// Projects/index.tsx
import React, { useState, useEffect } from 'react';
import { Card, Button, Row, Col, Badge, Modal, Space, Input, Select } from 'antd';
import { PlusOutlined, SearchOutlined } from '@ant-design/icons';
import { projectApi } from '../../services/api';
import ProjectForm from './components/ProjectForm';
import { Project } from '../../types';

const { Search } = Input;
const { Option } = Select;

const Projects: React.FC = () => {
  const [projects, setProjects] = useState<Project[]>([]);
  const [loading, setLoading] = useState(false);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [searchText, setSearchText] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');

  useEffect(() => {
    fetchProjects();
  }, []);

  const fetchProjects = async () => {
    setLoading(true);
    try {
      const response = await projectApi.getProjects();
      setProjects(response.data);
    } catch (error) {
      console.error('Failed to fetch projects:', error);
    } finally {
      setLoading(false);
    }
  };

  const getStatusColor = (status: string) => {
    const colors: Record<string, string> = {
      'PLANNING': 'blue',
      'IN_PROGRESS': 'green',
      'COMPLETED': 'purple',
      'PAUSED': 'orange',
      'CANCELLED': 'red',
    };
    return colors[status] || 'default';
  };

  const filteredProjects = projects.filter(project => {
    const matchesSearch = project.name.toLowerCase().includes(searchText.toLowerCase()) ||
                         project.description.toLowerCase().includes(searchText.toLowerCase());
    const matchesStatus = statusFilter === 'ALL' || project.status === statusFilter;
    return matchesSearch && matchesStatus;
  });

  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <h2 className="text-2xl font-semibold">Projects</h2>
        <Button type="primary" icon={<PlusOutlined />} onClick={() => setIsModalVisible(true)}>
          New Project
        </Button>
      </div>

      <div className="mb-6">
        <Row gutter={16}>
          <Col span={12}>
            <Search
              placeholder="Search projects"
              onChange={e => setSearchText(e.target.value)}
              allowClear
            />
          </Col>
          <Col span={6}>
            <Select
              defaultValue="ALL"
              style={{ width: '100%' }}
              onChange={value => setStatusFilter(value)}
            >
              <Option value="ALL">All Status</Option>
              <Option value="PLANNING">Planning</Option>
              <Option value="IN_PROGRESS">In Progress</Option>
              <Option value="COMPLETED">Completed</Option>
              <Option value="PAUSED">Paused</Option>
              <Option value="CANCELLED">Cancelled</Option>
            </Select>
          </Col>
        </Row>
      </div>

      <Row gutter={[16, 16]}>
        {filteredProjects.map(project => (
          <Col xs={24} sm={12} lg={8} key={project.id}>
            <Card
              hoverable
              loading={loading}
              className="h-full"
              actions={[
                <Button type="link" onClick={() => window.location.href = `/projects/${project.id}`}>
                  View Details
                </Button>
              ]}
            >
              <Card.Meta
                title={
                  <Space>
                    {project.name}
                    <Badge status={getStatusColor(project.status) as any} text={project.status} />
                  </Space>
                }
                description={project.description}
              />
              <div className="mt-4">
                <p className="mb-2">Progress: {project.progress}%</p>
                <p className="text-secondary">Members: {project.members?.length || 0}</p>
              </div>
            </Card>
          </Col>
        ))}
      </Row>

      <Modal
        title="Create New Project"
        open={isModalVisible}
        onCancel={() => setIsModalVisible(false)}
        footer={null}
        width={720}
      >
        <ProjectForm
          onSuccess={() => {
            setIsModalVisible(false);
            fetchProjects();
          }}
        />
      </Modal>
    </div>
  );
};

export default Projects;