// dove-frontend/src/pages/Tasks/TaskDetail.tsx
import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { Card, Descriptions, Progress, Timeline, Space, Tag, Button, message } from 'antd';
import { EditOutlined } from '@ant-design/icons';
import { taskApi } from '../../services/api';
import { Task } from '../../types';
import TaskForm from './components/TaskForm';

const TaskDetail: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const [task, setTask] = useState<Task | null>(null);
  const [loading, setLoading] = useState(false);
  const [isEditModalVisible, setIsEditModalVisible] = useState(false);

  useEffect(() => {
    if (id) {
      fetchTaskDetails();
    }
  }, [id]);

  const fetchTaskDetails = async () => {
    setLoading(true);
    try {
      const response = await taskApi.getTask(id!);
      setTask(response.data);
    } catch (error) {
      message.error('Failed to fetch task details');
    } finally {
      setLoading(false);
    }
  };

  if (!task) return <div>Loading...</div>;

  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <h2 className="text-2xl font-semibold">{task.title}</h2>
        <Button
          type="primary"
          icon={<EditOutlined />}
          onClick={() => setIsEditModalVisible(true)}
        >
          Edit Task
        </Button>
      </div>

      <Card loading={loading}>
        <Descriptions column={2}>
          <Descriptions.Item label="Status">
            <Tag color={task.status === 'COMPLETED' ? 'green' : 'blue'}>
              {task.status}
            </Tag>
          </Descriptions.Item>
          <Descriptions.Item label="Priority">
            <Tag color="red">P{task.priority}</Tag>
          </Descriptions.Item>
          <Descriptions.Item label="Assignee">
            {task.assignee.name}
          </Descriptions.Item>
          <Descriptions.Item label="Project">
            {task.projectId}
          </Descriptions.Item>
          <Descriptions.Item label="Progress" span={2}>
            <Progress percent={task.progress} />
          </Descriptions.Item>
        </Descriptions>

        <div className="mt-6">
          <h3 className="text-lg font-medium mb-2">Description</h3>
          <p>{task.description}</p>
        </div>

        <div className="mt-6">
          <h3 className="text-lg font-medium mb-2">Dependencies</h3>
          <Space wrap>
            {task.dependencies?.map(dep => (
              <Tag key={dep.id}>{dep.title}</Tag>
            ))}
          </Space>
        </div>

        <div className="mt-6">
          <h3 className="text-lg font-medium mb-2">Timeline</h3>
          <Timeline>
            {task.timeline?.map(item => (
              <Timeline.Item key={item.id}>
                <p>{item.action}</p>
                <p className="text-secondary">{item.timestamp}</p>
              </Timeline.Item>
            ))}
          </Timeline>
        </div>
      </Card>

      <Modal
        title="Edit Task"
        open={isEditModalVisible}
        onCancel={() => setIsEditModalVisible(false)}
        footer={null}
        width={720}
      >
        <TaskForm
          task={task}
          onSuccess={() => {
            setIsEditModalVisible(false);
            fetchTaskDetails();
          }}
        />
      </Modal>
    </div>
  );
};

export default TaskDetail;