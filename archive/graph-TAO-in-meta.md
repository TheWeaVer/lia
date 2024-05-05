# TAO (The Associations and Objects): The power of the graph

This is summarizing and understanding page of the Meta engineering blog [TAO: The power of the graph](https://engineering.fb.com/2013/06/25/core-infra/tao-the-power-of-the-graph/).

keywords: association, graph, multi region consistency


# Overview

> As efficient as MySQL is at managing data on disk, the assumptions built into the InnoDB buffer pool 
> algorithms don’t match the request pattern of serving the social graph. The spatial locality on ordered data 
> sets that a block cache attempts to exploit is not common in Facebook workloads. Instead, what we call 
> creation time locality dominates the workload — a data item is likely to be accessed if it has been recently
> created. Another source of mismatch between our workload and the design assumptions of a block cache is the
> fact that a relatively large percentage of requests are for relations that do not exist — e.g., “Does this
> user like that story?” is false for most of the stories in a user’s News Feed. Given the overall lack of
> spatial locality, pulling several kilobytes of data into a block cache to answer such queries just pollutes
> the cache and contributes to the lower overall hit rate in the block cache of a persistent store.

MySQL InnoDB storage engine utilized buffer pool algorithm to efficiently manage disk-based data. The buffer
pool caches frequently accessed data and indexed in memory to minimize disk I/O operations. The algorithm based
on the Spatial Locality principal, which assumes that there is a high likelihood of accessing data near a 
recently accessed piece of data. This pattern is observed in many traditional applications, where the buffer pool
algorithm proves to be very effective.

However, workload of social services does not always validate this assumption. The request pattens in social
services exhibit a different kind of locality known as Temporal Locality. Temporal Locality implies that data
that has been recently created or modified is more likely to be accessed again. For instance, in a social
network, users tend to access the most recent posts or news feed items more frequently.

In such patterns, the temporal order of data is more critical than the spatial arrangement. Therefore, buffer
pool algorithm are not optimized for there types of request patterns. Social services, in particular, exhibit
characteristics such as:
1. High access frequency to recent data
   - Users are more interested in the latest content, leading to frequent access to data that has been recently
   created. This means a high frequency of access to a small subset of the entire dataset.
2. Many requests for non-existent relationships
   - Queries about whether a user likes a specific story are likely to be false in most cases. These queries
   necessitate fetching data into memory even when there are no results, wasting cache space and reducing the
   overall hit rate.

Ref. [buffer pool architecture in inno db](mysql-memory-innodb-buffer-pool.md#importance-of-spatial-locality).


# Design of TAO
TBD

# Implementation
TBD

# Consistency
TBD
