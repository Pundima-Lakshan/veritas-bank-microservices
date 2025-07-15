import { AppBreadcrumb } from "@/components/templates/app-breadcrumb";
import { AppSidebar } from "@/components/templates/app-sidebar";
import { UserAvatar } from "@/components/templates/auth/user-avatar";
import { Separator } from "@/components/ui/separator";
import {
  SidebarInset,
  SidebarProvider,
  SidebarTrigger,
} from "@/components/ui/sidebar";
import { breadcrumbItems } from "@/lib/breadcrumb-items";
import { sidebarItems } from "@/lib/sidebar-items";
import { useAuthStore, useAuthStoreActions } from "@/stores/auth-store";
import { useAuth0 } from "@auth0/auth0-react";
import { Outlet } from "@tanstack/react-router";
import { useEffect } from "react";

export function Root() {
  const {
    loginWithRedirect,
    isAuthenticated,
    isLoading,
    getAccessTokenSilently,
  } = useAuth0();

  const { setAccessToken } = useAuthStoreActions();
  const accessToken = useAuthStore((state) => state.accessToken);

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

  useEffect(() => {
    if (!isAuthenticated || isLoading) {
      return;
    }
    getAccessTokenSilently().then((value) => {
      setAccessToken(value);
    });
  }, [getAccessTokenSilently, isAuthenticated, isLoading, setAccessToken]);

  if (isLoading || !isAuthenticated || !accessToken) {
    return <div>Authenticating...</div>;
  }

  return (
    <>
      <SidebarProvider defaultOpen={false}>
        <AppSidebar sidebarItems={sidebarItems} />
        <SidebarInset>
          <header className="flex h-16 shrink-0 items-center gap-2 border-b px-4">
            <SidebarTrigger className="-ml-1" />
            <Separator
              orientation="vertical"
              className="mr-2 data-[orientation=vertical]:h-4"
            />
            <div className="flex-1 flex items-center gap-2">
              <AppBreadcrumb items={breadcrumbItems} />
            </div>
            <div className="ml-auto">
              <UserAvatar />
            </div>
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
