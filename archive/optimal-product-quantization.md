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

### Vector Quantization

### Product Quantization

### Optimized Product Quantization

## Evaluation of Quantization Solutions

## Conclusion
