// store/index.ts
import { configureStore } from '@reduxjs/toolkit';
import { persistStore, persistReducer } from 'redux-persist';
import storage from 'redux-persist/lib/storage';
import authReducer from './slices/authSlice';
import appReducer from './slices/appSlice';

const persistConfig = {
  key: 'root',
  storage,
  whitelist: ['auth', 'app'],
};

const persistedAuthReducer = persistReducer(persistConfig, authReducer);
const persistedAppReducer = persistReducer(persistConfig, appReducer);

export const store = configureStore({
  reducer: {
    auth: persistedAuthReducer,
    app: persistedAppReducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware({
      serializableCheck: {
        ignoredActions: ['persist/PERSIST'],
      },
    }),
});

export const persistor = persistStore(store);

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;

export default store;