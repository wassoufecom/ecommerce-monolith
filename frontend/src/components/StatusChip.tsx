import { Chip } from '@mui/material';
import type { ChipProps } from '@mui/material';

const COLORS: Record<string, ChipProps['color']> = {
  PENDING: 'default',
  STOCK_RESERVED: 'info',
  PAYMENT_COMPLETED: 'primary',
  CONFIRMED: 'success',
  COMPLETED: 'success',
  SCHEDULED: 'success',
  IN_TRANSIT: 'info',
  DELIVERED: 'success',
  FAILED: 'error',
  REJECTED: 'error',
  PAYMENT_FAILED: 'error',
  DELIVERY_FAILED: 'error',
  CANCELLED: 'default',
};

/** Reusable status chip used for orders, payments and deliveries. */
export default function StatusChip({ status }: { status: string }) {
  return <Chip label={status} color={COLORS[status] ?? 'default'} size="small" />;
}
