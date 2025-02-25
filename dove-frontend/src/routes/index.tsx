// routes/index.tsx
import React from 'react';
import { Navigate } from 'react-router-dom';
import MainLayout from '../layouts/MainLayout';
import { RouteObject } from 'react-router-dom';

const Dashboard = React.lazy(() => import('../pages/Dashboard'));
const Projects = React.lazy(() => import('../pages/Projects'));
const Tasks = React.lazy(() => import('../pages/Tasks'));
const Knowledge = React.lazy(() => import('../pages/Knowledge'));
const Login = React.lazy(() => import('../pages/Login'));

const routes: RouteObject[] = [
  {
    path: '/',
    element: <MainLayout />,
    children: [
      { index: true, element: <Navigate to="/dashboard" replace /> },
      { path: 'dashboard', element: <Dashboard /> },
      { path: 'projects/*', element: <Projects /> },
      { path: 'tasks/*', element: <Tasks /> },
      { path: 'knowledge/*', element: <Knowledge /> },
    ],
  },
  {
    path: '/login',
    element: <Login />,
  },
];

export default routes;