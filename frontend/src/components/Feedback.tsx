import { Alert, CircularProgress, Paper, Typography } from '@mui/material';
import Box from '@mui/material/Box';

export function LoadingState() {
  return (
    <Box display="flex" justifyContent="center" alignItems="center" py={6}>
      <CircularProgress />
    </Box>
  );
}

export function ErrorAlert({ message }: { message: string }) {
  return (
    <Alert severity="error" sx={{ mb: 2 }}>
      {message}
    </Alert>
  );
}

export function PageTitle({ title, subtitle }: { title: string; subtitle?: string }) {
  return (
    <Paper sx={{ p: 2, mb: 3 }}>
      <Typography variant="h5">{title}</Typography>
      {subtitle && (
        <Typography variant="body2" color="text.secondary">
          {subtitle}
        </Typography>
      )}
    </Paper>
  );
}
