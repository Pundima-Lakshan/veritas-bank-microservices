import { AppSidebar } from "@/components/templates/app-sidebar";
import { Separator } from "@/components/ui/separator";
import {
  SidebarInset,
  SidebarProvider,
  SidebarTrigger,
} from "@/components/ui/sidebar";
import { useAuth0 } from "@auth0/auth0-react";
import { createRootRoute, Outlet } from "@tanstack/react-router";
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
      <SidebarProvider>
        <AppSidebar />
        <SidebarInset>
          <header className="flex h-16 shrink-0 items-center gap-2 border-b px-4">
            <SidebarTrigger className="-ml-1" />
            <Separator
              orientation="vertical"
              className="mr-2 data-[orientation=vertical]:h-4"
            />
            Breadcrumbs?
          </header>
          <div className="p-5">
            <Outlet />
          </div>
        </SidebarInset>
      </SidebarProvider>
      {/* <TanStackRouterDevtools /> */}
    </>
  );
}
