Primary Topics: Cross-tenant table sharing, naming collisions, global uniqueness, migration strategy, Intent/OPA architecture, delivery/process improvements

1. Cross-Tenant Sharing & Naming Collision

The main discussion focused on how shared API tables/views should behave when a consumer already has a local object with the same name.

Two key requirements emerged:

* Shared objects should retain clear and predictable names; adding arbitrary prefixes purely to avoid collisions is not desirable from a user-experience perspective.
* A naming collision must not be silently resolved. If the system cannot guarantee correct resolution, it should fail or explicitly identify the conflict rather than quietly selecting the wrong object.

The current implementation gives precedence to the local entity when a local and shared/virtual entity have the same name. This could result in a query resolving successfully but against the wrong object, which is considered unsafe behavior.

Principle agreed: Correctness takes precedence over making sharing succeed.

⸻

2. Two Collision Scenarios to Analyse

Two scenarios need to be explicitly documented and validated.

Scenario A – Shared object conflicts with existing local object

1. Team/Tenant A creates and shares a table.
2. Team/Tenant B already has a local table with the same name.
3. Applying the sharing rule introduces a collision.

The provider must not be able to unintentionally break the consumer’s existing environment.

Scenario B – New local object conflicts with existing shared object

1. Team/Tenant B already consumes a shared table from Team A.
2. Team B subsequently attempts to create a local table using the same name.
3. The new local object would override/conflict with the shared object.

The system should detect this situation before allowing the environment to enter an ambiguous state.

⸻

3. Global Uniqueness – Target Solution

The preferred long-term solution is to enforce a globally unique naming convention across tenants/clusters.

This would eliminate most runtime ambiguity instead of trying to resolve collisions after they occur.

The Iceberg model provides a useful precedent because schemas already incorporate tenant-specific naming.

A similar convention should be evaluated for API tables, for example:

<tenant>__<object-name>

Using a clear delimiter such as __ would make the tenant namespace and object name distinguishable.

Direction: Establish and enforce global uniqueness rather than relying on runtime precedence rules.

⸻

4. Short-Term vs. Long-Term Solution

There is a distinction between the immediate solution required to enable sharing and the maintainable target architecture.

Short term

* Remove the current prefix where required to support the expected sharing experience.
* Allow the sharing capability to progress.
* Clearly document the collision limitations.
* Ensure collisions are observable rather than silently hidden.

Long term

Introduce:

* Global naming/uniqueness rules.
* Validation before object creation.
* Validation when new sharing rules are introduced.
* Auditing of existing objects and sharing rules.
* Alerts/errors when collisions are detected.
* Build/pipeline enforcement where appropriate.
* A migration mechanism for existing objects.

The short-term implementation should therefore be treated as a stepping stone, not the final design.

⸻

5. Validation and Enforcement

Validation is required from both directions.

When creating a local table:
Check whether the proposed name conflicts with an object already available through sharing.

When introducing a new shared object/rule:
Check whether consumers already contain objects with the same name.

This is important because either the producer can break a consumer or the consumer can create something that overrides an existing shared object.

Validation may need to exist beyond the DVP/build pipeline because users may create objects through prototype or other paths that bypass standard project controls.

A Trino-level validation capability or centralized metadata service may therefore ultimately provide stronger enforcement.

⸻

6. Migration Strategy

Introducing tenant-prefixed globally unique names will potentially break existing:

* Queries
* DBT models
* Scripts
* Applications
* Existing integrations

Therefore, the naming convention cannot simply be switched on without a migration strategy.

Potential approaches discussed:

* Temporary aliases for legacy names.
* Query rewriting through Trino Gateway.
* Supporting old names during a defined migration window.
* Gradual migration of existing models and queries.

Gateway-based rewriting may help, but it also has implications because rewritten queries could subsequently appear using the new object names.

The migration mechanism requires further design before implementation.

⸻

7. Documentation / Visual Required

The technical analysis should be communicated simply.

Rather than a large technical document, create a page containing:

* A small visual showing producer → sharing → consumer.
* Scenario A: shared object conflicts with existing local object.
* Scenario B: new local object conflicts with existing shared object.
* Why silent precedence is unsafe.
* Short-term behavior.
* Target global-uniqueness model.
* Migration implications.

The document should explain why the change is required, not only what will be implemented.

John will review the page once the initial version is prepared.

⸻

8. Engineering Process / Team Structure

A broader engineering concern was discussed around the amount of cross-team and unofficial work.

Current challenges include:

* Too many people joining incidents unnecessarily.
* Engineers receiving work informally through relationships rather than through an owned backlog.
* Significant cross-team dependencies.
* Unclear ownership of some components.
* Senior engineers becoming overloaded with reviews, support and problem solving.
* Goodwill leading engineers to continuously take on other teams’ problems.
* Burnout risk.
* Too much time in meetings relative to product-led execution.

The preferred model is stronger ownership with work flowing through prioritised queues/backlogs instead of treating every issue as an all-hands problem.

Senior engineers also need sufficient capacity to mentor less-experienced engineers rather than simply absorbing additional implementation work.

⸻

9. Intent / OPA Discussion

The current Intent work and the possible role of MOAT were also reviewed.

The immediate question is whether MOAT is actually required for the capabilities currently being delivered.

Current requirements largely consist of:

1. Intent YAML/configuration validation and parsing.
2. Translation of configuration into the required policy representation.
3. Creation of application-specific policy bundles.
4. Distribution/integration of those bundles with OPA.

Much of this capability is required regardless of whether MOAT is introduced.

The newer OPA control-plane and dynamic/patch bundle capabilities should also be considered before introducing another architectural component.

Direction: Avoid a late architectural course correction until the base capability is delivered and evaluated.

⸻

10. OPA Rollout Strategy

A gradual rollout was discussed rather than directly replacing the existing file-based access-control mechanism.

Proposed approach:

1. Keep existing file-based access control as the authoritative enforcement mechanism.
2. Run OPA alongside it in audit mode.
3. Compare OPA decisions against existing access-control decisions.
4. Observe correctness and performance in production-like conditions.
5. Resolve discrepancies.
6. Once confidence is established, progressively move enforcement to OPA.

This provides a safer migration path and allows the new policy model to be validated before it becomes authoritative.

A possible change may be needed to the OPA integration/response behavior to permit fall-through while OPA operates in audit mode.

John has an earlier design covering this approach and will resend/find the document.

⸻

Decisions / Direction

#	Decision / Direction
1	Silent collision resolution is not acceptable; correctness takes precedence over successful sharing.
2	Document and analyse both producer→consumer and consumer→shared-object collision scenarios.
3	Global uniqueness is the preferred long-term solution.
4	Tenant-based naming should be evaluated for API tables, similar to the existing Iceberg approach.
5	The immediate sharing implementation may be used as a short-term stepping stone, with its limitations documented.
6	Validation/auditing must eventually cover both creation of local objects and introduction of shared objects.
7	Existing objects require a migration strategy before enforcing the new naming convention.
8	OPA should be introduced incrementally, preferably through audit-mode comparison before becoming authoritative.
9	Avoid introducing MOAT solely for capabilities already achievable through the current Intent/OPA design without demonstrating a clear additional requirement.

Action Items

Action	Owner	Target
Create a concise technical page explaining the two collision scenarios	Vivek	Today
Add a small visual showing producer/shared/consumer collision behavior	Vivek	Today
Document short-term solution vs. globally unique target architecture	Vivek	Today
Document migration implications for existing tables, DBT models, scripts and integrations	Vivek	Initial analysis
Send the page to John for technical review	Vivek	Today
Review Vivek’s collision/global-uniqueness analysis	John	After receipt
Locate/resend previous OPA + file-based access-control audit-mode design	John	Follow-up
Evaluate global uniqueness enforcement mechanism and where validation should occur	Vivek / Team	Follow-up
Evaluate migration options, including Trino Gateway/temporary legacy aliases	Team	Follow-up
Schedule follow-up discussion with relevant stakeholders	Vivek	Tomorrow



--------------------------------------------------------------------------------------


I cleaned the transcript into structured technical meeting notes, focusing on requirements, architectural decisions, and follow-ups rather than the conversational back-and-forth.

Meeting Notes – Intent, Policy Model, OPA & Resource Authorization

Participants: Vivek, Anthony, Mia, team members
Primary Topics: Day-1 requirements, Intent model, resource mapping, OPA caching, policy syntax, Invokers, environment isolation, Portico permissions, fine-grained access control

1. Team Alignment / Day-1 Requirements

The team needs stronger coordination across the different implementation streams. There is a risk of teams building individual components in isolation and producing incompatible solutions.

A number of Day-1 requirements were clarified with stakeholders while some team members were unavailable, and not all of these requirements are reflected consistently in the existing documentation.

Agreed approach

* Conduct another end-to-end review of the Day-1 requirements with the wider team.
* Walk through the complete Intent specification and recently clarified requirements.
* Ensure implementation teams understand dependencies between their components.
* Add additional use cases and test cases as requirements are clarified.
* Keep requirements, examples and Intent specifications synchronized.

Follow-up: Mia will update the requirements/specification and examples, followed by a team walkthrough.

⸻

2. Intent → Resource Mapping

The existing backend design already contains an Intent/Resource mapping concept.

The runtime authorization model needs to determine:

Runtime Resource ID → Resource Path → Intent(s)

The path alone is insufficient because runtime systems generally invoke resources using IDs rather than the human-readable paths defined in the Intent file.

Examples include:

* Agent ID
* Kestra Flow ID
* Galaxy Lens ID
* API table/resource ID
* Portico resource/schema ID

The system therefore needs a persistent/cached mapping from runtime resource identifiers to their associated path and Intent.

Important point

A resource may belong to multiple Intents.

Adding a resource to additional Intents can potentially grant it additional authorization, analogous to assigning additional roles to a user.

This mapping should be available consistently across resource types rather than implementing special cases for individual platforms.

⸻

3. Runtime Authorization Data Must Be Cached

The Intent/resource mapping must not be queried from PostgreSQL/Mobius on every authorization request.

Doing so would create significant additional load because authorization will be required for:

* Trino requests
* Agent requests
* Kestra requests
* Portico requests
* Galaxy requests
* Other future integrations

At scale, performing a database/Trino lookup for every authorization decision could generate millions of additional queries and is not considered viable.

Proposed caching model

1. OPA/authorization components start.
2. During startup, they load the required Intent/resource mapping.
3. Data is stored in memory/cache.
4. All authorization decisions are served from cache.
5. A successful Intent build triggers a cache refresh.
6. Existing cache remains active while the new data is loaded.
7. Once loading completes, the new cache replaces the previous version.

This provides effectively continuous authorization availability without introducing a synchronous database dependency into every request.

Initial optimization strategy

Start by refreshing the complete dataset rather than implementing incremental/differential cache updates.

The expected dataset is relatively small—likely MB-scale even with a substantial number of resources—so full refresh is simpler and sufficient initially.

Incremental cache updates can be introduced later if scale demonstrates a need.

⸻

4. Fail-Closed Behaviour

Authorization must operate fail closed.

At startup:

* The service must successfully populate its authorization cache before becoming healthy/live.
* If the source is unavailable, startup should remain in a failed/not-ready state.
* Appropriate retries/backoff should be implemented.
* Monitoring/alerts should indicate that the authorization component cannot initialize.

A component must never start serving authorization requests without valid policy/mapping data.

After startup, the existing valid cache can continue serving requests while a refresh occurs.

⸻

5. Environment-Specific S3 Policy Bundles

The existing design showed one S3 bucket being shared across multiple independently deployable environments.

This was identified as an architectural coupling.

Problem

If Prototype, Staging and Production/App use one bucket, changes to:

* Folder structure
* File format
* Bundle structure
* Policy layout

could implicitly bind all environments to the same deployment/version.

This makes independent rollout difficult and introduces cross-environment contamination risk.

Decision

Use one S3 bucket per environment.

Example convention:

policy-bundle-<environment>

For example:

* policy-bundle-prototype
* policy-bundle-staging
* policy-bundle-app

The deployment pipeline can derive the destination using the environment variable rather than implementing environment-specific logic.

Benefits

* Independent deployments
* Environment isolation
* Reduced cross-contamination risk
* Independent schema/bundle evolution
* Simple addition of future environments

⸻

6. Intent Approval Data

Two related but separate concepts were clarified.

Intent configuration

The Intent file describes the desired/configured approval or entitlement information.

Runtime approval state

Actual approval decisions and live approval state should come from the relevant runtime approval/event mechanism, including the Portico event stream.

The configured Intent definition should therefore not be confused with the live approval state.

⸻

7. Access Type vs. Entitlement

The term Entitlement was discussed as potentially clearer than access_type.

Reasoning:

An entitlement can represent more than traditional read/write access.

Examples include:

* Read
* Write
* Publish
* Share
* Execute/invoke

For example, permission to share a resource is naturally an entitlement even though it may not intuitively be described as “access.”

The terminology should be reviewed for consistency, but the broader conceptual model is:

Entitlements describe what an identity/resource is permitted to do.

⸻

8. Major Policy Simplification – Introduce invokers

The existing Intent model used multiple Boolean flags such as:

* is_agent
* is_kestra
* is_sid
* other future is_* flags

This approach does not scale as more calling systems are introduced.

Decision

Replace these Boolean flags with a generalized:

invokers

concept.

An Invoker identifies who or what participated in invoking the protected resource.

Potential Invokers include:

* Employee
* Contractor
* FID
* Agent
* Kestra
* Galaxy
* Portico
* Additional systems in the future

This eliminates the need to add a new Boolean property every time a new platform is integrated.

⸻

9. Invoker Chain

Authorization should support an Invoker chain, rather than only identifying the immediate caller.

For example:

Employee → Agent → Portico

or

Employee → Agent → Kestra

This enables policies to reason about the complete path through which an operation was initiated.

That is particularly important for sensitive write/publish operations.

The policy can therefore distinguish between:

* A user acting directly
* A user through an Agent
* An Agent through Kestra
* A user → Agent → Portico flow
* A system-to-system operation

This was considered significantly cleaner than the previous collection of Boolean flags.

⸻

10. any_of and all_of Semantics

Array-based policy attributes need clear Boolean semantics.

The proposed convention is:

Default array behaviour

An array has an implicit ANY OF semantic.

For example, an Invoker array containing Agent and Kestra means either value can satisfy the condition unless otherwise specified.

Explicit all_of

Where multiple conditions must simultaneously hold, use an explicit all_of.

This pattern should apply consistently across:

* Invokers
* Roles
* Invoker Intents
* Other array-based attributes

Care must be taken when consolidating arrays because changing two separate required conditions into one array could unintentionally change an AND into an OR.

⸻

11. Default Agent Deny

A critical security requirement from the previous is_agent design must be preserved.

Previously, agent access was effectively:

Denied unless explicitly enabled.

The same principle must continue with Invokers.

Required behaviour

If a policy does not explicitly allow an Agent Invoker, the generated policy should effectively contain:

NOT Agent

Therefore, a tenant whose Intent specification contains no Agent authorization should automatically deny agent access.

This prevents introducing agent access accidentally when a policy author only intended to authorize human users.

⸻

12. Employee, Contractor and FID Identity

The previous user abstraction needs additional refinement because authorization differentiates between different identity categories.

Relevant categories include:

* Employee
* Contractor
* FID/system identity

This is important because some data classifications may not be accessible to contractors even when other permissions appear to match.

Proposed Invoker vocabulary

The base Invoker set should therefore include:

* employee
* contractor
* fid
* agent
* kestra
* galaxy
* portico

Additional Invokers can be added later.

⸻

13. Roles vs. Invoker Intents

Two distinct authorization mechanisms are required.

Human identities

For human identities such as employees/contractors:

Roles provide fine-grained authorization.

Non-human/system resources

For Agents, Kestra flows, Galaxy lenses, Portico resources, etc.:

Intent membership provides the equivalent fine-grained authorization context.

This is necessary because many of these systems do not have a traditional identity/role model.

Conceptually:

Roles are authorization context for users.
Intent membership is authorization context for system resources.

⸻

14. Rename intents to invoker_intents

The generic name intents in the policy specification was considered potentially confusing.

Proposed change

Rename:

intents

to:

invoker_intents

This makes the meaning explicit:

The policy is checking which Intent(s) the invoking resource belongs to.

This is particularly useful because Intent membership is a new authorization concept for many users.

⸻

15. Roles and Invoker Intents Should Apply Contextually

Roles and Invoker Intents should not blindly apply to every Invoker.

Roles

Roles are relevant when evaluating human identities.

Invoker Intents

Invoker Intents are relevant when evaluating non-human resources such as:

* Agents
* Kestra flows
* Galaxy lenses
* Portico resources
* API tables where applicable

Policy generation must account for this distinction so that a valid system request is not denied merely because a non-human Invoker does not possess a human role.

⸻

16. Resource Mapping for DVT/DBT/API Tables

Initially it appeared that DVT/DBT resources might not need persistent runtime Intent mapping because they primarily receive queries.

However, API tables and similar resources can potentially initiate downstream actions—for example, publishing a Portico event.

Therefore, the mapping should be retained consistently for these resources as well.

Example:

1. API table belongs to the Email Recall Intent.
2. API table attempts to publish an Email Recall Portico event.
3. Authorization resolves API table → path → Intent.
4. Policy requires the Email Recall Intent.
5. Request succeeds only if the API table belongs to the required Intent.

This allows fine-grained control over which API tables/resources can invoke sensitive downstream capabilities.

⸻

17. Resource Exclusivity

resource_exclusivity should not be mandatory.

It should be an optional capability for genuinely sensitive use cases.

Examples may include highly sensitive cyber-operations functions such as Email Recall.

Documentation guidance

Documentation should actively discourage unnecessary exclusivity.

The preferred organizational principle is:

Resources/data should remain as open as reasonably possible across authorized cyber analysts unless there is a genuine sensitivity or control requirement.

The goal is to avoid teams creating unnecessary data silos.

⸻

18. Role Creation / Management

Users are expected to use centrally managed roles rather than embedding individual SIDs directly into policy.

An entitlement-management API exists for role creation.

A limited set of authorized tenant administrators/owners could potentially manage roles for their tenant.

Cross-resource role challenge

A role may be associated with a central resource/CLID such as Cosmos while the consuming service runs under another resource identity.

The JWT presented to the consuming service may therefore not contain that external role claim.

The authorization platform must consequently retrieve the role membership from the directory/entitlement system and cache it.

A small propagation delay is acceptable—for example, minutes rather than immediate consistency—provided it is controlled and documented.

⸻

19. Remove Direct SID-Based Policy Authorization

Direct SID/user identifiers should not be routinely specified in Intent policies.

Reasons include:

* Governance complexity
* Entitlement auditing requirements
* High risk of misuse
* Inconsistent authorization management
* Bypassing the normal role-approval process

Decision

Remove SID-based authorization from the normal Intent policy model.

Users should receive access through managed roles.

⸻

20. Write Access Requires Special Protection

Write access was identified as significantly more sensitive than read access.

Principle

Users should not directly issue writes against protected tables in Staging or Production/App.

Writes should occur through controlled systems such as:

* Portico
* Kestra
* other explicitly approved mechanisms

For example:

User → Agent → Portico → Write

may be permitted if all required policy checks succeed.

But:

User → direct table UPDATE

should be denied in controlled environments.

Similarly:

User → Agent → direct table write

should not automatically become acceptable simply because an Agent is involved.

Prototype

Prototype may permit more permissive behavior for development/testing, although the team acknowledged this may require future discussion with control/audit stakeholders.

⸻

21. Hard Guardrails for Write Entitlements

Some restrictions should not depend entirely on tenant-authored policy.

The generated Rego should contain platform-level guardrails preventing unsafe direct writes in Staging/App even if a policy author accidentally writes a permissive rule.

This provides defense in depth.

The Invoker chain makes these controls easier because the policy can determine whether the request came:

* directly from a user;
* through an Agent;
* through Portico;
* through Kestra;
* through another approved service.

⸻

22. Portico publish and read Are Separate Entitlements

Portico needs separate entitlements for:

* Publish
* Read

Publish permission must not automatically imply Read permission, and Read must not imply Publish.

Example:

A service may need permission to publish an Email Recall event without requiring broad access to read every Email Recall event.

Conversely, many consumers may need to read an event stream without being permitted to publish events.

⸻

23. Portico Fine-Grained Filtering

Initial implementation can allow Read access at the schema level.

Future fine-grained authorization should support filtering within a schema.

For example:

* User/role can read Schema A.
* Policy further restricts access to events where a field matches a permitted value.

The same concept can potentially apply to publishing, allowing policy to constrain which subsets/types of events a caller may publish.

This will be particularly valuable for AI/Agent use cases because it can constrain what autonomous systems are allowed to perform.

⸻

24. Column-Level Security / Masking

Column-level access behavior was discussed.

Returning unauthorized columns populated with NULL can be problematic because consumers cannot distinguish:

* genuinely null data;
* authorization masking;
* data-quality issues.

Stakeholder preference is therefore to omit unauthorized columns entirely where possible.

Trino already provides capabilities that can support this approach.

A future enhancement could expose masking behavior as a configurable policy option.

⸻

25. Intent-Based Security Boundary

Intent membership effectively creates a fine-grained security boundary around related resources.

Example – Email Recall Intent

The Intent might contain:

* API table
* Portico event/schema
* Kestra flow
* Agent

Policies can then require that the invoking resources belong to the Email Recall Intent.

This prevents an arbitrary Agent, Kestra flow or API table in the tenant from invoking a sensitive Email Recall capability.

This was identified as a particularly important use case for invoker_intents.

⸻

26. Simplified Policy Model

The discussion converged toward a substantially cleaner base policy model.

Most policies can potentially be expressed using four major concepts:

Invokers

Who/what is calling, including the invocation chain.

Roles

Fine-grained authorization context for human identities.

Invoker Intents

Fine-grained authorization context for non-human resources.

Environment

Prototype, Staging, App/Production, etc.

This replaces numerous special-case Boolean properties and provides a more extensible foundation.

⸻

27. Incremental Delivery

The Intent specification is expected to continue evolving.

The team should not wait for the entire Intent specification to become perfect before integrating components.

Agreed engineering approach

* Build the integration against the current specification.
* Incorporate specification changes incrementally.
* Keep implementation modular enough to absorb deltas.
* Get usable capabilities deployed early.
* Avoid a waterfall model where every upstream component must be finalized before downstream integration begins.

⸻

Key Decisions

#	Decision
1	Re-review all Day-1 requirements with the full team.
2	Maintain a runtime Resource ID → Path → Intent mapping.
3	Cache authorization/resource-mapping data; do not perform database/Trino lookups per request.
4	Authorization services must fail closed if initial cache population fails.
5	Use separate policy-bundle S3 buckets for each independently deployed environment.
6	Replace multiple is_* flags with a generalized invokers model.
7	Preserve default-deny behavior for Agents unless Agent invocation is explicitly authorized.
8	Support Invoker chains such as Employee → Agent → Portico.
9	Use explicit all_of when multiple conditions must simultaneously hold; arrays otherwise have ANY semantics.
10	Distinguish Employee, Contractor and FID/system identities.
11	Use Roles for human authorization and Intent membership for system-resource authorization.
12	Rename intents to invoker_intents for clarity.
13	Remove direct SID-based authorization from the standard Intent model.
14	Resource exclusivity is optional and should be discouraged unless genuinely required.
15	Direct user writes should be denied in Staging/App; writes should flow through approved controlled mechanisms.
16	publish and read are separate Portico entitlements.
17	Prefer hiding unauthorized columns rather than returning misleading NULL values.
18	Continue implementation incrementally as the Intent specification evolves.

Action Items

Action	Owner
Update Day-1 requirements with the latest decisions	Mia
Update Intent specification and examples so all documentation is consistent	Mia
Replace is_* Boolean examples with the new invokers model	Team
Add Employee, Contractor, FID, Agent, Kestra, Galaxy and Portico Invoker types	Team
Define and document Invoker-chain semantics	Team
Preserve implicit/default Agent-deny behavior in generated Rego	Team
Define any_of / all_of semantics consistently across policy arrays	Team
Update intents terminology to invoker_intents	Team
Finalize Resource ID → Path → Intent mapping	Vivek / Team
Design cache initialization, refresh, retry and fail-closed behavior	Vivek / Team
Separate S3 policy-bundle storage by environment	Team
Review role lookup/caching across CLIDs/resources	Team
Remove SID-based authorization from updated examples/specification	Mia / Team
Define platform-level guardrails for direct write access in Staging/App	Team
Document Portico Read vs. Publish entitlement semantics	Team
Review column hiding/masking behavior with Trino capabilities	Team
Walk the wider team through the revised requirements and Invoker model	Mia / Vivek
Share the simplified Invoker approach with John for review	Vivek

Next Step

The immediate next step is to update the requirements, Intent specification and examples, incorporating the new Invoker model and other decisions above, then conduct a full team walkthrough to ensure all implementation streams are aligned.

This meeting contains several significant architecture decisions; the biggest is the move from multiple is_* flags to an invokers + roles + invoker_intents + environment authorization model, which is worth capturing separately as an ADR/design decision.
