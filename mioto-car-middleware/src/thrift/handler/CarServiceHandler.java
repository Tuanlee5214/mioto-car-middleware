/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package thrift.handler;

import error.Err;
import java.util.List;
import model.AuthModel;
import model.CarBrandModel;
import model.CarFeatureModel;
import model.CarImageModel;
import model.CarModel;
import model.CarUnavailModel;
import model.DistrictModel;
import model.FeatureModel;
import model.FeePolicyModel;
import model.FeedBackModel;
import model.ProvinceModel;
import model.RoleModel;
import model.SessionModel;
import model.UserModel;
import model.UserRoleModel;
import model.VoucherModel;
import org.apache.log4j.Logger;
import org.apache.thrift.TException;
import thrift.MiotoCarService;
import thrift.OpHandle;
import thrift.TCar;
import thrift.TCarBrand;
import thrift.TCarBrandResult;
import thrift.TCarDetailResult;
import thrift.TCarFeature;
import thrift.TCarFilterRequest;
import thrift.TCarImage;
import thrift.TCarImageResult;
import thrift.TCarResult;
import thrift.TCarUnavails;
import thrift.TCarUnavailsResult;
import thrift.TDistrict;
import thrift.TDistrictResult;
import thrift.TFeature;
import thrift.TFeatureResult;
import thrift.TFeePolicy;
import thrift.TFeePolicyResult;
import thrift.TFeedBack;
import thrift.TFeedBackResult;
import thrift.TListCarBrandResult;
import thrift.TListCarFeatureViewResult;
import thrift.TListCarImageResult;
import thrift.TListCarUnavailsResult;
import thrift.TListCarViewResult;
import thrift.TListDistrictResult;
import thrift.TListFeatureResult;
import thrift.TListFeePolicyResult;
import thrift.TListFeedBackResult;
import thrift.TListProvinceResult;
import thrift.TListRoleResult;
import thrift.TListVoucherResult;
import thrift.TLoginInfo;
import thrift.TLoginRequest;
import thrift.TLoginResult;
import thrift.TLogoutResult;
import thrift.TProvince;
import thrift.TProvinceResult;
import thrift.TRole;
import thrift.TRoleResult;
import thrift.TSessionResult;
import thrift.TSignUpRequest;
import thrift.TUpdateUserResult;
import thrift.TUser;
import thrift.TUserResult;
import thrift.TUserRoleResult;
import thrift.TVoucher;
import thrift.TVoucherResult;

/**
 *
 * @author tuanlee
 */
public class CarServiceHandler implements MiotoCarService.Iface {

    private static final Logger _Logger = Logger.getLogger(CarServiceHandler.class);

    @Override
    public TLoginResult signup(OpHandle handle, TSignUpRequest request, TLoginInfo loginInfo) throws TException {
        try {
            _Logger.info("Call signup in mw handler");
            return AuthModel.Instance.signup(handle, request, loginInfo);
        } catch (Exception e) {
            _Logger.error("Sign up with phone = " + request.phone + " , src= " + handle.source, e);
            return new TLoginResult(Err.FAIL, "Lỗi hệ thống");
        }
    }

    @Override
    public TLoginResult login(OpHandle handle, TLoginRequest request, TLoginInfo loginInfo) throws TException {
        try {
            return AuthModel.Instance.login(handle, request, loginInfo);
        } catch (Exception e) {
            _Logger.error("Login with phone = " + request.phone + " , src= " + handle.source, e);
            return new TLoginResult(Err.FAIL, "Lỗi hệ thống");
        }
    }

    @Override
    public TLogoutResult logout(OpHandle handle, long sessionId) throws TException {
        try {
            return AuthModel.Instance.logout(handle, sessionId);
        } catch (Exception e) {
            _Logger.error("Logout with sessionId = " + sessionId + " , src= " + handle.source, e);
            return new TLogoutResult(Err.FAIL, "Lỗi hệ thống");
        }
    }

    @Override
    public TSessionResult getSession(OpHandle handle, long sessionId) throws TException {
        try {
            return SessionModel.Instance.getSession(sessionId);
        } catch (Exception e) {
            _Logger.error("getSession sessionId=" + sessionId + " src= " + handle.source, e);
            return new TSessionResult(Err.FAIL, "Lỗi hệ thống");
        }
    }

    @Override
    public TUserResult getUser(OpHandle handle, int userId) throws TException {
        try {
            return UserModel.Instance.getUser(userId);
        } catch (Exception e) {
            _Logger.error("getUser userId=" + userId + " src= " + handle.source, e);
            return new TUserResult(Err.FAIL, "Lỗi hệ thống");
        }
    }

    @Override
    public TUpdateUserResult updateUser(OpHandle handle, TUser user) throws TException {
        try {
            return UserModel.Instance.updateUser(user);
        } catch (Exception e) {
            _Logger.error("updateUser userId= " + user.getUserId() + " src= " + handle.source, e);
            return new TUpdateUserResult(Err.FAIL, "Lỗi hệ thống");
        }
    }

    @Override
    public TUserResult getUserBySession(OpHandle handle, long sessionId, TLoginInfo loginInfo) throws TException {
        try {
            return UserModel.Instance.getUserBySessionId(sessionId, loginInfo);
        } catch (Exception e) {
            _Logger.error("getUser by sessionId= " + sessionId + " src= " + handle.source, e);
            return new TUserResult(Err.FAIL, "Lỗi hệ thống");
        }
    }

    @Override
    public TFeePolicyResult createFeePolicy(OpHandle handle, TFeePolicy feePolicy) throws TException {
        try {
            return FeePolicyModel.Instance.createFeePolicy(feePolicy);
        } catch (Exception e) {
            _Logger.error("create FeePolicy " + "src= " + handle.source, e);
            return new TFeePolicyResult(Err.FAIL, "Lỗi hệ thống");
        }
    }

    @Override
    public TFeePolicyResult updateFeePolicy(OpHandle handle, TFeePolicy feePolicy) throws TException {
        try {
            return FeePolicyModel.Instance.updateFeePolicy(feePolicy);
        } catch (Exception e) {
            _Logger.error("update feePolicy with id= " + feePolicy.getFeePolicyId() + " src= " + handle.source, e);
            return new TFeePolicyResult(Err.FAIL, "Lỗi hệ thống");
        }
    }

    @Override
    public TFeePolicyResult getFeePolicyById(OpHandle handle, int feePolicyId) throws TException {
        try {
            return FeePolicyModel.Instance.getFeePolicyById(feePolicyId);
        } catch (Exception e) {
            _Logger.error("get fee policy by id = " + feePolicyId + " src= " + handle.source, e);
            return new TFeePolicyResult(Err.FAIL, "Lỗi hệ thống");
        }
    }

    @Override
    public TFeePolicyResult deleteFeePolicy(OpHandle handle, int feePolicyId) throws TException {
        try {
            return new TFeePolicyResult((int) FeePolicyModel.Instance.deleteFeePolicy(feePolicyId), "");
        } catch (Exception e) {
            _Logger.error("delete fee policy by id " + feePolicyId + " src= " + handle.source, e);
            return new TFeePolicyResult(Err.FAIL, "Lỗi hệ thống");
        }
    }

    @Override
    public TProvinceResult createProvince(OpHandle handle, TProvince province) throws TException {
        try {
            return ProvinceModel.Instance.createProvince(province);
        } catch (Exception e) {
            _Logger.error("create province with src= " + handle.source, e);
            return new TProvinceResult(Err.FAIL, "Lỗi hệ thống");
        }
    }

    @Override
    public TProvinceResult updateProvince(OpHandle handle, TProvince province) throws TException {
        try {
            return ProvinceModel.Instance.updateProvince(province);
        } catch (Exception e) {
            _Logger.error("update province with id : " + province.getProvinceId() + " src= " + handle.source, e);
            return new TProvinceResult(Err.FAIL, "Lỗi hệ thống");
        }
    }

    @Override
    public TProvinceResult getProvinceById(OpHandle handle, int provinceId) throws TException {
        try {
            return ProvinceModel.Instance.getProvinceById(provinceId);
        } catch (Exception e) {
            _Logger.error("get province by id = " + provinceId + " src= " + handle.source, e);
            return new TProvinceResult(Err.FAIL, "Lỗi hệ thống");
        }
    }

    @Override
    public TProvinceResult deleteProvince(OpHandle handle, int provinceId) throws TException {
        try {
            return new TProvinceResult((int) ProvinceModel.Instance.deleteProvince(provinceId), "");
        } catch (Exception e) {
            _Logger.error("delete province by id = " + provinceId + " src= " + handle.source, e);
            return new TProvinceResult(Err.FAIL, "Lỗi hệ thống");
        }
    }

    @Override
    public TDistrictResult createDistrict(OpHandle handle, TDistrict district) throws TException {
        try {
            return DistrictModel.Instance.createDistrict(district);
        } catch (Exception e) {
            _Logger.error("create district with src = " + handle.source, e);
            return new TDistrictResult(Err.FAIL, "Lỗi hệ thống");
        }
    }

    @Override
    public TDistrictResult updateDistrict(OpHandle handle, TDistrict district) throws TException {
        try {
            return DistrictModel.Instance.updateDistrict(district);
        } catch (Exception e) {
            _Logger.error("update district with id :" + district.getDistrictId() + " src= " + handle.source, e);
            return new TDistrictResult(Err.FAIL, "Lỗi hệ thống");
        }
    }

    @Override
    public TDistrictResult getDistrictById(OpHandle handle, int districtId) throws TException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public TDistrictResult deleteDistrict(OpHandle handle, int districtId) throws TException {
        try {
            return new TDistrictResult((int) DistrictModel.Instance.deleteDistrict(districtId), "");
        } catch (Exception e) {
            _Logger.error("delete district by id : " + districtId + " src= " + handle.source, e);
            return new TDistrictResult(Err.FAIL, "Lỗi hệ thống");
        }
    }

    @Override
    public TCarBrandResult createCarBrand(OpHandle handle, TCarBrand carBrand) throws TException {
        try {
            return CarBrandModel.Instance.createCarBrand(carBrand);
        } catch (Exception e) {
            _Logger.error("create car brand with src= " + handle.source, e);
            return new TCarBrandResult(Err.FAIL, "");
        }
    }

    @Override
    public TCarBrandResult updateCarBrand(OpHandle handle, TCarBrand carBrand) throws TException {
        try {
            return CarBrandModel.Instance.updateCarBrand(carBrand);
        } catch (Exception e) {
            _Logger.error("update car brand by id :" + carBrand.getCarBrandId() + " src= " + handle.source, e);
            return new TCarBrandResult(Err.FAIL, "Lỗi hệ thống");
        }
    }

    @Override
    public TCarBrandResult getCarBrandById(OpHandle handle, int carBrandId) throws TException {
        try {
            return CarBrandModel.Instance.getCarBrandById(carBrandId);
        } catch (Exception e) {
            _Logger.error("get car brand by id: " + carBrandId + " src= " + handle.source, e);
            return new TCarBrandResult(Err.FAIL, "Lỗi hệ thống");
        }
    }

    @Override
    public TCarBrandResult deleteCarBrand(OpHandle handle, int carBrandId) throws TException {
        try {
            return new TCarBrandResult((int) CarBrandModel.Instance.deleteCarBrand(carBrandId), "");
        } catch (Exception e) {
            _Logger.error("delete car brand by id: " + carBrandId + " src= " + handle.source, e);
            return new TCarBrandResult(Err.FAIL, "Lỗi hệ thống");
        }
    }

    @Override
    public TFeatureResult createFeature(OpHandle handle, TFeature feature) throws TException {
        try {
            return FeatureModel.Instance.createFeature(feature);
        } catch (Exception e) {
            _Logger.error("create feature result with src = " + handle.source, e);
            return new TFeatureResult(Err.FAIL, "Lỗi hệ thống");
        }
    }

    @Override
    public TFeatureResult updateFeature(OpHandle handle, TFeature feature) throws TException {
        try {
            return FeatureModel.Instance.updateFeature(feature);
        } catch (Exception e) {
            _Logger.error("update feature by id : " + feature.getFeatureId() + " src= " + handle.source, e);
            return new TFeatureResult(Err.FAIL, "Lỗi hệ thống");
        }
    }

    @Override
    public TFeatureResult getFeatureById(OpHandle handle, int featureId) throws TException {
        try {
            return FeatureModel.Instance.getFeatureById(featureId);
        } catch (Exception e) {
            _Logger.error("get feature by id : " + featureId + " src= " + handle.source, e);
            return new TFeatureResult(Err.FAIL, "Lỗi hệ thống");
        }
    }

    @Override
    public TFeatureResult deleteFeature(OpHandle handle, int featureId) throws TException {
        try {
            return new TFeatureResult((int) FeatureModel.Instance.deleteFeature(featureId), "");
        } catch (Exception e) {
            _Logger.error("delete feature by id : " + featureId + " src= " + handle.source, e);
            return new TFeatureResult(Err.FAIL, "Lỗi hệ thống");
        }
    }

    @Override
    public TFeedBackResult createFeedBack(OpHandle handle, TFeedBack feedback) throws TException {
        try {
            return FeedBackModel.Instance.createFeedBack(feedback);
        } catch (Exception e) {
            _Logger.error("create feedback with src: " + handle.source, e);
            return new TFeedBackResult(Err.FAIL, "Lỗi hệ thống");
        }
    }

    @Override
    public TFeedBackResult updateFeedBack(OpHandle handle, TFeedBack feedback) throws TException {
        try {
            return FeedBackModel.Instance.updateFeedBack(feedback);
        } catch (Exception e) {
            _Logger.error("update feedback by id :" + feedback.getFeedbackId() + " src= " + handle.source, e);
            return new TFeedBackResult(Err.FAIL, "Lỗi hệ thống");
        }
    }

    @Override
    public TFeedBackResult deleteFeedBack(OpHandle handle, int feedBackId) throws TException {
        try {
            return new TFeedBackResult((int) FeedBackModel.Instance.deleteFeedBack(feedBackId), "");
        } catch (Exception e) {
            _Logger.error("delete feedback by id: " + feedBackId + " src= " + handle.source, e);
            return new TFeedBackResult(Err.FAIL, "Lỗi hệ thống");
        }
    }

    @Override
    public TVoucherResult createVoucher(OpHandle handle, TVoucher voucher) throws TException {
        try {
            return VoucherModel.Instance.createVoucher(voucher);
        } catch (Exception e) {
            _Logger.error("create voucher with src= " + handle.source, e);
            return new TVoucherResult(Err.FAIL, "Lỗi hệ thống");
        }
    }

    @Override
    public TVoucherResult updateVoucher(OpHandle handle, TVoucher voucher) throws TException {
        try {
            return VoucherModel.Instance.updateVoucher(voucher);
        } catch (Exception e) {
            _Logger.error("update voucher by id : " + voucher.getVoucherId() + " src= " + handle.source, e);
            return new TVoucherResult(Err.FAIL, "Lỗi hệ thống");
        }
    }

    @Override
    public TVoucherResult getVoucherById(OpHandle handle, int voucherId) throws TException {
        try {
            return VoucherModel.Instance.getVoucherById(voucherId);
        } catch (Exception e) {
            _Logger.error("get voucher by id : " + voucherId + " src= " + handle.source, e);
            return new TVoucherResult(Err.FAIL, "Lỗi hệ thống");
        }
    }

    @Override
    public TVoucherResult deleteVoucher(OpHandle handle, int voucherId) throws TException {
        try {
            return new TVoucherResult((int) VoucherModel.Instance.deleteVoucher(voucherId), "");
        } catch (Exception e) {
            _Logger.error("delete voucher by id: " + voucherId + " src= " + handle.source, e);
            return new TVoucherResult(Err.FAIL, "Lỗi hệ thống");
        }
    }

    @Override
    public TListFeePolicyResult getFeePolicy(OpHandle handle, String name, int count, int offset) throws TException {
        try {
            return FeePolicyModel.Instance.getAllFeePolicy(name, count, offset);
        } catch (Exception e) {
            _Logger.error("get all fee policy with src = " + handle.source, e);
            return new TListFeePolicyResult(Err.FAIL, "Lỗi hệ thống");
        }
    }

    @Override
    public TListProvinceResult getProvince(OpHandle handle, String provinceName, int count, int offset) throws TException {
        try {
            return ProvinceModel.Instance.getAllProvince(provinceName, count, offset);
        } catch (Exception e) {
            _Logger.error("get all province with src = " + handle.source, e);
            return new TListProvinceResult(Err.FAIL, "Lỗi hệ thống");
        }
    }

    @Override
    public TListDistrictResult getDistrict(OpHandle handle, int provinceId, int count, int offset) throws TException {
        try {
            return DistrictModel.Instance.getDistrict(provinceId, count, offset);
        } catch (Exception e) {
            _Logger.error("get all district with src = " + handle.source, e);
            return new TListDistrictResult(Err.FAIL, "Lỗi hệ thống");
        }
    }

    @Override
    public TListCarBrandResult getCarBrand(OpHandle handle, String nameBrand, int count, int offset) throws TException {
        try {
            return CarBrandModel.Instance.getAllCarBrand(nameBrand, count, offset);
        } catch (Exception e) {
            _Logger.error("get all car brand with src = " + handle.source, e);
            return new TListCarBrandResult(Err.FAIL, "Lỗi hệ thống");
        }
    }

    @Override
    public TListFeatureResult getFeature(OpHandle handle, String nameFeature, int count, int offset) throws TException {
        try {
            return FeatureModel.Instance.getAllFeature(nameFeature, count, offset);
        } catch (Exception e) {
            _Logger.error("get all feature with src = " + handle.source, e);
            return new TListFeatureResult(Err.FAIL, "Lỗi hệ thống");
        }
    }

    @Override
    public TListFeedBackResult getFeedBack(OpHandle handle, int receiverId, int count, int offset) throws TException {
        try {
            return FeedBackModel.Instance.getFeedBackByReceiverId(receiverId, count, offset);
        } catch (Exception e) {
            _Logger.error("get all feedback with src = " + handle.source, e);
            return new TListFeedBackResult(Err.FAIL, "Lỗi hệ thống");
        }
    }

    @Override
    public TListVoucherResult getVoucher(OpHandle handle, String title, int count, int offset, String code) throws TException {
        try {
            return VoucherModel.Instance.getAllVoucher(title, code, count, offset);
        } catch (Exception e) {
            _Logger.error("get all voucher with src = " + handle.source, e);
            return new TListVoucherResult(Err.FAIL, "Lỗi hệ thống");
        }
    }

    @Override
    public TCarResult createCar(OpHandle handle, TCar car, List<TCarImage> images, List<TCarFeature> features) throws TException {
        try {
            return CarModel.Instance.createCar(car, images, features);
        } catch (Exception e) {
            _Logger.error("create car with src= " + handle.source, e);
            return new TCarResult(Err.FAIL, "Lỗi hệ thống");
        }
    }

    @Override
    public TCarResult updateCar(OpHandle handle, TCar car) throws TException {
        try {
            return CarModel.Instance.updateCar(car);
        } catch (Exception e) {
            _Logger.error("update car by id : " + car.getCarId() + " src= " + handle.source, e);
            return new TCarResult(Err.FAIL, "Lỗi hệ thống");
        }
    }

    @Override
    public TCarResult updateStatusCar(OpHandle handle, TCar car) throws TException {
        try {
            return CarModel.Instance.updateStatusCar(car);
        } catch (Exception e) {
            _Logger.error("update status car by id : " + car.getCarId() + " src= " + handle.source, e);
            return new TCarResult(Err.FAIL, "Lỗi hệ thống");
        }
    }

    @Override
    public TCarDetailResult getCarById(OpHandle handle, int carId) throws TException {
        try {
            return CarModel.Instance.getCarById(carId);
        } catch (Exception e) {
            _Logger.error("get car by id : " + carId + " src= " + handle.source, e);
            return new TCarDetailResult(Err.FAIL, "Lỗi hệ thống");
        }
    }

    @Override
    public TListCarViewResult getCarViewByUserId(OpHandle handle, int userId, int count, int offset) throws TException {
        try {
            return CarModel.Instance.getCarViewByUserId(userId, count, offset);
        } catch (Exception e) {
            _Logger.error("get car view by user id : " + userId + " src= " + handle.source, e);
            return new TListCarViewResult(Err.FAIL, "Lỗi hệ thống");
        }
    }

    @Override
    public TListCarViewResult searchCar(OpHandle handle, TCarFilterRequest filter, long startTime, long endTime, int count, int offset) throws TException {
        try {
            return CarModel.Instance.searchCar(filter, startTime, endTime, 1, count, offset);
        } catch (Exception e) {
            _Logger.error("search car with src = " + handle.source, e);
            return new TListCarViewResult(Err.FAIL, "Lỗi hệ thống");
        }
    }

    @Override
    public TListCarViewResult searchCarByAdmin(OpHandle handle, TCarFilterRequest filter, int status, int count, int offset) throws TException {
        try {
            return CarModel.Instance.searchCar(filter, 0, 0, status, count, offset);
        } catch (Exception e) {
            _Logger.error("search car by admin with src = " + handle.source, e);
            return new TListCarViewResult(Err.FAIL, "Lỗi hệ thống");
        }
    }

    @Override
    public TListCarImageResult getCarImagesByCarId(OpHandle handle, int carId) throws TException {
        try {
            return CarImageModel.Instance.getCarImagesByCarId(carId);
        } catch (Exception e) {
            _Logger.error("get car images by car id : " + carId + " src= " + handle.source, e);
            return new TListCarImageResult(Err.FAIL, "Lỗi hệ thống");
        }
    }

    @Override
    public TListCarImageResult createCarImages(OpHandle handle, List<TCarImage> images, int userId) throws TException {
        try {
            return CarImageModel.Instance.createCarImages(images, userId);
        } catch (Exception e) {
            _Logger.error("create car images with src= " + handle.source, e);
            return new TListCarImageResult(Err.FAIL, "Lỗi hệ thống");
        }
    }

    @Override
    public TCarImageResult deleteCarImages(OpHandle handle, int carId, List<Long> imageIds, int userId) throws TException {
        try {
            return new TCarImageResult((int) CarImageModel.Instance.deleteCarImages(carId, imageIds, userId), "");
        } catch (Exception e) {
            _Logger.error("delete car images by car id : " + carId + " src= " + handle.source, e);
            return new TCarImageResult(Err.FAIL, "Lỗi hệ thống");
        }
    }

    @Override
    public TListCarFeatureViewResult getListCarFeatures(OpHandle handle, int carId) throws TException {
        try {
            return CarFeatureModel.Instance.getListCarFeatures(carId);
        } catch (Exception e) {
            _Logger.error("get list car features by car id : " + carId + " src= " + handle.source, e);
            return new TListCarFeatureViewResult(Err.FAIL, "Lỗi hệ thống");
        }
    }

    @Override
    public TListCarFeatureViewResult createCarFeatures(OpHandle handle, List<TCarFeature> features, int userId) throws TException {
        try {
            return CarFeatureModel.Instance.createCarFeatures(features, userId);
        } catch (Exception e) {
            _Logger.error("create car features with src= " + handle.source, e);
            return new TListCarFeatureViewResult(Err.FAIL, "Lỗi hệ thống");
        }
    }

    @Override
    public TListCarFeatureViewResult deleteCarFeatures(OpHandle handle, int carId, List<Long> carFeatureIds, int userId) throws TException {
        try {
            return new TListCarFeatureViewResult((int) CarFeatureModel.Instance.deleteCarFeatures(carId, carFeatureIds, userId), "");
        } catch (Exception e) {
            _Logger.error("delete car features by car id : " + carId + " src= " + handle.source, e);
            return new TListCarFeatureViewResult(Err.FAIL, "Lỗi hệ thống");
        }
    }

    @Override
    public TListCarUnavailsResult getListCarUnavails(OpHandle handle, int carId) throws TException {
        try {
            return CarUnavailModel.Instance.getListCarUnavails(carId);
        } catch (Exception e) {
            _Logger.error("get list car unavails by car id : " + carId + " src= " + handle.source, e);
            return new TListCarUnavailsResult(Err.FAIL, "Lỗi hệ thống");
        }
    }

    @Override
    public TCarUnavailsResult createCarUnavails(OpHandle handle, TCarUnavails unavail, int userId) throws TException {
        try {
            return CarUnavailModel.Instance.createCarUnavails(unavail, userId);
        } catch (Exception e) {
            _Logger.error("create car unavails with src= " + handle.source, e);
            return new TCarUnavailsResult(Err.FAIL, "Lỗi hệ thống");
        }
    }

    @Override
    public TCarUnavailsResult deleteCarUnavails(OpHandle handle, int carId, long carUnavailId, int userId) throws TException {
        try {
            return new TCarUnavailsResult((int) CarUnavailModel.Instance.deleteCarUnavails(carId, carUnavailId, userId), "");
        } catch (Exception e) {
            _Logger.error("delete car unavails by id : " + carUnavailId + " src= " + handle.source, e);
            return new TCarUnavailsResult(Err.FAIL, "Lỗi hệ thống");
        }
    }

    @Override
    public TRoleResult createRole(OpHandle handle, TRole role) throws TException {
        try {
            return RoleModel.Instance.createRole(role);
        } catch (Exception e) {
            _Logger.error("create role with src = " + handle.source, e);
            return new TRoleResult(Err.FAIL, "Lỗi hệ thống");
        }
    }

    @Override
    public TRoleResult updateRole(OpHandle handle, TRole role) throws TException {
        try {
            return RoleModel.Instance.updateRole(role);
        } catch (Exception e) {
            _Logger.error("update role with src = " + handle.source, e);
            return new TRoleResult(Err.FAIL, "Lỗi hệ thống");
        }
    }

    @Override
    public TRoleResult deleteRole(OpHandle handle, int roleId) throws TException {
        try {
            long ret = RoleModel.Instance.deleteRole(roleId);   // model trả long
            if (Err.isFail(ret)) {
                return new TRoleResult((int) ret, "");
            }
            return new TRoleResult(Err.SUCCESS, "Xóa dữ liệu thành công");
        } catch (Exception e) {
            _Logger.error("delete role with src = " + handle.source, e);
            return new TRoleResult(Err.FAIL, "Lỗi hệ thống");
        }
    }

    @Override
    public TListRoleResult getRole(OpHandle handle, String name, int count, int offset) throws TException {
        try {
            return RoleModel.Instance.getAllRole(name, count, offset);
        } catch (Exception e) {
            _Logger.error("get role with src = " + handle.source, e);
            return new TListRoleResult(Err.FAIL, "Lỗi hệ thống");
        }
    }

    @Override
    public TUserRoleResult getUserRole(OpHandle handle, int userId) throws TException {
        try {
            return UserRoleModel.Instance.getUserRoleByUserId(userId);
        } catch (Exception e) {
            _Logger.error("get user role with src = " + handle.source, e);
            return new TUserRoleResult(Err.FAIL, "Lỗi hệ thống");
        }
    }

    @Override
    public TUserRoleResult createUserRole(OpHandle handle, int userId, List<Integer> roleIds) throws TException {
        try {
            return UserRoleModel.Instance.createUserRole(userId, roleIds);
        } catch (Exception e) {
            _Logger.error("create user role with src = " + handle.source, e);
            return new TUserRoleResult(Err.FAIL, "Lỗi hệ thống");
        }
    }

    @Override
    public TUserRoleResult deleteUserRole(OpHandle handle, int userId, List<Integer> roleIds) throws TException {
        try {
            return UserRoleModel.Instance.deleteUserRole(userId, roleIds);
        } catch (Exception e) {
            _Logger.error("delete user role with src = " + handle.source, e);
            return new TUserRoleResult(Err.FAIL, "Lỗi hệ thống");
        }
    }
}
