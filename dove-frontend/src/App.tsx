// App.tsx
import React from 'react';
import { useRoutes } from 'react-router-dom';
import routes from './routes';
import { ConfigProvider } from 'antd';
import zhCN from 'antd/locale/zh_CN';
import { useSelector } from 'react-redux';
import { RootState } from './store';

const App: React.FC = () => {
  const locale = useSelector((state: RootState) => state.app.locale);
  const element = useRoutes(routes);

  return (
    <ConfigProvider
      locale={locale === 'zh_CN' ? zhCN : undefined}
      theme={{
        token: {
          colorPrimary: '#0052CC',
          borderRadius: 3,
        },
      }}
    >
      {element}
    </ConfigProvider>
  );
};

export default App;