import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import FilterBar from '../components/FilterBar';

describe('FilterBar', () => {
  it('calls onChange with updated field when the user types', () => {
    const onChange = vi.fn();
    render(<FilterBar filters={{}} onChange={onChange} onReset={() => {}} />);

    fireEvent.change(screen.getByLabelText(/Search name/i), { target: { value: 'Ada' } });

    expect(onChange).toHaveBeenCalledWith(expect.objectContaining({ q: 'Ada' }));
  });

  it('calls onReset when the reset button is clicked', () => {
    const onReset = vi.fn();
    render(<FilterBar filters={{ q: 'Ada' }} onChange={() => {}} onReset={onReset} />);

    fireEvent.click(screen.getByText('Reset'));

    expect(onReset).toHaveBeenCalled();
  });
  it('supports designation and maximum salary filters', () => {
    const onChange = vi.fn();
    render(<FilterBar filters={{}} onChange={onChange} onReset={() => {}} />);

    fireEvent.change(screen.getByLabelText(/Designation/i), { target: { value: 'Engineer' } });
    fireEvent.change(screen.getByLabelText(/Max salary/i), { target: { value: '100000' } });

    expect(onChange).toHaveBeenLastCalledWith(expect.objectContaining({ maxSalary: '100000' }));
  });
});
