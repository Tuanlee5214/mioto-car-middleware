namespace java thrift

typedef i32 TUserID
typedef i64 TSessionID

enum TUserStatus {
    TUS_NULL    = 0,
    TUS_ACTIVE  = 1,
    TUS_LOCKED  = 2,
    TUS_REMOVED = 3,
}

struct TUser {
    1:optional TUserID userId,
    2:optional string  phone,
    3:optional string  email,
    4:optional string  displayName,
    20:optional byte   status,
    21:optional i64    timeCreated,
    22:optional i64    timeUpdated,
}

struct TLoginInfo {
    1:optional string userAgent,
    2:optional string userIP,
    3:optional bool   longSession,
}

struct TSession {
    1:optional TSessionID sessionId,
    2:optional TUserID    userId,
    20:optional i64       timeCreated,
    21:optional i64       timeExpired,
}

struct OpHandle {
    1:required string source,        // which client is calling
    2:optional string appName,
    3:optional string ip,
}

struct TUserResult {
    1:required i32   error,
    2:optional TUser value,
}

struct TLoginResult {
    1:required i32        error,
    2:optional TUser      user,
    3:optional TSessionID sessionId,
    4:optional i64        timeExpired,
}

struct TSessionResult {
    1:required i32      error,
    2:optional TSession value,
}
