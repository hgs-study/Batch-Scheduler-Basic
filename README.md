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


![image](https://user-images.githubusercontent.com/76584547/117035271-144bf800-ad3f-11eb-92b6-982a78734aac.png)
  

### Next
----
  + Job 내부의 Step들간에 순서 혹은 처리 흐름을 제어
  + 순차적으로 Step들 연결시킬때 사용




출처 : https://ahndy84.tistory.com/18

참고 : https://jojoldu.tistory.com/325?category=902551
