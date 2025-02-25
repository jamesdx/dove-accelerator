// Projects/ProjectDetail.tsx
import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { Tabs, Card, Button, Descriptions, Progress, Space, message } from 'antd';
import { EditOutlined } from '@ant-design/icons';
import { projectApi } from '../../services/api';
import { Project } from '../../types';
import ProjectMembers from './components/ProjectMembers';
import ProjectPhases from './components/ProjectPhases';
import ProjectForm from './components/ProjectForm';

const { TabPane } = Tabs;

const ProjectDetail: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const [project, setProject] = useState<Project | null>(null);
  const [loading, setLoading] = useState(false);
  const [isEditModalVisible, setIsEditModalVisible] = useState(false);

  useEffect(() => {
    if (id) {
      fetchProjectDetails();
    }
  }, [id]);

  const fetchProjectDetails = async () => {
    setLoading(true);
    try {
      const response = await projectApi.getProjects();
      setProject(response.data);
    } catch (error) {
      message.error('Failed to fetch project details');
    } finally {
      setLoading(false);
    }
  };

  if (!project) {
    return <div>Loading...</div>;
  }

  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <h2 className="text-2xl font-semibold">{project.name}</h2>
        <Button
          type="primary"
          icon={<EditOutlined />}
          onClick={() => setIsEditModalVisible(true)}
        >
          Edit Project
        </Button>
      </div>

      <Card loading={loading} className="mb-6">
        <Descriptions column={2}>
          <Descriptions.Item label="Status">{project.status}</Descriptions.Item>
          <Descriptions.Item label="Progress">
            <Progress percent={project.progress} size="small" />
          </Descriptions.Item>
          <Descriptions.Item label="Start Date">{project.startDate}</Descriptions.Item>
          <Descriptions.Item label="End Date">{project.endDate || 'Not set'}</Descriptions.Item>
        </Descriptions>
        <div className="mt-4">
          <h4 className="font-medium mb-2">Description</h4>
          <p>{project.description}</p>
        </div>
      </Card>

      <Tabs defaultActiveKey="members">
        <TabPane tab="Team Members" key="members">
          <ProjectMembers projectId={id!} members={project.members} />
        </TabPane>
        <TabPane tab="Project Phases" key="phases">
          <ProjectPhases projectId={id!} />
        </TabPane>
        <TabPane tab="Tasks" key="tasks">
          {/* Task list component will be added here */}
        </TabPane>
      </Tabs>

      <Modal
        title="Edit Project"
        open={isEditModalVisible}
        onCancel={() => setIsEditModalVisible(false)}
        footer={null}
        width={720}
      >
        <ProjectForm
          project={project}
          onSuccess={() => {
            setIsEditModalVisible(false);
            fetchProjectDetails();
          }}
        />
      </Modal>
    </div>
  );
};

export default ProjectDetail;