import { NextResponse } from "next/server";
import { BOOKINGS } from "@/app/api/mock/data";
function parse(cookie?: string | null) {
  const t = cookie?.match(/evcc_token=([^;]+)/)?.[1];
  try { return t ? JSON.parse(Buffer.from(t, "base64").toString("utf8")) : null; } catch { return null; }
}
export async function GET(req: Request) {
  const user = parse(req.headers.get("cookie"));
  if (!user) return NextResponse.json({ error: "unauthenticated" }, { status: 401 });
  if (user.role === "admin" || user.role === "staff") return NextResponse.json({ bookings: BOOKINGS });
  return NextResponse.json({ bookings: BOOKINGS.filter(b => b.userId === user.id) });
}
