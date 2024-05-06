# OPQ (Optimal Product Quantization)

- Reference: https://www.microsoft.com/en-us/research/wp-content/uploads/2013/11/pami13opq.pdf

## NN (Nearest Neighbor) Problem

The popular problem NN(nearest neighbor) is a search problem defined as following.

> In a set V of vectors, given a vector x, find the nearest vector in V.

For example, let's consider a $V = {(1, 1), (0, 1), (1, 0), (0, 0)}$ and $x = (2/3, 2/3)$. Let $v_1 = (1, 1)$, $v_2 = (0,1)$, $v_3 = (1, 0)$, and $v_4 = (0, 0)$. Then the distances between each vector of $V$ and $x$ are the following.

- $|x - v_1| = \sqrt{2}/3$
- $|x - v_2| = \sqrt{5}/3$
- $|x - v_3| = \sqrt{5}/3$
- $|x - v_4| = \sqrt{8}/3$

Thus the nearest vector is $v_1 = (1, 1)$.

## ANN (Approximate nearest neighbor)

One of the solution of NN problem is ANN. ANN is an approximated nearest neighbor search. As the previous example, which is called BruteForce NN solution, if there is a $D$-dimensional vector set $V$ and its cardinarity is $N$, then the complexity is $O(D*N)$. For example, if there is a $100$-dimensional vector space $V$ and it has its cardinary of $1e9$, then one query for this space has $1e12$ operations. This is too many operations to query. So the ANN is more perferred way to query to the vector database.

## Quantization

A method of ANN is the quantization. The quantization is making a continuous space to a discrete space to make the space iterable. For more detail, let us take a real line $R$ to search a specific real value $x$. To find the exact value, we need to check whether the value is exact same with all uncountable number. But with the real line, we cannot iterate the values, because the next value of a real value is not defined. It will help us to find a value to separate the real line using some points. For example, we can separate the real line to the points of $0.1 * n$ with the integers $n$.

With the quantization, we can make a uniformly distributed fashion like the above example, but it will be more better to make points to the specific points. The countable points of a vector space is not efficient for the all kind of datasets. How can we obtain the more better quantization on the vector space?

### Vector Quantization

The vector quantization method has two ways for ANN.

- to build inverted indxing for non-exhaustive search
- to encode vectors into compact codes for exhaustive search

The first method has k-means method and its variants. For example, we can build the clusters of the given vector points, so that we can obtain the inverted index of the vectors. Also, we can store the quantized vectors as the centroids of the clusters (more generally it is called as codewords).

| Vector | Cluster Index |
|-|-|
| (1, 1) | 0 |
| (0, 0) | 1 |
| ... | ... |

| Index | Centroids |
|-|-|
| 0 | (2, 2) |
| 1 | (-1, -1) |
| ... | ... |

Then we can obtain the most similar vector as following steps.

1. Using the centroids, get the $k$ indices of clusters to search.
2. Using the inverted index table, get the vectors to search.
3. Find the most similar vector among the vectors.

But the product quantization is more better to solve the ANN problem with the exponentially large number of codewords.

### Product Quantization

### Optimized Product Quantization

## Evaluation of Quantization Solutions

## Conclusion
