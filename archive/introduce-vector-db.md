# Vector DB

## Why is vector search important in a search system?

- To Provide similarity search, especially among advanced search features

## Why is similarity search important?

- In an image search, find images similar to a particular image.
- In a text search, find similar documents in context. 
- It is important in connection with the user experience.
  - ex) When deliver chocolate on Valentine's Day, you can buy heart-shaped chocolate regardless of the chocolate brand.

## What is Vector?

- Vector is used to represent points in space and can have multiple dimensions.
- Transforming text, images, videos, etc. into high-dimensional vectors for understanding.
- The transformation process usually uses machine learning algorithms.

## What is Vector DB?

- Vector DB that can store, manage, and retrieve high-dimensional vectors.
- Vector DB optimizes for similar vectors.

## Why Vector DB is needed

- Real-time similarity searches on large datasets require special technology.
- Traditional database systems are not optimized for vector search.

## How Vector DB Works and Key Features

### Key Features
- Vector Indexing
  - Use special indexing techniques to effectively store and retrieve high-dimensional vector data.
  - ex) Approximate Nearest Neighbor(ANN)
- Search for similarity
  - When the user provides a query vector, the Vector DB quickly finds the vectors most similar to this query vector among the stored vectors.
  - This process can be performed by special search algorithms, such as ANN, and similar items can be found on large datasets at very high speeds.
- Data segmentation and distribution processing
  - To effectively manage large vector datasets, Vector DB can split and store data across multiple nodes.
- Provides intuitive API
  - Most Vector DBs provide intuitive APIs that make it easy for users to insert, query, and modify data.
  - Developers can easily implement interactions between applications and Vector DB using this API

### **Vector Data Processing Process**

1. **Data Preparation**: Transforms the original data (text, images, etc.) into vector form; this is usually done using machine learning models.
2. **Insert Data**: Insert the transformed vector into the Vector DB, where the indexing algorithm is used to store the data efficiently.
3. **Run a similarity search**: When a user submits a query, the Vector DB finds and returns a vector similar to the query in the database.
4. **Evaluation and Coordination of Results**: Based on the search results, you can adjust the indexing method or query processing method if necessary to improve the search quality.

## Vector DB Solution

- Milvus, Weaviate, Vespa, Faiss

### **Vector DB Use Cases**

- Search for images and videos
  - When a user uploads an image or video, the system finds an image or video similar to that image in a database.
  - This can be useful in shopping, digital asset management, surveillance systems, etc.
- Natural language processing and document retrieval
  - An application that converts text data into vectors to find documents with similar topics or contexts.
  - This can be applied to customer support systems, knowledge management systems, legal and research document retrieval, etc
- Recommendation System
  - It analyzes users' preferences, previous behavior, and other users' data to recommend personalized products or content.
  - This is an important feature in online shopping, streaming services, social media platforms, and more

### **Considerations for introducing Vector DB**

- Performance and Scalability
  - Performance and scalability are critical considerations when large amounts of data and high-speed processing are required.
- Maintenance and Support
  - Consider community support and document quality when choosing an open source solution
  - Review technical support and service level agreements (SLA) when considering commercial solutions.
- Data security and privacy
  - You must comply with data security and privacy regulations when processing user data.
  - It is important to ensure that the Vector DB solution you select meets these requirements.

### **Best practice for Vector DB**

- Data Quality Management
  - **Data preprocessing**: Before converting the data into a vector, it is necessary to check the quality of the data and perform the necessary preprocessing operations (e.g., noise removal, normalization). Because high quality data greatly improves the accuracy of search results.
- Model selection and tuning
  - **Feature extraction model**: Models used to transform data into vectors (e.g., deep learning models) must be carefully selected and tuned to the data and use cases. The performance of the model directly affects the accuracy of vector search.
- Indexing strategy
  - **Select the appropriate indexing algorithm**: Select an indexing algorithm that fits the characteristics of the data and search requirements; indexing efficiency plays an important role in the speed of search and the scalability of the system.
- Monitoring and Coordinating Performance
  - **Performance Monitoring**: Continuously monitor the performance of the system and track indicators such as search speed, accuracy, system load, etc. Identify the cause of the degradation and can improve.

# references
- https://www.pinecone.io/learn/vector-database/