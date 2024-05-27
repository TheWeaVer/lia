# Transaction of VectorDB

## What is ACID?
- **Atomicity**: Ensures that transactions are completely applied in the database or not at all. For example, if an error occurs during the insertion of vector data, any changes that have been made are rolled back.
- **Consistency**: Ensures that the database remains in a consistent state after a transaction is completed. This means all constraints, triggers, etc., related to vector data must be satisfied post-transaction.
- **Isolation**: Ensures that transactions executed concurrently do not affect each other. This allows for vector searches or updates during concurrent data changes without adversely affecting other transactions.
- **Durability**: Guarantees that the results of successful transactions are not lost even in the event of a system failure, ensuring the stability of vector database data after a failure.

## Common Methods to Achieve Atomicity
- **Transaction Logging**: The database records changes in a log before starting a transaction. If an error occurs, the log is used to roll back the data to its prior state, ensuring the operation is completely successful or entirely fails.
- **Consensus Algorithms in Distributed Systems**: Managed services like Pinecone typically handle data across multiple nodes. In such cases, consensus algorithms (e.g., Paxos, Raft) ensure all nodes agree on the execution of transactions. This guarantees consistent application of changes across all nodes.
- **Data Partitioning and Locking Mechanisms**: Databases use locking mechanisms to control access to relevant data sets during a transaction, preventing other concurrent transactions from affecting data consistency.
- **Support for Atomic Operations**: Some systems provide support for atomic operations at the database engine level, ensuring that individual operations or small batches of operations are either fully executed or not executed at all.

## Common Methods to Achieve Consistency
- **Immediate Data Replication**: In distributed systems, changes to data are immediately replicated across all nodes, ensuring that any node accessed will always have the most current data. This process is optimized to minimize network latency.
- **Using Consensus Protocols**: Algorithms such as Paxos or Raft are used to ensure all nodes reach a consistent agreement on data changes, allowing all users to view the latest state of the database.
- **Global Locks or Locking Mechanisms**: During data changes, global locks on the entire database or specific data sets prevent simultaneous modifications, maintaining data consistency but potentially leading to performance degradation.
- **Version Control and Timestamps**: Every data change is assigned a version number or timestamp, ensuring users always access the most up-to-date data. This helps in tracking changes and resolving conflicts.

## Common Methods to Achieve Isolation
### Multi-Version Concurrency Control (MVCC)
- **MVCC**: Maintains multiple versions of data to prevent read transactions from being blocked by write transactions. This method reads a specific snapshot of data at the time of the transaction, enhancing concurrency while maintaining isolation.
### Locking Mechanism
- **Locking**: Sets locks on specific parts of data or the database during a transaction, ensuring exclusive use by that transaction. This prevents other transactions from modifying the data concurrently.
### Optimized Locking Strategies
- **Optimistic and Pessimistic Locking**: Optimistic locking executes transactions without anticipating conflicts, checking for data collisions only at commit time. Pessimistic locking acquires locks before accessing data, proactively preventing conflicts.
### Isolation Levels
- **Isolation Levels**: Databases offer various levels of isolation, allowing applications to select the degree of isolation needed. This helps balance performance and consistency.

## Common Methods to Achieve Durability
### Data Replication
- **Replication**: Storing copies of data across multiple nodes ensures that data can be recovered from another node if one fails. Systems like Elasticsearch, Solr, and Milvus enhance durability through data replication based on a distributed architecture. Pinecone, being a cloud-based service, is also likely to utilize data replication.
### Ensuring Persistence
- **Persistence**: Data is stored not only in memory but also on disk, ensuring data is maintained even after a system restart. Most vector database systems use disk-based storage solutions to ensure data persistence.
### Transaction Logging
- **Transaction Logging**: All data changes are recorded in transaction logs, allowing data to be restored to its last committed state in case of system errors. These logs play a crucial role in enhancing data durability.
### Automatic Recovery Mechanism
- **Automatic Recovery**: If the system shuts down abnormally, it initiates an automatic recovery process at startup to check and restore data consistency using transaction logs and persistence data.
### Regular Backups
- **Backup**: Data is periodically backed up to provide a last resort for recovery in case of significant data loss incidents. Backups can be stored offsite and used for recovery if necessary.

## Comparison Across VectorDBs
| Attribute/VectorDB | Pinecone | Elasticsearch | Solr | Milvus |
|---|---|---|---|---|
| Atomicity | Supported | Partially Supported | Partially Supported | Partially Supported |
| Consistency | Strongly Supported | Partially Supported; no strict consistency | Partially Supported; no strict consistency | Partially Supported; depends on the consistency model |
| Isolation | Supported | Partially Supported | Partially Supported | Partially Supported |
| Durability | Supported | Supported | Supported | Supported |

### Transaction Achievement Methods for Each VectorDB
- **Pinecone**: As a cloud-based managed service, it ensures robustness and durability of transactions through automated data replication and distributed processing.
- **Elasticsearch**: Performs data modification operations through well-defined RESTful APIs, internally using persistence and replication mechanisms to support transaction-like tasks.
- **Solr**: Utilizes Multi-Version Concurrency Control (MVCC) and write-ahead logging to ensure persistence of changes, providing a transaction-like environment.
- **Milvus**: Enhances transaction durability with a distributed system design and Write-Ahead Logging (WAL), supporting data persistence and recovery.
"""

## Reference
- https://www.linkedin.com/pulse/choosing-vector-database-your-gen-ai-stack-abhinav-srivastava/
- https://mingchin.tistory.com/381
- https://inspirit941.tistory.com/504
