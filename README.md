# 스프링 배치 

## 스프링 배치 기본 구조
----
=> 스프링 배치에 공통적으로 정의된 시나리오는 다음과 같다.
  + Read (가져와서) : 원하는 조건의 데이터 레코드를 DB에서 읽어옵니다.
  + Processing (처리하고) : 읽어온 데이터를 비즈니스 로직을 따라 처리한다.
  + Write (저장한다) : 처리된 데이터를 DB에 업데이트(저장)한다.
  + "가져와서" "처리하고" "저장한다"

![image](https://user-images.githubusercontent.com/76584547/116872774-73334380-ac51-11eb-8fab-bcaccc3e3303.png)

+ ItemReader  : 배치 데이터를 읽어오는 인터페이스. DB뿐만 아니라 File, XML 등 다양한 타입에서 읽어올 수 있다.
+ ItemProcessor : 읽어온 데이터를 가공/처리한다. 즉 비즈니스 로직을 처리한다.
+ ItemWriter : 처리한 데이터를 DB(또는 파일)에 저장한다.

### chunk
----
  + 단순하게 ItemReader에서 반환된 모든 데이터를 한꺼번에 받아서 처리하면 될까요? 만약 읽어온 데이터가 1만건, 아니 100만 건이 될 수도 있을 텐데요. 그렇게 많은 양의 데이터가 한꺼번에 메모리에 올려지면 실제 서비스에 어떤 영향을 주게 될까요?네 스프링부트 배치는 한 번의 트랜잭션 안에 처리(commit)되는 수를 정의할 수 있습니다. 그 역할을 하는 것이 바로 chunk라는 단위이지요. 이것은 스프링부트프레임의 가장 핵심적인 키워드라 잠시 후 다시 설명드리도록 하겠습니다.

### 배치 가상 프로세스
```
1. 데이터베이스에서 우리 서비스요금이 청구된 지 30일이 지난 고객 리스트를 읽어온다.  (ItemReader)

2. 1번에서 가져온 고객리스트들을 대상으로 다음의 비즈니스를 처리한다.  (ItemProcessor)
  - 각 고객정보의 카드정보를 활용하여 자동결제를 시도한다.
  - 자동결제를 시도하여 미납금액 결제을 정상적으로 처리된 고객은 요금 청구 완료상태로 전환한다.
  - 자동결제를 시도하여 미납금액 결제를 실패로 처리된 고객은 서비스구독 이용정지상태로 전환한다.
  - 자동결제가 성공한 고객에게는 결제가 완료됐다는 안내메시지를 등록된 휴대폰 정보를 통해 SMS로 발송한다.
  - 자동결제가 실패한 고객에게는 서비스이용정지 안내 메시지를 등록된 휴대폰 정보를 통해 SMS로 발송한다.

3. 2번에서 처리된 고객리스트들를 저장한다. (ItemWriter)
```


### Step
----
  + 기본구조(읽고, 처리하고, 저장한다.)는 Step이라는 객체에서 정의됩니다. 1개의 Step은 읽고, 처리하고 저장하는 구조를 가지고 있는 가장 실질적인 배치처리를 담당하는 도메인 객체입니다. 그리고 이 Step은 한 개 혹은 여러 개가 이루어 Job을 표현합니다. 그리고 1개의 Step은 위에서 설명한 것과 같이 ItemReader(읽고), ItemProcessor(처리하고), ItemWriter(저장하고)를 정의합니다.

### Job
----
  + 한개 혹은 여러 개의 Step을 이루어 하나의 단위로 만들어 표현한 객체입니다. 스프링 부트 배치 처리에 가장 윗 계층에 있는 객체이죠. Job 객체는 JobBuilderFactory 클래스에서 Job을 생성할 수 있습니다. 더 정확하게 표현하자면 JobBuilderFactory에서 생성된 JobBuilder를 통해 Job을 생성할 수 있습니다.

### Job vs Job Instance vs Job Execution
----
```
  - Job : simple Job
  - Job Instance : Job Parameter를 20210504로 실행한 simple Job (Job Parameter 단위로 생성)
  - Job Execution : Job Parameter를 20210504로 실행한 simple Job 1번째 시도 or 다음 번 시도
    => Job Instance 실행의 결과를 담고 있고 성공(COMPLETED)과 실패(FAILED)를 가지고 있다.
      ※ 실패 후 다시 시도할 수 있지만 성공 후 다시 시작할 수 없다.
```


### Next
----
![image](https://user-images.githubusercontent.com/76584547/117094725-7f2d1b80-ad9f-11eb-893f-ef9e2af4b96c.png)
  + Job 내부의 Step들간에 순서 혹은 처리 흐름을 제어
  + 순차적으로 Step들 연결시킬때 사용


### 특정 배치만 실행
----
![image](https://user-images.githubusercontent.com/76584547/117094765-a08e0780-ad9f-11eb-9ad7-56a18ea1c396.png)
  + 환경 변수 job.name과 일치하는 job만 실행시킨다. 
  + ${job.name:NONE} job.name : 우측으로 NONE이 있는데 job.name과 일치하지 않는 배치는 실행하지 않는다는 것이다.

![image](https://user-images.githubusercontent.com/76584547/117096104-0a5be080-ada3-11eb-8b29-139c1682d7bc.png)
  + step1 실패 시나리오: step1 -> step3
  + step1 성공 시나리오: step1 -> step2 -> step3
  + .on()
    + 캐치할 ExitStatus 지정
    + "*" 일 경우 모든 ExitStatus가 지정된다. ※중요!!!! on이 캐치하는 상태값이 BatchStatus가 아닌 ExitStatus라는 점
  + .to()
    + 다음응로 이동할 Step 지정
  + .from()
    + 일종의 이벤트 리스터 역할
    + 상태 값을 보고 일치하는 상태라면 to()에 포함된 step을 호출
    + step1의 이벤트 캐치가 FAILED로 되어있는 상태에서 추가로 이벤트 캐치하려면 from을 써야만 한다.  
  + end()
    + end는 FlowBuilder를 반환하는 end와 FlowBuilder를 종료하는 end 2개가 있음
    + on("*") 뒤에 있는 end는 FlowBuilder를 반환하는 end
    + build() 앞에 있는 end는 FlowBuilder를 종료하는 end
    + FlowBuilder를 반환하는 end 사용시 계속해서 from을 이어갈 수 있음 

### Decide
----
![image](https://user-images.githubusercontent.com/76584547/117108311-f1acf400-adbd-11eb-8123-1adb9a8e58e9.png)
![image](https://user-images.githubusercontent.com/76584547/117108332-fa9dc580-adbd-11eb-951d-c081249ebe6d.png)

  + Step의 결과에 따라 분기를 할 경우 2가지 문제가 생김
    + Step이 담당하는 역할이 2개 이상이 됩니다.
      + 실제 해당 Step이 처리해야할 로직외에도 분기처리를 시키기 위해 ExitStatus 조작이 필요합니다.
    + 다양한 분기 로직 처리의 어려움
      + ExitStatus를 커스텀하게 고치기 위해선 Listener를 생성하고 Job Flow에 등록하는 등 번거로움이 존재합니다.
    + Decide는 Spring Batch에서는 Step들의 Flow속에서 분기만 담당하는 타입이 있습니다.

### ItemStream 인터페이스
----
  + ItemReader의 주기적으로 상태를 저장하고 오류가 발생하면 해당 상태에서 복원하기 위한 마커 인터페이스입니다.
  + 즉, 배치 프로세스의 실행 컨텍스트와 연계해서 ItemReader의 상태를 저장하고 실패한 곳에서 다시 실행할 수 있게 해주는 역할을 합니다.


### CursorItemReader
----
  + Cursor 방식은 Database와 커넥션을 맺은 후, Cursor를 한칸씩 옮기면서 지속적으로 데이터를 빨아옵니다.
  + CursorItemReader는 Paging과 다르게 Streaming 으로 데이터를 처리합니다.
  + 쉽게 생각하시면 Database와 어플리케이션 사이에 통로를 하나 연결하고 하나씩 빨아들인다고 생각하시면 됩니다.
  + Jpa에는 CursorItemReader가 없습니다.


### PagingItemReader
----
  + Database Cursor를 사용하는 대신 여러 쿼리를 실행하여 각 쿼리가 결과의 일부를 가져 오는 방법도 있습니다.
  + 각 페이지마다 새로운 쿼리를 실행하므로 페이징시 결과를 정렬하는 것이 중요합니다.
  + 데이터 결과의 순서가 보장될 수 있도록 order by가 권장됩니다.
  + CursorItemReader와 설정이 크게 다른것은 바로 쿼리 (createQueryProvider())입니다.
  + 각 Database에는 Paging을 지원하는 자체적인 전략들이 있습니다.
    + 로컬에서 h2를 사용하다가 운영서버는 Mysql을 사용할 경우 


### JpaPagingItemReader
----
![image](https://user-images.githubusercontent.com/76584547/117148572-4f0e6880-adf1-11eb-9e24-c8a4adba241c.png)
![image](https://user-images.githubusercontent.com/76584547/117148652-651c2900-adf1-11eb-966b-cb4adca9378a.png)

  + Spring Batch 역시 JPA를 지원하기 위해 JpaPagingItemReader를 공식적으로 지원하고 있습니다.
    + 현재 Querydsl, Jooq 등을 통한 ItemReader 구현체는 공식 지원하지 않습니다.
    + CustomItemReader 구현체를 만드셔야만 합니다.
    + JPA는 Hibernate와 많은 유사점을 가지고 있습니다만, 한가지 다른 것이 있다면 Hibernate 에선 Cursor가 지원되지만 JPA에는 Cursor 기반 Database 접근을 지원하지 않습니다.
    + EntityManagerFactory를 지정하는 것 외에 JdbcPagingItemReader와 크게 다른 점은 없습니다.

### PagingItemReader 주의 사항
----
  + 정렬 (Order) 가 무조건 포함되어 있어야 합니다.

### ItemReader 주의 사항
-----
  + JpaRepository를 ListItemReader, QueueItemReader에 사용하면 안됩니다.
    + 간혹 JPA의 조회 쿼리를 쉽게 구현하기 위해 JpaRepository를 이용해서 new ListItemReader<>(jpaRepository.findByAge(age)) 로 Reader를 구현하는 분들을 종종 봅니다.
    + 이렇게 할 경우 Spring Batch의 장점인 페이징 & Cursor 구현이 없어 대규모 데이터 처리가 불가능합니다. (물론 Chunk 단위 트랜잭션은 됩니다.)
    + 만약 정말 JpaRepository를 써야 하신다면 RepositoryItemReader를 사용하시는 것을 추천합니다.
      + Paging을 기본적으로 지원합니다.
      + 예제 코드 : https://stackoverflow.com/questions/43003266/spring-batch-with-spring-data/43986718#43986718
    + Hibernate, JPA 등 영속성 컨텍스트가 필요한 Reader 사용시 fetchSize와 ChunkSize는 같은 값을 유지해야 합니다.


### Database ItemWriter
-----
  + Writer는 Chunk단위의 마지막 단계
    +  그래서 Database의 영속성과 관련해서는 항상 마지막에 Flush를 해줘야만 합니다.
    +  예를 들어 아래와 같이 영속성을 사용하는 JPA, Hibernate의 경우 ItemWriter 구현체에서는 flush()와 session.clear()가 따라옵니다.
  + 3가지 존재
    + JdbcBatchItemWriter
    + HibernateItemWriter
    + JpaItemWriter
  + JdbcBatchItemWriter
    + ChunkSize만큼 쿼리를 모아놨다가 ChunkSize만큼 쌓이면 모아놓은 쿼리 DB에 전송
      + 이렇게 처리하는 이유는 어플리케이션과 데이터베이스 간에 데이터를 주고 받는 회수를 최소화 하여 성능 향상을 꾀하기 위함입니다. 
  + JpaItemWriter
    +  JpaItemWriter는 JPA를 사용하기 때문에 영속성 관리를 위해 EntityManager를 할당해줘야 합니다.
    +  일반적으로 spring-boot-starter-data-jpa를 의존성에 등록하면 Entity Manager가 Bean으로 자동생성되어 DI 코드만 추가해주시면 됩니다.
  + Custom ItemWriter
    + Reader와 달리 Writer의 경우 Custom하게 구현해야할 일이 많습니다.
      + Reader에서 읽어온 데이터를 RestTemplate으로 외부 API로 전달해야할때
      + 임시저장을 하고 비교하기 위해 싱글톤 객체에 값을 넣어야할때
      + 여러 Entity를 동시에 save 해야할때

### ItemProcessor
-----
![image](https://user-images.githubusercontent.com/76584547/117323613-037bbd80-aeca-11eb-912c-1096c9fce56e.png)

  + ItemProcessor는 데이터를 가공하거나 필터링하는 역할 / 필수가 아니다.
  + ItemProcessor를 쓰는 것은 Reader, Writer 와는 별도의 단계로 분리되었기 때문에 비지니스 코드가 섞이는 것을 방지
  + ItemProcessor<I, O> I : ItemReader에서 받을 데이터 타입 , O : ItemWriter에 보낼 데이터 타입
    + 자바8 부터는 인터페이스의 추상 메소드가 1개일 경우 람다식을 사용할 수 있습니다. ItemProcessor 역시 process 만 있기 때문에 람다식을 사용할 수 있습니다.
      그래서 많은 배치들이 ItemProcessor를 다음과 같이 익명 클래스 혹은 람다식을 자주 사용합니다. 
  + ItemProcessor 구현체
    +  ItemProcessorAdapter
    +  ValidatingItemProcessor
    +  CompositeItemProcessor
      +  하지만 최근에는 대부분 Processor 구현을 직접 구현할때가 많고, 여차하면 람다식으로 빠르게 구현할때도 많습니다.
      +  그래서 ItemProcessorAdapter, ValidatingItemProcessor는 거의 사용하지 않습니다. 이들의 역할은 커스텀하게 직접 구현해도 되기 때문입니다.
      +  다만, CompositeItemProcessor는 간혹 필요할때가 있기 때문에 소개드립니다.
      +  CompositeItemProcessor는 ItemProcessor간의 체이닝을 지원하는 Processor라고 보시면 됩니다.
      +  필터가 2번 필요할 때 사용할 수 있다 (Processor의 역할을 줄이기 위해.)

출처 : https://ahndy84.tistory.com/18

참고 : https://jojoldu.tistory.com/325?category=902551
