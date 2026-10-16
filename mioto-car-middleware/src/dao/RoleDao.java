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
import thrift.TRole;

/**
 *
 * @author tuanlee
 */
public class RoleDao {
    private static final String TABLE = "Roles";
    private static final String KEY = "id";
    private static final String COLS = "id,name,displayName";
    private final MysqlClient _cli;

    public RoleDao(String name) {
        _cli = new MysqlClient(name);
    }

    public long createRole(TRole role)
    {
        String sql = "INSERT INTO " + TABLE + " (name) VALUES (?)";
        return _cli.executeInsertAndReturnKey(sql, role.getName());
    }

    public long updateRole(TRole role)
    {
        String sql = "UPDATE " + TABLE + " SET name=? WHERE " + KEY + "=?";
        return _cli.executeUpdate(sql, role.getName(), role.getRoleId());
    }

    public long deleteRole(long roleId)
    {
        return _cli.executeUpdate("DELETE FROM " + TABLE + " WHERE " + KEY + "=?", roleId);
    }

    public ValueResult<TRole> getRoleById(long roleId)
    {
        ValueResult<TRole> ret = new ValueResult<TRole>(Err.FAIL);
        String sql = "SELECT " + COLS + " FROM " + TABLE + " WHERE " + KEY + "=?";
        ret.error = _cli.executeQuery(new MysqlClient.IRowListener() {
            @Override
            public void onRow(ResultSet rs) throws SQLException {
                ret.value = map(rs);
            }
        }, sql, roleId);
        return ret;
    }

    public ValueResult<List<TRole>> getAllRole(String name, int count, int offset)
    {
        ValueResult<List<TRole>> ret = new ValueResult<List<TRole>>(Err.FAIL);
        StringBuilder sql = new StringBuilder("SELECT " + COLS + " FROM " + TABLE + " WHERE 1=1");
        List<Object> params = new ArrayList<Object>();

        if(name != null && !name.trim().isEmpty())
        {
            sql.append(" AND name=?");
            params.add(name.trim());
        }

        sql.append(" ORDER BY name ASC LIMIT ? OFFSET ?");
        params.add(count <= 0 || count > 100 ? 20 : count);
        params.add(Math.max(0, offset));
        ret.error = _cli.executeQuery(new MysqlClient.IRowListener() {
            @Override
            public void onRow(ResultSet rs) throws SQLException {
                if(ret.value == null) ret.value = new ArrayList<TRole>();
                ret.value.add(map(rs));
            }
        }, sql.toString(), params.toArray());
        return ret;
    }

    public ValueResult<List<TRole>> getRolesByIds(List<Integer> roleIds)
    {
        ValueResult<List<TRole>> ret = new ValueResult<List<TRole>>(Err.FAIL);
        StringBuilder sql = new StringBuilder("SELECT " + COLS + " FROM " + TABLE + " WHERE " + KEY + " IN (");
        List<Object> params = new ArrayList<Object>();
        for (int i = 0; i < roleIds.size(); i++) {
            sql.append(i == 0 ? "?" : ",?");
            params.add(roleIds.get(i));
        }
        sql.append(") ORDER BY " + KEY + " ASC");
        ret.error = _cli.executeQuery(new MysqlClient.IRowListener() {
            @Override
            public void onRow(ResultSet rs) throws SQLException {
                if(ret.value == null) ret.value = new ArrayList<TRole>();
                ret.value.add(map(rs));
            }
        }, sql.toString(), params.toArray());
        return ret;
    }

    private TRole map(ResultSet rs) throws SQLException
    {
        TRole role = new TRole();
        role.setRoleId(rs.getInt("id"));
        role.setName(rs.getString("name"));
        role.setDisplayName(rs.getString("displayName"));
        return role;
    }
}