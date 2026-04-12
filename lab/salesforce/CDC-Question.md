Perfect — now let’s explain CDC using your actual stack:
	•	Kestra for orchestration
	•	dbt for transforms
	•	Trino for query/merge
	•	Iceberg tables
	•	Parquet files in object storage

That is a very strong interview story because it is practical, modern, and believable.

⸻

CDC in Your Stack

Simple definition

In your setup, CDC means capturing changes from source systems and applying those changes incrementally into Iceberg tables stored as Parquet, using Kestra to orchestrate and Trino/dbt to process and merge.

So instead of reloading the full vulnerability table every time, you only process:
	•	new CVEs
	•	changed CVE scores
	•	updated severity
	•	status changes
	•	deleted or retired CVEs

⸻

Real Example Using Vulnerability Data

Let’s say your source sends this vulnerability data.

Current source state

cve_id	cve_name	severity	score	updated_at
CVE-2026-1001	OpenSSL Buffer Issue	HIGH	7.5	2026-04-10 10:00
CVE-2026-1002	Kernel Priv Esc	CRITICAL	9.8	2026-04-10 10:00

Later, source changes:
	•	CVE-2026-1001 score changes from 7.5 to 8.8
	•	new CVE CVE-2026-1003 arrives
	•	CVE-2026-1002 is retired or marked inactive

That is CDC input.

⸻

How It Works in Your Architecture

1. Raw incremental extract lands first

Kestra runs a workflow that pulls only changed records from source.

This can be based on:
	•	updated_at > last_successful_run
	•	source API incremental token
	•	source DB change feed if available

You land these changes into a raw/staging Iceberg table.

Example raw CDC table:

op	cve_id	cve_name	severity	score	updated_at
U	CVE-2026-1001	OpenSSL Buffer Issue	HIGH	8.8	2026-04-11 09:00
I	CVE-2026-1003	Apache RCE	CRITICAL	9.9	2026-04-11 09:02
D	CVE-2026-1002	Kernel Priv Esc	CRITICAL	9.8	2026-04-11 09:04

Where:
	•	I = insert
	•	U = update
	•	D = delete

⸻

2. Trino applies changes into target Iceberg table

Your curated vulnerability table may look like:

security.vulnerabilities_current

You then use Trino MERGE against the raw CDC table.

Example:

MERGE INTO security.vulnerabilities_current AS tgt
USING (
    SELECT op, cve_id, cve_name, severity, score, updated_at
    FROM security.vulnerabilities_cdc_stage
) AS src
ON tgt.cve_id = src.cve_id

WHEN MATCHED AND src.op = 'D' THEN
    DELETE

WHEN MATCHED AND src.op IN ('U', 'I') AND src.updated_at >= tgt.updated_at THEN
    UPDATE SET
        cve_name = src.cve_name,
        severity = src.severity,
        score = src.score,
        updated_at = src.updated_at

WHEN NOT MATCHED AND src.op IN ('I', 'U') THEN
    INSERT (cve_id, cve_name, severity, score, updated_at)
    VALUES (src.cve_id, src.cve_name, src.severity, src.score, src.updated_at);

This is the heart of CDC in your stack.

⸻

Where dbt Fits

dbt usually fits in two places.

Option 1: dbt prepares clean incremental staging models

You can use dbt to:
	•	clean source fields
	•	deduplicate records
	•	standardize severity values
	•	filter latest update per CVE

For example, a dbt model can produce the latest valid CDC event per cve_id.

Example logic:

with ranked as (
    select
        op,
        cve_id,
        cve_name,
        severity,
        score,
        updated_at,
        row_number() over (
            partition by cve_id
            order by updated_at desc
        ) as rn
    from {{ ref('vulnerabilities_cdc_raw') }}
)
select *
from ranked
where rn = 1

That becomes the clean source for Trino MERGE.

Option 2: dbt builds downstream gold models

After the current vulnerability table is updated, dbt can build:
	•	critical_vulnerabilities
	•	cve_score_trends
	•	open_cves_by_team
	•	top_assets_by_risk

So a good interview line is:

“Kestra orchestrates the ingest and merge flow, Trino performs CDC merge into Iceberg, and dbt builds standardized curated and business-facing models on top.”

⸻

Where Iceberg Helps

Iceberg is important because it makes CDC practical at scale.

Why Iceberg is useful here
	•	ACID-style table operations
	•	snapshot-based commits
	•	schema evolution
	•	partition pruning
	•	time travel
	•	reliable metadata management over Parquet files

Interview-safe explanation:

“Even though the underlying data is stored in Parquet files, Iceberg gives us table semantics and atomic commits, so CDC updates are managed safely through metadata and snapshots.”

⸻

Important Low-Level Detail

MERGE is not in-place row update

This is a very important interview point.

With Iceberg + Parquet:
	•	Parquet files are immutable
	•	rows are not updated in place
	•	affected data files are rewritten
	•	Iceberg updates table metadata to point to new files

So say this:

“In Iceberg, CDC updates are implemented by rewriting affected data files and committing a new snapshot, not by updating rows inside Parquet files directly.”

That sounds strong in interview.

⸻

Role of Kestra

Kestra is your control plane for the pipeline.

A typical CDC workflow in Kestra could be:

Kestra flow
	1.	Read last successful watermark
	2.	Pull changed vulnerability records from source
	3.	Load raw CDC data into staging Iceberg table
	4.	Trigger dbt staging model for dedup/cleanup
	5.	Run Trino MERGE
	6.	Run dbt gold models
	7.	Validate row counts and quality checks
	8.	Update watermark
	9.	Send alert if failure occurs

That is a very clean architecture answer.

⸻

Example End-to-End Flow You Can Say in Interview

“For vulnerability data, we orchestrate incremental extraction through Kestra using a watermark on source updates. The changed records land in a raw Iceberg staging table in Parquet format. We then use dbt to standardize and deduplicate the incoming changes, and Trino executes a MERGE into the curated Iceberg table keyed by CVE ID. This allows us to process inserts, score updates, and deletes incrementally instead of doing full reloads.”

That is a very strong answer.

⸻

Failure Scenarios in Your Stack

Now the important part: what can go wrong?

1. Duplicate CDC records

Example:
same CVE-2026-1001 update arrives twice.

Fix:
	•	deduplicate in dbt staging
	•	keep latest by updated_at
	•	merge on cve_id

2. Out-of-order events

Example:
score 8.8 arrives, then older event 7.5 arrives later.

Fix:
	•	compare timestamps in merge
	•	only update when src.updated_at >= tgt.updated_at

3. Partial workflow failure

Example:
raw load succeeds, merge fails.

Fix:
	•	keep staging table intact
	•	rerun merge safely
	•	watermark updates only after successful completion

That last point is critical.

“I only advance the watermark after the merge and validations succeed, otherwise the pipeline can miss changes.”

4. Delete handling

Some sources may not send delete events.

Fix:
	•	use soft delete logic
	•	periodic reconciliation
	•	or a source snapshot comparison job

⸻

Data Quality Checks You Should Mention

For vulnerability CDC, useful checks are:
	•	duplicate cve_id count
	•	null cve_id
	•	invalid score range, for example score not between 0 and 10
	•	invalid severity mapping
	•	updated row count anomaly
	•	compare source extract count vs staged count

Example interview phrasing:

“I treat pipeline success and data correctness separately. After merge, I run quality checks such as duplicate CVE detection, null key checks, score range validation, and source-to-target reconciliation.”

⸻

SCD vs CDC in Your Setup

Very common interview confusion.

CDC

How changes arrive.

SCD

How history is modeled.

So in your case:
	•	raw change events come through CDC
	•	current table may store latest state only
	•	another historical table can be built as SCD Type 2

Example historical model:

cve_id	severity	score	valid_from	valid_to	is_current
CVE-2026-1001	HIGH	7.5	2026-04-10	2026-04-11	false
CVE-2026-1001	HIGH	8.8	2026-04-11	null	true

That is powerful if they ask about history.

⸻

Strong Interview Answers You Can Reuse

Explain CDC in your stack

“In our stack, CDC is implemented as incremental change processing into Iceberg tables. Kestra orchestrates extraction of changed records from source systems, dbt standardizes and deduplicates them, and Trino applies those changes into curated Iceberg tables using MERGE. Since the underlying data is stored in Parquet, Iceberg gives us the transactional table layer needed to manage updates safely.”

Why use CDC instead of full load

“Because vulnerability data changes frequently and needs timely updates. Full reloads are expensive and slow, while CDC lets us process only inserts, updates, and deletes.”

What is the hardest part of CDC

“Correctness under retries, duplicates, late-arriving events, and out-of-order updates. The logic is less about moving data and more about preserving the right final state.”

⸻

Deep Technical Questions They May Ask You

Here are some likely ones based on your stack.

Q1. How do you prevent old CVE updates from overwriting newer ones?

Use updated_at or source version in merge condition.

Q2. What happens internally when Trino merges into Iceberg?

Affected Parquet files are rewritten and Iceberg commits a new snapshot.

Q3. Where would you deduplicate CDC records?

Preferably in dbt staging before merge, using window functions.

Q4. How do you recover if merge fails?

Do not advance watermark, rerun from staged CDC data.

Q5. How do you handle deletes if source does not send delete events?

Use reconciliation jobs or soft-delete inference.

⸻

One Clean Architecture Line

“Kestra orchestrates the incremental flow, dbt creates clean deterministic staging models, Trino performs the merge logic, and Iceberg provides atomic table semantics over Parquet storage.”

That is probably the best one-line summary for your setup.

⸻

60-second interview answer

“In my setup, CDC means capturing only changed vulnerability records, such as new CVEs, updated scores, or retired findings, instead of reloading everything. Kestra orchestrates incremental extraction using a watermark or source update timestamp. The changes land in a raw Iceberg staging table stored as Parquet. Then dbt cleans and deduplicates those changes, and Trino executes a MERGE into the curated Iceberg table keyed by CVE ID. Iceberg handles this safely through snapshot-based commits, so even though Parquet files are immutable, we still get reliable incremental updates at scale.”

Next I can give you a full Kestra + dbt + Trino CDC example, including:
	•	source table
	•	staging model
	•	merge SQL
	•	failure handling
	•	interview Q&A set
