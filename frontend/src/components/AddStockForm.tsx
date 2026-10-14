import { useState } from 'react';
import {
  Autocomplete, Button, Card, CardContent, Stack, TextField, Typography,
} from '@mui/material';
import { api } from '../api/client';
import type { ProductView } from '../api/types';

/** Reusable add-stock form (product must exist in the catalog, quantity > 0). */
export default function AddStockForm({ products, onAdded }: {
  products: ProductView[];
  onAdded: () => void;
}) {
  const [product, setProduct] = useState<ProductView | null>(null);
  const [quantity, setQuantity] = useState('10');
  const [error, setError] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);

  const submit = async () => {
    if (!product) return;
    setBusy(true);
    setError(null);
    try {
      await api.post('/inventory/add', { productId: product.id, quantity: Number(quantity) });
      onAdded();
    } catch (e) {
      setError((e as Error).message);
    } finally {
      setBusy(false);
    }
  };

  return (
    <Card variant="outlined">
      <CardContent>
        <Typography variant="h6" gutterBottom>Add stock</Typography>
        <Stack spacing={2}>
          <Autocomplete
            options={products}
            getOptionLabel={(p) => `${p.title} (${p.id.slice(0, 8)}…)`}
            value={product}
            onChange={(_, v) => setProduct(v)}
            renderInput={(params) => <TextField {...params} label="Product" />}
          />
          <TextField label="Quantity" type="number" value={quantity} onChange={(e) => setQuantity(e.target.value)} inputProps={{ min: 1 }} />
          {error && <Typography color="error">{error}</Typography>}
          <Button variant="contained" onClick={submit} disabled={busy || !product || !quantity}>
            Add stock
          </Button>
        </Stack>
      </CardContent>
    </Card>
  );
}
