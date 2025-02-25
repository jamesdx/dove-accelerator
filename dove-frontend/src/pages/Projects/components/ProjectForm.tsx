// Projects/components/ProjectForm.tsx
import React from 'react';
import { Form, Input, Select, DatePicker, Button } from 'antd';
import { Project } from '../../../types';
import { projectApi } from '../../../services/api';

const { TextArea } = Input;
const { RangePicker } = DatePicker;

interface ProjectFormProps {
  project?: Project;
  onSuccess: () => void;
}

const ProjectForm: React.FC<ProjectFormProps> = ({ project, onSuccess }) => {
  const [form] = Form.useForm();

  const onFinish = async (values: any) => {
    try {
      if (project?.id) {
        await projectApi.updateProject(project.id, values);
      } else {
        await projectApi.createProject(values);
      }
      onSuccess();
    } catch (error) {
      console.error('Failed to save project:', error);
    }
  };

  return (
    <Form
      form={form}
      layout="vertical"
      onFinish={onFinish}
      initialValues={project}
    >
      <Form.Item
        name="name"
        label="Project Name"
        rules={[{ required: true, message: 'Please enter project name' }]}
      >
        <Input />
      </Form.Item>

      <Form.Item
        name="description"
        label="Description"
        rules={[{ required: true, message: 'Please enter project description' }]}
      >
        <TextArea rows={4} />
      </Form.Item>

      <Form.Item
        name="status"
        label="Status"
        rules={[{ required: true, message: 'Please select project status' }]}
      >
        <Select>
          <Select.Option value="PLANNING">Planning</Select.Option>
          <Select.Option value="IN_PROGRESS">In Progress</Select.Option>
          <Select.Option value="COMPLETED">Completed</Select.Option>
          <Select.Option value="PAUSED">Paused</Select.Option>
          <Select.Option value="CANCELLED">Cancelled</Select.Option>
        </Select>
      </Form.Item>

      <Form.Item
        name="dates"
        label="Project Duration"
        rules={[{ required: true, message: 'Please select project duration' }]}
      >
        <RangePicker style={{ width: '100%' }} />
      </Form.Item>

      <Form.Item className="mb-0">
        <Button type="primary" htmlType="submit" block>
          {project ? 'Update Project' : 'Create Project'}
        </Button>
      </Form.Item>
    </Form>
  );
};

export default ProjectForm;