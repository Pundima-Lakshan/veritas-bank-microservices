import { useAuth0 } from "@auth0/auth0-react";
import { createRootRoute, Outlet } from "@tanstack/react-router";
import { TanStackRouterDevtools } from "@tanstack/react-router-devtools";
import { useEffect } from "react";

export const Route = createRootRoute({
  component: Root,
});

function Root() {
  const { loginWithRedirect, isAuthenticated, isLoading } = useAuth0();

  useEffect(() => {
    if (isAuthenticated || isLoading) {
      return;
    }
    loginWithRedirect({
      authorizationParams: {
        redirect_uri: window.location.href,
      },
    });
  }, [loginWithRedirect, isAuthenticated, isLoading]);

  if (isLoading || !isAuthenticated) {
    return <div>Authenticating...</div>;
  }

  return (
    <>
      <Outlet />
      <TanStackRouterDevtools />
    </>
  );
}
