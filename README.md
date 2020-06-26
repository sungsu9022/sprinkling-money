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

### 5.4 뿌려진 돈 분할 방식
> SprinklingMoneyDivider 참조
> 개인적으로 이 부분에서 가장 애를 먹었습니다..(아무렇게나 해도 된다고 하는게 더 어렵네요.)

#### 5.4.1 ratio 분할
 - 100% ratio를 기준으로 %를 나누는 방식으로 설계
 - N명의 receiver의 요청이 들어온경우 모두가 최소 1%라도 가져갈수 있는 조건을 정해서 개발했는데 이러다보니 자연스럽게 제약조건이 생기게 됨.(99명 이상으로는 ratio 분할을 할수 없음)
 - receiver수에 따라 동적으로 ratio을 계산하고, 0%가 나오면 재주첨하는 방식으로 설계

#### 5.4.2 머니 분할
 - 요청으로 들어온 totalAmount에서 비율을 기준으로 값을 나누려면 올림/반올림/버림 중 선택적으로 수행해야 하는데 이 과정을 조금 심플하게 처리하기 위한 고민
 - 심플하게 처리하려면 일단 "버림"으로 모두 처리하고 마지막 인덱스에 있는 머니에 차액을 더하도록 처리

#### 5.5 선착순 처리
 - 과제 스펙 중 "대화방에 있는 다른 사용자들은 위에 발송된 메세지를 클릭하여 금액을 무작위로 받아가게 됩니다" 를 재대로 못보고 개발을 잘못한 부분이 있습니다.
 - 이 부분에 대한 처리에 대한 설계는 일단 redis Lock을 이용해서 처리하는 방향으로 설계했습니다.
 - 받기 API 호출시 redis에 돈뿌리기 번호 기준으로 lock을 걸고 자신에게 받기를 할당한 후 받기 작업이 완료되면 lock을 해제하는 방식으로 진행합니다.
 - 이 과정에서 동시다발적으로 request가 들어올수 있을텐데 이 경우에는 lock이 풀렸는지 체크하면서 대기하도록 합니다.(여기서 thread가 적제되는 위험이 있는데 이건 서버 자원을 적절히 투입해서 대응해야 하는 부분입니다.)
 - lock 체크 loop에서 lock이 풀리면 receiver수가 모두 찼는지 확인하고 비어있으면 다시 lock을 걸고 머니를 할당하는 방식으로 처리합니다.
 - 이 부분은 미쳐 개발하지는 못한 부분입니다..ㅠ

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