---
title: Infinite Federation
layout: default
permalink: /infinite-federation/
---

<section class="if-hero">
  <div class="if-container">
    <h1>How Infinite Federation Works</h1>
    <p>Stand up a federated query layer, a shared catalog, and product-first governance—then let domains ship value.</p>
    <div class="if-hero-ctas">
      <a href="/start/" class="if-button">Deploy the starter</a>
      <a href="/contact/" class="if-link">Architecture workshop →</a>
    </div>
  </div>
</section>

<section class="if-container" style="padding:56px 0 12px">
  <h2>Core building blocks</h2>
  <ol>
    <li><strong>Federated Engine:</strong> Trino or Starburst as the execution fabric across sources.</li>
    <li><strong>Open Table Format:</strong> Apache Iceberg for ACID tables and time-travel.</li>
    <li><strong>Catalog:</strong> Polaris / Nessie / HMS for table & schema registry.</li>
    <li><strong>Object Store:</strong> S3 / ADLS / MinIO for cost-efficient lake storage.</li>
    <li><strong>Metadata Mesh:</strong> OpenMetadata (ownership, lineage, docs, tags).</li>
    <li><strong>Policy Plane:</strong> OPA (ABAC/RBAC) + row/column masking; SSO groups.</li>
    <li><strong>Observability:</strong> Query events, costs, SLOs; quality via data contracts.</li>
    <li><strong>Developer Access:</strong> JDBC/ODBC, REST, and optional MCP for agent workflows.</li>
  </ol>
</section>

<section class="if-container" style="padding:8px 0 12px">
  <h2>Logical architecture</h2>
  <div style="overflow:auto;border:1px solid var(--border);border-radius:12px;padding:16px">
<pre><code>
<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 900 550">
  <style>
    text { font-family: Inter, system-ui, sans-serif; }
    .title { font-weight: 700; fill: #111827; font-size: 22px; }
    .sub { fill: #6b7280; font-size: 16px; }
    .box {
      fill: #fff; stroke: #e5e7eb; stroke-width: 1.5;
      rx: 14; ry: 14;
      filter: drop-shadow(0 2px 4px rgba(0,0,0,0.08));
    }
    .chip {
      fill: url(#grad);
      rx: 10; ry: 10;
      filter: drop-shadow(0 2px 4px rgba(0,0,0,0.15));
    }
    .arrow {
      stroke: #9ca3af;
      stroke-width: 3;
      fill: none;
      marker-end: url(#arrow);
    }
  </style>

  <defs>
    <linearGradient id="grad" x1="0" x2="1" y1="0" y2="0">
      <stop offset="0%" stop-color="#7c5cff"/>
      <stop offset="100%" stop-color="#1ef1ff"/>
    </linearGradient>
    <marker id="arrow" viewBox="0 0 12 12" refX="10" refY="6" 
      markerWidth="10" markerHeight="10" orient="auto">
      <path d="M0,0 L12,6 L0,12 Z" fill="#9ca3af"/>
    </marker>
  </defs>

  <!-- Clients -->
  <rect class="box" x="100" y="20" width="800" height="75"/>
  <text x="500" y="58" text-anchor="middle" class="title">Clients: BI · Notebooks · Apps · LLM Agents</text>

  <!-- JDBC chip -->
  <line x1="525" y1="95" x2="525" y2="155" class="arrow"/>
  <rect class="chip" x="400" y="160" width="240" height="40"/>
  <text x="510" y="185" text-anchor="middle" fill="#fff" font-weight="700">JDBC · ODBC · REST</text>

  <!-- Trino/Starburst -->
  <line x1="525" y1="200" x2="525" y2="270" class="arrow"/>
  <rect class="box" x="325" y="275" width="400" height="75"/>
  <text x="525" y="310" text-anchor="middle" class="title">TRINO / STARBURST</text>
  <text x="525" y="330" text-anchor="middle" class="sub">resource groups · catalogs per domain</text>

  <!-- Three branches -->
  <line x1="525" y1="350" x2="525" y2="445" class="arrow"/>

  <!-- External Sources -->
  <rect class="box" x="325" y="450" width="400" height="75"/>
  <text x="525" y="490" text-anchor="middle" class="title">EXTERNAL SOURCES</text>
  <text x="525" y="510" text-anchor="middle" class="sub">(Snowflake · BigQuery · Postgres …)</text>


</svg>
</code></pre>
  </div>
</section>

<section class="if-container" style="padding:8px 0 12px">
  <h2>Operating model (Data Mesh)</h2>
  <div style="display:grid;grid-template-columns:repeat(auto-fit,minmax(260px,1fr));gap:18px">
    <div style="border:1px solid var(--border);padding:18px;border-radius:14px;background:var(--surface)">
      <h3>Domain ownership</h3>
      <p class="muted">Each domain publishes discoverable, documented products (with SLAs, owners, contracts).</p>
    </div>
    <div style="border:1px solid var(--border);padding:18px;border-radius:14px;background:var(--surface)">
      <h3>Platform guardrails</h3>
      <p class="muted">Central team runs engine, catalog, policy, and observability; no blocking domain autonomy.</p>
    </div>
    <div style="border:1px solid var(--border);padding:18px;border-radius:14px;background:var(--surface)">
      <h3>Self-service</h3>
      <p class="muted">Bootstrap templates, connectors, CI/CD, and access requests via groups & tags.</p>
    </div>
  </div>
</section>

<section class="if-container" style="padding:8px 0 12px">
  <h2>Getting started (30–60–90)</h2>
  <h3>Days 0–30: Foundation</h3>
  <ul>
    <li>Deploy Trino/Starburst with 2 catalogs (lake + one external).</li>
    <li>Stand up Iceberg + catalog (Polaris/Nessie/HMS) on MinIO/S3/ADLS.</li>
    <li>Wire SSO (OIDC/SAML), bootstrap OPA policies (PII tags → column masking).</li>
    <li>Install OpenMetadata; ingest Trino, object store, and lineage.</li>
  </ul>
  <h3>Days 31–60: First data products</h3>
  <ul>
    <li>Create 3–5 gold tables per domain with data contracts and SLAs.</li>
    <li>Expose governed views; enable caching/CTAS patterns where useful.</li>
    <li>Set SLOs and dashboards for query cost, success rate, freshness.</li>
  </ul>
  <h3>Days 61–90: Scale & standardize</h3>
  <ul>
    <li>Add 2–3 more domains; templatize onboarding via IaC + blueprints.</li>
    <li>Adopt resource groups and queues; define chargeback/showback.</li>
    <li>Optional: enable MCP endpoints for agent workflows over the catalog.</li>
  </ul>
</section>

<section class="if-container" style="padding:8px 0 12px">
  <h2>Governance & security (pragmatic)</h2>
  <ul>
    <li><strong>Groups over users:</strong> Access via SSO groups mapped to OPA policies.</li>
    <li><strong>Tag-driven rules:</strong> PII/SENSITIVE tags → dynamic column masking & row filters.</li>
    <li><strong>Contracts:</strong> Schemas + test checks in CI; break-glass policy per domain.</li>
    <li><strong>Audit:</strong> Query logs to lake; line-level access events retained per policy.</li>
  </ul>
</section>

<section class="if-container" style="padding:8px 0 32px">
  <h2>Success measures</h2>
  <ul>
    <li>Time to publish a new data product (PRD → prod).</li>
    <li>Adoption: active consumers, product reuse across domains.</li>
    <li>Cost efficiency: bytes scanned / query, cache hit-rate, egress avoided.</li>
    <li>Quality: contract pass-rate, freshness SLO, incident MTTR.</li>
  </ul>
  <div class="if-hero-ctas" style="margin-top:12px">
    <a href="/contact/" class="if-button">Book a discovery call</a>
    <a href="/start/" class="if-link">Try the starter →</a>
  </div>
</section>