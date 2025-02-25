// dove-frontend/src/pages/Tasks/components/TaskForm.tsx
import React from 'react';
import { Form, Input, Select, DatePicker, Button, InputNumber } from 'antd';
import { taskApi } from '../../../services/api';
import { Task } from '../../../types';

const { TextArea } = Input;

interface TaskFormProps {
  task?: Task;
  onSuccess: () => void;
}

const TaskForm: React.FC<TaskFormProps> = ({ task, onSuccess }) => {
  const [form] = Form.useForm();

  const onFinish = async (values: any) => {
    try {
      if (task?.id) {
        await taskApi.updateTask(task.id, values);
      } else {
        await taskApi.createTask(values);
      }
      onSuccess();
    } catch (error) {
      console.error('Failed to save task:', error);
    }
  };

  return (
    <Form
      form={form}
      layout="vertical"
      onFinish={onFinish}
      initialValues={task}
    >
      <Form.Item
        name="title"
        label="Task Title"
        rules={[{ required: true, message: 'Please enter task title' }]}
      >
        <Input />
      </Form.Item>

      <Form.Item
        name="description"
        label="Description"
        rules={[{ required: true, message: 'Please enter task description' }]}
      >
        <TextArea rows={4} />
      </Form.Item>

      <Form.Item
        name="priority"
        label="Priority"
        rules={[{ required: true, message: 'Please select priority' }]}
      >
        <Select>
          <Select.Option value={1}>P1 - Highest</Select.Option>
          <Select.Option value={2}>P2 - High</Select.Option>
          <Select.Option value={3}>P3 - Medium</Select.Option>
          <Select.Option value={4}>P4 - Low</Select.Option>
          <Select.Option value={5}>P5 - Lowest</Select.Option>
        </Select>
      </Form.Item>

      <Form.Item
        name="assigneeId"
        label="Assignee"
        rules={[{ required: true, message: 'Please select assignee' }]}
      >
        <Select placeholder="Select AI Agent">
          {/* Agent options will be populated dynamically */}
        </Select>
      </Form.Item>

      <Form.Item
        name="status"
        label="Status"
        rules={[{ required: true, message: 'Please select status' }]}
      >
        <Select>
          <Select.Option value="PENDING">Pending</Select.Option>
          <Select.Option value="IN_PROGRESS">In Progress</Select.Option>
          <Select.Option value="COMPLETED">Completed</Select.Option>
          <Select.Option value="FAILED">Failed</Select.Option>
        </Select>
      </Form.Item>

      <Form.Item
        name="progress"
        label="Progress"
        rules={[{ required: true, message: 'Please enter progress' }]}
      >
        <InputNumber min={0} max={100} formatter={value => `${value}%`} />
      </Form.Item>

      <Form.Item
        name="deadline"
        label="Deadline"
        rules={[{ required: true, message: 'Please select deadline' }]}
      >
        <DatePicker showTime style={{ width: '100%' }} />
      </Form.Item>

      <Form.Item
        name="dependencies"
        label="Dependencies"
      >
        <Select mode="multiple" placeholder="Select dependent tasks">
          {/* Task options will be populated dynamically */}
        </Select>
      </Form.Item>

      <Form.Item>
        <Button type="primary" htmlType="submit" block>
          {task ? 'Update Task' : 'Create Task'}
        </Button>
      </Form.Item>
    </Form>
  );
};

export default TaskForm;