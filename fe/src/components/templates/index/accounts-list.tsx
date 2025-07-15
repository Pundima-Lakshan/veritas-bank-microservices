import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/card";
import { Link } from "@tanstack/react-router";

// Minimal type for account
interface Account {
  id: string;
  name: string;
  balance: number;
  number: string;
  type: string;
}

function maskAccountNumber(number: string) {
  return number.replace(/\d(?=\d{4})/g, "*");
}

const accounts: Account[] = [
  {
    id: "1",
    name: "Checking Account",
    balance: 5234.56,
    number: "1234567890123456",
    type: "Checking",
  },
  {
    id: "2",
    name: "Savings Account",
    balance: 10450.12,
    number: "9876543210987654",
    type: "Savings",
  },
  {
    id: "3",
    name: "Business Account",
    balance: 25000.0,
    number: "1111222233334444",
    type: "Business",
  },
];

export function AccountsList() {
  return (
    <div className="grid grid-cols-1 gap-4 md:grid-cols-2 lg:grid-cols-3">
      {accounts.map((account) => (
        <Link
          to="."
          key={account.id}
          className="block hover:shadow-lg transition-shadow cursor-pointer"
        >
          <Card>
            <CardHeader>
              <CardTitle>{account.name}</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="space-y-1">
                <div>
                  <span className="font-medium">Balance:</span> $
                  {account.balance.toLocaleString()}
                </div>
                <div>
                  <span className="font-medium">Number:</span>{" "}
                  {maskAccountNumber(account.number)}
                </div>
                <div>
                  <span className="font-medium">Type:</span> {account.type}
                </div>
              </div>
            </CardContent>
          </Card>
        </Link>
      ))}
    </div>
  );
}
