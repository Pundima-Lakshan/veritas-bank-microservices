import { AccountsList } from "@/components/templates/index/accounts-list";
import { QuickActions } from "@/components/templates/index/quick-actions";

export function Index() {
  return (
    <div className="grid grid-cols-1 gap-5 md:grid-cols-3">
      <div className="md:col-span-2">
        <AccountsList />
      </div>
      <div className="md:col-span-1">
        <QuickActions />
      </div>
    </div>
  );
}
