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

struct TSignUpRequest {
    1:required string phone,
    2:required string pwd,
    3:required string displayName,
    4:optional string email,
}

struct TLoginRequest {
    1:required string phone,
    2:required string pwd,
}

struct TUserPwd {
    1:optional TUserID userId,
    2:optional string  pwdHash,
    3:optional string  salt,
    20:optional i64     timeUpdated,
}   

struct TLoginInfo {
    1:optional string userAgent,
    2:optional string userIP,
    3:optional bool   longSession,
}

struct TSession {
    1:optional TSessionID sessionId,
    2:optional TUserID    userId,
    3:optional string     userAgent,
    4:optional string     userIP,
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
    2:required string message,
    3:optional TUser value,
}

struct TUserPwdResult {
    1:required i32   error,
    2:required string message,
    3:optional TUserPwd value,
}

struct TLogoutResult {
    1:required i32   error,
    2:required string message,
}

struct TUpdateUserResult {
    1:required i32   error,
    2:required string message,
    3:optional i32   value,
}

struct TLoginResult {
    1:required i32        error,
    2:required string     message,
    3:optional TUser      user,
    4:optional TSessionID sessionId,
    5:optional i64        timeExpired,
}

struct TSessionResult {
    1:required i32      error,
    2:required string   message,
    3:optional TSession value,
}
