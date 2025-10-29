"use client";

import { useRouter } from "next/navigation";
import { z } from "zod";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";

const Schema = z.object({
  email: z.string().email().optional(),
  password: z.string().min(1).optional(),
  role: z.enum(["user","staff","admin"]).optional()
});
type FormData = z.infer<typeof Schema>;

export default function LoginForm() {
  const router = useRouter();
  const form = useForm<FormData>({
    resolver: zodResolver(Schema),
    defaultValues: { email: "", password: "", role: "user" as any }
  });

  const onSubmit = form.handleSubmit(async (data) => {
    const res = await fetch("/api/auth/login", {
      method: "POST", headers: { "content-type": "application/json" },
      body: JSON.stringify(data),
    });
    const j = await res.json();
    if (res.ok && j.token) {
      document.cookie = `evcc_token=${j.token}; path=/; max-age=86400`;
      const role = j.user?.role ?? data.role;
      if (role === "admin") router.replace("/admin");
      else if (role === "staff") router.replace("/staff");
      else router.replace("/app");
    } else {
      alert("Login failed (demo)");
    }
  });

  const loginWithGoogle = async () => {
    const res = await fetch("/api/auth/google-mock", { method: "POST" });
    const j = await res.json();
    if (res.ok && j.token) {
      document.cookie = `evcc_token=${j.token}; path=/; max-age=86400`;
      router.replace("/app");
    } else {
      alert("Google login (demo) lỗi");
    }
  };

  return (
    <div className="space-y-5">
      <form onSubmit={onSubmit} className="space-y-4">
        <div className="space-y-2">
          <Label htmlFor="email">Email</Label>
          <Input id="email" placeholder="you@example.com" {...form.register("email")} />
        </div>
        <div className="space-y-2">
          <Label htmlFor="password">Mật khẩu</Label>
          <Input id="password" type="password" {...form.register("password")} />
        </div>
        <div className="space-y-2">
          <Label>Vai trò (demo)</Label>
          <select className="w-full border rounded-md h-10 px-3" {...form.register("role")}>
            <option value="user">User</option>
            <option value="staff">Staff</option>
            <option value="admin">Admin</option>
          </select>
        </div>
        <Button type="submit" className="w-full h-10 rounded-xl font-semibold transition-transform active:scale-95">
          Đăng nhập
        </Button>
      </form>

      <div className="flex items-center gap-3">
        <div className="h-px flex-1 bg-border" /><span className="text-xs text-muted-foreground">hoặc</span><div className="h-px flex-1 bg-border" />
      </div>

      <button
        onClick={loginWithGoogle}
        className="w-full h-10 rounded-xl border flex items-center justify-center gap-2
                   bg-white hover:bg-white/90 transition-[transform,background] active:scale-95"
      >
        {/* Google icon inline */}
        <svg width="18" height="18" viewBox="0 0 533.5 544.3" aria-hidden><path fill="#4285f4" d="M533.5 278.4c0-18.5-1.5-37.1-4.6-55.1H272v104.4h147.3c-6.4 34.9-26 64.2-55.5 83.9v69.7h89.8c52.6-48.4 79.9-119.8 79.9-203z"/><path fill="#34a853" d="M272 544.3c72.5 0 133.5-24 178-65.1l-89.8-69.7c-24.9 16.7-56.9 26.6-88.2 26.6-67.7 0-125.2-45.6-145.8-106.7H35.1v67.1c44.5 88.2 135.6 147.8 236.9 147.8z"/><path fill="#fbbc04" d="M126.2 329.4c-10.3-30.9-10.3-64.2 0-95.1V167.2H35.1c-44.6 88.9-44.6 192.1 0 281l91.1-118.8z"/><path fill="#ea4335" d="M272 107.7c37.5-.6 73.5 13.5 101.1 39.6l75.6-75.6C403.5 25.4 339.3.3 272 0 170.8 0 79.7 59.6 35.1 147.8l91.1 66.5C146.9 153.3 204.4 107.7 272 107.7z"/></svg>
        <span className="font-medium">Tiếp tục với Google</span>
      </button>
    </div>
  );
}
