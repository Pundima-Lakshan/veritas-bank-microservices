import { Auth0Provider } from "@auth0/auth0-react";

export function AuthProvider({ children }: { children: React.ReactNode }) {
  return (
    <Auth0Provider
      domain="dev-wf5dh4s12xzurd8b.us.auth0.com"
      clientId="NGr0mARxhJYFWJIq8t4vwKqvTTmBn7B3"
      authorizationParams={{
        redirect_uri: window.location.origin,
      }}
    >
      {children}
    </Auth0Provider>
  );
}
