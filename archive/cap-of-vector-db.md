
# What is CAP?
- [CAP Theorem on Wikipedia](https://en.wikipedia.org/wiki/CAP_theorem)

# Explanation of CAP
- Consistency: The degree to which all nodes in the system display the same data at the same time. Pinecone aims for strong consistency, while Elasticsearch and Solr offer eventual consistency. Milvus enhances consistency through WAL and replication.
- Availability: The ability to always return a valid response when a request is received. All systems aim for high availability using various replication mechanisms and failover strategies.
- Partition Tolerance: The ability for the system to continue operating despite network partitions. All systems provide high partition tolerance based on a distributed architecture.

# Why is the CAP theorem not all satisfactory?

- "C, A, P are all considered characteristics of a distributed system, but in reality, this is not the case.
- It might feel like one can give up P and choose C and A, but P is a mandatory condition.
- If you choose not to opt for P, you must have a network that never fails, but no such network exists in this world.
- Whether you like it or not, P must be chosen, and ultimately the choice is between C or A.
- In other words, in a distributed system, during a network failure, you can only choose either consistency or availability, but not both."

# How VectorDBs Achieve These
| VectorDB | Consistency | Availability | Partition Tolerance |
| --- | --- | --- | --- |
| Pinecone | Uses synchronized replication and transaction logging to provide strong consistency | Ensures availability through automated replication and failover mechanisms | Maintains service operation during network partitions through a distributed architecture |
| Elasticsearch | Uses an eventual consistency model, adjustable through settings | Achieves high availability through data replication across clusters and shards | Ensures partition tolerance through shards and replicas |
| Solr | Uses eventual consistency model, consistency level adjustable through sync/async replication options | Ensures availability through master-slave or cloud mode | Strengthens partition tolerance with ZooKeeper-based cluster management |
| Milvus | Enhances consistency using WAL (Write-Ahead Logging) and synchronized replication | Ensures availability with data replication and automatic disaster recovery mechanisms | Achieves high partition tolerance with a distributed system design |

# Glossary
## Eventual Consistency Model
- In distributed systems, all replicas will eventually synchronize to the latest state over time
- Does not provide immediate consistency but maintains system availability and partition tolerance during network partitions or node failures
- In Elasticsearch, indexed documents may not be immediately available for all searches. As documents are indexed, this change must propagate to all replicas in the cluster, causing some delay. During this delay, Elasticsearch ensures eventual consistency; eventually, all replicas will be synchronized and reflect in search results.
- Elasticsearch manages this through 'refresh' and 'flush' mechanisms. Refresh operations make newly indexed data searchable, while flush operations permanently store data on disk. Elasticsearch periodically performs refreshes to keep search documents up to date, achieving eventual consistency even during network partitions or node failures.

## Write-Ahead Logging (WAL)
- A method of logging transactions ahead of the actual operations
### Benefits of WAL
- Durability: In case of a database system crash, WAL ensures that all changes after the last known safe point can be recovered, meeting the database's durability requirements.
- Data Recovery: After a system failure, WAL can be used to recover the database to a reliable and consistent state.
- Performance Improvement: Using WAL allows database write operations to be recorded sequentially in a log file, which is more efficient than random disk access, providing performance benefits, especially in write-heavy applications.

# Reference
- http://eincs.com/2013/07/misleading-and-truth-of-cap-theorem/
- https://www.ibm.com/kr-ko/topics/cap-theorem