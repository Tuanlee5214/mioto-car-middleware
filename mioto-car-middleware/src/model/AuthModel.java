/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import error.Err;
import org.apache.log4j.Logger;
import thrift.OpHandle;
import thrift.TLoginInfo;
import thrift.TLoginRequest;
import thrift.TLoginResult;
import thrift.TLogoutResult;
import thrift.TSession;
import thrift.TSessionResult;
import thrift.TSignUpRequest;
import thrift.TUser;
import thrift.TUserPwd;
import thrift.TUserPwdResult;
import thrift.TUserResult;
import thrift.TUserStatus;
import util.PwdUtil;

/**
 *
 * @author tuanlee
 */
public class AuthModel {
    
    private static final Logger _Logger = Logger.getLogger(AuthModel.class);
    private static final long DAYS_30 = 30L * 24 * 60 * 60 * 1000;
    private static final long HOURS_24 = 24 * 60 * 60 * 1000;

    
    public static final AuthModel Instance = new AuthModel();  
    private AuthModel() {}
    
    public TLoginResult signup(OpHandle handle, TSignUpRequest request, TLoginInfo loginInfo)
    {
        //Validate input
        if(request.getPhone() == null || request.getPhone().trim().isEmpty()) 
            return new TLoginResult(Err.BAD_REQUEST, "Số điện thoại không được để trống");
        
        if(request.getPwd() == null) 
            return new TLoginResult(Err.BAD_REQUEST, "Mật khẩu không được để trống");
        
        if(request.getPwd().length() < 6) 
            return new TLoginResult(Err.BAD_REQUEST, "Mật khẩu phải có ít nhất từ 6 kí tự trở lên");
            
        if(UserModel.Instance.isPhoneExisted(request.getPhone()))
            return new TLoginResult(Err.CONFLICT, "Số điện thoại đã tồn tại trong hệ thống");
        
        //Create user
        TUser user = new TUser();
        user.setDisplayName(request.getDisplayName().trim());
        user.setPhone(request.getPhone().trim());
        user.setEmail(request.getEmail().trim());
        user.setStatus((byte) TUserStatus.TUS_ACTIVE.getValue());
        user.setTimeCreated(System.currentTimeMillis());
        user.setTimeUpdated(System.currentTimeMillis());
        long userId = UserModel.Instance.createUser(user);
        if(Err.isFail(userId)) return new TLoginResult((int) userId, "Tạo người dùng thất bại");
        user.setUserId((int) userId);
        
        //Create UserPwd
        String salt = PwdUtil.newSalt();
        String pwdHash = PwdUtil.hash(request.getPwd(), salt);
        
        TUserPwd userPwd = new TUserPwd();
        userPwd.setUserId((int) userId);
        userPwd.setSalt(salt);
        userPwd.setPwdHash(pwdHash);  
        userPwd.setTimeUpdated(System.currentTimeMillis());
        long userPwdResult = UserPwdModel.Instance.createUserPwd(userPwd);
        if(Err.isFail(userPwdResult)) return new TLoginResult((int) userPwdResult, "Tạo nơi lưu mật khẩu thất bại");
        
        //Create Session
        long sessionId = PwdUtil.newSessionId();
        TSession session = new TSession();
        session.setSessionId(sessionId);
        session.setUserId((int) userId);
        session.setTimeCreated(System.currentTimeMillis());
        long timeExpired = System.currentTimeMillis() + (loginInfo.longSession ? DAYS_30 : HOURS_24);
        session.setTimeExpired(timeExpired);
        long sessionResult = SessionModel.Instance.createSession(session, loginInfo);
        if(Err.isFail(sessionResult)) return new TLoginResult((int) sessionResult, "Tạo phiên đăng nhập thất bại");
        
        
        //Set up result
        TLoginResult loginResult = new TLoginResult();
        loginResult.setUser(user);
        loginResult.setSessionId(sessionId);
        loginResult.setTimeExpired(timeExpired);
        loginResult.setError(Err.SUCCESS);
        loginResult.setMessage("Đăng kí thành công");
        
        //Return value
        return loginResult;
    }
    
    public TLoginResult login(OpHandle handle, TLoginRequest request, TLoginInfo loginInfo)
    {
        if (request.getPwd().length() < 6) {
            return new TLoginResult(Err.BAD_REQUEST, "Mật khẩu phải có từ 6 kí tự trở lên");
        }

        TUserResult u = UserModel.Instance.getUserByPhone(request.getPhone());
        if(Err.isFail(u.error) || u.value == null)
            return new TLoginResult(Err.FAIL, "Tài khoản hoặc mật khẩu không đúng");
        
        if(u.getValue().getStatus() != TUserStatus.TUS_ACTIVE.getValue())
            return new TLoginResult(Err.FORBIDDEN, "Tài khoản của bạn đã bị khóa");
                
        TUserPwdResult up = UserPwdModel.Instance.getUserPwdByUserId(u.value.getUserId());
        if(Err.isFail(up.error) || up.value == null 
                || !PwdUtil.matches(request.getPwd(), up.value.getSalt(), up.value.getPwdHash()))
            return new TLoginResult(Err.FAIL, "Tài khoản hoặc mật khẩu không đúng");
        
        long now = System.currentTimeMillis();
        TSession session = new TSession();
        session.setSessionId(PwdUtil.newSessionId());
        session.setUserId(u.value.getUserId());
        session.setTimeCreated(now);
        session.setTimeExpired(now + (loginInfo.longSession ? DAYS_30 : HOURS_24));
        long sessionResult = SessionModel.Instance.createSession(session, loginInfo);
        if(Err.isFail(sessionResult)) return new TLoginResult((int) sessionResult, "Tạo phiên đăng nhập thất bại");
        
        TLoginResult loginResult = new TLoginResult();
        loginResult.setUser(u.value);
        loginResult.setSessionId(session.getSessionId());
        loginResult.setTimeExpired(session.getTimeExpired());
        loginResult.setError(Err.SUCCESS);
        loginResult.setMessage("Đăng nhập thành công");

        return loginResult;

    }
    
    public TLogoutResult logout(OpHandle handle, long sessionId)
    {
        if(sessionId <= 0) return new TLogoutResult(Err.BAD_REQUEST, "sessionId không hợp lệ");
        TSessionResult ret = SessionModel.Instance.deleteSession(sessionId);
        
        if(Err.isFail(ret.error)) return new TLogoutResult(ret.error, ret.message);
        return new TLogoutResult(Err.SUCCESS, "Đăng xuất thành công");
    }
}
