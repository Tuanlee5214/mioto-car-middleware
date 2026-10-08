namespace java thrift

typedef i32 TUserID
typedef i64 TSessionID
    
enum TUserStatus {
    TUS_NULL    = 0,
    TUS_ACTIVE  = 1,
    TUS_LOCKED  = 2,
    TUS_REMOVED = 3,
}

enum TTransmission {
    T_AT = 1,
    T_MT = 2,
}

enum TTypeFuel {
    T_GAS      = 1,
    T_DIESEL   = 2,
    T_ELECTRIC = 3,
}

enum TCarStatus {
    TC_ACTIVE  = 1, 
    TC_PENDING = 2, 
    TC_BLOCKED = 3,
}

enum TBookingStatus {
    TB_PENDING      = 1,
    TB_CONFIRMED    = 2,
    TB_IN_PROGRESS  = 3,
    TB_COMPLETED    = 4,
    TB_CANCELLED    = 5,
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

struct TCar {
    1 :optional i32           carId,
    2 :optional string        carName,
    3 :optional i32           productionYear,
    4 :optional i32           carBrandId,
    5 :optional byte          numSeats,
    6 :optional i32           transmission,
    7 :optional i32           typeFuel,
    8 :optional string        fuelConsumption,   
    9 :optional string        description,
    10:optional string        pricePerDay,       
    11:optional string        policy,
    12:optional i32           districtId,
    13:optional TUserID       userId,            
    14:optional i32           status,
    20:optional i64           createdAt,
    21:optional i64           updatedAt,
}

struct TCarFilterRequest {
    1 :optional string        carName,
    2 :optional i32           productionYear,
    3 :optional i32           carBrandId,
    4 :optional byte          numSeats,
    5 :optional TTransmission transmission,
    6 :optional TTypeFuel     typeFuel,
    7 :optional string        fuelConsumption,
    9 :optional string        pricePerDay,
    11:optional i32           districtId,
    12:optional i32           provinceId,
    
}

struct TCarResult {
    1:required i32   error,
    2:required string message,
    3:optional TCar   value,
}

struct TCarFeature {
    1:optional i32 id,
    2:optional i32 carId,
    3:optional i32 featureId,
}

struct TCarDetail {
    1:optional TCarView              carView,
    2:optional list<TCarImage>       images,
    3:optional list<TCarFeatureView> features,
    4:optional list<TCarUnavails>    unavails,
}

struct TCarDetailResult {
    1:required i32        error,
    2:required string     message,
    3:optional TCarDetail value,
}

struct TListCarViewResult {
    1:required i32            error,
    2:required string         message,
    3:optional list<TCarView> value,
}

struct TListCarFeatureViewResult {
    1:required i32                   error,
    2:required string                message,
    3:optional list<TCarFeatureView> value,
}

struct TCarView {
  1: TCar car,
  2: optional string brandName,
  3: optional string districtName,
  4: optional string provinceName,
  5: optional string ownerName,
  6: optional string thumbnailUrl,
}

struct TCarFeatureView {
    1:required i32       id,
    2:required i32       carId,
    3:required i32       featureId,
    4:required string    featureName,
}

struct TCarImage {
    1:optional i32    id,
    2:optional i32    carId,
    3:optional string imageUrl,
    4:optional string publicId,
    5:optional i32    orderNum,
    20:optional i64   createdAt,
}

struct TCarImageResult {
    1:required i32     error,
    2:required string  message,
    3:optional TCarImage value,
}

struct TListCarImageResult {
    1:required i32            error,
    2:required string         message,
    3:optional list<TCarImage> value,
}

struct TCarUnavails {
    1:optional i32  id,
    2:optional i32  carId,
    3:optional i64  startTime,
    4:optional i64  endTime,
    20:optional i64 createdAt,
    21:optional i64 updatedAt,
}

struct TCarUnavailsResult {
    1:required i32        error,
    2:required string     message,
    3:optional TCarUnavails value,
}

struct TListCarUnavailsResult {
    1:required i32               error,
    2:required string            message,
    3:optional list<TCarUnavails> value,
}

struct TSignUpRequest {
    1:required string phone,
    2:required string pwd,
    3:required string displayName,
    4:optional string email,
}

struct TFeePolicy {
    1:optional i32    feePolicyId,
    2:optional string name,
    3:optional string percentFee,
    4:optional bool   isActive,
}

struct TFeePolicyRequest {
    1:required string name,
    2:required i32    percentFee,
    3:required bool   isActive,
}

struct TFeePolicyResult { 
    1:required i32        error,
    2:required string     message,
    3:optional TFeePolicy value,
}

struct TListFeePolicyResult {
    1:required i32        error,
    2:required string     message,
    3:optional list<TFeePolicy> value,
}

struct TProvince {
    1:optional i32    provinceId,
    2:optional string provinceName,
}

struct TProvinceRequest {
    1:required string provinceName
}

struct TProvinceResult { 
    1:required i32       error,
    2:required string    message,
    3:optional TProvince value,
}

struct TListProvinceResult {
    1:required i32       error,
    2:required string    message,
    3:optional list<TProvince> value,
}

struct TDistrict {
    1:optional i32    districtId,
    2:optional string districtName,
    3:optional i32    provinceId,
}

struct TDistrictRequest {
    1:required string districtName,
    2:required i32    provinceId,
}

struct TDistrictResult {
    1:required i32       error,
    2:required string    message,
    3:optional TDistrict value,
}

struct TListDistrictResult {
    1:required i32       error,
    2:required string    message,
    3:optional list<TDistrict> value,
}

struct TCarBrand {
    1 :optional i32    carBrandId,
    2 :optional string nameBrand,
    20:optional i64    createdAt,
    21:optional i64    updatedAt,
}

struct TCarBrandRequest {
    1:required string nameBrand,
}

struct TCarBrandResult {
    1:required i32  error,
    2:required string message,
    3:optional TCarBrand value,
}

struct TListCarBrandResult {
    1:required i32  error,
    2:required string message,
    3:optional list<TCarBrand> value,
}

struct TFeature {
    1 :optional i32    featureId,
    2 :optional string nameFeature,
    20:optional i64    createdAt,
    21:optional i64    updatedAt,
}

struct TFeatureRequest {                                                                                                                                                                                                                                                                                                                                                                                                                                               
    1:required string nameFeature,
}

struct TFeatureResult {
    1:required i32  error,
    2:required string message,
    3:optional TFeature value,
}

struct TListFeatureResult {
    1:required i32  error,
    2:required string message,
    3:optional list<TFeature> value,
}

struct TFeedBack {
    1:optional  i32  feedbackId, 
    2:optional  string comment,
    3:optional  i32  senderId,
    4:optional  i32  pointStar,
    5:optional  i32  receiverId, 
    20:optional i64  createdAt,
    21:optional i64  updatedAt,
}

struct TFeedBackRequest {
    1:required  string comment, 
    2:required  i32    senderId,
    3:required  i32    pointStar,
    4:required  i32    receiverId,
}

struct TFeedBackResult { 
    1:required  i32    error,
    2:required  string message,
    3:optional  TFeedBack value,
}

struct TListFeedBackResult {
    1:required  i32    error,
    2:required  string message,
    3:optional  list<TFeedBack> value,
}

struct TVoucher {
    1 :optional i32     voucherId,
    2 :optional string  title,
    3 :optional string  code,
    4 :optional string  imageUrl,
    5 :optional string  publicId,
    6 :optional string  body,
    7 :optional i32     discountPercent,
    8 :optional i64     maxDiscount,
    9 :optional i64     startDate, 
    10:optional i64     endDate, 
    20:optional i64     createdAt,
}

struct TVoucherRequest {
    1 :required string  title,
    2 :required string  code, 
    3 :required string  body, 
    4 :required i32     discountPercent,
    5 :required i64     maxDiscount,
    6 :required i64     startDate, 
    7 :required i64     endDate, 
    8 :required string  imageUrl,   
    9 :required string  publicId, 
}

struct TVoucherResult {
    1 :required i32      error, 
    2 :required string   message, 
    3 :optional TVoucher value
}

struct TListVoucherResult {
    1 :required i32      error, 
    2 :required string   message, 
    3 :optional list<TVoucher> value
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
    3:optional TUser   value,
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

struct TRole {
    1:optional i32 roleId, 
    2:optional string name,
}

struct TRoleResult {
    1:required i32 error,
    2:required string message, 
    3:optional TRole value 
}

struct TListRoleResult { 
    1:required i32 error, 
    2:required string message, 
    3:optional list<TRole> value 
}

struct TUserRole {
    1:optional i32 id,
    2:optional i32 userId,
    3:optional list<i32> roleIds,
    4:optional bool isSuperAdmin,
    5:optional list<TRole> roles,    
}

struct TUserRoleResult { 
    1:required i32 error, 
    2:required string message, 
    3:optional TUserRole value 
}
