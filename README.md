AURUM CONVERSION OS

This bundle contains a combined front-end page, a JSON product structure, and backend implementations.

Run front-end:
Open index.html in a browser.
Use Download Project ZIP on the page to regenerate the bundle.

Run Python API:
cd python
python -m uvicorn server:app --host 0.0.0.0 --port 8001

Run TypeScript API:
cd typescript
npx tsc api-server.ts --target ES2020 --module commonjs
node api-server.js

Run Java API:
cd java
javac Main.java
java Main

Product structure:
product-structure.json is the canonical offer tree.

References

Microsoft. (n.d.). TypeScript documentation. Retrieved August 25, 2026, from https://www.typescriptlang.org/docs/
Mozilla Developer Network. (n.d.). JavaScript. Retrieved August 25, 2026, from https://developer.mozilla.org/en-US/docs/Web/JavaScript
Oracle. (n.d.). Java 21 documentation. Retrieved August 25, 2026, from https://docs.oracle.com/en/java/javase/21/
Python Software Foundation. (n.d.). Python 3 documentation. Retrieved August 25, 2026, from https://docs.python.org/3/
Ramírez, S. (n.d.). FastAPI. Retrieved August 25, 2026, from https://fastapi.tiangolo.com/
WHATWG. (n.d.). HTML Living Standard. Retrieved August 25, 2026, from https://html.spec.whatwg.org/
