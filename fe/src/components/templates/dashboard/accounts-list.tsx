import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/card";
import { Link } from "@tanstack/react-router";
import { useGetAccounts } from "@/services/queries";

function maskAccountNumber(number: string) {
  return number.replace(/\d(?=\d{4})/g, "*");
}

export function AccountsList() {
  const { data: accounts, isLoading, error } = useGetAccounts();

  if (isLoading) {
    return <div>Loading accounts...</div>;
  }

  if (error) {
    return <div>Error loading accounts.</div>;
  }

  if (!accounts || accounts.length === 0) {
    return <div>No accounts found.</div>;
  }

  return (
    <div className="grid grid-cols-1 gap-4 md:grid-cols-2 lg:grid-cols-3">
      {accounts.map((account) => {
        return (
          <Link
            to="."
            key={account.id}
            className="block hover:shadow-lg transition-shadow cursor-pointer"
          >
            <Card>
              <CardHeader>
                <CardTitle>{account.accountName}</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="space-y-1">
                  <div>
                    <span className="font-medium">Number:</span>{" "}
                    {maskAccountNumber(account.accountNumber)}
                  </div>
                  <div>
                    <span className="font-medium">Balance:</span>
                    {account.balance.toLocaleString()}
                  </div>
                  <div>
                    <span className="font-medium">Currency:</span>{" "}
                    {account.currency}
                  </div>
                </div>
              </CardContent>
            </Card>
          </Link>
        );
      })}
    </div>
  );
}
