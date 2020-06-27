# kakaopay-sprinkling-money
> 2020 카카오페이 경력 공채 server 개발과제
> 카카오페이 뿌리기 기능 구현하기

## 1. 개발환경
 - JAVA8
 - Spring Boot 2.3.1.RELEASE
 - JPA(Hibernate) - H2(inMemory)
 - Build : Maven
 
## 2. Build & Execute
 - mvn compile && java -jar target/sprinkling-money-executable.war

## 3. Requirement
* 뿌리기, 받기, 조회 기능을 수행하는 REST API 를 구현합니다.
* 요청한 사용자의 식별값은 숫자 형태이며 "X-USER-ID" 라는 HTTP Header를 통해 전
* 요청한 사용자가 속한 대화방의 식별값은 문자 형태이며 "X-ROOM-ID" 라는 HTTP Header로 전달됩니다.
* 모든 사용자는 뿌리기에 충분한 잔액을 보유하고 있다고 가정하여 별도로 잔액에 관련된 체크는 하지 않습니다. 
* 작성하신 어플리케이션이 다수의 서버에 다수의 인스턴스로 동작하더라도 기능에 문제가 없도록 설계되어야 합니다.
* 각 기능 및 제약사항에 대한 단위테스트를 반드시 작성합니다.

## 4. 설계
### DB 설계
![스크린샷 2020-06-27 오전 4 21 34](https://user-images.githubusercontent.com/6982740/85893361-b650a900-b82d-11ea-9a20-eec93cc30abc.png)

## 5. 문제 해결
### 5.1. X-USER-ID, X-ROOM-ID header 처리
> SprinklingMoneyInterceptor, MessageRoomIdArgumentResolver, UserIdArgumentResolver 참조
 - Interceptor을 통해 유효성 검증
 - ArgumentResolver를 통해 값 바인딩

### 5.2. token 생성 규칙
> SprinklingMoneyTokenGenerator 참조
 - sha256 hash algorithm을 통해 생성하여 해시 충돌을 최소화하는 방향으로 개발
 - token생성기는 추후 확장이 가능한 형태로 개발하여 제네릭 메소드로 개발됨.
    - 하지만 현재의 책임은 페이머니 뿌리기에만 한정되어있기 때문에 package-private로 되어있고, 추후 다른곳에서도 공통으로 사용이 필요해지면 common Util 클래스로 변경
 - sha256 hash algorithm을 쓰더라도 source data가 동일하면 항상 같은 값을 반환하기 때문에 랜덤적인 요소를 위해 SALT로 LocalDateTime.now()을 첨가

### 5.3. token 충돌이 발생하는 경우에 처리가 가능한가?
 - token request parameter ( PathVariable ) 로 사용하고 있지만 실제 데이터 조회는 roomId + userId 기반으로 처리하도록 개발을 해서 token 충돌이 발생하더라도 서비스 이슈는 없음.

### 5.4 돈 분할 방식
> RandomMoneyGenerator 참조

#### 5.4.1 ratio 분할
 - 100% ratio를 기준으로 %를 나누는 방식으로 설계
 - N명의 receiver의 요청이 들어온경우 모두가 최소 1%라도 가져가도록 하는 조건을 정해서 처리하려다보니  자연스럽게 제약조건이 생기게 됨.(99명 이상의 receiver인 경우에는 현재로선 기능을 제공하지 않고 필요시 추후 추가 설계 고민
 - receiver수에 따라 동적으로 ratio을 계산하고, 0%가 나오면 재주첨하는 방식으로 설계
 - 여기 totalAmount가 혹시라도 receiver 수보다 작은 경우에 대한 처리 추가
    - 이경우에 누구는 0원을 받을수밖에 없는데 이런 부분에 대해서만 별도 처리
    - 기본적인 전제 조건이 0%가 나오지 않는 것이지만 이 경우에는 허용하여 0원을 받아갈수 있도록 함.

#### 5.4.2 머니 분할
 - ratio 기준으로 값을 처리할때 올림/반올림/버림 중 선택적으로 수행할 수 있는 이 과정을 조금 심플하게 처리하기 위한 고민이 있었음.
 - 만약 올림으로 하는 경우 전체 예산보다 높게 분배되는 케이스가 생길수 있으므로 머리가 아파질수 있음.
 - 처리한 방식은 일단 "버림"으로 모두 처리하고 마지막 receiver가 나머지 차액을 받는 방식으로 처리

### 5.5 선착순 지급 처리
> LockAspect 참조
 - redisLock을 이용하여 처리
 - receive request가 들어오면 redis에 lock에 대한 key를 셋팅하여 처리한다.(이 lock key는 머니뿌리기 번호 기준으로 동적으로 처리)
 - 이 처리 방식은 AOP를 적용하여 핵심로직은 침범하지 않는 부가 기능으로 제공한다.
 - 해당 머니뿌리기 번호에 해당하는 처리가 완료되면 다음 사용자가 머니를 지급받을 수 있다.
 - 이 과정에서 동시다발적으로 request가 들어오는 경우를  고려해야 할텐데, 일단 지급 관련 로직이 매우 무거운 로직은 아니기 때문에 비교적 lock이 걸리고, 데이터를 처리하고, 락이 풀리기까지의 대기시간이 아주 길지는 않을 것으로 예상된다.
 - 여기서 thread가 적제로 인한 위험을 방지하기 위해서는 현재로써는 서버 자원을 적절히 투입하고 앞단의 nginx에서 worker_connections같은 부분에서 성능테스트를 통해  최적화를 진행해야 한다.(배포 직전 확인 후 배포)


## 6. API 정보

### 6.1 기본 응답 정보
> 데이터 타입 : JSON
 
#### 6.1.1 success 응답 정보

| 구분    | 내용             | 비고                             |
| :------ | :--------------- | :--------------------------- |
| http status    | statusCoe | 200           |
| result  | 응답 내용 |                        |

``` json
{
  "result": {
    "token": "dd2"
  }
}
```

#### 6.1.2 failure 응답 정보
| 구분    | 내용             | 비고                             |
| :------ | :--------------- | :--------------------------- |
| http status    | statusCoe |  400, 403, 500             |
| code    | errorCode         | 6.1.3 failure 응답표(내부적으로 쓰이는 code)           |
| message | 메시지           | 실패 : 실패 사유 |

``` json
-- http status 
{
  "code": "4002",
  "message": "유효하지 않은 유저입니다."
}
```

#### 6.1.3 failure 응답표
| error Name | error Code    | http Status    |   description   |
| :-------- | :----------- | :------------ | :------|
| MISSING_PARAMETER    | "4001" |  400  | 필수 파라메터 누락|
| ILLEGAL_ACCESS    | "4002" |  403  | 잘못된 접근(유저나 메시지방 ID 에러)|
| NOT_EXIST    | "5000" |  500  | 존재하지 않는 데이터|
| EXPIRED    | "5001" |  500  | 만료|
| NOT_TARGET    | "5002" |  500  | 대상이 아님|
| ALREADY_DONE    | "5003" |  500  | 이미 완료한 경우|
| DATA_VALIDATION_ERROR    | "5003" |  500  | 데이터 검즈 에러|
| UNKNOWN_ERROR    | "9999" |  500  | 예상하지 못한 에러(SQL Exception 등)|


### 6.2. API LIST

#### 6.2.1. 뿌리기 API

> URL : /money/sprinkling
>
> Method : POST
>

##### Request Parameter ( Body )
> content-type : JSON

| 구분  | Type   | 필수여부 |
| :---- | :----- | :------- |
| receiverCount | int | Y   |
| amount | long | Y        |

##### Request Header
| 구분  | Type   | 필수여부 |  비고   |
| :---- | :----- | :------- |:------- |
| X-USER-ID | String | Y   |  사용자 ID |
| X-ROOM-ID | String | Y   |  메시지방 ID|
 

##### Success Response ( result 이하만 표시)
| 구분  | Type   | 필수여부 |  비고   |
| :---- | :----- | :------- |:------- |
| token | String |   Y   |  뿌리기 token |

``` json
{
  "result": {
    "token": "6a4"
  }
}
```


#### 6.2.2. 받기 API

> URL :/money/sprinkling/receive/{token}
>
> Method : PUT
>

#### Request Parameter ( Path )

| 구분  | Type    | 필수여부               |
| :--- | :------ | :--------------------- |
| token | String | Y       |

##### Request Header
| 구분  | Type   | 필수여부 |  비고   |
| :---- | :----- | :------- |:------- |
| X-USER-ID | String | Y   |  사용자 ID |
| X-ROOM-ID | String | Y   |  메시지방 ID|

##### Success Response ( result 이하만 표시 )
| 구분  | Type   | 필수여부 |  비고   |
| :---- | :----- | :------- |:------- |
| amount | long |   Y   |  지급받은 페이머니 |

``` json
{
  "result": {
    "amount": 4000
  }
}
```

#### 6.2.3. 쿠폰사용

> URL : /money/sprinkling/receive/{token}
>
> Method : GET

#### Request Parameter ( Path )

| 구분  | Type    | 필수여부               |
| :--- | :------ | :--------------------- |
| token | String | Y       |

##### Request Header
| 구분  | Type   | 필수여부 |  비고   |
| :---- | :----- | :------- |:------- |
| X-USER-ID | String | Y   |  사용자 ID |
| X-ROOM-ID | String | Y   |  메시지방 ID|

##### Success Response ( result 이하만 표시 )
| 구분  | Type   | 필수여부 |  비고   |
| :---- | :----- | :------- |:------- |
| sprinklingDate | DateTime |   Y   |  뿌린 |
| totalAmount | long |   Y   |  뿌린 금액 |
| receiveCompletedAmount | long |   Y   |  받기 완료된 금액 |
| receiveCompletedMemberList | Array |   Y   |  받기완료된정보 |
| receiveCompletedMemberList.userId | String |   Y   |  받은 사용자 ID |
| receiveCompletedMemberList.amount | String |   Y   |  받은 금액 |
| receiveCompletedMemberList.receiveDate | DateTime |   Y   |  받은 시간 |

``` json
{
  "result": {
    "sprinklingDate": "2020-06-27T04:04:23.31",
    "totalAmount": 10000,
    "receiveCompletedAmount": 4000,
    "receiveCompletedMemberList": [
      {
        "userId": "5510eae0-cf4c-4488-a6e4-07e25ad030e0",
        "amount": 4000,
        "receiveDate": "2020-06-27T04:12:04.036"
      }
    ]
  }
}

```
