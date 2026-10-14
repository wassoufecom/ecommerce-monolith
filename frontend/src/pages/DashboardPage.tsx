import { Grid, Card, CardContent, Typography } from '@mui/material';
import { useProducts } from '../hooks/useProducts';
import { useInventory } from '../hooks/useInventory';
import { useOrders } from '../hooks/useOrders';
import { PageTitle, LoadingState } from '../components/Feedback';

export default function DashboardPage() {
  const { data: products, loading: l1 } = useProducts();
  const { data: stock, loading: l2 } = useInventory();
  const { data: orders, loading: l3 } = useOrders();

  if (l1 || l2 || l3) return <LoadingState />;

  const confirmed = (orders ?? []).filter((o) => o.status === 'CONFIRMED').length;
  const units = (stock ?? []).reduce((sum, s) => sum + s.availableQty, 0);

  const stats = [
    { label: 'Products', value: products?.length ?? 0 },
    { label: 'Available stock units', value: units },
    { label: 'Orders', value: orders?.length ?? 0 },
    { label: 'Confirmed orders', value: confirmed },
  ];

  return (
    <>
      <PageTitle title="Dashboard" subtitle="Modular monolith — Clean Architecture + DDD + CQRS (state-stored)" />
      <Grid container spacing={3}>
        {stats.map((s) => (
          <Grid item xs={12} sm={6} md={3} key={s.label}>
            <Card variant="outlined">
              <CardContent>
                <Typography color="text.secondary">{s.label}</Typography>
                <Typography variant="h4">{s.value}</Typography>
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>
    </>
  );
}
