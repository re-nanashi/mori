# Developer notes
## Tasks
### Initial Setup (Phase 0)
- [x] Create GitHub Repository
- [ ] Enable branch protection on main
    - [x] Require a pull request before merging
    - [ ] Required status checks cannot be empty. **Solution:** write a workflow file first.
- [x] Create GitHub Project board
- [x] Create the initial folder structure
- [x] Write root pom.xml; initial config for Spring Boot and Spring Cloud
- [x] Write root docker-compose.yml (initially just postgres); (redis, kafka, rabbitmq, keycloak, localstack, zipkin, prometheus, grafana)
- [x] Write root Makefile with up, down, build, test, proto, migrate targets
- [ ] Create eureka-server service — @EnableEurekaServer, application.yml, Dockerfile
- [ ] Create config-server service — @EnableConfigServer, filesystem config in dev
- [ ] Verify: make up starts all infrastructure without errors
- [ ] Create ARCHITECTURE.md and first ADR explaining the monorepo decision

### Backlogs
- [ ] Write initial ci.yml
  - Need to develop first service (e.g. user-service)
  - Get it building locally with mvn package
  - Write at least a few unit tests
  - THEN create ci.yml so it has something real to do
- [ ] Write deploy-prod.yml
  - Wait until production code is ready
- [ ] Update docker-compose.yml to include other containers
- [ ] Add Makefile commands: lint, proto, migrate, logs

--- 

## Daily notes
### June 02, 2026
#### Overview
- I'm doing the initial repository setup (folder structure, infra, etc.) 
- Postpone creating deploy-prod.yml for the time being; wait for actual production code ready
- Postpone creating ci.yml for the time being; create a service first
