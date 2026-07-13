# :server

The **Ktor (Netty, JVM)** backend for the task tracker. Depends on `:shared` and reuses its
`Task` / `TaskPriority` models for JSON serialization.

## API

In-memory task store (a `MutableList<Task>`), JSON via `ContentNegotiation`:

- `GET  /api/ping` → `pong` (health check; the client polls this)
- `GET  /api/tasks` → `List<Task>` as JSON
- `POST /api/tasks` → create; server assigns an `id` when the client sends `id = 0`
- `DELETE /api/tasks/{id}` → remove by id (404 if absent)

## Build / run

```bash
./gradlew :server:run          # starts on 0.0.0.0:SERVER_PORT (8080, from :shared Constants)
./gradlew :server:build
```
Quick check: `curl http://127.0.0.1:8080/api/tasks`.

## Notes

- Independent of the `buildVariant` — it's a plain JVM module and always builds.
- Storage is in-memory (lost on restart). Persistence (SQLite/Exposed) is planned but not implemented.
- Clients reach it via `BaseUrlProvider` (Android emulator `10.0.2.2`, iOS sim `127.0.0.1`, real
  device `AURORA_DEVICE_IP`/`SERVER_IP`); see `NETWORK_CONFIG_README.md`.
