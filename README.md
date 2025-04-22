## 01. 프로젝트 설명
- 낙관적 락, 비관적 락, 분산락을 적용한 동시성 이슈 해결 프로젝트
- Redisson을 활용하여 분산락을 구현
- 락이 필요한 부분만 락을 적용하기 위해 함수형 프로그래밍을 도입하여 성능 개선

## 02. 핵심 기능
- 낙관적 락을 통한 동시성 이슈 해결
- 비관적 락을 통한 동시성 이슈 해결
- 분산락을 통한 동시성 이슈 해결
- 함수형 프로그래밍을 통한 분산락 성능 개선

## 03. 사용 기술
- `Spring Boot 3.2`, `Spring Data JPA`
- `Redis`, `MySQL`
- `Redisson`
- `Docker`, `Docker Compose`

## 04. 관련 포스팅
- [낙관적 락 vs 비관적 락 vs 분산락](https://lsh2613.tistory.com/270)
- [낙관적 락에서 조회 쿼리를 분리한 이유](https://lsh2613.tistory.com/270#%EB%82%99%EA%B4%80%EC%A0%81%20%EB%9D%BD%20%EC%A1%B0%ED%9A%8C%20%EC%BF%BC%EB%A6%AC%EB%A5%BC%20%EB%94%B0%EB%A1%9C%20%EB%B6%84%EB%A6%AC%ED%95%9C%20%EC%9D%B4%EC%9C%A0-1)
- [함수형 프로그래밍을 적용한 분산락 성능 개선](https://lsh2613.tistory.com/271)

## 05. 시작하기
**1. 프로젝트 불러오기**
``` bash
  git clone https://github.com/lsh2613/Redisson.git <원하는 경로>
  cd <원하는 경로>
```

**2. 도커 컴포즈 실행**<br>
``` bash
  docker-compose up --build -d
```

**3. 테스트 코드 실행**
``` bash
  ./gradlew test --tests "com.redisson.*"
```

## 06. 동시성 이슈 해결 성공
### 테스트 설명
- Ticket의 quantity(수량)은 1000개 할당
- 100개의 요청을 32개의 스레드로 처리
- 하나의 요청은 생성한 티켓에서 수량을 1개 감소하도록 적용

### 동시성 이슈 해결 성공
![image](https://github.com/user-attachments/assets/f4066763-58d3-43b8-93e9-0425ca0857b9)
![image](https://github.com/user-attachments/assets/4f8b207b-d61e-403a-bfb4-354df909a5b8)
![image](https://github.com/user-attachments/assets/e3ced8b8-f560-43e9-accf-6d4ad09cf4a5)
 
### 결과
| 락 유형       | 실행 시간 (초) |
|---------------|-----------|
| 낙관적 락    | 21.49 s   |
| 비관적 락    | 0.40 s    |
| 분산락       | 10.03 s   |
- 이 프로젝트는 하나의 공유 자원을 100개의 요청에서 동시에 접근하는 나름 극단적인(?) 특징을 가진다.
- 낙관적 락은 DB에 략을 걸지 않고 애플리케이션에서 커밋 시점에 Version을 체크하여 충돌을 감지하고, 충돌 발생 시 예외 처리(롤백 및 재시도 등)를 진행하기 때문에 좋지 않은 성능을 보인다
- 비관적 락은 반대로 DB에 락을 걸어 다른 스레드에서 읽기 조차 못하기 때문에 비즈니스 로직이 처리되지 않고, 예외 처리도 해줄 필요가 없어 좋은 성능을 보인다
- 분산락(Redisson)은 pub/sub 방식을 통해 공유 자원에 대해 락이 해제되었을 때 알림을 받아 처리하기 때문에, 이러한 통신 코스트로 인해 중간 성능을 보인다
- 이러한 결과는 절대적이지 않고, 상황에 따라 성능이 달라질 수 있다

<br>

## 7. 분산락 성능 개선 결과
### 테스트 설명
- 개선 전/후 비교를 위해 락이 불필요한 영역(sleep(500))을 적용 
- 함수형 프로그래밍을 통해 락이 필요한 부분만 락을 할당하여 성능 향상

**[ 성능 개선 전 ]**
![image](https://github.com/user-attachments/assets/88392ced-3212-4ee7-94e4-75fbc8b3ff3c)

**[ 성능 개선 후 ]**
![image](https://github.com/user-attachments/assets/702d7d4c-9ffd-4b63-9ae7-2fa7909d07ad)

### 결과
| 락 유형 | 실행 시간 (초) |
|------|-----------|
| 개선 전 | 52.71 s   |
| 개선 후 | 2.21 s    |

- 개선 후 약 50초의 성능 향상을 확인할 수 있다
- 이 시간은 락이 100개의 요청에 대해 불필요한 영역(0.5초)을 락을 기다리지 않고 먼저 실행됐음을 의미한다
