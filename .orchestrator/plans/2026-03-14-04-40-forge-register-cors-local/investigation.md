# Investigation
- Síntoma reportado: al registrar usuario desde frontend, el backend mostraba logs con warnings y error OTLP.
- Verificación API: `POST /api/auth/register` responde `200` por curl.
- Verificación navegador/CORS: preflight `OPTIONS` desde `http://localhost:5173` devolvía `403 Invalid CORS request`.
- Checkstyle warnings `MissingJavadoc*` aparecen como `WARN` y no bloquean arranque (`0 violations`).
- Error OTLP `localhost:4318` es de observabilidad local sin collector.
