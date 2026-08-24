# Aurum Conversion OS

A closed-loop presentation layer and lead intake system.

The page functions as a premium showroom: it presents the product, scores the lead, models revenue, recommends an offer, and exports the full project bundle. The backend files provide independent runtime implementations for lead capture and health verification.

---

## Bundle Contents

```text
aurum-conversion/
├── index.html
├── product-structure.json
├── README.md
├── python/
│   └── server.py
├── typescript/
│   └── api-server.ts
└── java/
    └── Main.java
```

---

## Front-End Execution

Open `index.html` directly in a browser.

The page includes:

- Combined HTML, CSS, and JavaScript
- Lead scoring form
- Revenue modeling panel
- Product structure renderer
- Offer ladder renderer
- ZIP export function
- Local lead persistence through `localStorage`
- Optional API submission when served over HTTP

To serve the page locally:

```bash
python -m http.server 8080
```

Then open:

```text
http://localhost:8080
```

Use the `Download Project ZIP` button on the page to regenerate the project bundle.

---

## Python API

Runtime target:

```text
Python 3
FastAPI
Uvicorn
```

Install dependencies:

```bash
python -m pip install fastapi uvicorn
```

Run from the `python` directory:

```bash
cd python
python -m uvicorn server:app --host 0.0.0.0 --port 8001
```

Endpoints:

```text
GET  /api/health
POST /api/leads
GET  /api/leads
POST /api/orders
```

The Python API includes lead scoring, validation, in-memory storage, and order creation against an existing lead.

---

## TypeScript API

Runtime target:

```text
Node.js
TypeScript
```

Install TypeScript:

```bash
npm install typescript
```

Compile and run from the `typescript` directory:

```bash
cd typescript
npx tsc api-server.ts --target ES2020 --module commonjs --lib ES2020
node api-server.js
```

Default port:

```text
8002
```

Override port:

```bash
PORT=9002 node api-server.js
```

Endpoints:

```text
GET  /api/health
POST /api/leads
GET  /api/leads
```

The TypeScript API uses Node built-in HTTP primitives and does not require runtime dependencies.

---

## Java API

Runtime target:

```text
JDK 17 or later
```

Compile and run from the `java` directory:

```bash
cd java
javac Main.java
java Main
```

Default port:

```text
8003
```

Custom port:

```bash
java Main 9003
```

Endpoints:

```text
GET  /api/health
POST /api/leads
GET  /api/leads
```

The Java API uses the built-in `com.sun.net.httpserver.HttpServer` interface and requires no external dependencies.

---

## Product Structure

`product-structure.json` is the canonical offer tree.

It contains:

```text
name
positioning
audience
stack
modules
offers
kpis
```

The front end reads this structure and renders:

- Product modules
- Offer cards
- Pricing blocks
- Feature lists
- Recommended engagement path

---

## Conversion Algorithm

### Lead Score

The lead score is weighted as follows:

```text
Budget     40 points
Urgency    30 points
Authority  20 points
Email      10 points
Total     100 points
```

Budget score is capped at a $100,000 budget.

```text
budget_score = min(budget / 100000, 1) * 40
urgency_score = urgency / 10 * 30
authority_score = authority / 10 * 20
email_score = 10 if email is valid
```

### Revenue Model

The revenue model uses:

```text
traffic
conversion_rate
average_order_value
repeat_rate
profit_margin
```

Calculation:

```text
first_purchase_revenue = traffic * conversion_rate * average_order_value
lifecycle_multiplier = 1 + repeat_rate * 1.8
gross_revenue = first_purchase_revenue * lifecycle_multiplier
net_revenue = gross_revenue * profit_margin
```

The recommended offer is selected from the net revenue output.

---

## Export Behavior

The ZIP export includes:

```text
index.html
product-structure.json
python/server.py
typescript/api-server.ts
java/Main.java
README.md
```

The ZIP is generated in-browser without external libraries.

---

## Operational Notes

- The front end works as a standalone static page.
- API submission is attempted only when the page is served over HTTP.
- Lead records are also stored locally in the browser.
- CORS headers are enabled for local integration.
- JSON output is escaped in the Java implementation.
- Input values are clamped in the front-end and TypeScript scoring models.

---

## References

Microsoft. (n.d.). TypeScript documentation. Retrieved August 25, 2026, from https://www.typescriptlang.org/docs/

Mozilla Developer Network. (n.d.). JavaScript. Retrieved August 25, 2026, from https://developer.mozilla.org/en-US/docs/Web/JavaScript

Oracle. (n.d.). Java SE 21 documentation. Retrieved August 25, 2026, from https://docs.oracle.com/en/java/javase/21/

Python Software Foundation. (n.d.). Python 3 documentation. Retrieved August 25, 2026, from https://docs.python.org/3/

Ramírez, S. (n.d.). FastAPI. Retrieved August 25, 2026, from https://fastapi.tiangolo.com/

WHATWG. (n.d.). HTML Living Standard. Retrieved August 25, 2026, from https://html.spec.whatwg.org/
```
