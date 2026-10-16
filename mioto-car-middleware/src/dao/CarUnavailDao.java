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
import thrift.TCarUnavails;

/**
     *
 * @author tuanlee
 */
public class CarUnavailDao {
    private static final Logger _Logger = Logger.getLogger(CarUnavailDao.class);
    private static final String TABLE   = "CarUnavails";
    private static final String KEY     = "id";
    private static final String COLS    = "id,carId,startTime,endTime,createdAt,updatedAt";
    
    private final MysqlClient _cli;
    public CarUnavailDao(String name)
    {
        _cli = new MysqlClient(name);
    }
    
    public MysqlClient getClient()
    {
        return _cli;
    }
    
    public long createCarUnavails(TCarUnavails u) {
        String sql = "INSERT INTO " + TABLE + " (carId,startTime,endTime,createdAt,updatedAt)"
                + " VALUES (?,?,?,?,?)";
        return _cli.executeInsertAndReturnKey(sql, u.getCarId(), u.getStartTime(), u.getEndTime(),
                u.getCreatedAt(), u.getUpdatedAt());
    }

    public ValueResult<List<TCarUnavails>> getListCarUnavails(long carId) {
        ValueResult<List<TCarUnavails>> result = new ValueResult<List<TCarUnavails>>(Err.FAIL);
        result.value = new ArrayList<TCarUnavails>();
        String sql = "SELECT " + COLS + " FROM " + TABLE + " WHERE carId=? ORDER BY startTime ASC";
        result.error = _cli.executeQuery(new MysqlClient.IRowListener() {
            @Override
            public void onRow(ResultSet rs) throws SQLException {
                result.value.add(map(rs));
            }
        }, sql, carId);
        return result;
    }

    public long deleteCarUnavails(long carId, long carUnavailId) {
        String sql = "DELETE FROM " + TABLE + " WHERE carId=? AND " + KEY + "=?";
        return _cli.executeUpdate(sql, carId, carUnavailId);
    }
    
    private TCarUnavails map(ResultSet rs) throws SQLException
    {
        TCarUnavails carUnavails = new TCarUnavails();
        carUnavails.setCarId(rs.getInt("carId"));
        carUnavails.setCreatedAt(rs.getLong("createdAt"));
        carUnavails.setUpdatedAt(rs.getLong("updatedAt"));
        carUnavails.setStartTime(rs.getLong("startTime"));
        carUnavails.setEndTime(rs.getLong("endTime"));
        carUnavails.setId(rs.getInt("id"));
        return carUnavails;
    }
}
