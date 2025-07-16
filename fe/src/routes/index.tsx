import { createFileRoute } from "@tanstack/react-router";
import { Index } from "@/pages/dashboard";

export const Route = createFileRoute("/")({
  component: Index,
});


