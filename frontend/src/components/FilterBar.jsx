import React from 'react';
import { Paper, Grid, TextField, MenuItem, Button } from '@mui/material';

const EMPLOYMENT_TYPES = ['FULL_TIME', 'PART_TIME', 'CONTRACT', 'INTERN'];

export default function FilterBar({ filters, onChange, onReset }) {
  const handleField = (field) => (event) => {
    onChange({ ...filters, [field]: event.target.value });
  };

  return (
    <Paper variant="outlined" sx={{ p: 2, mb: 2 }}>
      <Grid container spacing={2} alignItems="center">
        <Grid item xs={12} sm={6} md={3}>
          <TextField
            fullWidth
            size="small"
            label="Search name / code / email"
            value={filters.q || ''}
            onChange={handleField('q')}
          />
        </Grid>

        <Grid item xs={6} sm={3} md={2}>
          <TextField
            fullWidth
            size="small"
            label="Department"
            value={filters.department || ''}
            onChange={handleField('department')}
          />
        </Grid>

        <Grid item xs={6} sm={3} md={2}>
          <TextField
            fullWidth
            size="small"
            label="Designation"
            value={filters.designation || ''}
            onChange={handleField('designation')}
          />
        </Grid>

        <Grid item xs={6} sm={3} md={2}>
          <TextField
            fullWidth
            size="small"
            label="Country"
            value={filters.country || ''}
            onChange={handleField('country')}
          />
        </Grid>

        <Grid item xs={6} sm={3} md={2}>
          <TextField
            select
            fullWidth
            size="small"
            label="Employment type"
            value={filters.employmentType || ''}
            onChange={handleField('employmentType')}
          >
            <MenuItem value="">Any</MenuItem>
            {EMPLOYMENT_TYPES.map((type) => (
              <MenuItem key={type} value={type}>
                {type.replace('_', ' ')}
              </MenuItem>
            ))}
          </TextField>
        </Grid>

        <Grid item xs={6} sm={3} md={1.5}>
          <TextField
            fullWidth
            size="small"
            type="number"
            label="Min salary"
            value={filters.minSalary || ''}
            onChange={handleField('minSalary')}
            inputProps={{ min: 0 }}
          />
        </Grid>

        <Grid item xs={6} sm={3} md={1.5}>
          <TextField
            fullWidth
            size="small"
            type="number"
            label="Max salary"
            value={filters.maxSalary || ''}
            onChange={handleField('maxSalary')}
            inputProps={{ min: 0 }}
          />
        </Grid>

        <Grid item xs={12} sm={3} md={1}>
          <Button fullWidth onClick={onReset}>
            Reset
          </Button>
        </Grid>
      </Grid>
    </Paper>
  );
}
