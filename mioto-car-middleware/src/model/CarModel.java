/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import cache.SimpleCache;
import dao.CarDao;
import error.Err;
import error.ValueResult;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import thrift.TCar;
import thrift.TCarDetail;
import thrift.TCarDetailResult;
import thrift.TCarFeature;
import thrift.TCarFilterRequest;
import thrift.TCarImage;
import thrift.TCarResult;
import thrift.TCarStatus;
import thrift.TCarView;
import thrift.TListCarFeatureViewResult;
import thrift.TListCarImageResult;
import thrift.TListCarUnavailsResult;
import thrift.TListCarViewResult;

/**
 *
 * @author tuanlee
 */
public class CarModel {
    private static final Logger _Logger = Logger.getLogger(CarModel.class);
 
    public static final CarModel Instance = new CarModel();
    private static final TCarStatus DEFAULT_STATUS = TCarStatus.TC_PENDING;
    private final CarDao _dao = new CarDao("mioto");
    // cache theo carId, giá trị là chi tiết xe (TCarView)
    private final SimpleCache<Integer, TCarView> _cache = new SimpleCache<Integer, TCarView>("common");
 
    private CarModel() {
    }
     // car.userId do servlet gán từ session
    public TCarResult createCar(TCar car, List<TCarImage> images, List<TCarFeature> features) {
        TCar newCar = new TCar(car);
 
        long ret = _dao.createCar(newCar);
        if (Err.isFail(ret)) {
            return new TCarResult((int) ret, "Không thể tạo xe");
        }
        long carId = ret;
        newCar.setCarId((int) carId);
        
        if (images != null) {
            for (TCarImage i : images) {
                i.setCarId((int) carId);
            }
        }
        if (features != null) {
            for (TCarFeature f : features) {
                f.setCarId((int) carId);
            }
        }
 
        if (images != null && !images.isEmpty()) {
            TListCarImageResult r = CarImageModel.Instance.createCarImages(images);
            if (!Err.isSuccess(r.error)) {
                return new TCarResult(r.error, r.message);
            }
        }
        if (features != null && !features.isEmpty()) {
            TListCarFeatureViewResult r = CarFeatureModel.Instance.createCarFeatures(features);
            if (!Err.isSuccess(r.error)) {
                return new TCarResult(r.error, r.message);
            }
        }
 
        TCarResult result = new TCarResult((int) Err.SUCCESS, "");
        result.setValue(newCar);
        return result;
    }
 
    public TCarResult updateCar(TCar car) { 
        int error = _dao.checkIsOwnerCar(car.getUserId(), car.getCarId());
        if(Err.isFail(error))
        {
            if(Err.isNotFound(error)) return new TCarResult(Err.FORBIDDEN, "Bạn không phải chủ của xe này");
            return new TCarResult(Err.FAIL, "Lỗi hệ thống");
        }
        TCar newCar = new TCar(car);
        long ret = _dao.updateCar(newCar);
        if (ret == 0) {
            return new TCarResult((int) Err.NOT_FOUND, "");
        }
        if (ret < 0) {
            return new TCarResult((int) ret, "");
        }
        _cache.remove(car.getCarId());
 
        ValueResult<TCarView> view = loadCarView(car.getCarId());
        TCarResult result = new TCarResult((int) Err.SUCCESS, "Cập nhật xe thành công");
        result.setValue(Err.isSuccess(view.error) ? view.value.getCar() : newCar);
        return result;
    }
 
    // Admin khóa / mở xe
    public TCarResult updateStatusCar(TCar car) {
        if(car.getStatus() == TCarStatus.TC_ACTIVE.getValue())
        {
            long resultUser = UserModel.Instance.setIsOwnCarForUserByAdmin(car.getUserId());
            if (Err.isFail(resultUser)) {
                if (Err.isNotFound(resultUser)) {
                    return new TCarResult(Err.NOT_FOUND, "Không tìm thấy thông tin chủ xe");
                }
                return new TCarResult(Err.FAIL, "Lỗi hệ thống");
            }
        }    
        long ret = _dao.updateStatusCar(car);
        if (ret == 0) {
            return new TCarResult((int) Err.NOT_FOUND, "Không tìm thấy xe");
        }
        if (ret < 0) {
            return new TCarResult((int) ret, "Không thể cập nhật trạng thái xe");
        }
        _cache.remove((int) car.getCarId());

        TCarResult result = new TCarResult((int) Err.SUCCESS, "Cập nhật trạng thái thành công");
        ValueResult<TCarView> view = loadCarView(car.getCarId());
        if (Err.isSuccess(view.error)) {
            result.setValue(view.value.getCar());
        }
        return result;
    }
 
    public TCarDetailResult getCarById(long carId) {
        ValueResult<TCarView> car = loadCarView(carId);
        if (Err.isNetworkError(car.error)) {
            return new TCarDetailResult((int) car.error, "Lỗi kết nối mạng");
        }
        if (Err.isNotFound(car.error)) {
            return new TCarDetailResult((int) Err.NOT_FOUND, "Không tìm thấy xe");
        }
        if (!Err.isSuccess(car.error)) {
            return new TCarDetailResult((int) car.error, "Không thể lấy thông tin xe");
        }
 
        TListCarImageResult images = CarImageModel.Instance.getCarImagesByCarId(carId);
        if (!Err.isSuccess(images.error)) {
            return new TCarDetailResult(images.error, images.message);
        }
        TListCarFeatureViewResult features = CarFeatureModel.Instance.getListCarFeatures(carId);
        if (!Err.isSuccess(features.error)) {
            return new TCarDetailResult(features.error, features.message);
        }
        TListCarUnavailsResult unavails = CarUnavailModel.Instance.getListCarUnavails(carId);
        if (!Err.isSuccess(unavails.error)) {
            return new TCarDetailResult(unavails.error, unavails.message);
        }
 
        TCarDetail detail = new TCarDetail();
        detail.setCarView(car.value);
        detail.setImages(images.value);
        detail.setFeatures(features.value);
        detail.setUnavails(unavails.value);
 
        TCarDetailResult result = new TCarDetailResult((int) Err.SUCCESS, "Lấy dữ liệu thành công");
        result.setValue(detail);
        return result;
    }
    
    public int checkIsOwnerCar(long userId, long carId) {
        int error = _dao.checkIsOwnerCar(userId, carId);
        if (Err.isSuccess(error)) {
            return Err.SUCCESS;
        }
        if (Err.isNotFound(error)) {
            return Err.FORBIDDEN;  
        }
        return error;                                     
    }
    
    public TListCarViewResult getCarViewByUserId(long userId, int count, int offset) {
        ValueResult<List<TCarView>> ret = _dao.getCarViewByUserId(userId, count, offset);
        return buildListResult(ret);
    }
 
    public TListCarViewResult searchCar(TCarFilterRequest filter, long startTime, long endTime, int status, int count, int offset) {
        ValueResult<List<TCarView>> ret = _dao.searchCar(filter, startTime, endTime, status, count, offset);
        return buildListResult(ret);
    }
  
    private ValueResult<TCarView> loadCarView(long carId) {
        TCarView cached = _cache.get((int) carId);
        if (cached != null) {
            ValueResult<TCarView> hit = new ValueResult<TCarView>(Err.SUCCESS);
            hit.value = new TCarView(cached);
            return hit;
        }
        ValueResult<TCarView> ret = _dao.getCarViewById(carId);
        if (Err.isSuccess(ret.error)) {
            if (ret.value == null) {
                ret.error = Err.NOT_FOUND;
                return ret;
            }
            _cache.put((int) carId, new TCarView(ret.value));
            ret.value = new TCarView(ret.value);
        }
        return ret;
    }
 
    private TListCarViewResult buildListResult(ValueResult<List<TCarView>> ret) {
        if (Err.isFail(ret.error) && !Err.isNotFound(ret.error)) {
            return new TListCarViewResult((int) ret.error, "Lỗi kết nối mạng");
        }
        TListCarViewResult result = new TListCarViewResult((int) Err.SUCCESS, "Lấy dữ liệu thành công");
        result.setValue(ret.value != null ? ret.value : new ArrayList<TCarView>());
        return result;
    }
}
