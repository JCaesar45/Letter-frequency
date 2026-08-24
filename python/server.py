import re
import uuid
from datetime import datetime, timezone
from typing import Any, Dict, List

from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, Field

EMAIL_PATTERN = r"^[^@\s]+@[^@\s]+\.[^@\s]+$"

app = FastAPI(title="Aurum Conversion API", version="1.0.0")
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

LEADS: List[Dict[str, Any]] = []
ORDERS: List[Dict[str, Any]] = []


class LeadPayload(BaseModel):
    name: str = Field(min_length=1, max_length=120)
    email: str = Field(min_length=5, max_length=254)
    budget: float = Field(default=0, ge=0)
    urgency: int = Field(default=5, ge=1, le=10)
    authority: int = Field(default=5, ge=1, le=10)
    source: str = Field(default="web")


class OrderPayload(BaseModel):
    offer_id: str = Field(min_length=1, max_length=60)
    lead_id: str = Field(min_length=1, max_length=60)
    amount: float = Field(ge=0)


def score_lead(payload: LeadPayload) -> int:
    budget_score = min(payload.budget / 100000, 1) * 40
    urgency_score = payload.urgency / 10 * 30
    authority_score = payload.authority / 10 * 20
    email_score = 10 if re.match(EMAIL_PATTERN, payload.email) else 0
    return int(round(budget_score + urgency_score + authority_score + email_score))


@app.get("/api/health")
def health() -> Dict[str, str]:
    return {"status": "ok", "service": "aurum-conversion-api"}


@app.post("/api/leads", status_code=201)
def create_lead(payload: LeadPayload) -> Dict[str, Any]:
    if not re.match(EMAIL_PATTERN, payload.email):
        raise HTTPException(status_code=422, detail="email must be valid")
    lead = {
        "id": str(uuid.uuid4()),
        "created_at": datetime.now(timezone.utc).isoformat(),
        "score": score_lead(payload),
        **payload.dict(),
    }
    LEADS.append(lead)
    return lead


@app.get("/api/leads")
def list_leads() -> List[Dict[str, Any]]:
    return LEADS


@app.post("/api/orders", status_code=201)
def create_order(payload: OrderPayload) -> Dict[str, Any]:
    if not any(item["id"] == payload.lead_id for item in LEADS):
        raise HTTPException(status_code=404, detail="lead not found")
    order = {
        "id": str(uuid.uuid4()),
        "created_at": datetime.now(timezone.utc).isoformat(),
        **payload.dict(),
    }
    ORDERS.append(order)
    return order


if __name__ == "__main__":
    import uvicorn

    uvicorn.run(app, host="0.0.0.0", port=8001)
