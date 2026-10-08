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
import thrift.TVoucher;

/**
 *
 * @author tuanlee
 */
public class VoucherDao {
    
    private static final Logger _Logger = Logger.getLogger(VoucherDao.class);
    
    private static final String TABLE = "Vouchers";
    private static final String KEY   = "id";
    private static final String COLS  = "id,title,code,imageUrl,publicId,body,discountPercent,maxDiscount,startDate,endDate,createdAt";
    
    private final MysqlClient _cli;
    public VoucherDao(String name)
    {
        _cli = new MysqlClient(name);
    }
    
    public MysqlClient getClient()
    {
        return _cli;
    }
    
    public long createVoucher(TVoucher voucher)
    {
        String sql = "INSERT INTO " + TABLE
                + " (title,code,imageUrl,publicId,body,discountPercent,maxDiscount,startDate,endDate,createdAt)"
                + " VALUES (?,?,?,?,?,?,?,?,?,?)";
        return _cli.executeInsertAndReturnKey(sql, voucher.getTitle(), voucher.getCode(), voucher.getImageUrl(), 
                voucher.getPublicId(), voucher.getBody(), voucher.getDiscountPercent(), voucher.getMaxDiscount(), 
                voucher.getStartDate(), voucher.getEndDate(), voucher.getCreatedAt());
    }
    
    public long updatedVoucher(TVoucher voucher)
    {
        String sql = "UPDATE " + TABLE
                + " SET title=?,code=?,imageUrl=?,publicId=?,body=?,discountPercent=?,maxDiscount=?,startDate=?,endDate=?"
                + " WHERE " + KEY + "=?";
        return _cli.executeUpdate(sql, voucher.getTitle(), voucher.getCode(), voucher.getImageUrl(), 
                voucher.getPublicId(), voucher.getBody(), voucher.getDiscountPercent(), voucher.getMaxDiscount(), 
                voucher.getStartDate(), voucher.getEndDate(), voucher.getVoucherId());
    }
    
    public long deleteVoucher(long voucherId)
    {
        return _cli.executeUpdate("DELETE FROM " + TABLE + " WHERE " + KEY + "=?", voucherId);
    }
    
    public ValueResult<TVoucher> getVoucherById(long voucherId)
    {
        ValueResult<TVoucher> ret = new ValueResult<TVoucher>(Err.FAIL);
        String sql = "SELECT " + COLS + " FROM " + TABLE + " WHERE " + KEY + "=?";
        ret.error = _cli.executeQuery(new MysqlClient.IRowListener() {
            @Override
            public void onRow(ResultSet rs) throws SQLException {
                ret.value = map(rs);
            }
        }, sql, voucherId);
        return ret;
    }
    
    public ValueResult<List<TVoucher>> getAllVoucher(String title, String code, int count, int offset)
    {
        ValueResult<List<TVoucher>> ret = new ValueResult<List<TVoucher>>(Err.FAIL);
        StringBuilder sql = new StringBuilder("SELECT " + COLS + " FROM " + TABLE + " WHERE 1=1");
        List<Object> params = new ArrayList<Object>();
        
        if(title != null && !title.trim().isEmpty())
        {
            sql.append(" AND title=?");
            params.add(title);
        }
        
        if(code != null && !code.trim().isEmpty())
        {
            sql.append(" AND code=?");
            params.add(code);
        }
        sql.append(" ORDER BY createdAt DESC LIMIT ? OFFSET ?");
        params.add(count <= 0 || count > 100 ? 20 : count);
        params.add(Math.max(0, offset));
        
        ret.error = _cli.executeQuery(new MysqlClient.IRowListener() {
            @Override
            public void onRow(ResultSet rs) throws SQLException {
                if(ret.value == null) ret.value = new ArrayList<TVoucher>();
                ret.value.add(map(rs));
                
            }
        }, sql.toString(), params.toArray());
        return ret;
    }
    
    private TVoucher map(ResultSet rs) throws SQLException
    {
        TVoucher voucher = new TVoucher();
        voucher.setVoucherId(rs.getInt("id"));
        voucher.setTitle(rs.getString("title"));
        voucher.setCode(rs.getString("code"));
        voucher.setImageUrl(rs.getString("imageUrl"));
        voucher.setPublicId(rs.getString("publicId"));
        voucher.setBody(rs.getString("body"));
        voucher.setDiscountPercent(rs.getInt("discountPercent"));
        voucher.setMaxDiscount(rs.getInt("maxDiscount"));
        voucher.setStartDate(rs.getLong("startDate"));
        voucher.setEndDate(rs.getLong("endDate"));
        voucher.setCreatedAt(rs.getLong("createdAt"));
        return voucher;
    }
}
