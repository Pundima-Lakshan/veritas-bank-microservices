import { StrictMode } from 'react'
import ReactDOM from "react-dom/client";
import { createRouter, RouterProvider } from "@tanstack/react-router";

import "./index.css";

import { routeTree } from "./routeTree.gen";
import { AuthProvider } from "./providers/auth-provider";

export const router = createRouter({
  routeTree,
});

// Register the router instance for type safety
declare module "@tanstack/react-router" {
  interface Register {
    router: typeof router;
  }
}

const rootElement = document.getElementById("root")!;
if (!rootElement.innerHTML) {
  const root = ReactDOM.createRoot(rootElement);
  root.render(
    <StrictMode>
      <div className="h-full w-full font-display">
        <AuthProvider>
          <RouterProvider router={router} />
        </AuthProvider>
      </div>
    </StrictMode>
  );
}