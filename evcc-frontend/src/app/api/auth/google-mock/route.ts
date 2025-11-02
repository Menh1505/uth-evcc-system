import { NextResponse } from "next/server";
export async function POST() {
  const payload = { id: "google-u1", name: "Google User", email: "you@gmail.com", role: "user", provider: "google" };
  const token = Buffer.from(JSON.stringify(payload)).toString("base64");
  return NextResponse.json({ token, user: payload });
}
