import { NextResponse } from "next/server";
import type { NextRequest } from "next/server";

function parseToken(tok?: string | null) {
  if (!tok) return null;
  try { return JSON.parse(Buffer.from(tok, "base64").toString("utf8")); } catch { return null; }
}

export function middleware(req: NextRequest) {
  const cookie = req.headers.get("cookie") || "";
  const token = cookie.match(/evcc_token=([^;]+)/)?.[1];
  const user = parseToken(token ?? null);

  const url = req.nextUrl;
  const path = url.pathname;
  const protectedPaths = ["/app", "/staff", "/admin"];
  const isProtected = protectedPaths.some(p => path.startsWith(p));

  if (isProtected && !user) { url.pathname = "/login"; return NextResponse.redirect(url); }
  if (path.startsWith("/admin") && user?.role !== "admin") { url.pathname = "/app"; return NextResponse.redirect(url); }
  if (path.startsWith("/staff") && !["staff","admin"].includes(user?.role ?? "")) { url.pathname = "/app"; return NextResponse.redirect(url); }

  return NextResponse.next();
}

export const config = { matcher: ["/app/:path*", "/staff/:path*", "/admin/:path*"] };
