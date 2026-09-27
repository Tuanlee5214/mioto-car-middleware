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
import thrift.TFeature;

/**
 *
 * @author tuanlee
 */
public class FeatureDao {
    
    private static final Logger _Logger = Logger.getLogger(FeatureDao.class);
    
    private static final String TABLE   = "Features";
    private static final String KEY     = "id";
    private static final String COLS    = "id,nameFeature,createdAt,updatedAt";
    private final MysqlClient _cli;
    
    public FeatureDao(String name)
    {
        _cli = new MysqlClient(name);
    }
    
    public MysqlClient getClient()
    {
        return _cli;
    }
    
    public long createFeature(TFeature feature)
    {
        String sql = "INSERT INTO " + TABLE 
                + " (nameFeature,createdAt,updatedAt)"
                + " VALUES (?,?,?)";
        return _cli.executeInsertAndReturnKey(sql, feature.getNameFeature(), feature.getCreatedAt(), feature.getUpdatedAt());
    }
    
    public long updatedFeature(TFeature feature)
    {
        String sql = "UPDATE " + TABLE 
                + " SET nameFeature=?,updatedAt=?"
                + " WHERE " + KEY + "=?";
        return _cli.executeUpdate(sql, feature.getNameFeature(), feature.getUpdatedAt(), feature.getFeatureId());
    }
    
    public long deleteFeature(long featureId)
    {
        return _cli.executeUpdate("DELETE FROM " + TABLE + " WHERE " + KEY + "=?", featureId);
    }
    
    public ValueResult<TFeature> getFeatureById(long featureId)
    {
        ValueResult<TFeature> ret = new ValueResult<TFeature>(Err.FAIL);
        String sql = "SELECT " + COLS + " FROM " + TABLE + " WHERE " + KEY + "=?";
        ret.error = _cli.executeQuery(new MysqlClient.IRowListener() {
            @Override
            public void onRow(ResultSet rs) throws SQLException {
                ret.value = map(rs);
            }
        }, sql, featureId);
        return ret;
    }
    
    public ValueResult<List<TFeature>> getAllFeature(String nameFeature, int count, int offset)
    {
        ValueResult<List<TFeature>> ret = new ValueResult<List<TFeature>>(Err.FAIL);
        StringBuilder sql = new StringBuilder("SELECT " + COLS + " FROM " + TABLE + " WHERE 1=1");
        List<Object> params = new ArrayList<Object>();
        
        if(nameFeature != null && !nameFeature.isEmpty())
        {
            sql.append(" AND nameFeature=?");
            params.add(nameFeature);
        }
        
        sql.append(" ORDER BY nameFeature ASC LIMIT ? OFFSET ?");
        params.add(count <= 0 || count > 100 ? 20 : count);
        params.add(Math.max(0, offset));
        ret.error = _cli.executeQuery(new MysqlClient.IRowListener() {
            @Override
            public void onRow(ResultSet rs) throws SQLException {
                throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }
        }, sql.toString(), params.toArray());
        return ret;
    }
    
    private TFeature map(ResultSet rs) throws SQLException
    {
        TFeature feature = new TFeature();
        feature.setFeatureId(rs.getInt("id"));
        feature.setNameFeature(rs.getString("nameFeature"));
        feature.setCreatedAt(rs.getLong("createdAt"));
        feature.setUpdatedAt(rs.getLong("updatedAt"));
        return feature;
    }
}
