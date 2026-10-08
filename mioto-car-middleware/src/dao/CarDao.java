package dao;

import db.MysqlClient;
import error.Err;
import error.ValueResult;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import thrift.TCar;
import thrift.TCarFilterRequest;
import thrift.TCarStatus;
import thrift.TCarView;

/**
 *
 * @author tuanlee
 */
public class CarDao {

    private static final Logger _Logger = Logger.getLogger(CarDao.class);
    private static final String TABLE = "Cars";
    private static final String UNAVAIL_TABLE = "CarUnavails";
    private static final String KEY = "carId";
    private static final String COLS = "carId,carName,productionYear,carBrandId,numSeats,transmission,typeFuel,"
            + "fuelConsumption,description,pricePerDay,policy,districtId,userId,status,createdAt,"
            + "updatedAt";

    private static final String VIEW_COLS = "c.carId,c.carName,c.productionYear,c.carBrandId,c.numSeats,"
            + "c.transmission,c.typeFuel,c.fuelConsumption,c.description,c.pricePerDay,c.policy,"
            + "c.districtId,c.userId,c.status,c.createdAt,c.updatedAt,"
            + "b.nameBrand AS brandName,"
            + "d.nameDistrict AS districtName,"
            + "p.nameProvince AS provinceName,"
            + "u.displayName AS ownerName";
            
    private static final String VIEW_FROM = " FROM " + TABLE + " c"
            + " JOIN CarBrands b ON b.id=c.carBrandId"
            + " JOIN Districts d ON d.id=c.districtId"
            + " JOIN Provinces p ON p.id=d.provinceId"
            + " JOIN Users u ON u.userId=c.userId";
    
    private static final String THUMB_COL = ", (SELECT i.imageUrl FROM CarImages i WHERE i.carId=c.carId"
            + " ORDER BY i.orderNum ASC, i.id ASC LIMIT 1) AS thumbnailUrl";

    private final MysqlClient _cli;

    public CarDao(String name) {
        _cli = new MysqlClient(name);
    }

    public MysqlClient getClient() {
        return _cli;
    }

    public long createCar(TCar car) {
        String sql = "INSERT INTO " + TABLE
                + " (carName,productionYear,carBrandId,numSeats,transmission,typeFuel,"
                + " fuelConsumption,description,pricePerDay,policy,districtId,userId,status,createdAt,"
                + " updatedAt)"
                + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        return _cli.executeInsertAndReturnKey(sql, car.getCarName(), car.getProductionYear(), car.getCarBrandId(),
                car.getNumSeats(), car.getTransmission(), car.getTypeFuel(), car.getFuelConsumption(),
                car.getDescription(), car.getPricePerDay(), car.getPolicy(), car.getDistrictId(), car.getUserId(),
                TCarStatus.TC_PENDING.getValue(), car.getCreatedAt(), car.getUpdatedAt());
    }
    
    public long updateCar(TCar car) {
        String sql = "UPDATE " + TABLE
                + " SET carName=?,productionYear=?,carBrandId=?,numSeats=?,transmission=?,typeFuel=?,fuelConsumption=?,"
                + " description=?,pricePerDay=?,policy=?,districtId=?,updatedAt=?,status=?"
                + " WHERE " + KEY + "=?";
        return _cli.executeUpdate(sql, car.getCarName(), car.getProductionYear(), car.getCarBrandId(),
                car.getNumSeats(), car.getTransmission(), car.getTypeFuel(), car.getFuelConsumption(),
                car.getDescription(), car.getPricePerDay(), car.getPolicy(), car.getDistrictId(),
                car.getUpdatedAt(), TCarStatus.TC_PENDING.getValue(), car.getCarId());
    }
    
    public int checkIsOwnerCar(long userId, long carId)
    {
        int error;
        String sql = "SELECT 1 FROM " + TABLE + " WHERE " + KEY + "=? AND userId= ? LIMIT 1";
        error = _cli.executeQuery(null, sql, carId, userId);
        return error;
    }
    
    public long updateStatusCar(TCar car) {
        String sql = "UPDATE " + TABLE + " SET status=? WHERE " + KEY + "=?";
        return _cli.executeUpdate(sql, car.getStatus(), car.getCarId());
    }

    public ValueResult<TCarView> getCarViewById(long carId) {
        ValueResult<TCarView> result = new ValueResult<TCarView>(Err.FAIL);
        String sql = "SELECT " + VIEW_COLS + THUMB_COL + VIEW_FROM + " WHERE c." + KEY + "=?";
        result.error = _cli.executeQuery(new MysqlClient.IRowListener() {
            @Override
            public void onRow(ResultSet rs) throws SQLException {
                result.value = mapView(rs);
            }
        }, sql, carId);
        return result;
    }

    public ValueResult<List<TCarView>> getCarViewByUserId(long userId, int count, int offset) {
        ValueResult<List<TCarView>> result = new ValueResult<List<TCarView>>(Err.FAIL);
        result.value = new ArrayList<TCarView>();
        String sql = "SELECT " + VIEW_COLS + THUMB_COL + VIEW_FROM
                + " WHERE c.userId=?"
                + " ORDER BY c.createdAt DESC, c.carId DESC LIMIT ? OFFSET ?";
        result.error = _cli.executeQuery(new MysqlClient.IRowListener() {
            @Override
            public void onRow(ResultSet rs) throws SQLException {
                result.value.add(mapView(rs));
            }
        }, sql, userId, (count <= 0 || count > 100) ? 20 : count, Math.max(0, offset));
        return result;
    }

    // Check admin ở service: admin truyền status tùy ý, không phải admin thì truyền status = 1
public ValueResult<List<TCarView>> searchCar(TCarFilterRequest filter, long startTime, long endTime, int status, int count, int offset) {
        ValueResult<List<TCarView>> result = new ValueResult<List<TCarView>>(Err.FAIL);
        result.value = new ArrayList<TCarView>();
        StringBuilder sql = new StringBuilder("SELECT " + VIEW_COLS + THUMB_COL + VIEW_FROM + " WHERE 1=1");
        List<Object> params = new ArrayList<Object>();

        if (filter != null) {
            if (filter.isSetProductionYear()) {
                sql.append(" AND c.productionYear<=?");
                params.add(filter.getProductionYear());
            }
            if (filter.isSetCarBrandId()) {
                sql.append(" AND c.carBrandId=?");
                params.add(filter.getCarBrandId());
            }
            if (filter.isSetNumSeats()) {
                sql.append(" AND c.numSeats=?");
                params.add(filter.getNumSeats());
            }
            if (filter.isSetTransmission()) {
                sql.append(" AND c.transmission=?");
                params.add(filter.getTransmission().getValue());
            }
            if (filter.isSetTypeFuel()) {
                sql.append(" AND c.typeFuel=?");
                params.add(filter.getTypeFuel().getValue());
            }
            if (filter.isSetFuelConsumption()) {
                sql.append(" AND c.fuelConsumption<=?");
                params.add(filter.getFuelConsumption());
            }
            if (filter.isSetPricePerDay()) {
                sql.append(" AND c.pricePerDay<=?");
                params.add(filter.getPricePerDay());
            }
            if (filter.isSetDistrictId()) {
                sql.append(" AND c.districtId=?");
                params.add(filter.getDistrictId());
            }
            if (filter.isSetProvinceId()) {
                sql.append(" AND d.provinceId=?");
                params.add(filter.getProvinceId());
            }
        }
        if (status > 0) {
            sql.append(" AND c.status=?");
            params.add(status);
        }

        // Chỉ lấy xe rảnh trong [startTime, endTime): không có dòng bận nào chồng lấn
        if (startTime > 0 && endTime > startTime) {
            sql.append(" AND NOT EXISTS (SELECT 1 FROM " + UNAVAIL_TABLE + " u"
                    + " WHERE u.carId=c.carId AND u.startTime<? AND u.endTime>?)");
            params.add(endTime);   // busyStart < end
            params.add(startTime); // busyEnd   > start

            sql.append(" AND NOT EXISTS (SELECT 1 FROM Bookings bk"
                    + " WHERE bk.carId=c.carId AND bk.status IN (1,2,3,4)"
                    + " AND bk.startDate<? AND bk.endDate>?)");
            params.add(endTime);
            params.add(startTime);

        }

        sql.append(" ORDER BY c.createdAt DESC, c.carId DESC LIMIT ? OFFSET ?");
        params.add((count <= 0 || count > 100) ? 20 : count);
        params.add(Math.max(0, offset));

        result.error = _cli.executeQuery(new MysqlClient.IRowListener() {
            @Override
            public void onRow(ResultSet rs) throws SQLException {
                result.value.add(mapView(rs));
            }
        }, sql.toString(), params.toArray());
        return result;
    }


    private TCarView mapView(ResultSet rs) throws SQLException {
        TCarView view = new TCarView();
        view.setCar(mapCar(rs));
        view.setBrandName(rs.getString("brandName"));
        view.setDistrictName(rs.getString("districtName"));
        view.setThumbnailUrl(rs.getString("thumbnailUrl"));
        view.setProvinceName(rs.getString("provinceName"));
        view.setOwnerName(rs.getString("ownerName"));
        return view;
    }

    private TCar mapCar(ResultSet rs) throws SQLException {
        TCar car = new TCar();
        car.setCarId(rs.getInt("carId"));
        car.setCarName(rs.getString("carName"));
        car.setProductionYear(rs.getInt("productionYear"));
        car.setCarBrandId(rs.getInt("carBrandId"));
        car.setNumSeats(rs.getByte("numSeats"));
        car.setTransmission(rs.getByte("transmission"));
        car.setTypeFuel(rs.getByte("typeFuel"));
        car.setFuelConsumption(rs.getBigDecimal("fuelConsumption").toPlainString());
        car.setDescription(rs.getString("description"));
        car.setPricePerDay(rs.getBigDecimal("pricePerDay").toPlainString());
        car.setPolicy(rs.getString("policy"));
        car.setDistrictId(rs.getInt("districtId"));
        car.setUserId(rs.getInt("userId"));
        car.setStatus(rs.getInt("status"));
        car.setCreatedAt(rs.getLong("createdAt"));
        car.setUpdatedAt(rs.getLong("updatedAt"));
        return car;
    }
}