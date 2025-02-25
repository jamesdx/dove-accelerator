// dove-frontend/src/pages/Tasks/index.tsx
import React, { useState, useEffect } from 'react';
import { Table, Card, Button, Tabs, Row, Col, message } from 'antd';
import { DragDropContext, Droppable, Draggable } from 'react-beautiful-dnd';
import { PlusOutlined } from '@ant-design/icons';
import { taskApi } from '../../services/api';
import { Task } from '../../types';
import TaskForm from './components/TaskForm';
import TaskFilter from './components/TaskFilter';

const { TabPane } = Tabs;

const Tasks: React.FC = () => {
  const [tasks, setTasks] = useState<Task[]>([]);
  const [loading, setLoading] = useState(false);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [viewMode, setViewMode] = useState<'list' | 'board'>('list');

  useEffect(() => {
    fetchTasks();
  }, []);

  const fetchTasks = async () => {
    setLoading(true);
    try {
      const response = await taskApi.getTasks();
      setTasks(response.data);
    } catch (error) {
      message.error('Failed to fetch tasks');
    } finally {
      setLoading(false);
    }
  };

  const columns = [
    {
      title: 'Title',
      dataIndex: 'title',
      key: 'title',
    },
    {
      title: 'Status',
      dataIndex: 'status',
      key: 'status',
      render: (status: string) => {
        const colors: Record<string, string> = {
          PENDING: 'blue',
          IN_PROGRESS: 'orange',
          COMPLETED: 'green',
          FAILED: 'red',
        };
        return <Tag color={colors[status]}>{status}</Tag>;
      },
    },
    {
      title: 'Priority',
      dataIndex: 'priority',
      key: 'priority',
      render: (priority: number) => {
        const colors = ['red', 'orange', 'yellow', 'blue', 'green'];
        return <Tag color={colors[priority - 1]}>P{priority}</Tag>;
      },
    },
    {
      title: 'Assignee',
      dataIndex: 'assignee',
      key: 'assignee',
      render: (assignee: Agent) => assignee.name,
    },
    {
      title: 'Progress',
      dataIndex: 'progress',
      key: 'progress',
      render: (progress: number) => <Progress percent={progress} size="small" />,
    },
    {
      title: 'Actions',
      key: 'actions',
      render: (_: any, record: Task) => (
        <Space>
          <Button type="link" onClick={() => navigate(`/tasks/${record.id}`)}>
            View
          </Button>
          <Button type="link" onClick={() => handleEdit(record)}>
            Edit
          </Button>
        </Space>
      ),
    },
  ];

  const handleDragEnd = async (result: any) => {
    if (!result.destination) return;

    const taskId = result.draggableId;
    const newStatus = result.destination.droppableId;

    try {
      await taskApi.updateTask(taskId, { status: newStatus });
      fetchTasks();
    } catch (error) {
      message.error('Failed to update task status');
    }
  };

  const renderBoard = () => {
    const statuses = ['PENDING', 'IN_PROGRESS', 'COMPLETED', 'FAILED'];
    
    return (
      <DragDropContext onDragEnd={handleDragEnd}>
        <Row gutter={16}>
          {statuses.map(status => (
            <Col span={6} key={status}>
              <Card title={status.replace('_', ' ')} className="h-full">
                <Droppable droppableId={status}>
                  {(provided) => (
                    <div
                      ref={provided.innerRef}
                      {...provided.droppableProps}
                      className="min-h-[200px]"
                    >
                      {tasks
                        .filter(task => task.status === status)
                        .map((task, index) => (
                          <Draggable
                            key={task.id}
                            draggableId={task.id}
                            index={index}
                          >
                            {(provided) => (
                              <Card
                                ref={provided.innerRef}
                                {...provided.draggableProps}
                                {...provided.dragHandleProps}
                                className="mb-2"
                              >
                                <p className="font-medium">{task.title}</p>
                                <p className="text-sm text-secondary">
                                  Assignee: {task.assignee.name}
                                </p>
                                <Progress percent={task.progress} size="small" />
                              </Card>
                            )}
                          </Draggable>
                        ))}
                      {provided.placeholder}
                    </div>
                  )}
                </Droppable>
              </Card>
            </Col>
          ))}
        </Row>
      </DragDropContext>
    );
  };

  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <h2 className="text-2xl font-semibold">Tasks</h2>
        <Button
          type="primary"
          icon={<PlusOutlined />}
          onClick={() => setIsModalVisible(true)}
        >
          New Task
        </Button>
      </div>

      <TaskFilter onFilterChange={fetchTasks} />

      <Tabs defaultActiveKey="list" onChange={(key) => setViewMode(key as 'list' | 'board')}>
        <TabPane tab="List View" key="list">
          <Table
            columns={columns}
            dataSource={tasks}
            loading={loading}
            rowKey="id"
          />
        </TabPane>
        <TabPane tab="Board View" key="board">
          {renderBoard()}
        </TabPane>
      </Tabs>

      <Modal
        title="New Task"
        open={isModalVisible}
        onCancel={() => setIsModalVisible(false)}
        footer={null}
        width={720}
      >
        <TaskForm
          onSuccess={() => {
            setIsModalVisible(false);
            fetchTasks();
          }}
        />
      </Modal>
    </div>
  );
};

export default Tasks;