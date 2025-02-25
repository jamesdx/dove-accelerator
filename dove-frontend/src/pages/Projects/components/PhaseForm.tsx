// PhaseForm.tsx
import React from 'react';
import { Form, Input, Select, DatePicker, Button, Space } from 'antd';
import type { Moment } from 'moment';
import { MinusCircleOutlined, PlusOutlined } from '@ant-design/icons';

const { TextArea } = Input;
const { RangePicker } = DatePicker;

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

interface PhaseFormProps {
  phase?: Phase | null;
  phases: Phase[];
  onSubmit: (values: Phase) => void;
}

const PhaseForm: React.FC<PhaseFormProps> = ({ phase, phases, onSubmit }) => {
  const [form] = Form.useForm();

  const handleSubmit = (values: any) => {
    const dates = values.dates as [Moment, Moment];
    const formattedValues = {
      ...values,
      startDate: dates[0].format('YYYY-MM-DD'),
      endDate: dates[1].format('YYYY-MM-DD'),
      progress: phase?.progress || 0,
    };
    delete formattedValues.dates;
    onSubmit(formattedValues);
  };

  return (
    <Form
      form={form}
      layout="vertical"
      onFinish={handleSubmit}
      initialValues={{
        ...phase,
        dates: phase ? [moment(phase.startDate), moment(phase.endDate)] : undefined,
        milestones: phase?.milestones || [''],
        dependencies: phase?.dependencies || [],
      }}
    >
      <Form.Item
        name="name"
        label="Phase Name"
        rules={[{ required: true, message: 'Please enter phase name' }]}
      >
        <Input />
      </Form.Item>

      <Form.Item
        name="description"
        label="Description"
        rules={[{ required: true, message: 'Please enter phase description' }]}
      >
        <TextArea rows={4} />
      </Form.Item>

      <Form.Item
        name="status"
        label="Status"
        rules={[{ required: true, message: 'Please select phase status' }]}
      >
        <Select>
          <Select.Option value="PLANNED">Planned</Select.Option>
          <Select.Option value="IN_PROGRESS">In Progress</Select.Option>
          <Select.Option value="COMPLETED">Completed</Select.Option>
        </Select>
      </Form.Item>

      <Form.Item
        name="dates"
        label="Phase Duration"
        rules={[{ required: true, message: 'Please select phase duration' }]}
      >
        <RangePicker style={{ width: '100%' }} />
      </Form.Item>

      <Form.List name="milestones">
        {(fields, { add, remove }) => (
          <>
            {fields.map((field, index) => (
              <Form.Item
                required={false}
                label={index === 0 ? 'Milestones' : ''}
                key={field.key}
              >
                <Form.Item
                  {...field}
                  validateTrigger={['onChange', 'onBlur']}
                  rules={[
                    {
                      required: true,
                      whitespace: true,
                      message: 'Please input milestone or delete this field',
                    },
                  ]}
                  noStyle
                >
                  <Input style={{ width: '90%' }} placeholder="Enter milestone" />
                </Form.Item>
                {fields.length > 1 && (
                  <MinusCircleOutlined
                    className="ml-2"
                    onClick={() => remove(field.name)}
                  />
                )}
              </Form.Item>
            ))}
            <Form.Item>
              <Button
                type="dashed"
                onClick={() => add()}
                block
                icon={<PlusOutlined />}
              >
                Add Milestone
              </Button>
            </Form.Item>
          </>
        )}
      </Form.List>

      <Form.Item
        name="dependencies"
        label="Dependencies"
      >
        <Select mode="multiple" placeholder="Select dependent phases">
          {phases
            .filter(p => p.id !== phase?.id)
            .map(p => (
              <Select.Option key={p.id} value={p.id}>
                {p.name}
              </Select.Option>
            ))}
        </Select>
      </Form.Item>

      <Form.Item>
        <Button type="primary" htmlType="submit" block>
          {phase ? 'Update Phase' : 'Create Phase'}
        </Button>
      </Form.Item>
    </Form>
  );
};

export default PhaseForm;