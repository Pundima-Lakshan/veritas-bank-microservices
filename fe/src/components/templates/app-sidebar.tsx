import {
  Sidebar,
  SidebarContent,
  SidebarGroup,
  SidebarGroupContent,
  SidebarGroupLabel,
  SidebarHeader,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
  SidebarRail,
} from "@/components/ui/sidebar";
import type { SidebarItems } from "@/lib/sidebar-items";
import { Link } from "@tanstack/react-router";

export function AppSidebar({
  sidebarItems,
  ...props
}: React.ComponentProps<typeof Sidebar> & { sidebarItems: SidebarItems }) {
  return (
    <Sidebar {...props}>
      <SidebarHeader className="text-primary font-bold text-center">
        Veritas Banking
      </SidebarHeader>
      <SidebarContent>
        {sidebarItems.navMain.map((item) => (
          <SidebarGroup key={item.title}>
            <SidebarGroupLabel>{item.title}</SidebarGroupLabel>
            <SidebarGroupContent>
              <SidebarMenu>
                {item.items.map((item) => (
                  <SidebarMenuItem key={item.title}>
                    <SidebarMenuButton asChild>
                      <Link
                        to={item.url}
                        className="[&.active]:text-primary [&.active]:font-medium"
                      >
                        {item.title}
                      </Link>
                    </SidebarMenuButton>
                  </SidebarMenuItem>
                ))}
              </SidebarMenu>
            </SidebarGroupContent>
          </SidebarGroup>
        ))}
      </SidebarContent>
      <SidebarRail />
    </Sidebar>
  );
}
