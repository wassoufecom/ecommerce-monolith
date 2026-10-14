import { useFetch } from './useFetch';
import type { OrderView, PaymentView, DeliveryView } from '../api/types';

export const useOrders = () => useFetch<OrderView[]>('/orders');
export const useOrder = (orderId: string) => useFetch<OrderView>(`/order/${orderId}`);
export const usePayment = (orderId: string) =>
  useFetch<PaymentView>(`/payment/order/${orderId}`);
export const useDelivery = (orderId: string) =>
  useFetch<DeliveryView>(`/delivery/order/${orderId}`);
