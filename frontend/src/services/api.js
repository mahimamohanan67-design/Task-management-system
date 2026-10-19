import axios from 'axios';

export const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080/api',
  headers: { 'Content-Type': 'application/json' },
  timeout: 12000,
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('morrow-token');
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

api.interceptors.response.use((response) => response, (error) => {
  if (error.response?.status === 401 && !String(error.config?.url || '').includes('/auth/login')) {
    localStorage.removeItem('morrow-token');
    window.dispatchEvent(new Event('morrow:session-expired'));
  }
  const message = error.response?.data?.error || error.response?.data?.message ||
    (error.code === 'ECONNABORTED' ? 'Morrow took too long to answer.' :
      error.response ? `Something went wrong (${error.response.status}).` :
      'Could not connect to the Morrow server. Is the API running?');
  return Promise.reject(new Error(message));
});

export const fromApiTask = (task) => ({
  ...task,
  due: task.dueDate || '',
  status: ({ TODO: 'To Do', IN_PROGRESS: 'In Progress', COMPLETED: 'Completed' })[task.status] || task.status || 'To Do',
  priority: (task.priority || 'MEDIUM').toLowerCase().replace(/^./, c => c.toUpperCase()),
  tags: task.tags || [],
  timeOfDay: task.timeOfDay || 'Whenever',
  createdAt: task.createdAt?.slice(0, 10) || '',
  updatedAt: task.updatedAt,
  startDate: task.startDate || '',
});

export const toApiTask = (task) => ({
  title: task.title,
  description: task.description || '',
  category: task.category || 'Personal',
  priority: (task.priority || 'Medium').toUpperCase(),
  status: ({ 'To Do': 'TODO', 'In Progress': 'IN_PROGRESS', Completed: 'COMPLETED' })[task.status] || 'TODO',
  dueDate: task.due || null,
  startDate: task.startDate || null,
  timeOfDay: task.timeOfDay || 'Whenever',
  tags: task.tags || [],
});

export const authApi = {
  login: (credentials) => api.post('/auth/login', credentials).then(r => r.data),
  register: (credentials) => api.post('/auth/register', credentials).then(r => r.data),
  me: () => api.get('/auth/me').then(r => r.data),
};

export const taskApi = {
  list: (params = {}) => api.get('/tasks', { params: { page: 0, size: 100, ...params } }).then(r => r.data.content || r.data),
  page: (params = {}) => api.get('/tasks', { params }).then(r => r.data),
  get: (id) => api.get(`/tasks/${id}`).then(r => r.data),
  create: (task) => api.post('/tasks', task).then(r => r.data),
  update: (id, task) => api.put(`/tasks/${id}`, task).then(r => r.data),
  status: (id, status) => api.patch(`/tasks/${id}/status`, { status: ({ 'To Do': 'TODO', 'In Progress': 'IN_PROGRESS', Completed: 'COMPLETED' })[status] || status }).then(r => r.data),
  remove: (id) => api.delete(`/tasks/${id}`),
};

export const categoryApi = {
  list: () => api.get('/categories').then(r => r.data),
  create: (category) => api.post('/categories', category).then(r => r.data),
  update: (id, category) => api.put(`/categories/${id}`, category).then(r => r.data),
  remove: (id) => api.delete(`/categories/${id}`),
};

export const dashboardApi = {
  stats: (timezone = Intl.DateTimeFormat().resolvedOptions().timeZone) => api.get('/dashboard/stats', { params: { timezone } }).then(r => r.data),
  today: (timezone = Intl.DateTimeFormat().resolvedOptions().timeZone) => api.get('/dashboard/today', { params: { timezone } }).then(r => r.data),
  upcoming: (days = 14, timezone = Intl.DateTimeFormat().resolvedOptions().timeZone) => api.get('/dashboard/upcoming', { params: { days, timezone } }).then(r => r.data),
  recentActivity: () => api.get('/dashboard/recent-activity').then(r => r.data),
};

export const notificationApi = {
  list: () => api.get('/notifications').then(r => r.data),
  read: (id) => api.patch(`/notifications/${id}/read`).then(r => r.data),
  readAll: () => api.patch('/notifications/read-all').then(r => r.data),
};

export const apiClient = {
  auth: authApi,
  tasks: taskApi,
  categories: categoryApi,
  dashboard: dashboardApi,
  notifications: notificationApi,
};
