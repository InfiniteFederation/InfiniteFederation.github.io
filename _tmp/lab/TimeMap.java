import java.util.*;                                              // brings in HashMap, List, ArrayList, Map

class TimeMap {
    // one map holds all the "history" of times, per key
    private Map<String, List<Integer>> timestamps = new HashMap<>();   // key -> list of times saved for that key
    // a second map holds the matching values, in the same order as the times above
    private Map<String, List<String>> values = new HashMap<>();        // key -> list of values saved for that key

    public void set(String key, String value, int timestamp) {
        // If we haven't seen this key before, create empty lists for it first
        if (!timestamps.containsKey(key)) {                      // checks: does this key already have a list?
            timestamps.put(key, new ArrayList<Integer>());       // no -> make a new empty list for its times
            values.put(key, new ArrayList<String>());            // and a new empty list for its values
        }
        timestamps.get(key).add(timestamp);                      // add this time to the end of the key's time list
        values.get(key).add(value);                              // add this value to the end of the key's value list
    }

    public String get(String key, int timestamp) {
        if (!timestamps.containsKey(key)) {                      // this key was never saved at all
            return "";                                           // nothing to return
        }
        List<Integer> ts = timestamps.get(key);                  // grab this key's list of times
        List<String> vs = values.get(key);                       // grab this key's list of values (same order as ts)

        int lo = 0;                                              // left edge of the search area
        int hi = ts.size() - 1;                                  // right edge of the search area
        int resultIdx = -1;                                      // -1 means "nothing found yet"

        while (lo <= hi) {                                       // keep narrowing down until the area is empty
            int mid = (lo + hi) / 2;                              // pick the middle point to check
            if (ts.get(mid) <= timestamp) {                       // this time is not in the future -- it's a candidate
                resultIdx = mid;   // this one works, but maybe a later one works too
                lo = mid + 1;                                     // keep looking to the right for something closer
            } else {
                hi = mid - 1;      // too far in the future, look earlier
            }
        }

        if (resultIdx == -1) {                                   // the loop never found a valid time
            return "";                                           // so give back empty
        }
        return vs.get(resultIdx);                                // return the value that matched the best time found
    }
}

// --- Tracing our AAPL example through this code ---
// set("AAPL", "150", 1000) -> timestamps["AAPL"] = [1000], values["AAPL"] = ["150"]
// set("AAPL", "155", 2000) -> timestamps["AAPL"] = [1000, 2000], values["AAPL"] = ["150", "155"]
// set("AAPL", "160", 3000) -> timestamps["AAPL"] = [1000, 2000, 3000], values["AAPL"] = ["150", "155", "160"]
//
// get("AAPL", 2500):
//   ts = [1000, 2000, 3000]
//   lo=0, hi=2 -> mid=1 -> ts[1]=2000 <= 2500 -> resultIdx=1, lo=2
//   lo=2, hi=2 -> mid=2 -> ts[2]=3000 <= 2500? NO -> hi=1
//   loop ends (lo=2 > hi=1) -> resultIdx=1 -> return vs.get(1) = "155"  ✓ matches our example
