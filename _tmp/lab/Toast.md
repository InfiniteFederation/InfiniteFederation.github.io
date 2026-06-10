# Toast Interview — 1-Hour Revision Guide
**Vivek Jain · Senior Principal Engineer, AI Team Agent · Hiring Manager: Darien Ford**

---

## ⏱ How to use this guide
| Minutes | What to do |
|---|---|
| 0–15 | Read Section 1 — Who you are & your 5 STAR stories |
| 15–30 | Read Section 2 — Toast domain & your agent vision |
| 30–45 | Read Section 3 — All 12 questions + your answers |
| 45–55 | Read Section 4 — Gaps, bridges & questions for Darien |
| 55–60 | Read Section 5 — BrightHire tips & opening 90 seconds |

---

## Section 1 — Who You Are (your 3-sentence pitch)

> *"I'm a Principal Software Engineer with 18 years building distributed systems and data platforms. For the last 5 years at JPMC I've been building agentic AI infrastructure — a metadata-driven RAG platform that lets AI agents discover datasets, generate governed SQL, and execute auditable actions across enterprise data. I want to bring that architecture to Toast's workforce domain — where the stakes are real for restaurant operators and their staff."*

---

### Your 5 STAR Stories (know these cold)

---

#### Story 1 — The Federated Platform (Architecture Vision)
**Use for:** Q2 complex system, Q9 exec influence, Q5 product roadmap

| | |
|---|---|
| **Situation** | JPMC cybersecurity analysts needed to correlate data across 15+ systems. Analysis took weeks because every source had a different query interface. |
| **Task** | Design a unified query layer — no data duplication — serving 1,000+ users with enterprise SLAs. |
| **Action** | Architected Trino-based federated platform. Built Java API connector for REST/GraphQL as relational tables. Designed data mesh governance with OPA. Built Scala compaction engine. Migrated Hadoop → cloud-native Kubernetes. |
| **Result** | **200K queries/day. 99.9% reliability. Weeks → hours for vulnerability analysis. Multiple JPMC business units.** |
| **Key line** | *"The key decision — virtualisation over replication — eliminated an entire class of data consistency problems."* |

---

#### Story 2 — Agentic AI + OPA (Governance Story)
**Use for:** Q1 agent vision, Q3 coding agents, Q6 year one priorities

| | |
|---|---|
| **Situation** | AI agents at JPMC needed access to enterprise data but with no guardrails — a security and compliance risk. |
| **Task** | Build an agentic platform where agents could act autonomously but within governed, auditable boundaries. |
| **Action** | Built RAG architecture using structured metadata (schema, lineage, ownership). Integrated OPA + MOAT policy engine as a gate before every agent action. Enabled agents to discover datasets, generate SQL, execute governed queries. |
| **Result** | **Foundation for agent-based workflows used across multiple JPMC business units. Every agent action auditable.** |
| **Key line** | *"The pattern is: metadata-aware agents with governed execution. The domain changes from cybersecurity to workforce. The architecture doesn't."* |

---

#### Story 3 — Product Roadmap Influence
**Use for:** Q5 product influence, Q9 exec communication

| | |
|---|---|
| **Situation** | JPMC cybersecurity product roadmap was focused on building more dashboards. The real bottleneck was data access speed. |
| **Task** | Shift the roadmap priority without formal authority. |
| **Action** | Built a 2-week proof of concept. Showed leadership: "Today it takes a week to answer this question. Here it takes 4 seconds." Partnered with product director to reframe the roadmap around data access velocity. |
| **Result** | **Federated platform became top priority. Vulnerability analysis: weeks → hours.** |
| **Key line** | *"The fastest way to influence a product roadmap is to make the problem undeniable with working code, not slide decks."* |

---

#### Story 4 — Mentoring a Senior Engineer
**Use for:** Q7 mentoring

| | |
|---|---|
| **Situation** | Technically excellent senior engineer who consistently deferred architectural decisions upward. |
| **Task** | Help him develop architectural confidence and independent decision-making. |
| **Action** | Stopped answering his questions directly. Started asking: "What options have you considered? What would you decide?" Backed his decisions publicly in design reviews — even when I'd choose differently — then debriefed privately on tradeoffs. |
| **Result** | **6 months later: leading design reviews independently, onboarding new engineers himself.** |
| **Key line** | *"The best mentoring I've done isn't teaching — it's creating conditions where someone realises they were capable all along."* |

---

#### Story 5 — MCP Agent Architecture (Your New Story)
**Use for:** Q3 coding agents — YOUR STRONGEST NEW ANSWER

| | |
|---|---|
| **Situation** | Needed AI agents to interact with enterprise data without custom integration work for every new data source. |
| **Task** | Build a reusable, governed agent architecture that could plug into any data source through a standard interface. |
| **Action** | Connected agent to two MCP servers: OpenMetadata MCP (agent discovers datasets, schemas, lineage, ownership) + Trino MCP (agent executes governed SQL queries across federated sources). Policy layer sits between agent and MCP servers. |
| **Result** | **Agent can discover, understand, query, and act on enterprise data autonomously with full audit trail. Built equivalent Toast prototype using Anthropic SDK.** |
| **Key line** | *"MCP is the right abstraction for Toast too — one governed layer that every agent plugs into."* |

---

## Section 2 — Toast Domain & Your Agent Vision

### What Toast's Team product does (say this naturally)

> *"Toast's Team area is essentially a real-time financial compliance system. Every clock-in, shift edit, and tip allocation has downstream tax and wage-law consequences. Labor is 30–35% of a restaurant's revenue — it's the most controllable cost and the most complex to manage."*

| Product area | What it does | Agent opportunity |
|---|---|---|
| **Shift scheduling** | Drag-and-drop schedules, demand forecasting, overtime warnings | Proactive overtime risk detection |
| **Tip management** | Auto-pool tips from POS across roles, multi-role allocation | Tip pool audit & dispute resolution |
| **Payroll** | POS hours sync to payroll, run in <10 mins, auto tax filing | Payroll anomaly detection |
| **Labor compliance** | Multi-state overtime rules, tip credit verification, onboarding | Compliance monitoring across locations |

---

### Your Year One Agent Priorities (Q6 — know this cold)

**Priority 1 — Overtime risk detection**
> *"Highest ROI. Labor is 30–35% of revenue. An agent that flags overtime risk before it happens and suggests coverage options has immediate, measurable value."*

**Priority 2 — Tip pool audit trail**
> *"Tip discrepancies destroy staff trust. An agent that explains every cent of a tip calculation in plain English removes the #1 source of manager-staff friction."*

**Priority 3 — Multi-state compliance monitoring**
> *"Restaurant groups operate across states with different tip credit laws and overtime rules. An agent that surfaces compliance gaps before payroll runs protects operators from costly violations."*

> **Landing line:** *"All three are trust-building agents, not efficiency agents. When an operator trusts the agent's outputs, everything else follows."*

---

### Your Agent Architecture for Toast

```
Toast POS (live events: clock-ins, tips, orders)
        │
        ▼
Toast Metadata MCP          Toast Query MCP
(employee roles,    ◄──────► (hours worked,
 tip policies,               tip pool calc,
 wage rules,                 overtime scan,
 location config)            audit log)
        │                         │
        └──────────┬──────────────┘
                   ▼
         Governance / Policy Layer
         (labor law rules, FLSA,
          audit trail, human approval
          gate for write actions)
                   │
                   ▼
         Toast Workforce Agent
         (Claude claude-sonnet-4-20250514)
```

> **Say this:** *"I actually built this — a working agent with two MCP servers. Toast Metadata MCP mirrors OpenMetadata from my JPMC work. Toast Query MCP mirrors the Trino layer. The governance layer sits between the agent and the MCP servers, so every action is policy-checked before it runs."*

---

### Key Toast facts (drop these naturally)

- **156,000** restaurant locations
- **$2B+ ARR**, first GAAP profitability in 2024
- ~**11% penetrated** in total addressable market
- **Toast Pay Cards** — instant tip access after shift close
- **"No Tax on Tips"** — H.R.1 (2025–2028) directly affects Toast's payroll product
- POS = Point of Sale = the tablet/terminal where orders are taken, payments processed, tips given, employees clock in/out

---

## Section 3 — The 12 Questions

---

### Q1 — Architectural vision for AI agents in workforce management
*(Most important question — Darien's #1 probe)*

**Your answer structure:**
1. Frame the domain: *"The Team area is a real-time financial compliance system..."*
2. Describe the architecture: metadata layer → reasoning layer → governed action layer
3. Name the first 3 agents (overtime, tip audit, compliance)
4. Bridge to JPMC: *"Same pattern I built at JPMC — the domain changes, the architecture doesn't"*

---

### Q2 — Most complex distributed system you've designed
**→ Use Story 1 (Federated Platform)**
Land on: *"200K queries/day, 99.9% reliability, weeks to hours"*

---

### Q3 — Hands-on experience with coding agents
*(Highest-risk question — your MCP story saves you)*

**Your answer:**
> *"My core experience is building the agent infrastructure layer at JPMC — the metadata discovery, governed tool execution, and OPA policy enforcement that enables agents to operate safely. Concretely: I connected an agent to OpenMetadata MCP and Trino MCP — so the agent discovers datasets, understands their structure, generates queries, and executes them with a policy gate on every action. I've also built the Toast equivalent using the Anthropic SDK — a workforce agent with two MCP servers and a governance layer. I see spec-driven development as the right discipline for production agents — vibe coding gets you a prototype, SDD gets you something you can operate."*

---

### Q4 — How to spread agentic patterns across Toast
**Three phases:**
1. **Establish** — Build Team agent as the reference implementation with constitution.md
2. **Enable** — Reusable scaffolding library + shared policy layer + working group (1 engineer per FinTech team)
3. **Measure** — Adoption velocity tracked publicly, executive visibility

**Bridge:** *"I did this at JPMC — federated platform went from one team to 1,000+ users across multiple business units. Same playbook."*

---

### Q5 — How you influenced a product roadmap
**→ Use Story 3 (Product Roadmap)**
Land on: *"Working code is the most powerful product influence tool."*

---

### Q6 — Year one priorities for Toast Team agents
**→ See Section 2 priorities above**
Land on: *"Trust-building agents, not efficiency agents."*

---

### Q7 — How you mentor senior engineers
**→ Use Story 4 (Mentoring)**
Land on: *"Best mentoring isn't teaching — it's creating conditions where someone realises they were capable all along."*

---

### Q8 — A time a system failed at scale
**Your structure:**
- What system, what scale, what broke
- How you found out (metrics? alert? angry call?)
- Immediate containment steps
- Root cause analysis
- Systematic fix (not just a patch)
- **End with:** *"The thing that changed in how I design systems after this is [X]. I've applied it ever since."*

> ⚠️ Prepare your own real story here. Don't make one up — Darien was CTO, he'll probe.

---

### Q9 — Executive communication and influence
**Your answer:**
> *"Executives decide on risk, cost, competitive position and time — not technical merit. I translate technical options into those terms first. When I proposed the agentic platform at JPMC, I didn't lead with RAG architecture. I led with: 'Today it takes a security analyst a week to answer a threat question that should take 4 seconds. Here's what that costs us.' The architecture came after the business case landed."*

---

### Q10 — Why Toast, why this role
**Three parts:**
1. **Toast** — $2B ARR, 156K locations, 11% TAM penetration. Inflection point where agents compound the value of every product.
2. **Role** — Workforce management in hospitality is genuinely hard. Real-time financial compliance for high-stakes operator decisions.
3. **Fit** — *"My entire 5 years at JPMC has been preparing me for this: governed, trustworthy agent infrastructure for high-stakes decisions. I want to apply that at the product layer, not just infrastructure."*

---

### Q11 — SaaS gap: enterprise platforms vs customer-facing product
**Your bridge:**
> *"You're right I haven't owned a customer-facing SaaS product. What I've run is a platform 1,000+ engineers depended on daily with contractual SLAs, incident escalation, a deprecation process, and versioned APIs — the operational complexity maps directly. What I'd invest in immediately is time with restaurant operators. Not to learn the basics from a PM — to sit with a manager during a shift change and watch what they actually do. The best architectural decisions I've made came from watching real users."*

---

### Q12 — Your questions for Darien

1. *"What does success look like for the first Team agent in production — from an operator's perspective, not just a technical one?"*
2. *"How does the feedback loop work between restaurant operators and this team? What signal tells you an agent is trusted vs just used?"*
3. *"You've transformed engineering orgs at Capital One and Madhive — what's the cultural shift you're driving at Toast with agentic development, and where's the resistance?"*
4. *"What does this role need to accomplish in the first 90 days to make you say the hire was the right decision?"*

---

## Section 4 — Gaps & How to Bridge Them

| Gap | What to say |
|---|---|
| **No SaaS experience** | "Internal platform with 1,000+ users, SLAs, incident SLAs, versioned APIs — operationally identical. Delta I'd close: more time with end customers." |
| **Coding agents experience** | "Built agent infrastructure at JPMC. Built OpenMetadata MCP + Trino MCP agent. Built Toast prototype with Anthropic SDK yesterday." |
| **AWS** | "Cloud-native Kubernetes, S3-compatible storage (MinIO), event-driven Kafka. AWS primitives are a fast ramp — same architectural patterns." |
| **Restaurant domain** | "I've researched Toast's Team product specifically: shift management, tip pooling, FLSA compliance, multi-state overtime rules, Toast Pay Cards." |

---

### About Darien Ford
- Former **CTO at Madhive**, Sr. Director Engineering at **Capital One**, Sr. Director at **Knighted**
- Loves **building products and teams** — not just systems
- Heavy **AWS** background (Capital One)
- Will probe: Can you be the most senior technical voice in his LoB? Do you have genuine passion for agents? Will you shape product, not just execute?
- **What impresses him:** Business outcomes first, technical detail second. Working code > slide decks. Honesty about gaps + a plan to close them.

---

## Section 5 — Final Prep

### Your opening 90 seconds (practise this out loud)

> *"I'm Vivek — Principal Software Engineer, 18 years building distributed systems and data platforms. The last 5 years at JPMC have been spent almost entirely on agentic AI infrastructure: I designed a metadata-driven platform where AI agents discover datasets, generate governed SQL queries, and execute auditable actions across enterprise data — serving 1,000+ users at 200,000 queries a day. I connected that agent to OpenMetadata and Trino through MCP servers — same two-layer pattern I'd want to apply to Toast's workforce domain. I've spent the last week building the Toast version as a prototype. I'm excited to talk about it."*

---

### BrightHire — 5 rules

1. **Use STAR explicitly** — Situation → Task → Action → Result. Every answer.
2. **Pause 2 seconds** before answering — cleaner transcript, better scoring
3. **Say numbers out loud** — 200K queries, 99.9%, 1,000 users, weeks → hours
4. **Mirror the JD language** — "agentic", "spec-driven development", "mission-critical", "FinTech product strategy", "governed"
5. **Don't fill silence** — Darien taking notes on BrightHire looks like silence. Wait for the next question.

---

### Spec-Driven Development (SDD) — one paragraph

> *"SDD means writing the specification before writing any code — defining what to build, the constraints, the acceptance criteria, and the non-negotiables. The spec is the primary artifact; code is the output. For Toast's Team agents I'd start with a constitution.md encoding the non-negotiables: FLSA compliance checks, tip calculation audit requirements, payroll accuracy guarantees. Agents implement features within those rails. Vibe coding gets you a prototype. SDD gets you something you can operate, audit, and maintain."*

---

### Key vocabulary to use naturally

| Word | Means in context |
|---|---|
| **POS** | Point of Sale — the terminal where orders, payments, tips, clock-ins happen |
| **FLSA** | Fair Labor Standards Act — US federal overtime and tip credit law |
| **Tip credit** | Employer paying less than min wage because tips make up the difference (not allowed in Ireland) |
| **Tip pool** | Shared pot of tips distributed across roles by hours worked or points |
| **Overtime threshold** | 40 hrs/week federal US; 8 hrs/day in some states; 40 hrs/week Ireland |
| **MCP** | Model Context Protocol — standard interface between agent and tools/data sources |
| **RAG** | Retrieval-Augmented Generation — agent retrieves relevant data before answering |
| **OPA** | Open Policy Agent — policy engine that checks rules before actions execute |
| **SDD** | Spec-Driven Development — write the spec first, agent implements from it |
| **Constitution.md** | Non-negotiable rules file that governs what an agent can and cannot do |

---

### One-liners to land key points

- On the domain: *"This isn't a chatbot problem — it's a payroll compliance problem that happens to use AI."*
- On your architecture: *"The pattern is the same — metadata-aware agents with governed execution. The domain changes, the architecture doesn't."*
- On MCP: *"MCP is the right abstraction — build the governed data layer once, every agent plugs in."*
- On SDD: *"Vibe coding gets you a prototype. Spec-driven development gets you something you can operate."*
- On product influence: *"Working code is the most powerful product influence tool — more than any slide deck."*
- On trust: *"The first agents I'd ship are trust-building agents, not efficiency agents. Trust unlocks everything else."*

---

## Quick Reference Card

```
JPMC → Toast mapping
─────────────────────────────────────────────────
OpenMetadata MCP   →   Toast Workforce Metadata MCP
Trino MCP          →   Toast Query / POS MCP
Cybersecurity data →   Workforce data (hours, tips, shifts)
OPA + MOAT         →   Labor law + payroll compliance gates
200K queries/day   →   156,000 restaurant locations
Weeks → hours      →   Manager decisions: minutes not days

Your 5 stories
─────────────────────────────────────────────────
1. Federated platform     → Architecture, scale, reliability
2. Agentic AI + OPA       → Governance, agent infrastructure
3. Product roadmap        → Influence, exec communication
4. Mentoring engineer     → People, leadership
5. MCP agent (new!)       → Coding agents, hands-on proof

Toast numbers to drop
─────────────────────────────────────────────────
156,000 locations · $2B+ ARR · 11% TAM penetration
30-35% of revenue = labor · <10 min payroll run
```

---

*Good luck Vivek. You've built the architecture. You understand the domain. You have the stories. Go show Darien what you'd build.*
