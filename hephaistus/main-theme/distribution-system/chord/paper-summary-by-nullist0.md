# CHORD

## 4. CHORD Protocol

### Definitions

- m = the number of identifier
- identifier of key = SHA-1(key value)
- identifier of node = SHA-1(node IP)
- identifier circle = modulo set of 2^m
- successor(k) = the first node n of identifier(k) <= identifier(n)
- finger[k] of node N = the first node n of identifier(N) + 2^(k-1) <= identifier(n)

### Save

키가 k인 데이터를 저장하는 규칙은 `successor(k)`에 해당하는 노드에 저장하는 것이다.

### Find

새로운 키-값을 노드에 저장하기 위해서 값을 찾는데에 `finger`라는 테이블을 통해 노드를 찾고 해당 값을 노드에 저장한다. 이때 노드를 탐색할때마다 최소한 2배씩 탐색 영역이 줄어듦으로 탐색 범위 횟수는 O(logN)이 된다.

```text=
n.find_successor(id)
  if (id in (n, successor])
    return successor;
  else
    n' = closet_preceding_node(id);
    return n'.find_successor(id);

n.closest_preceding_node(id)
  for i = m downto 1
    if(finger[i] in (n, id))
      return finger[i];
  return n;
```

### Stabilization

노드가 추가되고 삭제될때 노드에 저장되었던 데이터는 이동되어야 한다.

- 노드가 추가되는 경우 다음 노드에 저장된 데이터 중 저장 규칙에 맞게 새로운 노드에 저장되어야 한다.
- 노드가 삭제되는 경우 저장된 데이터를 다음 노드에 저장 규칙에 맞게 저장해야 한다.
- 두 경우 모두 `finger` 테이블을 업데이트 해야한다.

이러한 과정을 stabilization이라고 한다.

```text
n.create()
  predecessor = null;
  successor = n;

n.join(n')
  predecessor = nil;
  successor = n'.find_successor(n);

n.stabilize()
  x = successor.predecessor;
  if (x in (n, successor))
    successor = s;
  successor.notify(n);

n.notify(n')
  if (predecessor is nil or n' in (predecessor, n))
    predecessor = n;

n.fix_fingers()
  next = next + 1;
  if (next > m)
    next = 1;
  finger[next] = find_successor(n + 2^(next - 1));

n.check_predecessor()
  if (predecessor has failed)
    predecessor = nil;
```

### Failure

하지만 `finger`에 있는 노드들이 가용하지 못할 수 있다. 이 상황을 방어하기 위해 `r`개의 노드를 미리 저장해두면 안전해진다. 그리고 `r`은 이론상 Omega(logN)이면 충분하다.