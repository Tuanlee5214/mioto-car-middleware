/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import cache.SimpleCache;
import dao.UserPwdDao;
import error.Err;
import error.ValueResult;
import org.apache.log4j.Logger;
import thrift.TUserPwd;
import thrift.TUserPwdResult;

/**
 *
 * @author tuanlee
 */
public class UserPwdModel {
   
    private static final Logger _Logger = Logger.getLogger(UserPwdModel.class);
    public static final UserPwdModel Instance = new UserPwdModel();
    private final UserPwdDao _dao = new UserPwdDao("mioto");
    private final SimpleCache<Integer, TUserPwd> _cache = new SimpleCache<Integer, TUserPwd>("userPwd");
    
    private UserPwdModel() {}
    
    public UserPwdDao getDao() { return _dao; }
    public SimpleCache<Integer, TUserPwd> getCache() { return _cache; }
    
    public long createUserPwd(TUserPwd userPwd)
    {
        long id = _dao.CreateUserPwd(userPwd);
        return id;  
    }
    
    public TUserPwdResult getUserPwdByUserId(long userId)
    {
        TUserPwdResult result = new TUserPwdResult(Err.FAIL, "");
        TUserPwd cached = _cache.get((int) userId);
        if(cached != null)
        {
            result.setError(Err.SUCCESS);
            result.setMessage("Lấy dữ liệu mật khẩu người dùng thành công");
            result.setValue(new TUserPwd(cached));
            return result;
        }
        
        ValueResult<TUserPwd> ret = _dao.getUserPwdByUserId(userId);
        if(Err.isNetworkError(ret.error))
        {
            return new TUserPwdResult((int) ret.error, "Lỗi kết nối mạng");
        }
        if(Err.isNotFound(ret.error))
        {
            return new TUserPwdResult((int) ret.error, "Không tìm thấy thông tin mật khẩu người dùng");
        }
        
        if(ret.isSuccess() && ret.value != null)
        {
            _cache.put((int) userId, new TUserPwd(ret.value));
            result.setError(Err.SUCCESS);
            result.setMessage("Tìm thấy thông tin mật khẩu người dùng thành công");
            result.setValue(new TUserPwd(ret.value));
        }
        return result;
    }
}
