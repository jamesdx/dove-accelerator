// types/index.ts
export interface User {
  id: string;
  name: string;
  email: string;
  avatar?: string;
  role: string;
}

export interface Project {
  id: string;
  name: string;
  description: string;
  status: 'PLANNING' | 'IN_PROGRESS' | 'COMPLETED' | 'PAUSED' | 'CANCELLED';
  members: Agent[];
  startDate: string;
  endDate?: string;
  progress: number;
}

export interface Agent {
  id: string;
  name: string;
  modelType: string;
  role: string;
  status: 'ACTIVE' | 'INACTIVE';
  skills: string[];
  experienceYears: number;
}

export interface Task {
  id: string;
  title: string;
  description: string;
  status: 'PENDING' | 'IN_PROGRESS' | 'COMPLETED' | 'FAILED';
  priority: 1 | 2 | 3 | 4 | 5;
  assignee: Agent;
  projectId: string;
  deadline?: string;
  progress: number;
}

export interface KnowledgeItem {
  id: string;
  title: string;
  content: string;
  category: string;
  tags: string[];
  source: string;
  createdAt: string;
  updatedAt: string;
}

export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
}