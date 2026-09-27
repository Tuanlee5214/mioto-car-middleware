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
import thrift.TProvince;

/**
 *
 * @author tuanlee
 */
public class ProvinceDao {
    private static final Logger _Logger = Logger.getLogger(ProvinceDao.class);
    
    private static final String TABLE = "Provinces";
    private static final String KEY   = "id";
    private static final String COLS  = "id,nameProvince";
    
    private final MysqlClient _cli;
    
    public ProvinceDao(String name)
    {
        _cli = new MysqlClient(name);
    }
    
    public MysqlClient getClient()
    {
        return _cli;
    }
    
    public long createProvince(TProvince province)
    {
        String sql = "INSERT INTO " + TABLE
                + " (nameProvince)"
                + " VALUES (?)";
        return _cli.executeInsertAndReturnKey(sql, province.getProvinceName());
    }
    
    public long updateProvince(TProvince province)
    {
        String sql = "UPDATE " + TABLE
                + " SET nameProvince=?"
                + " WHERE " + KEY + "=?";
        return _cli.executeUpdate(sql, province.getProvinceName(), province.getProvinceId());
    }
    
    public long deleteProvince(long provinceId)
    {
        return _cli.executeUpdate("DELETE FROM " + TABLE + " WHERE " + KEY + "=?", provinceId);
    }
    
    public ValueResult<TProvince> getProvinceById(long provinceId)
    {
        ValueResult<TProvince> ret = new ValueResult<TProvince>(Err.FAIL);
        String sql = "SELECT " + COLS + " FROM " + TABLE + " WHERE " + KEY + "=?";
        ret.error = _cli.executeQuery(new MysqlClient.IRowListener() {
            @Override
            public void onRow(ResultSet rs) throws SQLException {
                ret.value = map(rs);
            }
        }, sql, provinceId);
        return ret;
    }
    
    public ValueResult<List<TProvince>> getAllProvince(String provinceName, int count, int offset)
    {
        ValueResult<List<TProvince>> ret = new ValueResult<List<TProvince>>(Err.FAIL);
        StringBuilder sql = new StringBuilder("SELECT " + COLS + " FROM " + TABLE + " WHERE 1=1");
        List<Object> params = new ArrayList<Object>();
        
        if(provinceName != null && !provinceName.isEmpty())
        {
            sql.append(" AND nameProvince=?");
            params.add(provinceName);
        }
        
        sql.append(" ORDER BY nameProvince ASC LIMIT ? OFFSET ?");
        params.add(count <= 0 || count > 100 ? 20 : count);
        params.add(Math.max(0, offset));
        
        ret.error = _cli.executeQuery(new MysqlClient.IRowListener() {
            @Override
            public void onRow(ResultSet rs) throws SQLException {
                if(ret.value == null)
                {
                    ret.value = new ArrayList<TProvince>();
                }
                
                ret.value.add(map(rs));
            }
        }, sql.toString(), params.toArray());
        return ret;
    }
    
    private TProvince map(ResultSet rs) throws SQLException
    {
        TProvince province = new TProvince();
        province.setProvinceId(rs.getInt("id"));
        province.setProvinceName(rs.getString("nameProvince"));
        return province;
    }
}
