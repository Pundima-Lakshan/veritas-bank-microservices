import { useAuth0 } from "@auth0/auth0-react";
import { Avatar, AvatarImage, AvatarFallback } from "../../ui/avatar";
import { Popover, PopoverTrigger, PopoverContent } from "../../ui/popover";
import { useState } from "react";
import { Button } from "@/components/ui/button";

export function UserAvatar() {
  const { user, isAuthenticated, isLoading, logout } = useAuth0();
  const [open, setOpen] = useState(false);

  if (isLoading) return null;
  if (!isAuthenticated || !user) return null;

  // Get initials for fallback
  const initials = user.name
    ? user.name
        .split(" ")
        .map((n) => n[0])
        .join("")
        .toUpperCase()
    : user.nickname?.[0]?.toUpperCase() || "U";

  return (
    <Popover open={open} onOpenChange={setOpen}>
      <PopoverTrigger asChild>
        <Button aria-label="User menu" type="button" variant="ghost">
          <span className="hidden sm:inline text-sm">
            {user.name || user.nickname || user.email}
          </span>
          <Avatar>
            {user.picture && (
              <AvatarImage src={user.picture} alt={user.name || "User"} />
            )}
            <AvatarFallback>{initials}</AvatarFallback>
          </Avatar>
        </Button>
      </PopoverTrigger>
      <PopoverContent align="end" className="w-fit p-2">
        <div className="flex flex-col items-start">
          <Button
            variant="destructive"
            onClick={() =>
              logout({ logoutParams: { returnTo: window.location.origin } })
            }
          >
            Log Out
          </Button>
        </div>
      </PopoverContent>
    </Popover>
  );
}
