---
layout: default
title: Data Engineering Interview Answer Guide Part 2 Java Edition
permalink: /lab/salesforce/data-engineering-interview-answer-guide-part-2-java/
description: Java-oriented Part 2 with worked answers for the remaining coding problems, plus Java-specific interview guidance.
---

# Data Engineering Interview Crash Workbook: Answer Guide Part 2 Java Edition

This is the **Java version of Part 2**. It mirrors the same coding topics as [Part 2](/lab/salesforce/data-engineering-interview-answer-guide-part-2/), but the worked solutions are written in Java.

## How To Practice

For each problem:

1. say the brute-force solution
2. state the optimized pattern
3. name the Java data structure
4. code it in Java
5. test one normal case and one edge case

---

## Remaining Coding Answers In Java

## Arrays And Strings

### 154. Three Sum

```java
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Solution {
    public List<List<Integer>> threeSum(int[] nums) {
        Arrays.sort(nums);
        List<List<Integer>> result = new ArrayList<>();

        for (int i = 0; i < nums.length - 2; i++) {
            if (i > 0 && nums[i] == nums[i - 1]) {
                continue;
            }

            int left = i + 1;
            int right = nums.length - 1;

            while (left < right) {
                int total = nums[i] + nums[left] + nums[right];
                if (total == 0) {
                    result.add(Arrays.asList(nums[i], nums[left], nums[right]));
                    left++;
                    right--;
                    while (left < right && nums[left] == nums[left - 1]) {
                        left++;
                    }
                    while (left < right && nums[right] == nums[right + 1]) {
                        right--;
                    }
                } else if (total < 0) {
                    left++;
                } else {
                    right--;
                }
            }
        }

        return result;
    }
}
```

### 157. Merge Sorted Arrays

```java
class Solution {
    public void merge(int[] nums1, int m, int[] nums2, int n) {
        int i = m - 1;
        int j = n - 1;
        int write = m + n - 1;

        while (j >= 0) {
            if (i >= 0 && nums1[i] > nums2[j]) {
                nums1[write--] = nums1[i--];
            } else {
                nums1[write--] = nums2[j--];
            }
        }
    }
}
```

### 158. Remove Duplicates From Sorted Array

```java
class Solution {
    public int removeDuplicates(int[] nums) {
        if (nums.length == 0) {
            return 0;
        }

        int write = 1;
        for (int read = 1; read < nums.length; read++) {
            if (nums[read] != nums[write - 1]) {
                nums[write++] = nums[read];
            }
        }

        return write;
    }
}
```

### 159. Rotate Array

```java
class Solution {
    public void rotate(int[] nums, int k) {
        k %= nums.length;
        reverse(nums, 0, nums.length - 1);
        reverse(nums, 0, k - 1);
        reverse(nums, k, nums.length - 1);
    }

    private void reverse(int[] nums, int left, int right) {
        while (left < right) {
            int temp = nums[left];
            nums[left] = nums[right];
            nums[right] = temp;
            left++;
            right--;
        }
    }
}
```

### 160. Prefix Sum

```java
class PrefixSum {
    private final int[] prefix;

    public PrefixSum(int[] nums) {
        prefix = new int[nums.length + 1];
        for (int i = 0; i < nums.length; i++) {
            prefix[i + 1] = prefix[i] + nums[i];
        }
    }

    public int rangeSum(int left, int right) {
        return prefix[right + 1] - prefix[left];
    }
}
```

### 161. Difference Array Idea

```java
class Solution {
    public int[] applyUpdates(int length, int[][] updates) {
        int[] diff = new int[length + 1];

        for (int[] update : updates) {
            int left = update[0];
            int right = update[1];
            int delta = update[2];
            diff[left] += delta;
            if (right + 1 < diff.length) {
                diff[right + 1] -= delta;
            }
        }

        int[] result = new int[length];
        int running = 0;
        for (int i = 0; i < length; i++) {
            running += diff[i];
            result[i] = running;
        }

        return result;
    }
}
```

### 162. Group Anagrams

```java
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Solution {
    public List<List<String>> groupAnagrams(String[] strs) {
        Map<String, List<String>> groups = new HashMap<>();

        for (String word : strs) {
            char[] chars = word.toCharArray();
            Arrays.sort(chars);
            String key = new String(chars);
            groups.computeIfAbsent(key, k -> new ArrayList<>()).add(word);
        }

        return new ArrayList<>(groups.values());
    }
}
```

### 163. Valid Anagram

```java
class Solution {
    public boolean isAnagram(String s, String t) {
        if (s.length() != t.length()) {
            return false;
        }

        int[] count = new int[26];
        for (char ch : s.toCharArray()) {
            count[ch - 'a']++;
        }
        for (char ch : t.toCharArray()) {
            count[ch - 'a']--;
        }
        for (int value : count) {
            if (value != 0) {
                return false;
            }
        }
        return true;
    }
}
```

### 164. String Compression

```java
class Solution {
    public int compress(char[] chars) {
        int write = 0;
        int read = 0;

        while (read < chars.length) {
            char current = chars[read];
            int start = read;

            while (read < chars.length && chars[read] == current) {
                read++;
            }

            chars[write++] = current;
            int count = read - start;
            if (count > 1) {
                for (char digit : String.valueOf(count).toCharArray()) {
                    chars[write++] = digit;
                }
            }
        }

        return write;
    }
}
```

### 165. Character Frequency Map

```java
import java.util.HashMap;
import java.util.Map;

class Solution {
    public Map<Character, Integer> frequency(String s) {
        Map<Character, Integer> freq = new HashMap<>();
        for (char ch : s.toCharArray()) {
            freq.put(ch, freq.getOrDefault(ch, 0) + 1);
        }
        return freq;
    }
}
```

### 166. Substring Search

```java
class Solution {
    public int strStr(String haystack, String needle) {
        if (needle.isEmpty()) {
            return 0;
        }

        for (int i = 0; i <= haystack.length() - needle.length(); i++) {
            if (haystack.substring(i, i + needle.length()).equals(needle)) {
                return i;
            }
        }

        return -1;
    }
}
```

### 167. Pattern Matching

```java
import java.util.HashMap;
import java.util.Map;

class Solution {
    public boolean wordPattern(String pattern, String s) {
        String[] words = s.split(" ");
        if (pattern.length() != words.length) {
            return false;
        }

        Map<Character, String> pToW = new HashMap<>();
        Map<String, Character> wToP = new HashMap<>();

        for (int i = 0; i < pattern.length(); i++) {
            char p = pattern.charAt(i);
            String w = words[i];

            if (pToW.containsKey(p) && !pToW.get(p).equals(w)) {
                return false;
            }
            if (wToP.containsKey(w) && wToP.get(w) != p) {
                return false;
            }

            pToW.put(p, w);
            wToP.put(w, p);
        }

        return true;
    }
}
```

### 168. Sliding Window Max

```java
import java.util.ArrayDeque;
import java.util.Deque;

class Solution {
    public int[] maxSlidingWindow(int[] nums, int k) {
        Deque<Integer> dq = new ArrayDeque<>();
        int[] result = new int[nums.length - k + 1];
        int index = 0;

        for (int i = 0; i < nums.length; i++) {
            while (!dq.isEmpty() && dq.peekFirst() <= i - k) {
                dq.pollFirst();
            }
            while (!dq.isEmpty() && nums[dq.peekLast()] <= nums[i]) {
                dq.pollLast();
            }
            dq.offerLast(i);

            if (i >= k - 1) {
                result[index++] = nums[dq.peekFirst()];
            }
        }

        return result;
    }
}
```

### 169. Longest Common Prefix

```java
class Solution {
    public String longestCommonPrefix(String[] strs) {
        if (strs.length == 0) {
            return "";
        }

        String prefix = strs[0];
        for (int i = 1; i < strs.length; i++) {
            while (!strs[i].startsWith(prefix)) {
                prefix = prefix.substring(0, prefix.length() - 1);
                if (prefix.isEmpty()) {
                    return "";
                }
            }
        }

        return prefix;
    }
}
```

### 170. Roman To Integer

```java
import java.util.HashMap;
import java.util.Map;

class Solution {
    public int romanToInt(String s) {
        Map<Character, Integer> values = new HashMap<>();
        values.put('I', 1);
        values.put('V', 5);
        values.put('X', 10);
        values.put('L', 50);
        values.put('C', 100);
        values.put('D', 500);
        values.put('M', 1000);

        int total = 0;
        for (int i = 0; i < s.length(); i++) {
            int current = values.get(s.charAt(i));
            if (i + 1 < s.length() && current < values.get(s.charAt(i + 1))) {
                total -= current;
            } else {
                total += current;
            }
        }
        return total;
    }
}
```

### 171. Integer To Roman

```java
class Solution {
    public String intToRoman(int num) {
        int[] values = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
        String[] symbols = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            while (num >= values[i]) {
                result.append(symbols[i]);
                num -= values[i];
            }
        }

        return result.toString();
    }
}
```

### 172. String To Integer

```java
class Solution {
    public int myAtoi(String s) {
        int i = 0;
        int n = s.length();

        while (i < n && s.charAt(i) == ' ') {
            i++;
        }
        if (i == n) {
            return 0;
        }

        int sign = 1;
        if (s.charAt(i) == '+' || s.charAt(i) == '-') {
            sign = s.charAt(i) == '-' ? -1 : 1;
            i++;
        }

        long result = 0;
        while (i < n && Character.isDigit(s.charAt(i))) {
            result = result * 10 + (s.charAt(i) - '0');
            if (sign * result > Integer.MAX_VALUE) {
                return Integer.MAX_VALUE;
            }
            if (sign * result < Integer.MIN_VALUE) {
                return Integer.MIN_VALUE;
            }
            i++;
        }

        return (int) (sign * result);
    }
}
```

### 173. Reverse Words In Sentence

```java
class Solution {
    public String reverseWords(String s) {
        String[] words = s.trim().split("\\s+");
        StringBuilder result = new StringBuilder();

        for (int i = words.length - 1; i >= 0; i--) {
            result.append(words[i]);
            if (i > 0) {
                result.append(" ");
            }
        }

        return result.toString();
    }
}
```

### 175. Find Missing Number

```java
class Solution {
    public int missingNumber(int[] nums) {
        int expected = nums.length * (nums.length + 1) / 2;
        int actual = 0;

        for (int num : nums) {
            actual += num;
        }

        return expected - actual;
    }
}
```

### 176. Best Time To Buy And Sell Stock

```java
class Solution {
    public int maxProfit(int[] prices) {
        int minPrice = Integer.MAX_VALUE;
        int bestProfit = 0;

        for (int price : prices) {
            minPrice = Math.min(minPrice, price);
            bestProfit = Math.max(bestProfit, price - minPrice);
        }

        return bestProfit;
    }
}
```

### 178. Merge Intervals

```java
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Solution {
    public int[][] merge(int[][] intervals) {
        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));
        List<int[]> merged = new ArrayList<>();

        for (int[] interval : intervals) {
            if (merged.isEmpty() || merged.get(merged.size() - 1)[1] < interval[0]) {
                merged.add(interval);
            } else {
                merged.get(merged.size() - 1)[1] =
                    Math.max(merged.get(merged.size() - 1)[1], interval[1]);
            }
        }

        return merged.toArray(new int[merged.size()][]);
    }
}
```

### 179. Insert Interval

```java
import java.util.ArrayList;
import java.util.List;

class Solution {
    public int[][] insert(int[][] intervals, int[] newInterval) {
        List<int[]> result = new ArrayList<>();
        int i = 0;

        while (i < intervals.length && intervals[i][1] < newInterval[0]) {
            result.add(intervals[i++]);
        }

        while (i < intervals.length && intervals[i][0] <= newInterval[1]) {
            newInterval[0] = Math.min(newInterval[0], intervals[i][0]);
            newInterval[1] = Math.max(newInterval[1], intervals[i][1]);
            i++;
        }
        result.add(newInterval);

        while (i < intervals.length) {
            result.add(intervals[i++]);
        }

        return result.toArray(new int[result.size()][]);
    }
}
```

### 180. Meeting Rooms Overlap

```java
import java.util.Arrays;

class Solution {
    public boolean canAttendMeetings(int[][] intervals) {
        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));

        for (int i = 1; i < intervals.length; i++) {
            if (intervals[i][0] < intervals[i - 1][1]) {
                return false;
            }
        }

        return true;
    }
}
```

---

## Stack, Queue, Linked List

### Helper Node

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
```

### 182. Min Stack

```java
import java.util.ArrayDeque;
import java.util.Deque;

class MinStack {
    private final Deque<int[]> stack = new ArrayDeque<>();

    public void push(int val) {
        int currentMin = stack.isEmpty() ? val : Math.min(val, stack.peek()[1]);
        stack.push(new int[] {val, currentMin});
    }

    public void pop() {
        stack.pop();
    }

    public int top() {
        return stack.peek()[0];
    }

    public int getMin() {
        return stack.peek()[1];
    }
}
```

### 183. Queue Using Two Stacks

```java
import java.util.ArrayDeque;
import java.util.Deque;

class MyQueue {
    private final Deque<Integer> in = new ArrayDeque<>();
    private final Deque<Integer> out = new ArrayDeque<>();

    public void push(int x) {
        in.push(x);
    }

    public int pop() {
        move();
        return out.pop();
    }

    public int peek() {
        move();
        return out.peek();
    }

    public boolean empty() {
        return in.isEmpty() && out.isEmpty();
    }

    private void move() {
        if (out.isEmpty()) {
            while (!in.isEmpty()) {
                out.push(in.pop());
            }
        }
    }
}
```

### 184. Stack Using Queues

```java
import java.util.ArrayDeque;
import java.util.Queue;

class MyStack {
    private final Queue<Integer> q = new ArrayDeque<>();

    public void push(int x) {
        q.offer(x);
        for (int i = 0; i < q.size() - 1; i++) {
            q.offer(q.poll());
        }
    }

    public int pop() {
        return q.poll();
    }

    public int top() {
        return q.peek();
    }

    public boolean empty() {
        return q.isEmpty();
    }
}
```

### 187. Merge Two Sorted Linked Lists

```java
class Solution {
    public ListNode mergeTwoLists(ListNode list1, ListNode list2) {
        ListNode dummy = new ListNode(0);
        ListNode tail = dummy;

        while (list1 != null && list2 != null) {
            if (list1.val <= list2.val) {
                tail.next = list1;
                list1 = list1.next;
            } else {
                tail.next = list2;
                list2 = list2.next;
            }
            tail = tail.next;
        }

        tail.next = list1 != null ? list1 : list2;
        return dummy.next;
    }
}
```

### 188. Find Middle Of Linked List

```java
class Solution {
    public ListNode middleNode(ListNode head) {
        ListNode slow = head;
        ListNode fast = head;

        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }

        return slow;
    }
}
```

### 189. Remove Nth Node From End

```java
class Solution {
    public ListNode removeNthFromEnd(ListNode head, int n) {
        ListNode dummy = new ListNode(0, head);
        ListNode slow = dummy;
        ListNode fast = dummy;

        for (int i = 0; i <= n; i++) {
            fast = fast.next;
        }

        while (fast != null) {
            slow = slow.next;
            fast = fast.next;
        }

        slow.next = slow.next.next;
        return dummy.next;
    }
}
```

### 190. Reorder List

```java
class Solution {
    public void reorderList(ListNode head) {
        if (head == null || head.next == null) {
            return;
        }

        ListNode slow = head;
        ListNode fast = head;
        while (fast.next != null && fast.next.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }

        ListNode prev = null;
        ListNode curr = slow.next;
        slow.next = null;
        while (curr != null) {
            ListNode next = curr.next;
            curr.next = prev;
            prev = curr;
            curr = next;
        }

        ListNode first = head;
        ListNode second = prev;
        while (second != null) {
            ListNode temp1 = first.next;
            ListNode temp2 = second.next;
            first.next = second;
            second.next = temp1;
            first = temp1;
            second = temp2;
        }
    }
}
```

### 191. LRU Cache Concept

**Java interview answer:** use a `HashMap<Integer, Node>` plus a doubly linked list, or use `LinkedHashMap` if the interviewer allows library-backed implementation. The key idea is `O(1)` lookup and `O(1)` eviction.

### 192. Circular Queue

```java
class CircularQueue {
    private final int[] q;
    private int head = 0;
    private int count = 0;

    CircularQueue(int k) {
        q = new int[k];
    }

    public boolean enQueue(int value) {
        if (count == q.length) {
            return false;
        }
        int tail = (head + count) % q.length;
        q[tail] = value;
        count++;
        return true;
    }

    public boolean deQueue() {
        if (count == 0) {
            return false;
        }
        head = (head + 1) % q.length;
        count--;
        return true;
    }
}
```

### 193. Deque Use Cases

**Java interview answer:** use `ArrayDeque`. It is ideal for sliding-window maximum, BFS, monotonic queue patterns, and problems where you need fast insertion and removal at both ends.

### 194. Monotonic Stack

**Java interview answer:** use an `ArrayDeque<Integer>` storing indices. Maintain increasing or decreasing order depending on whether you want next greater, next smaller, or span-like behavior.

### 195. Next Greater Element

```java
import java.util.ArrayDeque;
import java.util.Deque;

class Solution {
    public int[] nextGreater(int[] nums) {
        int[] result = new int[nums.length];
        java.util.Arrays.fill(result, -1);
        Deque<Integer> stack = new ArrayDeque<>();

        for (int i = 0; i < nums.length; i++) {
            while (!stack.isEmpty() && nums[stack.peek()] < nums[i]) {
                result[stack.pop()] = nums[i];
            }
            stack.push(i);
        }

        return result;
    }
}
```

### 196. Daily Temperatures

```java
import java.util.ArrayDeque;
import java.util.Deque;

class Solution {
    public int[] dailyTemperatures(int[] temperatures) {
        int[] result = new int[temperatures.length];
        Deque<Integer> stack = new ArrayDeque<>();

        for (int i = 0; i < temperatures.length; i++) {
            while (!stack.isEmpty() && temperatures[stack.peek()] < temperatures[i]) {
                int prev = stack.pop();
                result[prev] = i - prev;
            }
            stack.push(i);
        }

        return result;
    }
}
```

### 197. Largest Rectangle In Histogram

```java
import java.util.ArrayDeque;
import java.util.Deque;

class Solution {
    public int largestRectangleArea(int[] heights) {
        Deque<Integer> stack = new ArrayDeque<>();
        int best = 0;

        for (int i = 0; i <= heights.length; i++) {
            int current = i == heights.length ? 0 : heights[i];
            while (!stack.isEmpty() && heights[stack.peek()] > current) {
                int height = heights[stack.pop()];
                int left = stack.isEmpty() ? -1 : stack.peek();
                int width = i - left - 1;
                best = Math.max(best, height * width);
            }
            stack.push(i);
        }

        return best;
    }
}
```

### 198. Trapping Rain Water

```java
class Solution {
    public int trap(int[] height) {
        int left = 0;
        int right = height.length - 1;
        int leftMax = 0;
        int rightMax = 0;
        int water = 0;

        while (left < right) {
            if (height[left] < height[right]) {
                leftMax = Math.max(leftMax, height[left]);
                water += leftMax - height[left];
                left++;
            } else {
                rightMax = Math.max(rightMax, height[right]);
                water += rightMax - height[right];
                right--;
            }
        }

        return water;
    }
}
```

### 199. Evaluate Reverse Polish Notation

```java
import java.util.ArrayDeque;
import java.util.Deque;

class Solution {
    public int evalRPN(String[] tokens) {
        Deque<Integer> stack = new ArrayDeque<>();

        for (String token : tokens) {
            switch (token) {
                case "+":
                    stack.push(stack.pop() + stack.pop());
                    break;
                case "-": {
                    int b = stack.pop();
                    int a = stack.pop();
                    stack.push(a - b);
                    break;
                }
                case "*":
                    stack.push(stack.pop() * stack.pop());
                    break;
                case "/": {
                    int b = stack.pop();
                    int a = stack.pop();
                    stack.push(a / b);
                    break;
                }
                default:
                    stack.push(Integer.parseInt(token));
            }
        }

        return stack.peek();
    }
}
```

### 200. Sliding Window With Deque

**Java interview answer:** the main Java type is `ArrayDeque<Integer>`. Store indices, drop expired indices from the front, and drop weaker candidates from the back.

---

## Trees And Graphs

### Helper Nodes

```java
class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    TreeNode(int val) {
        this.val = val;
    }
}

class Node {
    public int val;
    public java.util.List<Node> neighbors;

    public Node() {
        neighbors = new java.util.ArrayList<>();
    }

    public Node(int val) {
        this.val = val;
        neighbors = new java.util.ArrayList<>();
    }
}
```

### 213. Maximum Depth Of Binary Tree

```java
class Solution {
    public int maxDepth(TreeNode root) {
        if (root == null) {
            return 0;
        }
        return 1 + Math.max(maxDepth(root.left), maxDepth(root.right));
    }
}
```

### 214. Invert Binary Tree

```java
class Solution {
    public TreeNode invertTree(TreeNode root) {
        if (root == null) {
            return null;
        }
        TreeNode temp = root.left;
        root.left = invertTree(root.right);
        root.right = invertTree(temp);
        return root;
    }
}
```

### 215. Validate BST

```java
class Solution {
    public boolean isValidBST(TreeNode root) {
        return dfs(root, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    private boolean dfs(TreeNode node, long low, long high) {
        if (node == null) {
            return true;
        }
        if (node.val <= low || node.val >= high) {
            return false;
        }
        return dfs(node.left, low, node.val) && dfs(node.right, node.val, high);
    }
}
```

### 216. Lowest Common Ancestor

```java
class Solution {
    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        if (root == null || root == p || root == q) {
            return root;
        }

        TreeNode left = lowestCommonAncestor(root.left, p, q);
        TreeNode right = lowestCommonAncestor(root.right, p, q);

        if (left != null && right != null) {
            return root;
        }
        return left != null ? left : right;
    }
}
```

### 217. Level Order Traversal

```java
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

class Solution {
    public List<List<Integer>> levelOrder(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null) {
            return result;
        }

        Deque<TreeNode> queue = new ArrayDeque<>();
        queue.offer(root);

        while (!queue.isEmpty()) {
            int size = queue.size();
            List<Integer> level = new ArrayList<>();

            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();
                level.add(node.val);
                if (node.left != null) {
                    queue.offer(node.left);
                }
                if (node.right != null) {
                    queue.offer(node.right);
                }
            }

            result.add(level);
        }

        return result;
    }
}
```

### 218. Tree Diameter

```java
class Solution {
    private int best = 0;

    public int diameterOfBinaryTree(TreeNode root) {
        depth(root);
        return best;
    }

    private int depth(TreeNode node) {
        if (node == null) {
            return 0;
        }

        int left = depth(node.left);
        int right = depth(node.right);
        best = Math.max(best, left + right);
        return 1 + Math.max(left, right);
    }
}
```

### 219. Path Sum

```java
class Solution {
    public boolean hasPathSum(TreeNode root, int targetSum) {
        if (root == null) {
            return false;
        }
        if (root.left == null && root.right == null) {
            return targetSum == root.val;
        }
        return hasPathSum(root.left, targetSum - root.val)
            || hasPathSum(root.right, targetSum - root.val);
    }
}
```

### 220. Serialize And Deserialize Tree

```java
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

class Codec {
    public String serialize(TreeNode root) {
        StringBuilder sb = new StringBuilder();
        build(root, sb);
        return sb.toString();
    }

    private void build(TreeNode node, StringBuilder sb) {
        if (node == null) {
            sb.append("#,");
            return;
        }
        sb.append(node.val).append(",");
        build(node.left, sb);
        build(node.right, sb);
    }

    public TreeNode deserialize(String data) {
        List<String> values = new LinkedList<>(Arrays.asList(data.split(",")));
        return parse(values);
    }

    private TreeNode parse(List<String> values) {
        String value = values.remove(0);
        if (value.equals("#")) {
            return null;
        }

        TreeNode node = new TreeNode(Integer.parseInt(value));
        node.left = parse(values);
        node.right = parse(values);
        return node;
    }
}
```

### 222. Clone Graph

```java
import java.util.HashMap;
import java.util.Map;

class Solution {
    public Node cloneGraph(Node node) {
        if (node == null) {
            return null;
        }
        return dfs(node, new HashMap<>());
    }

    private Node dfs(Node node, Map<Node, Node> copies) {
        if (copies.containsKey(node)) {
            return copies.get(node);
        }

        Node copy = new Node(node.val);
        copies.put(node, copy);
        for (Node neighbor : node.neighbors) {
            copy.neighbors.add(dfs(neighbor, copies));
        }
        return copy;
    }
}
```

### 223. Course Schedule

```java
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

class Solution {
    public boolean canFinish(int numCourses, int[][] prerequisites) {
        List<List<Integer>> graph = new ArrayList<>();
        for (int i = 0; i < numCourses; i++) {
            graph.add(new ArrayList<>());
        }

        int[] indegree = new int[numCourses];
        for (int[] edge : prerequisites) {
            graph.get(edge[1]).add(edge[0]);
            indegree[edge[0]]++;
        }

        Deque<Integer> queue = new ArrayDeque<>();
        for (int i = 0; i < numCourses; i++) {
            if (indegree[i] == 0) {
                queue.offer(i);
            }
        }

        int taken = 0;
        while (!queue.isEmpty()) {
            int course = queue.poll();
            taken++;
            for (int next : graph.get(course)) {
                indegree[next]--;
                if (indegree[next] == 0) {
                    queue.offer(next);
                }
            }
        }

        return taken == numCourses;
    }
}
```

### 224. Topological Sort

**Java interview answer:** build adjacency lists with `ArrayList<List<Integer>>`, track indegrees in an `int[]`, then process zero-indegree nodes using an `ArrayDeque<Integer>`.

### 225. Detect Cycle In Graph

```java
import java.util.List;
import java.util.Map;

class Solution {
    public boolean hasCycle(Map<Integer, List<Integer>> graph) {
        java.util.Map<Integer, Integer> state = new java.util.HashMap<>();

        for (int node : graph.keySet()) {
            if (dfs(node, graph, state)) {
                return true;
            }
        }

        return false;
    }

    private boolean dfs(int node, Map<Integer, List<Integer>> graph, java.util.Map<Integer, Integer> state) {
        if (state.containsKey(node)) {
            return state.get(node) == 1;
        }

        state.put(node, 1);
        for (int neighbor : graph.getOrDefault(node, java.util.Collections.emptyList())) {
            if (dfs(neighbor, graph, state)) {
                return true;
            }
        }
        state.put(node, 2);
        return false;
    }
}
```

### 226. Connected Components

```java
import java.util.ArrayList;
import java.util.List;

class Solution {
    public int countComponents(int n, int[][] edges) {
        List<List<Integer>> graph = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            graph.add(new ArrayList<>());
        }
        for (int[] edge : edges) {
            graph.get(edge[0]).add(edge[1]);
            graph.get(edge[1]).add(edge[0]);
        }

        boolean[] seen = new boolean[n];
        int count = 0;

        for (int i = 0; i < n; i++) {
            if (!seen[i]) {
                count++;
                dfs(i, graph, seen);
            }
        }

        return count;
    }

    private void dfs(int node, List<List<Integer>> graph, boolean[] seen) {
        if (seen[node]) {
            return;
        }
        seen[node] = true;
        for (int next : graph.get(node)) {
            dfs(next, graph, seen);
        }
    }
}
```

### 227. Shortest Path In Unweighted Graph

```java
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;

class Solution {
    public int shortestPath(Map<Integer, List<Integer>> graph, int start, int target) {
        Deque<int[]> queue = new ArrayDeque<>();
        java.util.Set<Integer> seen = new java.util.HashSet<>();
        queue.offer(new int[] {start, 0});
        seen.add(start);

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int node = current[0];
            int dist = current[1];
            if (node == target) {
                return dist;
            }

            for (int next : graph.getOrDefault(node, java.util.Collections.emptyList())) {
                if (seen.add(next)) {
                    queue.offer(new int[] {next, dist + 1});
                }
            }
        }

        return -1;
    }
}
```

### 228. Dijkstra Basics

**Java interview answer:** use `PriorityQueue<int[]>` where each entry is `{distance, node}`. Pop the smallest known distance, skip stale entries, and relax neighbors. Dijkstra requires non-negative weights.

### 229. Union Find Basics

```java
class UnionFind {
    private final int[] parent;
    private final int[] rank;

    UnionFind(int n) {
        parent = new int[n];
        rank = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i;
        }
    }

    public int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]);
        }
        return parent[x];
    }

    public boolean union(int a, int b) {
        int rootA = find(a);
        int rootB = find(b);
        if (rootA == rootB) {
            return false;
        }

        if (rank[rootA] < rank[rootB]) {
            int temp = rootA;
            rootA = rootB;
            rootB = temp;
        }
        parent[rootB] = rootA;
        if (rank[rootA] == rank[rootB]) {
            rank[rootA]++;
        }
        return true;
    }
}
```

### 230. Bipartite Graph Check

```java
import java.util.ArrayDeque;
import java.util.Deque;

class Solution {
    public boolean isBipartite(int[][] graph) {
        int[] color = new int[graph.length];
        java.util.Arrays.fill(color, -1);

        for (int start = 0; start < graph.length; start++) {
            if (color[start] != -1) {
                continue;
            }

            Deque<Integer> queue = new ArrayDeque<>();
            queue.offer(start);
            color[start] = 0;

            while (!queue.isEmpty()) {
                int node = queue.poll();
                for (int neighbor : graph[node]) {
                    if (color[neighbor] == -1) {
                        color[neighbor] = 1 - color[node];
                        queue.offer(neighbor);
                    } else if (color[neighbor] == color[node]) {
                        return false;
                    }
                }
            }
        }

        return true;
    }
}
```

---

## Recursion, Backtracking, DP, Search

### 262. Climbing Stairs

```java
class Solution {
    public int climbStairs(int n) {
        if (n <= 2) {
            return n;
        }

        int a = 1;
        int b = 2;
        for (int i = 3; i <= n; i++) {
            int next = a + b;
            a = b;
            b = next;
        }
        return b;
    }
}
```

### 264. Longest Common Subsequence

```java
class Solution {
    public int longestCommonSubsequence(String text1, String text2) {
        int rows = text1.length();
        int cols = text2.length();
        int[][] dp = new int[rows + 1][cols + 1];

        for (int i = rows - 1; i >= 0; i--) {
            for (int j = cols - 1; j >= 0; j--) {
                if (text1.charAt(i) == text2.charAt(j)) {
                    dp[i][j] = 1 + dp[i + 1][j + 1];
                } else {
                    dp[i][j] = Math.max(dp[i + 1][j], dp[i][j + 1]);
                }
            }
        }

        return dp[0][0];
    }
}
```

### 265. Edit Distance

```java
class Solution {
    public int minDistance(String word1, String word2) {
        int rows = word1.length();
        int cols = word2.length();
        int[][] dp = new int[rows + 1][cols + 1];

        for (int i = 0; i <= rows; i++) {
            dp[i][cols] = rows - i;
        }
        for (int j = 0; j <= cols; j++) {
            dp[rows][j] = cols - j;
        }

        for (int i = rows - 1; i >= 0; i--) {
            for (int j = cols - 1; j >= 0; j--) {
                if (word1.charAt(i) == word2.charAt(j)) {
                    dp[i][j] = dp[i + 1][j + 1];
                } else {
                    dp[i][j] = 1 + Math.min(
                        dp[i + 1][j],
                        Math.min(dp[i][j + 1], dp[i + 1][j + 1])
                    );
                }
            }
        }

        return dp[0][0];
    }
}
```

### 266. Knapsack Idea

**Java interview answer:** define `dp[i][cap]` as the best value using items from index `i` onward with remaining capacity `cap`. The transition compares skipping the item versus taking it if capacity allows.

### 267. Subsets

```java
import java.util.ArrayList;
import java.util.List;

class Solution {
    public List<List<Integer>> subsets(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        backtrack(nums, 0, new ArrayList<>(), result);
        return result;
    }

    private void backtrack(int[] nums, int index, List<Integer> path, List<List<Integer>> result) {
        if (index == nums.length) {
            result.add(new ArrayList<>(path));
            return;
        }

        path.add(nums[index]);
        backtrack(nums, index + 1, path, result);
        path.remove(path.size() - 1);
        backtrack(nums, index + 1, path, result);
    }
}
```

### 268. Permutations

```java
import java.util.ArrayList;
import java.util.List;

class Solution {
    public List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        backtrack(nums, 0, result);
        return result;
    }

    private void backtrack(int[] nums, int start, List<List<Integer>> result) {
        if (start == nums.length) {
            List<Integer> permutation = new ArrayList<>();
            for (int num : nums) {
                permutation.add(num);
            }
            result.add(permutation);
            return;
        }

        for (int i = start; i < nums.length; i++) {
            swap(nums, start, i);
            backtrack(nums, start + 1, result);
            swap(nums, start, i);
        }
    }

    private void swap(int[] nums, int i, int j) {
        int temp = nums[i];
        nums[i] = nums[j];
        nums[j] = temp;
    }
}
```

### 269. Combination Sum

```java
import java.util.ArrayList;
import java.util.List;

class Solution {
    public List<List<Integer>> combinationSum(int[] candidates, int target) {
        List<List<Integer>> result = new ArrayList<>();
        backtrack(candidates, 0, target, new ArrayList<>(), result);
        return result;
    }

    private void backtrack(int[] candidates, int index, int remaining, List<Integer> path, List<List<Integer>> result) {
        if (remaining == 0) {
            result.add(new ArrayList<>(path));
            return;
        }
        if (remaining < 0 || index == candidates.length) {
            return;
        }

        path.add(candidates[index]);
        backtrack(candidates, index, remaining - candidates[index], path, result);
        path.remove(path.size() - 1);
        backtrack(candidates, index + 1, remaining, path, result);
    }
}
```

### 270. N-Queens Idea

**Java interview answer:** backtrack row by row. Track blocked columns, diagonals, and anti-diagonals using `HashSet<Integer>` or boolean arrays.

### 272. Search In Rotated Sorted Array

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

            if (nums[left] <= nums[mid]) {
                if (nums[left] <= target && target < nums[mid]) {
                    right = mid - 1;
                } else {
                    left = mid + 1;
                }
            } else {
                if (nums[mid] < target && target <= nums[right]) {
                    left = mid + 1;
                } else {
                    right = mid - 1;
                }
            }
        }

        return -1;
    }
}
```

### 273. Find First And Last Position

```java
class Solution {
    public int[] searchRange(int[] nums, int target) {
        return new int[] {bound(nums, target, true), bound(nums, target, false)};
    }

    private int bound(int[] nums, int target, boolean first) {
        int left = 0;
        int right = nums.length - 1;
        int answer = -1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] == target) {
                answer = mid;
                if (first) {
                    right = mid - 1;
                } else {
                    left = mid + 1;
                }
            } else if (nums[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return answer;
    }
}
```

### 274. Median Of Two Sorted Arrays Idea

**Java interview answer:** binary search on the smaller array to find a valid partition. Mention `O(log(min(m, n)))` and explain the left-partition max and right-partition min logic.

### 275. Kth Largest Element Using Heap

```java
import java.util.PriorityQueue;

class Solution {
    public int findKthLargest(int[] nums, int k) {
        PriorityQueue<Integer> minHeap = new PriorityQueue<>();

        for (int num : nums) {
            minHeap.offer(num);
            if (minHeap.size() > k) {
                minHeap.poll();
            }
        }

        return minHeap.peek();
    }
}
```

### 276. Top K Frequent Elements

```java
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

class Solution {
    public int[] topKFrequent(int[] nums, int k) {
        Map<Integer, Integer> count = new HashMap<>();
        for (int num : nums) {
            count.put(num, count.getOrDefault(num, 0) + 1);
        }

        PriorityQueue<int[]> heap = new PriorityQueue<>((a, b) -> Integer.compare(a[1], b[1]));
        for (Map.Entry<Integer, Integer> entry : count.entrySet()) {
            heap.offer(new int[] {entry.getKey(), entry.getValue()});
            if (heap.size() > k) {
                heap.poll();
            }
        }

        int[] result = new int[k];
        for (int i = k - 1; i >= 0; i--) {
            result[i] = heap.poll()[0];
        }

        return result;
    }
}
```

### 277. Heap Basics

**Java interview answer:** Java’s `PriorityQueue` is a min-heap by default. Use it when you repeatedly need the smallest item, or invert the comparator for max-heap behavior.

### 278. Recursion Tree Thinking

**Java interview answer:** explain how many branches each call creates, how deep the recursion goes, and whether subproblems repeat. That naturally leads into time complexity and memoization.

### 279. Memoization vs Tabulation

**Java interview answer:** memoization is top-down with recursion plus cache, often using arrays or maps. Tabulation is bottom-up with iterative DP arrays. Tabulation avoids recursion depth limits and often makes iteration order more explicit.

### 280. State Definition In DP

**Java interview answer:** define the state in plain English first. For example, `dp[i]` might mean the best answer starting at index `i`, or `dp[i][j]` might mean the best answer using prefixes up to `i` and `j`.

## Java Input / Output Sample Bank

Use this section as your test-case sheet. After coding each problem, speak one sample input and one expected output before moving on.

### Arrays And Strings Samples

| Problem | Input | Output |
|---|---|---|
| Three Sum | `[-1,0,1,2,-1,-4]` | `[[-1,-1,2],[-1,0,1]]` |
| Merge Sorted Arrays | `nums1 = [1,2,3,0,0,0], m = 3, nums2 = [2,5,6], n = 3` | `nums1 = [1,2,2,3,5,6]` |
| Remove Duplicates | `[0,0,1,1,1,2,2,3,3,4]` | length `5`, prefix `[0,1,2,3,4]` |
| Rotate Array | `nums = [1,2,3,4,5,6,7], k = 3` | `[5,6,7,1,2,3,4]` |
| Prefix Sum Range Query | `nums = [2,4,6,8], left = 1, right = 3` | `18` |
| Difference Array Updates | `length = 5, updates = [[1,3,2],[2,4,3],[0,2,-2]]` | `[-2,0,3,5,3]` |
| Group Anagrams | `["eat","tea","tan","ate","nat","bat"]` | groups like `[["eat","tea","ate"],["tan","nat"],["bat"]]` |
| Valid Anagram | `s = "anagram", t = "nagaram"` | `true` |
| String Compression | `['a','a','b','b','c','c','c']` | length `6`, chars start as `['a','2','b','2','c','3']` |
| Substring Search | `haystack = "sadbutsad", needle = "sad"` | `0` |
| Word Pattern | `pattern = "abba", s = "dog cat cat dog"` | `true` |
| Sliding Window Max | `nums = [1,3,-1,-3,5,3,6,7], k = 3` | `[3,3,5,5,6,7]` |
| Longest Common Prefix | `["flower","flow","flight"]` | `"fl"` |
| Roman To Integer | `"MCMXCIV"` | `1994` |
| Integer To Roman | `1994` | `"MCMXCIV"` |
| String To Integer | `"   -42"` | `-42` |
| Reverse Words | `"  hello   world  "` | `"world hello"` |
| Missing Number | `[3,0,1]` | `2` |
| Best Time To Buy And Sell Stock | `[7,1,5,3,6,4]` | `5` |
| Merge Intervals | `[[1,3],[2,6],[8,10],[15,18]]` | `[[1,6],[8,10],[15,18]]` |
| Insert Interval | `intervals = [[1,3],[6,9]], new = [2,5]` | `[[1,5],[6,9]]` |
| Meeting Rooms Overlap | `[[0,30],[5,10],[15,20]]` | `false` |

### Stack, Queue, Linked List Samples

| Problem | Input | Output |
|---|---|---|
| Min Stack | `push(-2), push(0), push(-3), getMin()` | `-3` |
| Queue Using Two Stacks | `push(1), push(2), peek(), pop()` | `peek = 1, pop = 1` |
| Stack Using Queues | `push(1), push(2), top(), pop()` | `top = 2, pop = 2` |
| Merge Two Lists | `1->2->4` and `1->3->4` | `1->1->2->3->4->4` |
| Middle Of List | `1->2->3->4->5` | node with value `3` |
| Remove Nth From End | `1->2->3->4->5, n = 2` | `1->2->3->5` |
| Reorder List | `1->2->3->4->5` | `1->5->2->4->3` |
| Circular Queue | size `3`, enqueue `1,2,3,4` | last enqueue returns `false` |
| Next Greater Element | `[2,1,2,4,3]` | `[4,2,4,-1,-1]` |
| Daily Temperatures | `[73,74,75,71,69,72,76,73]` | `[1,1,4,2,1,1,0,0]` |
| Largest Rectangle | `[2,1,5,6,2,3]` | `10` |
| Trapping Rain Water | `[0,1,0,2,1,0,1,3,2,1,2,1]` | `6` |
| Reverse Polish Notation | `["2","1","+","3","*"]` | `9` |

### Trees, Graphs, DP Samples

| Problem | Input | Output |
|---|---|---|
| Maximum Depth | tree `3,9,20,null,null,15,7` | `3` |
| Invert Tree | root `4,2,7,1,3,6,9` | `4,7,2,9,6,3,1` |
| Validate BST | `2,1,3` | `true` |
| Lowest Common Ancestor | root `3,5,1,6,2,0,8`, `p=5`, `q=1` | node `3` |
| Level Order Traversal | root `3,9,20,null,null,15,7` | `[[3],[9,20],[15,7]]` |
| Tree Diameter | root `1,2,3,4,5` | `3` |
| Path Sum | root `5,4,8,11,null,13,4,7,2,null,null,null,1`, target `22` | `true` |
| Clone Graph | node `1` linked to `2` and `4` | deep copy graph with same shape |
| Course Schedule | `numCourses = 2, prerequisites = [[1,0]]` | `true` |
| Detect Cycle Directed Graph | `0->1, 1->2, 2->0` | `true` |
| Connected Components | `n = 5, edges = [[0,1],[1,2],[3,4]]` | `2` |
| Shortest Path Unweighted | edges `0-1-2`, start `0`, target `2` | `2` |
| Bipartite Graph | `[[1,3],[0,2],[1,3],[0,2]]` | `true` |
| Climbing Stairs | `n = 5` | `8` |
| Longest Common Subsequence | `"abcde", "ace"` | `3` |
| Edit Distance | `"horse", "ros"` | `3` |
| Subsets | `[1,2]` | `[[],[1],[2],[1,2]]` |
| Permutations | `[1,2,3]` | `6` permutations |
| Combination Sum | `candidates = [2,3,6,7], target = 7` | `[[2,2,3],[7]]` |
| Search Rotated Array | `[4,5,6,7,0,1,2], target = 0` | `4` |
| Find First And Last Position | `[5,7,7,8,8,10], target = 8` | `[3,4]` |
| Kth Largest | `nums = [3,2,1,5,6,4], k = 2` | `5` |
| Top K Frequent | `nums = [1,1,1,2,2,3], k = 2` | `[1,2]` |

### Extra Edge Cases To Rehearse

| Problem | Edge Case | Expected Output |
|---|---|---|
| Three Sum | `[0,0,0,0]` | `[[0,0,0]]` |
| Rotate Array | `nums = [1], k = 10` | `[1]` |
| Valid Anagram | `"rat", "car"` | `false` |
| Meeting Rooms | `[[7,10],[2,4]]` | `true` |
| Remove Nth From End | `1->2, n = 2` | `2` |
| Validate BST | `5,1,4,null,null,3,6` | `false` |
| Course Schedule | `numCourses = 2, prerequisites = [[1,0],[0,1]]` | `false` |
| Coin Change-style DP thinking | `coins = [2], amount = 3` | `-1` |

---

## Java Interview Reminders

- prefer `ArrayDeque` over legacy `Stack`
- use `HashMap` and `HashSet` directly and explain why
- for heaps, say `PriorityQueue`
- use helper methods to keep the main solution readable
- mention time and space complexity immediately after coding

## Good Java Spoken Line

I naturally think in Java data structures, so I’ll implement this in Java to keep the solution clear and precise.
