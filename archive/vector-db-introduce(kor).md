# VectorDB

## 검색 시스템에서 벡터 검색이 왜 중요한가?

- 고급 검색 기능 중 특히 유사성 검색을 제공하기 위해서

## 그럼 유사성 검색이 왜 중요한가?

- 이미지 검색에서 특정 이미지와 비슷한 이미지들을 찾을 수 있음.
- 텍스트 검색에서 문맥적으로 유사한 문서를 찾을 수 있음.
- 이는 유저 경험과 연결되어 중요한 역할을 함.
  - ex) 발렌타인 데이에 초콜릿을 전달할 때, 초콜릿 브랜드는 상관없이 하트모양의 초콜릿을 구매할때.

## 그렇다면 Vector란?

- 벡터는 공간 내의 점을 나타내는데 사용되며, 여러 차원을 가질 수 있음
- 텍스트, 이미지, 비디오 등의 데이터를 고차원 벡터로 변환하여 이해할 수 있음
- 변환과정은 대개 딥러닝 모델같은 기계학습 알고리즘이 사용됨

## VectorDB란?

- 고차원 벡터들을 저장, 관리, 검색할 수 있는 데이터베이스
- 벡터 DB는 유사성 검색(similarity search)에 최적화

## VectorDB의 필요성

- 대규모 데이터셋에서 유사성 검색을 실시간으로 수행하기 위해서는 특별한 기술이 필요
- 전통적인 데이터베이스 시스템은 이러한 유형의 검색에 최적화되어 있지 않음

## 그렇다면 VectorDB의 작동원리 및 주요기능은?

- 벡터 인덱싱
  - 고차원 벡터 데이터를 효과적으로 저장하고 검색하기 위해 특수한 인덱싱 기술을 사용
  - Approximate Nearest Neighbor(ANN) 알고리즘
- 유사성 검색
  - 사용자가 쿼리 벡터를 제공하면, Vector DB는 저장된 벡터 중에서 이 쿼리 벡터와 가장 유사한 벡터들을 신속하게 찾아냄
  - 이 과정은 ANN 검색 알고리즘에 의해 수행될 수 있으며, 매우 빠른 속도로 대규모 데이터셋에서도 유사한 항목을 찾아낼 수 있음
- 데이터 분할 및 분산 처리
  - 대규모 벡터 데이터셋을 효과적으로 관리하기 위해, Vector DB는 데이터를 분할하고 여러 노드에 분산시켜 저장할 수 있음
- 직관적인 API 제공
  - 대부분의 Vector DB는 사용자가 쉽게 데이터를 삽입, 조회, 수정할 수 있도록 직관적인 API를 제공함
  - 개발자는 이 API를 사용하여 애플리케이션과 Vector DB 간의 상호 작용을 쉽게 구현할 수 있음

### **벡터 데이터 처리 과정**

1. **데이터 준비**: 원본 데이터(텍스트, 이미지 등)를 벡터 형태로 변환합니다. 이 과정은 일반적으로 머신 러닝 모델을 사용하여 수행됩니다.
2. **데이터 삽입**: 변환된 벡터를 Vector DB에 삽입합니다. 이 때, 인덱싱 알고리즘을 사용하여 데이터를 효율적으로 저장합니다.
3. **유사성 검색 실행**: 사용자가 쿼리를 제출하면, Vector DB는 쿼리와 유사한 벡터를 데이터베이스에서 찾아 반환합니다.
4. **결과 평가 및 조정**: 검색 결과를 바탕으로, 필요한 경우 인덱싱 방법이나 쿼리 처리 방식을 조정하여 검색 품질을 개선할 수 있습니다.

## Vector DB 솔루션

- Milvus, Weaviate, Vespa, Faiss

### **Vector DB 사용 사례**

- 이미지 및 비디오 검색
  - 사용자가 이미지를 업로드하면, 시스템이 해당 이미지와 유사한 이미지나 비디오를 데이터베이스에서 찾아내는 기능. 이는 쇼핑, 디지털 자산 관리, 감시 시스템 등에서 유용하게 사용될 수 있음
- 자연어 처리 및 문서 검색
  - 텍스트 데이터를 벡터로 변환하여, 주제나 문맥이 유사한 문서를 찾아내는 애플리케이션. 이는 고객 지원 시스템, 지식 관리 시스템, 법률 및 연구 문서 검색 등에 적용될 수 있음
- 추천 시스템
  - 사용자의 선호도, 이전 행동, 그리고 다른 사용자들의 데이터를 분석하여 개인화된 제품이나 콘텐츠를 추천.
  - 이는 온라인 쇼핑, 스트리밍 서비스, 소셜 미디어 플랫폼 등에서 중요한 기능임

### **Vector DB 도입 시 고려 사항**

- 성능 및 확장성
  - 대량의 데이터와 고속 처리가 요구되는 경우, 성능과 확장성이 중요한 고려 사항이 됩니다. 사용할 Vector DB 솔루션이 이러한 요구 사항을 충족하는지 확인 필요
- 유지보수 및 지원
  - 오픈 소스 솔루션을 선택할 경우, 커뮤니티 지원 및 문서의 질을 고려
  - 상업용 솔루션을 고려하는 경우에는 기술 지원 및 서비스 수준 협약(SLA)을 검토
- 데이터 보안 및 개인정보 보호
  - 사용자 데이터를 처리할 때는 데이터 보안 및 개인정보 보호 규정을 준수해야 합니다. 선택한 Vector DB 솔루션이 이러한 요구 사항을 충족하는지 확인하는 것이 중요합니다.

### **Vector DB 베스트 프랙티스**

- 데이터 품질 관리
  - **데이터 전처리**: 데이터를 벡터로 변환하기 전에, 데이터의 품질을 확인하고 필요한 전처리 작업(예: 노이즈 제거, 정규화)을 수행 필요. 높은 품질의 데이터는 검색 결과의 정확도를 크게 향상시키기 때문.
- 모델 선택과 튜닝
  - **특징 추출 모델**: 데이터를 벡터로 변환할 때 사용하는 모델(예: 딥러닝 모델)을 신중하게 선택하고, 데이터와 사용 사례에 맞게 튜닝해야함. 모델의 성능은 벡터 검색의 정확도에 직접적인 영향을 미침.
- 인덱싱 전략
  - **적절한 인덱싱 알고리즘 선택**: 데이터의 특성과 검색 요구 사항에 맞는 인덱싱 알고리즘을 선택. 인덱싱 효율성은 검색 속도와 시스템의 확장성에 중요한 역할을 함.
- 성능 모니터링 및 조정
  - **성능 모니터링**: 시스템의 성능을 지속적으로 모니터링하고, 검색 속도, 정확도, 시스템 부하 등의 지표를 추적. 성능 저하의 원인을 식별하고 개선 가능.
