# ==================================================================================== #
# VARIABLES
# ==================================================================================== #
PROJECT_NAME=sanjy-client-web
REGISTRY_HOST=under7
POM_VERSION := $(shell ./mvnw help:evaluate -Dexpression=project.version -q -DforceStdout)





# ==================================================================================== #
## ===== HELPERS =====
# ==================================================================================== #
## help: Describe all available targets
.PHONY: help
help:
	@echo 'Usage: make <target>'
	@sed -n 's/^##//p' $(MAKEFILE_LIST) | column -t -s ':' | \
	awk 'BEGIN {first=1} \
						/^ *=====/ { if (!first) print ""; print "   " $$0; first=0; next } \
						/^ *-----/ { print ""; print "   " $$0; next } \
						{ print "   " $$0 }'

# Hidden
.PHONY: all
all: help





# ==================================================================================== #
## ===== DEV =====
# ==================================================================================== #
## dev/compile: Just compile the application
.PHONY: dev/compile
dev/compile:
	@echo ">>> Compiling…"
	./mvnw -B -ntp clean compile

## dev/run: Run the application locally (performs clean install first, loads .env variables)
.PHONY: dev/run
dev/run:
	@echo '>>> Loading environment variables from .env...' && \
	set -a && \
	. $(CURDIR)/.env && \
	set +a && \
	echo '>>> Compiling...' && \
	./mvnw -B -ntp clean install -DskipTests && \
	echo '>>> Starting Spring Boot application...' && \
	./mvnw -B -ntp spring-boot:run





# ==================================================================================== #
## ===== TEST =====
# ==================================================================================== #
## test: Run all tests (unit tests ending with *Test.java + integration tests ending with *IT.java) in JVM mode
.PHONY: test
test:
	echo ">>> Running all tests (unit + integration)…" && \
	./mvnw -B -ntp clean compile verify

## test/native: Run integration tests only (*IT.java) in GraalVM native mode. Unit tests (*Test.java) are excluded because Mockito is incompatible with GraalVM Native Image
.PHONY: test/native
test/native:
	echo ">>> Running integration tests only (unit tests excluded due to Mockito incompatibility)…" && \
	./mvnw -B -ntp -PnativeTest clean compile test





# ==================================================================================== #
## ===== BUILD =====
# ==================================================================================== #
## ----- Frontend -----
## build/frontend: Build the frontend (React/Vite)
.PHONY: build/frontend
build/frontend:
	@START=$$(date +%s); \
	echo '>>> Building frontend...'; \
	cd src/main/frontend && npm ci && npm run build; \
	END=$$(date +%s); \
	ELAPSED=$$((END-START)); \
	echo ">>> Frontend build completed in $$((ELAPSED/3600))h $$(((ELAPSED%3600)/60))m $$((ELAPSED%60))s"

## ----- Backend -----
## build/backend/jvm: Build the backend for JVM environment (without frontend)
.PHONY: build/backend/jvm
build/backend/jvm:
	@START=$$(date +%s); \
	echo '>>> Building backend for JVM...'; \
	./mvnw -B -ntp clean package -Dmaven.test.skip -T1C -DargLine="Xms2g -Xmx2g" --batch-mode -q; \
	END=$$(date +%s); \
	ELAPSED=$$((END-START)); \
	echo ">>> Backend JVM build completed in $$((ELAPSED/3600))h $$(((ELAPSED%3600)/60))m $$((ELAPSED%60))s"

## build/backend/graalvm: Build the backend as GraalVM native image (without frontend)
.PHONY: build/backend/graalvm
build/backend/graalvm:
	@START=$$(date +%s) && \
	echo '>>> Loading environment variables from .env...' && \
	set -a && \
	. $(CURDIR)/.env && \
	set +a && \
	echo '>>> Building backend GraalVM native image...' && \
	./mvnw -B -ntp clean package -Dmaven.test.skip -Pnative native:compile && \
	END=$$(date +%s) && \
	ELAPSED=$$((END-START)) && \
	echo ">>> Backend GraalVM build completed in $$((ELAPSED/3600))h $$(((ELAPSED%3600)/60))m $$((ELAPSED%60))s"

## ----- Full -----
## build/jvm: Build frontend + backend for JVM
.PHONY: build/jvm
build/jvm: build/frontend build/backend/jvm
	@echo '>>> Full JVM build completed!'

## build/graalvm: Build frontend + backend as GraalVM native image
.PHONY: build/graalvm
build/graalvm: build/frontend build/backend/graalvm
	@echo '>>> Full GraalVM build completed!'

## ----- Docker JVM -----
## build/docker/jvm: Build a Docker image with JVM (full build from scratch)
.PHONY: build/docker/jvm
build/docker/jvm:
	@START=$$(date +%s); \
	echo '>>> Building docker image for JVM (full mode)...'; \
	DOCKER_BUILDKIT=1 docker build --build-arg BUILD_MODE=full --tag '$(REGISTRY_HOST)/$(PROJECT_NAME):$(POM_VERSION)-jvm' -f Dockerfile_jvm .; \
	END=$$(date +%s); \
	ELAPSED=$$((END-START)); \
	echo ">>> Docker JVM image build completed in $$((ELAPSED/3600))h $$(((ELAPSED%3600)/60))m $$((ELAPSED%60))s"

## build/docker/local/jvm: Build a Docker image with JVM using pre-built artifacts (fast)
.PHONY: build/docker/local/jvm
build/docker/local/jvm:
	@START=$$(date +%s); \
	echo '>>> Building docker image for JVM (local mode - using pre-built JAR)...'; \
	DOCKER_BUILDKIT=1 docker build --build-arg BUILD_MODE=local --tag '$(REGISTRY_HOST)/$(PROJECT_NAME):$(POM_VERSION)-jvm' -f Dockerfile_jvm .; \
	END=$$(date +%s); \
	ELAPSED=$$((END-START)); \
	echo ">>> Docker JVM image build completed in $$((ELAPSED/3600))h $$(((ELAPSED%3600)/60))m $$((ELAPSED%60))s"

## build/docker/force/jvm: Build a Docker image with JVM without caching layers (For debugging)
.PHONY: build/docker/force/jvm
build/docker/force/jvm:
	@START=$$(date +%s); \
	echo '>>> Building docker image for JVM without caching layers'; \
	DOCKER_BUILDKIT=1 docker build --build-arg BUILD_MODE=full --tag '$(REGISTRY_HOST)/$(PROJECT_NAME):$(POM_VERSION)-jvm' -f Dockerfile_jvm . --progress=plain --no-cache; \
	END=$$(date +%s); \
	ELAPSED=$$((END-START)); \
	echo ">>> Docker JVM force image build completed in $$((ELAPSED/3600))h $$(((ELAPSED%3600)/60))m $$((ELAPSED%60))s"

## ----- Docker GraalVM -----
## build/docker/graalvm: Build a Docker image with GraalVM (full build from scratch)
.PHONY: build/docker/graalvm
build/docker/graalvm:
	@START=$$(date +%s); \
	echo '>>> Building docker image for GraalVM (full mode)...'; \
	DOCKER_BUILDKIT=1 docker build --build-arg BUILD_MODE=full --tag '$(REGISTRY_HOST)/$(PROJECT_NAME):$(POM_VERSION)-graalvm' -f Dockerfile_graalvm .; \
	END=$$(date +%s); \
	ELAPSED=$$((END-START)); \
	echo ">>> Docker GraalVM image build completed in $$((ELAPSED/3600))h $$(((ELAPSED%3600)/60))m $$((ELAPSED%60))s"

## build/docker/local/graalvm: Build a Docker image with GraalVM using pre-built artifacts (fast)
.PHONY: build/docker/local/graalvm
build/docker/local/graalvm:
	@START=$$(date +%s); \
	echo '>>> Building docker image for GraalVM (local mode - using pre-built native binary)...'; \
	DOCKER_BUILDKIT=1 docker build --build-arg BUILD_MODE=local --tag '$(REGISTRY_HOST)/$(PROJECT_NAME):$(POM_VERSION)-graalvm' -f Dockerfile_graalvm .; \
	END=$$(date +%s); \
	ELAPSED=$$((END-START)); \
	echo ">>> Docker GraalVM image build completed in $$((ELAPSED/3600))h $$(((ELAPSED%3600)/60))m $$((ELAPSED%60))s"

## build/docker/force/graalvm: Build a Docker image with GraalVM without caching layers (For debugging)
.PHONY: build/docker/force/graalvm
build/docker/force/graalvm:
	@START=$$(date +%s); \
	echo '>>> Building docker image for GraalVM without caching layers'; \
	DOCKER_BUILDKIT=1 docker build --build-arg BUILD_MODE=full --tag '$(REGISTRY_HOST)/$(PROJECT_NAME):$(POM_VERSION)-graalvm' -f Dockerfile_graalvm . --progress=plain --no-cache; \
	END=$$(date +%s); \
	ELAPSED=$$((END-START)); \
	echo ">>> Docker GraalVM force image build completed in $$((ELAPSED/3600))h $$(((ELAPSED%3600)/60))m $$((ELAPSED%60))s"





# ==================================================================================== #
## ===== VERSION =====
# ==================================================================================== #
## version: Display current project version
.PHONY: version
version:
	@echo "$(POM_VERSION)"

## version/set: Set new project version (usage; make version/set 1.0.23)
.PHONY: version/set
version/set:
	@if [ -z "$(filter-out version/set,$(MAKECMDGOALS))" ]; then \
		echo "ERROR: Version number is required"; \
		echo "Usage: make version/set 1.0.23"; \
		exit 1; \
	fi
	@echo ">>> Setting project version to $(filter-out version/set,$(MAKECMDGOALS))..."
	@echo ">>> Updating pom.xml..."
	@./mvnw -B -ntp versions:set -DnewVersion=$(filter-out version/set,$(MAKECMDGOALS)) -DgenerateBackupPoms=false
	@echo ">>> Updating package.json..."
	@cd src/main/frontend && npm version $(filter-out version/set,$(MAKECMDGOALS)) --no-git-tag-version --allow-same-version
	@echo ">>> Project version updated to $(filter-out version/set,$(MAKECMDGOALS)) (pom.xml + package.json)"

# Prevent make from treating version number as a target
%:
	@:





# ==================================================================================== #
## ===== QUALITY =====
# ==================================================================================== #
## ----- Geral -----
## snyk/test: Scan all dependencies for vulnerabilities (backend + frontend, requires SNYK_TOKEN env var)
.PHONY: snyk/test
snyk/test: snyk/test/backend snyk/test/frontend
	@echo ">>> Full Snyk scan completed!"

## snyk/monitor: Upload all project snapshots to Snyk (backend + frontend, requires SNYK_TOKEN env var)
.PHONY: snyk/monitor
snyk/monitor: snyk/monitor/backend snyk/monitor/frontend
	@echo ">>> All project snapshots uploaded successfully!"
	@echo ">>> View results in your Snyk dashboard"

## ----- Backend -----
## snyk/test/backend: Scan backend (Maven) dependencies for vulnerabilities
.PHONY: snyk/test/backend
snyk/test/backend:
	@echo ">>> Running Snyk vulnerability scan on backend..."
	@if [ -z "$$SNYK_TOKEN" ]; then \
		echo "ERROR: SNYK_TOKEN environment variable is not set"; \
		echo "Please set it with: export SNYK_TOKEN=your_token_here"; \
		exit 1; \
	fi
	./mvnw -B -ntp snyk:test
	@echo ">>> Backend Snyk scan completed!"

## snyk/monitor/backend: Upload backend snapshot to Snyk for continuous monitoring
.PHONY: snyk/monitor/backend
snyk/monitor/backend:
	@echo ">>> Uploading backend snapshot to Snyk..."
	@if [ -z "$$SNYK_TOKEN" ]; then \
		echo "ERROR: SNYK_TOKEN environment variable is not set"; \
		echo "Please set it with: export SNYK_TOKEN=your_token_here"; \
		exit 1; \
	fi
	./mvnw -B -ntp snyk:monitor
	@echo ">>> Backend snapshot uploaded!"

## ----- Frontend -----
## snyk/test/frontend: Scan frontend (npm) dependencies for vulnerabilities
.PHONY: snyk/test/frontend
snyk/test/frontend:
	@echo ">>> Running Snyk vulnerability scan on frontend..."
	@if [ -z "$$SNYK_TOKEN" ]; then \
		echo "ERROR: SNYK_TOKEN environment variable is not set"; \
		echo "Please set it with: export SNYK_TOKEN=your_token_here"; \
		exit 1; \
	fi
	cd src/main/frontend && npm run snyk:test
	@echo ">>> Frontend Snyk scan completed!"

## snyk/monitor/frontend: Upload frontend snapshot to Snyk for continuous monitoring
.PHONY: snyk/monitor/frontend
snyk/monitor/frontend:
	@echo ">>> Uploading frontend snapshot to Snyk..."
	@if [ -z "$$SNYK_TOKEN" ]; then \
		echo "ERROR: SNYK_TOKEN environment variable is not set"; \
		echo "Please set it with: export SNYK_TOKEN=your_token_here"; \
		exit 1; \
	fi
	cd src/main/frontend && npm run snyk:monitor
	@echo ">>> Frontend snapshot uploaded!"

## sonar: Publish analysis results to SonarCloud (run 'make test' first, requires SONAR_TOKEN env var)
.PHONY: sonar
sonar:
	@echo ">>> Publishing analysis to SonarCloud..."
	@if [ -z "$$SONAR_TOKEN" ]; then \
		echo "ERROR: SONAR_TOKEN environment variable is not set"; \
		echo "Please set it with: export SONAR_TOKEN=your_token_here"; \
		exit 1; \
	fi
	@if [ ! -f "target/site/jacoco/jacoco.xml" ]; then \
		echo "ERROR: JaCoCo coverage report not found"; \
		echo "Please run 'make test' first to generate the coverage report"; \
		exit 1; \
	fi
	./mvnw -B -ntp sonar:sonar
	@echo ">>> Analysis published successfully!"
	@echo ">>> View results at: https://sonarcloud.io/dashboard?id=UnDer-7_sanjy-client-web"





# ==================================================================================== #
## ===== CODING STYLE =====
# ==================================================================================== #
## ----- Geral -----
## fmt: Format all source code (backend + frontend)
.PHONY: fmt
fmt: fmt/backend fmt/frontend

## fmt/check: Check all code formatting (backend + frontend)
.PHONY: fmt/check
fmt/check: fmt/backend/check fmt/frontend/check

## ----- Backend -----
## fmt/backend: Format backend code using Spotless
.PHONY: fmt/backend
fmt/backend:
	@echo ">>> Formatting backend source code…"
	./mvnw -B -ntp clean spotless:apply

## fmt/backend/check: Check backend code formatting
.PHONY: fmt/backend/check
fmt/backend/check:
	@echo ">>> Checking backend code formatting…"
	./mvnw -B -ntp clean spotless:check

## ----- Frontend -----
## fmt/frontend: Format frontend code using Prettier
.PHONY: fmt/frontend
fmt/frontend:
	@echo ">>> Formatting frontend source code…"
	cd src/main/frontend && npm run format

## fmt/frontend/check: Check frontend code formatting
.PHONY: fmt/frontend/check
fmt/frontend/check:
	@echo ">>> Checking frontend code formatting…"
	cd src/main/frontend && npm run format:check

## ----- Linting -----

## lint: Verify code compliance with Checkstyle standards
.PHONY: lint
lint:
	@echo ">>> Running Checkstyle validation…"
	@./mvnw -B -ntp clean checkstyle:check || (echo "" && \
	echo "==========================================================================" && \
	echo " ⚠️  CODE STYLE VIOLATIONS DETECTED  ⚠️" && \
	echo "==========================================================================" && \
	echo "" && \
	echo "The code does not comply with the project's coding standards." && \
	echo "" && \
	echo "To see a detailed HTML report with specific violations, run:" && \
	echo "    make lint/report" && \
	echo "" && \
	echo "The report makes it much easier to identify and fix the issues." && \
	echo "==========================================================================" && \
	echo "" && exit 1)

## lint/report: Generate detailed HTML report of Checkstyle violations
.PHONY: lint/report
lint/report:
	@echo ">>> Generating Checkstyle HTML report…"
	@./mvnw -B -ntp compile checkstyle:checkstyle && (echo "" && \
	echo "==========================================================================" && \
	echo " ✅  CHECKSTYLE REPORT GENERATED SUCCESSFULLY  ✅" && \
	echo "==========================================================================" && \
	echo "" && \
	echo "Report location: target/reports/checkstyle.html" && \
	echo "" && \
	echo "Simply open it in your browser to view detailed violation information." && \
	echo "==========================================================================" && \
	echo "")
