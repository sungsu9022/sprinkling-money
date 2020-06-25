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
### DB


## 5. 문제 해결
- 쿠폰번호 생성은 라이브러리 사용없이 직접 구현
    - 기본 아이디어
        - 쿠폰 코드에 가능한 글자 [0-9a-zA-Z]로, 62(=10+26*2) 글자를 한 String 변수로 만듬
        - 난수를 만들어 위 변수의 index로 부터 쿠폰 코드 한자리를 가져옴
        - 이것을 쿠폰 코드 자리수인 16번을 반복하여 코드를 만듬
    - Index를 고르는 랜덤한 난수가 중요
    - 난수 생성기(Random Number Generator, RNG)        
        - 완벽한 난수 생성기, True RNG는 하드웨어(전자기 소음, 방사선 원소, 원자 물리적 현상)를 사용해야함
        - 그래서 아주 긴 시간이 걸리더라도 결국 반복되는 의사 난수 생성기(Pseudorandom Number Generator, PRNG)가 필요
        - Java의 난수 생성기
            - JCA에서 여러 provider 중에서 결정하여 사용함
                - http://d2.naver.com/helloworld/197937
            - Secure Random library는 암호학적으로 안전한 PRNG(cryptographically secure PRNG, CSPRNG)를 사용
                - 블룸 블룸 슙(BBS)
    - 나이브한 아이디어
        - 하나의 Email에 하나의 쿠폰이 발행되므로, Email을 시드로 사용
        - epoch 타임을 shift 연산하여 시드로 사용
    - 현실
        - 라이브러리를 생성하지 않고 만들어야 함        
        - 나이브한 방법으로 했을 때, 어느 한 입력값으로부터 쿠폰 코드가 유추된다면?
            - 문제 없음: (공돌이 생각) 어차피 이메일에 하나의 쿠폰이 발급된다면, 쿠폰 사용 시 이메일 검사를 할 것이다.
            - 문제 있음: 기존 이메일 정보를 활용할 수 없는 자회사의 새로운 서비스 프로모션이거나 혹은 제휴사, 오프라인에서 제휴하는 이벤트라면 이메일로 검증을 할 수 없다.
        - WELL이나 메르센 트위스트(CSPRNG는 아니지만)을 구현하는게 맞다고 생각됨
        - 결론: WELL512와 Random seed를 위한 LGC 구현


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