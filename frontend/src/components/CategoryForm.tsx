import { useState } from 'react';
import { Button, Card, CardContent, Stack, TextField, Typography } from '@mui/material';
import { api } from '../api/client';

/** Reusable create-category form (name unique and non-empty). */
export default function CategoryForm({ onCreated }: { onCreated: () => void }) {
  const [name, setName] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);

  const submit = async () => {
    setBusy(true);
    setError(null);
    try {
      await api.post('/category/create', { name });
      setName('');
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
        <Typography variant="h6" gutterBottom>Create category</Typography>
        <Stack spacing={2}>
          <TextField label="Name" value={name} onChange={(e) => setName(e.target.value)} required />
          {error && <Typography color="error">{error}</Typography>}
          <Button variant="contained" onClick={submit} disabled={busy || !name}>
            Create category
          </Button>
        </Stack>
      </CardContent>
    </Card>
  );
}
