/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import cache.SimpleCache;
import dao.UserDao;
import error.Err;
import error.ValueResult;
import org.apache.log4j.Logger;
import thrift.TUpdateUserResult;
import thrift.TUser;
import thrift.TUserResult;

/**
 *
 * @author tuanlee
 */
public class UserModel {
        
    private static final Logger _Logger = Logger.getLogger(UserModel.class);
    
    public static final UserModel Instance = new UserModel();
    private final UserDao _dao = new UserDao("mioto");
    private final SimpleCache<Integer, TUser> _cacheId = new SimpleCache<Integer, TUser>("user");
    private final SimpleCache<String, TUser> _cachePhone = new SimpleCache<String, TUser>("user");
    
    private UserModel(){}
    
    public UserDao getDao() { return _dao;}
    public SimpleCache<Integer, TUser> getCache() { return _cacheId;}
    
    public boolean isPhoneExisted(String phone)
    {
        return _dao.isPhoneExisted(phone);
    }
    
    public long createUser(TUser user)
    {
        long id = _dao.createUser(user);
        if (Err.isFail(id)) return id;
        _cacheId.remove((int) id);
        _cachePhone.remove(user.getPhone());
        return id;
    }
    
    public TUserResult getUser(long userId)
    {
        TUserResult result = new TUserResult(Err.FAIL, "");
        TUser cached = _cacheId.get((int)userId);
        if(cached != null) 
        {
            result.setError(Err.SUCCESS);
            result.setMessage("Lấy dữ liệu người dùng thành công");
            result.setValue(new TUser(cached));
            return result;
        }
        
        ValueResult<TUser> ret = _dao.getUser(userId);
        if(Err.isNetworkError(ret.error)) 
        {
            return new TUserResult((int) ret.error, "Lỗi kết nối mạng");
        }
        
        if(Err.isNotFound(ret.error)) 
        {
            return new TUserResult((int) ret.error, "Không tìm thấy người dùng");
        }

        if(ret.isSuccess() && ret.value != null)
        {
            _cacheId.put((int) userId, new TUser(ret.value));
            _cachePhone.put(ret.value.getPhone(), new TUser(ret.value));
            result.setError(Err.SUCCESS);
            result.setMessage("Lấy dữ liệu người dùng thành công");
            result.setValue(ret.value);
        }
        return result;
    }
    
    public TUserResult getUserByPhone(String phone)
    {
        if(phone == null) return new TUserResult(Err.BAD_REQUEST, "Số điện thoại không được để trống");
        String clearPhone = phone.trim();
        long now = System.currentTimeMillis();
        TUserResult result = new TUserResult(Err.FAIL, "");
        TUser cached = _cachePhone.get(clearPhone);
        if(cached != null)
        {
            result.setError(Err.SUCCESS);
            result.setMessage("Lấy dữ liệu người dùng thành công");
            result.setValue(new TUser(cached));
            return result;
        }
        
        ValueResult<TUser> ret = _dao.getUserByPhone(clearPhone);
        if(Err.isNetworkError(ret.error))
        {
            return new TUserResult((int) ret.error, "Lỗi kết nối mạng");
        }
        if(Err.isNotFound(ret.error))
        {
            return new TUserResult((int) ret.error, "Không tìm thấy người dùng");
        }
        if(Err.isSuccess(ret.error))
        {
            _cacheId.put((int)ret.value.getUserId(), new TUser(ret.value));
            _cachePhone.put(clearPhone, new TUser(ret.value));
            result.setError(Err.SUCCESS);
            result.setMessage("Lấy thông tin người dùng thành công");
            result.setValue(new TUser(ret.value));
        }
        return result;
    }
    
    public TUpdateUserResult updateUser(TUser user)
    {
        ValueResult<Integer> ret = _dao.updateUser(user);
        TUpdateUserResult result = new TUpdateUserResult(Err.FAIL, "");
        if(Err.isNetworkError(ret.error)) 
        {
            return new TUpdateUserResult((int) ret.error, "Lỗi kết nối mạng");
        }
        
        result.setError((int) ret.error);
        result.setMessage("Cập nhật thành công");
        result.setValue(ret.value);
        _cacheId.remove(user.getUserId());
        _cachePhone.remove(user.getPhone());
        return result;
    }
}
