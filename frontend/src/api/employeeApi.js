import apiClient from './client';

export async function searchEmployees(params, signal) {
  const { data } = await apiClient.get('/employees', { params, signal });
  return data;
}

export async function exportEmployeesCsv(params) {
  const { data } = await apiClient.get('/employees/export', {
    params,
    responseType: 'blob',
  });
  return data;
}

export async function getEmployee(id) {
  const { data } = await apiClient.get(`/employees/${id}`);
  return data;
}

export async function createEmployee(payload) {
  const { data } = await apiClient.post('/employees', payload);
  return data;
}

export async function updateEmployee(id, payload) {
  const { data } = await apiClient.put(`/employees/${id}`, payload);
  return data;
}

export async function deleteEmployee(id) {
  await apiClient.delete(`/employees/${id}`);
}

export async function getDashboard() {
  const { data } = await apiClient.get('/analytics/dashboard');
  return data;
}
