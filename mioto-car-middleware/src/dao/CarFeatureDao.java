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
import thrift.TCarFeature;
import thrift.TCarFeatureView;

/**
 *
 * @author tuanlee
 */
public class CarFeatureDao {
    private static final Logger _Logger = Logger.getLogger(CarFeatureDao.class);
    private static final String TABLE   = "CarFeatures";
    private static final String KEY     = "id";
    private static final String COLS    = "id,carId,featureId"; // them vai cot ma join chung voi nhau di 
    
    private final MysqlClient _cli;
    
    public CarFeatureDao(String name)
    {
        _cli = new MysqlClient(name);
    }
    
    public MysqlClient getClient()
    {
        return _cli;
    }
    
    public long createListCarFeatures(List<TCarFeature> carFeatures) {
        if (carFeatures == null || carFeatures.isEmpty()) {
            return 0;
        }
        StringBuilder sql = new StringBuilder("INSERT INTO " + TABLE + " (carId,featureId) VALUES ");
        List<Object> params = new ArrayList<Object>();
        for (int i = 0; i < carFeatures.size(); i++) {
            sql.append(i == 0 ? "(?,?)" : ",(?,?)");
            params.add(carFeatures.get(i).getCarId());
            params.add(carFeatures.get(i).getFeatureId());
        }
        return _cli.executeUpdate(sql.toString(), params.toArray());
    }

    // Join Features để lấy tên tính năng
    public ValueResult<List<TCarFeatureView>> getListCarFeatures(long carId) {
        ValueResult<List<TCarFeatureView>> result = new ValueResult<List<TCarFeatureView>>(Err.FAIL);
        result.value = new ArrayList<TCarFeatureView>();
        String sql = "SELECT cf.id,cf.carId,cf.featureId,f.nameFeature AS featureName"
                + " FROM " + TABLE + " cf JOIN Features f ON f.id=cf.featureId"
                + " WHERE cf.carId=? ORDER BY cf.id";
        result.error = _cli.executeQuery(new MysqlClient.IRowListener() {
            @Override
            public void onRow(ResultSet rs) throws SQLException {
                result.value.add(map(rs));
            }
        }, sql, carId);
        return result;
    }

    public long deleteListCarFeatures(long carId, List<Long> carFeatureIds) {
        if (carFeatureIds == null || carFeatureIds.isEmpty()) {
            return 0;
        }
        StringBuilder sql = new StringBuilder("DELETE FROM " + TABLE + " WHERE carId=? AND " + KEY + " IN (");
        List<Object> params = new ArrayList<Object>();
        params.add(carId);
        for (int i = 0; i < carFeatureIds.size(); i++) {
            sql.append(i == 0 ? "?" : ",?");
            params.add(carFeatureIds.get(i));
        }
        sql.append(")");
        return _cli.executeUpdate(sql.toString(), params.toArray());
    }
    
    private TCarFeatureView map(ResultSet rs) throws SQLException
    {
        TCarFeatureView view = new TCarFeatureView();
        view.setId(rs.getInt("id"));
        view.setCarId(rs.getInt("carId"));
        view.setFeatureId(rs.getInt("featureId"));
        view.setFeatureName(rs.getString("featureName"));
        return view;
    }
}
