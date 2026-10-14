import { useState } from 'react';
import {
  Button, Card, CardContent, Divider, IconButton, Stack, Table, TableBody,
  TableCell, TableHead, TableRow, TextField, Typography,
} from '@mui/material';
import DeleteIcon from '@mui/icons-material/Delete';
import AddIcon from '@mui/icons-material/Add';
import { api } from '../api/client';
import type { CreateOrderRequest, ProductView } from '../api/types';

interface Line {
  productId: string;
  quantity: number;
  price: number;
}

/** Reusable order form — triggers the fulfillment saga on submit. */
export default function OrderCreateForm({ products, onCreated }: {
  products: ProductView[];
  onCreated: (orderId: string) => void;
}) {
  const [userId, setUserId] = useState('customer-1');
  const [address, setAddress] = useState({ street: 'Main St 1', city: 'Warsaw', zip: '00-001', country: 'PL' });
  const [lines, setLines] = useState<Line[]>([]);
  const [card, setCard] = useState({ cardNumber: '4111111111111111', cvv: '123', expiryDate: '12/28' });
  const [error, setError] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);

  const total = lines.reduce((sum, l) => sum + l.quantity * l.price, 0);

  const updateLine = (index: number, patch: Partial<Line>) =>
    setLines((prev) => prev.map((l, i) => (i === index ? { ...l, ...patch } : l)));

  const addLine = () =>
    setLines((prev) =>
      products.length > 0
        ? [...prev, { productId: products[0].id, quantity: 1, price: products[0].price }]
        : prev,
    );

  const submit = async () => {
    setBusy(true);
    setError(null);
    try {
      const body: CreateOrderRequest = {
        userId,
        address,
        orderItems: lines.map((l) => ({ productId: l.productId, quantity: l.quantity, price: l.price })),
        price: Number(total.toFixed(2)),
        paymentDetails: card,
      };
      const result = await api.post<{ orderId: string }>('/order/create', body);
      onCreated(result.orderId);
    } catch (e) {
      setError((e as Error).message);
    } finally {
      setBusy(false);
    }
  };

  return (
    <Card variant="outlined">
      <CardContent>
        <Typography variant="h6" gutterBottom>Create order (starts the saga)</Typography>
        <Stack spacing={2}>
          <TextField label="User ID" value={userId} onChange={(e) => setUserId(e.target.value)} />
          <Divider />
          <Typography variant="subtitle1">Shipping address</Typography>
          <Stack direction="row" spacing={2}>
            <TextField label="Street" value={address.street} onChange={(e) => setAddress({ ...address, street: e.target.value })} fullWidth />
            <TextField label="City" value={address.city} onChange={(e) => setAddress({ ...address, city: e.target.value })} />
            <TextField label="ZIP" value={address.zip} onChange={(e) => setAddress({ ...address, zip: e.target.value })} />
            <TextField label="Country" value={address.country} onChange={(e) => setAddress({ ...address, country: e.target.value })} />
          </Stack>
          <Divider />
          <Typography variant="subtitle1">Items</Typography>
          <Table size="small">
            <TableHead>
              <TableRow>
                <TableCell>Product</TableCell>
                <TableCell>Quantity</TableCell>
                <TableCell>Unit price</TableCell>
                <TableCell />
              </TableRow>
            </TableHead>
            <TableBody>
              {lines.map((line, index) => (
                <TableRow key={index}>
                  <TableCell>
                    <TextField
                      select
                      SelectProps={{ native: true }}
                      size="small"
                      value={line.productId}
                      onChange={(e) => {
                        const p = products.find((x) => x.id === e.target.value);
                        updateLine(index, { productId: e.target.value, price: p?.price ?? line.price });
                      }}
                    >
                      {products.map((p) => (
                        <option key={p.id} value={p.id}>{p.title}</option>
                      ))}
                    </TextField>
                  </TableCell>
                  <TableCell>
                    <TextField
                      size="small" type="number" value={line.quantity}
                      inputProps={{ min: 1 }}
                      onChange={(e) => updateLine(index, { quantity: Number(e.target.value) })}
                    />
                  </TableCell>
                  <TableCell>
                    <TextField
                      size="small" type="number" value={line.price}
                      inputProps={{ min: 0.01, step: 0.01 }}
                      onChange={(e) => updateLine(index, { price: Number(e.target.value) })}
                    />
                  </TableCell>
                  <TableCell>
                    <IconButton onClick={() => setLines((prev) => prev.filter((_, i) => i !== index))}>
                      <DeleteIcon fontSize="small" />
                    </IconButton>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
          <Button startIcon={<AddIcon />} onClick={addLine} disabled={products.length === 0}>
            Add item
          </Button>
          <Typography variant="subtitle1">Total: {total.toFixed(2)}</Typography>
          <Divider />
          <Typography variant="subtitle1">Payment (mock gateway)</Typography>
          <Stack direction="row" spacing={2}>
            <TextField label="Card number" value={card.cardNumber} onChange={(e) => setCard({ ...card, cardNumber: e.target.value })} fullWidth />
            <TextField label="CVV" value={card.cvv} onChange={(e) => setCard({ ...card, cvv: e.target.value })} />
            <TextField label="Expiry (MM/YY)" value={card.expiryDate} onChange={(e) => setCard({ ...card, expiryDate: e.target.value })} />
          </Stack>
          <Typography variant="caption" color="text.secondary">
            Use card 4000000000000002 to simulate a declined payment.
          </Typography>
          {error && <Typography color="error">{error}</Typography>}
          <Button variant="contained" onClick={submit} disabled={busy || lines.length === 0}>
            Place order
          </Button>
        </Stack>
      </CardContent>
    </Card>
  );
}
