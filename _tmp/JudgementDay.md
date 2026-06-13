# 🎯 Salesforce Software Engineering Architect — 3-Day Prep Plan

**Owner:** VJ (Vivek Jain) · **Loop:** Andrew Hogg · Dave Angulo · Danielle Maves
**Companion to:** AIM Interview Answer Bank · **Window:** 3 days

> You already have the stories. This plan does three things: (1) assigns the *right* story to the *right* interviewer so you never tell the same 2-minute pitch twice, (2) closes the four gaps you've already flagged, and (3) hardens the one loop that can actually sink you — **Dave**.

---

## The strategic read (memorize this first)

Three interviewers, three competencies, three very different rooms:

| Interviewer | Competency | Risk | Your move |
|---|---|---|---|
| **Andrew Hogg** | Drive Innovation | Will catch hand-waving on distributed-systems / DB / cloud trade-offs | Lead with the *virtualization-over-replication* bet + how you de-risked it |
| **Dave Angulo** | Technical Depth + Champion Customer Success | **Highest risk AND highest opportunity** — he builds your exact world | Go deep, and connect your RAG/MCP/agent layer straight to his work |
| **Danielle Maves** | Achieve Results (30 min) | Will read rambling as weak ownership | Tight STAR, lead with the number, lean on your governance pedigree |

**The one thing to internalize:** everything in your bank runs on the Cyber Data Mesh, and that's fine across a 3-person loop — *as long as you vary the angle*. Innovation cut for Andrew. Technical-depth cut for Dave. Quantified-ownership cut for Danielle. Same project, three different stories.

---

## ⚠️ Must-fix before any session (do this in the first hour)

**Your CV contradicts itself on the cost number.** Page 1 "Key Achievements" says **40%** ($1.2M→$700K). Page 2 (J.P. Morgan bullet) says **30%**. Andrew and Dave are detail-people — if one reads the CV closely and you quote 40% out loud, that's an avoidable credibility ding.

- **Lock it: 40% · $1.2M → $700K · 99.98% reliability** everywhere — CV, both spoken and written.
- The 99.9% vs 99.98% wobble in your open-items list: pick **99.98%** and use it everywhere.
- *Tell me if you want me to regenerate the CV with these reconciled — it's a 5-minute fix and worth doing before you submit anywhere.*

---

## Interviewer-by-interviewer playbook

### 🔵 AH — Drive Innovation

He's a practitioner-turned-director (DataStax/Cassandra → Seagate cloud → Salesforce Data Architecture). He's lived hype cycles. He rewards *engineering discipline alongside* innovation.

**Lead story: virtualization over replication.**
This is your crown jewel for innovation because it was a *non-obvious, contested* call — not "we adopted Kafka." Frame it exactly as the brief wants:
> status quo (everyone wanted a lake) → the gap you saw (sync, freshness, duplicate storage, governance drift) → what you proposed (federated Trino + a Java SQL-over-API connector exposing 80+ REST APIs as tables) → **how you validated it cheaply** (prototype proving query pushdown made latency acceptable) → buy-in → outcome → what you'd do differently.

**The "validated cheaply" beat is the whole game with Andrew.** He explicitly wants to hear how you de-risk before committing capacity. Your prototype-before-debate move is the answer — make it prominent.

**Have loaded:**
- A *failed* experiment → the **adoption stall** story. Frame it as "an innovation that initially failed because I treated it as an architecture problem when it was a product problem." Genuine failure + course-correct = exactly his "walk me through a failed innovation" question.
- One-level-deeper answers he *will* push on: why Trino over a lake; what pushdown actually buys you; freshness/consistency trade-offs; failure modes of federation when a source API is down or slow; how you instrument it.

**Ask him:** "You're leading a cloudification of networking inside the Office of the CEO — where does the data-architecture strategy need to be 18 months out, and how does this Architect role plug into that?"

---

### 🟢 DA — Technical Depth + Customer Success (your hardest loop)

He built Data Cloud from scratch: CDC on Kafka, Identity Resolution, Transform Platform (billions/day), Data Graphs, ML/AI extensibility. 7 patents. Built **Twining MCP** for AI-agent coordination. He will go *deep* and will not be impressed by diagram-level descriptions.

**Two halves to this loop — prep both.**

**Half 1 — Technical depth (whiteboard the Mesh out loud):**
Be able to talk through Cyber Data Mesh end-to-end without notes — data flow, components, failure modes, scaling characteristics, numbers. Rehearse this *verbally*, standing up, twice. Then prep to go one level deeper on each of:
- **Streaming vs batch** — your NiFi/Flink/Kafka real-time path vs dbt/Kestra batch; when you chose which and why.
- **Consistency vs availability** — the federation choice *is* a CAP-flavored answer; you traded centralized consistency for live availability + governance-at-source. Say it in those terms.
- **Multi-tenancy** — your policy-as-code guardrail floor (deny-by-default, no cross-tenant exposure) is a strong multi-tenant answer. He'll like it.
- **Schema evolution** — how Iceberg + your metadata/contracts layer handle it.
- **Trino federation internals** — connector SPI, predicate/aggregate pushdown, what doesn't push down and how you handle it.
- **OPA policy evaluation** — where decisions are made, latency of asking a central engine per query, caching.

**The MCP connection is the single biggest opportunity in the whole loop.** Dave wrote an open-source MCP server for agent coordination. *You built a RAG + MCP + agentic governed-SQL layer* — agents discover datasets via metadata, ground via retrieval, and act through policy-bound access with a human in the loop. That is a direct hit on his world. Bring it up deliberately and let it run. This is where you stop being a candidate and start being a peer.

**On Identity Resolution — be honest.** He holds patents in match-and-merge; do not pretend. Bridge instead: *"I haven't built identity resolution directly, but the adjacent problem I owned was metadata-driven ownership, lineage, and entity provenance — here's how I'd reason about match-and-merge if I were designing it..."* Honesty + adjacent depth beats a bluff he'll detect in one follow-up. (Same principle you already apply to Go/Rust = exposure, not proficiency.)

**Half 2 — Customer Success:**
Lead with the **stakeholder-workflow-listening** story (analysts asked for "faster vulnerability response," you sat with their workflow, found the bottleneck was data-gathering not analysis, redesigned around *their* workflow → weeks to hours, and they *adopted* rather than tolerated it). That's a "customer/user changed my technical direction" story — strengthen the moment where their reality changed your design.

Also have ready: **a technically "wrong" but right-for-the-customer call** — the hybrid (materialize hot datasets even though pure federation was architecturally "purer"), or self-service onboarding (less elegant, more adoptable). He asks this directly.

**Ask him:** "Across Data Cloud at trillions of records, where's the hardest live problem right now — identity resolution at scale, transform throughput, or grounding agents safely on enterprise data? And how is Agentforce changing the data-platform requirements underneath it?"

---

### 🟡 DM — Achieve Results (30 min, STAR-tight)

Lawyer-turned-strategist, led AI governance to **ISO 42001** certification at Lumen, now VP AI Strategy. She assesses ownership, measurable outcomes, accountability in ambiguity. Short session — no room to ramble.

**Lead story:** a quantified ownership-in-ambiguity story — either the **AI-enablement-from-ambiguity** story (framed assumptions, built the riskiest-assumption prototype first, scaled from real feedback) or the **leadership-across-13-teams-that-didn't-report-to-you** story. Both end in hard numbers.

**Land the numbers, every time:** 1,000+ users · 200K queries/day · 99.98% · 40% cost cut · weeks→hours · 13+ depts · 2024 Business Result Award.

**Play your governance card — it's tailor-made for her.** Policy-as-code, OPA, ABAC/PBAC, lineage, human-in-the-loop, AI-ready governance. Your "governance shouldn't live inside the thing it governs" failure-and-fix story will land *especially* well with someone who built an AI-governance program to an ISO standard.

**Have ready:** "an outcome that didn't match your standard" → the **governance-retrofit failure** (you embedded access logic in pipelines, it became a maintenance trap, you externalized to OPA). Clean STAR, owns the mistake, shows the system-level fix.

**Ask her:** "As AI governance moves from policy to *product*, how is Salesforce embedding governance into the agentic platform itself rather than bolting it on — and what does 'done' look like for responsible agentic AI?"

---

## The four gaps to close (your own open items)

1. **Reconcile the CV numbers** — *must-fix, see top.* Lock 40% / 99.98%.
2. **A non–Data Mesh conflict story** — so your team-conflict answer (Q3) and your leadership-disagreement answer don't *both* ride the centralize-vs-federate hybrid. Mine your Cognizant era: Lloyds service-virtualization rollout, RBS unified-auth/logging across B2B/B2C, or the Threat-Intel Hub integration. Pick one where two teams or vendors genuinely clashed and you brokered it.
3. **A "disagreed and was *wrong*" story** — right now *every* disagreement story ends in your vindication. Dave and Danielle may probe humility. Prepare one where you pushed a position, lost, and the team was right to overrule you — and what you took from it. This is the highest-value new story to build.
4. **DSA / fundamentals refresh** — Trie problems + crisp OOP/DBMS articulation, in case Dave runs a light design or coding exercise. 30–45 min is enough to knock the rust off.

---

## 📅 Day-by-day

### Day 1 — Reconcile + build the missing stories
- [ ] Fix CV numbers (40% / 99.98%) — flag to me if you want it regenerated.
- [ ] Build the **"disagreed and was wrong"** story (STAR, ~90 sec).
- [ ] Build the **non–Data Mesh conflict** story from a Cognizant client.
- [ ] Strengthen the **"customer changed my direction"** story (workflow-listening) for Dave.
- [ ] Re-read AIM bank Parts 1 & 2 to reload the polished phrasing.

### Day 2 — The Dave loop (technical depth)
- [ ] Whiteboard Cyber Data Mesh end-to-end **out loud**, twice, standing up.
- [ ] Drill the one-level-deeper answers: streaming/batch, CAP trade-off, multi-tenancy, schema evolution, Trino pushdown, OPA eval latency.
- [ ] Rehearse the **RAG + MCP + agent** connection to Dave's Twining MCP work until it's natural.
- [ ] Prepare the honest **identity-resolution bridge**.
- [ ] DSA refresh: Trie + OOP/DBMS articulation.

### Day 3 — Polish, behavioral, values, logistics
- [ ] Rehearse Andrew's innovation story with the *de-risking* beat front and center.
- [ ] Rehearse Danielle's STAR answers — tight, number-led, governance-flavored.
- [ ] Map your stories to Salesforce values (below).
- [ ] Run the three pause-marked scripts from the bank once each, out loud.
- [ ] Lock your three "questions to ask."
- [ ] Confirm logistics (timezone of each call, tooling, a whiteboard surface). Sleep.

---

## Salesforce values → your stories (closing your last open item)

| Value | Your story |
|---|---|
| **Trust** | Policy-as-code guardrail floor; governance externalized to OPA; secure-by-default multi-tenancy |
| **Customer Success** | Workflow-listening → redesigned around analysts' real bottleneck → they *adopted* it |
| **Innovation** | Virtualization-over-replication; SQL-over-API connector as a reusable firm-wide primitive |
| **Equality** | Force for Good — led 6 engineers building a learning platform for The Prince's Trust; cross-team mentoring |
| **Sustainability** | 40% infra cost cut via workload-aware rightsizing/auto-scaling (efficiency + footprint) |

---

## One-line reminders for the day
- Don't tell the 2-min pitch three times — **one angle per interviewer.**
- With Dave: **depth, then the MCP connection.** Don't bluff identity resolution.
- With Danielle: **number first, then the story.**
- With Andrew: **"here's how I validated it cheaply before committing."**
- Disagreeing well only earns trust **if you commit fully** once the call is made.