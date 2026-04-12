---
layout: default
title: Data Engineering Interview Answer Guide
permalink: /lab/salesforce/data-engineering-interview-answer-guide/
description: Expanded answers, examples, and visual aids for the Data Engineering Interview Crash Workbook.
---

# Data Engineering Interview Crash Workbook: Answer Guide

This page is a **separate expanded companion** to the original workbook at [Data Engineering Interview Crash Workbook](/lab/salesforce/Data%20Engineering%20Interview%20Crash%20Workbook). It turns the prompt-style notes into interview-ready answers with examples, trade-offs, and a structure that reads cleanly on GitHub Pages.

## How To Use This Guide

For each question, try to answer in this order:

1. Define the concept in one sentence.
2. Explain why it matters in production.
3. Give one example.
4. Call out one trade-off or failure mode.
5. If relevant, show how you would implement or debug it.

## Table of Contents

1. [SQL](#sql)
2. [Coding](#coding)
3. [Data Modeling](#data-modeling)
4. [Data Architecture](#data-architecture)
5. [Interview Delivery Tips](#interview-delivery-tips)

---

## SQL

### What Interviewers Want To Hear

In SQL interviews, the interviewer usually wants proof that you can:

- write correct queries
- reason about row counts
- distinguish business logic from syntax
- debug incorrect results
- explain performance implications

The strongest answer is not just a query. It is: **what the query does, why it is correct, why it is efficient enough, and what could go wrong**.

### 1. What Is A Window Function?

**Short answer:** A window function calculates a value across a related set of rows while still returning one output row for each input row.

**Detailed answer:**  
Window functions are useful when you need row-level detail plus analytical context. Unlike `GROUP BY`, they do not collapse rows. That makes them ideal for ranking, running totals, moving averages, session boundaries, and previous-versus-current comparisons.

**Example: running total per employee**

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

**How to explain it live:**

- `PARTITION BY employee_id` restarts the calculation for each employee.
- `ORDER BY sale_date` defines the sequence of rows.
- The output still keeps every sale row.

**Common mistake:** confusing window functions with grouped aggregations and accidentally returning the wrong grain.

### 2. GROUP BY vs Window Function

**Short answer:** `GROUP BY` reduces rows; window functions do not.

**Detailed answer:**  
Use `GROUP BY` when the target output is one row per group, such as one row per customer or one row per day. Use a window function when the target output must keep row-level detail but also needs grouped context.

**Example**

```sql
-- one row per employee
SELECT employee_id, SUM(amount) AS total_sales
FROM sales
GROUP BY employee_id;

-- one row per sale with employee-level context
SELECT
  employee_id,
  sale_date,
  amount,
  SUM(amount) OVER (PARTITION BY employee_id) AS total_sales
FROM sales;
```

**Interview line:**  
`GROUP BY` changes the grain. Window functions enrich the existing grain.

### 3. ROW_NUMBER vs RANK vs DENSE_RANK

**Short answer:** `ROW_NUMBER` is always unique; `RANK` leaves gaps on ties; `DENSE_RANK` does not.

**Example with salaries `100, 100, 90`:**

| Salary | ROW_NUMBER | RANK | DENSE_RANK |
|---|---:|---:|---:|
| 100 | 1 | 1 | 1 |
| 100 | 2 | 1 | 1 |
| 90 | 3 | 3 | 2 |

**When to use each:**

- `ROW_NUMBER`: deduplication or top 1 row per business key
- `RANK`: leaderboard-style ranking where ties consume positions
- `DENSE_RANK`: grouped ranking without gaps

### 4. Running Total

**Short answer:** Use `SUM(...) OVER (ORDER BY ...)`.

```sql
SELECT
  order_date,
  revenue,
  SUM(revenue) OVER (ORDER BY order_date) AS running_revenue
FROM daily_revenue;
```

**Important note:** if the data contains multiple rows per day and you want a daily running total, aggregate to day first, then apply the window.

### 5. Moving Average

**Short answer:** Use a window frame.

```sql
SELECT
  order_date,
  AVG(revenue) OVER (
    ORDER BY order_date
    ROWS BETWEEN 2 PRECEDING AND CURRENT ROW
  ) AS moving_avg_3
FROM daily_revenue;
```

**Interview trap:** `ROWS BETWEEN 2 PRECEDING` means previous two rows, not necessarily previous two calendar days.

### 6. LEAD vs LAG

**Short answer:** `LAG` looks backward; `LEAD` looks forward.

```sql
SELECT
  user_id,
  event_time,
  event_time
    - LAG(event_time) OVER (
      PARTITION BY user_id
      ORDER BY event_time
    ) AS gap_from_previous
FROM events;
```

**Use cases:**

- change detection
- gap analysis
- churn or reactivation logic
- comparing current and previous status

### 7. FIRST_VALUE and LAST_VALUE

**Short answer:** They return the first or last value in the window.

**Important detail:** `LAST_VALUE` often surprises people because the default frame may stop at the current row. To get the actual last value across the whole partition, define the frame explicitly.

```sql
SELECT
  customer_id,
  order_date,
  LAST_VALUE(order_date) OVER (
    PARTITION BY customer_id
    ORDER BY order_date
    ROWS BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING
  ) AS last_order_date
FROM orders;
```

### 8. What Is PARTITION BY?

**Short answer:** It defines logical groups inside a window function.

**Example:** If you partition by `customer_id`, each customer's rows are processed independently.

### 9. What Is ORDER BY In A Window?

**Short answer:** It defines sequence inside each partition.

Without it, ranking and running totals do not make business sense because there is no defined order.

### 10. What Is A Frame Clause?

**Short answer:** It controls which subset of ordered rows participates in the calculation.

**Examples:**

- running total: from start to current row
- moving average: from N rows before to current row
- full partition reference: from first row to last row

### 11. Deduplication Using ROW_NUMBER

**Short answer:** Partition by business key, order by freshness or quality, keep rank 1.

```sql
WITH ranked AS (
  SELECT
    *,
    ROW_NUMBER() OVER (
      PARTITION BY customer_id
      ORDER BY updated_at DESC, ingest_ts DESC
    ) AS rn
  FROM customer_records
)
SELECT *
FROM ranked
WHERE rn = 1;
```

**What to say in interview:**

- Define the business key.
- Define the winner rule.
- Explain what happens for ties.
- Explain whether you need to keep history elsewhere.

### 12. Top N Per Group

```sql
WITH ranked AS (
  SELECT
    category,
    product_id,
    revenue,
    ROW_NUMBER() OVER (
      PARTITION BY category
      ORDER BY revenue DESC
    ) AS rn
  FROM product_revenue
)
SELECT *
FROM ranked
WHERE rn <= 3;
```

**Why interviewers ask this:** it checks whether you understand partitioning and post-window filtering.

### 13. Detect Gaps In Dates

```sql
SELECT
  customer_id,
  order_date,
  LAG(order_date) OVER (
    PARTITION BY customer_id
    ORDER BY order_date
  ) AS previous_order_date
FROM orders;
```

Then compute the difference. Large gaps often indicate churn, inactivity, or missing data.

### 14. Sessionization

**Short answer:** Split a user's event stream into sessions using an inactivity threshold.

```sql
WITH tagged AS (
  SELECT
    *,
    CASE
      WHEN LAG(event_time) OVER (
        PARTITION BY user_id
        ORDER BY event_time
      ) IS NULL THEN 1
      WHEN event_time - LAG(event_time) OVER (
        PARTITION BY user_id
        ORDER BY event_time
      ) > INTERVAL '30' MINUTE THEN 1
      ELSE 0
    END AS new_session
  FROM events
),
sessionized AS (
  SELECT
    *,
    SUM(new_session) OVER (
      PARTITION BY user_id
      ORDER BY event_time
    ) AS session_id
  FROM tagged
)
SELECT *
FROM sessionized;
```

**Production considerations:**

- late-arriving events can change session boundaries
- timezone handling matters
- out-of-order events must be corrected or tolerated

### Join Questions You Should Answer Smoothly

#### INNER JOIN

Returns only matching rows from both tables.

#### LEFT JOIN

Returns every row from the left table and matched rows from the right table. If no match exists, right-side columns become `NULL`.

#### FULL OUTER JOIN

Returns matching rows plus unmatched rows from both sides.

#### SELF JOIN

Useful for hierarchical data such as employee-to-manager relationships.

```sql
SELECT
  e.employee_name,
  m.employee_name AS manager_name
FROM employees e
LEFT JOIN employees m
  ON e.manager_id = m.employee_id;
```

#### Semi Join

Use `EXISTS` when you only care whether a match exists.

```sql
SELECT *
FROM customers c
WHERE EXISTS (
  SELECT 1
  FROM orders o
  WHERE o.customer_id = c.customer_id
);
```

#### Anti Join

Use a `LEFT JOIN ... WHERE right.id IS NULL` or `NOT EXISTS` pattern to find missing relationships.

```sql
SELECT c.*
FROM customers c
WHERE NOT EXISTS (
  SELECT 1
  FROM orders o
  WHERE o.customer_id = c.customer_id
);
```

### 15. Why Joins Create Duplicate Rows

**Short answer:** because the join key is not unique on at least one side.

**Example:** one customer has 3 orders and 2 support tickets. Joining both detail tables by customer produces 6 rows.

**Best explanation:** row explosion is a cardinality problem, not a SQL syntax problem.

### 16. How To Fix Join Duplication

- check uniqueness of each join key
- pre-aggregate one side to the needed grain
- deduplicate first
- join on the full business key, not a partial key
- validate row counts before and after the join

### 17. Join Skew

**Short answer:** one or a few keys own a disproportionate amount of data, causing uneven work distribution.

**Example:** if 40 percent of events have `country = 'US'`, a distributed join may overload one worker.

**Mitigations:**

- filter early
- pre-aggregate heavy keys
- salting in large distributed systems
- choose a different join strategy

### 18. Broadcast Join vs Shuffle Join

**Broadcast join:** replicate the small table to all workers.  
**Shuffle join:** redistribute both tables by key.

Use broadcast when one side is small enough to replicate cheaply. It avoids moving the large table.

### 19. Null Behavior In Joins

**Short answer:** `NULL = NULL` is not true in standard SQL equality joins.

That means rows with null join keys usually do not match. In an interview, explicitly say you would decide whether null means "unknown" or "missing relationship."

### 20. How To Debug A Bad Join

Use this sequence:

1. Check row counts before the join.
2. Check key uniqueness on each side.
3. Check null rates.
4. Sample exploding keys.
5. Verify expected grain after the join.
6. Compare business totals before and after.

### Aggregations

#### COUNT(*) vs COUNT(column)

- `COUNT(*)` counts all rows
- `COUNT(column)` ignores nulls

#### WHERE vs HAVING

- `WHERE` filters rows before grouping
- `HAVING` filters aggregated groups after grouping

#### Conditional Aggregation

```sql
SELECT
  department,
  SUM(CASE WHEN status = 'active' THEN 1 ELSE 0 END) AS active_count,
  SUM(CASE WHEN status = 'inactive' THEN 1 ELSE 0 END) AS inactive_count
FROM employees
GROUP BY department;
```

#### DISTINCT Count

Use it for unique entities such as active users, unique customers, or unique devices.

**Interview note:** distinct counts are expensive at scale because the engine must track uniqueness.

#### Approximate Distinct Count

Use when exact precision is not necessary and scale is large. Examples include web analytics, telemetry, and observability systems.

**Trade-off:** much faster and smaller memory footprint, but not exact.

#### Median and Percentiles

These are more robust than averages when distributions are skewed.

**Example:** p95 latency tells you tail performance, while average latency can hide outliers.

#### Why AVG Can Mislead

If 99 requests take 10 ms and 1 request takes 5 seconds, the average looks acceptable while the user experience may still be bad.

#### Aggregation After Join Trap

**Wrong pattern:** join two detail tables and then sum.  
**Better pattern:** aggregate each detail table to the required grain first, then join.

### CTEs and Subqueries

#### What Is A CTE?

A CTE is a named temporary result set defined with `WITH`. It helps break a complex query into readable steps.

#### Why Use CTEs?

- readability
- easier debugging
- more explicit business logic
- reusable intermediate results

#### Recursive CTE

Useful for hierarchies such as org charts, folder trees, and dependency chains.

```sql
WITH RECURSIVE org AS (
  SELECT employee_id, manager_id, employee_name, 0 AS level
  FROM employees
  WHERE manager_id IS NULL

  UNION ALL

  SELECT e.employee_id, e.manager_id, e.employee_name, o.level + 1
  FROM employees e
  JOIN org o
    ON e.manager_id = o.employee_id
)
SELECT *
FROM org;
```

#### Correlated Subquery

A correlated subquery depends on values from the outer query.

```sql
SELECT e.*
FROM employees e
WHERE salary > (
  SELECT AVG(salary)
  FROM employees
  WHERE department = e.department
);
```

#### EXISTS vs IN

`EXISTS` is often the clearer choice when the logic is row-by-row membership checking. It also avoids some null-related confusion seen with `NOT IN`.

### Advanced SQL Topics You Should Be Ready For

#### Deduplication Strategies

Use a decision rule, not just `DISTINCT`.

- latest record wins
- highest-quality source wins
- earliest event wins
- merge fields from multiple records

Say the rule explicitly because deduplication is a business decision.

#### CDC Query Logic

CDC means capturing inserts, updates, and deletes from a source system.

**Typical SQL behavior in a lakehouse:**

- read source changes by commit timestamp or log position
- order by key and change time
- keep latest state per business key
- apply deletes carefully

#### Merge / Upsert Pattern

```sql
MERGE INTO dim_customer t
USING stage_customer s
ON t.customer_id = s.customer_id
WHEN MATCHED AND t.hash_value <> s.hash_value THEN
  UPDATE SET
    customer_name = s.customer_name,
    updated_at = s.updated_at,
    hash_value = s.hash_value
WHEN NOT MATCHED THEN
  INSERT (customer_id, customer_name, updated_at, hash_value)
  VALUES (s.customer_id, s.customer_name, s.updated_at, s.hash_value);
```

**What interviewer wants:** do you understand idempotency, late-arriving data, and change detection.

#### Predicate Pushdown and Partition Pruning

**Predicate pushdown:** push filters to the source so less data is scanned.  
**Partition pruning:** skip partitions that cannot contain relevant data.

**Example:** filtering `WHERE event_date = DATE '2026-04-01'` allows the engine to read one partition instead of a whole year.

#### Explain Plan Basics

When explaining a query plan, focus on:

- scan size
- join order
- data shuffles
- filters applied early or late
- whether the engine used partition pruning or pushdown

#### Row-Level Security and Column Masking

These are not just governance terms. They affect query design and user-visible results.

**Interview example:**  
An analyst in EMEA can see only EMEA rows, while PII columns are masked for non-privileged roles.

#### SQL Injection Basics

If asked, keep the answer simple:

- never concatenate raw user input into SQL
- use parameterized queries
- validate or whitelist dynamic identifiers where possible

### SQL Practice Prompt Map

Use this table to answer many of the workbook prompts quickly.

| Prompt Type | Primary Tool | What To Say |
|---|---|---|
| Latest record per key | `ROW_NUMBER()` | Partition by key, order by freshness, keep row 1 |
| Top N per category | `ROW_NUMBER()` or `DENSE_RANK()` | Rank inside each group, filter rank |
| Missing relationships | anti join | Left join plus null filter or `NOT EXISTS` |
| Users with activity gaps | `LAG()` | Compare current event date with previous event date |
| Rolling metric | window frame | Explain row-based vs date-based frames |
| Above average in peer group | correlated subquery or window avg | Compare row to group average |
| Broken foreign keys | anti join | Find fact keys with no dimension match |
| Load validation | counts plus sums | Check row count, duplicates, nulls, totals |

---

## Coding

### What Interviewers Want To Hear

For coding rounds, the target is usually:

1. clarify the problem
2. state brute force
3. improve to an efficient approach
4. code clearly
5. test edge cases out loud

If your Python is weaker than your Java, keep the code simple. That is better than writing clever but brittle Python.

### Arrays and Strings

#### Reverse A String

**Brute force:** build a new string from the back.  
**Optimized idea:** Python slicing is already concise and efficient enough for interview scale.

```python
def reverse_string(s: str) -> str:
    return s[::-1]
```

**Complexity:** `O(n)` time, `O(n)` space for the new string.

#### Palindrome Check

```python
def is_palindrome(s: str) -> bool:
    cleaned = "".join(ch.lower() for ch in s if ch.isalnum())
    left, right = 0, len(cleaned) - 1
    while left < right:
        if cleaned[left] != cleaned[right]:
            return False
        left += 1
        right -= 1
    return True
```

**Why this is good:** it handles spaces, punctuation, and mixed case.

#### Two Sum

**Brute force:** try all pairs, `O(n^2)`.  
**Optimized idea:** hash map of value to index.

```python
def two_sum(nums: list[int], target: int) -> list[int]:
    seen = {}
    for i, num in enumerate(nums):
        need = target - num
        if need in seen:
            return [seen[need], i]
        seen[num] = i
    return []
```

**Complexity:** `O(n)` time, `O(n)` space.

**Edge cases:**

- duplicate numbers
- negative values
- no solution

#### Longest Substring Without Repeating Characters

**Pattern:** sliding window.

```python
def length_of_longest_substring(s: str) -> int:
    last_seen = {}
    left = 0
    best = 0

    for right, ch in enumerate(s):
        if ch in last_seen and last_seen[ch] >= left:
            left = last_seen[ch] + 1
        last_seen[ch] = right
        best = max(best, right - left + 1)

    return best
```

**Complexity:** `O(n)` time, `O(min(n, charset))` space.

#### Maximum Subarray

**Pattern:** Kadane's algorithm.

```python
def max_subarray(nums: list[int]) -> int:
    current = best = nums[0]
    for num in nums[1:]:
        current = max(num, current + num)
        best = max(best, current)
    return best
```

**Interview explanation:** at each position, either start a new subarray or extend the existing one.

#### Product Of Array Except Self

**Pattern:** prefix and suffix products without division.

```python
def product_except_self(nums: list[int]) -> list[int]:
    n = len(nums)
    result = [1] * n

    prefix = 1
    for i in range(n):
        result[i] = prefix
        prefix *= nums[i]

    suffix = 1
    for i in range(n - 1, -1, -1):
        result[i] *= suffix
        suffix *= nums[i]

    return result
```

**Complexity:** `O(n)` time, `O(1)` extra space if output array is not counted.

### Stack, Queue, Linked List

#### Valid Parentheses

```python
def is_valid(s: str) -> bool:
    pairs = {")": "(", "]": "[", "}": "{"}
    stack = []

    for ch in s:
        if ch in "([{":
            stack.append(ch)
        else:
            if not stack or stack[-1] != pairs.get(ch):
                return False
            stack.pop()

    return not stack
```

**Why this works:** the latest opening bracket must match the next closing bracket, so stack order maps perfectly to the problem.

#### Reverse Linked List

```python
class ListNode:
    def __init__(self, val=0, next=None):
        self.val = val
        self.next = next


def reverse_list(head: ListNode | None) -> ListNode | None:
    prev = None
    curr = head

    while curr:
        nxt = curr.next
        curr.next = prev
        prev = curr
        curr = nxt

    return prev
```

**Interview explanation:** keep three pointers: previous, current, next.

#### Detect Cycle In Linked List

Use Floyd's tortoise and hare algorithm.

```python
def has_cycle(head: ListNode | None) -> bool:
    slow = fast = head
    while fast and fast.next:
        slow = slow.next
        fast = fast.next.next
        if slow == fast:
            return True
    return False
```

**Complexity:** `O(n)` time, `O(1)` space.

### Trees and Graphs

#### BFS vs DFS

**BFS:** level by level using a queue.  
**DFS:** go deep first using recursion or a stack.

Use BFS for shortest path in an unweighted graph or level-order traversal. Use DFS for traversal, cycle checks, or exhaustive exploration.

#### Binary Tree Traversal

```python
def inorder(root):
    if not root:
        return []
    return inorder(root.left) + [root.val] + inorder(root.right)
```

**Know the order:**

- preorder: root, left, right
- inorder: left, root, right
- postorder: left, right, root

#### Number Of Islands

**Pattern:** graph traversal on a grid.

```python
def num_islands(grid: list[list[str]]) -> int:
    if not grid:
        return 0

    rows, cols = len(grid), len(grid[0])
    count = 0

    def dfs(r: int, c: int) -> None:
        if r < 0 or r >= rows or c < 0 or c >= cols:
            return
        if grid[r][c] != "1":
            return

        grid[r][c] = "0"
        dfs(r + 1, c)
        dfs(r - 1, c)
        dfs(r, c + 1)
        dfs(r, c - 1)

    for r in range(rows):
        for c in range(cols):
            if grid[r][c] == "1":
                count += 1
                dfs(r, c)

    return count
```

**Complexity:** `O(rows * cols)` time.

#### Topological Sort / Course Schedule

If asked about course schedule, say:

- model courses as a directed graph
- indegree tells how many prerequisites remain
- process zero-indegree nodes first
- if processed node count is less than total nodes, a cycle exists

That is already a strong answer even before coding.

### Recursion, DP, and Search

#### Fibonacci Recursion vs DP

**Recursion only:** elegant but repeats work exponentially.  
**DP:** stores prior results and reduces time dramatically.

```python
def fib(n: int) -> int:
    if n <= 1:
        return n

    a, b = 0, 1
    for _ in range(2, n + 1):
        a, b = b, a + b
    return b
```

#### Coin Change

**Pattern:** dynamic programming with minimum subproblem cost.

```python
def coin_change(coins: list[int], amount: int) -> int:
    dp = [amount + 1] * (amount + 1)
    dp[0] = 0

    for current in range(1, amount + 1):
        for coin in coins:
            if coin <= current:
                dp[current] = min(dp[current], dp[current - coin] + 1)

    return dp[amount] if dp[amount] != amount + 1 else -1
```

#### Binary Search

```python
def binary_search(nums: list[int], target: int) -> int:
    left, right = 0, len(nums) - 1

    while left <= right:
        mid = left + (right - left) // 2
        if nums[mid] == target:
            return mid
        if nums[mid] < target:
            left = mid + 1
        else:
            right = mid - 1

    return -1
```

**What to say:** binary search requires a monotonic condition, usually a sorted array or a search space where one side can be eliminated.

### Coding Prompt Pattern Map

Many of the listed workbook problems reduce to these patterns:

| Pattern | Common Problems |
|---|---|
| Hash map | Two Sum, Valid Anagram, Character Frequency |
| Two pointers | Palindrome, Three Sum, Merge Sorted Arrays |
| Sliding window | Longest Substring, Minimum Window, Max Window |
| Prefix sum | Range sum, subarray totals, difference arrays |
| Stack | Valid Parentheses, Daily Temperatures, Histogram |
| Linked-list pointers | Reverse list, middle node, cycle detection |
| BFS/DFS | Number of Islands, tree traversal, connected components |
| Heap | Top K Frequent, Kth Largest, task scheduling |
| Dynamic programming | Coin Change, Climbing Stairs, LCS, Edit Distance |
| Binary search | Search insert, rotated array, first/last position |

**Strong interview line:**  
I want to classify the problem first, because that usually determines the right data structure and complexity target.

### Coding Quick Answer Bank

Use these one-line expansions for the rest of the coding workbook:

| Problem | Best Pattern | What To Say |
|---|---|---|
| Three Sum | sort + two pointers | Fix one value, solve Two Sum on the suffix, skip duplicates |
| Merge Sorted Arrays | two pointers | Compare heads and write smaller item first |
| Remove Duplicates From Sorted Array | slow/fast pointers | Keep a write pointer for the next unique value |
| Rotate Array | reverse sections | Reverse whole array, then reverse both parts |
| Group Anagrams | hash map on sorted key | Sorted characters or frequency tuple identifies each group |
| Majority Element | Boyer-Moore | Majority candidate survives pair cancellation |
| Merge Intervals | sort + sweep | Sort by start time, then merge overlaps greedily |
| Meeting Rooms Overlap | sort + compare neighbors | If current start is before previous end, there is overlap |
| Min Stack | stack of pairs | Store value plus running minimum |
| LRU Cache | hash map + doubly linked list | Need `O(1)` lookup and `O(1)` eviction |
| Daily Temperatures | monotonic stack | Keep decreasing temperatures until a warmer day appears |
| Trapping Rain Water | two pointers or stack | Water depends on left max and right max boundaries |
| Validate BST | recursion with bounds | Every node must stay inside inherited min/max limits |
| Lowest Common Ancestor | recursive split | First node where targets diverge is the answer |
| Clone Graph | DFS/BFS + map | Need visited map from original node to cloned node |
| Top K Frequent | heap or bucket sort | Count first, then extract highest frequencies |
| Longest Common Subsequence | 2D DP | `dp[i][j]` is the best answer for both prefixes |
| Edit Distance | 2D DP | Transition is insert, delete, or replace |
| Combination Sum | backtracking | Choose current number again or move forward |
| Kth Largest Element | heap or quickselect | Keep top `k` efficiently instead of fully sorting |

---

## Data Modeling

### Core Interview Formula

For a modeling question, answer in this order:

1. business goal
2. grain
3. facts
4. dimensions
5. keys
6. history strategy
7. quality checks
8. performance strategy

### 1. Fact vs Dimension

**Fact table:** stores measurable events or outcomes.  
**Dimension table:** stores descriptive context used to slice facts.

**Example:**

- `fact_orders`: order_id, customer_key, product_key, order_date_key, quantity, revenue
- `dim_customer`: customer_key, customer_name, region, segment

**Interview line:** facts answer "what happened" and dimensions answer "in what context."

### 2. Grain

**Short answer:** grain is the exact meaning of one row.

**Examples:**

- one row per order
- one row per order line
- one row per user per day
- one row per device telemetry event

If you do not define grain first, the rest of the model becomes ambiguous and metrics become unreliable.

### 3. Star Schema vs Snowflake Schema

**Star schema:** central fact table with denormalized dimensions.  
**Snowflake schema:** dimensions are normalized into additional related tables.

**When to prefer star:**

- BI-friendly design
- simpler joins
- faster analyst adoption

**When snowflake can help:**

- dimensions are large and highly reusable
- normalization reduces redundancy
- stricter master-data control is needed

### 4. Visual: Star Schema

![Star schema example](/assets/img/star-schema-diagram.svg)

### 5. Surrogate Key vs Natural Key

**Natural key:** business-generated identifier such as `customer_id` from a source system.  
**Surrogate key:** warehouse-generated identifier used for modeling and history tracking.

Use surrogate keys when:

- source keys can change
- multiple sources use conflicting key spaces
- you need slowly changing history

### 6. SCD Type 1, 2, and 3

#### Type 1

Overwrite old value with new value. No history preserved.

**Use when:** correcting errors or storing current state only.

#### Type 2

Insert a new version row for each meaningful change and track validity dates.

**Typical columns:**

- business key
- surrogate key
- effective_start_ts
- effective_end_ts
- is_current

**Use when:** historical reporting matters.

#### Type 3

Store current and previous value in the same row.

**Use when:** only limited history is needed, such as previous plan and current plan.

### 7. Degenerate, Junk, and Conformed Dimensions

#### Degenerate dimension

A business identifier stored in the fact table with no separate dimension, such as `order_number`.

#### Junk dimension

A dimension that groups low-cardinality flags together, such as `is_mobile`, `is_promo`, `is_new_user`.

#### Conformed dimension

A shared dimension used consistently across multiple fact tables, such as a common `dim_date` or `dim_customer`.

### 8. Snapshot Models

#### Periodic snapshot

One row per entity per fixed interval, such as account balance per day.

#### Accumulating snapshot

One row per workflow item that gets updated as milestones are reached, such as order lifecycle from placed to shipped to delivered.

#### Event table

One row per event. Best when you want the raw history and can derive later snapshots.

### 9. Factless Fact Table

Used to record that something happened even when there is no numeric measure.

**Example:** student attendance, store visit, policy violation, eligibility relationship.

### 10. Bridge Table

Use a bridge table for many-to-many relationships.

**Examples:**

- customer to household
- product to multiple categories
- account to multiple owners

### 11. Late-Arriving Data

If late-arriving facts reference dimensions that are not loaded yet, common strategies are:

- use an "unknown" member row first
- backfill the correct key later
- hold the record in quarantine if accuracy is critical

**Interview point:** explain whether correctness or freshness matters more.

### 12. Partitioning and Clustering

**Partitioning** improves scan efficiency by physically grouping data on a coarse key such as date.  
**Clustering** or sorting improves pruning and locality inside partitions.

**Bad partition example:** partitioning by a high-cardinality user ID.  
**Better partition example:** partition by event date, cluster by customer or region if it helps common filters.

### 13. User Growth Model Example

**Business goal:** track acquisition, activation, retention, and engagement.

**Recommended model:**

- `fact_user_activity_daily`
  - grain: one row per user per day
  - measures: session_count, event_count, active_flag, purchase_flag
- `dim_user`
  - signup_date, country, device_type, acquisition_channel
- `dim_date`
  - calendar breakdowns for daily, weekly, monthly reporting

**Derived metrics:**

- DAU, WAU, MAU
- new users
- retained users
- resurrection users
- churned users

**Quality checks:**

- no duplicate `user_id + activity_date`
- active users should be a subset of total users
- signup date should not be after activity date

### 14. Design Trade-Offs You Should Mention

| Design Choice | Benefit | Trade-Off |
|---|---|---|
| Star schema | simple analytics | more dimension redundancy |
| Snowflake | normalized dimensions | more joins, harder BI use |
| SCD2 | historical accuracy | more storage and query complexity |
| Event model | full detail | downstream aggregation required |
| Snapshot model | easy reporting | storage growth and update logic |
| Wide table | fewer joins | schema rigidity and sparse columns |

### 15. How To Explain A Model In An Interview

Use this sentence flow:

1. The business question is ___.
2. The grain is ___.
3. The main fact tables are ___.
4. The key dimensions are ___.
5. History is handled with ___.
6. Data quality is enforced through ___.
7. Performance is improved with ___.
8. The main trade-off is ___.

### Modeling Quick Answer Bank

| Topic | Strong Short Answer |
|---|---|
| Normalization | reduce redundancy and improve consistency in transactional systems |
| Denormalization | duplicate selected attributes to simplify analytics and reduce joins |
| 3NF | each attribute depends on the key, the whole key, and nothing but the key |
| OLTP vs OLAP | OLTP optimizes writes and transactions; OLAP optimizes analytical reads |
| JSON column trade-offs | flexible ingestion, but weaker governance and harder analytics if overused |
| Wide vs narrow table | wide is convenient for reads; narrow is more extensible and storage efficient |
| Aggregated table | precompute expensive metrics for faster dashboards and common BI queries |
| Data versioning | preserve historical states so changes are explainable and reproducible |
| GDPR deletion | support subject lookup, delete propagation, and downstream purge or tombstone handling |
| Unknown member row | use a default dimension row so facts are not dropped when reference data is missing |
| Historical restatement | recalculate prior periods only when business rules require corrected history |
| Semantic layer | centralize metric definitions so teams do not compute the same KPI differently |

---

## Data Architecture

### Core Interview Formula

For system or platform design questions, cover:

1. requirements
2. scale
3. latency
4. ingestion
5. storage
6. processing
7. serving
8. governance
9. observability
10. failure handling
11. trade-offs

### 1. Batch vs Streaming

**Batch:** process data in chunks on a schedule.  
**Streaming:** process data continuously with low latency.

**When to use batch:**

- lower complexity
- cost efficiency
- daily or hourly reporting is acceptable

**When to use streaming:**

- fraud detection
- operational alerting
- live dashboards
- time-sensitive personalization

**Strong answer:** I choose based on business latency requirements, not because one is universally better.

### 2. Kafka Basics

Kafka is a distributed log used for durable, ordered event streams.

**Key terms:**

- topic: named stream
- partition: ordered shard of the topic
- offset: position of a record within a partition
- producer: writes events
- consumer: reads events
- consumer group: parallel readers sharing work

**Good interview example:** clickstream events, CDC events, payment state changes.

### 3. Delivery Guarantees

#### At-most-once

Messages may be lost, but never processed more than once.

#### At-least-once

Messages are retried if needed, so duplicates are possible.

#### Exactly-once

Usually means exactly-once effect in the end-to-end pipeline, not magic delivery with no coordination. It requires idempotency, transactional guarantees, or deduplication.

### 4. Backpressure and Dead-Letter Queues

**Backpressure:** downstream system cannot keep up with upstream input rate.  
**DLQ:** isolated storage for records that repeatedly fail processing.

**What to say:** DLQs are useful, but only if someone monitors and reprocesses them. A dead-letter queue without operations is just hidden data loss.

### 5. Idempotency

An idempotent pipeline can safely reprocess the same event without changing the final outcome incorrectly.

**Examples:**

- upsert by business key
- deduplicate by event ID
- ignore older versions using event timestamps

### 6. CDC Pipeline Design

**Typical design:**

1. source database emits change logs
2. capture layer reads inserts, updates, deletes
3. events land in Kafka or object storage
4. processing layer standardizes schema and orders changes
5. sink applies merge or append logic to target tables
6. validation checks counts, lag, duplicates, and delete handling

**Failure modes to mention:**

- schema drift
- out-of-order changes
- duplicate delivery
- missing delete propagation
- replay gaps

### 7. API Ingestion Design

**Typical approach:**

- scheduler or event trigger starts ingestion
- call source API with rate-limit awareness
- persist raw responses
- normalize into structured tables
- checkpoint pagination state
- retry transient failures with backoff
- alert on persistent failures

**Trade-offs:** APIs are often slower, rate-limited, and less reliable than CDC or file-based ingestion.

### 8. Spark, Trino, and Lakehouse Roles

#### Spark

Best for large-scale transformation, batch processing, streaming jobs, ML preparation, and custom compute logic.

#### Trino

Best for interactive SQL, federated access, and fast analytical reads across many systems.

#### Lakehouse

Combines warehouse-style table management with data lake storage flexibility.

**Strong comparison:** Spark is often the compute engine for heavy transformation. Trino is often the query engine for interactive analytics and federation.

### 9. Iceberg and Delta Basics

Both are table formats that add transactional metadata to data lake storage.

**Common benefits:**

- ACID-like table operations
- schema evolution
- time travel
- partition metadata management
- easier merge and delete workflows

**What to say if asked for differences:** both solve similar problems; exact trade-offs depend on engine ecosystem, operational standards, and feature maturity in your platform.

### 10. Lakehouse Concept

A lakehouse stores data in open object storage but manages it like a queryable table system with metadata, transactions, and governance.

That allows multiple engines to share data while avoiding some classic data-lake chaos.

### 11. Visual: Platform Flow

![Data platform pipeline example](/assets/img/data-platform-pipeline.svg)

### 12. Governance

Be ready to speak about:

- metadata catalog
- lineage
- RBAC and ABAC
- row-level and column-level security
- masking and tokenization
- audit logging
- data contracts

**Strong interview line:** governance is not a separate compliance layer added at the end. It has to be built into ingestion, storage, query serving, and auditing.

### 13. Observability

For data platforms, observability should cover:

- pipeline success or failure rate
- freshness lag
- row counts
- null spikes
- schema changes
- query latency
- resource usage
- access audit trails

**Good answer:** I want both platform health and data quality visibility, because pipelines can be technically healthy while still producing wrong data.

### 14. Reliability and Fault Tolerance

**Key ideas to mention:**

- retries with backoff for transient failures
- idempotent writes
- checkpointing
- replay support
- multi-AZ or multi-node high availability
- backup and restore procedures
- disaster recovery objectives

### 15. Architecture Trade-Offs

| Choice | Benefit | Cost |
|---|---|---|
| Batch | simple and cheaper | higher latency |
| Streaming | low latency | more operational complexity |
| Federation | fresh access to source systems | source dependency and governance complexity |
| Central ingestion | predictable curated datasets | latency and duplication |
| Open table formats | interoperability | operational maturity required |
| Multi-tenant platform | cost efficiency | stronger isolation and governance required |

### 16. Example: How To Answer A Real-Time Fraud Platform Question

**Requirements:** detect suspicious transactions within seconds, support analyst review, maintain auditability.  
**Ingestion:** transaction events stream through Kafka.  
**Processing:** stream processor enriches events with customer and device context.  
**Storage:** hot alerts in operational store, full event history in lakehouse.  
**Serving:** alert API plus analyst dashboard.  
**Governance:** PII masking, role-based access, lineage for decisions.  
**Observability:** end-to-end lag, false positive rate, dropped events, alert throughput.  
**Failure handling:** replay from Kafka, idempotent alert writes, DLQ for malformed events.  
**Trade-off:** lower latency increases complexity, so I would reserve the streaming path for decisions that truly need it.

### 17. How To Present Architecture In An Interview

Talk from left to right:

1. sources
2. ingestion
3. storage
4. processing
5. serving
6. governance and monitoring across the whole flow

That keeps the answer structured and easier to follow.

### Architecture Quick Answer Bank

| Topic | Strong Short Answer |
|---|---|
| Topic, partition, offset | topic is the stream, partition is an ordered shard, offset is record position |
| Producer vs consumer | producer writes events, consumer reads and processes them |
| Consumer group | lets multiple consumers split partitions for parallel processing |
| Lambda vs Kappa | Lambda separates batch and speed layers; Kappa keeps a single streaming path |
| Data lake vs warehouse | lake is storage-first and flexible; warehouse is modeled and query-optimized |
| Metadata layer | stores technical and business context about datasets, ownership, lineage, and policies |
| Data contract | formal agreement on schema, semantics, quality, and change expectations |
| RBAC vs ABAC | RBAC uses roles; ABAC evaluates attributes such as team, region, sensitivity, and purpose |
| Encryption at rest vs in transit | protect stored data on disk and protect network traffic between systems |
| SLA vs SLO | SLA is the external commitment; SLO is the internal reliability target |
| Materialized view | precomputed query result for faster reads, with refresh trade-offs |
| Small files problem | too many tiny files increase metadata and planning overhead in lakehouse systems |
| Partition evolution | change partition strategy over time without rewriting all historical data when format allows it |
| Federation vs ingestion | federation favors freshness and lower duplication; ingestion favors curated performance and control |
| Cost vs latency | lower latency usually requires more always-on infrastructure and operational complexity |

---

## Interview Delivery Tips

### A Good SQL Answer Sounds Like This

I will first define the output grain. Then I will choose the correct join and aggregation strategy. After that I will validate row counts and explain any performance considerations such as partition pruning or join explosion.

### A Good Coding Answer Sounds Like This

I will start with the brute-force approach so we have a correct baseline. Then I will optimize it using the right data structure, state the complexity, and test the edge cases before finalizing the code.

### A Good Modeling Answer Sounds Like This

I will start from the business question and define the grain first. Then I will identify facts, dimensions, history strategy, and the quality checks needed to keep the model trustworthy.

### A Good Architecture Answer Sounds Like This

I will clarify requirements first, especially scale and latency. Then I will walk through ingestion, storage, processing, serving, governance, and failure handling, and I will close with the main trade-offs.

### Final Advice

- start simple
- state assumptions
- keep the grain explicit
- validate row counts in SQL
- give brute force first in coding
- name the trade-off in architecture
- use one real example from your work when possible

If you want, the next logical step is to create **part 2** as a separate file with:

- full worked answers for the remaining coding problem list
- mock interview questions with spoken answers
- a final 3-day revision checklist
