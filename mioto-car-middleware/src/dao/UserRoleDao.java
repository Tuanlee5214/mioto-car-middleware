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
import java.util.Collection;
import java.util.List;
import thrift.TUserRole;

/**
 *
 * @author tuanlee
 */
public class UserRoleDao {
    private static final String TABLE = "UserRoles";
    private static final String COLS = "id,userId,roleIds,isSuperAdmin";
    private final MysqlClient _cli;

    public UserRoleDao(String name) {
        _cli = new MysqlClient(name);
    }

    public ValueResult<TUserRole> getUserRoleByUserId(long userId)
    {
        ValueResult<TUserRole> ret = new ValueResult<TUserRole>(Err.FAIL);
        String sql = "SELECT " + COLS + " FROM " + TABLE + " WHERE userId=?";
        ret.error = _cli.executeQuery(new MysqlClient.IRowListener() {
            @Override
            public void onRow(ResultSet rs) throws SQLException {
                ret.value = map(rs);
            }
        }, sql, userId);
        return ret;
    }

    // Ghi đè list. Chưa có dòng thì tạo (isSuperAdmin = 0), có rồi thì chỉ đổi roleIds
    public long saveUserRole(int userId, Collection<Integer> roleIds)
    {
        StringBuilder csv = new StringBuilder();
        for (Integer id : roleIds) {
            if (csv.length() > 0) csv.append(",");
            csv.append(id);
        }
        String sql = "INSERT INTO " + TABLE + " (userId,roleIds,isSuperAdmin) VALUES (?,?,0)"
                + " ON DUPLICATE KEY UPDATE roleIds=VALUES(roleIds)";
        return _cli.executeUpdate(sql, userId, csv.toString());
    }

    private TUserRole map(ResultSet rs) throws SQLException
    {
        TUserRole userRole = new TUserRole();
        userRole.setId(rs.getInt("id"));
        userRole.setUserId(rs.getInt("userId"));
        userRole.setIsSuperAdmin(rs.getBoolean("isSuperAdmin"));
        List<Integer> roleIds = new ArrayList<Integer>();
        String csv = rs.getString("roleIds");
        if (csv != null && !csv.trim().isEmpty()) {
            for (String s : csv.split(",")) {
                roleIds.add(Integer.valueOf(s.trim()));
            }
        }
        userRole.setRoleIds(roleIds);
        return userRole;
    }
}