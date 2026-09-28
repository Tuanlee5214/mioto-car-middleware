/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import cache.SimpleCache;
import dao.DistrictDao;
import error.Err;
import error.ValueResult;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import thrift.TDistrict;
import thrift.TDistrictResult;
import thrift.TListDistrictResult;

/**
 *
 * @author tuanlee
 */
public class DistrictModel {
    private static final Logger _Logger = Logger.getLogger(DistrictModel.class);
    
    public static final DistrictModel Instance = new DistrictModel();
    private final DistrictDao _dao = new DistrictDao("mioto");
    private final SimpleCache<Integer, TDistrict> _cache = new SimpleCache<Integer, TDistrict>("common");
    
    private DistrictModel() {}
    
    public DistrictDao getDao()
    {
        return _dao;
    }
    
    public SimpleCache<Integer, TDistrict> getCache()
    {
        return _cache;
    }
    
    public long createDistrict(TDistrict district)
    {
        long result = _dao.createDistrict(district);
        if(Err.isFail(result)) return result;
        _cache.remove((int)result);
        return result;
    }
    
    public long updateDistrict(TDistrict district)
    {
        long result = _dao.updateDistrict(district);
        if(Err.isFail(result)) return result;
        _cache.remove((int)district.getDistrictId());
        return result;
    }
    
    public long deleteDistrict(long districtId)
    {
        long result = _dao.deleteDistrict(districtId);
        if(result == 0) return Err.NOT_FOUND;
        if(result < 0) return result;
        _cache.remove((int)districtId);
        return result;
    }
    
    public TListDistrictResult getDistrict(long provinceId, int count, int offset)
    {
        TListDistrictResult result = new TListDistrictResult(Err.FAIL, "");
        ValueResult<List<TDistrict>> ret = _dao.getDistrict(provinceId, count, offset);
        if(Err.isFail(ret.error)) return new TListDistrictResult((int) ret.error, "");
        result.setError(Err.SUCCESS);
        result.setMessage("Lấy dữ liệu thành công");
        result.setValue(new ArrayList<TDistrict>(ret.value));
        return result;
    }
}
