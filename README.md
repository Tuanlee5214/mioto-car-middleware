# mioto-car-middleware

Service xử lý business logic của Mioto Car, expose ra ngoài qua Thrift (không phải REST). Đây là chỗ duy nhất trong hệ thống được phép chạm vào MySQL - webservice hay bất kỳ client nào khác đều phải đi qua Thrift client, không query DB trực tiếp.

## Luồng chạy

```
Thrift client (webservice...)
      │  binary protocol qua TCP
      ▼
ThriftServer (TThreadPoolServer)
      │
CarServiceHandler implements MiotoCarService.Iface
      │  (mọi method catch Exception, log lại, trả Err.FAIL - không bao giờ ném raw exception ra ngoài)
      ▼
*Model (nghiệp vụ, cache, check cross-entity)
      │
*Dao (SQL thuần, PreparedStatement)
      │
MysqlClient → SimpleConnectionPool → MySQL
```

Vài chỗ đáng nói:

- `SimpleConnectionPool` (`src/db/`) là connection pool tự viết, có timeout khi hết slot, claim slot trước khi mở connection thật để không giữ lock lúc đang làm I/O, tự bỏ connection chết. Có test stress nhiều thread trong `test/db/SimpleConnectionPoolTest.java`.
- `SimpleCache<K,V>` (`src/cache/`) là cache LRU + TTL in-memory, dùng ở tầng Model cho mấy entity đọc nhiều ghi ít kiểu voucher, fee-policy, car-unavail.
- `genjava/` là code Thrift tự generate, đừng sửa tay trong đó - muốn đổi API thì sửa file `.thrift` rồi generate lại.

## Cần gì để chạy

- JDK 8+
- MySQL với schema đã tạo sẵn
- Thrift compiler nếu cần regenerate code sau khi sửa `.thrift`
- Project dùng NetBeans (`nbproject/`) nhưng build bằng `ant` được luôn, không cần mở IDE

## Config

Repo hiện chưa có sẵn `conf/`, cần tự tạo 2 file trước khi chạy.

`conf/development.log4j.ini`:
```ini
log4j.rootLogger=INFO,STDOUT
log4j.appender.STDOUT=org.apache.log4j.ConsoleAppender
log4j.appender.STDOUT.layout=org.apache.log4j.PatternLayout
log4j.appender.STDOUT.layout.ConversionPattern=%5p [%t] %d{HH:mm:ss} %C{1}:%L - %m%n
```

`conf/development.ini` (format `[Class@name]`, đọc bởi `util.Config`):
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

Lưu ý: TTL của `SimpleCache@ratelimit` nên >= window dùng trong `RateLimiter.allow()` bên webservice, không thì counter bị cache tự xoá trước khi window kết thúc, rate limit coi như reset sớm.

## Chạy

```bash
ant build
java -cp "build/classes:lib/*" -Dappenv=development -Dconf=conf app.EServiceDeamon
```

Mặc định lắng nghe port 10101, `TBinaryProtocol` + `TThreadPoolServer`.

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

- `PwdUtil.hash()` dùng SHA-256 + salt, chưa có work factor nên vẫn yếu trước brute-force GPU dù đã salt. Nên đổi sang BCrypt/Argon2.
- Rate limit phụ thuộc TTL của cache phải khớp với window truyền vào `allow()` - hai cái này đang cấu hình độc lập, dễ lệch khi sửa config mà quên sửa cả hai.
- Chưa check overlap thời gian khi tạo `CarUnavails` mới cho cùng một xe.
