/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        usana: {
          50: '#f0f7ff',
          100: '#e0effe',
          200: '#bae0fd',
          300: '#7cc7fb',
          400: '#38aaf7',
          500: '#0e8fe4',
          600: '#0270c2',
          700: '#03589c',
          800: '#074b81',
          900: '#0c3f6b',
          950: '#082847',
        },
        cellular: {
          emerald: '#10b981',
          teal: '#14b8a6',
          cyan: '#06b6d4',
          purple: '#8b5cf6',
          rose: '#f43f5e',
          amber: '#f59e0b'
        }
      },
      fontFamily: {
        sans: [
          'Pretendard',
          '-apple-system',
          'BlinkMacSystemFont',
          'Segoe UI',
          'Roboto',
          'Helvetica Neue',
          'Arial',
          'sans-serif'
        ]
      }
    },
  },
  plugins: [],
}
