import { create } from 'zustand';
import keycloak from '@/config/keycloak';
import { User } from '@/shared/types';

interface AuthState {
  isAuthenticated: boolean;
  user: User | null;
  isLoading: boolean;
  setAuthenticated: (authenticated: boolean) => void;
  setUser: (user: User | null) => void;
  setLoading: (loading: boolean) => void;
  hasRole: (role: string) => boolean;
  logout: () => void;
}

export const useAuthStore = create<AuthState>((set, get) => ({
  isAuthenticated: false,
  user: null,
  isLoading: true,

  setAuthenticated: (authenticated) => set({ isAuthenticated: authenticated }),
  
  setUser: (user) => set({ user }),
  
  setLoading: (loading) => set({ isLoading: loading }),

  hasRole: (role: string) => {
    const { user } = get();
    return user?.roles?.includes(role) || false;
  },

  logout: () => {
    keycloak.logout();
    set({ isAuthenticated: false, user: null });
  },
}));
