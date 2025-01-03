# Map-reduce

## Motivation

There are hundreds of programming models to handle some data sets. But the Google has some problems to handle
their data sets.

- The input data is large.
- The computations have to be distributed across more than hundreds of machines.

To handle those data sets, the below detalis must be implemented.

- how to parallelized the computation
- how to distribute the data
- how to handle failures

After Google implemented hundreds of such programming models, they made an abstraction of them, that is
Map-reduce model.

## Model definition

The programming model can be defined as the input and output of its execution. There are three execution model
on Map-reduce model, that are the full program, map function and reduce function.

- A computation
    - Input: a set of key/value pair
    - Output: a set of key/value pair
- Map: the function of producing intermediate key/value pair
    - Input: a key/value pair
    - Output: a set of intermediate key/value pairs
- Reduce: the function of producing intermediate key/value pair
    - Input: an intermediate key I and a set of values for that key.
    - Output: the desired ouput on the given intermediate key I
      With pseudocode, we can write as below. The specific implementation needs to be written by the developers.

```
def program(input: Pair<K, V>): Output<K, O>
  do 

def map(input: Pair<K, V>): Set<Intermediate<T, W>>
  do

def reduce(key: T, intermediates: Set<Intermediate<T, W>>): Output<K, O>
  do
```

## Implementation on Google

### Distributed execution

![execution overviews](images/map-reduce-execution.png)

In general, a MapReduce library will be implemented by following the below steps. The below is came from the
paper.

1. The MapReduce library in the user program first splits the input files into M pieces of typically 16
   megabytes to 64 megabytes (MB) per piece (controllable by the user via an optional parameter). It then starts
   up many copies of the program on a cluster of machines.
2. One of the copies of the program is special – the master. The rest are workers that are assigned work by the
   master. There are M map tasks and R reduce tasks to assign. The master picks idle workers and assigns each
   one a map task or a reduce task.
3. A worker who is assigned a map task reads the contents of the corresponding input split. It parses key/value
   pairs out of the input data and passes each pair to the user-defined Map function. The intermediate key/value
   pairs produced by the Map function are buffered in memory.
4. Periodically, the buffered pairs are written to local disk, partitioned into R regions by the partitioning
   function. The locations of these buffered pairs on the local disk are passed back to the master, who is
   responsible for forwarding these locations to the reduce workers.
5. When a reduce worker is notified by the master about these locations, it uses remote procedure calls to read
   the buffered data from the local disks of the map workers. When a reduce worker has read all intermediate
   data, it sorts it by the intermediate keys so that all occurrences of the same key are grouped together. The
   sorting is needed because typically many different keys map to the same reduce task. If the amount of
   intermediate data is too large to fit in memory, an external sort is used.
6. The reduce worker iterates over the sorted intermediate data and for each unique intermediate key
   encountered, it passes the key and the corresponding set of intermediate values to the user’s Reduce
   function. The output of the Reduce function is appended to a final output file for this reduce partition.
7. When all map tasks and reduce tasks have been completed, the master wakes up the user program. At this point,
   the MapReduce call in the user program returns back to the user code.

This can be simplified as below psuedocodes.

```
// Worker
def Worker.invoke(func: Function, args: Array)
  result := func(args)
  buffer(result)
  return bufferedResult

// MapReduce maseter
def process(input: Pair<K, V>): Output<K, O>
  workers = find computation workers
  mapWorkers = assign workers to do map functions
  reduceWorkers assign workers to do reduce functions

  for splittedInput = split input to M inputs; do
    await mapWorkers.invoke(map, splittedInput)
      .thenFlatten { intermediates -> findIdleWorker(reduceWorkers).invoke(reduce, key, intermediates) }
      .then(::bufferResult)
  return bufferredResult

// User-defined map function
def map(input: Pair<K, V>): Set<Intermediates<T, W>>
  do user defined map process

// User-defined reduce function
def reduce(key: T, intermediates: Set<Intermediate<T, W>>): Output<K, O>
  do user defined reduce process
```

### Data structures on master

The master should keep below data structures.

- The state for each map and reduce tasks.
- The identity of the worker machine for each map and reduce tasks.
- The $R$ intermediate files for each completed map tasks. (These files will be piped to the in-progress reduce
  tasks incrementally).

For more example, the master can store them in json like below.

```json
{
  "tasks": [
    {
      "task": "map-1",
      "state": "in-progress",
      "worker": "worker-1"
    },
    {
      "task": "map-2",
      "state": "completed",
      "worker": "worker-2"
    },
    {
      "task": "reduce-1",
      "state": "idle",
      "worker": "undefined"
    }
  ],
  "files": [
    {
      "location": "/user/var/xxx",
      "from": "map-2",
      "to": "reduce-1",
      "size": "3000"
    }
  ]
}
```

### Fault Tolerance

The MapReduce calculation has multiple machines. The distributed computation can be failed.

- The worker machine failure
- The master machine failure

Google prevent such failures using below implementations.

- The worker machine failure
    - The master pings every worker periodically, and consider the worker as failure when it does not response.
    - Restoration policy
        - Failed in-progressing reduce/map worker: Re-execute the worker.
        - Failed completed map worker: Re-execute the worker when the result is in-accessible. The reduce task
          will be notified if the machine does not read the result of the failed task.
        - Failed completed reduce worker: No re-execution because it will save result to a global file.
- The master machine failure
    - The master saves the status represented by the data structure defined above periodically. This is similar
      to the database transaction restoration.
    - Restoration policy: The master loads the status and restore its state.

Above contents are the fault tolerance of stateless jobs. The original paper is also introducing about the fault
tolerance of stateful jobs. Please refer the original paper.

### Locality

As we know, the network communication has disadvantage of speed and bandwidth usage. Google uses the local
storage of each machine to reduce the usage of network. More detail, GFS(Google file system) will

- divide the input data into 64MB blocks, and
- store several copies of each block on different machines.

The second thing will help the MapReduce restore using locality. On the failure of a map task, the master will
schedule the machine near a replica of that task's input data. So the most input data will be read locally and
consume no network bandwidth.

### Task Granularity

The MapReduce has three parameters with tasks.

- the number of map tasks: $M$
- the number of reduce tasks: $R$
- the number of worker machines: $W$

Ideally, $M \gg W$ and $R \gg W$ are needed. It will improve

- the dynamic load balancing, and
- the speed of recovery on failure.

But too many tasks will reduce the framework overhead. This is theoretical approach. With the Google
implementation, there are practical bounds on how $M$ and $R$.

- There are $O(M + R)$ scheduling decisions on master.
- There are $O(M * R)$ states on memory of master.

Also, there are some heuristics on Google's implementation.

- The input data is roughly from 16MB to 64MB.
- The $R$ is a small multiple of $W$.

Google often uses $M = 200,000$, $R = 5,000$, $W = 2,000$. Moreover, from the Hadoop's documents, there are some
guides.

- The number of map tasks
    - It is determined by the size of split of input size. By default, it is 128MB.
- The number of reduce tasks
    - A multiple of the block size
    - A task time between 5 and 15 minutes
    - Creates the fewest files possible

### Backup tasks

The MapReduce can have a large time to complete, because of "straggler". The straggler is a machine that takes
an unusually long time to complete one of the last few map or reduce tasks in the computation. They appears
because of lots of reasons. For example,

- Bad disk causes bit correction algorithm.
- Competition for CPU, memory, local disk, or network bandwidth
- Bugs on machine initialization code

Google has a general mechanism to alleviate the problem of stragglers, that is the backup tasks. When the
MapReduce operation is close to completion, the master schedules backup executions of the remaining in-progress
tasks. A task is marked as completed if the primary or backup execution completes. This heuristic solution
reduces 44% of the execution time.

## Refinements

We can use basic implementation of MapReduce, but there are a few extensions useful. We will introduce 9 tools.

1. Partitioning function
2. Ordering guarantees
3. Combiner function
4. Input and output types
5. Side-effect
6. Skipping bad records
7. Local execution
8. Status information
9. Counters

### Partitioning function: Save result of the desired keys to on same output file

- Requirement: Specific results of reduce work need to be saved on the same output file.
- Solution: Use specific partitioning function to distribute the result by the specific partition.

The users of MapReduce can define the parameters, that are `the number of reduce tasks`(i.e.
`the number of output files`). Let define this as $R$. We can use $hash(key) \mod R$ as a partitioning function
in general cases. But we may need to define a specific function.

For example, we hope to calculate a rank of a website using MapReduce. If we use general partitioning function,
the result of many sites of a same host will be distributed to $R$ output files. But it is better to collect the
result of many sites of a same host on an output file. In this case, the function, which is
$hash(hostname(URL)) \mod R$, will satisfy the requirement.

### Ordering guarantees: Sorting of result

- Requirement: Find the result of reduce task by given key quickly.
- Solution: Use ordered result by key and use binary search.

In Google, the intermediate key-value pairs have same ordering with original keys sorted in ascending order.
This ensures the results to have same increasing order. As the result, we can find desired result using binary
search instead of sequential search.

### Combiner function: Make intermediate key-value pairs be smaller

- Requirement: Decrease the size of intermediate key-value pairs if we can.
- Conditions
    - Many repetition of intermediate key-value pairs exists.
    - Reduce function is commutative and associative.
- Solution: Add a new function Combiner function on the Map task.

Like the word counter, we can construct as below

- Map function to provide a set of intermediate key-value pairs as <word, 1>
- Reduce function to provide a key-value of <word, # of words>

We can predict many same intermediate key-value pairs are generated, like <"a", 1>. Before starting a Reduce
task, we can decrease the size by the Combiner function.

> Combiner function: a function to be executed on each machine that performs a map task. It may have the same
> code with the Reduce task. Unlike the Reduce task, it will write on an intermediate file rather than an output
> file.

### Input and output types: Make key-value pair have meaningful range

- Requirement: Specify input type and output type.
- Solution: Add a reader implementation on the tasks.

In general, there are several implementation to generate an output type from a file. For instance, the text mode
implementation will map a file to a pair of line number and line text. We can change the key and value by some
reasons. For example, we can map key as a first line number and a value as 30 lines of a file. Also, we can read
a key-value pair from database. It is a concept of reader. The reader will provide a functionality to convert
from a file to an input/output type. But the predefined implementations may be enough to use.

### Side-effects: Producing auxiliary file from each tasks

- Requirements: Make a side-effect like producing some additional file.
- Solution: Use atomic and idempotent side-effects.

Some users may want to produce an auxiliary file from map/reduce operators. We may implement this by below
steps.

1. Write a temporary file.
2. Rename atomically file to mark as generated.

Because of lack of two-phase commit of multiple output files, the tasks should be deterministic. It means that
when the task is executed, the output should be same whenever the task is reset.

#### Note

From the MATLAB document, the deterministic datastore means below requirements. For example, a file with
resetting the seek position, or a cold flow of an input values.

> The datastore is deterministic if the result of read operation on the datastore is same after the datastore is
> reset, which removes all executed read operations.

From the Hadoop document, the deterministic datastore will be implemented as below.

1. Write a temporary file named with a unique name generated by task and number of attempts (re-execution caused
   by failure).
2. Move the temporary file to the global output directory.
3. If the task is failed, the temporary files are discarded.

### Skipping bad records: Ensure robustness on a few failures

- Requirements: A few bugs can be occurred but the MapReduce work should not be stopped.
- Solution: Skip the bad records and send signal to re-execute it later.

There can be a bug when the MapReduce works. We can

- fix the bug, or
- ignore the record causing the error.

It is the best to fix the bug, but it may not be available because the code is the third party library so that
it may not be changeable. As a result we can take the second solution, and Google uses the below steps.

1. Install a signal handler to catch segmentation fault or bus errors.
2. Map or Reduce operation will be invoked after the MapReduce stores the sequence number of the argument as a
   global variable.
3. If the user code generates a signal, then the signal handler will send a UDP package, containing the sequence
   number defined on previous step, to the master.
4. If the task re-executed by mechanism of fault tolerance is failed, the master will skip it.

### Local extensions: Debug, profile, and perform small-scale testing on local machine

- Requirements: Before running a MapReduce using the distributed system, we want to track all the operations.
- Solution: Run MapReduce locally with the small set of records.

The MapReduce operation is not easy to debug. Because it uses

- distributed system on several thousand machines, and
- dynamically determined work assignment by the master.

But we hope to debug, profile, and test with small-scale. We can use this with local extension, which executes
MapReduce operation on local machine. Users can use special flag and debugging or testing tool like gdb.

### Status information: See the current status of MapReduce operation

- Requirements: We hope to observe the current progress status or error task status.
- Solution: Use the master's status information page.

The master runs internal HTTP server and exports a set of status pages for human consumption. In this page, we
can some information. For example, the below things can be checked.

- The progress of computation.
- The standard error and standard output files generated by each task.
- How long the computation will take.
- Whether more resources should be added to the computation.
- Which workers have failed.

### Counters: Provide a count value of MapReduce operation

- Requirements: Add a desired count index of a MapReduce operation without duplication.
- Solution: Use counter implementation on the MapReduce library.

If we want to count something on the MapReduce operation, there are a requirements to implement the counter.

- The counter should be increased atomically through the multiple workers.
- The counter should not be increased in duplicate by the re-execution of a task or the backup tasks.
- The counter should be stored on the master to be able to be observed on the status page.

Because the above requirements are not easy to implement, the MapReduce provides such Counter feature. For
example, Hadoop provides a
[Counter](https://hadoop.apache.org/docs/r3.0.1/api/org/apache/hadoop/mapreduce/Counter.html) implementation can
be used as below. The below example is the counting a word with two counters. A counter will count the number of
words having any lowercase character, and another counter will count the number of words having any uppercase
characters.

```kotlin
enum class WordMapReduceCounters {
    // Counter enum for counting words containing any uppercase character.
    CONTAINS_LOWERCASE,

    // Counter enum for counting words containing any lowercase character.
    CONTAINS_UPPERCASE;
}

class WordCountMapper : Mapper() {
    override fun map(key: Any, value: Text, context: Context) {
        val tokenizer = StringTokenizer(value.toString())
        while (tokenizer.hasMoreTokens()) {
            val token = tokenizer.nextToken()
            val text = Text().set(token)
            when {
                token.toUppercase() == token ->
                    context.getCounter(WordMapReduceCounters.CONTAINS_UPPERCASE).increase(1)
                token.toLowercase() == token ->
                    context.getCounter(WordMapReduceCounters.CONTAINS_LOWERCASE).increase(1)
                else -> {
                    context.getCounter(WordMapReduceCounters.CONTAINS_UPPERCASE).increase(1)
                    context.getCounter(WordMapReduceCounters.CONTAINS_LOWERCASE).increase(1)
                }
            }
            // it is the same with an intermediate key-value <word, 1>
            context.write(text, IntWritable(1))
        }
    }
}
```

## Performance

Let me summarize the testing result only. For the detailed performance configuration and implementation, please
check the original paper.

There are two computation performance result.

- Grep search on 1e+10 100-byte records for a relatively rare three-character pattern
- Sort 1e+10 100-byte records.

### Cluster Configuration

- The number of machines: about 1,800
- CPU on each machine: two 2GHz Intel Xeon processor with enabled HyperThreading feature
- Memory on each machine: 4GB
- Disks on each machine: two 180GB IDE disk
- Network link: gigabit Ethernet link
- Network topology: two-level tree-shaped switched network
- Network bandwidth: about 100-200 Gbps of aggregate bandwidth available at root
- RTT (round-trip time) on each pair: less than a milliseconds

### Result analysis

The parameters of the MapReduce processing are below.

- The number of Map operation = $M$
- The number of Reduce operation = $R$

There are two performance index can be comparable

- Peak processing rate per seconds (i.e peak rate)
- Total execution time

The result of Grep operation is below. But there is no comparison with other algorithms on the original paper.

- Grep
    - Parameters: $M = 15000, R = 1$
    - Peak rate = 30GB/s
    - Total execution time = 150 s

We can find meaningful comparison on the performance of Sort. There are three tests. We can compare the total
execution time with TeraSort benchmark. Also, please note that the test is constructed as the MapReduce
operation saves the two same reduced file on the master. It is intended to ensure reliability and availability.

- Common configuration
    - Parameters: $M = 15000, R = 4000$
- Sort with normal operation
    - Total execution time = 891 s (-16% over the TeraSort benchmark)
- Sort with no backup tasks
    - Total execution time = 1283 s (+44% over the normal execution time)
- Sort with killing 200 workers
    - Total execution time = 933s (+5% over the normal execution time)

Note that the fault tolerance and backup task strategy is efficient on actual operation.

The peak rate is not denoted here because the sorting itself is worked by MapReduce library. Google uses the
key ordering by the master. So the peak rate of the entire operation is not an actual execution. The paper
separates steps of MapReduce into three phases. One is input phase which is working on Map workers, another is
shuffling phase which is working on the master, and the last is output phase which is working on Reduce workers.
For the detailed analysis, please check the paper. I couldn't understand the meaning of these analysis.

## Experience: Advantages of using MapReduce library

On the Google, the MapReduce is used widely. For example, the below jobs are worked by MapReduce operation.

- large-scale machine learning problems
- clustering problems for the Google News and Froogle products
- extraction of data used to produce reports of popular queries
- extraction of properties of web pages for new experiments and products
- large-scale graph computations

The MapReduce helps the productivity too. There are advantages.

- The map and reduce are simple programs so speeding up development and prototyping cycle.
- The programmers who have no experience with distributed and/or parallel system can exploit large amounts of
  resources easily.

More detailed experience can be found on the Google web search service. Google team get the below advantages.

- The indexing code is simpler, smaller, and easier to understand.
    - Ex. the size of one phase of the computation dropped from 3800 lines to 700 lines (-82% of lines)
- The performance of MapReduce library is good enough.
    - Ex. an operation taking a few months is changed to an operation taking a few days.
- The indexing process has become much easier to operate.
- It is easy to improve the performance of the indexing process by adding new machines to the indexing cluster.

# Reference

- https://static.googleusercontent.com/media/research.google.com/ko//archive/mapreduce-osdi04.pdf