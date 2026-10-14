import { useState } from 'react';
import {
  Autocomplete, Button, Card, CardContent, Stack, TextField, Typography,
} from '@mui/material';
import { api } from '../api/client';
import type { CreateProductRequest } from '../api/types';

/** Reusable create-product form (title non-empty, price > 0, categories optional). */
export default function ProductForm({ categoryNames, onCreated }: {
  categoryNames: string[];
  onCreated: () => void;
}) {
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [price, setPrice] = useState('');
  const [categories, setCategories] = useState<string[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);

  const submit = async () => {
    setBusy(true);
    setError(null);
    try {
      const body: CreateProductRequest = {
        title,
        description,
        price: Number(price),
        categories,
      };
      await api.post('/product/create', body);
      setTitle('');
      setDescription('');
      setPrice('');
      setCategories([]);
      onCreated();
    } catch (e) {
      setError((e as Error).message);
    } finally {
      setBusy(false);
    }
  };

  return (
    <Card variant="outlined">
      <CardContent>
        <Typography variant="h6" gutterBottom>Create product</Typography>
        <Stack spacing={2}>
          <TextField label="Title" value={title} onChange={(e) => setTitle(e.target.value)} required />
          <TextField label="Description" value={description} onChange={(e) => setDescription(e.target.value)} multiline minRows={2} />
          <TextField label="Price" type="number" value={price} onChange={(e) => setPrice(e.target.value)} required inputProps={{ min: 0.01, step: 0.01 }} />
          <Autocomplete
            multiple
            options={categoryNames}
            value={categories}
            onChange={(_, v) => setCategories(v)}
            renderInput={(params) => <TextField {...params} label="Categories" />}
          />
          {error && <Typography color="error">{error}</Typography>}
          <Button variant="contained" onClick={submit} disabled={busy || !title || !price}>
            Create product
          </Button>
        </Stack>
      </CardContent>
    </Card>
  );
}
