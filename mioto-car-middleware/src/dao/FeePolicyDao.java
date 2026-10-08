/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import db.MysqlClient;
import error.Err;
import error.ValueResult;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import thrift.TFeePolicy;

/**
 *
 * @author tuanlee
 */
public class FeePolicyDao {
    
    private static final Logger _Logger = Logger.getLogger(FeePolicyDao.class);
    
    private static final String TABLE = "FeePolicies";
    private static final String KEY   = "feePolicyId";
    private static final String COLS  = "feePolicyId,nameFeePolicy,percentFee,isActive";
    
    private final MysqlClient _cli;
    
    public FeePolicyDao(String name)
    {
        _cli = new MysqlClient(name);
    }
    
    public MysqlClient getClient()
    {
        return _cli;
    }
    
    public long createFeePolicy(TFeePolicy feePolicy)
    {
        String sql = "INSERT INTO " + TABLE
                + " (nameFeePolicy,percentFee,isActive)"
                + " VALUES (?,?,?)";
        return _cli.executeInsertAndReturnKey(sql, feePolicy.getName(), feePolicy.getPercentFee(), feePolicy.isIsActive());
    }       
    
    public long updateFeePolicy(TFeePolicy feePolicy)
    {
        String sql = "UPDATE " + TABLE
                + " SET nameFeePolicy=?,percentFee=?,isActive=?"
                + " WHERE " + KEY + "=?";
        return _cli.executeUpdate(sql, feePolicy.getName(), new BigDecimal(feePolicy.getPercentFee()), feePolicy.isIsActive(), feePolicy.getFeePolicyId());
    }
    
    public long deleteFeePolicy(long feePolicyId)
    {
        return _cli.executeUpdate("DELETE FROM " + TABLE + " WHERE " + KEY + "=?", feePolicyId);
    }
    
    public ValueResult<TFeePolicy> getFeePolicyById(long feePolicyId)
    {
        ValueResult<TFeePolicy> ret = new ValueResult<TFeePolicy>(Err.FAIL);
        String sql = "SELECT " + COLS + " FROM " + TABLE + " WHERE " + KEY + "=?";
        ret.error = _cli.executeQuery(new MysqlClient.IRowListener() {
            @Override
            public void onRow(ResultSet rs) throws SQLException {
                ret.value = mapToFeePolicy(rs);
            }
        }, sql, feePolicyId);
        return ret;
    }
    
    public ValueResult<List<TFeePolicy>> getAllFeePolicy(String name, int count, int offset)
    {
        ValueResult<List<TFeePolicy>> ret = new ValueResult<List<TFeePolicy>>(Err.FAIL);
        StringBuilder sql = new StringBuilder("SELECT " + COLS + " FROM " + TABLE + " WHERE 1=1");
        List<Object> params = new ArrayList<Object>();
        
        if(name != null && !name.trim().isEmpty())
        {
            sql.append(" AND nameFeePolicy = ?");
            params.add(name);
        }
        
        sql.append(" ORDER BY percentFee ASC LIMIT ? OFFSET ?");
        params.add(count <= 0 || count > 100 ? 20 : count);
        params.add(Math.max(0, offset));
        
        ret.error = _cli.executeQuery(new MysqlClient.IRowListener() {
            @Override
            public void onRow(ResultSet rs) throws SQLException {
                if(ret.value == null)
                {
                    ret.value = new ArrayList<TFeePolicy>();
                }
                ret.value.add(mapToFeePolicy(rs));
            }
        }, sql.toString(), params.toArray());
        return ret; 
    }
    
    private TFeePolicy mapToFeePolicy(ResultSet rs) throws SQLException
    {
        TFeePolicy feePolicy = new TFeePolicy();
        feePolicy.setFeePolicyId(rs.getInt("feePolicyId"));
        feePolicy.setIsActive(rs.getBoolean("isActive"));
        feePolicy.setName(rs.getString("nameFeePolicy"));
        feePolicy.setPercentFee(rs.getBigDecimal("percentFee").toPlainString());
        return feePolicy;
    }
}
