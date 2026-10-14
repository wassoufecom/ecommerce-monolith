import { AppBar, Box, Button, Container, Toolbar, Typography } from '@mui/material';
import StorefrontIcon from '@mui/icons-material/Storefront';
import { NavLink, Outlet } from 'react-router-dom';

const NAV = [
  { label: 'Dashboard', to: '/' },
  { label: 'Products', to: '/products' },
  { label: 'Inventory', to: '/inventory' },
  { label: 'Orders', to: '/orders' },
  { label: 'New Order', to: '/orders/create' },
];

/** Reusable app shell: top bar with navigation + routed page content. */
export default function Layout() {
  return (
    <Box sx={{ minHeight: '100vh', bgcolor: '#f5f5f5' }}>
      <AppBar position="static">
        <Toolbar>
          <StorefrontIcon sx={{ mr: 1 }} />
          <Typography variant="h6" sx={{ flexGrow: 1 }}>
            E-Commerce — Modular Monolith
          </Typography>
          {NAV.map((item) => (
            <Button
              key={item.to}
              color="inherit"
              component={NavLink}
              to={item.to}
              sx={{ textTransform: 'none' }}
            >
              {item.label}
            </Button>
          ))}
        </Toolbar>
      </AppBar>
      <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
        <Outlet />
      </Container>
    </Box>
  );
}
