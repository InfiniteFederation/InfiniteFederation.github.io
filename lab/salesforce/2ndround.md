---
layout: default
title: Salesforce 2nd Round Prep
permalink: /lab/salesforce/2nd-round-prep/
description: Focused preparation notes for the Salesforce second-round interview covering concepts, system design, and implementation details.
---

# Salesforce 2nd Round Prep

This round is not generic. The expected style is:

> Explain like an architect, but stay grounded like a data engineer.

The content below is tuned for an interviewer with a background in EDW, MySQL, Python, and Tableau.

## Table of Contents

1. [How This Round Will Likely Flow](#how-this-round-will-likely-flow)
2. [Part 1: Concepts](#part-1-concepts)
3. [Part 2: System Design](#part-2-system-design)
4. [Part 3: Implementation Deep Dive](#part-3-implementation-deep-dive)
5. [Golden Story](#golden-story)
6. [Common Traps](#common-traps)
7. [One Strong Line To Use](#one-strong-line-to-use)
8. [Final Preparation Checklist](#final-preparation-checklist)
9. [Last-Mile Prep](#last-mile-prep)

---

## How This Round Will Likely Flow

### 1. Concepts

**Likely duration:** 15 to 20 minutes

They want to know:

- Do you understand fundamentals clearly?
- Can you explain them simply?
- Can you connect them to practical use?

### 2. System Design

**Likely duration:** 20 to 25 minutes

They want to know:

- Can you design something usable by the business?
- Can you structure your answer cleanly?
- Do you understand trade-offs?

### 3. Implementation Deep Dive

**Likely duration:** 15 to 20 minutes

They want to know:

- Do you understand how the system really works?
- Can you discuss failure handling and scale?
- Are you strong beyond diagrams?

---

## Part 1: Concepts

### Must-Know Topics

#### 1. Data Warehouse Fundamentals

Be ready to explain:

- Fact vs dimension
- Star vs snowflake schema
- SCD (slowly changing dimensions)
- ETL vs ELT

**Simple interview line:**

> Fact tables store measurable events, while dimensions provide context for analysis.

#### 2. Batch vs Streaming

Be very clear on the trade-offs:

| Batch | Streaming |
|---|---|
| high latency | low latency |
| simpler operations | higher complexity |
| cost efficient | better for real-time use cases |

**Strong line to add:**

> In practice, most systems are hybrid rather than purely batch or purely streaming.

#### 3. CDC

This is especially important.

**Suggested explanation:**

> CDC captures incremental changes from source systems like MySQL by reading logs such as binlogs, then pushing those changes into downstream processing systems.

Be ready to discuss:

- snapshot plus incremental load
- ordering issues
- deduplication
- idempotency

#### 4. Data Quality

If asked how you ensure correctness, cover:

- validation rules
- schema checks
- deduplication
- monitoring
- reconciliation

---

## Part 2: System Design

This is the strongest part of the round for you if you keep the answer structured.

### Likely Prompt

> Design a data platform or analytics system.

### The Best Structure To Reuse

#### 1. Clarify First

Ask:

- Is this batch or real-time?
- What is the scale?
- What are the data sources?
- Who are the users?
- What latency is acceptable?

#### 2. Give A High-Level Design

Start with:

> I’d design this as a layered data platform.

Then walk through:

##### Ingestion

- APIs
- databases
- logs
- CDC plus batch

##### Processing

- transformation layer
- SQL
- Python
- dbt-style modeling if relevant

##### Storage

- data lake or warehouse
- Iceberg-style table layer if relevant

##### Query Layer

- SQL access layer
- Trino-style concept if relevant

##### Consumption

- BI tools
- dashboards
- reporting
- downstream applications

### Speak In The Interviewer’s Language

Instead of saying:

> data mesh plus federated engine

Prefer:

> Think of it like a modern data warehouse with decoupled storage and compute.

That framing will land better with an EDW-oriented interviewer.

---

## Part 3: Implementation Deep Dive

This is where you can separate yourself.

### Likely Deep Questions

#### How Does CDC Actually Work?

Answer structure:

1. the database writes changes to the binlog
2. a CDC tool reads the log
3. the changes are converted into events
4. events are pushed to Kafka or another transport
5. downstream consumers apply the changes

#### How Do You Handle Duplicates?

Say:

- idempotent writes
- primary key plus timestamp or version
- merge or upsert logic
- deduplication during processing

#### How Do You Handle Late Data?

Say:

- watermarking
- reprocessing
- partition updates
- backfills where required

#### How Do You Optimize Queries?

Say:

- partitioning
- indexing for MySQL
- predicate pushdown
- caching
- reducing scanned data

#### How Do You Scale?

Say:

- horizontal scaling
- partition-based parallelism
- distributed compute
- workload isolation

---

## Golden Story

If you get an open-ended question and want a strong answer fast, use:

### Vulnerability Data Pipeline

Use this structure:

1. Problem: slow response to vulnerabilities
2. Solution: unified data platform
3. Ingestion: APIs plus CDC
4. Processing: streaming plus batch
5. Storage: unified data layer
6. Query: SQL-based access
7. Outcome: faster response and better decisions

This works well because it naturally connects:

- data engineering
- analytics
- system design
- business impact

---

## Common Traps

### 1. Too Theoretical

Avoid going too deep into topics like:

- CAP theorem
- distributed consensus

unless the interviewer takes you there.

### 2. Too Tool-Heavy

Do not say:

> We use Trino, Iceberg, Kafka

without explaining why each choice matters.

### 3. No Business Context

Always connect the design back to:

- analytics
- reporting
- decision-making
- business speed

---

## One Strong Line To Use

Use this once during the round:

> I usually design systems starting from the analytics use case, then work backwards to ingestion and storage.

That is strong because it sounds business-aware and architecture-minded.

---

## Final Preparation Checklist

Before the interview, be ready to explain:

- CDC in depth
- star schema and SCD
- batch vs streaming
- an end-to-end data pipeline
- query optimization
- handling failures

---

## Last-Mile Prep

The highest-value next step is a mock in this exact format:

- concepts
- system design
- implementation deep dive

You can practice with this prompt:

> Start mock interview
