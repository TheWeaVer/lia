# InnoDB Buffer pool

The buffer pool is an area in main memory where `InnoDB` caches table and index dta as it is accessed.
The buffer pool permits frequently used data to be accessed directly from memory, which speeds up processing.
On dedicated servers, up to 80% of physical memory is often assigned to the buffer pool.

For efficiency of high-volume read operations, the buffer pool is divided into pages that can potentially hold
multiple rows. For efficiency of cache management, the buffer pool is implemented as a linked list of pages;
data that is rarely used aged out of the cache using a variation of the least recently used (LRU) algorithm.

For who manage MySQL, knowing how to take advantage of the buffer pool to keep frequently accessed data in
memory is an important aspect of MySQL tuning.

By the way, in this page, we focus on explaining the algorithm of buffer pool.


## Buffer Pool LRU algorithm

The buffer pool is managed as a list using a variation of the LRU algorithm. When room is needed to add a new 
page to the buffer pool, the least recently used page is evicted and a new page is added to the middle of the
list. The midpoint insertion strategy treats the list as two sublists:
* At the head, a sublist of new ("young") pages that were accessed recently
* At the tail, a sublist of old pages that were access less recently

The algorithm keeps frequently used pages in the new sublist. The old sublist contains less frequently used 
pages; these pages are candidates for eviction.

By default, the algorithm operates as follows:
1. 3/8 of the buffer pool is devoted to the old sublist. 
2. The midpoint of the list is the boundary where the tail of the new sublist meets the head of the old sublist. 
3. When InnoDB reads a page into the buffer pool, it initially inserts it at the midpoint (the head of the old 
sublist). A page can be read because it is required for a user-initiated operation such as an SQL query, or as 
part of a read-ahead operation performed automatically by InnoDB.
4. Accessing a page in the old sublist makes it “young”, moving it to the head of the new sublist. If the page 
was read because it was required by a user-initiated operation, the first access occurs immediately and the page
is made young. If the page was read due to a read-ahead operation, the first access does not occur immediately 
and might not occur at all before the page is evicted. 
5. As the database operates, pages in the buffer pool that are not accessed “age” by moving toward the tail of 
the list. Pages in both the new and old sublists age as other pages are made new. Pages in the old sublist also 
page as pages are inserted at the midpoint. Eventually, a page that remains unused reaches the tail of the old 
sublist and is evicted.

By default, pages read by queries are immediately moved into the new sublist, meaning they stay in the buffer
pool longer. A table scan, performed for a mysqldump operation or a SELECT statement with no WHERE clause, for
example, can bring a large amount of data into the buffer pool and evict an equivalent amount of older data, 
even if the new data is never used again. Similarly, pages that are loaded by the read-ahead background thread
and accessed only once are moved to the head of the new list. These situations can push frequently used pages to
the old sublist where they become subject to eviction. 

This is the clone of https://dev.mysql.com/doc/refman/8.0/en/innodb-buffer-pool.html.


## Importance of spatial locality


### Table scan

Searching the entire table to find all records that meet a specific condition. In this case, the table's data is
likely stored in contiguous blocks on disk. By leveraging spatial locality, the buffer pool can prefetch
adjacent blocks into memory as the current block is accessed. This significantly reduces disk I/O during
sequential data access, improving overall scan performance.


### Index Access

Indexes are also stored on disk and can have hierarchical structures like B-trees. When a database searches for
a specific record using an index, it sequentially traverses the nodes of the index. Spatial locality becomes
critical here. Adjacent index nodes are likely to be stored near each other on disk, so efficiently caching
them in the buffer pool can enhance index traversal performance.


### Join Operations

When joining two tables in a database, a record is fetched from one table, and then the associated record is
sought in another table. If the two tables have a foreign key-primary key relationship, related records may be
stored close to each other on disk. In this case, leveraging spatial locality to cache related data effectively
can improve the performance of join operations.

Ref. [TAO: Power of graph](graph-TAO-in-meta.md#overview).
