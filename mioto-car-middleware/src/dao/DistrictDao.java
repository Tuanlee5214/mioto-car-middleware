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
import thrift.TDistrict;

/**
 *
 * @author tuanlee
 */
public class DistrictDao {
    
    private static final Logger _Logger = Logger.getLogger(DistrictDao.class);
    
    private static final String TABLE = "Districts";
    private static final String KEY   = "id";
    private static final String COLS  = "id,nameDistrict,provinceId";
    
    private final MysqlClient _cli;
    
    public DistrictDao(String name)
    {
        _cli = new MysqlClient(name);
    }
    
    public MysqlClient getClient()
    {
        return _cli;
    }
    
    public long createDistrict(TDistrict district)
    {
        String sql = "INSERT INTO " + TABLE 
                + " (nameDistrict,provinceId)"
                + " VALUES(?,?)";
        return _cli.executeInsertAndReturnKey(sql, district.getDistrictName(), district.getProvinceId());
    }
    
    public long updateDistrict(TDistrict district)
    {
        String sql = "UPDATE " + TABLE
                + " SET nameDistrict=?"
                + " WHERE " + KEY + "=?";
        return _cli.executeUpdate(sql, district.getDistrictName(), district.getDistrictId());
    }
    
    public long deleteDistrict(long districtId)
    {
        return _cli.executeUpdate("DELETE FROM " + TABLE + " WHERE " + KEY + "=?", districtId);
    }
    
    public ValueResult<List<TDistrict>> getDistrict(long provinceId, int count, int offset)
    {
        ValueResult<List<TDistrict>> ret = new ValueResult<List<TDistrict>>(Err.FAIL);
        String sql = "SELECT " + COLS + " FROM " + TABLE + " WHERE provinceId=? ORDER BY nameDistrict ASC LIMIT ? OFFSET ?";
        List<Object> params = new ArrayList<Object>();
        params.add(provinceId);
        params.add(count <= 0 || count > 100 ? 20 : count);
        params.add(Math.max(0, offset));
        ret.error = _cli.executeQuery(new MysqlClient.IRowListener() {
            @Override
            public void onRow(ResultSet rs) throws SQLException {
                if(ret.value == null)
                {
                    ret.value = new ArrayList<TDistrict>();
                }
                ret.value.add(map(rs));
            }
        }, sql, params.toArray());
        return ret;
    }
    
    
    private TDistrict map(ResultSet rs) throws SQLException
    {
        TDistrict district = new TDistrict();
        district.setDistrictId(rs.getInt("id"));
        district.setDistrictName(rs.getString("nameDistrict"));
        district.setProvinceId(rs.getInt("provinceId"));
        return district;
    }
}
