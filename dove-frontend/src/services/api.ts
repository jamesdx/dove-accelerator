// services/api.ts
import axios from 'axios';
import { setupInterceptors } from './interceptors';
import { ApiResponse } from '../types';

const api = axios.create({
  baseURL: '/api/v1',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

setupInterceptors(api);

export const authApi = {
  login: (username: string, password: string) =>
    api.post<ApiResponse<{ token: string; refreshToken: string }>>('/auth/login', {
      username,
      password,
    }),
  register: (data: { username: string; password: string; email: string }) =>
    api.post<ApiResponse<{ token: string }>>('/auth/register', data),
  refreshToken: (refreshToken: string) =>
    api.post<ApiResponse<{ token: string }>>('/auth/refresh', { refreshToken }),
  logout: () => api.post('/auth/logout'),
};

export const projectApi = {
  getProjects: () => api.get<ApiResponse<any>>('/projects'),
  createProject: (data: any) => api.post<ApiResponse<any>>('/projects', data),
  updateProject: (id: string, data: any) =>
    api.put<ApiResponse<any>>(`/projects/${id}`, data),
  deleteProject: (id: string) => api.delete(`/projects/${id}`),
};

export const taskApi = {
  getTasks: () => api.get<ApiResponse<any>>('/tasks'),
  createTask: (data: any) => api.post<ApiResponse<any>>('/tasks', data),
  updateTask: (id: string, data: any) =>
    api.put<ApiResponse<any>>(`/tasks/${id}`, data),
  deleteTask: (id: string) => api.delete(`/tasks/${id}`),
};

export const agentApi = {
  getAgents: () => api.get<ApiResponse<any>>('/agents'),
  createAgent: (data: any) => api.post<ApiResponse<any>>('/agents', data),
  updateAgent: (id: string, data: any) =>
    api.put<ApiResponse<any>>(`/agents/${id}`, data),
  deleteAgent: (id: string) => api.delete(`/agents/${id}`),
};

export const knowledgeApi = {
  getKnowledgeList: () => api.get<ApiResponse<any>>('/knowledge'),
  createKnowledge: (data: any) => api.post<ApiResponse<any>>('/knowledge', data),
  updateKnowledge: (id: string, data: any) =>
    api.put<ApiResponse<any>>(`/knowledge/${id}`, data),
  deleteKnowledge: (id: string) => api.delete(`/knowledge/${id}`),
  searchKnowledge: (query: string) =>
    api.get<ApiResponse<any>>(`/knowledge/search?keyword=${query}`),
};

export default api;