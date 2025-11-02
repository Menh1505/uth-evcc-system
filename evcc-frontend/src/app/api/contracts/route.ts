import { NextResponse } from "next/server";
import { CONTRACTS } from "@/app/api/mock/data";
export async function GET() { return NextResponse.json({ contracts: CONTRACTS }); }
