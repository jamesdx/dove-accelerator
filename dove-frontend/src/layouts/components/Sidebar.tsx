// layouts/components/Sidebar.tsx
import React from 'react';
import { Layout, Menu } from 'antd';
import {
  DashboardOutlined,
  ProjectOutlined,
  TaskOutlined,
  BookOutlined,
  TeamOutlined,
  SettingOutlined,
} from '@ant-design/icons';
import { useNavigate, useLocation } from 'react-router-dom';

const { Sider } = Layout;

interface SidebarProps {
  collapsed: boolean;
}

const Sidebar: React.FC<SidebarProps> = ({ collapsed }) => {
  const navigate = useNavigate();
  const location = useLocation();

  const menuItems = [
    {
      key: '/dashboard',
      icon: <DashboardOutlined />,
      label: '仪表盘',
    },
    {
      key: '/projects',
      icon: <ProjectOutlined />,
      label: '项目管理',
    },
    {
      key: '/tasks',
      icon: <TaskOutlined />,
      label: '任务管理',
    },
    {
      key: '/agents',
      icon: <TeamOutlined />,
      label: 'AI团队',
    },
    {
      key: '/knowledge',
      icon: <BookOutlined />,
      label: '知识库',
    },
    {
      key: '/settings',
      icon: <SettingOutlined />,
      label: '设置',
    },
  ];

  return (
    <Sider 
      theme="light"
      trigger={null} 
      collapsible 
      collapsed={collapsed}
      className="border-r border-gray-200"
    >
      <div className="h-16 flex items-center justify-center border-b border-gray-200">
        <img src="/dove.svg" alt="Logo" className="h-8" />
        {!collapsed && <span className="ml-2 text-lg font-semibold">Dove Accelerator</span>}
      </div>
      <Menu
        mode="inline"
        selectedKeys={[location.pathname]}
        items={menuItems}
        onClick={({ key }) => navigate(key)}
      />
    </Sider>
  );
};

export default Sidebar;