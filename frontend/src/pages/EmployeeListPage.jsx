import React, { useEffect, useState } from 'react';
import { Box, Typography, Button, Stack, Alert, CircularProgress } from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import { useNavigate } from 'react-router-dom';
import FilterBar from '../components/FilterBar';
import EmployeeTable from '../components/EmployeeTable';
import { searchEmployees, exportEmployeesCsv, deleteEmployee } from '../api/employeeApi';

const DEFAULT_FILTERS = {
  q: '',
  department: '',
  country: '',
  designation: '',
  employmentType: '',
  minSalary: '',
  maxSalary: '',
};

export default function EmployeeListPage() {
  const navigate = useNavigate();
  const [filters, setFilters] = useState(DEFAULT_FILTERS);
  const [pageIndex, setPageIndex] = useState(0);
  const [page, setPage] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [reloadKey, setReloadKey] = useState(0);
  const [exporting, setExporting] = useState(false);

  useEffect(() => {
    const controller = new AbortController();

    const timer = setTimeout(async () => {
      setLoading(true);
      setError(null);

      try {
        const params = {
          q: filters.q?.trim() || undefined,
          department: filters.department?.trim() || undefined,
          country: filters.country?.trim() || undefined,
          designation: filters.designation?.trim() || undefined,
          employmentType: filters.employmentType || undefined,
          minSalary: filters.minSalary || undefined,
          maxSalary: filters.maxSalary || undefined,
          page: pageIndex,
          size: 25,
        };

        const data = await searchEmployees(params, controller.signal);
        setPage(data);
      } catch (err) {
        if (err?.code !== 'ERR_CANCELED' && err?.name !== 'CanceledError') {
          setError('Could not load employees. Is the backend running?');
        }
      } finally {
        if (!controller.signal.aborted) {
          setLoading(false);
        }
      }
    }, 300);

    return () => {
      clearTimeout(timer);
      controller.abort();
    };
  }, [filters, pageIndex, reloadKey]);

  const handleFilterChange = (next) => {
    setFilters(next);
    setPageIndex(0);
  };

  const handleExport = async () => {
    setExporting(true);
    setError(null);
    try {
      const params = {
        q: filters.q?.trim() || undefined,
        department: filters.department?.trim() || undefined,
        country: filters.country?.trim() || undefined,
        designation: filters.designation?.trim() || undefined,
        employmentType: filters.employmentType || undefined,
        minSalary: filters.minSalary || undefined,
        maxSalary: filters.maxSalary || undefined,
      };
      const blob = await exportEmployeesCsv(params);
      const url = window.URL.createObjectURL(blob);
      const anchor = document.createElement('a');
      anchor.href = url;
      anchor.download = 'employees.csv';
      document.body.appendChild(anchor);
      anchor.click();
      anchor.remove();
      window.URL.revokeObjectURL(url);
    } catch {
      setError('Could not export employees. Is the backend running?');
    } finally {
      setExporting(false);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Delete this employee record? This cannot be undone.')) return;

    try {
      await deleteEmployee(id);
      setReloadKey((value) => value + 1);
    } catch {
      setError('Could not delete employee.');
    }
  };

  return (
    <Box>
      <Stack direction="row" justifyContent="space-between" alignItems="center" sx={{ mb: 2 }}>
        <Typography variant="h5" fontWeight={600}>
          Employees
        </Typography>
        <Stack direction="row" spacing={1}>
          <Button variant="outlined" onClick={handleExport} disabled={exporting}>
            {exporting ? 'Exporting…' : 'Export CSV'}
          </Button>
          <Button variant="contained" startIcon={<AddIcon />} onClick={() => navigate('/employees/new')}>
            Add employee
          </Button>
        </Stack>
      </Stack>

      <FilterBar
        filters={filters}
        onChange={handleFilterChange}
        onReset={() => handleFilterChange({ ...DEFAULT_FILTERS })}
      />

      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

      {loading && !page ? (
        <Box sx={{ display: 'flex', justifyContent: 'center', py: 6 }}>
          <CircularProgress />
        </Box>
      ) : (
        <EmployeeTable
          page={page}
          onPageChange={setPageIndex}
          onEdit={(id) => navigate(`/employees/${id}/edit`)}
          onDelete={handleDelete}
        />
      )}
    </Box>
  );
}
