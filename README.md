# Lumen

Lumen is intended to become a multi-tenant RAG and agent platform. This repository currently contains an **early foundation only**: a Java 25 / Spring Boot 4 API with JWT resource-server configuration, a tenant-claim endpoint, health and Prometheus endpoints, virtual-thread configuration, and unit tests. It is not yet a working RAG system or a deployable multi-tenant service.

## Run the foundation

Prerequisites: Java 25, Gradle 9.2.1, and an OpenID Connect issuer you control. Set `LUMEN_JWT_ISSUER_URI` to its issuer URI, then run `gradle :lumen-api:bootRun`. Spring Security validates JWTs using the issuer's keys. `GET /v1/tenant` requires a valid bearer token containing `tenant_id`. Health is public; other endpoints require authentication. Restrict management access at the network layer before deploying.

Run unit tests with `gradle test`. The tests do not establish cross-tenant isolation; they only exercise tenant-claim handling.

## Security status

No credentials or tokens belong in this repository. Supply issuer settings and any future credentials from the environment or a secret manager. The current JWT setup validates issuer and signature but does not yet enforce an audience or tenant authorization policy. Do not expose this prototype to untrusted traffic. Tenant-aware data access and database row-level security are not yet implemented or tested.

## Measurements and AWS

No workload, retrieval, cost, reliability, or AWS results have been measured. No EKS deployment exists. Local observations cannot establish AWS throughput, latency, or cost; any future estimates must identify assumptions and stay distinct from measured results. Do not use AWS deployment or performance claims on a resume until verified.

## Remaining work

Ingestion and outbox, Kafka worker/retries/DLQ, S3-compatible storage, pgvector and row-level security, hybrid retrieval, model integration and streaming, MCP agent, quotas, evaluations, tracing, load tests, Compose, and cloud infrastructure remain to be implemented. The original build plan is the intended roadmap, not evidence that these features exist.
