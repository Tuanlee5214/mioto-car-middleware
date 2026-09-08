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
import thrift.TLoginInfo;
import thrift.TLoginRequest;
import thrift.TLoginResult;
import thrift.TSessionResult;
import thrift.TSignUpRequest;
import thrift.TUpdateUserResult;
import thrift.TUser;
import thrift.TUserResult;

/**
 *
 * @author tuanlee
 */
public class CarServiceHandler implements MiotoCarService.Iface {
    private static final Logger _Logger = Logger.getLogger(CarServiceHandler.class);

    @Override
    public TLoginResult signup(OpHandle handle, TSignUpRequest request, TLoginInfo loginInfo) throws TException {
        try {
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
        }
        catch(Exception e){
            _Logger.error("Login with phone = " + request.phone + " , src= " + handle.source, e);
            return new TLoginResult(Err.FAIL, "Lỗi hệ thống");
        }
    }

    @Override
    public int logout(OpHandle handle, long sessionId) throws TException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
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
        try{
            return UserModel.Instance.updateUser(user);
        }
        catch(Exception e)
        {
            _Logger.error("updateUser userId= " + userId + " src= " + handle.source, e);
            return new TUpdateUserResult(Err.FAIL, "Lỗi hệ thống");
        }
    }
}
