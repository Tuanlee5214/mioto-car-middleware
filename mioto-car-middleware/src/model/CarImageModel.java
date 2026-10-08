/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import cache.SimpleCache;
import dao.CarImageDao;
import error.Err;
import error.ValueResult;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;
import thrift.TCarImage;
import thrift.TListCarImageResult;

/**
 *
 * @author tuanlee
 */
public class CarImageModel {
    private static final Logger _Logger = Logger.getLogger(CarImageModel.class);
 
    public static final CarImageModel Instance = new CarImageModel();
    private final CarImageDao _dao = new CarImageDao("mioto");
    // cache theo carId, giá trị là danh sách ảnh của xe
    private final SimpleCache<Integer, List<TCarImage>> _cache = new SimpleCache<Integer, List<TCarImage>>("common");
 
    private CarImageModel() {
    }
  
    public TListCarImageResult getCarImagesByCarId(long carId) {
        List<TCarImage> cached = _cache.get((int) carId);
        if (cached != null) {
            return success(copy(cached));
        }
 
        ValueResult<List<TCarImage>> ret = _dao.getCarImagesByCarId(carId);
        if (Err.isFail(ret.error) && !Err.isNotFound(ret.error)) {
            return new TListCarImageResult((int) ret.error, "Lỗi kết nối mạng");
        }
        List<TCarImage> value = ret.value != null ? ret.value : new ArrayList<TCarImage>();
        _cache.put((int) carId, copy(value));
        return success(copy(value));
    }
    
    public TListCarImageResult createCarImages(List<TCarImage> images, int userId) {
        if(images == null || images.isEmpty()) return new TListCarImageResult(Err.BAD_REQUEST, "");
        int error = CarModel.Instance.checkIsOwnerCar(userId, images.get(0).getCarId());
        if(Err.isFail(error)) return new TListCarImageResult(error, "");
        
        return this.createCarImages(images);
    }
    // Client chỉ gửi imageUrl, publicId. carId, createdAt, orderNum do server gán.
    public TListCarImageResult createCarImages( List<TCarImage> images) {
        if (images == null || images.isEmpty()) {
            return new TListCarImageResult(Err.BAD_REQUEST, "");
        }
        long carId = images.get(0).getCarId();
        
        int max = _dao.getMaxOrderNum(carId);
        long ret = _dao.createListCarImages(images, max + 1);
        if (Err.isFail(ret)) {
            return new TListCarImageResult((int) ret, "");
        }
        _cache.remove((int) carId);
        return getCarImagesByCarId(carId);
    }
 
    public long deleteCarImages(long carId, List<Long> imageIds, int userId) {
        int error = CarModel.Instance.checkIsOwnerCar(userId, carId);
        if(Err.isFail(error)) return error;
        
        return this.deleteCarImages(carId, imageIds);
    }
    
    public long deleteCarImages(long carId, List<Long> imageIds) {
        if (imageIds == null || imageIds.isEmpty()) {
            return 0;
        }
 
        TListCarImageResult current = getCarImagesByCarId(carId);
        if (!Err.isSuccess(current.error)) {
            return current.error;
        }
        Set<Long> owned = new HashSet<Long>();
        for (TCarImage img : current.value) {
            owned.add((long) img.getId());
        }
        Set<Long> ids = new LinkedHashSet<Long>(imageIds); 
        for (Long id : ids) {
            if (!owned.contains(id)) {
                return Err.NOT_FOUND;             
            }
        }
 
        long ret = _dao.deleteListCarImages(carId, new ArrayList<Long>(ids));
        if (ret < 0) {
            return ret;
        }
        _cache.remove((int) carId);
        return ret;
    }
 
    private TListCarImageResult success(List<TCarImage> value) {
        TListCarImageResult result = new TListCarImageResult((int) Err.SUCCESS, "Lấy dữ liệu thành công");
        result.setValue(value);
        return result;
    }
 
    private List<TCarImage> copy(List<TCarImage> src) {
        List<TCarImage> dst = new ArrayList<TCarImage>(src.size());
        for (TCarImage i : src) {
            dst.add(new TCarImage(i));
        }
        return dst;
    }
}
