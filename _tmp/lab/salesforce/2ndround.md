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
5. [Platform Vocabulary You Can Use](#platform-vocabulary-you-can-use)
6. [Golden Story](#golden-story)
7. [Common Traps](#common-traps)
8. [One Strong Line To Use](#one-strong-line-to-use)
9. [Final Preparation Checklist](#final-preparation-checklist)
10. [Last-Mile Prep](#last-mile-prep)

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

**Use one connected vulnerability-management example across all four topics**

##### Fact vs Dimension

**Example fact table**

`fact_cve_asset_impact`

```text
cve_id | asset_key | owner_key | detection_date_key | severity | impacted_flag
```

**Example dimension tables**

`dim_asset`

```text
asset_key | asset_id | hostname | environment | business_service
```

`dim_asset_owner`

```text
owner_key | owner_id | owner_name | team | region
```

**How to explain it**

- facts store measurable business events
- dimensions store descriptive context

**Interview line**

> In a vulnerability analytics platform, the impacted CVE-asset relationship is the fact because it represents the measurable security event, while asset and asset owner are dimensions because they describe the context of that impact.

**Tools / technology**

- common in Snowflake, BigQuery, Redshift, Databricks, dbt models
- consumed easily by Tableau, Power BI, and Looker

##### Star vs Snowflake Schema

**Star schema example**

`fact_cve_asset_impact` joins directly to:

- `dim_asset`
- `dim_asset_owner`
- `dim_cve`
- `dim_date`
- `dim_environment`

`dim_asset` might contain:

```text
asset_key | asset_id | hostname | environment_name | business_service_name
```

**Why star schema works well**

- fewer joins
- simpler for analysts
- easier for BI tools

**Snowflake schema example**

`fact_cve_asset_impact` joins to `dim_asset`, and then `dim_asset` joins to:

- `dim_environment`
- `dim_business_service`
- `dim_asset_platform`

**Why snowflake schema is different**

- more normalized
- less redundancy
- more joins

**Interview line**

> I usually prefer star schema for analytics and Tableau-style reporting, while snowflake schema is useful when dimensions need stronger normalization and reuse.

**Tools / technology**

- star schema is common in BI-oriented warehouses and semantic layers
- snowflake schema is more common in traditional EDW-style normalized reference models

##### SCD (Slowly Changing Dimensions)

Use asset owner as the example.

**Type 1**

Overwrite old value:

```text
asset_id | owner_name
srv-101  | Team-B
```

**Use when**

- only current state matters
- history is not required

**Type 2**

Keep history by creating a new row:

```text
asset_owner_key | asset_id | owner_name | start_date | end_date   | is_current
1               | srv-101  | Team-A     | 2025-01-01 | 2026-03-31 | false
2               | srv-101  | Team-B     | 2026-04-01 | null       | true
```

**Use when**

- business wants historical truth
- reports must reflect what was true at the time of the event

**Type 3**

Keep current and previous value:

```text
asset_id | current_owner | previous_owner
srv-101  | Team-B        | Team-A
```

**Use when**

- limited history is enough

**Interview line**

> If the business wants to know who owned an asset when a CVE was detected, I would use SCD Type 2.

**What about Type 4, Type 5, and Type 6?**

These do exist, but they are less commonly discussed than Types 1, 2, and 3.

**Type 4**

Keep current values in one table and historical values in a separate history table.

Example:

```text
dim_asset_owner_current
asset_id | owner_name
srv-101  | Team-B
```

```text
dim_asset_owner_history
asset_id | owner_name | start_date | end_date
srv-101  | Team-A     | 2025-01-01 | 2026-03-31
srv-101  | Team-B     | 2026-04-01 | null
```

**When to use it**

- current queries should stay simple
- history is still needed, but in a separate structure

**Type 5**

Type 5 is less consistently defined across teams, but usually means:

- current-state overwrite in the main dimension, like Type 1
- plus a mini-dimension or separate structure to preserve some historical context

**Interview-safe line**

> Type 5 is less standardized, so if I mention it, I would clarify what definition the team uses.

**Type 6**

Type 6 is a hybrid of Type 1, Type 2, and Type 3.

Example:

```text
asset_owner_key | asset_id | current_owner | previous_owner | start_date | end_date   | is_current
1               | srv-101  | Team-A        | null           | 2025-01-01 | 2026-03-31 | false
2               | srv-101  | Team-B        | Team-A         | 2026-04-01 | null       | true
```

**Why Type 6 exists**

- full history is preserved like Type 2
- previous value is easy to access like Type 3
- some current-state overwrite behavior can still be applied like Type 1

**Best interview summary**

> The most important SCD types in practice are Type 1, Type 2, and Type 3. Type 4 and Type 6 also exist, while Type 5 is less standardized and should be explained carefully.

**Tools / technology**

- implemented with SQL `MERGE`
- common in Snowflake, BigQuery, Databricks, Delta Lake, Iceberg, and dbt snapshots

##### ETL vs ELT

Use vulnerability data coming from scanners, asset inventory, and MySQL-backed operational systems into an analytical platform.

**ETL**

Extract -> Transform -> Load

Flow:

- extract from MySQL, vulnerability scanners, or CMDB sources
- transform in Spark, Informatica, Talend, or SSIS
- load clean conformed data into the warehouse

**When it fits**

- older EDW style
- heavy pre-load transformation
- strong control before load

**ELT**

Extract -> Load -> Transform

Flow:

- extract from MySQL, scanner APIs, or CDC streams
- load raw data into Snowflake, BigQuery, Databricks, or lakehouse storage
- transform inside the target platform using SQL or dbt

**When it fits**

- modern cloud warehouses
- warehouse compute is strong enough to transform after load
- easier raw-data retention and replay

**Interview line**

> ETL transforms vulnerability and asset data before loading, while ELT lands raw scanner, asset, and ownership data first and then transforms it inside the analytical platform. In modern cloud systems, ELT is often preferred because it preserves raw data and makes replay easier when mappings or ownership logic change.

**Tools / technology**

- ETL: Informatica, Talend, SSIS, Spark jobs
- ELT: dbt, Snowflake SQL, BigQuery SQL, Databricks SQL
- ingestion: Fivetran, Airbyte, Debezium, Kafka Connect

#### 1A. Constraints In The Database World

Constraints are rules enforced by the database to keep data valid and consistent.

**The most important constraints**

- `PRIMARY KEY`: uniquely identifies each row
- `FOREIGN KEY`: enforces valid relationships between tables
- `UNIQUE`: prevents duplicate values
- `NOT NULL`: requires a value to exist
- `CHECK`: enforces allowed conditions or ranges
- `DEFAULT`: provides a default value if one is not supplied

**Simple example**

```sql
CREATE TABLE asset_owner (
  owner_id INT PRIMARY KEY,
  owner_name VARCHAR(100) NOT NULL
);

CREATE TABLE assets (
  asset_id VARCHAR(50) PRIMARY KEY,
  hostname VARCHAR(100) NOT NULL,
  owner_id INT,
  environment VARCHAR(20) CHECK (environment IN ('prod', 'nonprod')),
  status VARCHAR(20) DEFAULT 'active',
  FOREIGN KEY (owner_id) REFERENCES asset_owner(owner_id)
);

CREATE TABLE Persons (
    ID int NOT NULL,
    LastName varchar(255) NOT NULL,
    FirstName varchar(255),
    Age int,
    CONSTRAINT UC_Person UNIQUE (ID,LastName)
);
```

**How to explain it**

- primary key protects uniqueness
- foreign key protects referential integrity
- not null protects required fields
- check protects valid business values
- default makes inserts safer and more consistent

**Interview line**

> Constraints are database-enforced rules that protect data integrity, such as uniqueness, required values, valid ranges, and valid table relationships.

#### 1B. ACID Transactions In MySQL vs Iceberg

`ACID` stands for:

- `Atomicity`
- `Consistency`
- `Isolation`
- `Durability`

**Simple meaning**

- atomicity: either the full change happens or none of it happens
- consistency: data remains valid before and after the transaction
- isolation: concurrent work does not expose broken intermediate state
- durability: once committed, the change should survive failure

##### ACID in MySQL

MySQL, especially with `InnoDB`, is a transactional database engine. ACID is handled inside the database using transaction control, locks, and redo or undo logs.

**Example**

```sql
START TRANSACTION;

UPDATE assets
SET owner_id = 9002
WHERE asset_id = 'srv-101';

INSERT INTO asset_owner_audit (asset_id, old_owner_id, new_owner_id)
VALUES ('srv-101', 9001, 9002);

COMMIT;
```

**How to explain it**

- both statements succeed together or fail together
- other users should not see half-finished changes
- once committed, the data is durable in the database

**Interview line**

> In MySQL, ACID is handled by the database engine at transaction level, usually with row-level locking, transaction logs, and commit or rollback semantics.

##### ACID in Iceberg

Iceberg is not a traditional OLTP database. It is a table format over object storage, so ACID is implemented through snapshot-based table commits rather than row locks inside a database engine.

**Example**

```sql
MERGE INTO lake.security.curated_cve_asset_impact t
USING lake.security.raw_cve_asset_updates s
ON t.cve_id = s.cve_id
AND t.asset_id = s.asset_id
WHEN MATCHED THEN
  UPDATE SET
    owner_id = s.owner_id,
    severity = s.severity,
    status = s.status
WHEN NOT MATCHED THEN
  INSERT (cve_id, asset_id, owner_id, severity, environment, detection_ts, detection_date, status)
  VALUES (s.cve_id, s.asset_id, s.owner_id, s.severity, s.environment, s.detection_ts, s.detection_date, s.status);
```

**How to explain it**

- new data files are written first
- then Iceberg publishes a new metadata snapshot atomically
- readers see either the old snapshot or the new snapshot
- they do not see a half-written table state

**Interview line**

> In Iceberg, ACID is snapshot-based. Writers create new data files and then atomically publish a new table snapshot, so readers see a consistent table version.

##### The Practical Difference

- MySQL: ACID at database transaction level
- Iceberg: ACID at table snapshot level
- MySQL: optimized for transactional row updates
- Iceberg: optimized for large-scale analytical table changes

**CVE and asset example**

- MySQL example: update asset owner and write an audit row in one transaction
- Iceberg example: merge a batch of CVE and asset updates into a curated table and publish one new consistent snapshot

**Best short answer**

> MySQL gives ACID inside the database transaction engine. Iceberg gives ACID at the table-snapshot level on analytical storage.

#### 2. Batch vs Streaming

Be very clear on the trade-offs:

| Batch | Streaming |
|---|---|
| high latency | low latency |
| simpler operations | higher complexity |
| cost efficient | better for real-time use cases |

**Strong line to add:**

> In practice, most systems are hybrid rather than purely batch or purely streaming.

**How to explain it in a fuller answer**

Batch is the right choice when the business can tolerate delay and wants simplicity, lower cost, and easier operations. Streaming is the right choice when decisions need to be made continuously or with very low latency, such as fraud, alerting, or real-time monitoring.

**Good business example**

- batch: daily finance reporting, monthly customer segmentation, end-of-day reconciliation
- streaming: fraud alerts, vulnerability detection, operational dashboards, user-activity monitoring

**Trino / Iceberg angle**

> In systems I usually build, I often use streaming or CDC to land fresh data and then expose it through Iceberg tables for reliable analytics. Trino then gives low-latency SQL access to that curated layer, so the business gets near-real-time visibility without every query hitting the raw source directly.

**Strong interview version**

> I choose batch or streaming based on latency requirements first. If the business needs hourly or daily decisions, batch is often enough and is easier to operate. If the business needs continuous reaction, I add streaming, but I still usually pair it with a stable analytical storage layer for reporting and replay.

#### 3. CDC

This is especially important.

**Suggested explanation:**

> CDC captures incremental changes from source systems like MySQL by reading logs such as binlogs, then pushing those changes into downstream processing systems.

Be ready to discuss:

- snapshot plus incremental load
- ordering issues
- deduplication
- idempotency

**How to explain CDC end to end**

CDC starts with a source system, often MySQL or PostgreSQL, where changes are written to a transaction log such as a binlog or WAL. A CDC connector reads those changes, converts them into ordered change events, and pushes them downstream. Those events can then be consumed by Kafka, stream processors, or ingestion jobs and written into analytical storage such as Iceberg tables.

**The clean mental model**

1. snapshot existing data
2. start reading new changes from the log
3. merge both into a stable target table
4. keep processing changes continuously

**What can go wrong**

- events arrive out of order
- retries create duplicates
- deletes are ignored
- schema changes are not handled
- the snapshot overlaps incorrectly with incremental changes

**How to answer the hard follow-up**

> The difficult part of CDC is not just reading changes. It is preserving correctness under retries, ordering issues, late arrivals, and schema evolution.

**Trino / Iceberg angle**

> In a Trino and Iceberg style platform, CDC events often land in raw tables first. Then a merge or compaction process creates the curated Iceberg table that Trino queries. That gives both replayability and clean analytical reads.

#### 4. Data Quality

If asked how you ensure correctness, cover:

- validation rules
- schema checks
- deduplication
- monitoring
- reconciliation

**Full answer structure**

I usually think about data quality in four layers:

1. schema quality
2. content quality
3. pipeline reliability
4. business reconciliation

**Examples**

- schema quality: expected columns and types are present
- content quality: no impossible values like negative quantity or invalid dates
- pipeline reliability: freshness, row counts, retry safety
- business reconciliation: source totals and target totals match

**Strong interview line**

> Data quality is not one check at the end. It has to be enforced from ingestion through transformation and then monitored in production.

**Trino / Iceberg angle**

> With Iceberg, I usually separate raw and curated layers so I can preserve source fidelity but still enforce stronger validation in the curated tables that Trino exposes to users.

#### 5. Index vs Partition vs Primary Key

This is a very likely concept check because it tests whether you understand storage, query performance, and relational design at the same time.

**Simple way to explain it:**

- `primary key`: uniquely identifies a row in a table
- `index`: speeds up lookup on one or more columns
- `partition`: physically splits data into chunks, usually by a coarse key like date

**Good interview explanation:**

> A primary key is mainly about uniqueness and data integrity. An index is mainly about faster access paths. A partition is mainly about reducing how much data needs to be scanned.

**Example:**

- `asset_id` can be the primary key of an `assets` table
- an index on `owner_id` helps find all assets for a given owner quickly
- partitioning a vulnerability-impact table by `detection_date` helps skip old data when querying recent CVEs

**One-line distinction:**

> Primary key is about identity, index is about speed, and partition is about scan reduction and manageability.

**How a primary key works**

A primary key enforces uniqueness for each row. The database uses it to make sure two rows do not represent the same entity with the same identifier.

**Example:**

```text
assets
asset_id | hostname     | owner_id
srv-101  | payments-01  | 9001
srv-102  | billing-02   | 9002
```

If `asset_id` is the primary key:

- `srv-101` can appear only once
- inserting another row with `srv-101` should fail
- every row has one stable identity

**Interview line:**

> A primary key is mainly a data-integrity rule. It tells the system how to uniquely identify a row.

**How an index works**

An index is an additional data structure that helps the database find matching rows faster without scanning the whole table.

Think of it like the index at the back of a book:

- without an index, the database may scan many rows
- with an index, it can jump closer to the matching values

**Example:**

```sql
SELECT *
FROM assets
WHERE owner_id = 9001;
```

If there is an index on `owner_id`, MySQL can use that index to find rows for owner `9001` much faster than a full table scan.

**Interview line:**

> An index speeds up lookups and joins on selected columns, but it also adds storage cost and slows down writes because the index must be maintained.

**How partitioning works**

Partitioning physically organizes large data into chunks, usually by a coarse-grained column such as date, region, or environment. When a query filters on that partition column, the engine can skip partitions that cannot contain matching data.

**Example:**

Suppose `fact_cve_asset_impact` is partitioned by `detection_date`.

```sql
SELECT *
FROM fact_cve_asset_impact
WHERE detection_date = DATE '2026-04-14';
```

If the table is partitioned by `detection_date`, the engine can scan only the partition for `2026-04-14` instead of scanning the entire dataset.

**Interview line:**

> Partitioning does not uniquely identify rows and it is not the same as an index. Its main benefit is that it reduces how much data must be read.

**Trino / Iceberg angle**

> In MySQL, indexes matter a lot for transactional lookup performance. In Iceberg and Trino, traditional database indexes are not the main optimization tool. Partitioning, file pruning, metadata statistics, and good table layout matter more.

##### MySQL partitioning example

In MySQL, partitioning is managed by the database engine itself.

**Create table**

```sql
CREATE TABLE cve_asset_impact_mysql (
  impact_id BIGINT NOT NULL,
  cve_id VARCHAR(32) NOT NULL,
  asset_id VARCHAR(64) NOT NULL,
  owner_id INT NOT NULL,
  severity VARCHAR(16) NOT NULL,
  detection_date DATE NOT NULL,
  status VARCHAR(20) NOT NULL,
  PRIMARY KEY (impact_id, detection_date)
)
PARTITION BY RANGE COLUMNS (detection_date) (
  PARTITION p2026_01 VALUES LESS THAN ('2026-02-01'),
  PARTITION p2026_02 VALUES LESS THAN ('2026-03-01'),
  PARTITION p2026_03 VALUES LESS THAN ('2026-04-01'),
  PARTITION pmax VALUES LESS THAN (MAXVALUE)
);
```

**Insert sample data**

```sql
INSERT INTO cve_asset_impact_mysql
  (impact_id, cve_id, asset_id, owner_id, severity, detection_date, status)
VALUES
  (1, 'CVE-2026-1001', 'srv-101', 9001, 'CRITICAL', '2026-01-15', 'OPEN'),
  (2, 'CVE-2026-1002', 'srv-102', 9002, 'HIGH', '2026-02-10', 'OPEN'),
  (3, 'CVE-2026-1003', 'srv-103', 9001, 'MEDIUM', '2026-03-05', 'CLOSED');
```

**Query example**

```sql
SELECT cve_id, asset_id, severity
FROM cve_asset_impact_mysql
WHERE detection_date >= '2026-02-01'
  AND detection_date < '2026-03-01';
```

**Important MySQL note**

> In MySQL, the partition column must be included in every unique key, including the primary key.

##### Trino-on-Iceberg CREATE TABLE

In Iceberg, partitioning is stored as table metadata and is used by engines like Trino for partition and file pruning.

**Create table**

```sql
CREATE TABLE lake.security.curated_cve_asset_impact (
  cve_id VARCHAR,
  asset_id VARCHAR,
  owner_id INTEGER,
  severity VARCHAR,
  environment VARCHAR,
  detection_ts TIMESTAMP(6),
  detection_date DATE,
  status VARCHAR
)
WITH (
  format = 'PARQUET',
  partitioning = ARRAY['month(detection_date)', 'environment']
);
```

**Insert sample data**

```sql
INSERT INTO lake.security.curated_cve_asset_impact
  (cve_id, asset_id, owner_id, severity, environment, detection_ts, detection_date, status)
VALUES
  ('CVE-2026-1001', 'srv-101', 9001, 'CRITICAL', 'prod', TIMESTAMP '2026-01-15 10:00:00', DATE '2026-01-15', 'OPEN'),
  ('CVE-2026-1002', 'srv-102', 9002, 'HIGH', 'prod', TIMESTAMP '2026-02-10 08:30:00', DATE '2026-02-10', 'OPEN'),
  ('CVE-2026-1003', 'srv-103', 9001, 'MEDIUM', 'nonprod', TIMESTAMP '2026-02-11 09:00:00', DATE '2026-02-11', 'CLOSED');
```

**Query example**

```sql
SELECT cve_id, asset_id, severity
FROM lake.security.curated_cve_asset_impact
WHERE detection_date >= DATE '2026-02-01'
  AND detection_date < DATE '2026-03-01'
  AND environment = 'prod';
```

**How to explain the difference**

> In MySQL, partitioning is a physical layout feature in the transactional database. In Iceberg, partitioning is metadata-driven and helps analytical engines like Trino skip irrelevant files and partitions before scanning data.

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

##### What These Questions Mean And What Answers You Might Hear

These questions are not filler. They define the architecture.

If you ask good clarification questions, the interviewer usually gives you enough constraints to choose between batch, streaming, lakehouse, federation, caching, and governance patterns.

##### Is this batch or real-time?

**What this question means**

You are asking whether the business needs delayed reporting or near-immediate updates.

**Possible answers**

- batch: daily, hourly, or scheduled processing is enough
- near-real-time: data should appear within minutes
- real-time: data should appear within seconds
- hybrid: reporting can be batch, but alerts or operational views need fresher data

**CVE and asset example**

- batch answer: "A daily vulnerability exposure report is enough for leadership and compliance."
- near-real-time answer: "Security operations wants updated impacted-asset views every 5 to 15 minutes."
- hybrid answer: "Dashboards can refresh every hour, but critical CVE alerts should be available within a few minutes."

**How you can respond**

> If this is batch, I would favor simpler scheduled ingestion and transformation. If it is near-real-time or real-time, I would add CDC or streaming ingestion and a fresher serving layer.

##### What is the scale?

**What scale means**

Scale means how much data, how fast it arrives, how many users query it, and how large the system must grow.

You are really asking:

- how many records are we processing
- how often new data arrives
- how many concurrent users or queries we expect
- how much historical data we keep
- how many source systems are involved

**Possible answers**

- small: thousands to low millions of rows, a few users, limited concurrency
- medium: tens or hundreds of millions of rows, multiple teams, regular dashboard usage
- large: billions of rows, many sources, many concurrent users, long retention

**CVE and asset example**

- small scale: "We ingest CVEs from one scanner and maintain 50,000 assets."
- medium scale: "We ingest multiple scanner feeds and track 5 million asset-CVE relationships."
- large scale: "We process billions of vulnerability observations across environments, with many analysts and platform services querying at the same time."

**How you can respond**

> Scale affects partitioning, file layout, metadata design, compute sizing, and whether I need stronger workload isolation or source protection.

##### What are the data sources?

**What this question means**

You are asking where the data comes from and what ingestion pattern fits each source.

**Possible answers**

- relational databases such as MySQL or PostgreSQL
- scanner APIs or SaaS APIs
- flat files such as CSV, JSON, or Parquet
- logs and event streams
- CMDB, asset inventory, identity systems, or internal applications

**CVE and asset example**

- CVE source: vulnerability scanner API or feed
- asset source: CMDB, asset inventory, or device-management system
- asset owner source: HR, directory service, or internal ownership registry
- operational source: MySQL storing remediation or exception workflows

**How you can respond**

> I would map ingestion to source type. Databases are a strong fit for CDC, APIs need checkpointing and retry logic, and files need validation and idempotent loading.

##### Who are the users?

**What this question means**

You are asking who consumes the data and what kind of access pattern they need.

**Possible answers**

- analysts using BI tools
- security operations teams investigating active issues
- data engineers building pipelines
- internal applications or APIs
- governance, audit, or compliance users
- leadership consuming dashboards

**CVE and asset example**

- analysts want trend reporting by severity, owner, and business service
- security operations wants current impacted assets and owner lookup
- leadership wants high-level dashboards
- governance users want metadata, lineage, and access controls

**How you can respond**

> If the main users are analysts, I optimize for curated tables and BI friendliness. If the users are operational responders, I optimize more for freshness and targeted lookup performance.

##### What latency is acceptable?

**What this question means**

Latency means how long the business can wait between source change and usable data in the target system.

This is different from scale.

- scale is how much data and usage the system must handle
- latency is how quickly the data must become available

**Possible answers**

- seconds
- minutes
- hourly
- daily
- different latency for different use cases

**CVE and asset example**

- "Critical asset exposure must be visible within 2 minutes."
- "Daily executive reporting by 7 AM is enough."
- "Asset-owner mapping can update hourly, but CVE detection status should update every 5 minutes."

**How you can respond**

> Latency drives whether I choose scheduled batch, micro-batch, CDC, or streaming. It also affects whether I expose fresh federated reads, curated tables, or both.

##### A Good Combined Clarification Example

You can ask it like this:

> Before I design it, I want to clarify whether this is batch or near-real-time, what the data volume and concurrency look like, which sources are involved, who the users are, and how fresh the data needs to be.

If the interviewer answers:

- near-real-time
- multiple scanner APIs plus MySQL plus asset inventory
- millions of CVE-asset relationships
- analysts and security operations both use it
- freshness needed within 5 minutes

Then a strong response is:

> In that case, I would avoid a purely batch design. I would likely use CDC for MySQL, API ingestion with checkpointing, land the data into raw storage, curate it into Iceberg tables, and expose it through Trino for governed query access, with enough freshness for both dashboards and operational investigation.

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

### A More Complete System Design Script

If the interviewer asks for a modern analytics platform, you can answer in this flow:

#### Step 1: State the goal clearly

> I’m going to assume the goal is to build a platform that supports reliable analytics, self-service SQL access, and enough freshness for business reporting and operational decisions.

#### Step 2: Separate raw and curated layers

> I would separate the platform into raw ingestion, transformation, curated storage, and query serving. That makes it easier to preserve source fidelity while still exposing high-quality analytical tables.

#### Step 3: Describe ingestion

Use:

- CDC for databases
- APIs for SaaS systems
- batch file ingestion where needed
- event streams for real-time use cases

**Good interview line**

> I would not force every source into the same ingestion model. Databases are a strong fit for CDC, APIs need checkpointing and retries, and event streams need partitioning and replay strategy.

#### Step 4: Describe storage with your strengths

This is where your Trino and Iceberg background becomes an advantage.

> For storage, I would use object storage with Iceberg tables as the analytical table layer. Iceberg gives schema evolution, partition evolution, metadata-based pruning, and reliable table management. That makes it a strong fit for large-scale analytical systems where multiple compute engines may read the same data.

#### Step 5: Describe processing

> For processing, I would use SQL-first transformations where possible and use Spark or streaming engines where heavier stateful logic is needed. I prefer to keep transformations modular, testable, and layered so the business-facing tables stay understandable.

#### Step 6: Describe query serving

> For interactive SQL and federated access, I would use Trino conceptually. Trino works well as a query layer because it separates compute from storage and can query across lakehouse tables and external sources with a consistent SQL interface.

#### Step 7: Describe consumption in the interviewer’s language

> On top of that, I’d expose curated tables to BI tools like Tableau and to downstream applications that need governed access to data products.

### Strong Trino / Iceberg System Design Version

If you want a direct version that matches your experience:

1. sources: MySQL, APIs, event streams, operational systems
2. ingestion: CDC plus batch plus API pull
3. raw storage: object store landing zone
4. curated storage: Iceberg tables
5. processing: SQL, Spark, or stream processing depending on latency
6. query layer: Trino for interactive SQL and federation
7. governance: metadata catalog, RBAC or ABAC, lineage, masking
8. consumption: Tableau, analysts, internal apps, AI agents

### Trade-Offs To Mention Explicitly

- federation improves freshness but can increase source dependency
- ingestion improves control but adds latency and duplication
- Iceberg improves table management but requires good operational discipline
- Trino is excellent for interactive SQL, but very heavy transformations may still belong in Spark or another compute engine

### Strong Close For System Design

> So my design would use CDC and batch ingestion into a raw layer, curate the data into Iceberg tables, and expose it through Trino for governed SQL access. That gives a good balance of freshness, performance, and analytical usability.

### Speak In The Interviewer’s Language

Instead of saying:

> data mesh plus federated engine

Prefer:

> Think of it like a modern data warehouse with decoupled storage and compute.

That framing will land better with an EDW-oriented interviewer.

### How To Explain Your Current Platform

If the interviewer asks what you actually work with today, you can use this structure.

#### Federated Query Execution Plane

> The namespace-scoped execution plane is where query execution, orchestration, and storage-facing services run together. In my world that typically includes Trino for SQL execution, orchestration services, table metadata services, object storage, and operational backing stores.

**Components you can name**

- `gex`: custom governance execution engine
- `trino`: distributed SQL query engine
- `hms`: Hive Metastore style table metadata
- `minio`: object storage
- `mysql`: operational relational backing store
- `kestra`: orchestration and workflow layer

**Interview line**

> I think of the execution plane as the runtime layer where queries are planned, governed, and executed against shared analytical storage.

#### Metadata Plane

> The metadata plane is the layer that keeps technical metadata, lineage, discovery, and search available for users and systems.

**Components you can name**

- `om-server`: OpenMetadata server
- `om-ingestion`: metadata ingestion jobs
- `om-elasticsearch`: metadata search
- `om-mysql`: relational backing store for OpenMetadata

**Interview line**

> The metadata plane is important because federation without metadata quickly becomes hard to govern and hard to trust.

#### Unified Metadata Fabric

> The unified metadata fabric is the governance boundary that helps multiple engines share metadata safely without losing control over ownership and policy boundaries.

**Components you can name**

- `umf-gravitino`
- `umf-mysql`

**Interview line**

> I use the metadata fabric concept to separate storage and compute concerns from metadata governance concerns.

#### Authorization Exchange

> The authorization exchange is where policy decisions are evaluated and enforced at runtime.

**Components you can name**

- `moat`
- `opa`
- `mysql`

**Interview line**

> I treat authorization as a runtime decision system, not just a static config file, especially in federated query environments.

#### Data Catalog, Control, Explorer, and Observability POCs

You can describe these as proof-of-concept layers that validate the broader federation pattern.

- data catalog POCs: `gravitino`, `nessie`, `polaris`
- data control POCs: `openldap`, `ranger`
- data explorer POCs: `superset`, `metabase`, `sqlpad`, `redash`, `querybook`, `cloudbeaver`
- gateway and observability: `trino-gateway`, `grafana`, `keda`, `kiali`

**Simple summary**

> I have worked in a federated data platform where execution, metadata, authorization, catalog, and observability are treated as separate but connected planes.

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

**Full version you can say**

> At the source database level, every insert, update, and delete is written into a transaction log. A CDC connector reads that log and converts those low-level changes into structured events. Those events are then transported, often through Kafka, and consumed by downstream processors. The processor applies ordering, deduplication, and merge logic before writing into analytical storage such as Iceberg tables.

**If they ask what is hard about it**

> The hard part is not capturing the event itself. The hard part is handling ordering, retries, schema changes, and making sure the target state remains correct.

#### How Do You Handle Duplicates?

Say:

- idempotent writes
- primary key plus timestamp or version
- merge or upsert logic
- deduplication during processing

**Full answer**

> I treat duplicates as a normal distributed-systems problem, not an exception. I usually handle them through idempotent writes, stable business keys, ordering fields like timestamps or version numbers, and merge logic in the target layer. If the upstream system can redeliver events, the target table logic must still produce the same final state.

**Iceberg angle**

> In an Iceberg-based pipeline, I would typically use merge or overwrite logic carefully so that retries do not create inconsistent final data.

#### What Is Deduplication?

Deduplication means removing logically repeated records so that the same real-world event or entity is not counted more than once.

**CVE and asset example**

```text
cve_id        | asset_id | detected_at
CVE-2026-1001 | srv-101  | 2026-04-10 10:00
CVE-2026-1001 | srv-101  | 2026-04-10 10:00
```

After deduplication, only one of those rows should remain.

**How to explain it**

- first define the business key, for example `cve_id + asset_id`
- then define the winner rule, for example latest `updated_at`
- keep one correct row and discard or merge the rest

**Interview line**

> Deduplication means identifying logically repeated records using a business key and keeping only the correct version based on a rule such as latest timestamp, highest version, or first valid event.

#### How Do You Handle Late Data?

Say:

- watermarking
- reprocessing
- partition updates
- backfills where required

**Full answer**

> Late data is expected in real systems. I usually separate event time from processing time, use watermarks if streaming is involved, and make sure the storage layer supports safe reprocessing or partition updates. For analytics, the key is deciding whether dashboards should prioritize freshness or corrected history.

#### How Do You Optimize Queries?

Say:

- partitioning
- indexing for MySQL
- predicate pushdown
- caching
- reducing scanned data

**Full answer**

> I optimize queries differently depending on the system. In MySQL I think about indexes, join order, and execution plans. In Trino and Iceberg I think more about partitioning, file sizes, metadata pruning, predicate pushdown, and avoiding unnecessary scans. In analytics systems, reducing scanned data is usually more important than micro-optimizing syntax.

**Trino / Iceberg answer you can say directly**

> In Trino over Iceberg, I focus on partition design, filtering on partition-aligned columns, keeping files reasonably sized, compacting small files, and making sure table metadata can prune irrelevant files early.

#### What Is A Curated Iceberg Table?

A curated Iceberg table is a business-ready analytical table, not just raw landed data.

**Simple layer model**

- raw: source-shaped data landed as-is
- staging: lightly standardized and cleaned
- curated: trusted, modeled, deduplicated, query-ready data

**CVE and asset example**

`raw_cve_asset_events` may contain:

- duplicate CVE records
- source-specific field names
- missing owner mappings
- mixed timestamp formats

`curated_cve_asset_impact` should contain:

- one clean row per business key
- normalized severity
- standardized timestamps
- mapped asset and owner context
- a shape ready for Trino, BI, and reporting

**Interview line**

> A curated Iceberg table is the trusted analytical layer. Raw data is landed first, then cleaned, standardized, deduplicated, and modeled into Iceberg tables that engines like Trino can query reliably.

#### What Is Run-Length Encoding In Parquet?

This is a good implementation-detail topic because it shows you understand columnar storage beyond just naming Parquet.

**Simple explanation:**

> Run-length encoding stores repeated values as a value plus a count instead of writing the same value many times.

**Example:**

Instead of storing:

```text
EU, EU, EU, EU, US, US, APAC
```

run-length encoding stores something conceptually like:

```text
(EU, 4), (US, 2), (APAC, 1)
```

**Why it helps in Parquet:**

- Parquet is columnar, so repeated values in a column are common
- low-cardinality columns like country, status, or boolean flags compress very well
- less data on disk usually means less data to read

**Important nuance:**

> RLE works best when the same values appear consecutively or in long runs, so it is especially effective for sorted or naturally clustered column values.

**Strong interview line:**

> In Parquet, encodings like run-length encoding help reduce storage and improve scan efficiency, especially for repeated values in low-cardinality columns.

**Why this matters for your background**

> Even if I’m not manually implementing Parquet internals, understanding encodings helps explain why columnar formats like Parquet work so well with engines like Trino for analytical scans.

#### Run-Length Encoding Java Example

If they ask you to explain it algorithmically, you can show it using a simple string-compression example.

**Input**

```text
aaabbccccd
```

**Output**

```text
a3b2c4d1
```

**How it works**

1. start at the first character
2. count how many times it repeats consecutively
3. append character plus count
4. continue with the next new character

**Java code**

```java
class Solution {
    public String runLengthEncode(String s) {
        if (s == null || s.isEmpty()) {
            return "";
        }

        StringBuilder result = new StringBuilder();
        int count = 1;

        for (int i = 1; i < s.length(); i++) {
            if (s.charAt(i) == s.charAt(i - 1)) {
                count++;
            } else {
                result.append(s.charAt(i - 1)).append(count);
                count = 1;
            }
        }

        result.append(s.charAt(s.length() - 1)).append(count);
        return result.toString();
    }
}
```

**Example test cases**

```text
"aaabbccccd" -> "a3b2c4d1"
"abcd"       -> "a1b1c1d1"
"aaaaa"      -> "a5"
""           -> ""
```

**Interview line**

> Run-length encoding is a simple compression technique where repeated adjacent values are stored as value plus count instead of repeating the same value multiple times.

#### How Do You Scale?

Say:

- horizontal scaling
- partition-based parallelism
- distributed compute
- workload isolation

**Full answer**

> I scale by separating storage from compute, partitioning work well, and isolating workloads. For query systems, concurrency and noisy-neighbor control matter just as much as raw compute. For ingestion and processing, I focus on parallelism by partition, reliable checkpointing, and avoiding bottlenecks in metadata or small-file handling.

**Trino / Iceberg angle**

> In Trino-style systems, scale comes from distributed workers and efficient pruning. In Iceberg-style storage, scale also depends on healthy table metadata, file layout, partition design, and compaction strategy.

### Extra Deep-Dive Topics You Should Be Ready For

#### Why Trino Instead Of Spark For Query Serving?

> I would use Trino for interactive SQL, federation, and high-concurrency analytical reads. I would use Spark more for heavy transformations, batch compute, or jobs that need richer execution control. They solve related but different problems.

#### Why Iceberg Instead Of Plain Parquet Files?

> Plain Parquet files give columnar storage, but Iceberg adds table semantics: schema evolution, partition evolution, metadata tracking, time travel, and safer table operations. That is important when the data platform is not just storing files, but managing analytical tables at scale.

#### What Can Go Wrong With Iceberg?

- too many small files
- poor partition design
- metadata growth
- compaction not running
- merge-heavy workloads creating fragmentation

**Good line**

> Iceberg solves many table-management problems, but it still requires healthy operational practices around compaction, partitioning, and metadata maintenance.

#### What Can Go Wrong With Trino?

- high concurrency can create resource pressure
- federated queries can overload source systems
- poorly filtered queries can scan too much data
- mixed workloads can create noisy-neighbor issues

**Good line**

> Trino is excellent for governed SQL access, but it needs workload management, source protection, and table design discipline to perform well consistently.

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

### Expanded Version You Can Speak

The business problem was that vulnerability data was spread across multiple systems, so identifying affected assets and owners was too slow. I designed a unified data platform that combined API-based ingestion, CDC from operational systems, and a curated analytical layer. For storage, I would describe it as a modern analytical table layer, and if appropriate I would mention Iceberg because it supports reliable schema and table management. For query serving, I would explain that a Trino-style engine gave teams SQL access across datasets without forcing every workflow into one rigid pipeline. The outcome was faster response time, better visibility, and better alignment between platform design and business decision-making.

### Why This Story Works

- it shows business impact
- it includes ingestion, processing, storage, and query design
- it gives you room to discuss governance
- it naturally fits Trino and Iceberg conversations

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
- index vs partition vs primary key
- Parquet run-length encoding
- an end-to-end data pipeline
- query optimization
- handling failures

### Trino / Iceberg-Specific Checklist

Also be ready to explain:

- why Trino is a strong query layer
- when Spark is still the better compute engine
- why Iceberg is more than just Parquet files
- what a curated Iceberg table means
- partition design and file pruning
- small files and compaction
- schema evolution and partition evolution
- deduplication and idempotent merge logic
- why federation is powerful but risky without governance

---

## Last-Mile Prep

The highest-value next step is a mock in this exact format:

- concepts
- system design
- implementation deep dive

You can practice with this prompt:

> Start mock interview
