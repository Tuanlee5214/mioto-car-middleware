/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import error.Err;
import error.ValueResult;
import org.apache.log4j.Logger;
import thrift.OpHandle;
import thrift.TLoginInfo;
import thrift.TLoginResult;
import thrift.TSession;
import thrift.TSignUpRequest;
import thrift.TUser;
import thrift.TUserPwd;
import util.PwdUtil;

/**
 *
 * @author tuanlee
 */
public class AuthModel {
    
    private static final Logger _Logger = Logger.getLogger(AuthModel.class);
    private static final long SESSION_TTL_MS = 30L * 24 * 60 * 60 * 1000;

    
    public static final AuthModel Instance = new AuthModel();  
    private AuthModel() {}
    
    public ValueResult<TLoginResult> signup(OpHandle handle, TSignUpRequest request, TLoginInfo loginInfo)
    {
        //Validate input
        if(request.getPhone() == null || request.getPhone().trim().isEmpty()) 
            return new ValueResult<TLoginResult>(Err.BAD_REQUEST, "Số điện thoại không được để trống");
        
        if(request.getPwd() == null) 
            return new ValueResult<TLoginResult>(Err.BAD_REQUEST, "Mật khẩu không được để trống");
        
        if(request.getPwd().length() < 6) 
            return new ValueResult<TLoginResult>(Err.BAD_REQUEST, "Mật khẩu phải có ít nhất từ 6 kí tự trở lên");
            
        if(UserModel.Instance.isPhoneExisted(request.getPhone()))
        {
            return new ValueResult<TLoginResult>(Err.CONFLICT, "Số điện thoại đã tồn tại trong hệ thống");
        }
        
        //Create user
        TUser user = new TUser();
        user.setDisplayName(request.getDisplayName().trim());
        user.setPhone(request.getPhone().trim());
        user.setEmail(request.getEmail().trim());
        long userId = UserModel.Instance.createUser(user);
        if(Err.isFail(userId)) return new ValueResult<TLoginResult>(userId, "Tạo người dùng thất bại");
        user.setUserId((int) userId);
        
        //Create UserPwd
        String salt = PwdUtil.newSalt();
        String pwdHash = PwdUtil.hash(request.getPwd(), salt);
        
        TUserPwd userPwd = new TUserPwd();
        userPwd.setUserId((int) userId);
        userPwd.setSalt(salt);
        userPwd.setPwdHash(pwdHash);    
        long userPwdResult = UserPwdModel.Instance.createUserPwd(userPwd);
        if(Err.isFail(userPwdResult)) return new ValueResult<TLoginResult>(userPwdResult, "Tạo nơi lưu mật khẩu thất bại");
        
        //Create Session
        long sessionId = PwdUtil.newSessionId();
        TSession session = new TSession();
        session.setSessionId(sessionId);
        session.setUserId((int) userId);
        long sessionResult = SessionModel.Instance.createSession(session, loginInfo);
        if(Err.isFail(sessionResult)) return new ValueResult<TLoginResult>(sessionResult, "Tạo phiên đăng nhập thất bại");
        
        long timeExpired = System.currentTimeMillis() + SESSION_TTL_MS;
        
        //Set up result
        TLoginResult loginResult = new TLoginResult();
        loginResult.setUser(user);
        loginResult.setSessionId(sessionId);
        loginResult.setTimeExpired(timeExpired);
        loginResult.setError(Err.SUCCESS);
        loginResult.setMessage("Đăng kí thành công");
        
        //Return value
        ValueResult<TLoginResult> finalResult = new ValueResult<TLoginResult>(Err.SUCCESS, loginResult, "Đăng kí thành công");
        return finalResult;
    }
}
