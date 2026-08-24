declare const require: (id: string) => any;
declare const process: any;
declare const console: any;

const http = require("http");

interface LeadPayload {
  name: string;
  email: string;
  budget: number;
  urgency: number;
  authority: number;
  source: string;
}

interface StoredLead extends LeadPayload {
  id: string;
  createdAt: string;
  score: number;
}

type RouteContext = {
  method: string;
  pathname: string;
};

const PORT = Number(process.env.PORT || 8002);
const EMAIL_PATTERN = /^[^@\s]+@[^@\s]+\.[^@\s]+$/;
const leads: StoredLead[] = [];

function uuid(): string {
  return "xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx".replace(/[xy]/g, function (c) {
    const r = Math.random() * 16;
    const v = c === "x" ? r : (r % 16) | 8;
    return Math.floor(v).toString(16);
  });
}

function clamp(value: number, min: number, max: number): number {
  return Math.min(max, Math.max(min, value));
}

function scoreLead(payload: LeadPayload): number {
  const budgetScore = Math.min(payload.budget / 100000, 1) * 40;
  const urgencyScore = clamp(payload.urgency, 1, 10) / 10 * 30;
  const authorityScore = clamp(payload.authority, 1, 10) / 10 * 20;
  const emailScore = EMAIL_PATTERN.test(payload.email) ? 10 : 0;
  return Math.round(budgetScore + urgencyScore + authorityScore + emailScore);
}

function normalizeLead(input: any): LeadPayload {
  return {
    name: String(input.name || "Anonymous"),
    email: String(input.email || ""),
    budget: Number(input.budget || 0),
    urgency: clamp(Number(input.urgency || 5), 1, 10),
    authority: clamp(Number(input.authority || 5), 1, 10),
    source: String(input.source || "web")
  };
}

function json(res: any, statusCode: number, body: any): void {
  const payload = JSON.stringify(body);
  res.writeHead(statusCode, {
    "Content-Type": "application/json",
    "Access-Control-Allow-Origin": "*",
    "Access-Control-Allow-Methods": "GET,POST,OPTIONS",
    "Access-Control-Allow-Headers": "Content-Type"
  });
  res.end(payload);
}

function readBody(req: any): Promise<string> {
  return new Promise(function (resolve, reject) {
    let data = "";
    req.on("data", function (chunk: any) {
      data += chunk.toString("utf8");
    });
    req.on("end", function () {
      resolve(data);
    });
    req.on("error", reject);
  });
}

const server = http.createServer(async function (req: any, res: any) {
  const urlPath = String(req.url || "/").split("?")[0];
  const context: RouteContext = { method: req.method, pathname: urlPath };

  if (context.method === "OPTIONS") {
    json(res, 204, {});
    return;
  }

  if (context.method === "GET" && context.pathname === "/api/health") {
    json(res, 200, { status: "ok", service: "aurum-typescript-api" });
    return;
  }

  if (context.method === "GET" && context.pathname === "/api/leads") {
    json(res, 200, leads);
    return;
  }

  if (context.method === "POST" && context.pathname === "/api/leads") {
    try {
      const raw = await readBody(req);
      const parsed = raw ? JSON.parse(raw) : {};
      const leadPayload = normalizeLead(parsed);
      if (!EMAIL_PATTERN.test(leadPayload.email)) {
        json(res, 422, { detail: "email must be valid" });
        return;
      }
      const stored: StoredLead = {
        ...leadPayload,
        id: uuid(),
        createdAt: new Date().toISOString(),
        score: scoreLead(leadPayload)
      };
      leads.push(stored);
      json(res, 201, stored);
    } catch (error) {
      json(res, 400, { detail: "invalid JSON payload" });
    }
    return;
  }

  json(res, 404, { detail: "route not found" });
});

server.listen(PORT, function () {
  console.log("Aurum TypeScript API listening on port " + PORT);
});
