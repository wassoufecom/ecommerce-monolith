import { useNavigate } from 'react-router-dom';
import { Link } from '@mui/material';
import { useOrders } from '../hooks/useOrders';
import DataTable, { type Column } from '../components/DataTable';
import StatusChip from '../components/StatusChip';
import { ErrorAlert, LoadingState, PageTitle } from '../components/Feedback';
import type { OrderView } from '../api/types';

export default function OrdersPage() {
  const orders = useOrders();
  const navigate = useNavigate();

  const columns: Column<OrderView>[] = [
    {
      header: 'Order',
      render: (o) => (
        <Link component="button" onClick={() => navigate(`/orders/${o.id}`)}>{o.id.slice(0, 8)}…</Link>
      ),
    },
    { header: 'User', render: (o) => o.userId },
    { header: 'Total', render: (o) => o.totalAmount.toFixed(2) },
    { header: 'Items', render: (o) => o.items.length },
    { header: 'Status', render: (o) => <StatusChip status={o.status} /> },
  ];

  return (
    <>
      <PageTitle title="Orders" subtitle="Query side — read model updated by domain events." />
      {orders.error && <ErrorAlert message={orders.error} />}
      {orders.loading ? <LoadingState /> : (
        <DataTable columns={columns} rows={orders.data} rowKey={(o) => o.id} emptyMessage="No orders yet" />
      )}
    </>
  );
}
