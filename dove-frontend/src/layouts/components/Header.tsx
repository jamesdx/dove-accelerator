// layouts/components/Header.tsx
import React from 'react';
import { Layout, Button, Space, Dropdown, Avatar } from 'antd';
import {
  MenuFoldOutlined,
  MenuUnfoldOutlined,
  BellOutlined,
  UserOutlined,
} from '@ant-design/icons';
import type { MenuProps } from 'antd';
import { useSelector } from 'react-redux';
import { RootState } from '../../store';

const { Header: AntHeader } = Layout;

interface HeaderProps {
  collapsed: boolean;
  onCollapse: () => void;
}

const Header: React.FC<HeaderProps> = ({ collapsed, onCollapse }) => {
  const user = useSelector((state: RootState) => state.auth.user);

  const userMenuItems: MenuProps['items'] = [
    {
      key: 'profile',
      label: '个人信息',
    },
    {
      key: 'logout',
      label: '退出登录',
    },
  ];

  return (
    <AntHeader className="bg-white px-6 flex justify-between items-center border-b border-gray-200">
      <Button
        type="text"
        icon={collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
        onClick={onCollapse}
      />
      <Space size={16}>
        <Button type="text" icon={<BellOutlined />} />
        <Dropdown menu={{ items: userMenuItems }} trigger={['click']}>
          <Space className="cursor-pointer">
            <Avatar icon={<UserOutlined />} />
            <span>{user?.name}</span>
          </Space>
        </Dropdown>
      </Space>
    </AntHeader>
  );
};

export default Header;