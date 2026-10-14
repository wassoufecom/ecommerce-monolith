// API types mirror the backend read/write DTOs (Swagger: /swagger-ui/index.html).

export interface ProductView {
  id: string;
  title: string;
  description: string;
  price: number;
  categories: string[];
}

export interface CategoryView {
  id: string;
  name: string;
}

export interface StockView {
  productId: string;
  availableQty: number;
  reservedQty: number;
}

export interface OrderItemView {
  productId: string;
  quantity: number;
  unitPrice: number;
}

export interface OrderView {
  id: string;
  userId: string;
  status: string;
  totalAmount: number;
  items: OrderItemView[];
  street: string;
  city: string;
  zip: string;
  country: string;
  createdAt: string | null;
}

export interface PaymentView {
  id: string | null;
  orderId: string;
  amount: number;
  status: string;
  cardLast4: string | null;
  failureReason: string | null;
  createdAt: string | null;
}

export interface DeliveryView {
  id: string;
  orderId: string;
  status: string;
  street: string | null;
  city: string | null;
  zip: string | null;
  country: string | null;
  createdAt: string | null;
}

export interface CreateProductRequest {
  title: string;
  description?: string;
  price: number;
  categories?: string[];
}

export interface CreateCategoryRequest {
  name: string;
}

export interface AddStockRequest {
  productId: string;
  quantity: number;
}

export interface CreateOrderRequest {
  userId: string;
  address: { street: string; city: string; zip: string; country: string };
  orderItems: { productId: string; quantity: number; price: number }[];
  price: number;
  paymentDetails: { cardNumber: string; cvv: string; expiryDate: string };
}

export interface ApiError {
  timestamp: string;
  status: number;
  error: string;
  errors: { field: string; message: string }[];
}
