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
## ===== CODING STYLE =====
# ==================================================================================== #
## fmt: Format all source code files using Spotless
.PHONY: fmt
fmt:
	@echo ">>> Formatting all source code files…"
	./mvnw -B -ntp clean spotless:apply

## fmt/check: Check code formatting without applying changes
.PHONY: fmt/check
fmt/check:
	@echo ">>> Checking code formatting…"
	./mvnw -B -ntp clean spotless:check

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
