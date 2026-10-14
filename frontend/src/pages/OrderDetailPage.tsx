import { useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { Card, CardContent, Grid, Typography } from '@mui/material';
import { useOrder, usePayment, useDelivery } from '../hooks/useOrders';
import SagaStepper from '../components/SagaStepper';
import StatusChip from '../components/StatusChip';
import DataTable, { type Column } from '../components/DataTable';
import { ErrorAlert, LoadingState, PageTitle } from '../components/Feedback';
import type { OrderItemView } from '../api/types';

export default function OrderDetailPage() {
  const { orderId = '' } = useParams();
  const order = useOrder(orderId);
  const payment = usePayment(orderId);
  const delivery = useDelivery(orderId);

  const status = order.data?.status ?? 'PENDING';
  const terminal = ['CONFIRMED', 'REJECTED', 'PAYMENT_FAILED', 'DELIVERY_FAILED'].includes(status);

  // Live-refresh until the saga reaches a terminal state (eventual consistency).
  useEffect(() => {
    if (terminal) return;
    const timer = setInterval(() => {
      order.refresh();
      payment.refresh();
      delivery.refresh();
    }, 1500);
    return () => clearInterval(timer);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [terminal, order.data?.status]);

  if (order.loading && !order.data) return <LoadingState />;
  if (order.error) return <ErrorAlert message={order.error} />;

  const itemColumns: Column<OrderItemView>[] = [
    { header: 'Product', render: (i) => i.productId.slice(0, 8) },
    { header: 'Qty', render: (i) => i.quantity },
    { header: 'Unit price', render: (i) => i.unitPrice.toFixed(2) },
  ];

  return (
    <>
      <PageTitle title={`Order ${orderId.slice(0, 8)}…`} />
      <SagaStepper status={status} />
      <Grid container spacing={3}>
        <Grid item xs={12} md={6}>
          <Card variant="outlined">
            <CardContent>
              <Typography variant="h6">Order</Typography>
              <Typography>Status: <StatusChip status={status} /></Typography>
              <Typography>User: {order.data?.userId}</Typography>
              <Typography>Total: {order.data?.totalAmount.toFixed(2)}</Typography>
              <Typography>
                Ship to: {order.data?.street}, {order.data?.city} {order.data?.zip}, {order.data?.country}
              </Typography>
              <DataTable columns={itemColumns} rows={order.data?.items ?? []} rowKey={(i) => i.productId} />
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} md={6}>
          <Card variant="outlined" sx={{ mb: 2 }}>
            <CardContent>
              <Typography variant="h6">Payment</Typography>
              {payment.data && payment.data.status !== 'PENDING' ? (
                <>
                  <Typography>Status: <StatusChip status={payment.data.status} /></Typography>
                  {payment.data.cardLast4 && <Typography>Card: **** {payment.data.cardLast4}</Typography>}
                  {payment.data.failureReason && (
                    <Typography color="error">Reason: {payment.data.failureReason}</Typography>
                  )}
                </>
              ) : (
                <Typography color="text.secondary">No payment yet…</Typography>
              )}
            </CardContent>
          </Card>
          <Card variant="outlined">
            <CardContent>
              <Typography variant="h6">Delivery</Typography>
              {delivery.data ? (
                <>
                  <Typography>Status: <StatusChip status={delivery.data.status} /></Typography>
                  <Typography>
                    {delivery.data.street}, {delivery.data.city} {delivery.data.zip}, {delivery.data.country}
                  </Typography>
                </>
              ) : (
                <Typography color="text.secondary">No delivery yet…</Typography>
              )}
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </>
  );
}
