---
title: "Trino Readiness: 20 Questions to Answer First"
excerpt: "A practical checklist before you scale queries to thousands of users."
---

1. What are your latency & concurrency SLOs?
2. Are catalogs mapped to data-class tags (PII/PCI)?
3. Do you have a resource-group policy per persona?
4. Which upgrade window & rollback do you enforce?
5. How will you prove MERGE correctness over time?
