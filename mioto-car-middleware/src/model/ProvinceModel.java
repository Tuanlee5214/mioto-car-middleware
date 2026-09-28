/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import cache.SimpleCache;
import dao.ProvinceDao;
import error.Err;
import error.ValueResult;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import thrift.TListProvinceResult;
import thrift.TProvince;
import thrift.TProvinceResult;

/**
 *
 * @author tuanlee
 */
public class ProvinceModel {
    private static final Logger _Logger = Logger.getLogger(ProvinceModel.class);
    
    public static final ProvinceModel Instance = new ProvinceModel();
    private final ProvinceDao _dao = new ProvinceDao("mioto");
    private final SimpleCache<Integer, TProvince> _cache = new SimpleCache<Integer, TProvince>("common");
    private ProvinceModel() {}
    
    public ProvinceDao getDao()
    {
        return _dao;
    }
    
    public SimpleCache<Integer, TProvince> getCache()
    {
        return _cache;
    }
    
    public long createProvince(TProvince province)
    {
        long result = _dao.createProvince(province);
        if(Err.isFail(result)) return result;
        
        _cache.remove((int) result);
        return result;
    }
    
    public long updateProvince(TProvince province)
    {
        long result = _dao.updateProvince(province);
        if(Err.isFail(result)) return result;
        
        _cache.remove((int) province.getProvinceId());
        return result;
    }
    
    public long deleteProvince(long provinceId)
    {
        long result = _dao.deleteProvince(provinceId);
        if(result == 0) return Err.NOT_FOUND;
        if(result < 0) return result;
        _cache.remove((int) provinceId);
        return result;  
    }
    
    public TProvinceResult getProvinceById(long provinceId)
    {
        TProvinceResult result = new TProvinceResult(Err.FAIL, "");
        TProvince cached = _cache.get((int) provinceId);
        if(cached != null)
        {
            result.setError(Err.SUCCESS);
            result.setMessage("Lấy dữ liệu thành công");
            result.setValue(new TProvince(cached));
            return result;
        }
        
        ValueResult<TProvince> ret = _dao.getProvinceById(provinceId);
        if(Err.isNetworkError(ret.error)) return new TProvinceResult((int) ret.error, "Lỗi kết nối mạng");
        if(Err.isNotFound(ret.error)) return new TProvinceResult((int) ret.error, "Không tìm thấy dữ liệu");
        if(Err.isSuccess(ret.error))
        {
            _cache.put((int)provinceId, new TProvince(ret.value));
            result.setError(Err.SUCCESS);
            result.setMessage("Lấy dữ liệu thành công");
            result.setValue(new TProvince(ret.value));
        }
        return result;
    }
    
    public TListProvinceResult getAllProvince(String provinceName, int count, int offset)
    {
        TListProvinceResult result = new TListProvinceResult(Err.FAIL, "");
        ValueResult<List<TProvince>> ret = _dao.getAllProvince(provinceName, count, offset);
        if(Err.isFail(ret.error)) return new TListProvinceResult(Err.FAIL, "Lỗi kết nối mạng");
        result.setError(Err.SUCCESS);
        result.setMessage("Lấy dữ liệu thành công");
        result.setValue(new ArrayList<TProvince>(ret.value));
        return result;
    }
}
