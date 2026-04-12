---
layout: default
title: Data Engineering Interview Answer Guide Java Edition
permalink: /lab/salesforce/data-engineering-interview-answer-guide-java/
description: Java-oriented companion to the Data Engineering Interview Answer Guide with Java code examples for coding sections.
---

# Data Engineering Interview Crash Workbook: Answer Guide Java Edition

This is a **separate Java-oriented companion** to [Part 1](/lab/salesforce/data-engineering-interview-answer-guide/). It keeps the same interview purpose, but rewrites the coding examples in Java so you can practice in the language you are strongest in.

The SQL, data modeling, and architecture sections are language-neutral. The main change in this Java edition is the coding section and the way you should explain data structures during interviews.

## How To Use This Java Edition

For each coding problem:

1. explain the brute-force solution
2. explain the optimized solution
3. say which Java data structure you want to use
4. write the Java code cleanly
5. test one normal case and one edge case

---

## SQL

The SQL section from [Part 1](/lab/salesforce/data-engineering-interview-answer-guide/#sql) does not change for Java. SQL, data modeling, and architecture answers are the same regardless of whether you code in Java or Python.

Use the same interview structure:

- define the output grain
- explain join logic
- explain aggregation or window logic
- validate row counts
- mention performance concerns

---

## Coding In Java

### What Interviewers Want To Hear

If you prefer Java, say that clearly and then code in Java confidently. A strong answer sounds like this:

I’ll start with the brute-force approach so we have a correct baseline. Then I’ll optimize it using the right data structure. I’ll code it in Java since that’s the language I’m most comfortable with for clean implementation and complexity analysis.

### Java Collections Cheat Sheet

| Pattern | Java Type |
|---|---|
| hash map | `HashMap<K, V>` |
| set | `HashSet<T>` |
| queue | `ArrayDeque<T>` or `LinkedList<T>` |
| stack-like usage | `ArrayDeque<T>` |
| heap | `PriorityQueue<T>` |
| dynamic array | `ArrayList<T>` |
| double-ended queue | `ArrayDeque<T>` |

### 1. Reverse A String

```java
class Solution {
    public String reverseString(String s) {
        return new StringBuilder(s).reverse().toString();
    }
}
```

**Complexity:** `O(n)` time, `O(n)` space.

### 2. Palindrome Check

```java
class Solution {
    public boolean isPalindrome(String s) {
        int left = 0;
        int right = s.length() - 1;

        while (left < right) {
            while (left < right && !Character.isLetterOrDigit(s.charAt(left))) {
                left++;
            }
            while (left < right && !Character.isLetterOrDigit(s.charAt(right))) {
                right--;
            }

            if (Character.toLowerCase(s.charAt(left)) != Character.toLowerCase(s.charAt(right))) {
                return false;
            }

            left++;
            right--;
        }

        return true;
    }
}
```

### 3. Two Sum

```java
import java.util.HashMap;
import java.util.Map;

class Solution {
    public int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> seen = new HashMap<>();

        for (int i = 0; i < nums.length; i++) {
            int need = target - nums[i];
            if (seen.containsKey(need)) {
                return new int[] {seen.get(need), i};
            }
            seen.put(nums[i], i);
        }

        return new int[] {};
    }
}
```

**Complexity:** `O(n)` time, `O(n)` space.

### 4. Longest Substring Without Repeating Characters

```java
import java.util.HashMap;
import java.util.Map;

class Solution {
    public int lengthOfLongestSubstring(String s) {
        Map<Character, Integer> lastSeen = new HashMap<>();
        int left = 0;
        int best = 0;

        for (int right = 0; right < s.length(); right++) {
            char ch = s.charAt(right);
            if (lastSeen.containsKey(ch) && lastSeen.get(ch) >= left) {
                left = lastSeen.get(ch) + 1;
            }
            lastSeen.put(ch, right);
            best = Math.max(best, right - left + 1);
        }

        return best;
    }
}
```

### 5. Maximum Subarray

```java
class Solution {
    public int maxSubArray(int[] nums) {
        int current = nums[0];
        int best = nums[0];

        for (int i = 1; i < nums.length; i++) {
            current = Math.max(nums[i], current + nums[i]);
            best = Math.max(best, current);
        }

        return best;
    }
}
```

### 6. Product Of Array Except Self

```java
class Solution {
    public int[] productExceptSelf(int[] nums) {
        int n = nums.length;
        int[] result = new int[n];

        int prefix = 1;
        for (int i = 0; i < n; i++) {
            result[i] = prefix;
            prefix *= nums[i];
        }

        int suffix = 1;
        for (int i = n - 1; i >= 0; i--) {
            result[i] *= suffix;
            suffix *= nums[i];
        }

        return result;
    }
}
```

### 7. Valid Parentheses

```java
import java.util.ArrayDeque;
import java.util.Deque;

class Solution {
    public boolean isValid(String s) {
        Deque<Character> stack = new ArrayDeque<>();

        for (char ch : s.toCharArray()) {
            if (ch == '(' || ch == '[' || ch == '{') {
                stack.push(ch);
            } else {
                if (stack.isEmpty()) {
                    return false;
                }
                char top = stack.pop();
                if ((ch == ')' && top != '(') ||
                    (ch == ']' && top != '[') ||
                    (ch == '}' && top != '{')) {
                    return false;
                }
            }
        }

        return stack.isEmpty();
    }
}
```

### 8. Reverse Linked List

```java
class ListNode {
    int val;
    ListNode next;

    ListNode() {}

    ListNode(int val) {
        this.val = val;
    }

    ListNode(int val, ListNode next) {
        this.val = val;
        this.next = next;
    }
}

class Solution {
    public ListNode reverseList(ListNode head) {
        ListNode prev = null;
        ListNode curr = head;

        while (curr != null) {
            ListNode next = curr.next;
            curr.next = prev;
            prev = curr;
            curr = next;
        }

        return prev;
    }
}
```

### 9. Detect Cycle In Linked List

```java
class Solution {
    public boolean hasCycle(ListNode head) {
        ListNode slow = head;
        ListNode fast = head;

        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
            if (slow == fast) {
                return true;
            }
        }

        return false;
    }
}
```

### 10. Binary Tree Traversal

```java
import java.util.ArrayList;
import java.util.List;

class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode(int val) {
        this.val = val;
    }
}

class Solution {
    public List<Integer> inorderTraversal(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        dfs(root, result);
        return result;
    }

    private void dfs(TreeNode node, List<Integer> result) {
        if (node == null) {
            return;
        }
        dfs(node.left, result);
        result.add(node.val);
        dfs(node.right, result);
    }
}
```

### 11. Number Of Islands

```java
class Solution {
    public int numIslands(char[][] grid) {
        int rows = grid.length;
        int cols = grid[0].length;
        int count = 0;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] == '1') {
                    count++;
                    dfs(grid, r, c);
                }
            }
        }

        return count;
    }

    private void dfs(char[][] grid, int r, int c) {
        if (r < 0 || r >= grid.length || c < 0 || c >= grid[0].length || grid[r][c] != '1') {
            return;
        }

        grid[r][c] = '0';
        dfs(grid, r + 1, c);
        dfs(grid, r - 1, c);
        dfs(grid, r, c + 1);
        dfs(grid, r, c - 1);
    }
}
```

### 12. Fibonacci

```java
class Solution {
    public int fib(int n) {
        if (n <= 1) {
            return n;
        }

        int a = 0;
        int b = 1;
        for (int i = 2; i <= n; i++) {
            int next = a + b;
            a = b;
            b = next;
        }

        return b;
    }
}
```

### 13. Coin Change

```java
import java.util.Arrays;

class Solution {
    public int coinChange(int[] coins, int amount) {
        int[] dp = new int[amount + 1];
        Arrays.fill(dp, amount + 1);
        dp[0] = 0;

        for (int current = 1; current <= amount; current++) {
            for (int coin : coins) {
                if (coin <= current) {
                    dp[current] = Math.min(dp[current], dp[current - coin] + 1);
                }
            }
        }

        return dp[amount] == amount + 1 ? -1 : dp[amount];
    }
}
```

### 14. Binary Search

```java
class Solution {
    public int search(int[] nums, int target) {
        int left = 0;
        int right = nums.length - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] == target) {
                return mid;
            }
            if (nums[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return -1;
    }
}
```

### Coding Prompt Pattern Map In Java

| Pattern | Typical Java Types | Example Problems |
|---|---|---|
| hash map | `HashMap` | Two Sum, Valid Anagram |
| sliding window | `HashMap`, `HashSet` | Longest Substring |
| stack | `ArrayDeque` | Valid Parentheses, Daily Temperatures |
| queue / BFS | `ArrayDeque` | Number of Islands, Level Order |
| heap | `PriorityQueue` | Top K Frequent, Kth Largest |
| dynamic programming | arrays, `List` | Coin Change, Climbing Stairs |
| graph traversal | `Map<Integer, List<Integer>>`, `boolean[]` | Course Schedule |

### Interview Delivery Tip For Java

Because you are stronger in Java, your best approach is:

- keep the code explicit
- use standard library types the interviewer will immediately recognize
- avoid overengineering with too many helper classes
- state complexity clearly
- test with a small manual example

### Core Input / Output Samples

Use these when you practice speaking. Say the input, the expected output, and one sentence for why.

| Problem | Input | Output | Why |
|---|---|---|---|
| Reverse String | `"salesforce"` | `"ecrofselas"` | reverses all characters |
| Palindrome Check | `"A man, a plan, a canal: Panama"` | `true` | ignores punctuation and case |
| Two Sum | `nums = [2, 7, 11, 15], target = 9` | `[0, 1]` | `2 + 7 = 9` |
| Longest Substring | `"abcabcbb"` | `3` | longest unique substring is `"abc"` |
| Maximum Subarray | `[-2,1,-3,4,-1,2,1,-5,4]` | `6` | best subarray is `[4,-1,2,1]` |
| Product Except Self | `[1,2,3,4]` | `[24,12,8,6]` | multiply all values except current index |
| Valid Parentheses | `"({[]})"` | `true` | every opener closes in correct order |
| Reverse Linked List | `1 -> 2 -> 3 -> null` | `3 -> 2 -> 1 -> null` | links are reversed |
| Detect Cycle | `3 -> 2 -> 0 -> -4`, tail points to node `2` | `true` | fast and slow pointers meet |
| Inorder Traversal | root `2`, left `1`, right `3` | `[1,2,3]` | inorder for BST is sorted |
| Number Of Islands | `[['1','1','0'],['1','0','0'],['0','1','1']]` | `2` | there are two disconnected land groups |
| Fibonacci | `n = 7` | `13` | iterative DP avoids repeated work |
| Coin Change | `coins = [1,2,5], amount = 11` | `3` | `5 + 5 + 1` |
| Binary Search | `nums = [1,3,5,7,9], target = 7` | `3` | target is at index `3` |

### Additional Java Practice Examples

These are extra examples you can use immediately after finishing the main sample.

| Problem | Extra Input | Extra Output |
|---|---|---|
| Two Sum | `nums = [3,2,4], target = 6` | `[1,2]` |
| Longest Substring | `"bbbbb"` | `1` |
| Maximum Subarray | `[5,4,-1,7,8]` | `23` |
| Product Except Self | `[-1,1,0,-3,3]` | `[0,0,9,0,0]` |
| Valid Parentheses | `"(]"` | `false` |
| Number Of Islands | `[['1','1','1'],['0','1','0'],['1','1','1']]` | `1` |
| Coin Change | `coins = [2], amount = 3` | `-1` |
| Binary Search | `nums = [2,4,6,8], target = 5` | `-1` |

---

## Data Modeling

Use the same modeling section from [Part 1](/lab/salesforce/data-engineering-interview-answer-guide/#data-modeling). Modeling answers are unchanged by programming language.

The strongest modeling answer still follows:

1. business goal
2. grain
3. facts
4. dimensions
5. keys
6. history strategy
7. quality checks
8. performance strategy

---

## Data Architecture

Use the same architecture section from [Part 1](/lab/salesforce/data-engineering-interview-answer-guide/#data-architecture). Architecture answers are also language-neutral.

The strongest system-design answer still covers:

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

---

## Final Advice

If you code in Java during the interview, say so early and make it a strength:

I’m most fluent in Java, so I’ll implement in Java to keep the solution clean and precise.

That is a stronger choice than forcing Python if Java is where you naturally think about data structures and control flow.
