---
layout: default
title: Data Engineering Interview Crash Workbook
permalink: /lab/salesforce/data-engineering-interview-crash-workbook/
description: 3-day intensive prep workbook for Salesforce, Google, Snowflake, and Databricks data engineering interviews.
---

# Data Engineering Interview Crash Workbook

*3-Day Intensive Prep for Salesforce, Google, Snowflake, Databricks*

## Table of Contents

1. [How To Use This Workbook](#how-to-use-this-workbook)
2. [3-Day Study Plan](#3-day-study-plan)
3. [Master Question Bank With Answer Patterns](#master-question-bank-with-answer-patterns)
4. [Section 1: SQL](#section-1--sql-1-150)
5. [Section 2: Coding](#section-2--coding-151-300)
6. [Section 3: Data Modeling](#section-3--data-modeling-301-400)
7. [Section 4: Data Architecture](#section-4--data-architecture-401-500)
8. [Rapid Hands-On Exercise Plan](#rapid-hands-on-exercise-plan)
9. [Final 3-Day Prioritization](#final-3-day-prioritization)
10. [Interview Speaking Rules](#interview-speaking-rules)
11. [Personal Focus For You](#personal-focus-for-you)

---

## How To Use This Workbook

You have 3 days, so do not try to memorize 500 disconnected answers.
Use this workbook in this order for every topic:
1. Understand the idea
2. Say the short answer out loud
3. Expand into the long answer
4. Do the exercise by hand
5. Code or query it yourself
6. Explain the trade-off

For each topic below, use this template:
- What is it?
- Why do we use it?
- When should we use it?
- What are the trade-offs?
- How would I implement it?
- What can go wrong?

---

## 3-Day Study Plan

### Day 1 — SQL + Data Modeling

#### Morning
- Window functions
- GROUP BY / HAVING
- Joins
- CTEs
- Deduplication

#### Afternoon
- Fact vs dimension
- Star vs snowflake
- SCD types
- Partitioning
- User growth modeling

#### Evening
- Solve 15 SQL problems
- Explain 5 modeling designs aloud

### Day 2 — Coding + Python Basics

#### Morning
- Arrays
- Strings
- Hash maps
- Stack / queue
- Big-O

#### Afternoon
- Linked list
- Trees
- Graphs
- BFS / DFS
- Sliding window / two pointers

#### Evening
- Solve 10 coding problems
- Rewrite in Python without looking

### Day 3 — Architecture + Behavioral + Mock Interviews

#### Morning
- Batch vs streaming
- Kafka
- Data lake / lakehouse
- Trino / Spark / Iceberg
- Governance / lineage / metadata

#### Afternoon
- System design drills
- Data pipeline design
- API ingestion design
- CDC design

#### Evening
- Behavioral answers
- 2 full mock rounds
- Rapid-fire review

---

## Master Question Bank With Answer Patterns

The 500 questions are grouped into patterns. Master the pattern once, then you can answer 10–20 related questions.

---

## Section 1 — SQL (1–150)

### A. Window Functions (1–25)

1. What is a window function?

**Short answer:** A window function calculates a value across related rows without collapsing the result set.

**Long answer:** Unlike GROUP BY, which reduces rows, a window function keeps each row and adds analytical context such as rank, running total, moving average, or previous/next row comparison. It usually uses OVER(), optionally with PARTITION BY and ORDER BY.

**Exercise:**
Take a sales table with columns (employee_id, sale_date, amount). Write a query that shows the running total by employee.

```sql
SELECT
  employee_id,
  sale_date,
  amount,
  SUM(amount) OVER (
    PARTITION BY employee_id
    ORDER BY sale_date
  ) AS running_total
FROM sales;
```

**Understand it:**
- PARTITION BY resets calculation per employee
- ORDER BY defines sequence
- rows stay visible

---

2. Difference between GROUP BY and window function

**Short answer:** GROUP BY collapses rows; window functions do not.

**Long answer:** Use GROUP BY when you need one row per group. Use window functions when you need row-level detail plus aggregated context.

**Exercise:** Compare these two queries on the same table and explain why one returns fewer rows.

---

3. ROW_NUMBER vs RANK vs DENSE_RANK

**Short answer:** ROW_NUMBER always unique; RANK leaves gaps on ties; DENSE_RANK does not.

**Long answer:**
- ROW_NUMBER: best for deduplication
- RANK: best when positional gaps are acceptable
- DENSE_RANK: best when tied values should share the same rank without skipping the next rank

**Exercise:** For salary values 100, 100, 90, write all three rankings and explain the output.

---

4. Running total

**Short answer:** Use SUM(...) OVER (ORDER BY ...).

**Exercise:**

```sql
SELECT order_date,
       revenue,
       SUM(revenue) OVER (ORDER BY order_date) AS running_revenue
FROM daily_revenue;
```

---

5. Moving average

**Short answer:** Use a frame clause such as ROWS BETWEEN 2 PRECEDING AND CURRENT ROW.

**Exercise:**

```sql
SELECT order_date,
       AVG(revenue) OVER (
         ORDER BY order_date
         ROWS BETWEEN 2 PRECEDING AND CURRENT ROW
       ) AS moving_avg_3
FROM daily_revenue;
```

**Understand it:** This is a 3-row moving average, not always 3 calendar days.

---

6. LEAD vs LAG

**Short answer:** LAG looks backward; LEAD looks forward.

**Exercise:** Find time between two consecutive user events.

```sql
SELECT
  user_id,
  event_time,
  event_time - LAG(event_time) OVER (
    PARTITION BY user_id ORDER BY event_time
  ) AS gap_from_previous
FROM events;
```

---

7. FIRST_VALUE and LAST_VALUE

**Short answer:** Return first or last value in the window.

**Important trap:** LAST_VALUE often needs a frame clause, otherwise it may not behave as expected.

---

8. NTILE

**Short answer:** Splits ordered rows into buckets.

**Use case:** quartiles, deciles, customer segmentation.

---

9. PERCENT_RANK

**Short answer:** Gives relative rank from 0 to 1.

---

10. CUME_DIST

**Short answer:** Cumulative distribution showing fraction of rows less than or equal to current row.

---

11. What is PARTITION BY?

**Short answer:** It defines groups for the window function.

---

12. What is ORDER BY in a window?

**Short answer:** It defines processing sequence inside each partition.

---

13. What is a frame clause?

**Short answer:** It narrows which rows in the ordered partition are included.

---

14. Deduplicate records using ROW_NUMBER

**Short answer:** Assign row numbers per business key, keep rn = 1.

```sql
WITH ranked AS (
  SELECT *,
         ROW_NUMBER() OVER (
           PARTITION BY customer_id
           ORDER BY updated_at DESC
         ) AS rn
  FROM customer_records
)
SELECT *
FROM ranked
WHERE rn = 1;
```

**Understand it:** This keeps the latest record per customer.

---

15. Top N per group

**Short answer:** Use ROW_NUMBER() or DENSE_RANK() with PARTITION BY.

---

16. Detect gaps in dates or IDs

**Short answer:** Compare current row with previous row using LAG.

---

17. Sessionization

**Short answer:** Split user activity into sessions based on inactivity threshold.

**Long answer:** Use LAG to compare current event time with previous event time. If the gap exceeds a threshold like 30 minutes, mark it as a new session. Then use cumulative sum to generate session IDs.

```sql
WITH tagged AS (
  SELECT *,
         CASE
           WHEN event_time - LAG(event_time) OVER (
             PARTITION BY user_id ORDER BY event_time
           ) > INTERVAL '30' MINUTE
           OR LAG(event_time) OVER (
             PARTITION BY user_id ORDER BY event_time
           ) IS NULL
           THEN 1 ELSE 0
         END AS new_session
  FROM events
), sessions AS (
  SELECT *,
         SUM(new_session) OVER (
           PARTITION BY user_id ORDER BY event_time
         ) AS session_id
  FROM tagged
)
SELECT * FROM sessions;
```

**Exercise:** Change threshold from 30 minutes to 15 minutes and explain what changes.

---

18–25. Practice prompts
18. Rank employees within department by salary
19. Find first purchase date per customer
20. Find repeat purchase gap
21. Rolling 7-day revenue
22. Top 3 products per category
23. Detect change in subscription plan
24. Compare current and previous order amount
25. Bucket customers into revenue quartiles

For each one, answer with:
- the window function needed
- partition column
- order column
- output meaning

---

### B. Joins (26–50)

**Sample tables used in the examples below**

**customers**

| customer_id | customer_name | region |
|---|---|---|
| 1 | Alice | EU |
| 2 | Bob | US |
| 3 | Carol | EU |
| 4 | Dan | APAC |

**orders**

| order_id | customer_id | region | amount |
|---|---|---|---:|
| 101 | 1 | EU | 120 |
| 102 | 1 | EU | 80 |
| 103 | 2 | US | 75 |
| 104 | 5 | US | 60 |

**employees**

| employee_id | employee_name | manager_id |
|---|---|---:|
| 10 | Eva | null |
| 11 | Frank | 10 |
| 12 | Grace | 10 |

26. What is an INNER JOIN?

**Short answer:** Returns only matching rows from both tables.

**Example:**

```sql
SELECT
  c.customer_id,
  c.customer_name,
  o.order_id,
  o.amount
FROM customers c
INNER JOIN orders o
  ON c.customer_id = o.customer_id;
```

**Result:**

| customer_id | customer_name | order_id | amount |
|---|---|---:|---:|
| 1 | Alice | 101 | 120 |
| 1 | Alice | 102 | 80 |
| 2 | Bob | 103 | 75 |

27. What is a LEFT JOIN?

**Short answer:** Returns all rows from the left table and matching rows from the right.

**Example:**

```sql
SELECT
  c.customer_id,
  c.customer_name,
  o.order_id
FROM customers c
LEFT JOIN orders o
  ON c.customer_id = o.customer_id
ORDER BY c.customer_id, o.order_id;
```

**Result:**

| customer_id | customer_name | order_id |
|---|---|---:|
| 1 | Alice | 101 |
| 1 | Alice | 102 |
| 2 | Bob | 103 |
| 3 | Carol | null |
| 4 | Dan | null |

28. Difference between LEFT JOIN and INNER JOIN

**Short answer:** LEFT keeps unmatched left-side rows; INNER removes them.

29. RIGHT JOIN

**Short answer:** Same logic as LEFT JOIN but preserves right table rows.

30. FULL OUTER JOIN

**Short answer:** Returns all matching and non-matching rows from both tables.

**Example:**

```sql
SELECT
  c.customer_id,
  c.customer_name,
  o.order_id,
  o.amount
FROM customers c
FULL OUTER JOIN orders o
  ON c.customer_id = o.customer_id
ORDER BY c.customer_id, o.order_id;
```

**Result:**

| customer_id | customer_name | order_id | amount |
|---|---|---:|---:|
| 1 | Alice | 101 | 120 |
| 1 | Alice | 102 | 80 |
| 2 | Bob | 103 | 75 |
| 3 | Carol | null | null |
| 4 | Dan | null | null |
| null | null | 104 | 60 |

**Understand it:** this keeps unmatched rows from both sides.

31. CROSS JOIN

**Short answer:** Cartesian product of both tables.

**Sample tables for cross join**

**colors**

| color |
|---|
| Red |
| Blue |

**sizes**

| size |
|---|
| S |
| M |
| L |

**Example:**

```sql
SELECT
  c.color,
  s.size
FROM colors c
CROSS JOIN sizes s
ORDER BY c.color, s.size;
```

**Result:**

| color | size |
|---|---|
| Blue | L |
| Blue | M |
| Blue | S |
| Red | L |
| Red | M |
| Red | S |

**Understand it:** every row in `colors` pairs with every row in `sizes`, so `2 x 3 = 6` rows.

32. SELF JOIN

**Short answer:** A table joined to itself, often for hierarchy or comparisons.

**Example:**

```sql
SELECT
  e.employee_name,
  m.employee_name AS manager_name
FROM employees e
LEFT JOIN employees m
  ON e.manager_id = m.employee_id;
```

**Result:**

| employee_name | manager_name |
|---|---|
| Eva | null |
| Frank | Eva |
| Grace | Eva |

**Understand it:** the same `employees` table plays two roles here:
- `e` is the employee
- `m` is that employee's manager

33. Anti join

**Short answer:** Find rows in A not in B.

```sql
SELECT c.*
FROM customers c
LEFT JOIN orders o
  ON c.customer_id = o.customer_id
WHERE o.customer_id IS NULL;
```

**Result:** `Carol` and `Dan`

34. Semi join

**Short answer:** Return rows in A that have a match in B, without bringing B columns.

```sql
SELECT
  c.customer_id,
  c.customer_name
FROM customers c
WHERE EXISTS (
  SELECT 1
  FROM orders o
  WHERE c.customer_id = o.customer_id
);
```

**Result:**

| customer_id | customer_name |
|---|---|
| 1 | Alice |
| 2 | Bob |

**Understand it:** unlike a regular join, this only answers "does a match exist?" and does not return columns from `orders`.

35. What causes duplicate rows after a join?

**Short answer:** Non-unique join keys.

**Understand it:** If one customer has 3 orders and 2 payments, a direct join may create 6 rows.

36. How do you fix join duplication?

**Short answer:** Understand cardinality, pre-aggregate, or deduplicate before joining.

37. What is join skew?

**Short answer:** Uneven key distribution causes one node or partition to process far more data.

38. Broadcast join vs shuffle join

**Short answer:** Broadcast sends small table to all workers; shuffle redistributes both sides by key.

39. When should you use a broadcast join?

**Short answer:** When one side is small enough to replicate efficiently.

40. Joining on multiple keys

**Exercise:** Write a join on (customer_id, region) and explain why using only one column is dangerous.

**Example:**

```sql
SELECT
  c.customer_id,
  c.customer_name,
  o.order_id
FROM customers c
JOIN orders o
  ON c.customer_id = o.customer_id
 AND c.region = o.region;
```

**Why this matters:** if `customer_id` is reused across regions or the business key is composite, joining on only `customer_id` can create incorrect matches.

41. Non-equi join

**Short answer:** Join condition uses <, >, BETWEEN, or other non-equality conditions.

**Mini example:** join orders to a pricing-band table using `amount BETWEEN min_amount AND max_amount`.

42. Join order and performance

**Short answer:** Filter early, shrink large tables before joining.

43. Null behavior in joins

**Short answer:** NULL = NULL is not true, so null keys do not match in standard equality joins.

44. How do you debug a bad join?

**Checklist:**
- check key uniqueness
- count rows before and after
- inspect nulls
- verify join type
- sample exploding keys

**Quick debug query:**

```sql
SELECT customer_id, COUNT(*)
FROM orders
GROUP BY customer_id
HAVING COUNT(*) > 1;
```

If this returns rows, you already know that joining `customers` to `orders` can create multiple rows per customer unless that is the intended output grain.

45–50. Practice prompts
45. Find customers with no orders
46. Find orders with missing product reference
47. Join employee with manager using self join
48. Explain many-to-many join explosion
49. Compare EXISTS vs IN
50. Reduce join cost on large datasets

---

### C. Aggregations (51–75)

51. COUNT(*) vs COUNT(column)

**Short answer:** COUNT(*) counts all rows; COUNT(column) ignores nulls.

52. What does GROUP BY do?

**Short answer:** Groups rows by one or more columns before aggregation.

53. WHERE vs HAVING

**Short answer:** WHERE filters rows before grouping; HAVING filters groups after aggregation.

54. Conditional aggregation

**Short answer:** Use SUM(CASE WHEN ... THEN 1 ELSE 0 END) or similar.

```sql
SELECT
  department,
  SUM(CASE WHEN status = 'active' THEN 1 ELSE 0 END) AS active_count
FROM employees
GROUP BY department;
```

55. DISTINCT count

**Short answer:** COUNT(DISTINCT user_id) counts unique users.

56. Approx distinct count

**Short answer:** Used for large-scale systems when exact distinct count is expensive.

57. ROLLUP

**Short answer:** Adds subtotal and grand total rows.

58. CUBE

**Short answer:** Produces all combinations of subtotals.

59. GROUPING SETS

**Short answer:** Lets you explicitly choose multiple grouping combinations.

60. Median

**Short answer:** Some engines support percentile_cont(0.5).

61. Percentile

**Short answer:** Used to understand distribution, not just average.

62. Why is AVG sometimes misleading?

**Short answer:** Outliers can distort it; median may be better.

63. Nulls in aggregations

**Short answer:** Most aggregates ignore nulls except COUNT(*).

64. Aggregation after join trap

**Short answer:** If a join duplicates rows, your sums and counts become wrong.

65–75. Practice prompts
65. Daily active users
66. Revenue by region
67. Monthly cumulative sales
68. Ratio of active to total users
69. 95th percentile latency
70. Distinct customers by month
71. Product revenue share
72. Top category by revenue
73. Countries with declining growth
74. Average order value
75. Fraud count by severity

---

### D. CTEs and Subqueries (76–100)

76. What is a CTE?

**Short answer:** A named temporary result set used inside a query.

77. Why use CTEs?

**Short answer:** Readability, reusability, step-wise logic.

78. Recursive CTE

**Short answer:** A CTE that references itself, used for hierarchies or graph-like traversal.

79. Subquery vs CTE

**Short answer:** Similar logically; CTE is often easier to read and debug.

80. Correlated subquery

**Short answer:** A subquery that depends on values from the outer query.

81. EXISTS vs IN

**Short answer:** Both check membership; EXISTS is often clearer and can be better for correlated logic.

82. Scalar subquery

**Short answer:** Returns one value, such as a max date.

83. Common CTE interview pattern

**Pattern:**
1. Filter base data
2. Rank or aggregate
3. Join results
4. Final filter

84–100. Practice prompts
84. Latest record per customer
85. Org hierarchy using recursive CTE
86. Orders above customer average
87. Users with no transactions
88. Products never sold
89. Most recent login per user
90. Monthly cohort extraction
91. Nested CTE refactor
92. CTE + window combination
93. Recursive tree traversal
94. Parent-child path building
95. Subquery to CTE rewrite
96. Use EXISTS for filtering
97. Find max salary per department
98. Find rows above department average
99. Query decomposition strategy
100. Explain readability vs performance trade-off

---

### E. Advanced SQL (101–150)

For these, use the answer pattern below.

**Short answer template:** Define it in one sentence.  
**Long answer template:** Explain use case, benefit, and one limitation.  
**Hands-on template:** Write one query or pseudo-query.
101. Deduplication strategies
102. CDC query logic
103. Snapshot vs incremental query
104. Merge / upsert pattern
105. Time travel query
106. JSON parsing in SQL
107. Arrays in SQL
108. Struct / nested fields
109. Regex filtering
110. Data masking
111. Row-level security
112. Column masking
113. Predicate pushdown
114. Partition pruning
115. File skipping
116. Explain plan basics
117. Cost-based optimization
118. Materialized views
119. Query caching
120. Resource groups
121. Query federation
122. Trino pushdown behavior
123. Snowflake clustering basics
124. BigQuery partitioning basics
125. Iceberg metadata tables
126. Delta transaction log idea
127. Null-safe comparisons
128. Merge late-arriving data
129. Validate counts after load
130. Build audit queries
131. Find broken foreign keys
132. Data quality assertions
133. Window performance concerns
134. Join performance concerns
135. Aggregation skew
136. Snapshot isolation concept
137. Query retry strategy
138. Fault-tolerant SQL pipelines
139. Access filters
140. Secure data views
141. Multi-tenant filtering
142. Dynamic SQL risks
143. SQL injection basics
144. Query observability
145. Debug wrong result sets
146. Debug row explosion
147. Debug null issues
148. Query anti-patterns
149. SQL interview best practices
150. How to talk through a SQL solution live

---

## Section 2 — Coding (151–300)

Use this answer format for all coding problems:
- brute-force idea
- optimized idea
- time complexity
- space complexity
- edge cases
- clean code

### A. Arrays and Strings (151–180)

151. Reverse a string

**Short answer:** Use slicing in Python or two pointers.

```python
def reverse_string(s: str) -> str:
    return s[::-1]
```

152. Palindrome check

```python
def is_palindrome(s: str) -> bool:
    cleaned = ''.join(ch.lower() for ch in s if ch.isalnum())
    return cleaned == cleaned[::-1]
```

153. Two Sum

**Short answer:** Use a hash map for O(n).

```python
def two_sum(nums, target):
    seen = {}
    for i, n in enumerate(nums):
        need = target - n
        if need in seen:
            return [seen[need], i]
        seen[n] = i
    return []
```

**Understand it:** store past values, not future values.

154. Three Sum

**Short answer:** Sort + two pointers.

155. Longest substring without repeating characters

**Short answer:** Sliding window + set/map.

156. Maximum subarray

**Short answer:** Kadane’s algorithm.

157. Merge sorted arrays

158. Remove duplicates from sorted array

159. Rotate array

160. Prefix sum

161. Difference array idea

162. Group anagrams

163. Valid anagram

164. String compression

165. Character frequency map

166. Substring search

167. Pattern matching

168. Sliding window max

169. Longest common prefix

170. Roman to integer

171. Integer to Roman

172. String to integer

173. Reverse words in sentence

174. Product of array except self

175. Find missing number

176. Best time to buy and sell stock

177. Majority element

178. Merge intervals

179. Insert interval

180. Meeting rooms overlap

For each one, do:
1. write brute force
2. optimize
3. code it
4. say time/space

---

### B. Stack, Queue, Linked List (181–210)

181. Valid parentheses

```python
def is_valid(s: str) -> bool:
    pairs = {')': '(', ']': '[', '}': '{'}
    stack = []
    for ch in s:
        if ch in pairs.values():
            stack.append(ch)
        else:
            if not stack or stack[-1] != pairs.get(ch):
                return False
            stack.pop()
    return not stack
```

182. Min stack

183. Queue using two stacks

184. Stack using queues

185. Reverse linked list

```python
class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next

def reverse_list(head):
    prev = None
    curr = head
    while curr:
        nxt = curr.next
        curr.next = prev
        prev = curr
        curr = nxt
    return prev
```

186. Detect cycle in linked list

187. Merge two sorted linked lists

188. Find middle of linked list

189. Remove nth node from end

190. Reorder list

191. LRU cache concept

192. Circular queue

193. Deque use cases

194. Monotonic stack

195. Next greater element

196. Daily temperatures

197. Largest rectangle in histogram

198. Trapping rain water

199. Evaluate reverse polish notation

200. Sliding window with deque

201–210. Practice repeats and variations

---

### C. Trees and Graphs (211–260)

211. Binary tree traversal

**Short answer:**
- preorder: root left right
- inorder: left root right
- postorder: left right root

212. BFS vs DFS

**Short answer:** BFS uses queue, DFS uses stack/recursion.

213. Maximum depth of binary tree

214. Invert binary tree

215. Validate BST

216. Lowest common ancestor

217. Level order traversal

218. Tree diameter

219. Path sum

220. Serialize and deserialize tree

221. Number of islands

222. Clone graph

223. Course schedule

224. Topological sort

225. Detect cycle in graph

226. Connected components

227. Shortest path in unweighted graph

228. Dijkstra basics

229. Union find basics

230. Bipartite graph check

231–260. Practice variants

**Hands-on rule:** Draw the tree or graph before coding.

---

### D. Recursion, Backtracking, DP, Search (261–300)

261. Fibonacci recursion vs DP

262. Climbing stairs

263. Coin change

264. Longest common subsequence

265. Edit distance

266. Knapsack idea

267. Subsets

268. Permutations

269. Combination sum

270. N-Queens idea

271. Binary search

272. Search in rotated sorted array

273. Find first and last position

274. Median of two sorted arrays idea

275. Kth largest element using heap

276. Top K frequent elements

277. Heap basics

278. Recursion tree thinking

279. Memoization vs tabulation

280. State definition in DP

281–300. Mixed practice

**Interview speaking tip:** Always say:
- “Let me start with brute force.”
- “Now I’ll optimize.”
- “Here’s the complexity.”
- “Let’s test edge cases.”

---

## Section 3 — Data Modeling (301–400)

### Core Answer Formula

For every modeling question answer in this order:
1. business goal
2. grain
3. dimensions
4. facts
5. keys
6. update pattern
7. quality checks
8. performance considerations

301. Fact vs dimension

**Short answer:** Facts store measurable events; dimensions store descriptive context.

302. Star schema

**Short answer:** Fact table in center with denormalized dimensions around it.

303. Snowflake schema

**Short answer:** Dimensions are normalized into multiple related tables.

304. When to use star vs snowflake

**Short answer:** Star for analytics simplicity and speed; snowflake for reduced redundancy and stricter modeling.

305. What is grain?

**Short answer:** The exact level of detail represented by one row in a fact table.

Example: one row per order line, not one row per order.

306. Surrogate key vs natural key

307. SCD Type 1

308. SCD Type 2

309. SCD Type 3

310. Factless fact table

311. Degenerate dimension

312. Junk dimension

313. Conformed dimension

314. Bridge table

315. Role-playing dimension

316. Snapshot table

317. Accumulating snapshot

318. Periodic snapshot

319. Event table

320. User growth model design

**User growth model sample answer:**
- business goal: track new, active, retained users over time
- grain: one row per user per day in activity fact or one row per signup event
- facts: login_count, active_flag, session_count
- dimensions: date, user, geography, device
- metrics: DAU, WAU, MAU, cumulative users

321–340. Practice prompts
321. Design ecommerce warehouse
322. Design payments data model
323. Design fraud event model
324. Design vulnerability scoring model
325. Design customer 360 model
326. Design support ticket analytics
327. Design employee attrition analytics
328. Design subscription analytics
329. Design IoT telemetry model
330. Design clickstream model
331. Design ad impressions model
332. Design experiment metrics model
333. Design inventory analytics
334. Design order fulfillment analytics
335. Design API usage analytics
336. Design model for late-arriving data
337. Design historical pricing model
338. Design SCD for customer address
339. Design audit trail schema
340. Design data quality monitoring model

341–360. Modeling concepts
341. Normalization
342. Denormalization
343. 3NF
344. OLTP vs OLAP
345. Partition strategy
346. Clustering strategy
347. Data skew considerations
348. Null handling strategy
349. Schema evolution
350. Data versioning
351. Data retention
352. Archival strategy
353. GDPR deletion considerations
354. Multi-tenant design
355. Hierarchical data modeling
356. Graph modeling basics
357. Semi-structured data modeling
358. JSON column trade-offs
359. Wide vs narrow tables
360. Aggregated tables

361–400. Advanced practice prompts
361. Lakehouse table design
362. Iceberg partition evolution idea
363. Hudi vs Delta vs Iceberg discussion
364. Streaming event model
365. Batch snapshot model
366. Slowly changing relationships
367. Data vault basics
368. Domain-driven modeling
369. KPI modeling
370. Metric consistency layer
371. Metadata model
372. Data catalog entity model
373. Ownership model
374. Access control model
375. Lineage model
376. Compliance model
377. Data contract model
378. Feature store model
379. Time-series partitioning
380. Daily cohort model
381. Retention cohort model
382. Marketing attribution model
383. Session fact design
384. Revenue recognition model
385. Refund adjustment model
386. Currency conversion model
387. Multi-region reporting model
388. Slowly changing facts
389. Event sourcing model
390. CQRS idea in analytics
391. Cross-domain metric reconciliation
392. Data mart integration
393. Semantic layer concept
394. Snapshot reconciliation
395. Late-arriving dimensions
396. Missing dimension handling
397. Unknown member row
398. Historical restatement handling
399. Modeling trade-offs
400. How to explain a data model in interview

---

## Section 4 — Data Architecture (401–500)

### Core Answer Formula

For architecture questions use this structure:
1. requirements
2. scale
3. latency
4. data sources
5. ingestion
6. storage
7. processing
8. serving
9. governance
10. observability
11. failure handling
12. trade-offs

401. Batch vs streaming

**Short answer:** Batch processes data in chunks; streaming processes continuously with low latency.

**Long answer:** Choose batch when cost and simplicity matter more than immediacy. Choose streaming when decisions must be made in near real time, such as fraud or threat detection.

402. Kafka basics

403. Topic, partition, offset

404. Producer vs consumer

405. Consumer group

406. Exactly-once concept

407. At-least-once vs at-most-once

408. Backpressure

409. Dead-letter queue

410. Idempotency

411. CDC pipeline design

412. API ingestion design

413. Batch pipeline design

414. Real-time alerting pipeline

415. Lambda vs Kappa

416. Spark architecture basics

417. Trino architecture basics

418. Iceberg basics

419. Delta basics

420. Lakehouse concept

421–440. Governance and scale
421. Data lake design
422. Data warehouse design
423. Metadata layer
424. OpenMetadata concept
425. Data lineage
426. Data catalog
427. Data contracts
428. RBAC
429. ABAC
430. OPA policy engine concept
431. Row-level security
432. Column-level security
433. Encryption at rest and in transit
434. Masking and tokenization
435. Audit logging
436. Monitoring and observability
437. SLA vs SLO
438. Reliability design
439. Fault tolerance
440. High availability

441–460. Platform design practice prompts
441. Design a real-time fraud platform
442. Design a vulnerability intelligence pipeline
443. Design a clickstream analytics platform
444. Design a customer 360 lakehouse
445. Design a federated query layer
446. Design a metadata-driven governance system
447. Design a CDC ingestion system
448. Design an API-to-lake ingestion system
449. Design a cost-optimized analytics platform
450. Design multi-tenant data platform
451. Design low-latency dashboard backend
452. Design event replay system
453. Design schema evolution strategy
454. Design data quality framework
455. Design lineage and audit system
456. Design cross-region data platform
457. Design hybrid cloud data stack
458. Design role-based access at query layer
459. Design row filters for sensitive data
460. Design platform for 1000+ users and 200k queries/day

461–480. Infra and operations
461. Kubernetes basics for data platform
462. Autoscaling
463. Resource isolation
464. Multi-cluster strategy
465. Deployment strategies
466. Blue-green deployment
467. Canary deployment
468. CI/CD for data platform
469. Secrets management
470. Disaster recovery
471. Backup and restore
472. Chaos testing idea
473. Performance tuning
474. Throughput vs latency
475. Caching strategies
476. Materialized views
477. Query acceleration
478. File compaction
479. Small files problem
480. Partition evolution

481–500. Executive-level trade-off questions
481. Batch vs stream trade-offs
482. Spark vs Trino trade-offs
483. Iceberg vs Delta trade-offs
484. Open table format benefits
485. Federation vs ingestion trade-offs
486. Build vs buy for metadata platform
487. Lakehouse vs warehouse trade-offs
488. Pushdown vs centralized compute
489. Governance vs developer agility
490. Cost vs latency
491. Reliability vs speed of delivery
492. Multi-tenant isolation trade-offs
493. Real-time vs simplicity
494. API integration challenges
495. Schema drift handling
496. Platform observability priorities
497. Security-first architecture
498. Migration from legacy Hadoop to modern stack
499. How to present architecture in interview
500. How to answer follow-up challenges confidently

---

## Rapid Hands-On Exercise Plan

### SQL Drills
1. Create 5 small tables in any SQL environment.
2. Practice 3 joins, 3 windows, 3 aggregations daily.
3. For every query, explain output before running it.
4. After running it, validate row count and logic.

### Coding Drills
1. Solve 10 core problems only, repeatedly.
2. Write in Python from memory.
3. For each problem say:
- brute force
- optimized
- complexity
- edge cases
4. Recode without looking.

### Modeling Drills
1. Pick one business case.
2. Define grain first.
3. Draw fact and dimensions on paper.
4. Explain late-arriving data and history.
5. Explain quality checks.

### Architecture Drills
1. State requirements first.
2. Draw ingestion, storage, compute, serving.
3. Add governance and monitoring.
4. Add failure scenarios.
5. Add trade-offs.

---

## Final 3-Day Prioritization

If you cannot finish all 500, do these first:

### Must-Win SQL
- windows
- joins
- aggregation
- CTE
- dedup
- sessionization

### Must-Win Coding
- arrays
- strings
- hashmap
- stack
- queue
- linked list
- tree traversal
- BFS/DFS
- sliding window
- binary search

### Must-Win Modeling
- grain
- fact vs dimension
- star vs snowflake
- SCD2
- snapshots
- partitioning

### Must-Win Architecture
- batch vs stream
- Kafka basics
- lakehouse
- Trino/Spark roles
- governance
- API ingestion
- CDC
- fault tolerance

---

## Interview Speaking Rules
- Start simple.
- State assumptions.
- Give brute force first for coding.
- Give trade-offs for architecture.
- Use one real example from your work whenever possible.
- Do not jump into code before explaining.
- Validate with a small test case.
- End with complexity or trade-off summary.

---

## Personal Focus For You

Because you said Java is stronger and Python is weaker:
- Think solution in Java if needed
- Speak algorithm in plain English
- Code in simple Python
- Avoid clever Python tricks
- Use readable loops, dicts, lists, sets

**Example interview line:**

> I usually think in Java-style data structures, but I’ll code this in simple Python for speed and readability.

That is completely acceptable.
