import { Grid } from '@mui/material';
import { useProducts } from '../hooks/useProducts';
import { useInventory } from '../hooks/useInventory';
import DataTable, { type Column } from '../components/DataTable';
import AddStockForm from '../components/AddStockForm';
import { ErrorAlert, LoadingState, PageTitle } from '../components/Feedback';
import type { StockView } from '../api/types';

export default function InventoryPage() {
  const products = useProducts();
  const inventory = useInventory();

  const titleOf = (productId: string) =>
    products.data?.find((p) => p.id === productId)?.title ?? productId.slice(0, 8);

  const columns: Column<StockView>[] = [
    { header: 'Product', render: (s) => titleOf(s.productId) },
    { header: 'Available', render: (s) => s.availableQty },
    { header: 'Reserved', render: (s) => s.reservedQty },
  ];

  return (
    <>
      <PageTitle title="Inventory" subtitle="Stock levels updated by domain events (add / reserve / release)." />
      {inventory.error && <ErrorAlert message={inventory.error} />}
      <Grid container spacing={3}>
        <Grid item xs={12} md={5}>
          <AddStockForm products={products.data ?? []} onAdded={inventory.refresh} />
        </Grid>
        <Grid item xs={12} md={7}>
          {inventory.loading ? <LoadingState /> : (
            <DataTable columns={columns} rows={inventory.data} rowKey={(s) => s.productId} emptyMessage="No stock yet" />
          )}
        </Grid>
      </Grid>
    </>
  );
}
