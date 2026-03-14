# AGENTS.md

## Propósito
Este repositorio se opera **siempre** con los workflows de `.agent`.
La prioridad es mantener consistencia, trazabilidad y seguridad en cada tarea.

## Carga obligatoria al iniciar cada tarea
1. Leer este archivo (`AGENTS.md`).
2. Leer `.agent/MANUAL.md`.
3. Aplicar reglas globales de `.agent/rules/global.md`.

## Router de workflows (siempre activo)
- Si el usuario pide análisis, diagnóstico, deuda técnica, seguridad o revisión general:
  - Ejecutar workflow `audit` de `.agent/workflows/audit.md`.
  - Modo estricto **read-only** (solo escribir en `.orchestrator/audits/`).
- Si el usuario pide plan, arquitectura, diseño o estrategia antes de tocar código:
  - Ejecutar workflow `think` de `.agent/workflows/think.md`.
  - Modo estricto **read-only** (sin cambios en código fuente).
- Si el usuario pide implementar, arreglar, refactorizar o construir:
  - Ejecutar workflow `forge` de `.agent/workflows/forge.md`.
  - Implementar por fases con checkpoints y verificación.

## Política de ejecución
- No pedir permisos innecesarios para acciones del flujo (`forge`, `think`, `audit`) dentro de `.orchestrator`.
- **Prohibido** hacer `git commit` o `git push` automáticamente durante `forge`.
- Commits/push solo en workflows manuales (`ship`/`pr`) cuando el usuario lo pida explícitamente.

## Convenciones de respuesta
- Responder en español claro, directo y accionable.
- Informar progreso de forma breve durante tareas largas.
- Al finalizar: resumir qué se hizo, qué se verificó y próximos pasos.

## Regla de oro
Ante duda, priorizar este orden:
1. Seguridad y reglas globales.
2. Workflow correcto (`audit` / `think` / `forge`).
3. Trazabilidad en `.orchestrator`.
