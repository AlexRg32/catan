# Design
- Añadir `CorsConfigurationSource` en `SecurityConfig` para permitir origen web local configurable por env.
- Mantener seguridad JWT actual y permitir preflight/headers estándar.
- Desactivar tracing OTLP por defecto en local para eliminar ruido de logs.
- Ajustar script de desarrollo backend para saltar checkstyle/spotless durante `spring-boot:run`.
