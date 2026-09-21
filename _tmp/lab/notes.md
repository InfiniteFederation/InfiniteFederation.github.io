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

Next Meeting

A follow-up discussion is planned for 22 September 2026, after the collision/global-uniqueness page has been prepared and reviewed.

I can also turn this into a shorter executive MoM for sharing with senior stakeholders, with the discussion compressed to Problem → Decision → Architecture → Actions.
