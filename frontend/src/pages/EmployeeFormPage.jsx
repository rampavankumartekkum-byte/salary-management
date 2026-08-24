import React, { useEffect, useState } from 'react';
import {
  Box, Paper, Typography, Grid, TextField, MenuItem, Button, Alert, Stack,
} from '@mui/material';
import { useNavigate, useParams } from 'react-router-dom';
import { getEmployee, createEmployee, updateEmployee } from '../api/employeeApi';

const EMPLOYMENT_TYPES = ['FULL_TIME', 'PART_TIME', 'CONTRACT', 'INTERN'];

const EMPTY_FORM = {
  employeeCode: '', firstName: '', lastName: '', email: '',
  department: '', designation: '', country: '', currency: '',
  baseSalary: '', annualBonus: '', employmentType: 'FULL_TIME',
  managerName: '', dateJoined: '',
};

export default function EmployeeFormPage({ mode }) {
  const { id } = useParams();
  const navigate = useNavigate();
  const [form, setForm] = useState(EMPTY_FORM);
  const [errors, setErrors] = useState({});
  const [submitError, setSubmitError] = useState(null);
  const [loading, setLoading] = useState(mode === 'edit');

  useEffect(() => {
    if (mode === 'edit' && id) {
      getEmployee(id).then((emp) => {
        setForm({ ...emp });
        setLoading(false);
      });
    }
  }, [mode, id]);

  const handleChange = (field) => (event) => {
    setForm({ ...form, [field]: event.target.value });
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setSubmitError(null);
    setErrors({});
    const payload = {
      ...form,
      baseSalary: Number(form.baseSalary),
      annualBonus: Number(form.annualBonus || 0),
    };
    try {
      if (mode === 'create') {
        await createEmployee(payload);
      } else {
        await updateEmployee(id, payload);
      }
      navigate('/employees');
    } catch (err) {
      if (err.response?.status === 400) {
        setErrors(err.response.data.fieldErrors || {});
      } else if (err.response?.status === 409) {
        setSubmitError(err.response.data.message);
      } else {
        setSubmitError('Something went wrong saving this employee.');
      }
    }
  };

  if (loading) return null;

  return (
    <Box>
      <Typography variant="h5" fontWeight={600} sx={{ mb: 2 }}>
        {mode === 'create' ? 'Add employee' : 'Edit employee'}
      </Typography>
      <Paper variant="outlined" sx={{ p: 3, maxWidth: 720 }}>
        {submitError && <Alert severity="error" sx={{ mb: 2 }}>{submitError}</Alert>}
        <form onSubmit={handleSubmit}>
          <Grid container spacing={2}>
            <Grid item xs={6}>
              <TextField fullWidth label="Employee code" value={form.employeeCode}
                onChange={handleChange('employeeCode')} error={!!errors.employeeCode} helperText={errors.employeeCode} />
            </Grid>
            <Grid item xs={6}>
              <TextField fullWidth label="Email" value={form.email}
                onChange={handleChange('email')} error={!!errors.email} helperText={errors.email} />
            </Grid>
            <Grid item xs={6}>
              <TextField fullWidth label="First name" value={form.firstName}
                onChange={handleChange('firstName')} error={!!errors.firstName} helperText={errors.firstName} />
            </Grid>
            <Grid item xs={6}>
              <TextField fullWidth label="Last name" value={form.lastName}
                onChange={handleChange('lastName')} error={!!errors.lastName} helperText={errors.lastName} />
            </Grid>
            <Grid item xs={6}>
              <TextField fullWidth label="Department" value={form.department}
                onChange={handleChange('department')} error={!!errors.department} helperText={errors.department} />
            </Grid>
            <Grid item xs={6}>
              <TextField fullWidth label="Designation" value={form.designation}
                onChange={handleChange('designation')} error={!!errors.designation} helperText={errors.designation} />
            </Grid>
            <Grid item xs={6}>
              <TextField fullWidth label="Country" value={form.country}
                onChange={handleChange('country')} error={!!errors.country} helperText={errors.country} />
            </Grid>
            <Grid item xs={6}>
              <TextField fullWidth label="Currency (e.g. USD)" value={form.currency}
                onChange={handleChange('currency')} error={!!errors.currency} helperText={errors.currency} />
            </Grid>
            <Grid item xs={6}>
              <TextField fullWidth type="number" label="Base salary" value={form.baseSalary}
                onChange={handleChange('baseSalary')} error={!!errors.baseSalary} helperText={errors.baseSalary} />
            </Grid>
            <Grid item xs={6}>
              <TextField fullWidth type="number" label="Annual bonus" value={form.annualBonus}
                onChange={handleChange('annualBonus')} error={!!errors.annualBonus} helperText={errors.annualBonus} />
            </Grid>
            <Grid item xs={6}>
              <TextField select fullWidth label="Employment type" value={form.employmentType}
                onChange={handleChange('employmentType')}>
                {EMPLOYMENT_TYPES.map((type) => (
                  <MenuItem key={type} value={type}>{type.replace('_', ' ')}</MenuItem>
                ))}
              </TextField>
            </Grid>
            <Grid item xs={6}>
              <TextField fullWidth type="date" label="Date joined" InputLabelProps={{ shrink: true }}
                value={form.dateJoined} onChange={handleChange('dateJoined')}
                error={!!errors.dateJoined} helperText={errors.dateJoined} />
            </Grid>
            <Grid item xs={12}>
              <TextField fullWidth label="Manager name (optional)" value={form.managerName || ''}
                onChange={handleChange('managerName')} />
            </Grid>
          </Grid>
          <Stack direction="row" spacing={2} sx={{ mt: 3 }}>
            <Button type="submit" variant="contained">Save</Button>
            <Button onClick={() => navigate('/employees')}>Cancel</Button>
          </Stack>
        </form>
      </Paper>
    </Box>
  );
}
