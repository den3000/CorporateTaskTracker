# Plan: Mobile App & Server Integration (Task Tracker) - Iterative Approach

**Goal:** Establish communication between the Compose Multiplatform app and the Ktor server using
the existing `shared` module models, moving from an in-memory server to a persistent SQLite backend.

## ✅ Phase 1: Shared Model Integration (DONE)
*Target: `server/` dependency on `shared`*

1.  **Dependency Setup:** Configure `server/build.gradle.impl` to depend on the `shared` module.
2.  **Code Reuse:** Ensure `server` uses the exact same `Task` and `TaskPriority` classes from `shared/src/commonMain` for both logic and serialization.

## ✅ Phase 2: Basic Backend Draft (Text-based) (DONE)
*Target: `server/` - API Endpoints only*

1.  **Endpoint Development:** Implement Ktor routes using a simple `MutableList<Task>` in-memory storage:
    *   `GET /api/tasks`: Return the list of tasks as JSON.
    *   `POST /api/tasks`: Add a task to the list.
    *   `DELETE /api/tasks/{id}`: Remove a task by ID.
2.  **Serialization:** Use manual parsing via `Json.decodeFromString` and `toString` for now (due to lack of internet). `ContentNegotiation` will be configured later.
3.  **Verification:** Use `curl` to manually trigger requests and verify the server responds with the correct JSON structure.

## 📱 Phase 3: Client-Server Communication (End-to-End)
*Target: `composeApp/` - Integration with existing Ktor Client*

1.  **Repository Update:** Modify `LocalTaskRepository` (or the relevant data layer) to call the `RemoteTaskDataSource` (using the already configured Ktor client) during any write/read operation.
2.  **Flow Verification:**
    *   Trigger a task creation in the UI.
    *   Monitor server logs to confirm the `POST` request arrived.
    *   Verify that the task is visible in the `GET /api/tasks` response.
3.  **Stability Check:** Ensure no crashes occur during network timeouts or malformed responses.

## 🚀 Phase 4: Content Negotiation Setup
*Target: `server/` - Proper Serialization (Requires Internet)*

1.  **Dependency Update:** Download and configure `kotlinx.serialization` and Ktor `ContentNegotiation`.
2.  **JSON Migration:** Switch the communication protocol from manual parsing to JSON via `ContentNegotiation`.

## 💾 Phase 5: Server Persistence (SQLite with Exposed)
*Target: `server/` - Adding Database Layer*

1.  **Database Setup:** Integrate **Exposed Framework** with **SQLite** (local file-based) to replace the `MutableList`.
2.  **Schema Mapping:** Map `shared.Task` properties to an Exposed `Table` definition.
3.  **DAO/Service Layer:** Replace the in-memory list logic with SQL queries (CRUD) using Exposed.
4.  **Final Integration:** Ensure the `server` routes now interact with the SQLite database instead of the list.

## 🧪 Phase 6: Robustness & Error Handling
1.  **Offline Handling:** Implement logic to handle `ConnectException` in the client (return to `local` mode).
2.  **Error Mapping:** Ensure server-side errors (e.g., 404, 500) are gracefully handled by the `composeApp` UI.
