import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import EmployeeTable from '../components/EmployeeTable';

const samplePage = {
  content: [
    {
      id: 1,
      employeeCode: 'ACME00001',
      firstName: 'Ada',
      lastName: 'Lovelace',
      department: 'Engineering',
      designation: 'Senior Software Engineer',
      country: 'United Kingdom',
      currency: 'GBP',
      baseSalary: 95000,
      employmentType: 'FULL_TIME',
    },
  ],
  page: 0,
  size: 25,
  totalElements: 1,
  totalPages: 1,
  last: true,
};

describe('EmployeeTable', () => {
  it('renders employee rows with formatted salary', () => {
    render(<EmployeeTable page={samplePage} onPageChange={() => {}} onEdit={() => {}} onDelete={() => {}} />);

    expect(screen.getByText('Ada Lovelace')).toBeInTheDocument();
    expect(screen.getByText('Engineering')).toBeInTheDocument();
    expect(screen.getByText(/£95,000/)).toBeInTheDocument();
  });

  it('shows an empty state when there are no results', () => {
    render(<EmployeeTable page={{ ...samplePage, content: [], totalElements: 0 }}
      onPageChange={() => {}} onEdit={() => {}} onDelete={() => {}} />);

    expect(screen.getByText(/No employees match these filters/)).toBeInTheDocument();
  });

  it('calls onEdit when the edit action is clicked', () => {
    const onEdit = vi.fn();
    render(<EmployeeTable page={samplePage} onPageChange={() => {}} onEdit={onEdit} onDelete={() => {}} />);

    fireEvent.click(screen.getByLabelText('edit-1'));
    expect(onEdit).toHaveBeenCalledWith(1);
  });

  it('calls onDelete when the delete action is clicked', () => {
    const onDelete = vi.fn();
    render(<EmployeeTable page={samplePage} onPageChange={() => {}} onEdit={() => {}} onDelete={onDelete} />);

    fireEvent.click(screen.getByLabelText('delete-1'));
    expect(onDelete).toHaveBeenCalledWith(1);
  });
});
