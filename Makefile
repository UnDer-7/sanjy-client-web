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
## ===== COMPILE =====
# ==================================================================================== #
## compile: Just compile the application
.PHONY: compile
compile:
	@echo ">>> Compiling…"
	./mvnw -B -ntp clean compile




# ==================================================================================== #
## ===== QUALITY =====
# ==================================================================================== #
## snyk/test: Scan for vulnerabilities in dependencies and code (requires SNYK_TOKEN env var)
.PHONY: snyk/test
snyk/test:
	@echo ">>> Running Snyk vulnerability scan..."
	@if [ -z "$$SNYK_TOKEN" ]; then \
		echo "ERROR: SNYK_TOKEN environment variable is not set"; \
		echo "Please set it with: export SNYK_TOKEN=your_token_here"; \
		exit 1; \
	fi
	./mvnw -B -ntp snyk:test
	@echo ">>> Snyk scan completed!"

## snyk/monitor: Upload project snapshot to Snyk for continuous monitoring (requires SNYK_TOKEN env var)
.PHONY: snyk/monitor
snyk/monitor:
	@echo ">>> Uploading project snapshot to Snyk..."
	@if [ -z "$$SNYK_TOKEN" ]; then \
		echo "ERROR: SNYK_TOKEN environment variable is not set"; \
		echo "Please set it with: export SNYK_TOKEN=your_token_here"; \
		exit 1; \
	fi
	./mvnw -B -ntp snyk:monitor
	@echo ">>> Project snapshot uploaded successfully!"
	@echo ">>> View results in your Snyk dashboard"

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
