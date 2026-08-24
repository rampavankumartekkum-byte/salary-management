import React from 'react';
import {
  Table,
  TableHead,
  TableBody,
  TableRow,
  TableCell,
  TableContainer,
  TablePagination,
  Paper,
  Chip,
  IconButton,
  Stack,
} from '@mui/material';
import EditIcon from '@mui/icons-material/EditOutlined';
import DeleteIcon from '@mui/icons-material/DeleteOutline';

function formatMoney(amount, currency) {
  try {
    return new Intl.NumberFormat('en-US', { style: 'currency', currency }).format(amount);
  } catch {
    return `${amount} ${currency}`;
  }
}

export default function EmployeeTable({ page, onPageChange, onEdit, onDelete }) {
  if (!page) return null;

  return (
    <Paper variant="outlined">
      <TableContainer>
        <Table size="small">
          <TableHead>
            <TableRow>
              <TableCell>Code</TableCell>
              <TableCell>Name</TableCell>
              <TableCell>Department</TableCell>
              <TableCell>Designation</TableCell>
              <TableCell>Country</TableCell>
              <TableCell align="right">Base salary</TableCell>
              <TableCell>Type</TableCell>
              <TableCell align="right">Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {page.content.map((emp) => (
              <TableRow key={emp.id} hover>
                <TableCell>{emp.employeeCode}</TableCell>
                <TableCell>{emp.firstName} {emp.lastName}</TableCell>
                <TableCell>{emp.department}</TableCell>
                <TableCell>{emp.designation}</TableCell>
                <TableCell>{emp.country}</TableCell>
                <TableCell align="right">{formatMoney(emp.baseSalary, emp.currency)}</TableCell>
                <TableCell>
                  <Chip size="small" label={emp.employmentType.replace('_', ' ')} />
                </TableCell>
                <TableCell align="right">
                  <Stack direction="row" spacing={0.5} justifyContent="flex-end">
                    <IconButton size="small" aria-label={`edit-${emp.id}`} onClick={() => onEdit(emp.id)}>
                      <EditIcon fontSize="small" />
                    </IconButton>
                    <IconButton size="small" aria-label={`delete-${emp.id}`} onClick={() => onDelete(emp.id)}>
                      <DeleteIcon fontSize="small" />
                    </IconButton>
                  </Stack>
                </TableCell>
              </TableRow>
            ))}
            {page.content.length === 0 && (
              <TableRow>
                <TableCell colSpan={8} align="center" sx={{ py: 4, color: 'text.secondary' }}>
                  No employees match these filters.
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </TableContainer>
      <TablePagination
        component="div"
        count={page.totalElements}
        page={page.page}
        onPageChange={(_, newPage) => onPageChange(newPage)}
        rowsPerPage={page.size}
        rowsPerPageOptions={[page.size]}
      />
    </Paper>
  );
}
