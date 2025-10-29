import { NextResponse } from "next/server";
import { USERS } from "@/app/api/mock/data";

export async function POST(req: Request) {
  const body = await req.json().catch(()=>({}));
  const { email, role } = body;
  let user = USERS.find(u => u.email === email);
  if (!user) user = USERS.find(u => u.role === role) ?? USERS[0];
  const payload = { id: user.id, name: user.name, email: user.email, role: user.role };
  const token = Buffer.from(JSON.stringify(payload)).toString("base64"); // demo
  return NextResponse.json({ token, user: payload });
}
