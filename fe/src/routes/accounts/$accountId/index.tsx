import { Account } from "@/pages/account";
import { createFileRoute } from "@tanstack/react-router";

export const Route = createFileRoute("/accounts/$accountId/")({
  component: Component,
});

function Component() {
  const { accountId } = Route.useParams();
  return <Account accountId={accountId} />;
}
