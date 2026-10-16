/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import cache.SimpleCache;
import dao.CarUnavailDao;
import error.Err;
import error.ValueResult;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import thrift.TCarUnavails;
import thrift.TCarUnavailsResult;
import thrift.TListCarUnavailsResult;

/**
 *
 * @author tuanlee
 */
public class CarUnavailModel {
    private static final Logger _Logger = Logger.getLogger(CarUnavailModel.class);
 
    public static final CarUnavailModel Instance = new CarUnavailModel();
    private final CarUnavailDao _dao = new CarUnavailDao("mioto");
    // cache theo carId, giá trị là danh sách lịch bận do chủ xe tự khóa
    private final SimpleCache<Integer, List<TCarUnavails>> _cache
            = new SimpleCache<Integer, List<TCarUnavails>>("common");
 
    private CarUnavailModel() {}
    
    public TListCarUnavailsResult getListCarUnavails(long carId) {
        List<TCarUnavails> cached = _cache.get((int) carId);
        if (cached != null) {
            return success(copy(cached));
        }
 
        ValueResult<List<TCarUnavails>> ret = _dao.getListCarUnavails(carId);

        if (Err.isFail(ret.error) && !Err.isNotFound(ret.error)) {
            return new TListCarUnavailsResult((int) ret.error, "Lỗi kết nối mạng");
        }
        List<TCarUnavails> value = ret.value != null ? ret.value : new ArrayList<TCarUnavails>();
        _cache.put((int) carId, copy(value));
        return success(copy(value));
    }
    
    public TCarUnavailsResult createCarUnavails(TCarUnavails unavail, int userId) {  
        if(unavail == null) return new TCarUnavailsResult(Err.BAD_REQUEST, "");
        int error = CarModel.Instance.checkIsOwnerCar(userId, unavail.getCarId());
        if(Err.isFail(error)) return new TCarUnavailsResult(error, "");
        
        return this.createCarUnavails(unavail);
    }
 
    public TCarUnavailsResult createCarUnavails(TCarUnavails unavail) {  
        long ret = _dao.createCarUnavails(unavail);
        if (Err.isFail(ret)) {
            return new TCarUnavailsResult((int) ret, "Không thể khóa lịch xe");
        }
        long carId = unavail.getCarId();
        unavail.setId((int) ret);
        _cache.remove((int) carId);
 
        TCarUnavailsResult result = new TCarUnavailsResult((int) Err.SUCCESS, "Khóa lịch thành công");
        result.setValue(unavail);
        return result;
    }
    
    public long deleteCarUnavails(long carId, long carUnavailId, int userId) {
        int error = CarModel.Instance.checkIsOwnerCar(userId, carId);
        if(Err.isFail(error)) return error;
        return this.deleteCarUnavails(carId, carUnavailId);
    }
 
    public long deleteCarUnavails(long carId, long carUnavailId) {
        TListCarUnavailsResult current = getListCarUnavails(carId);
        if (!Err.isSuccess(current.error)) {
            return current.error;
        }
        boolean belongs = false;
        for (TCarUnavails u : current.value) {
            if (u.getId() == carUnavailId) {
                belongs = true;
                break;
            }
        }
        if (!belongs) {
            return Err.NOT_FOUND; 
        }
 
        long ret = _dao.deleteCarUnavails(carId, carUnavailId);
        if (ret == 0) {
            return Err.NOT_FOUND;
        }
        if (ret < 0) {
            return ret;
        }
        _cache.remove((int) carId);
        return ret;
    }
 
    private TListCarUnavailsResult success(List<TCarUnavails> value) {
        TListCarUnavailsResult result = new TListCarUnavailsResult((int) Err.SUCCESS, "Lấy dữ liệu thành công");
        result.setValue(value);
        return result;
    }
 
    private List<TCarUnavails> copy(List<TCarUnavails> src) {
        List<TCarUnavails> dst = new ArrayList<TCarUnavails>(src.size());
        for (TCarUnavails u : src) {
            dst.add(new TCarUnavails(u));
        }
        return dst;
    }
}
