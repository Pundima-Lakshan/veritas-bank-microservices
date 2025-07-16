import { AccountsList } from "@/components/templates/dashboard/accounts-list";
import { RecentUserTransactions } from "@/components/templates/transactions/recent-user-transactions";

export function Index() {
  return (
    <div className="grid grid-cols-1 gap-5 md:grid-cols-3">
      <div className="md:col-span-3">
        <AccountsList />
      </div>
      {/* <div className="md:col-span-1">
        <QuickActions />
      </div> */}
      <div className="md:col-span-3">
        <RecentUserTransactions />
      </div>
    </div>
  );
}
