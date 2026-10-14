import { BrowserRouter, Route, Routes } from 'react-router-dom';
import Layout from './components/Layout';
import DashboardPage from './pages/DashboardPage';
import ProductsPage from './pages/ProductsPage';
import InventoryPage from './pages/InventoryPage';
import OrdersPage from './pages/OrdersPage';
import OrderCreatePage from './pages/OrderCreatePage';
import OrderDetailPage from './pages/OrderDetailPage';

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route element={<Layout />}>
          <Route path="/" element={<DashboardPage />} />
          <Route path="/products" element={<ProductsPage />} />
          <Route path="/inventory" element={<InventoryPage />} />
          <Route path="/orders" element={<OrdersPage />} />
          <Route path="/orders/create" element={<OrderCreatePage />} />
          <Route path="/orders/:orderId" element={<OrderDetailPage />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}
