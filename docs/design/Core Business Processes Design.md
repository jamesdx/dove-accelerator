# Implementing "dove-accelerator" Core Business Processes with Code

This document outlines how the provided Java code structure (and the implied code patterns) implements the core business processes defined in the "dove-accelerator: Core Business Processes (Most Essential - Final Refinement)" section of the `Overall Project Goal.md` document. It also explains the design of the core business process.

## Core Business Process 1: AI-Driven Software Development - Task Execution within a Project

This is the central process of `dove-accelerator`, where AI agents actively participate in software development.

**Code Areas:** `dove-agent`, `dove-task`, `dove-knowledge`, `dove-project`, `dove-gateway`

**Breakdown and Code Mapping:**

1.  **Task Creation and Definition (Task Management):**

    *   **Description:** A user (e.g., project manager, product owner) or the system (e.g., through task decomposition) defines a task within a project, specifying details, dependencies, and potential assignees.
    *   **Code Location:** Primarily `dove-task` module.
    *   **Implementation:**
        *   **`Task` Entity:** (`dove-task/src/main/java/com/doveaccelerator/task/entity/Task.java`)
            *   Represents a task with properties like `taskId`, `projectId`, `title`, `description`, `status` (e.g., "pending," "in-progress," "completed"), `assigneeAgentId`, `dependencyTasks` (comma-separated list of task IDs), `createTime`, and `updateTime`.
            *   Uses JPA annotations (`@Entity`, `@Id`, `@GeneratedValue`, etc.) for database mapping.
            *   Database:  `dove-task/pom.xml` specifies `mariadb` as the database.
        *   **`TaskRepository`:** (`dove-task/src/main/java/com/doveaccelerator/task/repository/TaskRepository.java`)
            *   Extends `JpaRepository` for standard database interactions (CRUD operations).
        *   **`TaskService`:** (`dove-task/src/main/java/com/doveaccelerator/task/service/TaskService.java` and `com/doveaccelerator/task/service/impl/TaskServiceImpl.java`)
            *   Handles business logic for task management, including creating, updating, retrieving, assigning, and triggering dependent tasks.
            *   Key methods (as seen in `TaskServiceImpl.java`): `createTask`, `updateTask`, `deleteTask`, `getTask`, `getAllTasks`, `getTasksByProject`, `getTasksByProjectAndStatus`, `getTasksByPhase`, `getOverdueTasks`, `getTasksByAgent`, `assignTask`, `removeAssignment`, `addDependency`, `removeDependency`, `updateProgress`, `startTask`, `completeTask`, `pauseTask`.
        *   **`TaskController`:** (`dove-task/src/main/java/com/doveaccelerator/task/controller/TaskController.java` - likely)
            *   Exposes REST endpoints (e.g., `/api/tasks`) for task management operations.

    *   **Code Snippet (`Task` entity):**

        ```java
        package com.doveaccelerator.task.entity;

        import jakarta.persistence.*;
        import lombok.Data;

        import java.time.LocalDateTime;

        @Entity
        @Data
        public class Task {
            @Id
            @GeneratedValue(strategy = GenerationType.IDENTITY)
            private Long taskId;

            private Long projectId;
            private String title;
            private String description;
            private String status; // pending, in-progress, completed
            private Long assigneeAgentId;
            private String dependencyTasks; //task id list, split by `,`
            private LocalDateTime createTime;
            private LocalDateTime updateTime;

            // ... other fields and methods
        }
        ```

2.  **Task Assignment (Task & Agent Management):**

    *   **Description:** Assigning a task to an appropriate AI agent based on its capabilities and the task's requirements.
    *   **Code Location:** Interaction between `dove-task` and `dove-agent`.
    *   **Implementation:**
        *   **`TaskService.assignTaskToAgent()`:**
            *   Updates the `assigneeAgentId` in the `Task` entity.
            *   Sets the initial `status` of the task to "pending."
            * Implemented in `TaskServiceImpl.java`.
        *   **`Project` entity**
            * define the `agentIds` for project.
        *   **`Agent` Entity:** (`dove-agent/src/main/java/com/doveaccelerator/agent/entity/Agent.java` - should be defined)
            *   Represents an AI agent with properties like `agentId`, `type` (e.g., "code", "test", "document"), and `status`.

    *   **Code Snippet (`TaskService.assignTaskToAgent()`):**

        ```java
        // in TaskService.java (TaskServiceImpl.java)
        public void assignTaskToAgent(Long taskId, Long agentId) {
            Task task = taskRepository.findById(taskId).orElseThrow(() -> new RuntimeException("Task not found"));
            task.setAssigneeAgentId(agentId);
            task.setStatus("pending");
            taskRepository.save(task);
        }
        ```

3.  **Agent Task Pickup (Agent Management):**

    *   **Description:** An AI agent identifies and selects a task assigned to it that is ready for execution.
    *   **Code Location:** `dove-agent`
    *   **Implementation:**
        *   **No `TaskFetcher`:** There is no dedicated `TaskFetcher` class.
        *   **Agent-Driven Task Acquisition:** The `dove-agent` service is responsible for checking and picking up tasks.
        *   **`AgentService` (or similar):** An `AgentService` (or a similarly named component) in `dove-agent` manages the agent's behavior.
         * **`DoveAgentApplication`**: it defined the `@EnableFeignClients`, `@EnableAsync` and `@EnableTransactionManagement`.
        *   **`TaskService.getTasksByAgent()` via Feign:** The `AgentService` uses `TaskService.getTasksByAgent()` (by calling the `task` service by `FeignClient`) to fetch tasks assigned to the agent where the `status` is "pending."
        *   **Scheduled Task or Polling:**  The agent may periodically (e.g., via a scheduled task) or continuously poll the `TaskService` for new task assignments.
        *   **Task Selection and Status Update:** The agent iterates through the retrieved `pending` tasks. Upon selecting a task, it immediately calls `TaskService.updateTaskStatus()` (via Feign) to update the task's `status` to "in-progress."
        * **Code Snippet (Agent handle the task)**
          ```java
          //in AgentService.java
          public void processTasks(){
              // get the tasks assigned to this agent.
              List<TaskDTO> tasks = taskFeignClient.getTasksByAgent(this.agentId);
              for(TaskDTO task : tasks){
                 if ("pending".equals(task.getStatus())){
                      // update the task status to in-progress.
                      taskFeignClient.updateTaskStatus(task.getTaskId(), "in-progress");
                      //execute the task
                      executeTask(task);
                   }
              }
           }
          ```

4.  **Knowledge Retrieval (Knowledge Management):**

    *   **Description:** An agent retrieves relevant information from the knowledge base to aid in task execution.
    *   **Code Location:** `dove-knowledge`
    *   **Implementation:**
        *   **`Knowledge` Entity:** (`dove-knowledge/src/main/java/com/doveaccelerator/knowledge/entity/Knowledge.java` - likely)
            *   Represents a knowledge article.
            *   Database: `dove-knowledge/pom.xml` specifies the use of `mariadb` (likely for structured knowledge), `postgresql` with `pgvector` (for vector embeddings and similarity search), and `elasticsearch` (for text-based search).
        *   **`KnowledgeRepository`:**
            *   Manages knowledge data access and persistence.
        *   **`KnowledgeService`:**
            *   Provides knowledge-related business logic, including methods like `searchKnowledge(String query)`.
        *   **Feign Client:** (`dove-agent/pom.xml` includes Feign).
            *   `dove-agent` utilizes Feign to make remote procedure calls (RPCs) to `dove-knowledge`'s API to search for knowledge articles.
            *   `dove-knowledge` use `spring-cloud-starter-openfeign`, `spring-cloud-starter-loadbalancer` to implement the Feign.
            * `dove-knowledge/pom.xml` also use `MapStruct` to do the data transfer.

    *   **Code Snippet (`KnowledgeService.searchKnowledge()`):**

        ```java
        // in KnowledgeService.java
        @Service
        public class KnowledgeService {
            // ...

            public List<Knowledge> searchKnowledge(String query) {
                // ... search logic ...
                return knowledgeRepository.findAll();
            }
        }
        ```

5.  **Task Execution (Agent Management):**

    *   **Description:** The agent performs the assigned task using its specialized AI capabilities.
    *   **Code Location:** `dove-agent`, `langchain4j`, `spring-ai`
    *   **Implementation:**
        *   **Agent Logic:**
            *   The `dove-agent` service houses the core AI agent logic.
            *   AI Frameworks: It leverages `langchain4j` or `spring-ai` for AI-driven operations. `dove-agent/pom.xml` lists the dependencies, for example `spring-ai-openai-spring-boot-starter`, `spring-ai-anthropic-spring-boot-starter` and `langchain4j-open-ai`.
            *   Task Processing: Agent logic includes steps like:
                *   Understanding the task `description`.
                *   Leveraging `KnowledgeService` for relevant information.
                *   Carrying out AI-based actions (e.g., code generation, testing, documentation).
        *   **Agent Type:**
            *   Agent `type` defines the agent's role and capabilities (e.g., "code" agent, "test" agent, "document" agent).
        * `dove-agent/pom.xml` also use `MapStruct`, and `springdoc-openapi-starter-webmvc-ui`.
    *   **Code Snippet(Agent handle the task)**
       ```java
           //in AgentService.java
               public void executeTask(Task task){
                   // get the knowledge.
                   List<Knowledge> knowledges = knowledgeFeignClient.searchKnowledge(task.getDescription());
                   // do the task based on the type.
                   if (this.type == AgentType.CODE){
                       // generate the code.
                       // use langchain4j or spring ai to generate the code.
                   } else if (this.type == AgentType.TEST){
                       // do the test.
                       // ...
                   } else if (this.type == AgentType.DOCUMENT){
                       // write the document.
                   }
                   //update the task status to completed.
                   taskService.updateTaskStatus(task.getTaskId(), "completed");
               }
       ```

6.  **Task Status Update and Reporting (Task Management):**

    *   **Description:** Agents update the task's progress and completion status.
    *   **Code Location:** `dove-task`
    *   **Implementation:**
        *   **`TaskService.updateTaskStatus()`:**
            *   Modifies the `status` of a `Task` (e.g., "in-progress," "completed").
            *   The agent calls this API to update the task's status.

    *   **Code Snippet (`TaskService.updateTaskStatus()`):**

        ```java
        // in TaskService.java (TaskServiceImpl.java)
        public void updateTaskStatus(Long taskId, String newStatus) {
            Task task = taskRepository.findById(taskId).orElseThrow(() -> new RuntimeException("Task not found"));
            task.setStatus(newStatus);
            task.setUpdateTime(LocalDateTime.now());
            taskRepository.save(task);
        }
        ```

7.  **Triggering Dependent Tasks:**

    *   **Description:** When a task is completed, check if it unblocks any dependent tasks.
    *   **Code Location:** `dove-task`, `dove-agent`
    *   **Implementation:**
        *   **`Task` Entity: `dependencyTasks`:** Stores the list of dependent task IDs.
        *   **`TaskService.updateTaskStatus()`:**
            *   When a task's status is updated to "completed," the method checks `dependencyTasks`.
            *   If dependencies exist, it finds and updates their status to "pending."
            * It will notify the agent to pickup the task.

    *   **Code Snippet:**

        ```java
        // in TaskService.java (TaskServiceImpl.java)
        public void updateTaskStatus(Long taskId, String newStatus) {
             Task task = taskRepository.findById(taskId).orElseThrow(() -> new RuntimeException("Task not found"));
             task.setStatus(newStatus);
             task.setUpdateTime(LocalDateTime.now());
             taskRepository.save(task);

            // check the dependency.
            if ("completed".equals(newStatus)){
                List<Task> nextTasks = taskRepository.findByDependencyTasksLike("%"+taskId+"%");
                for (Task nextTask : nextTasks){
                    // trigger the next task
                    nextTask.setStatus("pending");
                    taskRepository.save(nextTask);
                    // notify the agent.
                    agentService.notifyAgent(nextTask.getAssigneeAgentId(), nextTask.getTaskId());
                }
            }
        }
        ```

**Mechanisms for Agent Collaboration:**

*   **Message Queues:** (RocketMQ - implied) for asynchronous communication and event notifications.
*   **API Calls:** `dove-agent` uses Feign/RestTemplate; `dove-gateway` manages API access.
*   **Shared Data:** MariaDB/Redis for data sharing and coordination.
*   **Task Dependency:** The `Task` entity's `dependencyTasks` field defines task dependencies.
*   **Agent coordinator**: A component in agent module, trigger the next task.

## Core Business Process 2: Project Initiation, Management, and AI Agent Assignment

This process sets up the projects and resources needed for AI-driven development. It involves defining project scope, assigning resources (including AI agents), and initially planning tasks.

**Code Areas:** `dove-project`, `dove-agent`, `dove-task`

**Breakdown and Code Mapping:**

1.  **Project Creation (Project Management):**

    *   **Description:** Creating a new software project, including defining its name, description, and initial scope.
    *   **Code Location:** `dove-project`
    *   **Implementation:**
        *   **`Project` Entity:** (`dove-project/src/main/java/com/doveaccelerator/project/entity/Project.java`)
            *   `projectId`, `name`, `description`, `status`, `startTime`, `endTime`, `agentIds`.
            *   Database: `dove-project/pom.xml` uses `mariadb` as the database.
            *   Other library: `apache poi` is used to process document.
            * `dove-project/pom.xml` use `MapStruct` to do the data transfer.
        *   **`ProjectRepository`:**
            *   Handles project data persistence.
        *   **`ProjectService`:**
            *   Handles project-related business logic, including creation, updates, tracking, etc.
        *   **`ProjectController`:**
            *   Provides REST endpoints for project management actions.

2.  **Agent Assignment (Project Management):**

    *   **Description:** Assigning AI agents to a project or specific project phases based on their skills and the project's needs.
    *   **Code:** `dove-project` and `dove-agent`
    *   **Implementation:**
        *   **`Project` Entity: `agentIds`:** Stores a list of agent IDs assigned to the project.
        *   **`ProjectService.assignAgentToProject()`:** Updates the `agentIds` list, linking agents to the project.
        *   **`AgentService` interaction:** `agentService` may query `projectService` to get the projects assigned to an agent.
        * **Business Logic**:
            * **Agent Role**: The `agent` entity need have the role to define which role it can do.
            * **Project phase**: project can have phase.
            *  The project can define which agent to user for this phase.
            * The `agentIds` is the list of agent for the project.

    *   **Code snippet:**

        ```java
        // in project entity.
        package com.doveaccelerator.project.entity;

        import jakarta.persistence.*;
        import lombok.Data;

        import java.time.LocalDateTime;
        import java.util.List;

        @Entity
        @Data
        public class Project {
            @Id
            @GeneratedValue(strategy = GenerationType.IDENTITY)
            private Long projectId;
            private String name;
            private String description;
            private String status;
            private LocalDateTime startTime;
            private LocalDateTime endTime;
            // agent ids for project.
            @ElementCollection
            private List<Long> agentIds;

            // ... other fields and methods
        }
        ```

3.  **Task Decomposition (Project/Product Manager/Architect AI Agents):**

    *   **Description:** Breaking a project down into smaller, manageable tasks.
    *   **Code:** `dove-task` and `dove-agent`
    *   **Implementation:**
        *   **Agent Collaboration:** `projectService` can call `agentService` to obtain an architect or product manager agent.
        *   **Agent Task Decomposition:** An AI agent (e.g., of type "architect") in the `dove-agent` module will perform the task decomposition.
        *   **`TaskService` Interaction:** The agent creates new tasks and saves them to the `task` database through `taskService`.
        *   **Project Phases:** Projects may be divided into phases, with tasks belonging to specific phases.
        * **Business Logic**:
          * The Project can be divide into phase, each phase can have tasks.
          * The tasks can define the dependency, the task will be triggered based on the dependency.
          * The agent will do the task decomposition based on the `Project`, then save it to the `Task` entity.

4.  **Project Tracking and Management (Project Management):**

    *   **Description:** Monitoring a project's status, progress, and overall health.
    *   **Code Location:** `dove-project`
    *   **Implementation:**
        *   **`ProjectService`:**
            *   Methods to manage project `status` and properties (e.g., `start`, `pause`, `complete`).
        *   **`Project` Entity: `status`:** The `status` field reflects the project's current state.
        * **Business Logic**:
            * The `Project` have `status`, `startTime`, `endTime` to manage the project.
            * `ProjectService` will manage the project status.
            * The agent can do the task decomposition.
            * The system can create the task for project based on the phase.
            * The `ProjectService` can query the `taskService` to get the project progress.
            * The project can be update by the project manager, or the product owner.
            * The project can be paused, and resumed.

## Core Business Process 3: Secure Access - User Authentication and Authorization

This process ensures that only authorized users and agents can access the system and its resources.

**Code Areas:** `dove-auth`, `dove-gateway`, `dove-common`, all other services

**Breakdown and Code Mapping:**

1.  **User Login (Authentication & Authorization):**

    *   **Description:** Users (and possibly agents) log in to the system using their credentials.
    *   **Code Location:** `dove-auth`
    *   **Implementation:**
        *   **`User` Entity:** (`dove-auth/src/main/java/com/doveaccelerator/auth/entity/User.java`)
            *   Represents a user with attributes like `userId`, `username`, `password`, `roles`, etc.
        *   **`UserRepository`:**
            *   Handles user data persistence.
        *   **`UserService`:**
            *   Manages user-related business logic, including authentication, password encoding, etc.
        *   **`SecurityConfig`:** (`dove-auth/src/main/java/com/doveaccelerator/auth/config/SecurityConfig.java`)
            *   Defines Spring Security rules for authentication and authorization.
        *   **`JwtUtil`:**
            *   Generates and validates JWT (JSON Web Token) tokens.
        *   **`PermissionCheckAspect`**: (`dove-auth/src/main/java/com/doveaccelerator/auth/aspect/PermissionCheckAspect.java`)
            * Check the permission based on the define.
        * **`RequirePermission` Annotation**:
           * define the required permission.
        * **Database and Session:**
          * `dove-auth/src/main/resources/application.yml` indicates:
            *   `mariadb` is used to store user data.
            *   `redis` is used for session management.
            * JWTs are used for stateless authentication.

2.  **Session Management (Authentication & Authorization):**

    *   **Description:** Managing user sessions after successful login.
    *   **Code Location:** `dove-auth`, `redis`
    *   **Implementation:**
        *   **JWT Token Generation:**
            *   Upon successful login, `dove-auth` generates a JWT token.
        *   **Redis (Token Storage):**
            *   The JWT token (and potentially some user data) is stored in Redis.
        * **Business Logic**:
          * The JWT token will contain the user info, it will be checked in `gateway` and `auth` module.

3.  **Authorization Check (Authentication & Authorization):**

    *   **Description:** Verifying if a user has the necessary permissions to access a specific resource.
    *   **Code Location:** `dove-auth`, `dove-gateway`, other services
    *   **Implementation:**
        *   **`dove-gateway` Filter:**
            *   Intercepts all incoming API requests.
            *   Checks for a JWT in the `Authorization` header.
            *   Validates the token using `JwtUtil`.
            *   May call `dove-auth` (via REST) to get user details or roles if needed.
            *   Blocks unauthorized requests.
        *   **Microservice `SecurityConfig`:**
            *   Each microservice (e.g., `dove-task`, `dove-project`) has its own `SecurityConfig` to protect REST endpoints.
        *   **`PermissionCheckAspect`**:
            * check the permission for each request.
        * **`RequirePermission`**:
          * Annotation to define the required permission for the method.
        * **Business Logic**:
           * the request will be intercepted by `gateway`, and then call `auth` module to do the auth check.
           * The `auth` module defined the `SecurityConfig`, `PermissionCheckAspect` to check if the user have the permission.

4. **Logout**:
   * **Description**: user logout.
   * **Code**: `auth` module.
   * **Implementation**: `auth` service provide the logout api.

## Core Business Process 4: API access

* **Description**: User or other service access the system api.
* **Code**: `dove-gateway`, all service.
* **Implementation**:
    1. **Request**: user request the `gateway`.
    2. **Auth check**: `gateway` call `auth` to check.
    3. **Route**: `gateway` route the request to the right service.
    4. **Response**: return the response.
    * `gateway` is the entrance for all api call.
    * The gateway use filter to do the auth check.
    * gateway use loadbalancer to get the service address.

## Core Business Process 5: Todo App Management

* **Description**: a demo app to show the ability of this platform.
* **Code**: `dove-auth`, `dove-gateway`, `dove-project`, `dove-task`, `dove-agent`, `dove-knowledge`.
* **Implementation**
    1. **User Login**: use `auth` service, already implemented.
    2. **Create Todo Task**: use the `project`, `task` module.
    3. **Update Todo Task**: use `task` module.
    4. **Delete Todo Task**: use `task` module.
    5. **List Todo Task**: use `task` module.
    6. **Filter**: use `task` module.
    7. **Order**: use `task` module.
    8. **Data Statistic**: use `task` module.
    * It is a demo app, you need to implement the `todo` module.
    * it will use the function defined in other modules.

## Core Business Process 5: Todo App Management

*   **Description**: This is envisioned as a demo application that showcases the capabilities of the `dove-accelerator` platform. It provides a simple to-do list management interface, demonstrating the use of tasks, user authentication, and potentially AI agent interaction.
*   **Code**: `dove-auth`, `dove-gateway`, `dove-project`, `dove-task`, `dove-agent`, `dove-knowledge`.
*   **Implementation (Conceptual):**
    1.  **User Login**: Users will log in using the existing `dove-auth` service.
    2.  **Create Todo Task**: Creating new tasks will involve interacting with the `dove-task` module. This is similar to the existing task management functionality, but with a simpler UI and use case.
    3.  **Update Todo Task**: Updating task details (e.g., title, description, status) will also utilize the `dove-task` module.
    4.  **Delete Todo Task**: Removing tasks will be handled by the `dove-task` module.
    5.  **List Todo Task**: Retrieving the list of to-do tasks for a specific user will be done by querying the `dove-task` module.
    6.  **Filter**: Filtering tasks (e.g., by status, date) will involve querying the `dove-task` data.
    7.  **Order**: Ordering tasks (e.g., by priority, due date) will involve sorting the data retrieved from `dove-task`.
    8.  **Data Statistic**: Displaying statistics about tasks (e.g., completed vs. pending) will require processing data from `dove-task`.
    * **Note:** This Todo App is currently a *conceptual* feature. The actual `todo` module and its specific implementation details are not yet defined in the existing codebase. This is a suggestion for future development.
    * it will use the function defined in other modules.

## Preconditions for System Operation (Design):

This section describes the preconditions that *should* be met for the proper operation of the `dove-accelerator` system, as designed. The current codebase partially implements some of these, but not all:

1.  **System Startup**: All core microservices need to be running. This includes `dove-agent`, `dove-task`, `dove-knowledge`, `dove-project`, `dove-auth`, and `dove-gateway`.
2.  **AI Agent Initialization**: AI agents in the `dove-agent` module should be initialized, defining their types and roles. Currently, the `agent` module provides a basic structure for agents but does not contain fully-fledged AI agent implementations.
3.  **Database Ready**: The databases (`mariadb`, `postgresql`, `redis`, `elasticsearch`) used by the system must be running and accessible. The code already defines database connections and entities, but not all database functionality is fully implemented.
4.  **Knowledge Ready**: The knowledge base (`dove-knowledge`) should be populated with the initial data required for agents. Currently, the `knowledge` module defines the database connection and entity, but the knowledge is not in the database.
5. **Auth ready**: The `auth` service should be ready, and you need to define the user in the system. Now the code implement the basic user login and auth, but it's not fully implemented.

**Important Note:** The current codebase provides the *foundation* for these processes, such as entities, repositories, basic services, and API endpoints. However, it does not yet fully implement the *complete* functionality described in this document.

## Next Steps (Toward Full Implementation):

This design document serves as a roadmap for future development. The next steps to move towards full implementation include:

1.  **Complete AI Agent Logic:** Implement the core AI logic within `dove-agent`, leveraging `langchain4j` or `spring-ai` for task execution.
2.  **Implement Agent Collaboration:** Implement the mechanisms for asynchronous communication (message queues) and agent interaction (API calls, data sharing).
3.  **Refine Knowledge Base:** Populate the `dove-knowledge` knowledge base with substantial, relevant information.
4. **Implement todo app**: complete the todo app.
5.  **Complete Project Management:** Fully implement the `ProjectService`, including task decomposition and project tracking.
6.  **Enhance Security:** Refine and harden the authentication and authorization logic.
7. **Complete test**: add the test for each module.
8.  **Testing and Refinement:** Thoroughly test each business process to ensure it works as designed and refine the design based on the test results.

This `Core Business Processes Design.md` document should be treated as a living document that will be updated as the implementation progresses.

