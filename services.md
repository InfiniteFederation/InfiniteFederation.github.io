---
title: Services
permalink: /services/
---

## 1) Trino/Starburst Foundations
- Cluster sizing & topology (coord/worker, gateway, HA)
- Catalog strategy (Iceberg, Hive, JDBC, Delta, object storage)
- Security: SSO/SAML/OIDC, ABAC/RBAC, masking, token redaction
- Network: mTLS, ingress, egress, service mesh

**Deliverables:** architecture doc, infra as code (K8s YAML/Helm), runbooks.

## 2) Data Lakehouse on Iceberg
- Polaris/Nessie/REST catalog options
- Table formats, partitioning, compaction, vacuuming
- Schema evolution, CDC & MERGE correctness

## 3) Performance & Cost Tuning
- Spill, exchange buffers, memory calculators
- Resource groups, queuing, workload isolation
- Benchmarking: TPC-DS style, perf gates in CI

## 4) Governance & Observability
- Tag-based policies, column/row filters
- OpenMetadata integration, lineage
- Trino events → metrics, alerts, SLOs, dashboards

## 5) Migrations & Upgrades
- Starburst → Trino (and vice-versa)
- Major version upgrades with compat matrices
- Blue/green & drain workflows

## 6) AI & Text-to-SQL Enablement
- Guardrailed SQL generation
- Query templates, semantic layers, ACL-aware agents

[Download the detailed service catalog →](/playbooks/)
