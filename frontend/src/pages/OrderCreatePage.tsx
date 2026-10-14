import { useNavigate } from 'react-router-dom';
import { useProducts } from '../hooks/useProducts';
import OrderCreateForm from '../components/OrderCreateForm';
import { ErrorAlert, LoadingState, PageTitle } from '../components/Feedback';

export default function OrderCreatePage() {
  const products = useProducts();
  const navigate = useNavigate();

  return (
    <>
      <PageTitle title="New order" subtitle="POST /order/create — OrderCreated → StockCheckedAndReserved → PaymentCreated → DeliveryScheduled" />
      {products.error && <ErrorAlert message={products.error} />}
      {products.loading ? <LoadingState /> : (
        <OrderCreateForm
          products={products.data ?? []}
          onCreated={(orderId) => navigate(`/orders/${orderId}`)}
        />
      )}
    </>
  );
}
