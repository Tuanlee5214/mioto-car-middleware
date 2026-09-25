/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package thrift.handler;

import error.Err;
import model.AuthModel;
import model.SessionModel;
import model.UserModel;
import org.apache.log4j.Logger;
import org.apache.thrift.TException;
import thrift.MiotoCarService;
import thrift.OpHandle;
import thrift.TCarBrand;
import thrift.TCarBrandResult;
import thrift.TDistrict;
import thrift.TDistrictResult;
import thrift.TFeature;
import thrift.TFeatureResult;
import thrift.TFeePolicy;
import thrift.TFeePolicyResult;
import thrift.TFeedBack;
import thrift.TFeedBackRequest;
import thrift.TFeedBackResult;
import thrift.TLoginInfo;
import thrift.TLoginRequest;
import thrift.TLoginResult;
import thrift.TLogoutResult;
import thrift.TProvince;
import thrift.TProvinceResult;
import thrift.TSessionResult;
import thrift.TSignUpRequest;
import thrift.TUpdateUserResult;
import thrift.TUser;
import thrift.TUserResult;
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
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public TFeePolicyResult updateFeePolicy(OpHandle handle, TFeePolicy feePolicy) throws TException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public TFeePolicyResult getFeePolicy(OpHandle handle, String name) throws TException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public TFeePolicyResult getFeePolicyById(OpHandle handle, int feePolicyId) throws TException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public TFeePolicyResult deleteFeePolicy(OpHandle handle, int feePolicyId) throws TException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public TProvinceResult createProvince(OpHandle handle, TProvince province) throws TException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public TProvinceResult updateProvince(OpHandle handle, TProvince province) throws TException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public TProvinceResult getProvince(OpHandle handle, String provinceName) throws TException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public TProvinceResult getProvinceById(OpHandle handle, int provinceId) throws TException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public TProvinceResult deleteProvince(OpHandle handle, int provinceId) throws TException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public TDistrictResult createDistrict(OpHandle handle, TDistrict district) throws TException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public TDistrictResult updateDistrict(OpHandle handle, TDistrict district) throws TException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public TDistrictResult getDistrict(OpHandle handle, int provinceId) throws TException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public TDistrictResult getDistrictById(OpHandle handle, int districtId) throws TException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public TDistrictResult deleteDistrict(OpHandle handle, int districtId) throws TException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public TCarBrandResult createCarBrand(OpHandle handle, TCarBrand carBrand) throws TException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public TCarBrandResult updateCarBrand(OpHandle handle, TCarBrand carBrand) throws TException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public TCarBrandResult getCarBrand(OpHandle handle, String nameBrand) throws TException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public TCarBrandResult getCarBrandById(OpHandle handle, int carBrandId) throws TException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public TCarBrandResult deleteCarBrand(OpHandle handle, int carBrandId) throws TException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public TFeatureResult createFeature(OpHandle handle, TFeature feature) throws TException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public TFeatureResult updateFeature(OpHandle handle, TFeature feature) throws TException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public TFeatureResult getFeature(OpHandle handle, String nameFeature) throws TException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public TFeatureResult getFeatureById(OpHandle handle, int featureId) throws TException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public TFeatureResult deleteFeature(OpHandle handle, int featureId) throws TException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public TFeedBackResult createFeedBack(OpHandle handle, TFeedBack feedback) throws TException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public TFeedBackResult updateFeedBack(OpHandle handle, TFeedBack feedback) throws TException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public TFeedBackResult getFeedBack(OpHandle handle, TFeedBackRequest request) throws TException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public TFeedBackResult deleteFeedBack(OpHandle handle, int feedBackId) throws TException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public TVoucherResult createVoucher(OpHandle handle, TVoucher voucher) throws TException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public TVoucherResult updateVoucher(OpHandle handle, TVoucher voucher) throws TException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public TVoucherResult getVoucher(OpHandle handle, String title) throws TException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public TVoucherResult getVoucherById(OpHandle handle, int voucherId) throws TException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public TVoucherResult deleteVoucher(OpHandle handle, int voucherId) throws TException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
