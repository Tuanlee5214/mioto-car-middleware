/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import cache.SimpleCache;
import db.UserDao;
import error.Err;
import error.ValueResult;
import org.apache.log4j.Logger;
import thrift.TUser;

/**
 *
 * @author tuanlee
 */
public class UserModel {
        
    private static final Logger _Logger = Logger.getLogger(UserModel.class);
    
    public static final UserModel Instance = new UserModel();
    
    private final UserDao _dao = new UserDao("mioto");
    private final SimpleCache<Integer, TUser> _cache = new SimpleCache<Integer, TUser>("user");
    private UserModel(){}
    
    public UserDao getDao() { return _dao;}
    public SimpleCache<Integer, TUser> getCache() { return _cache;}
    
    public boolean isPhoneExisted(String phone)
    {
        return _dao.isPhoneExisted(phone);
    }
    
    public long createUser(TUser user)
    {
        long id = _dao.createUser(user);
        if (Err.isFail(id)) return id;
        _cache.remove((int) id);
        return id;
    }
    
    public ValueResult<TUser> getUser(long userId)
    {
        TUser cached = _cache.get((int)userId);
        if(cached != null) 
        {
            return new ValueResult<TUser>(Err.SUCCESS, new TUser(cached));    
        }
        
        ValueResult<TUser> ret = _dao.getUser(userId);
        if(ret.isSuccess() && ret.value != null)
        {
            _cache.put((int) userId, new TUser(ret.value));
        }
        
        return ret;
    }
}
