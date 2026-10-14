import {
  Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper,
} from '@mui/material';
import type { ReactNode } from 'react';

export interface Column<T> {
  header: string;
  render: (row: T) => ReactNode;
}

/** Reusable typed data-table wrapper around MUI Table. */
export default function DataTable<T>({
  columns,
  rows,
  rowKey,
  emptyMessage = 'No data',
}: {
  columns: Column<T>[];
  rows: T[] | null;
  rowKey: (row: T) => string;
  emptyMessage?: string;
}) {
  return (
    <TableContainer component={Paper} variant="outlined">
      <Table size="small">
        <TableHead>
          <TableRow>
            {columns.map((c) => (
              <TableCell key={c.header}>{c.header}</TableCell>
            ))}
          </TableRow>
        </TableHead>
        <TableBody>
          {(rows ?? []).length === 0 && (
            <TableRow>
              <TableCell colSpan={columns.length} align="center">
                {emptyMessage}
              </TableCell>
            </TableRow>
          )}
          {(rows ?? []).map((row) => (
            <TableRow key={rowKey(row)} hover>
              {columns.map((c) => (
                <TableCell key={c.header}>{c.render(row)}</TableCell>
              ))}
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
}
