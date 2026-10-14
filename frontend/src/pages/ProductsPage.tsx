import { Grid } from '@mui/material';
import { useProducts, useCategories } from '../hooks/useProducts';
import DataTable, { type Column } from '../components/DataTable';
import ProductForm from '../components/ProductForm';
import CategoryForm from '../components/CategoryForm';
import { ErrorAlert, LoadingState, PageTitle } from '../components/Feedback';
import type { ProductView } from '../api/types';

export default function ProductsPage() {
  const products = useProducts();
  const categories = useCategories();

  const columns: Column<ProductView>[] = [
    { header: 'Title', render: (p) => p.title },
    { header: 'Description', render: (p) => p.description ?? '—' },
    { header: 'Price', render: (p) => p.price.toFixed(2) },
    { header: 'Categories', render: (p) => p.categories.join(', ') || '—' },
  ];

  return (
    <>
      <PageTitle title="Catalog" subtitle="Create categories & products — the saga reads product prices from here." />
      {products.error && <ErrorAlert message={products.error} />}
      {categories.error && <ErrorAlert message={categories.error} />}
      <Grid container spacing={3}>
        <Grid item xs={12} md={6}>
          <CategoryForm onCreated={categories.refresh} />
        </Grid>
        <Grid item xs={12} md={6}>
          <ProductForm categoryNames={(categories.data ?? []).map((c) => c.name)} onCreated={products.refresh} />
        </Grid>
        <Grid item xs={12}>
          {products.loading ? <LoadingState /> : (
            <DataTable columns={columns} rows={products.data} rowKey={(p) => p.id} emptyMessage="No products yet" />
          )}
        </Grid>
      </Grid>
    </>
  );
}
