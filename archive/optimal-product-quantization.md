# OPQ (Optimal Product Quantization)

- Reference: https://www.microsoft.com/en-us/research/wp-content/uploads/2013/11/pami13opq.pdf

## NN (Nearest Neighbor) Problem

The popular problem NN(nearest neighbor) is a search problem defined as following.

> In a set V of vectors, given a vector x, find the nearest vector in V.

For example, let's consider a $V = \lbrace(1, 1), (0, 1), (1, 0), (0, 0)\rbrace$ and $x = (2/3, 2/3)$. Let $v_1 = (1, 1)$, $v_2 = (0,1)$, $v_3 = (1, 0)$, and $v_4 = (0, 0)$. Then the distances between each vector of $V$ and $x$ are the following.

- $|x - v_1| = \sqrt{2}/3$
- $|x - v_2| = \sqrt{5}/3$
- $|x - v_3| = \sqrt{5}/3$
- $|x - v_4| = \sqrt{8}/3$

Thus the nearest vector is $v_1 = (1, 1)$.

## ANN (Approximate nearest neighbor)

One of the solution of NN problem is ANN. ANN is an approximated nearest neighbor search. As the previous example, which is called BruteForce NN solution, if there is a $D$-dimensional vector set $V$ and its cardinarity is $N$, then the complexity is $O(DN)$. For example, if there is a $100$-dimensional vector space $V$ and it has its cardinary of $1e9$, then one query for this space has $1e12$ operations. This is too many operations to query. So the ANN is more perferred way to query to the vector database.

## Quantization

A method of ANN is the quantization. The quantization is making a continuous space to a discrete space to make the space iterable. For more detail, let us take a real line $R$ to search a specific real value $x$. To find the exact value, we need to check whether the value is exact same with all uncountable number. But with the real line, we cannot iterate the values, because the next value of a real value is not defined. It will help us to find a value to separate the real line using some points. For example, we can separate the real line to the points of $0.1 * n$ with the integers $n$.

With the quantization, we can make a uniformly distributed fashion like the above example, but it will be more better to make points to the specific points. The countable points of a vector space is not efficient for the all kind of datasets. How can we obtain the more better quantization on the vector space?

### Vector Quantization

More generally, we can define the idea of solution as the below constraint problem.

> Let $x$ be a vector of $D$-dimensional vector space $V$ and a finite set of codewords $C= \lbrace c_j\rbrace$ with the index set $J$, is called as a codebook. Then we hope to construct a function $dec \circ enc: V \to V$, called quantizer. The $dec: J \to V$ and $enc: V \to J$ are called as a decoder and an encoder, respectively.  
> We hope that the quantizier to minimize the quantization distortion $E = \frac{1}{N} \sum_x{\|x - dec(enc(x))\|}$ where $N$ is the size of vector set.

The vector quantization method has to define three things.

- The way to build codebook.
- The way to calculate $enc$
- The way to calculate $dec$

The first thing is separated as below.

- to build inverted indxing for non-exhaustive search
- to encode vectors into compact codes for exhaustive search

#### Inverted indexing - k-means

With this method, we usually use k-means method and its variants. For example, we can build the clusters of the given vector points, so that we can obtain the inverted index of the vectors. Also, we can store the quantized vectors as the centroids of the clusters being as codebook.

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

For shortly, we will define codebook and functions using k-means from the problem definition.

- codebook = the set of centroids of clusters
- $enc(x)$ = the pair of the most nearest point of codebook from $x$ and $x$.
- $dec(c, x)$ = the most nearest point from $x$ in the cluster of having the centroid $c$. 

But the product quantization is more better to solve the ANN problem with the exponentially large number of codewords.

#### Compact codes - Product Quantization

The vector qunatization is the idea of reducing the search space, but the idea of product quantization is to reduce the vector space dimension itself. How we do this?

1. Decompose the original vector space into the $M$-dimensinal sub-spaces.
2. Qunatize each subspace into $k$ codewords.

Formally, denote any $x \in R^D$ as the concatenation of $M$ subvectors $x = [x^1, x^2, ..., x^M]$ with $x^m \in R^{D/M}$. Then we can divide the problem as the $M$ vector spaces $R^{D/M}$. For the data set, we can divide the set to the $M$ subspaces, and then we can find each codebooks $C^1, C^2, \dots, C^M$. The objective function of problem can be defined as below.

> Let $x$ be a vector of $D$-dimensional vector space $V = \prod_{i = 1}^M W$ where $W = R^{D/M}$ and a finite sets of codewords $C^i= \lbrace c_{i, j}\rbrace \subset W$ with the index set $J^i$, is called as a codebook. Let $C = C^1 \times C^2 \times \cdots \times C^M$, and $J =  J^1 \times J^2 \times \cdots \times J^M$. Then we hope to construct a function $dec \circ enc: V \to V$, called quantizer. The $dec: J \to V$ and $enc: V \to J$ are called as a decoder and an encoder as before, respectively. The objective function is to minmize $\sum_x{\|x - dec(enc(x))\|^2}$.

This is the basic of product quantization. 

### Optimized Product Quantization

With the product optimization, we can reduce the dimension of making codebooks from $D$ to $D/M$. But we can do more thing. As we can see from the graph below, the result of product quantization will preserve the basis of the vector space.

![image](/archive/images/optimal-product-quantization-visualization.png)

From the exprience of PCA(principal component analysis), we can find a new basis representing the data set more natuallty, and it will help us analysis the data set. Formally, we hope to find an orthogonormal matrix $R$ to map the element basis to new basis.

> Let $x$ be a vector of $D$-dimensional vector space $V = \prod_{i = 1}^M W$ where $W = R^{D/M}$ and a finite sets of codewords $C^i= \lbrace c_{i, j}\rbrace \subset W$ with the index set $J^i$, is called as a codebook. Let $R$ be an orthonormal matrix and $C = \lbrace c | Rc \in C^1 \times C^2 \times \cdots \times C^M\rbrace$, and $J =  J^1 \times J^2 \times \cdots \times J^M$. Then we hope to construct a function $dec \circ enc: V \to V$, called quantizer. The $dec: J \to V$ and $enc: V \to J$ are called as a decoder and an encoder as before, respectively. The objective function is to minmize $\sum_x{\|x - dec(enc(x))\|^2}$.

In general, finding $R$ is difficult and takes time. We can find $R$ by the below algoritm.

1. Fix $R$ and optimize $\lbrace C^i\rbrace$. Because the $R$ is orthonormal, so that $|x - c| = |Rx - Rc|$, the optimization problem will be changed to the PQ problem.
2. Fix $\lbrace C^i\rbrace$ and optimize $R$. The optimization problem will be given as $min \sum_x{|Rx - Rdec(enc(x))|^2}$. Because $\lbrace C^i\rbrace$ is fixed, the $R^{T} dec \circ enc$ will be fixed, so that it can be treated as a matrix. Denoting $x$ as a matrix, then the problem can be simplified as $min |RX - Y|^2_F$. We can solve this using the SVD.

#### Algorithm of OPQ

More formally, we can write the algorithm for OPQ as below.

- Input: training samples $\lbrace x\rbrace$, number of subspaces $M$, number of sub-codewords $k$ in each subcodebook.
- Output: the matrix R, sub-codebooks $\lbrace C^m \rbrace^M_{i=1}$, $M$ sub-indices $\lbrace i^m \rbrace ^M_{m=1}$ for each $x$.

1. Initialize $R$, $\lbrace C^m\rbrace ^M_{i=1}$, and  $\lbrace i^m\rbrace ^M_{m=1}$.
2. **repeat**
    1. Step 1: project the data: $\hat x = Rx$.
    2. **for** $m = 1$ to $M$ **do**
        1. for $j = 1$ to $k$: update $R^Tdec^m(j)$ by the sample mean of $\lbrace\hat x^m | enc^m(\hat x^m) = j\rbrace$.
        2. for $\forall \hat x^m$: update $enc^m(\hat x^m)$ by the sub-index of the sub-codeword $\hat c^m$ that is nearest to $\hat x^m$.
    3. **end for**
    4. Step 2: solve $R$ by (7).
3. **until** max iteration number reached.

## Choice of initial $R$

In the algorithm, finding $R$ can take much times to converge some value. But we can use useful inital value assuming the data as the gaussian distribution. With the assumption of the gaussian distribution on the data set, the $R$ is optimal when it is the result of PCA. I will skip the exact derivation.

## Experiment

We can compare our OPQ algorithm with the other algorithms. I will not handle the refrences on the paper in this file.

- $OPQ_P$: this is our parametric solution. This will assume the parametric guassian distribution on the data set.
- $OPQ_{NP}$: this is our non-parametric solution initialized by the parametric solution. This will not assume any distribution but using the initial value using parametric solution. For example, this will use the PCA result.
- $PQ_{RO}$: the dimensions are randomly ordered. This is suggested from the reference [1] in paper.
- $PQ_{RR}$: the data are aligned using PCA and then randomly rotated. This is suggested from the reference [3] in paper.
- $TC$ (Transform Coding): this is a Scalar Quantization (SQ) method. SQ is a special case of PQ that each dimension forms a subspace. TC uses the principal components as the subspaces. It assigns each principal component with an adaptive number of bits. This is suggested from the reference [8] in paper.
- $ITQ$ : this is a special vector quantization method that is also binary embedding. This is suggested from the reference [9] in paper.

![experiment result](/archive/images/optimal-product-quantization-exp-result.png)

We can see the $OPQ_{NP}$ has the optimal performance of the NN problem.

## Conclusion

With the problem NN, we can use OPQ to find the appropriate point having low distortion compared with the other algorithms.
