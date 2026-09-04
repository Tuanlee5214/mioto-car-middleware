include "mioto_car_struct.thrift"

namespace java thrift

service MiotoCarService {
mioto_car_struct.TLoginResult signup(
        1:required mioto_car_struct.OpHandle  handle
        2:required string                     phone
        3:required string                     pwd
        4:required string                     displayName
        5:required mioto_car_struct.TLoginInfo loginInfo);

    mioto_car_struct.TLoginResult login(
        1:required mioto_car_struct.OpHandle  handle
        2:required string                     phone
        3:required string                     pwd
        4:required mioto_car_struct.TLoginInfo loginInfo);

    i32 logout(
        1:required mioto_car_struct.OpHandle   handle
        2:required mioto_car_struct.TSessionID sessionId);

    /** Resolve a session for request authentication. THE hottest call in the system. */
    mioto_car_struct.TSessionResult getSession(
        1:required mioto_car_struct.OpHandle   handle
        2:required mioto_car_struct.TSessionID sessionId);

    mioto_car_struct.TUserResult getUser(
        1:required mioto_car_struct.OpHandle handle
        2:required mioto_car_struct.TUserID  userId);

    i32 updateUser(
        1:required mioto_car_struct.OpHandle handle
        2:required mioto_car_struct.TUser    user
    );
}