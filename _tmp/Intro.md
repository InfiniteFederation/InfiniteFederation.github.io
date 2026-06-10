# Target: ~90 seconds, spoken naturally.

**Pause legend:** `/` = short breath · `//` = full beat (stop, let it land) · **bold** = stress the word

---

"I'm a **hands-on** Staff and Principal-level engineer and architect <br>
/ with 18 years   <br>
/ building large-scale distributed data and AI platforms / mostly across Fortune 100 banks. //  <br>

For the last decade I've been at **JPMorgan Chase**  <br>
/ as a VP in cyber engineering.  <br>
/ My flagship work has been architecting the firm's **first Cyber Data Mesh ecosystem**.  <br>
/ That become the foundation of our cyber security.  <br>
/ — a Kubernetes-native data platform  <br>
// platform has grown to support more than a **thousand users**  <br>
/ and processing over **200,000 queries a day**   <br>
/ achieving a **99.98% ** successful query completion rate.  <br>
/ — and it won the firm's **2024 Business Result Award**. //  <br>

The thing I'd want you to know about me  <br>
/ is that I'm **hands-on**  <br>
/ not just on the whiteboard.  <br>

// I personally built the hardest part  <br>
/ — a federated **SQL-over-API engine**   <br>
/ that turned 80-plus REST APIs into relational tables   <br>
/ — and it became a **reusable primitive**  <br>
/ used right across the firm.  <br>

// In fact, I authored a paper on it / and **filed a patent**. //

Most recently / I've been building a **security and governance fabric** / so AI agents can discover data / and **act on it within policy**. // That's actually a big part of why I reached out to Salesforce / given the direction with **Agentforce**. //

I'm a citizen here in Dublin / and I'm looking for a senior **platform or architect** role / where I can keep building at that scale." //

---

## Delivery cues
- Three longest beats to honour: after the **award**, after **"not just on the whiteboard,"** and after **Agentforce**. Let the interviewer absorb.
- Slow down on *"I'm hands-on, not just on the whiteboard."* That's your differentiator.
- End on the upward note (*"keep building at that scale"*) and **stop** — no trailing filler.

## If they probe the AI / fabric line
Plain-language backup: *"It's a governance and policy layer — OPA, ABAC — that lets AI agents query data safely and act within policy."*

---

# "Why Salesforce?"
Target: ~45 seconds. Specific and genuine, not flattering.

"A few reasons. // The honest one is that the work I've been doing most recently / — building governed, policy-bound layers so AI agents can act on data safely / — is **exactly** the problem Salesforce is now solving at scale with **Agentforce**. // I've been building that pattern inside **one** firm. / Salesforce is productizing it for **thousands** of customers. // That's a much bigger canvas for the same skill set. //

The second reason is the **platform** angle. / My whole career has been about building **reusable primitives** other teams build on top of / — and Salesforce is fundamentally a platform company. / That's the kind of leverage I want my work to have. //

And honestly / the **outcome focus** appeals to me. / At my best I've translated engineering into real business results / — like taking platform cost from 1.2 million to 700K while holding reliability. / That maps to how Salesforce thinks about customer value." //

**Notes:** Lead with the Agentforce → *"one firm vs. thousands of customers"* hook. The cost stat doubles as your business-outcome STAR story, reinforcing the theme.

---

---

# "Why are you leaving JPMorgan? / Why now?"
Target: ~40 seconds. Forward-looking and positive — never criticise JPMorgan.

"It's a good question / — I've had a great decade at JPMorgan / and I'm not running **from** anything. // I took the Cyber Data Mesh from a blank page / all the way to enterprise adoption / and the Business Result Award. // I've essentially **completed the arc** of that work. //

What I want next / is to apply that same platform-and-AI architecture experience / at **product scale** / — where the thing I build ships to **thousands of customers** / rather than supporting **one** firm internally. // The agentic AI direction at Salesforce / is the most exciting version of that I've seen. //

So it's less about leaving / and more about the **next right problem** for the skills I've spent a decade sharpening." //

**Notes:** *"Not running from anything"* + *"completed the arc"* inoculate against the flight-risk read. Echoes the "why Salesforce" theme (one firm → thousands of customers) — vary the wording slightly so it's not word-for-word.

---

## State your lane early
You're **backend / platform / architecture**, not full-stack — say so, so they route you to the right interview loop.


# 1. Hardest thing you've built (→ the SQL-over-API engine)
"The hardest thing I've built / is the federated SQL-over-API engine at the heart of the Data Mesh. // On the surface it's just 'expose REST APIs as SQL tables' / — but the hard part is everything underneath. // APIs don't behave like databases / — no schemas, pagination, rate limits, inconsistent auth, no query plan. // I built a connector on the Trino SPI / that mapped 80-plus APIs into relational tables / pushed predicates down so we weren't pulling millions of rows / and stayed performant under real load. // The breakthrough was treating it as a query-planning problem / not an integration problem. // It became a reusable primitive across the firm / — and I filed a patent on it." //

The "why it's hard" insight is what scores. Don't just list features — name the conceptual reframe.

# 2. Something brand new to you (→ the agentic AI layer)
"Most recently / the agentic AI work was genuinely new territory. // I had deep distributed-systems experience / but RAG, embeddings, and the Model Context Protocol / I had to learn from the ground up. // I gave myself a small, real problem first / — letting an agent safely query one governed dataset / — built a thin prototype / and learned by shipping, not just reading. // I read the MCP spec closely / leaned on the open-source community / and pressure-tested my understanding by explaining it back to my team. // Within a few months / I had a governed layer where agents could act within policy." //

Shows your learning method and humility — exactly what they probe with "new to you."

# 3. Requirements missing or ambiguous (→ the Data Mesh inception)
"The Data Mesh started with almost no requirements / — just a frustration that analysts waited weeks / and every team built its own pipelines. // No spec / 13-plus departments / each with a different idea of what they needed. // So I didn't wait for clarity / — I created it. // I ran short discovery sessions / found the common pattern under their different asks / — they all wanted to query across sources without moving data / — and turned that into a one-page 'SQL-on-anything' principle. // Then I validated it with a thin slice / one connector, one real use case / and let the working demo replace the missing requirements doc." //

"I didn't wait for clarity, I created it" is the line. Ambiguity questions test whether you freeze or drive.

# 4. How do you know the steps are the right steps? (→ your method, not a story)
"I assume I'll be partly wrong / so I optimise for finding out fast and cheap. <br>
// Three things. <br>
// First / I keep decisions reversible where I can / — a thin slice or PoC before a big commit / so a wrong step costs days, not quarters. <br>
// Second / I attach a measurable checkpoint to each step / — if this is right, this number moves / — so it's not opinion. <br>
// Third / I expose the plan to strong engineers early, in design reviews / because the steps I'm least sure about are the ones I can't see myself. <br>
// The connector's a good example / — I built a PoC and measured query latency / before asking anyone to commit to the architecture." <br>
// This is the most senior answer of the five. Reversibility + metrics + peer review = staff/principal judgment.<br>

# 5. Biggest challenge so far (→ adoption / convergence)
"My biggest challenge wasn't technical / — it was getting 13-plus departments to converge on one platform / when each had already built its own. <br>
// The technology was the easy part. <br>
// People had real reasons not to trust a shared platform  <br>
/ — control, latency, fear of disruption. <br>
// So I treated adoption as the product.  <br>
// I made migrating easier than staying / — reference connectors, hands-on support  <br>
/ — won over one credible team first / and let their result pull the others in.  <br>
// Modernising the legacy Hadoop estate to Kubernetes underneath / cut analysis from weeks to hours  <br>
/ — a result the holdouts couldn't argue with. // Reaching a thousand users was a trust problem / not just an engineering one." // <br>

Framing your biggest challenge as people/influence — not code — signals real seniority.

# 


