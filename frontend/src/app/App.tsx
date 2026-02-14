import React, { useEffect, useState } from 'react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { Toaster } from 'react-hot-toast';
import keycloak from '@/config/keycloak';
import { useAuthStore } from '@/shared/store/authStore';
import { AppRouter } from './AppRouter';
import { Loading } from '@/shared/components';

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      refetchOnWindowFocus: false,
      retry: 1,
      staleTime: 5 * 60 * 1000, // 5 minutes
    },
  },
});

// Global flag to prevent double initialization (React StrictMode issue)
let isKeycloakInitializing = false;
let isKeycloakInitialized = false;

export const App: React.FC = () => {
  const [keycloakReady, setKeycloakReady] = useState(false);
  const { setAuthenticated, setUser, setLoading } = useAuthStore();

  useEffect(() => {
    // Prevent double initialization in React StrictMode
    if (isKeycloakInitializing || isKeycloakInitialized) {
      setKeycloakReady(true);
      setLoading(false);
      return;
    }

    isKeycloakInitializing = true;

    const initKeycloak = async () => {
      try {
        console.log('Initializing Keycloak...');
        
        const authenticated = await keycloak.init({
          onLoad: 'check-sso',
          checkLoginIframe: false,
          pkceMethod: 'S256',
          // Enable silent SSO check for better UX
          silentCheckSsoRedirectUri: window.location.origin + '/silent-check-sso.html',
        });

        isKeycloakInitialized = true;
        isKeycloakInitializing = false;

        console.log('Keycloak initialized, authenticated:', authenticated);

        setAuthenticated(authenticated);

        if (authenticated && keycloak.tokenParsed) {
          const user = {
            username: keycloak.tokenParsed.preferred_username || '',
            email: keycloak.tokenParsed.email,
            roles: keycloak.tokenParsed.realm_access?.roles || [],
          };
          setUser(user);

          console.log('User authenticated:', user.username);
          console.log('Token expires at:', keycloak.tokenParsed.exp ? 
            new Date(keycloak.tokenParsed.exp * 1000).toLocaleString() : 'unknown');

          // Set up automatic token refresh
          const refreshInterval = setInterval(() => {
            keycloak.updateToken(70)
              .then((refreshed) => {
                if (refreshed) {
                  console.log('Token refreshed successfully');
                  // Update user state if token was refreshed
                  if (keycloak.tokenParsed) {
                    const updatedUser = {
                      username: keycloak.tokenParsed.preferred_username || '',
                      email: keycloak.tokenParsed.email,
                      roles: keycloak.tokenParsed.realm_access?.roles || [],
                    };
                    setUser(updatedUser);
                    setAuthenticated(true);
                  }
                }
              })
              .catch(() => {
                console.error('Failed to refresh token');
                clearInterval(refreshInterval);
              });
          }, 60000);

          // Token expired handler
          keycloak.onTokenExpired = () => {
            console.log('Token expired, attempting refresh...');
            keycloak.updateToken(30)
              .catch(() => {
                console.error('Token refresh failed after expiration');
                setAuthenticated(false);
                setUser(null);
              });
          };

          // Auth refresh error handler
          keycloak.onAuthRefreshError = () => {
            console.error('Authentication refresh error');
            setAuthenticated(false);
            setUser(null);
          };

          // Auth success handler
          keycloak.onAuthSuccess = () => {
            console.log('Authentication successful');
          };
        }

        setKeycloakReady(true);
        setLoading(false);
      } catch (error) {
        console.error('Failed to initialize Keycloak:', error);
        isKeycloakInitialized = true;
        isKeycloakInitializing = false;
        setKeycloakReady(true);
        setLoading(false);
      }
    };

    initKeycloak();
  }, [setAuthenticated, setUser, setLoading]);

  if (!keycloakReady) {
    return (
      <div className="flex items-center justify-center min-h-screen bg-gradient-to-br from-gray-50 to-primary-50/20">
        <Loading size="lg" text="Initializing application..." />
      </div>
    );
  }

  return (
    <QueryClientProvider client={queryClient}>
      <AppRouter />
      <Toaster
        position="top-center"
        toastOptions={{
          duration: 4000,
          style: {
            background: '#fff',
            color: '#111827',
            padding: '16px',
            borderRadius: '12px',
            boxShadow: '0 10px 15px -3px rgba(0, 0, 0, 0.1)',
          },
          success: {
            iconTheme: {
              primary: '#22c55e',
              secondary: '#fff',
            },
          },
          error: {
            iconTheme: {
              primary: '#ef4444',
              secondary: '#fff',
            },
          },
        }}
      />
    </QueryClientProvider>
  );
};
