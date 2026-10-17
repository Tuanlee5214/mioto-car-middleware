# mioto-car-middleware

Service nghiệp vụ của Mioto Car - chạy Thrift server chứ không phải REST. Đây là chỗ duy nhất được chạm vào MySQL, service khác như `mioto-car-webservice` muốn lấy dữ liệu đều phải gọi qua Thrift, không được query thẳng vào DB.

## Luồng chạy

```
Thrift client (webservice...)
      │  binary protocol qua TCP
      ▼
ThriftServer (TThreadPoolServer)
      │
CarServiceHandler implements MiotoCarService.Iface
      │   (mọi method catch Exception, log lại, trả Err.FAIL - không ném raw exception ra ngoài)
      ▼
*Model (nghiệp vụ, cache, check cross-entity)
      │
*Dao (SQL thuần, PreparedStatement)
      │
MysqlClient → SimpleConnectionPool → MySQL
```

`SimpleConnectionPool` (`src/db/`) là connection pool tự viết, có timeout khi hết slot, claim slot trước khi mở connection thật để không giữ lock lúc đang làm I/O, tự bỏ connection chết. Có test stress nhiều thread trong `test/db/SimpleConnectionPoolTest.java`.

`SimpleCache<K,V>` (`src/cache/`) là cache LRU + TTL in-memory, dùng ở tầng Model cho mấy entity đọc nhiều ghi ít kiểu voucher, fee-policy, car-unavail.

`genjava/` là code Thrift tự generate, đừng sửa tay trong đó - muốn đổi API thì sửa file `.thrift` rồi generate lại.

## Cần gì để chạy

Cần JDK 8 trở lên, MySQL đã tạo sẵn schema, và Thrift compiler nếu có sửa file `.thrift`. Project tạo bằng NetBeans nhưng build bằng `ant` là đủ, không cần mở IDE.

## Config

Repo chưa có sẵn `conf/`, cần tự tạo 2 file trước khi chạy.

`conf/development.log4j.ini`:
```ini
log4j.rootLogger=INFO,STDOUT
log4j.appender.STDOUT=org.apache.log4j.ConsoleAppender
log4j.appender.STDOUT.layout=org.apache.log4j.PatternLayout
log4j.appender.STDOUT.layout.ConversionPattern=%5p [%t] %d{HH:mm:ss} %C{1}:%L - %m%n
```

`conf/development.ini`:
```ini
[SimpleConnectionPool@mioto]
driver=com.mysql.cj.jdbc.Driver
host=127.0.0.1:3306
dbname=mioto_car
uname=root
pwd=
max_conn=4
conn-timeout=5000
wait-timeout=3000

[SimpleCache@common]
max-size=500
ttl-seconds=60

[SimpleCache@ratelimit]
max-size=500
ttl-seconds=60

[ThriftServer@thrift]
port=10101
num-thread=4
```

TTL của `SimpleCache@ratelimit` nên để lớn hơn hoặc bằng window mà `RateLimiter.allow()` bên webservice dùng, không thì counter bị cache tự xoá trước khi hết window, coi như rate limit reset sớm hơn dự tính.

## Chạy

```bash
ant build
java -cp "build/classes:lib/*" -Dappenv=development -Dconf=conf app.EServiceDeamon
```

Mặc định port 10101, `TBinaryProtocol` + `TThreadPoolServer`.

## Cấu trúc

```
src/
├── app/            # ThriftServer, EServiceDeamon (entry point)
├── thrift/handler/ # CarServiceHandler - implement interface sinh từ .thrift
├── model/          # nghiệp vụ, mỗi entity một *Model singleton (.Instance)
├── dao/            # SQL thuần, không có logic nghiệp vụ trong này
├── db/             # SimpleConnectionPool, MysqlClient
├── cache/          # SimpleCache
├── genjava/        # code Thrift tự sinh, đừng sửa
├── util/           # Config, PwdUtil
└── error/          # Err, ValueResult<T>
test/               # unit test + stress test connection pool
thrift/             # file .thrift, nguồn chân lý của API
```

## Đang biết nhưng chưa sửa

`PwdUtil.hash()` dùng SHA-256 + salt, chưa có work factor nên vẫn yếu trước brute-force GPU dù đã salt. Nên đổi sang BCrypt hoặc Argon2.

Rate limit phụ thuộc TTL của cache phải khớp với window truyền vào `allow()` - hai cái này đang cấu hình độc lập, dễ lệch nhau khi sửa config mà quên sửa cả hai.

Chưa có check overlap thời gian khi tạo `CarUnavails` mới cho cùng một xe.
