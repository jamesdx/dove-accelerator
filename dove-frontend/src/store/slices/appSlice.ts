// store/slices/appSlice.ts
import { createSlice, PayloadAction } from '@reduxjs/toolkit';

interface AppState {
  locale: string;
  theme: 'light' | 'dark';
  sidebarCollapsed: boolean;
  loading: boolean;
}

const initialState: AppState = {
  locale: 'zh_CN',
  theme: 'light',
  sidebarCollapsed: false,
  loading: false,
};

const appSlice = createSlice({
  name: 'app',
  initialState,
  reducers: {
    setLocale: (state, action: PayloadAction<string>) => {
      state.locale = action.payload;
    },
    setTheme: (state, action: PayloadAction<'light' | 'dark'>) => {
      state.theme = action.payload;
    },
    setSidebarCollapsed: (state, action: PayloadAction<boolean>) => {
      state.sidebarCollapsed = action.payload;
    },
    setLoading: (state, action: PayloadAction<boolean>) => {
      state.loading = action.payload;
    },
  },
});

export const { setLocale, setTheme, setSidebarCollapsed, setLoading } = appSlice.actions;
export default appSlice.reducer;