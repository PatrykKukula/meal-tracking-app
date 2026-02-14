import React from 'react';
import ReactDOM from 'react-dom/client';
import { App } from './app/App';
import './index.css';

// IMPORTANT: Removed React.StrictMode to prevent double initialization of Keycloak
// StrictMode causes useEffect to run twice in development, which breaks Keycloak init
ReactDOM.createRoot(document.getElementById('root')!).render(
  <App />
);
