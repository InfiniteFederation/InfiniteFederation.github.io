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



Because this meeting concerns a workplace escalation, I’ve kept the notes factual and attributed allegations/comments rather than presenting disputed statements as established facts. This format should also be suitable for your own record if you later need to reconstruct the timeline.

Meeting Notes – Workplace Concerns and Management Escalation

Participants: Vivek, Anthony
Purpose: Escalation of concerns regarding management interactions, team environment, delivery/rating discussions, and impact on ability to work effectively
Confidentiality: Personal / Management discussion

⸻

1. Purpose of the Discussion

Vivek requested the meeting with Anthony to raise concerns that had accumulated over approximately the previous six months regarding interactions with his manager, JC.

Vivek explained that he had previously tried to manage the situation himself and had deliberately avoided escalating it. However, following several incidents before and after his extended leave, he felt the situation had reached a point where he required Anthony’s support.

The principal concerns raised were:

* Repeated interactions perceived as aggressive or disrespectful.
* Delivery discussions repeatedly being connected to performance ratings.
* Concerns regarding the tone and manner in which feedback was delivered.
* Lack of clarity/alignment around requirements followed by criticism of the team’s output.
* Impact on motivation and ability to concentrate on technical work.
* Concerns that similar issues may be affecting other members of the team.
* Concern about recent employee departures and the overall team environment.

⸻

2. April Incident – Delivery Discussion

Vivek described an incident around 13 April, shortly after the Stability Sprint and during a period involving a production/P2 issue.

According to Vivek:

* JC contacted him and initiated an extended discussion regarding what Vivek had delivered during the previous two sprints.
* Vivek explained the work he had completed and subsequently maintained detailed records of his activities.
* During the discussion, Vivek requested that the conversation be paused and resumed later.
* Vivek stated that the conversation continued despite those requests.
* Vivek eventually became visibly/emotionally distressed and said that he was not feeling well and needed to end the call.
* According to Vivek, JC then turned on his camera and asked Vivek to turn on his camera as well.

Vivek described this as a particularly difficult interaction and stated that it had a significant personal impact.

He noted that during approximately 11 years with J.P. Morgan, he had not previously experienced workplace interactions affecting him in this manner.

⸻

3. Period Before Leave

Vivek explained that concerns continued during the months following the April incident.

He said that he and other colleagues attempted to continue delivering despite the difficult working environment.

Before taking leave beginning around 27 June, Vivek stated that he attempted to consolidate the technical work and document the remaining pieces required for delivery, including integration, testing and final implementation activities.

Vivek said that one reason for taking the extended break was to focus on his wellbeing and that he hoped the working situation would improve when he returned.

⸻

4. Return From Leave

Vivek returned around 12 August.

He explained that he initially expected the situation to have improved but encountered further difficult interactions shortly after returning.

He also noted that several colleagues had left or were planning to leave during the wider period, which increased his concern regarding team morale and stability.

Vivek stated that he believed a broader skip-level discussion with team members could provide additional perspective on the working environment.

⸻

5. August Smart Approval Incident

Vivek described an incident in approximately mid/late August involving the Smart Approval design.

Context

Vivek and Varsha were working on the integration between:

* Intent-based authorization; and
* Smart Approval.

The team presented its initial design to John and received requests for additional low-level technical detail.

Concern raised

Following the session with John, Vivek stated that JC challenged the team regarding why they had presented that particular design.

JC reportedly referred to another design that had previously been discussed with Anthony, Robbie, John and JC.

Vivek stated that:

* He had not previously seen that design.
* Varsha had also not seen it.
* The design had not been available to the implementation team despite the team being expected to work against it.
* The manner in which the issue was raised was perceived as rude/aggressive.

Vivek’s concern was therefore not simply disagreement over the architecture, but that the team was criticized for not following information that they did not believe had previously been provided to them.

Anthony asked questions during the meeting to establish which design and meeting were being referenced and to clarify the timeline.

⸻

6. Recent Delivery / Rating Discussion

Vivek described another discussion occurring shortly before this meeting concerning delivery.

According to Vivek, the discussion repeatedly focused on:

* What had been delivered.
* Demonstrating the value delivered.
* Potential impact on individual ratings.
* Potential impact on team ratings.

Vivek said he counted approximately 13 references to “delivery/delivered” during the roughly hour-long discussion.

Vivek stated that he attempted to explain that work was actively progressing, artifacts existed, and the team was integrating the various technical components.

His concern was not being held accountable for delivery itself; rather, it was the repeated association of delivery concerns with ratings and the manner in which that message was communicated.

Vivek described the interaction as demotivating and said it contributed to his concern about the overall working environment.

⸻

7. Concern Regarding Varsha’s Rating

Vivek raised a separate concern regarding a discussion about Varsha, who was leaving the firm.

According to Vivek, JC discussed potentially assigning Varsha the lowest performance rating because of perceived lack of delivery.

Vivek stated that he challenged the delivery characterization by highlighting work Varsha had completed, including contributions during production/data issues and other technical incidents.

Vivek also recalled a comment/question regarding whether a sufficiently low rating could affect Varsha’s ability to return to the firm in the future.

Vivek raised this with Anthony because he was concerned about whether departure status was being considered in connection with performance rating.

No conclusion was reached in this meeting regarding the actual rating or whether such a rating would affect future re-employment.

⸻

8. Concern Regarding Notice-Period Comment

Vivek also recalled a conversation following discussion of another colleague’s resignation.

During discussion of notice periods, a comment was reportedly made to the effect that Dublin employees had the “privilege” of a two-month notice period, contrasted with situations where people could be let go with no notice.

Vivek stated that he found the comment concerning in the context of the other interactions taking place within the team.

⸻

9. Team Attrition and Morale

Vivek raised broader concerns regarding team morale and employee retention.

He noted multiple recent departures and stated that some long-tenured and technically strong colleagues had either left or were planning to leave.

He expressed concern that:

* Team energy had deteriorated.
* Engineers who had previously been highly motivated appeared less engaged.
* Repeated difficult management interactions could be contributing to the environment.
* Other team members may have their own examples or experiences.

Vivek suggested that skip-level conversations with individual team members could help management independently understand whether the concerns were isolated or more widespread.

⸻

10. Impact on Vivek

Vivek explained that the situation was affecting him beyond individual meetings.

He described:

* Difficulty disengaging from conversations after work.
* Replaying comments and interactions mentally.
* Difficulty sleeping after particularly difficult interactions.
* Reduced ability to focus on technical problem-solving.
* Loss of motivation following repeated negative interactions.
* Emotional distress following some conversations.

Vivek contrasted this with his previous experience of thinking about technical ideas and solutions outside normal working hours.

He stated that his concern was increasingly that his mental energy was being consumed by workplace interactions rather than engineering problems.

⸻

11. Confidence in Delivery and Technical Ownership

Vivek emphasized that he was not seeking to avoid delivery accountability or technical ownership.

He stated that:

* He remains confident in his ability to deliver the Intent work.
* He is comfortable owning complex technical problems.
* He has previously helped build major capabilities from the ground up.
* He is willing to continue taking responsibility for difficult engineering work.

The issue raised was specifically the working environment and management relationship rather than unwillingness to deliver.

⸻

12. Request for Management Support

Vivek asked Anthony for support in resolving the situation.

He indicated that he would be open to:

* Remaining responsible for his technical deliverables while the management issue is addressed; or
* Moving to another team/reporting arrangement if that is the most appropriate solution.

Vivek stated that he believed he could perform effectively elsewhere but did not believe the current situation was sustainable without intervention.

He also expressed concern about the potential effect of the situation on his performance rating.

⸻

13. Anthony’s Response

Anthony acknowledged the seriousness of the concerns.

During the discussion, Anthony stated:

“This is not acceptable.”

He indicated that the environment described by Vivek was not how he wanted the organization to operate.

Anthony asked follow-up questions to understand:

* The timeline.
* The April incident.
* The August Smart Approval/design incident.
* Recent delivery discussions.
* Whether delivery/rating concerns had continued after Vivek returned from leave.

Anthony told Vivek that he would address the situation and reassured him regarding his concerns.

Anthony also encouraged Vivek to raise similar issues earlier in the future so that management has an opportunity to intervene before they escalate.

⸻

14. Key Points Recorded

#	Point
1	Vivek formally raised concerns regarding repeated management interactions over approximately six months.
2	Concerns relate primarily to communication style, perceived disrespect/aggression, repeated delivery/rating pressure, and the resulting working environment.
3	Vivek described a significant April interaction regarding sprint delivery.
4	A further Smart Approval/design incident occurred after Vivek returned from leave in August.
5	Vivek raised concern about a recent delivery discussion repeatedly connecting delivery with performance ratings.
6	Vivek raised a separate concern regarding discussion of Varsha’s rating while she was leaving the firm.
7	Broader team attrition and morale were raised as potential indicators that the issue may extend beyond one individual.
8	Vivek stated that the situation was affecting concentration, sleep, motivation and ability to disengage from work interactions.
9	Vivek reiterated confidence in his technical ability and willingness to remain accountable for delivery.
10	Vivek requested management intervention and indicated willingness to move teams/reporting lines if necessary.
11	Anthony stated that the situation described was not acceptable and committed to addressing it.
12	Anthony encouraged earlier escalation of similar concerns in the future.

Follow-Up Items

Action	Owner
Review the concerns and determine appropriate management intervention	Anthony
Review the relevant incidents/timeline as necessary	Anthony
Consider whether broader team/skip-level conversations are appropriate	Anthony
Preserve factual notes/timeline of relevant interactions	Vivek
Continue documenting specific incidents with dates, participants and factual wording where possible	Vivek
Follow up regarding the working/reporting arrangement after management review	Anthony / Vivek
Continue technical delivery while the management concern is addressed, subject to agreed arrangements	Vivek

Overall Outcome

Vivek formally escalated concerns regarding his working relationship with JC and explained that the cumulative effect of repeated interactions had become unsustainable for him.

Anthony acknowledged the concerns, stated that the behavior/environment described was not acceptable, and indicated that he would take steps to address the situation.

Vivek made clear that his objective is to return to a working environment where he can focus on engineering and delivery, and that he is open to an alternative team or reporting arrangement if required.

For a sensitive matter like this, this version is deliberately written as a contemporaneous factual record—e.g. “Vivek stated/recalled/reported” rather than asserting disputed events as proven facts.
