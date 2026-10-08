/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import cache.SimpleCache;
import dao.CarFeatureDao;
import error.Err;
import error.ValueResult;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;
import thrift.TCarFeature;
import thrift.TCarFeatureView;
import thrift.TListCarFeatureViewResult;

/**
 *
 * @author tuanlee
 */
public class CarFeatureModel {
    private static final Logger _Logger = Logger.getLogger(CarFeatureModel.class);
 
    public static final CarFeatureModel Instance = new CarFeatureModel();
    private final CarFeatureDao _dao = new CarFeatureDao("mioto");
    private final SimpleCache<Integer, List<TCarFeatureView>> _cache
            = new SimpleCache<Integer, List<TCarFeatureView>>("common");
 
    private CarFeatureModel() {
    }
  
    public TListCarFeatureViewResult getListCarFeatures(long carId) {
        List<TCarFeatureView> cached = _cache.get((int) carId);
        if (cached != null) {
            return success(copy(cached));
        }
 
        ValueResult<List<TCarFeatureView>> ret = _dao.getListCarFeatures(carId);
        if (Err.isFail(ret.error) && !Err.isNotFound(ret.error)) {
            return new TListCarFeatureViewResult((int) ret.error, "Lỗi kết nối mạng");
        }
        List<TCarFeatureView> value = ret.value != null ? ret.value : new ArrayList<TCarFeatureView>();
        _cache.put((int) carId, copy(value));
        return success(copy(value));
    }
    
    public TListCarFeatureViewResult createCarFeatures(List<TCarFeature> features, int userId) {
        if(features == null || features.isEmpty())
        {
            return new TListCarFeatureViewResult(Err.BAD_REQUEST, "");
        }
        
        int error = CarModel.Instance.checkIsOwnerCar(userId, features.get(0).getCarId());
        if(Err.isFail(error)) return new TListCarFeatureViewResult(error, "");
        return this.createCarFeatures(features);
    }
 
    public TListCarFeatureViewResult createCarFeatures(List<TCarFeature> features) {
        if (features == null || features.isEmpty()) {
            return new TListCarFeatureViewResult(Err.BAD_REQUEST, "");
        }
        
        long carId = features.get(0).getCarId();
 
        TListCarFeatureViewResult current = getListCarFeatures(carId);
        if (!Err.isSuccess(current.error)) {
            return current;
        }
        Set<Integer> existed = new HashSet<Integer>();
        for (TCarFeatureView v : current.value) {
            existed.add(v.getFeatureId());
        }
 
        List<TCarFeature> carFeatures = new ArrayList<TCarFeature>();
        for (TCarFeature f : features) {
            if (existed.add(f.getFeatureId())) { // add trả false nếu đã có -> bỏ qua
                TCarFeature c = new TCarFeature();
                c.setCarId((int) carId);
                c.setFeatureId(f.getFeatureId());
                carFeatures.add(c);
            }
        }
        if (carFeatures.isEmpty()) {
            return current;
        }
 
        long ret = _dao.createListCarFeatures(carFeatures);
        if (Err.isFail(ret)) {
            return new TListCarFeatureViewResult((int) ret, "Không thể thêm tính năng");
        }
        _cache.remove((int) carId);
        return getListCarFeatures(carId);
    }
 
    public long deleteCarFeatures(long carId, List<Long> carFeatureIds, long userId) {
        int error = CarModel.Instance.checkIsOwnerCar(userId, carId);
        if(Err.isFail(error)) return (long) error;
        return this.deleteCarFeatures(carId, carFeatureIds);
    }
    // carFeatureIds là id của dòng CarFeatures (không phải featureId). Trả về số dòng đã xóa.
    public long deleteCarFeatures(long carId, List<Long> carFeatureIds) {
        if (carFeatureIds == null || carFeatureIds.isEmpty()) {
            return 0;
        }
 
        TListCarFeatureViewResult current = getListCarFeatures(carId);
        if (!Err.isSuccess(current.error)) {
            return current.error;
        }
        Set<Long> owned = new HashSet<Long>();
        for (TCarFeatureView v : current.value) {
            owned.add((long) v.getId());
        }
        Set<Long> ids = new LinkedHashSet<Long>(carFeatureIds); 
        for (Long id : ids) {
            if (!owned.contains(id)) {
                return Err.NOT_FOUND; 
            }
        }
 
        long ret = _dao.deleteListCarFeatures(carId, new ArrayList<Long>(ids));
        if (ret < 0) {
            return ret;
        }
        _cache.remove((int) carId);
        return ret;
    }
 
    private TListCarFeatureViewResult success(List<TCarFeatureView> value) {
        TListCarFeatureViewResult result
                = new TListCarFeatureViewResult((int) Err.SUCCESS, "Lấy dữ liệu thành công");
        result.setValue(value);
        return result;
    }
 
    private List<TCarFeatureView> copy(List<TCarFeatureView> src) {
        List<TCarFeatureView> dst = new ArrayList<TCarFeatureView>(src.size());
        for (TCarFeatureView v : src) {
            dst.add(new TCarFeatureView(v));
        }
        return dst;
    }
}
