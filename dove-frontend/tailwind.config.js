/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        primary: {
          DEFAULT: '#0052CC', // JIRA Blue
          light: '#4C9AFF',
          dark: '#0747A6',
        },
        secondary: {
          DEFAULT: '#6B778C', // JIRA Gray
          light: '#8993A4',
          dark: '#505F79',
        },
        success: {
          DEFAULT: '#36B37E', // JIRA Green
          light: '#57D9A3',
          dark: '#006644',
        },
        warning: {
          DEFAULT: '#FFAB00', // JIRA Yellow
          light: '#FFE380',
          dark: '#FF8B00',
        },
        danger: {
          DEFAULT: '#FF5630', // JIRA Red
          light: '#FF7452',
          dark: '#DE350B',
        },
        background: {
          DEFAULT: '#FFFFFF',
          secondary: '#F4F5F7',
          tertiary: '#EBECF0',
        }
      },
      boxShadow: {
        card: '0 1px 1px rgba(9, 30, 66, 0.25)',
        dropdown: '0 4px 8px -2px rgba(9, 30, 66, 0.25)',
        modal: '0 10px 20px rgba(9, 30, 66, 0.25)',
      },
      fontFamily: {
        sans: ['-apple-system', 'BlinkMacSystemFont', 'Segoe UI', 'Roboto', 'Oxygen', 'Ubuntu', 'Cantarell', 'Fira Sans', 'Droid Sans', 'Helvetica Neue', 'sans-serif'],
      },
    },
  },
  plugins: [],
  corePlugins: {
    preflight: true,
  },
}