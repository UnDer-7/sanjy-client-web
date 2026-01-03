# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Sanjy Client Web is a modern full-stack application for diet and meal tracking. It consists of a React-based Single Page Application (SPA) frontend bundled with a Spring Boot Backend for Frontend (BFF). The BFF communicates with a backend service (sanjy-server) via OpenFeign clients.

**Base Package**: `br.com.gorillaroxo.sanjy.client`

**Architecture Pattern**: Backend for Frontend (BFF) - The Spring Boot application serves the React SPA and provides API endpoints that aggregate/transform data from the backend service.

## Essential Rules

**IMPORTANT**: These rules MUST be followed at all times:

1. **English Only**: ALL code, comments, commit messages, variable names, class names, method names, and documentation MUST be written in English. Never use Portuguese or any other language in the codebase.

2. **Mantine Documentation Consultation**: When creating or editing React components in the frontend, ALWAYS consult the Mantine documentation at https://mantine.dev/llms.txt to ensure correct usage of components, hooks, and patterns.

3. **Mandatory Build Validation**: After implementing ANY code changes, you MUST:
   - Run `mvn clean install` from the project root
   - If the build fails, analyze errors, fix them, and run `mvn clean install` again
   - Repeat until the build succeeds with `BUILD SUCCESS`
   - Only consider the task complete when the build passes successfully

## Architecture

### Project Structure

The project is a single-module Maven application with two main parts:

1. **Frontend** (`src/main/frontend/`) - React SPA:
   - **React 19** with **TypeScript**
   - **Mantine v8** component library for UI
   - **Vite** for build tooling and development server
   - PostCSS with Mantine preset for styling
   - Built artifacts are bundled into Spring Boot's static resources for production

2. **Backend** (`src/main/java/`) - Spring Boot BFF (Backend for Frontend):
   - **Spring Boot 3.5.6** REST API (port 8080 in production, 8081 in development)
   - OpenFeign clients for communicating with sanjy-server backend
   - Request/Response DTOs for API communication
   - Configuration properties (`SanjyClientConfigProp`)
   - Feign interceptor for distributed tracing (correlation ID, channel headers)
   - Utility classes for distributed tracing and request constants
   - Exception classes (`BusinessException`, `UnexpectedErrorException`)
   - Spring AI integration for diet plan file processing (PDF, text)
   - Serves the React SPA in production

### Key Technologies

**Backend:**
- **Java 21**
- **Spring Boot 3.5.6**
- **Spring Cloud 2025.0.0** (OpenFeign for HTTP clients)
- **Spring AI 1.0.2** (OpenAI integration for diet plan extraction)
- **Apache PDFBox** (PDF text extraction)
- **Lombok** + **MapStruct** (with proper annotation processor ordering)
- **SpringDoc OpenAPI** (Swagger documentation)
- **Logstash Logback Encoder** (structured logging)
- **GraalVM Native Image** support (static musl builds)

**Frontend:**
- **React 19.2.0** (UI library)
- **TypeScript ~5.9.3** (type safety)
- **Mantine v8.3.10** (component library - [LLM docs](https://mantine.dev/llms.txt))
- **Vite 7.2.4** (build tool and dev server)
- **PostCSS** with Mantine preset (styling)

### Distributed Tracing

The BFF implements correlation ID tracking across all requests:
- `RequestFilter` generates or extracts correlation IDs from headers
- `FeignInterceptor` propagates correlation IDs to backend service calls
- Uses SLF4J MDC for logging context (correlation ID, transaction ID, HTTP request)
- Custom channel header (`X-Channel`) identifies the client type

### Configuration Management

**Backend Configuration:**
- `.env` file for development environment variables
- `application.yml` in `src/main/resources`
- Type-safe configuration via `SanjyClientConfigProp` record with validation
- Key configuration properties:
  - `sanjy-client.external-apis.sanjy-server.url` - Backend service URL
  - `sanjy-client.application.channel` - Client channel identifier
  - `sanjy-client.logging.*` - Logging configuration
  - `spring.ai.openai.*` - OpenAI API credentials

**Frontend Configuration:**
- Environment variables via `.env` files in the frontend directory
- Vite uses `VITE_` prefix for environment variables exposed to the client
- API base URL configuration for development vs production

### Spring AI Integration

The BFF uses Spring AI to extract diet plan information from uploaded files:
- `ProcessDietPlanFileService` - Orchestrates file processing
- `ExtractTextFromFileStrategy` - Strategy pattern for different file types (PDF, text)
- `DietPlanConverter` - Uses OpenAI ChatClient to convert extracted text to structured DTOs

### Frontend-Backend Integration

The project follows the architecture described in [Bundling React (Vite) with Spring Boot](https://www.jessym.com/articles/bundling-react-vite-with-spring-boot):

**Development Mode:**
- Frontend runs on Vite dev server (port 5173) with hot module replacement
- Backend BFF runs on Spring Boot (port 8081)
- Frontend proxies API requests to the backend during development

**Production Mode:**
- Vite builds the React app to static assets
- Static assets are copied to Spring Boot's `src/main/resources/static`
- Spring Boot serves both the SPA and API endpoints on a single port
- All frontend routes fall back to `index.html` for client-side routing

## CRITICAL: Code Validation Rules

**MANDATORY**: After making ANY code changes (creating files, editing code, refactoring, etc.), you MUST:

1. Run `mvn clean install` from the project root
2. If the build FAILS:
   - Analyze the error messages carefully
   - Fix the compilation/build errors
   - Run `mvn clean install` again
3. Repeat step 2 until the build SUCCEEDS
4. Only consider the task complete when `mvn clean install` passes with BUILD SUCCESS

**Do NOT skip this validation step.** A successful build ensures:
- All modules compile correctly
- No syntax or type errors
- Annotation processors (Lombok, MapStruct) run successfully
- Module dependencies are satisfied
- The code is ready for deployment

## Build and Development

### Building the Project

**Backend:**
```bash
# Clean install (compile and package)
mvn clean install

# Compile only (quieter output)
mvn clean compile -q

# Run the Spring Boot BFF
./mvnw spring-boot:run

# Access BFF API
http://localhost:8081
```

**Frontend:**
```bash
# Navigate to frontend directory
cd src/main/frontend

# Install dependencies
npm install

# Run development server with HMR
npm run dev

# Build for production
npm run build

# Preview production build
npm run preview

# Access frontend dev server
http://localhost:5173
```

### Running Tests

**Backend Tests:**
```bash
# Run all backend tests
mvn test

# Run tests in native image
mvn test -PnativeTest
```

**Frontend Tests:**
```bash
cd src/main/frontend

# Run tests (when configured)
npm test

# Run tests with coverage
npm run test:coverage
```

### Environment Setup

**Backend Environment:**
- Copy `.env` file in project root and populate with required values
- Required OpenAI credentials: `LOCAL_SANJY_OPENAI_API_KEY`, `LOCAL_SANJY_OPENAI_ORGANIZATION_ID`, `LOCAL_SANJY_OPENAI_PROJECT_ID`
- Backend service URL defaults to `http://localhost:8080`

**Frontend Environment:**
- Create `.env` file in `src/main/frontend/` if needed
- Use `VITE_` prefix for environment variables exposed to the browser
- Example: `VITE_API_BASE_URL=http://localhost:8081` for development

### GraalVM Native Image

The application is configured for GraalVM native compilation with static musl builds:

```bash
# Build native image
mvn native:compile -Pnative

# Run native executable
./target/sanjy-client-web.graalvm

# Build Docker container with Cloud Native Buildpacks
./mvnw spring-boot:build-image -Pnative
docker run --rm -p 8081:8081 sanjy-client:0.0.1-SNAPSHOT
```

### Important Build Notes

**Backend:**
- **Annotation Processors**: Lombok must run before MapStruct. The `pom.xml` configures the correct ordering with `lombok-mapstruct-binding`.
- **Lombok Configuration**: `lombok.config` ensures Spring annotations (`@Qualifier`, `@Value`, `@Lazy`) are copied to generated constructors.

**Frontend:**
- **PostCSS Configuration**: `postcss.config.cjs` must be present with `postcss-preset-mantine` for Mantine styles to work correctly
- **TypeScript**: Project uses strict TypeScript configuration for type safety
- **ESLint**: Code quality is enforced via ESLint with React-specific rules

**Integration:**
- Frontend build output should be copied to `src/main/resources/static` for production deployment
- Spring Boot must be configured to serve `index.html` for all non-API routes to support client-side routing

## Application Structure

### Backend Structure (BFF)

**Controllers** - REST API endpoints (use `@RestController`):
- Diet plan management endpoints (`/api/diet-plan/*`)
- Meal recording and viewing endpoints (`/api/meal/*`)
- File upload endpoints for diet plan processing

**Services:**
- `ProcessDietPlanFileService` - Orchestrates file processing with Spring AI
- Business logic for diet plan and meal management

**DTOs:**
- Request/Response objects for API communication
- Separate packages for requests and responses

**Feign Clients:**
- `DietPlanFeignClient` - Diet plan operations (`/v1/diet-plan`)
- `MealRecordFeignClient` - Meal record operations
- Configured to use `${sanjy-client.external-apis.sanjy-server.url}` from properties
- Automatically intercepted by `FeignInterceptor` for header propagation

**DTO Pattern:**
- **Requests**: POJOs with Lombok `@Data`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`
- **Responses**: Java records with `@Builder` and defensive copying in compact constructors

### Frontend Structure (React SPA)

**Directory Structure:**
```
src/main/frontend/
├── src/
│   ├── components/      # Reusable React components
│   ├── pages/          # Page-level components
│   ├── hooks/          # Custom React hooks
│   ├── services/       # API client services
│   ├── types/          # TypeScript type definitions
│   ├── theme.ts        # Mantine theme configuration
│   ├── App.tsx         # Root component with MantineProvider
│   └── main.tsx        # Application entry point
├── public/             # Static assets
├── index.html          # HTML template
├── vite.config.ts      # Vite configuration
├── tsconfig.json       # TypeScript configuration
└── package.json        # NPM dependencies
```

**Key Components:**
- **App.tsx**: Root component that wraps the application with `MantineProvider`
- **theme.ts**: Mantine theme customization (colors, fonts, spacing, etc.)
- **Components**: Use Mantine v8 components - always consult https://mantine.dev/llms.txt for correct usage

## Development Workflow

### Adding New Features

**Full-Stack Feature (Frontend + Backend):**
1. **Define DTOs** in the backend under `dto.request` and `dto.response`
2. **Create/update Feign client interface** if communicating with sanjy-server
3. **Implement REST controller** in the BFF with `@RestController`
4. **Create API service** in frontend (`src/main/frontend/src/services/`)
5. **Define TypeScript types** matching the backend DTOs
6. **Create React components** using Mantine v8 (consult https://mantine.dev/llms.txt)
7. **Create page components** that use the services and components

**Backend-Only Feature:**
1. Define DTOs in `dto.request` and `dto.response`
2. Create/update Feign client if needed
3. Implement REST controller
4. Add business logic in services

**Frontend-Only Feature:**
1. Create TypeScript types in `src/types/`
2. Create Mantine components (consult https://mantine.dev/llms.txt for correct usage)
3. Add custom hooks in `src/hooks/` if needed
4. Create page components

### Exception Handling

**Backend:**
- Use exception hierarchy: `BusinessException` (business rule violations), `UnexpectedErrorException` (technical errors)
- `ExceptionCode` - Centralized error codes
- `GlobalExceptionHandlerConfig` - Handles exceptions and returns JSON error responses

**Frontend:**
- Implement error boundaries for React component errors
- Handle API errors in service layer
- Display user-friendly error messages using Mantine notifications

## API Documentation

Swagger UI is available when the BFF is running:
- Swagger UI: `http://localhost:8081/swagger-ui.html`
- OpenAPI spec: `http://localhost:8081/v3/api-docs`

Documents all REST API endpoints exposed by the Backend for Frontend.

## Code Style Notes

**Backend:**
- Use Lombok annotations to reduce boilerplate
- Response DTOs should be immutable records
- Request DTOs can be mutable POJOs for form binding
- REST controllers use `@RestController` and return `ResponseEntity`
- Follow the package structure: `controller`, `client`, `dto.request`, `dto.response`, `service`, `config`, `filter`
- Use structured logging with `StructuredArguments.kv()` for JSON logging support
- Always use correlation ID context in logs (automatically available via MDC)

**Frontend:**
- Use functional components with TypeScript
- Define proper TypeScript types for all props and state
- Use Mantine v8 components (consult https://mantine.dev/llms.txt)
- Follow React best practices: hooks, composition, single responsibility
- Organize components by feature or domain
- Use custom hooks for reusable logic
- Handle errors gracefully with error boundaries
- Use ESLint rules defined in the project

**Integration:**
- Keep TypeScript types in sync with backend DTOs
- Use consistent naming between frontend and backend
- Document API endpoints used by frontend services
