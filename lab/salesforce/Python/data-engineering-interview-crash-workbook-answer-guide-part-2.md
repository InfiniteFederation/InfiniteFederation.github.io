---
layout: default
title: Data Engineering Interview Answer Guide Part 2
permalink: /lab/salesforce/data-engineering-interview-answer-guide-part-2/
description: Part 2 of the expanded interview workbook with remaining coding answers, mock spoken answers, and a final 3-day revision checklist.
---

# Data Engineering Interview Crash Workbook: Answer Guide Part 2

This is **Part 2** of the workbook companion. It is a separate page from [Part 1](/lab/salesforce/data-engineering-interview-answer-guide/) and focuses on:

- full worked answers for the remaining coding problem list
- mock interview questions with spoken answers
- a final 3-day revision checklist

## How To Use This Page

For each coding problem:

1. say the brute-force idea first
2. say the optimized idea
3. explain the data structure choice
4. code the clean solution
5. test one happy case and one edge case

---

## Remaining Coding Answers

## Arrays And Strings

### 154. Three Sum

**Brute force:** try all triplets, `O(n^3)`.  
**Optimized idea:** sort, fix one number, then use two pointers on the rest.

```python
def three_sum(nums: list[int]) -> list[list[int]]:
    nums.sort()
    result = []
    n = len(nums)

    for i in range(n - 2):
        if i > 0 and nums[i] == nums[i - 1]:
            continue

        left, right = i + 1, n - 1
        while left < right:
            total = nums[i] + nums[left] + nums[right]
            if total == 0:
                result.append([nums[i], nums[left], nums[right]])
                left += 1
                right -= 1
                while left < right and nums[left] == nums[left - 1]:
                    left += 1
                while left < right and nums[right] == nums[right + 1]:
                    right -= 1
            elif total < 0:
                left += 1
            else:
                right -= 1

    return result
```

**Complexity:** `O(n^2)` time, `O(1)` extra space excluding output.  
**Edge cases:** duplicates, all positives, all negatives.

### 157. Merge Sorted Arrays

**Brute force:** concatenate then sort.  
**Optimized idea:** fill from the end because the first array has extra space.

```python
def merge(nums1: list[int], m: int, nums2: list[int], n: int) -> None:
    i, j, write = m - 1, n - 1, m + n - 1

    while j >= 0:
        if i >= 0 and nums1[i] > nums2[j]:
            nums1[write] = nums1[i]
            i -= 1
        else:
            nums1[write] = nums2[j]
            j -= 1
        write -= 1
```

**Complexity:** `O(m + n)` time, `O(1)` extra space.

### 158. Remove Duplicates From Sorted Array

```python
def remove_duplicates(nums: list[int]) -> int:
    if not nums:
        return 0

    write = 1
    for read in range(1, len(nums)):
        if nums[read] != nums[write - 1]:
            nums[write] = nums[read]
            write += 1

    return write
```

**Idea:** the write pointer marks the next location for a new unique value.  
**Complexity:** `O(n)` time, `O(1)` space.

### 159. Rotate Array

**Optimized idea:** reverse whole array, reverse first `k`, reverse rest.

```python
def rotate(nums: list[int], k: int) -> None:
    k %= len(nums)

    def reverse(left: int, right: int) -> None:
        while left < right:
            nums[left], nums[right] = nums[right], nums[left]
            left += 1
            right -= 1

    reverse(0, len(nums) - 1)
    reverse(0, k - 1)
    reverse(k, len(nums) - 1)
```

### 160. Prefix Sum

**Use case:** fast range-sum queries.

```python
def build_prefix(nums: list[int]) -> list[int]:
    prefix = [0]
    for num in nums:
        prefix.append(prefix[-1] + num)
    return prefix


def range_sum(prefix: list[int], left: int, right: int) -> int:
    return prefix[right + 1] - prefix[left]
```

**Interview line:** precompute once, answer many range queries in `O(1)`.

### 161. Difference Array Idea

**Use case:** many range updates, one final reconstruction.

```python
def apply_updates(length: int, updates: list[tuple[int, int, int]]) -> list[int]:
    diff = [0] * (length + 1)

    for left, right, delta in updates:
        diff[left] += delta
        if right + 1 < len(diff):
            diff[right + 1] -= delta

    result = [0] * length
    running = 0
    for i in range(length):
        running += diff[i]
        result[i] = running
    return result
```

### 162. Group Anagrams

```python
from collections import defaultdict


def group_anagrams(words: list[str]) -> list[list[str]]:
    groups = defaultdict(list)
    for word in words:
        key = "".join(sorted(word))
        groups[key].append(word)
    return list(groups.values())
```

**Complexity:** `O(n * k log k)` where `k` is word length.  
**Alternative:** use a 26-count tuple to avoid sorting.

### 163. Valid Anagram

```python
from collections import Counter


def is_anagram(s: str, t: str) -> bool:
    return Counter(s) == Counter(t)
```

**Interview note:** if only lowercase letters exist, a fixed-size frequency array is faster than a hash map.

### 164. String Compression

```python
def compress(chars: list[str]) -> int:
    write = 0
    read = 0

    while read < len(chars):
        ch = chars[read]
        start = read
        while read < len(chars) and chars[read] == ch:
            read += 1

        chars[write] = ch
        write += 1
        count = read - start
        if count > 1:
            for digit in str(count):
                chars[write] = digit
                write += 1

    return write
```

### 165. Character Frequency Map

```python
def char_frequency(s: str) -> dict[str, int]:
    freq = {}
    for ch in s:
        freq[ch] = freq.get(ch, 0) + 1
    return freq
```

**Why interviewers ask this:** it checks whether you naturally reach for a hash map.

### 166. Substring Search

**Simple interview-safe solution:**

```python
def str_str(haystack: str, needle: str) -> int:
    if needle == "":
        return 0

    for i in range(len(haystack) - len(needle) + 1):
        if haystack[i:i + len(needle)] == needle:
            return i
    return -1
```

**Complexity:** `O((n-m+1) * m)` worst case.  
**If pushed further:** mention KMP or Rabin-Karp.

### 167. Pattern Matching

For interview level, this often means mapping pattern symbols to words.

```python
def word_pattern(pattern: str, s: str) -> bool:
    words = s.split()
    if len(pattern) != len(words):
        return False

    p_to_w = {}
    w_to_p = {}

    for p, w in zip(pattern, words):
        if p in p_to_w and p_to_w[p] != w:
            return False
        if w in w_to_p and w_to_p[w] != p:
            return False
        p_to_w[p] = w
        w_to_p[w] = p

    return True
```

### 168. Sliding Window Max

**Optimized idea:** deque keeps candidate indices in decreasing value order.

```python
from collections import deque


def max_sliding_window(nums: list[int], k: int) -> list[int]:
    dq = deque()
    result = []

    for i, num in enumerate(nums):
        while dq and dq[0] <= i - k:
            dq.popleft()
        while dq and nums[dq[-1]] <= num:
            dq.pop()

        dq.append(i)
        if i >= k - 1:
            result.append(nums[dq[0]])

    return result
```

### 169. Longest Common Prefix

```python
def longest_common_prefix(strs: list[str]) -> str:
    if not strs:
        return ""

    prefix = strs[0]
    for word in strs[1:]:
        while not word.startswith(prefix):
            prefix = prefix[:-1]
            if not prefix:
                return ""
    return prefix
```

### 170. Roman To Integer

```python
def roman_to_int(s: str) -> int:
    values = {"I": 1, "V": 5, "X": 10, "L": 50, "C": 100, "D": 500, "M": 1000}
    total = 0

    for i in range(len(s)):
        if i + 1 < len(s) and values[s[i]] < values[s[i + 1]]:
            total -= values[s[i]]
        else:
            total += values[s[i]]
    return total
```

### 171. Integer To Roman

```python
def int_to_roman(num: int) -> str:
    values = [
        (1000, "M"), (900, "CM"), (500, "D"), (400, "CD"),
        (100, "C"), (90, "XC"), (50, "L"), (40, "XL"),
        (10, "X"), (9, "IX"), (5, "V"), (4, "IV"), (1, "I"),
    ]

    result = []
    for value, symbol in values:
        while num >= value:
            result.append(symbol)
            num -= value
    return "".join(result)
```

### 172. String To Integer

```python
def my_atoi(s: str) -> int:
    s = s.lstrip()
    if not s:
        return 0

    sign = 1
    i = 0
    if s[i] in "+-":
        sign = -1 if s[i] == "-" else 1
        i += 1

    result = 0
    while i < len(s) and s[i].isdigit():
        result = result * 10 + int(s[i])
        i += 1

    result *= sign
    min_int, max_int = -(2 ** 31), 2 ** 31 - 1
    if result < min_int:
        return min_int
    if result > max_int:
        return max_int
    return result
```

### 173. Reverse Words In Sentence

```python
def reverse_words(s: str) -> str:
    words = s.split()
    return " ".join(reversed(words))
```

### 175. Find Missing Number

```python
def missing_number(nums: list[int]) -> int:
    expected = len(nums) * (len(nums) + 1) // 2
    return expected - sum(nums)
```

**Alternative:** XOR.  
**Complexity:** `O(n)` time, `O(1)` space.

### 176. Best Time To Buy And Sell Stock

```python
def max_profit(prices: list[int]) -> int:
    best_buy = float("inf")
    best_profit = 0

    for price in prices:
        best_buy = min(best_buy, price)
        best_profit = max(best_profit, price - best_buy)

    return best_profit
```

### 178. Merge Intervals

```python
def merge_intervals(intervals: list[list[int]]) -> list[list[int]]:
    if not intervals:
        return []

    intervals.sort(key=lambda x: x[0])
    merged = [intervals[0]]

    for start, end in intervals[1:]:
        last = merged[-1]
        if start <= last[1]:
            last[1] = max(last[1], end)
        else:
            merged.append([start, end])

    return merged
```

### 179. Insert Interval

```python
def insert_interval(intervals: list[list[int]], new_interval: list[int]) -> list[list[int]]:
    result = []
    i = 0

    while i < len(intervals) and intervals[i][1] < new_interval[0]:
        result.append(intervals[i])
        i += 1

    while i < len(intervals) and intervals[i][0] <= new_interval[1]:
        new_interval[0] = min(new_interval[0], intervals[i][0])
        new_interval[1] = max(new_interval[1], intervals[i][1])
        i += 1

    result.append(new_interval)

    while i < len(intervals):
        result.append(intervals[i])
        i += 1

    return result
```

### 180. Meeting Rooms Overlap

```python
def can_attend_meetings(intervals: list[list[int]]) -> bool:
    intervals.sort(key=lambda x: x[0])
    for i in range(1, len(intervals)):
        if intervals[i][0] < intervals[i - 1][1]:
            return False
    return True
```

---

## Stack, Queue, Linked List

### 182. Min Stack

```python
class MinStack:
    def __init__(self):
        self.stack = []

    def push(self, val: int) -> None:
        current_min = val if not self.stack else min(val, self.stack[-1][1])
        self.stack.append((val, current_min))

    def pop(self) -> None:
        self.stack.pop()

    def top(self) -> int:
        return self.stack[-1][0]

    def get_min(self) -> int:
        return self.stack[-1][1]
```

**Key idea:** each entry stores both value and min so far.

### 183. Queue Using Two Stacks

```python
class MyQueue:
    def __init__(self):
        self.in_stack = []
        self.out_stack = []

    def push(self, x: int) -> None:
        self.in_stack.append(x)

    def _move(self) -> None:
        if not self.out_stack:
            while self.in_stack:
                self.out_stack.append(self.in_stack.pop())

    def pop(self) -> int:
        self._move()
        return self.out_stack.pop()

    def peek(self) -> int:
        self._move()
        return self.out_stack[-1]

    def empty(self) -> bool:
        return not self.in_stack and not self.out_stack
```

### 184. Stack Using Queues

```python
from collections import deque


class MyStack:
    def __init__(self):
        self.q = deque()

    def push(self, x: int) -> None:
        self.q.append(x)
        for _ in range(len(self.q) - 1):
            self.q.append(self.q.popleft())

    def pop(self) -> int:
        return self.q.popleft()

    def top(self) -> int:
        return self.q[0]

    def empty(self) -> bool:
        return not self.q
```

### 187. Merge Two Sorted Linked Lists

```python
def merge_two_lists(list1, list2):
    dummy = ListNode()
    tail = dummy

    while list1 and list2:
        if list1.val <= list2.val:
            tail.next = list1
            list1 = list1.next
        else:
            tail.next = list2
            list2 = list2.next
        tail = tail.next

    tail.next = list1 if list1 else list2
    return dummy.next
```

### 188. Find Middle Of Linked List

```python
def middle_node(head):
    slow = fast = head
    while fast and fast.next:
        slow = slow.next
        fast = fast.next.next
    return slow
```

**Why it works:** fast moves twice as fast, so slow reaches the middle when fast ends.

### 189. Remove Nth Node From End

```python
def remove_nth_from_end(head, n: int):
    dummy = ListNode(0, head)
    slow = fast = dummy

    for _ in range(n + 1):
        fast = fast.next

    while fast:
        slow = slow.next
        fast = fast.next

    slow.next = slow.next.next
    return dummy.next
```

### 190. Reorder List

**Pattern:** find middle, reverse second half, then merge alternating nodes.

```python
def reorder_list(head) -> None:
    if not head or not head.next:
        return

    slow = fast = head
    while fast and fast.next:
        slow = slow.next
        fast = fast.next.next

    prev = None
    curr = slow.next
    slow.next = None
    while curr:
        nxt = curr.next
        curr.next = prev
        prev = curr
        curr = nxt

    first, second = head, prev
    while second:
        tmp1, tmp2 = first.next, second.next
        first.next = second
        second.next = tmp1
        first, second = tmp1, tmp2
```

### 191. LRU Cache Concept

**Spoken answer:** an LRU cache needs `O(1)` get and put. That normally means a hash map for lookup plus a doubly linked list to maintain recency order. On access, move the node to the front. On capacity overflow, evict the tail.

### 192. Circular Queue

```python
class CircularQueue:
    def __init__(self, k: int):
        self.q = [0] * k
        self.head = 0
        self.count = 0
        self.k = k

    def en_queue(self, value: int) -> bool:
        if self.count == self.k:
            return False
        tail = (self.head + self.count) % self.k
        self.q[tail] = value
        self.count += 1
        return True

    def de_queue(self) -> bool:
        if self.count == 0:
            return False
        self.head = (self.head + 1) % self.k
        self.count -= 1
        return True
```

### 193. Deque Use Cases

**Spoken answer:** a deque supports insertion and deletion at both ends in `O(1)`. It is useful for sliding-window maximum, BFS, palindrome checks, task scheduling, and monotonic queue patterns.

### 194. Monotonic Stack

**Spoken answer:** a monotonic stack maintains elements in increasing or decreasing order so each new item can efficiently resolve previous items. It is ideal for next-greater-element, daily temperatures, and histogram-area problems.

### 195. Next Greater Element

```python
def next_greater(nums: list[int]) -> list[int]:
    result = [-1] * len(nums)
    stack = []

    for i, num in enumerate(nums):
        while stack and nums[stack[-1]] < num:
            idx = stack.pop()
            result[idx] = num
        stack.append(i)

    return result
```

### 196. Daily Temperatures

```python
def daily_temperatures(temperatures: list[int]) -> list[int]:
    result = [0] * len(temperatures)
    stack = []

    for i, temp in enumerate(temperatures):
        while stack and temperatures[stack[-1]] < temp:
            prev = stack.pop()
            result[prev] = i - prev
        stack.append(i)

    return result
```

### 197. Largest Rectangle In Histogram

```python
def largest_rectangle_area(heights: list[int]) -> int:
    stack = []
    best = 0

    for i, h in enumerate(heights + [0]):
        while stack and heights[stack[-1]] > h:
            height = heights[stack.pop()]
            left = stack[-1] if stack else -1
            width = i - left - 1
            best = max(best, height * width)
        stack.append(i)

    return best
```

### 198. Trapping Rain Water

```python
def trap(height: list[int]) -> int:
    left, right = 0, len(height) - 1
    left_max = right_max = 0
    water = 0

    while left < right:
        if height[left] < height[right]:
            left_max = max(left_max, height[left])
            water += left_max - height[left]
            left += 1
        else:
            right_max = max(right_max, height[right])
            water += right_max - height[right]
            right -= 1

    return water
```

### 199. Evaluate Reverse Polish Notation

```python
def eval_rpn(tokens: list[str]) -> int:
    stack = []

    for token in tokens:
        if token not in {"+", "-", "*", "/"}:
            stack.append(int(token))
            continue

        b = stack.pop()
        a = stack.pop()
        if token == "+":
            stack.append(a + b)
        elif token == "-":
            stack.append(a - b)
        elif token == "*":
            stack.append(a * b)
        else:
            stack.append(int(a / b))

    return stack[-1]
```

### 200. Sliding Window With Deque

**Spoken answer:** use a deque when the window needs both order and fast max or min lookup. Keep only useful candidate indices, remove expired ones from the front, and remove weaker candidates from the back.

---

## Trees And Graphs

### 213. Maximum Depth Of Binary Tree

```python
def max_depth(root) -> int:
    if not root:
        return 0
    return 1 + max(max_depth(root.left), max_depth(root.right))
```

### 214. Invert Binary Tree

```python
def invert_tree(root):
    if not root:
        return None
    root.left, root.right = invert_tree(root.right), invert_tree(root.left)
    return root
```

### 215. Validate BST

```python
def is_valid_bst(root) -> bool:
    def dfs(node, low, high):
        if not node:
            return True
        if not (low < node.val < high):
            return False
        return dfs(node.left, low, node.val) and dfs(node.right, node.val, high)

    return dfs(root, float("-inf"), float("inf"))
```

### 216. Lowest Common Ancestor

```python
def lowest_common_ancestor(root, p, q):
    if not root or root == p or root == q:
        return root

    left = lowest_common_ancestor(root.left, p, q)
    right = lowest_common_ancestor(root.right, p, q)

    if left and right:
        return root
    return left if left else right
```

### 217. Level Order Traversal

```python
from collections import deque


def level_order(root) -> list[list[int]]:
    if not root:
        return []

    result = []
    q = deque([root])

    while q:
        level = []
        for _ in range(len(q)):
            node = q.popleft()
            level.append(node.val)
            if node.left:
                q.append(node.left)
            if node.right:
                q.append(node.right)
        result.append(level)

    return result
```

### 218. Tree Diameter

```python
def diameter_of_binary_tree(root) -> int:
    best = 0

    def depth(node):
        nonlocal best
        if not node:
            return 0
        left = depth(node.left)
        right = depth(node.right)
        best = max(best, left + right)
        return 1 + max(left, right)

    depth(root)
    return best
```

### 219. Path Sum

```python
def has_path_sum(root, target_sum: int) -> bool:
    if not root:
        return False
    if not root.left and not root.right:
        return target_sum == root.val
    return (
        has_path_sum(root.left, target_sum - root.val)
        or has_path_sum(root.right, target_sum - root.val)
    )
```

### 220. Serialize And Deserialize Tree

```python
from collections import deque


def serialize(root) -> str:
    values = []

    def dfs(node):
        if not node:
            values.append("#")
            return
        values.append(str(node.val))
        dfs(node.left)
        dfs(node.right)

    dfs(root)
    return ",".join(values)


def deserialize(data: str):
    values = deque(data.split(","))

    def dfs():
        val = values.popleft()
        if val == "#":
            return None
        node = TreeNode(int(val))
        node.left = dfs()
        node.right = dfs()
        return node

    return dfs()
```

### 222. Clone Graph

```python
def clone_graph(node):
    if not node:
        return None

    copies = {}

    def dfs(curr):
        if curr in copies:
            return copies[curr]

        copy = Node(curr.val)
        copies[curr] = copy
        for neighbor in curr.neighbors:
            copy.neighbors.append(dfs(neighbor))
        return copy

    return dfs(node)
```

### 223. Course Schedule

```python
from collections import defaultdict, deque


def can_finish(num_courses: int, prerequisites: list[list[int]]) -> bool:
    graph = defaultdict(list)
    indegree = [0] * num_courses

    for course, prereq in prerequisites:
        graph[prereq].append(course)
        indegree[course] += 1

    q = deque([i for i in range(num_courses) if indegree[i] == 0])
    taken = 0

    while q:
        course = q.popleft()
        taken += 1
        for nxt in graph[course]:
            indegree[nxt] -= 1
            if indegree[nxt] == 0:
                q.append(nxt)

    return taken == num_courses
```

### 224. Topological Sort

**Spoken answer:** topological sort orders nodes so every prerequisite comes before dependent nodes. It works only for DAGs. Use indegree plus queue or DFS postorder.

### 225. Detect Cycle In Graph

For a directed graph, use DFS states.

```python
def has_cycle_directed(graph: dict[int, list[int]]) -> bool:
    state = {}

    def dfs(node):
        if node in state:
            return state[node] == 1

        state[node] = 1
        for neighbor in graph.get(node, []):
            if dfs(neighbor):
                return True
        state[node] = 2
        return False

    for node in graph:
        if dfs(node):
            return True
    return False
```

### 226. Connected Components

```python
def count_components(n: int, edges: list[list[int]]) -> int:
    graph = {i: [] for i in range(n)}
    for a, b in edges:
        graph[a].append(b)
        graph[b].append(a)

    seen = set()

    def dfs(node):
        if node in seen:
            return
        seen.add(node)
        for nxt in graph[node]:
            dfs(nxt)

    count = 0
    for node in range(n):
        if node not in seen:
            count += 1
            dfs(node)
    return count
```

### 227. Shortest Path In Unweighted Graph

```python
def shortest_path_unweighted(graph: dict[int, list[int]], start: int, target: int) -> int:
    q = deque([(start, 0)])
    seen = {start}

    while q:
        node, dist = q.popleft()
        if node == target:
            return dist
        for nxt in graph.get(node, []):
            if nxt not in seen:
                seen.add(nxt)
                q.append((nxt, dist + 1))
    return -1
```

**Why BFS:** each edge has equal cost.

### 228. Dijkstra Basics

**Spoken answer:** Dijkstra finds shortest paths in a graph with non-negative edge weights. Use a min-heap keyed by current best distance. When a node is popped with its smallest known distance, relax its outgoing edges.

### 229. Union Find Basics

```python
class UnionFind:
    def __init__(self, n: int):
        self.parent = list(range(n))
        self.rank = [0] * n

    def find(self, x: int) -> int:
        if self.parent[x] != x:
            self.parent[x] = self.find(self.parent[x])
        return self.parent[x]

    def union(self, a: int, b: int) -> bool:
        root_a = self.find(a)
        root_b = self.find(b)
        if root_a == root_b:
            return False

        if self.rank[root_a] < self.rank[root_b]:
            root_a, root_b = root_b, root_a
        self.parent[root_b] = root_a
        if self.rank[root_a] == self.rank[root_b]:
            self.rank[root_a] += 1
        return True
```

**Use cases:** cycle detection, connected components, MST.

### 230. Bipartite Graph Check

```python
def is_bipartite(graph: list[list[int]]) -> bool:
    color = {}

    for start in range(len(graph)):
        if start in color:
            continue

        q = deque([start])
        color[start] = 0

        while q:
            node = q.popleft()
            for neighbor in graph[node]:
                if neighbor in color:
                    if color[neighbor] == color[node]:
                        return False
                else:
                    color[neighbor] = 1 - color[node]
                    q.append(neighbor)

    return True
```

---

## Recursion, Backtracking, DP, Search

### 262. Climbing Stairs

```python
def climb_stairs(n: int) -> int:
    if n <= 2:
        return n

    a, b = 1, 2
    for _ in range(3, n + 1):
        a, b = b, a + b
    return b
```

### 264. Longest Common Subsequence

```python
def longest_common_subsequence(text1: str, text2: str) -> int:
    rows, cols = len(text1), len(text2)
    dp = [[0] * (cols + 1) for _ in range(rows + 1)]

    for i in range(rows - 1, -1, -1):
        for j in range(cols - 1, -1, -1):
            if text1[i] == text2[j]:
                dp[i][j] = 1 + dp[i + 1][j + 1]
            else:
                dp[i][j] = max(dp[i + 1][j], dp[i][j + 1])

    return dp[0][0]
```

### 265. Edit Distance

```python
def min_distance(word1: str, word2: str) -> int:
    rows, cols = len(word1), len(word2)
    dp = [[0] * (cols + 1) for _ in range(rows + 1)]

    for i in range(rows + 1):
        dp[i][cols] = rows - i
    for j in range(cols + 1):
        dp[rows][j] = cols - j

    for i in range(rows - 1, -1, -1):
        for j in range(cols - 1, -1, -1):
            if word1[i] == word2[j]:
                dp[i][j] = dp[i + 1][j + 1]
            else:
                dp[i][j] = 1 + min(
                    dp[i + 1][j],
                    dp[i][j + 1],
                    dp[i + 1][j + 1],
                )

    return dp[0][0]
```

### 266. Knapsack Idea

**Spoken answer:** in 0/1 knapsack, each item can be taken once or skipped. The DP state is typically `dp[i][capacity]`, meaning the best value using items from `i` onward with remaining capacity. The recurrence compares taking the item versus skipping it.

### 267. Subsets

```python
def subsets(nums: list[int]) -> list[list[int]]:
    result = []

    def backtrack(index: int, path: list[int]) -> None:
        if index == len(nums):
            result.append(path[:])
            return
        path.append(nums[index])
        backtrack(index + 1, path)
        path.pop()
        backtrack(index + 1, path)

    backtrack(0, [])
    return result
```

### 268. Permutations

```python
def permute(nums: list[int]) -> list[list[int]]:
    result = []

    def backtrack(start: int) -> None:
        if start == len(nums):
            result.append(nums[:])
            return
        for i in range(start, len(nums)):
            nums[start], nums[i] = nums[i], nums[start]
            backtrack(start + 1)
            nums[start], nums[i] = nums[i], nums[start]

    backtrack(0)
    return result
```

### 269. Combination Sum

```python
def combination_sum(candidates: list[int], target: int) -> list[list[int]]:
    result = []

    def backtrack(index: int, remaining: int, path: list[int]) -> None:
        if remaining == 0:
            result.append(path[:])
            return
        if remaining < 0 or index == len(candidates):
            return

        path.append(candidates[index])
        backtrack(index, remaining - candidates[index], path)
        path.pop()
        backtrack(index + 1, remaining, path)

    backtrack(0, target, [])
    return result
```

### 270. N-Queens Idea

**Spoken answer:** place queens row by row. At each row, try safe columns only. Track used columns, diagonals, and anti-diagonals in sets. Backtrack when a placement creates conflict.

### 272. Search In Rotated Sorted Array

```python
def search_rotated(nums: list[int], target: int) -> int:
    left, right = 0, len(nums) - 1

    while left <= right:
        mid = left + (right - left) // 2
        if nums[mid] == target:
            return mid

        if nums[left] <= nums[mid]:
            if nums[left] <= target < nums[mid]:
                right = mid - 1
            else:
                left = mid + 1
        else:
            if nums[mid] < target <= nums[right]:
                left = mid + 1
            else:
                right = mid - 1

    return -1
```

### 273. Find First And Last Position

```python
def search_range(nums: list[int], target: int) -> list[int]:
    def bound(find_first: bool) -> int:
        left, right = 0, len(nums) - 1
        answer = -1
        while left <= right:
            mid = left + (right - left) // 2
            if nums[mid] == target:
                answer = mid
                if find_first:
                    right = mid - 1
                else:
                    left = mid + 1
            elif nums[mid] < target:
                left = mid + 1
            else:
                right = mid - 1
        return answer

    return [bound(True), bound(False)]
```

### 274. Median Of Two Sorted Arrays Idea

**Spoken answer:** the optimal solution does a binary search on the smaller array to find a partition where left-side elements are less than or equal to right-side elements across both arrays. The median then comes from the border values. This gives `O(log(min(m, n)))`.

### 275. Kth Largest Element Using Heap

```python
import heapq


def find_kth_largest(nums: list[int], k: int) -> int:
    heap = []
    for num in nums:
        heapq.heappush(heap, num)
        if len(heap) > k:
            heapq.heappop(heap)
    return heap[0]
```

### 276. Top K Frequent Elements

```python
from collections import Counter
import heapq


def top_k_frequent(nums: list[int], k: int) -> list[int]:
    counts = Counter(nums)
    return [item for item, _ in heapq.nlargest(k, counts.items(), key=lambda x: x[1])]
```

### 277. Heap Basics

**Spoken answer:** a heap is a partial ordering where the smallest element in a min-heap is always at the root. It is ideal when you repeatedly need the smallest or largest element but do not need a fully sorted structure.

### 278. Recursion Tree Thinking

**Spoken answer:** recursion-tree thinking means visualizing how each call branches into smaller calls, how deep the tree goes, and how much repeated work happens. It helps estimate time complexity and identify when memoization is needed.

### 279. Memoization vs Tabulation

**Memoization:** top-down recursion with caching.  
**Tabulation:** bottom-up iteration.

**Spoken answer:** memoization is often easier to derive from the recurrence. Tabulation usually gives tighter control over iteration order and avoids recursion overhead.

### 280. State Definition In DP

**Spoken answer:** the most important DP step is defining what `dp[i]` or `dp[i][j]` means. If the state is ambiguous, the recurrence will be wrong. I try to define the state in plain English before writing transitions.

---

## Mock Interview Questions With Spoken Answers

These are intentionally written in a speaking style, not an essay style.

### 1. Walk Me Through How You Solve A Coding Problem In A Live Interview

**Spoken answer:**  
First I want to restate the problem and confirm the input and output assumptions. Then I usually start with the brute-force approach so we have a correct baseline. After that I look for the pattern, such as hash map, sliding window, two pointers, stack, BFS, or dynamic programming. Once I have the optimized approach, I will explain the complexity, code it cleanly, and test at least one normal case and one edge case out loud.

### 2. How Do You Decide Between BFS And DFS?

**Spoken answer:**  
I usually ask what the problem is optimizing for. If I need the shortest path in an unweighted graph or I need strict level-order traversal, BFS is the natural choice. If I need exhaustive exploration, path checks, recursion over structure, or backtracking, DFS is often simpler. I also consider memory, because BFS can hold an entire frontier at once.

### 3. Explain Why You Used A Hash Map In Two Sum

**Spoken answer:**  
The brute-force version compares every pair, which is `O(n^2)`. A hash map lets me remember what I have already seen, so for each number I can ask in constant time whether the complement already exists. That reduces the time complexity to `O(n)` with `O(n)` extra space.

### 4. How Would You Explain Dynamic Programming To A Non-Expert Interviewer?

**Spoken answer:**  
I think of dynamic programming as solving a large problem by storing answers to overlapping smaller problems so I do not recompute them. The core steps are defining the state, writing the recurrence, choosing base cases, and deciding whether top-down memoization or bottom-up tabulation is cleaner.

### 5. What Is The Most Common Mistake In SQL Interviews?

**Spoken answer:**  
The most common mistake is not being explicit about grain. People jump into joins and aggregations without saying what one output row represents. That leads to row explosion, wrong counts, and incorrect sums. I try to say the output grain first, then build the query around that.

### 6. How Do You Debug Wrong Results In A SQL Query?

**Spoken answer:**  
I reduce the problem step by step. I check row counts at each stage, verify key uniqueness before joins, inspect null behavior, and test the logic on a very small sample I can reason about by hand. If there is a join, I check whether I accidentally changed the grain. If there is an aggregation, I validate totals before and after.

### 7. How Would You Explain SCD Type 2 In An Interview?

**Spoken answer:**  
SCD Type 2 is how I preserve full history for changing dimension attributes. Instead of overwriting the row, I insert a new version with effective start and end timestamps and mark the current row. That lets analysts answer questions like what the customer segment was at the time of an order, not just what it is today.

### 8. How Do You Answer A System Design Question For A Data Platform?

**Spoken answer:**  
I start with requirements, especially scale, latency, reliability, and governance constraints. Then I walk left to right through sources, ingestion, storage, processing, and serving. After that I add observability, security, and failure handling. I always close with trade-offs, because architecture is about choosing the right compromise for the business need.

### 9. When Would You Choose Batch Over Streaming?

**Spoken answer:**  
I choose batch when the business does not need second-level latency and simplicity matters more than immediacy. It is usually cheaper, easier to operate, and easier to reason about. I choose streaming when the data has to drive near-real-time decisions such as fraud detection, alerting, or live operational workflows.

### 10. How Do You Handle A Problem When You Forget The Exact Algorithm?

**Spoken answer:**  
I do not try to hide that. I restate the brute-force approach, solve the problem correctly first, and then look for structure that improves it. Interviewers usually care more about problem-solving discipline than whether I instantly remember the optimal named algorithm.

### 11. Give Me An Example Of Explaining Trade-Offs Clearly

**Spoken answer:**  
If I compare Spark and Trino, I would say Spark is strong for heavy transformations and large compute jobs, while Trino is strong for interactive SQL and federation. Spark may be better for pipeline compute, and Trino may be better for low-latency ad hoc analytics. The right choice depends on workload pattern, concurrency, governance needs, and operational cost.

### 12. How Do You Keep Python Code Readable Under Interview Pressure?

**Spoken answer:**  
I keep it boring on purpose. I avoid clever one-liners and use simple loops, dictionaries, sets, and helper functions. I optimize for correctness and readability first, because interview code needs to be explained live, not just run.

---

## Final 3-Day Revision Checklist

## Day 1: SQL And Modeling

### Morning

- practice 3 window-function problems
- practice 3 join-debugging problems
- solve 2 aggregation problems with `GROUP BY`, `HAVING`, and conditional logic
- explain `ROW_NUMBER`, `RANK`, and `DENSE_RANK` without notes

### Afternoon

- define grain for 5 sample business questions
- explain fact vs dimension out loud
- explain star vs snowflake out loud
- explain SCD Type 1, 2, and 3 with one example each
- draw one ecommerce star schema on paper

### Evening

- solve one end-to-end SQL problem with CTE + join + window
- review row explosion, null behavior, and duplicate join keys
- rehearse a spoken answer for late-arriving data and unknown dimension members

## Day 2: Coding

### Morning

- arrays and strings:
  - Two Sum
  - Three Sum
  - Longest Substring Without Repeating Characters
  - Product of Array Except Self
  - Merge Intervals

### Afternoon

- linked list and stack:
  - Reverse Linked List
  - Detect Cycle
  - Valid Parentheses
  - Daily Temperatures
  - LRU Cache concept

### Evening

- trees and graphs:
  - Maximum Depth
  - Validate BST
  - Number of Islands
  - Course Schedule
  - BFS vs DFS explanation

### Night Review

- say this exact structure for every coding problem:
  - brute force
  - optimized idea
  - complexity
  - edge cases
  - quick test case

## Day 3: Architecture And Interview Delivery

### Morning

- explain batch vs streaming
- explain Kafka topic, partition, offset, consumer group
- explain CDC ingestion design
- explain API ingestion design
- explain Spark vs Trino vs lakehouse roles

### Afternoon

- do one whiteboard architecture drill:
  - sources
  - ingestion
  - storage
  - processing
  - serving
  - governance
  - observability
  - failure handling

### Evening

- practice 10 mock spoken answers from this page
- rehearse one project story with measurable impact
- rehearse one failure story and how you fixed it
- rehearse one trade-off discussion

## Final Night Before Interview

- do not learn new topics
- review only your must-win set
- write 5 coding patterns from memory
- write 5 SQL patterns from memory
- sleep instead of cramming

## Must-Win Set

If time is limited, review these first:

- SQL: windows, joins, deduplication, CTEs, aggregations
- coding: hash map, sliding window, stack, linked list, BFS/DFS, binary search
- modeling: grain, fact vs dimension, SCD2, snapshots
- architecture: batch vs stream, Kafka basics, CDC, lakehouse, governance

## Interview-Day Speaking Reminders

- start simple
- state assumptions
- keep the grain explicit
- do not jump into code too early
- close with complexity or trade-off summary
- if stuck, fall back to brute force and improve

## Suggested Next File After This

If you want a Part 3, the most useful next separate file would be:

- Salesforce-focused behavioral answers
- data engineering system design whiteboard templates
- 20 timed mock interview exercises with answer keys
