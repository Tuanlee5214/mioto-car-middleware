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
import thrift.TCarImage;

/**
 *
 * @author tuanlee
 */
public class CarImageDao {

    private static final Logger _Logger = Logger.getLogger(CarImageDao.class);
    private static final String TABLE = "CarImages";
    private static final String COLS = "id,carId,publicId,createdAt,orderNum";
    private static final String KEY = "id";

    private final MysqlClient _cli;

    public CarImageDao(String name) {
        _cli = new MysqlClient(name);
    }

    public MysqlClient getClient() {
        return _cli;
    }

    // Insert nhiều dòng trong 1 câu SQL
    public long createListCarImages(List<TCarImage> images, int startOrder) {
        if (images == null || images.isEmpty()) {
            return 0;
        }
        StringBuilder sql = new StringBuilder("INSERT INTO " + TABLE
                + " (carId,publicId,createdAt,orderNum) VALUES ");
        List<Object> params = new ArrayList<Object>();
        for (int i = 0; i < images.size(); i++) {
            TCarImage img = images.get(i);
            sql.append(i == 0 ? "(?,?,?,?)" : ",(?,?,?,?)");
            params.add(img.getCarId());
            params.add(img.getPublicId());
            params.add(img.getCreatedAt());
            params.add(startOrder + i);
        }
        return _cli.executeUpdate(sql.toString(), params.toArray());
    }
    
    public int getMaxOrderNum(long carId) {
        final int[] max = {0};
        String sql = "SELECT COALESCE(MAX(orderNum),0) AS mx FROM " + TABLE + " WHERE carId=?";
        _cli.executeQuery(new MysqlClient.IRowListener() {
            @Override
            public void onRow(ResultSet rs) throws SQLException {
                max[0] = rs.getInt("mx");
            }
        }, sql, carId);
        return max[0];
    }
    
    public ValueResult<List<String>> getPublicIdsByIds(int carId, List<Long> ids)
    {
        StringBuilder sb = new StringBuilder("SELECT publicId FROM " + TABLE + " WHERE id IN(");
        List<Object> params = new ArrayList<Object>();
        for(int i = 0; i < ids.size(); i++)
        {
            if(i > 0) sb.append(",");
            sb.append("?");
            params.add(ids.get(i));
        }
        sb.append(") AND carId=?");
        params.add(carId);
        ValueResult<List<String>> result = new ValueResult<List<String>>(Err.FAIL);
        result.value = new ArrayList<String>();
        result.error = _cli.executeQuery(new MysqlClient.IRowListener() {
            @Override
            public void onRow(ResultSet rs) throws SQLException {
                result.value.add(rs.getString("publicId"));
            }
        }, sb.toString(), params.toArray());
        return result;
    }

    public ValueResult<List<TCarImage>> getCarImagesByCarId(long carId) {
        ValueResult<List<TCarImage>> result = new ValueResult<List<TCarImage>>(Err.FAIL);
        result.value = new ArrayList<TCarImage>();
        String sql = "SELECT " + COLS + " FROM " + TABLE
                + " WHERE carId=? ORDER BY orderNum ASC, id ASC";
        result.error = _cli.executeQuery(new MysqlClient.IRowListener() {
            @Override
            public void onRow(ResultSet rs) throws SQLException {
                result.value.add(map(rs));
            }
        }, sql, carId);
        return result;
    }
    
    public long countImagesOfCar(long carId)
    {
        String sql = "SELECT COUNT(id) FROM " + TABLE + " WHERE carId=?";
        return _cli.executeQuery(null, sql, carId);
    }

    public long deleteListCarImages(long carId, List<Long> carImageIds) {
        if (carImageIds == null || carImageIds.isEmpty()) {
            return 0;
        }
        StringBuilder sql = new StringBuilder("DELETE FROM " + TABLE + " WHERE carId=? AND " + KEY + " IN (");
        List<Object> params = new ArrayList<Object>();
        params.add(carId);
        for (int i = 0; i < carImageIds.size(); i++) {
            sql.append(i == 0 ? "?" : ",?");
            params.add(carImageIds.get(i));
        }
        sql.append(")");
        return _cli.executeUpdate(sql.toString(), params.toArray());
    }

    private TCarImage map(ResultSet rs) throws SQLException {
        TCarImage carImage = new TCarImage();
        carImage.setId(rs.getInt("id"));
        carImage.setCarId(rs.getInt("carId"));
        carImage.setPublicId(rs.getString("publicId"));
        carImage.setCreatedAt(rs.getLong("createdAt"));
        carImage.setOrderNum(rs.getInt("orderNum"));
        return carImage;
    }
}
