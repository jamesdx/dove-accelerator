// services/interceptors.ts
import { AxiosInstance } from 'axios';
import { store } from '../store';
import { logout } from '../store/slices/authSlice';
import { message } from 'antd';

export const setupInterceptors = (api: AxiosInstance) => {
  api.interceptors.request.use(
    (config) => {
      const token = store.getState().auth.token;
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
      return config;
    },
    (error) => {
      return Promise.reject(error);
    }
  );

  api.interceptors.response.use(
    (response) => {
      if (response.data.code !== 200) {
        message.error(response.data.message || '请求失败');
        return Promise.reject(new Error(response.data.message));
      }
      return response.data;
    },
    (error) => {
      if (error.response) {
        switch (error.response.status) {
          case 401:
            store.dispatch(logout());
            message.error('登录已过期，请重新登录');
            break;
          case 403:
            message.error('没有权限访问');
            break;
          case 404:
            message.error('请求的资源不存在');
            break;
          case 500:
            message.error('服务器错误');
            break;
          default:
            message.error(error.response.data?.message || '请求失败');
        }
      } else {
        message.error('网络错误');
      }
      return Promise.reject(error);
    }
  );
};