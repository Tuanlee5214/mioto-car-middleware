/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import cache.SimpleCache;
import dao.CarBrandDao;
import error.Err;
import error.ValueResult;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import thrift.TCarBrand;
import thrift.TCarBrandResult;
import thrift.TListCarBrandResult;

/**
 *
 * @author tuanlee
 */
public class CarBrandModel {
    
    private static final Logger _Logger = Logger.getLogger(CarBrandModel.class);
    
    public static final CarBrandModel Instance = new CarBrandModel();
    private final CarBrandDao _dao = new CarBrandDao("mioto");
    private final SimpleCache<Integer, TCarBrand> _cache = new SimpleCache<Integer, TCarBrand>("common");
    
    private CarBrandModel() {}
    
    public CarBrandDao getDao()
    {
        return _dao;
    }
    
    public SimpleCache<Integer, TCarBrand> getCache()
    {
        return _cache;
    }
    
    public TCarBrandResult createCarBrand(TCarBrand carBrand)
    {
        TCarBrandResult result = new TCarBrandResult();
        long ret = _dao.createCarBrand(carBrand);
        if(Err.isFail(ret)) return new TCarBrandResult((int) ret, "");
        
        result.setError(Err.SUCCESS);
        result.setMessage("");
        TCarBrand created = new TCarBrand(carBrand);
        created.setCarBrandId((int) ret);
        result.setValue(created);
        _cache.remove((int)ret);
        return result;
    }
    
    public TCarBrandResult updateCarBrand(TCarBrand carBrand)
    {
        TCarBrandResult result = new TCarBrandResult();
        long ret = _dao.updateCarBrand(carBrand);
        if(Err.isFail(ret)) return new TCarBrandResult((int) ret, "");
        
        result.setError(Err.SUCCESS);
        result.setMessage("");
        result.setValue(new TCarBrand(carBrand));
        _cache.remove(carBrand.getCarBrandId());
        return result;
    }
    
    public long deleteCarBrand(long carBrandId)
    {
        long result = _dao.deleteCarBrand(carBrandId);
        if(result == 0) return Err.NOT_FOUND;
        if(result < 0) return result;
        
        _cache.remove((int)carBrandId);
        return result;
    }
    
    public TListCarBrandResult getAllCarBrand(String nameBrand, int count, int offset)
    {
        TListCarBrandResult result = new TListCarBrandResult();
        ValueResult<List<TCarBrand>> ret = _dao.getAllCarBrand(nameBrand, count, offset);
        if(Err.isFail(ret.error) && !Err.isNotFound(ret.error)) return new TListCarBrandResult((int) ret.error, "Lỗi kết nối mạng");
        
        List<TCarBrand> value = ret.value != null ? ret.value : new ArrayList<TCarBrand>();
        result.setError(Err.SUCCESS);
        result.setMessage("Lấy dữ liệu thành công");
        result.setValue(value);
        return result;
    }
    
    public TCarBrandResult getCarBrandById(long carBrandId)
    {
        TCarBrandResult result = new TCarBrandResult(Err.FAIL, "");
        TCarBrand cached = _cache.get((int)carBrandId);
        if(cached != null)
        {
            result.setError(Err.SUCCESS);
            result.setMessage("Lấy dữ liệu thành công");
            result.setValue(new TCarBrand(cached));
            return result;
        }
        
        ValueResult<TCarBrand> ret = _dao.getCarBrandById(carBrandId);
        if(Err.isNetworkError(ret.error)) return new TCarBrandResult((int) ret.error, "Lỗi kết nối mạng");
        if(Err.isNotFound(ret.error)) return new TCarBrandResult((int) Err.NOT_FOUND, "Không tìm thấy hãng xe");
        if(Err.isSuccess(ret.error)) 
        {
            _cache.put((int)carBrandId, new TCarBrand(ret.value));
            result.setError(Err.SUCCESS);
            result.setMessage("Lấy dữ liệu thành công");
            result.setValue(new TCarBrand(ret.value));
        }
        return result;
    }
}
