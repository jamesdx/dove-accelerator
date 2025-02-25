// dove-frontend/src/pages/Tasks/components/TaskFilter.tsx
import React, { useState } from 'react';
import { Form, Row, Col, Select, DatePicker, Button, Space } from 'antd';
import { ReloadOutlined } from '@ant-design/icons';
import type { Moment } from 'moment';

const { RangePicker } = DatePicker;
const { Option } = Select;

interface TaskFilterProps {
  onFilterChange: (filters: any) => void;
}

const TaskFilter: React.FC<TaskFilterProps> = ({ onFilterChange }) => {
  const [form] = Form.useForm();

  const handleFilterChange = () => {
    const values = form.getFieldsValue();
    const filters = {
      ...values,
      dateRange: values.dateRange ? {
        startDate: values.dateRange[0].format('YYYY-MM-DD'),
        endDate: values.dateRange[1].format('YYYY-MM-DD'),
      } : undefined,
    };
    onFilterChange(filters);
  };

  const handleReset = () => {
    form.resetFields();
    onFilterChange({});
  };

  return (
    <Form
      form={form}
      className="mb-6"
      onValuesChange={handleFilterChange}
    >
      <Row gutter={16}>
        <Col xs={24} sm={12} md={6} lg={4}>
          <Form.Item name="status" label="Status">
            <Select placeholder="Select Status" allowClear>
              <Option value="PENDING">Pending</Option>
              <Option value="IN_PROGRESS">In Progress</Option>
              <Option value="COMPLETED">Completed</Option>
              <Option value="FAILED">Failed</Option>
            </Select>
          </Form.Item>
        </Col>

        <Col xs={24} sm={12} md={6} lg={4}>
          <Form.Item name="priority" label="Priority">
            <Select placeholder="Select Priority" allowClear>
              <Option value={1}>P1 - Highest</Option>
              <Option value={2}>P2 - High</Option>
              <Option value={3}>P3 - Medium</Option>
              <Option value={4}>P4 - Low</Option>
              <Option value={5}>P5 - Lowest</Option>
            </Select>
          </Form.Item>
        </Col>

        <Col xs={24} sm={12} md={6} lg={4}>
          <Form.Item name="assigneeId" label="Assignee">
            <Select placeholder="Select Assignee" allowClear>
              {/* Agent options will be populated dynamically */}
            </Select>
          </Form.Item>
        </Col>

        <Col xs={24} sm={12} md={6} lg={8}>
          <Form.Item name="dateRange" label="Date Range">
            <RangePicker style={{ width: '100%' }} />
          </Form.Item>
        </Col>

        <Col xs={24} sm={12} md={6} lg={4}>
          <Form.Item label=" " colon={false}>
            <Space>
              <Button
                icon={<ReloadOutlined />}
                onClick={handleReset}
              >
                Reset
              </Button>
            </Space>
          </Form.Item>
        </Col>
      </Row>
    </Form>
  );
};

export default TaskFilter;