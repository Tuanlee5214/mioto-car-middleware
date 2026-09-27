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
import thrift.TCarBrand;


/**
 *
 * @author tuanlee
 */
public class CarBrandDao {
    
    private static final Logger _Logger = Logger.getLogger(CarBrandDao.class);
    private static final String TABLE   = "CarBrands";
    private static final String KEY     = "id";
    private static final String COLS    = "id,nameBrand,createdAt,updatedAt";
    
    private final MysqlClient _cli;
    public CarBrandDao(String name)
    {
        _cli = new MysqlClient(name);
    }
    
    public MysqlClient getClient()
    {
        return _cli;
    }
    
    public long createCarBrand(TCarBrand carBrand)
    {
        String sql = "INSERT INTO " + TABLE 
                + " (nameBrand,createdAt,updatedAt)"
                + " VALUES (?,?,?)";
        return _cli.executeInsertAndReturnKey(sql, carBrand.getNameBrand(), carBrand.getCreatedAt(), carBrand.getUpdatedAt());
    }
    
    public long updateCarBrand(TCarBrand carBrand)
    {
        String sql = "UPDATE " + TABLE
                + " SET nameBrand=?,updatedAt=?"
                + " WHERE " + KEY + "=?";
        return _cli.executeUpdate(sql, carBrand.getNameBrand(), carBrand.getUpdatedAt(), carBrand.getCarBrandId());
    }
    
    public long deleteCarBrand(long carBrandId)
    {
        return _cli.executeUpdate("DELETE FROM " + TABLE + " WHERE " + KEY + "=?", carBrandId);
    }
    
    public ValueResult<TCarBrand> getCarBrandById(long carBrandId)
    {
        ValueResult<TCarBrand> ret = new ValueResult<TCarBrand>(Err.FAIL);
        String sql = "SELECT " + COLS + " FROM " + TABLE + " WHERE " + KEY + "=?";
        ret.error  = _cli.executeQuery(new MysqlClient.IRowListener() {
            @Override
            public void onRow(ResultSet rs) throws SQLException {
                ret.value = map(rs);
            }
        }, sql, carBrandId);
        return ret;
    }
    
    public ValueResult<List<TCarBrand>> getAllCarBrand(String nameBrand, int count, int offset)
    {
        ValueResult<List<TCarBrand>> ret = new ValueResult<List<TCarBrand>>(Err.FAIL);
        StringBuilder sql = new StringBuilder("SELECT " + COLS + " FROM " + TABLE + " WHERE 1=1");
        List<Object> params = new ArrayList<Object>();
        
        if(nameBrand != null && !nameBrand.isEmpty())
        {
            sql.append(" AND nameBrand=?");
            params.add(nameBrand);
        }
        
        sql.append(" ORDER BY nameBrand ASC LIMIT ? OFFSET ?");
        params.add(count <= 0 || count > 100 ? 20 : count);
        params.add(Math.max(0, offset));
        ret.error = _cli.executeQuery(new MysqlClient.IRowListener() {
            @Override
            public void onRow(ResultSet rs) throws SQLException {
                if(ret.value == null) ret.value = new ArrayList<TCarBrand>();
                ret.value.add(map(rs));
            }
        }, sql.toString(), params.toArray());
        return ret;
    }
    
    private TCarBrand map(ResultSet rs) throws SQLException
    {
        TCarBrand carBrand = new TCarBrand();
        carBrand.setCarBrandId(rs.getInt("id"));
        carBrand.setNameBrand(rs.getString("nameBrand"));
        carBrand.setCreatedAt(rs.getLong("createdAt"));
        carBrand.setUpdatedAt(rs.getLong("updatedAt"));
        return carBrand;
    }
    
}
