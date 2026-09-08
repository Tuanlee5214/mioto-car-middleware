/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import db.MysqlClient;
import org.apache.log4j.Logger;
import thrift.TUserPwd;

/**
 *
 * @author tuanlee
 */
public class UserPwdDao {
    
    private static final Logger _Logger = Logger.getLogger(UserPwdDao.class);
    
    private static final String TABLE = "UserPwds";
    private static final String KEY = "userId";
    private static final String COLS = "userId,pwdHash,salt,timeUpdated";
    
    private final MysqlClient _cli;
    
    public UserPwdDao(String name)
    {
        _cli = new MysqlClient(name);
    }
    
    public MysqlClient getClient() 
    {
        return _cli;
    }
    
    public long CreateUserPwd(TUserPwd userPwd)
    {
        String sql = "INSERT INTO " + TABLE
                + " (userId,pwdHash,salt,timeUpdated)"
                + " VALUES (?,?,?,?)";
        return _cli.executeInsertAndReturnKey(sql, userPwd.getUserId(), userPwd.getPwdHash(), userPwd.getSalt(), userPwd.getTimeUpdated());
    }
}
    