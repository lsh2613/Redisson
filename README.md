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

## 06. 결과
> **테스트 설명**
> - Ticket의 quantity(수량)은 1000개 할당
> - 100개의 요청을 32개의 스레드로 처리
> - 하나의 요청은 생성한 티켓에서 수량을 1개 감소하도록 적용

### **[ 동시성 이슈 해결 성공 ]**
![image](https://github.com/user-attachments/assets/f4066763-58d3-43b8-93e9-0425ca0857b9)
![image](https://github.com/user-attachments/assets/4f8b207b-d61e-403a-bfb4-354df909a5b8)
![image](https://github.com/user-attachments/assets/e3ced8b8-f560-43e9-accf-6d4ad09cf4a5)
> **[ 결과 ]**
> 
>| 락 유형       | 실행 시간 (초) |
>|---------------|-----------|
>| 낙관적 락    | 21.49 s   |
>| 비관적 락    | 0.40 s    |
>| 분산락       | 10.03 s   |
> 
<br>

### > **분산락 성능 개선**
> - 개선 전/후 비교를 위해 락이 불필요한 영역(sleep(500))을 적용 
> - 함수형 프로그래밍을 통해 락이 필요한 부분만 락을 할당하여 성능 향상

**[ 성능 개선 전 ]**
![image](https://github.com/user-attachments/assets/88392ced-3212-4ee7-94e4-75fbc8b3ff3c)

**[ 성능 개선 후 ]**
![image](https://github.com/user-attachments/assets/702d7d4c-9ffd-4b63-9ae7-2fa7909d07ad)

> **[ 결과 ]**
> 
> - 개선 후 50초의 성능 향상을 확인할 수 있다
> - 이 시간은 락이 100개의 요청에 대해 불필요한 영역(0.5초)을 락을 기다리지 않고 먼저 실행됐음을 의미한다
