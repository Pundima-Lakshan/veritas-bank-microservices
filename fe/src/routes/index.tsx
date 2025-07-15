import { createFileRoute } from "@tanstack/react-router";
import { AccountSummary } from "@/components/templates/index/account-summary";
import { QuickActions } from "@/components/templates/index/quick-actions";
import { RecentTransactions } from "@/components/templates/index/recent-transactions";

export const Route = createFileRoute("/")({
  component: Index,
});

function Index() {
  return (
    <div className="grid grid-cols-1 gap-5 md:grid-cols-3">
      <AccountSummary />
      <QuickActions />
      <RecentTransactions />
    </div>
  );
}
