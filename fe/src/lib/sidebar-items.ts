export type SidebarNavItem = {
  title: string;
  url: string;
};

export type SidebarNavGroup = {
  title: string;
  url: string;
  items: SidebarNavItem[];
};

export type SidebarItems = {
  navMain: SidebarNavGroup[];
};

export const sidebarItems: SidebarItems = {
  navMain: [
    {
      title: "Main",
      url: "#",
      items: [
        {
          title: "Home",
          url: "/",
        },
      ],
    },
    {
      title: "Account",
      url: "#",
      items: [
        {
          title: "Portfolio",
          url: "/about",
        },
      ],
    },
  ],
};
