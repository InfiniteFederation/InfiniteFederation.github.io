---
title: "Banking Data Mesh Migration: Starburst→Trino"
permalink: /case-studies/starburst-migration-bank/
---
# Banking Data Mesh Migration: Starburst→Trino

## Challenge
A Tier-1 bank was struggling with mixed Trino/Starburst versions across different business units, leading to rising costs, upgrade friction, and inconsistent governance across their federated data architecture.

## Solution
We migrated the entire bank to a unified Trino-based data mesh while maintaining zero downtime and improving performance across all domains.

### Key Components
- **Federated Query Engine**: Unified Trino cluster replacing mixed Starburst/Trino deployments
- **Domain Isolation**: Resource groups and workload isolation for each business unit
- **Governance Framework**: Centralized policy management with domain-specific controls
- **Migration Strategy**: Blue/green deployment with automated drain and cutover

### Implementation
- **Blue/Green Migration**: Zero-downtime cutover behind existing gateway
- **Drain Automation**: Intelligent query routing and graceful shutdown
- **Resource Optimization**: Spill tuning and memory management
- **MERGE Audit**: Data consistency validation across all domains

## Results
- **35% reduction** in infrastructure costs through unified architecture
- **48% faster** median query latency across all business units
- **Zero downtime** migration with automated rollback capabilities
- **100% compliance** with banking regulations through improved governance

## Technology Stack
- **Query Engine**: Trino with Starburst Enterprise features
- **Storage**: Apache Iceberg with cross-domain data sharing
- **Governance**: OPA-based policy engine with RBAC
- **Monitoring**: Custom dashboards with domain-specific metrics

## Business Impact
The unified data mesh enabled:
- Consistent data access patterns across all business units
- Reduced operational overhead with centralized management
- Faster regulatory reporting through improved governance
- Cost savings that funded additional data initiatives
