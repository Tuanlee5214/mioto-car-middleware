/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import dao.UserPwdDao;
import org.apache.log4j.Logger;
import thrift.TUserPwd;

/**
 *
 * @author tuanlee
 */
public class UserPwdModel {
   
    private static final Logger _Logger = Logger.getLogger(UserPwdModel.class);
    public static final UserPwdModel Instance = new UserPwdModel();
    private final UserPwdDao _dao = new UserPwdDao("mioto");
    
    private UserPwdModel() {}
    
    public UserPwdDao getDao() { return _dao; }
    
    public long createUserPwd(TUserPwd userPwd)
    {
        long id = _dao.CreateUserPwd(userPwd);
        return id;  
    }
}
