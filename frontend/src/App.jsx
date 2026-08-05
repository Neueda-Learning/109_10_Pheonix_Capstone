import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { lazy, Suspense } from 'react';
import { ThemeProvider } from './context/ThemeContext';
import { ToastProvider } from './context/ToastContext';
import AppLayout from './components/layout/AppLayout';

const DashboardPage = lazy(() => import('./pages/DashboardPage'));
const CustomersPage = lazy(() => import('./pages/CustomersPage'));
const CustomerDetailPage = lazy(() => import('./pages/CustomerDetailPage'));
const InvestmentsPage = lazy(() => import('./pages/InvestmentsPage'));
const SuggestionsPage = lazy(() => import('./pages/SuggestionsPage'));

function App() {
  return (
    <ThemeProvider>
      <ToastProvider>
        <BrowserRouter>
          <AppLayout>
            <Suspense fallback={<div className="page-loading">Loading page…</div>}>
              <Routes>
                <Route path="/"              element={<DashboardPage />} />
                <Route path="/customers"     element={<CustomersPage />} />
                <Route path="/customers/:id" element={<CustomerDetailPage />} />
                <Route path="/investments"   element={<InvestmentsPage />} />
                <Route path="/suggestions"   element={<SuggestionsPage />} />
                <Route path="*"              element={<Navigate to="/" replace />} />
              </Routes>
            </Suspense>
          </AppLayout>
        </BrowserRouter>
      </ToastProvider>
    </ThemeProvider>
  );
}

export default App;
