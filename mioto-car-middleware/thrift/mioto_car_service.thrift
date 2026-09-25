include "mioto_car_struct.thrift"

namespace java thrift

service MiotoCarService {
    mioto_car_struct.TLoginResult signup(
        1:required mioto_car_struct.OpHandle  handle
        2:required mioto_car_struct.TSignUpRequest request
        3:required mioto_car_struct.TLoginInfo loginInfo);

    mioto_car_struct.TLoginResult login(
        1:required mioto_car_struct.OpHandle  handle
        2:required mioto_car_struct.TLoginRequest request
        3:required mioto_car_struct.TLoginInfo loginInfo);

    mioto_car_struct.TLogoutResult logout(
        1:required mioto_car_struct.OpHandle   handle
        2:required mioto_car_struct.TSessionID sessionId);

    /** Resolve a session for request authentication. THE hottest call in the system. */
    mioto_car_struct.TSessionResult getSession(
        1:required mioto_car_struct.OpHandle   handle
        2:required mioto_car_struct.TSessionID sessionId);

    mioto_car_struct.TUserResult getUser(
        1:required mioto_car_struct.OpHandle handle
        2:required mioto_car_struct.TUserID  userId);

    mioto_car_struct.TUpdateUserResult updateUser(
        1:required mioto_car_struct.OpHandle handle
        2:required mioto_car_struct.TUser    user
    );

    mioto_car_struct.TFeePolicyResult createFeePolicy(
        1:required mioto_car_struct.OpHandle          handle
        2:required mioto_car_struct.TFeePolicy        feePolicy
    );

    mioto_car_struct.TFeePolicyResult updateFeePolicy(
        1:required mioto_car_struct.OpHandle          handle
        2:required mioto_car_struct.TFeePolicy        feePolicy
    );

    mioto_car_struct.TFeePolicyResult getFeePolicy(
        1:required mioto_car_struct.OpHandle handle
        2:optional string                    name
    );

    mioto_car_struct.TFeePolicyResult getFeePolicyById(
        1:required mioto_car_struct.OpHandle handle
        2:required i32                       feePolicyId
    );

    mioto_car_struct.TFeePolicyResult deleteFeePolicy(
        1:required mioto_car_struct.OpHandle handle
        2:required i32                       feePolicyId
    );
    
    mioto_car_struct.TProvinceResult createProvince(
        1:required mioto_car_struct.OpHandle         handle
        2:required mioto_car_struct.TProvince        province
    );

    mioto_car_struct.TProvinceResult updateProvince(
        1:required mioto_car_struct.OpHandle         handle
        2:required mioto_car_struct.TProvince        province
    );
    
    mioto_car_struct.TProvinceResult getProvince(
        1:required mioto_car_struct.OpHandle         handle
        2:required string                            provinceName
    );

    mioto_car_struct.TProvinceResult getProvinceById(
        1:required mioto_car_struct.OpHandle         handle
        2:required i32                               provinceId
    );

    mioto_car_struct.TProvinceResult deleteProvince(
        1:required mioto_car_struct.OpHandle         handle
        2:required i32                               provinceId
    );

    mioto_car_struct.TDistrictResult createDistrict(
        1:required mioto_car_struct.OpHandle         handle
        2:required mioto_car_struct.TDistrict        district
    );
    
    mioto_car_struct.TDistrictResult updateDistrict(
        1:required mioto_car_struct.OpHandle         handle
        2:required mioto_car_struct.TDistrict        district
    );
    
    mioto_car_struct.TDistrictResult getDistrict(
        1:required mioto_car_struct.OpHandle         handle
        2:required i32                               provinceId
    );

    mioto_car_struct.TDistrictResult getDistrictById(
        1:required mioto_car_struct.OpHandle         handle
        2:required i32                               districtId
    );

    mioto_car_struct.TDistrictResult deleteDistrict(
        1:required mioto_car_struct.OpHandle         handle
        2:required i32                               districtId
    );

    mioto_car_struct.TCarBrandResult createCarBrand (
        1:required mioto_car_struct.OpHandle         handle
        2:required mioto_car_struct.TCarBrand        carBrand
    );

    mioto_car_struct.TCarBrandResult updateCarBrand (
        1:required mioto_car_struct.OpHandle         handle
        2:required mioto_car_struct.TCarBrand        carBrand
    );

    mioto_car_struct.TCarBrandResult getCarBrand (
        1:required mioto_car_struct.OpHandle         handle
        2:optional string                            nameBrand
    );
    
    mioto_car_struct.TCarBrandResult getCarBrandById (
        1:required mioto_car_struct.OpHandle         handle
        2:optional i32                               carBrandId
    );

    mioto_car_struct.TCarBrandResult deleteCarBrand (
        1:required mioto_car_struct.OpHandle         handle
        2:optional i32                               carBrandId
    );

    mioto_car_struct.TFeatureResult createFeature (
        1:required mioto_car_struct.OpHandle         handle
        2:optional mioto_car_struct.TFeature         feature
    );
    
    mioto_car_struct.TFeatureResult updateFeature (
        1:required mioto_car_struct.OpHandle         handle
        2:optional mioto_car_struct.TFeature         feature
    );

    mioto_car_struct.TFeatureResult getFeature (
        1:required mioto_car_struct.OpHandle         handle
        2:optional string                            nameFeature
    );

    mioto_car_struct.TFeatureResult getFeatureById (
        1:required mioto_car_struct.OpHandle         handle
        2:optional i32                               featureId
    );

    mioto_car_struct.TFeatureResult deleteFeature (
        1:required mioto_car_struct.OpHandle         handle
        2:optional i32                               featureId
    );
    
    mioto_car_struct.TFeedBackResult createFeedBack (
        1:required mioto_car_struct.OpHandle         handle
        2:required mioto_car_struct.TFeedBack        feedback
    );
    
    mioto_car_struct.TFeedBackResult updateFeedBack (
        1:required mioto_car_struct.OpHandle         handle
        2:required mioto_car_struct.TFeedBack        feedback
    );

    mioto_car_struct.TFeedBackResult getFeedBack (
        1:required mioto_car_struct.OpHandle         handle
        2:required mioto_car_struct.TFeedBackRequest request
    );

    mioto_car_struct.TFeedBackResult deleteFeedBack (
        1:required mioto_car_struct.OpHandle         handle
        2:required i32                               feedBackId
    );

    mioto_car_struct.TVoucherResult createVoucher (
        1:required mioto_car_struct.OpHandle         handle
        2:required mioto_car_struct.TVoucher         voucher
    );

    mioto_car_struct.TVoucherResult updateVoucher (
        1:required mioto_car_struct.OpHandle         handle
        2:required mioto_car_struct.TVoucher         voucher
    );

    mioto_car_struct.TVoucherResult getVoucher (
        1:required mioto_car_struct.OpHandle         handle
        2:optional string                            title
    );
    
    mioto_car_struct.TVoucherResult getVoucherById (
        1:required mioto_car_struct.OpHandle         handle
        2:required i32                               voucherId
    );

    mioto_car_struct.TVoucherResult deleteVoucher (
        1:required mioto_car_struct.OpHandle         handle
        2:required i32                               voucherId
    );

    mioto_car_struct.TUserResult getUserBySession(
        1:required  mioto_car_struct.OpHandle handle
        2:required  mioto_car_struct.TSessionID sessionId
        3:required  mioto_car_struct.TLoginInfo loginInfo
    );
    
}