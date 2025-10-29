import { NextResponse } from "next/server";
function parseToken(tok?: string | null) {
  if (!tok) return null;
  try { return JSON.parse(Buffer.from(tok, "base64").toString("utf8")); } catch { return null; }
}
export async function GET(req: Request) {
  const cookie = req.headers.get("cookie") || "";
  const token = cookie.match(/evcc_token=([^;]+)/)?.[1];
  const user = parseToken(token ?? null);
  if (!user) return NextResponse.json({ error: "unauthenticated" }, { status: 401 });
  return NextResponse.json({ user });
}
