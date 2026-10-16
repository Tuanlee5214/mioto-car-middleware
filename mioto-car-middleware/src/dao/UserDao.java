/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import db.MysqlClient;
import error.Err;
import error.ValueResult;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import thrift.TUser;

/**
 *
 * @author tuanlee
 */
public class UserDao {

    private static final Logger _Logger = Logger.getLogger(UserDao.class);

    private static final String TABLE = "Users";
    private static final String KEY = "userId";
    private static final String COLS = "userId,phone,email,displayName,status,timeCreated,timeUpdated";

    private final MysqlClient _cli;

    public UserDao(String name) {
        _cli = new MysqlClient(name);
    }

    public MysqlClient getClient() {
        return _cli;
    }

    public boolean isPhoneExisted(String phone) {
        String sql = "SELECT 1 FROM " + TABLE + " WHERE phone=? LIMIT 1";
        int result = _cli.executeQuery(null, sql, phone);

        return Err.isSuccess(result);
    }
    
    public boolean isEmailExisted(String email, long userId)
    {
        String sql = "SELECT 1 FROM " + TABLE + " WHERE email=? AND userId!= LIMIT 1";
        int result = _cli.executeQuery(null, sql, email);
        return Err.isSuccess(result);
    }

    public long createUser(TUser user) {
        String sql = "INSERT INTO " + TABLE
                + " (phone,email,displayName,status,timeCreated,timeUpdated)"
                + " VALUES (?,?,?,?,?,?)";
        return _cli.executeInsertAndReturnKey(sql, user.getPhone(), user.getEmail(), user.getDisplayName(),
                user.getStatus(), user.getTimeCreated(), user.getTimeUpdated());
    }

    public ValueResult<TUser> getUser(long userId) {
        final ValueResult<TUser> ret = new ValueResult<TUser>(Err.FAIL);
        String sql = "SELECT " + COLS + " FROM " + TABLE + " WHERE " + KEY + "=?";
        ret.error = _cli.executeQuery(new MysqlClient.IRowListener() {
            @Override
            public void onRow(ResultSet rs) throws SQLException {
                ret.value = mapToUser(rs);
            }
        }, sql, userId);

        return ret;
    }
    
    public ValueResult<List<TUser>> getAllUser(int status, int count, int offset)
    {
        ValueResult<List<TUser>> ret = new ValueResult<List<TUser>>(Err.FAIL);
        StringBuilder sql = new StringBuilder("SELECT " + COLS + " FROM " + TABLE + " WHERE 1=1");
        List<Object> params = new ArrayList<Object>();
        
        if(status > 0 && status <= 3)
        {
            sql.append(" AND status=?");
            params.add(status);
        }
        
        sql.append(" ORDER BY userId DESC LIMIT ? OFFSET ?");
        params.add(count <= 0 || count > 100 ? 20 : count);
        params.add(Math.max(0, offset));
        
        ret.error = _cli.executeQuery(new MysqlClient.IRowListener() {
            @Override
            public void onRow(ResultSet rs) throws SQLException {
                if(ret.value == null) ret.value = new ArrayList<TUser>();
                ret.value.add(mapToUser(rs));
            }
        }, sql.toString(), params.toArray());
        return ret;
    }
    
    
    public ValueResult<TUser> getUserByPhone(String phone) {
        final ValueResult<TUser> ret = new ValueResult<TUser>(Err.FAIL);
        String sql = "SELECT " + COLS + " FROM " + TABLE + " WHERE phone=?";
        ret.error = _cli.executeQuery(new MysqlClient.IRowListener() {
            @Override
            public void onRow(ResultSet rs) throws SQLException {
                ret.value = mapToUser(rs);
            }
        }, sql, phone);

        return ret;     
    }
    
    public ValueResult<Integer> updateUser(TUser user) {
        ValueResult<Integer> ret = new ValueResult<Integer>(Err.FAIL);
        String sql = "UPDATE " + TABLE
                + " SET email=?,displayName=?,timeUpdated=?"
                + " WHERE " + KEY + "=?";
        ret.error = _cli.executeUpdate(sql, user.getEmail(), user.getDisplayName(), System.currentTimeMillis(), user.getUserId());
        return ret;
    }

    private TUser mapToUser(ResultSet rs) throws SQLException {
        TUser user = new TUser();
        user.setUserId(rs.getInt("userId"));
        user.setPhone(rs.getString("phone"));
        user.setEmail(rs.getString("email"));
        user.setDisplayName(rs.getString("displayName"));
        user.setStatus(rs.getByte("status"));
        user.setTimeCreated(rs.getLong("timeCreated"));
        user.setTimeUpdated(rs.getLong("timeUpdated"));
        return user;
    }
}
