import { Box, Step, StepLabel, Stepper, Typography } from '@mui/material';

const STEPS = ['Order created', 'Stock reserved', 'Payment completed', 'Delivery scheduled'];

const STATUS_STEP: Record<string, number> = {
  PENDING: 0,
  STOCK_RESERVED: 1,
  PAYMENT_COMPLETED: 2,
  CONFIRMED: 3,
};

const FAILURE_STEP: Record<string, number> = { REJECTED: 1, PAYMENT_FAILED: 2, DELIVERY_FAILED: 3 };

/** Visualizes the choreographed order-fulfillment saga. */
export default function SagaStepper({ status }: { status: string }) {
  const failedAt = FAILURE_STEP[status];
  const active = STATUS_STEP[status] ?? 0;
  const isFailed = failedAt !== undefined;

  return (
    <Box sx={{ width: '100%', mb: 3 }}>
      <Stepper activeStep={isFailed ? failedAt : active}>
        {STEPS.map((label, index) => (
          <Step key={label} completed={!isFailed && index < active}>
            <StepLabel error={isFailed && index === failedAt}>{label}</StepLabel>
          </Step>
        ))}
      </Stepper>
      <Typography
        variant="body2"
        color={isFailed ? 'error' : 'text.secondary'}
        sx={{ mt: 1 }}
      >
        {isFailed
          ? `Saga failed at "${STEPS[failedAt]}" — order status: ${status}`
          : status === 'CONFIRMED'
            ? 'Saga complete — the order is fully fulfilled.'
            : `Saga in progress — order status: ${status}`}
      </Typography>
    </Box>
  );
}
