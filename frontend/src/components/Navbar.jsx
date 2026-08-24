import React from 'react';
import { AppBar, Toolbar, Typography, Button, Stack } from '@mui/material';
import { Link, useLocation } from 'react-router-dom';

export default function Navbar() {
  const location = useLocation();

  return (
    <AppBar position="static" color="primary" elevation={0}>
      <Toolbar sx={{ maxWidth: 1200, mx: 'auto', width: '100%' }}>
        <Typography variant="h6" sx={{ flexGrow: 1, fontWeight: 600 }}>
          ACME Salary Management
        </Typography>
        <Stack direction="row" spacing={1}>
          <Button
            component={Link}
            to="/employees"
            color="inherit"
            variant={location.pathname.startsWith('/employees') ? 'outlined' : 'text'}
          >
            Employees
          </Button>
          <Button
            component={Link}
            to="/dashboard"
            color="inherit"
            variant={location.pathname === '/dashboard' ? 'outlined' : 'text'}
          >
            Dashboard
          </Button>
        </Stack>
      </Toolbar>
    </AppBar>
  );
}
