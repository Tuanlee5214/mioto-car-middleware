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
        
    public TDistrictResult createDistrict(TDistrict district)
    {
        TDistrictResult result = new TDistrictResult();
        long ret = _dao.createDistrict(district);
        if(Err.isFail(ret)) return new TDistrictResult((int)ret, "");
        
        TDistrict created = new TDistrict(district);
        created.setDistrictId((int) ret);
        result.setError(Err.SUCCESS);
        result.setMessage("");
        result.setValue(created);
        _cache.remove((int)ret);
        return result;
    }
    
    public TDistrictResult updateDistrict(TDistrict district)
    {
        TDistrictResult result = new TDistrictResult();
        long ret = _dao.updateDistrict(district);
        if(ret == 0) return new TDistrictResult(Err.NOT_FOUND, "");
        if(Err.isFail(ret)) return new TDistrictResult((int)ret, "");
        TDistrictResult returnValue = this.getDistrictById(district.getDistrictId());
        if(Err.isFail(returnValue.getError())) return returnValue;
        
        
        result.setError(Err.SUCCESS);
        result.setMessage("");
        result.setValue(returnValue.getValue());
        _cache.remove((int)district.getDistrictId());
        return result;
    }
    
    public TDistrictResult getDistrictById(long districtId)
    {
        TDistrictResult result = new TDistrictResult(Err.FAIL, "");
        TDistrict cached = _cache.get((int)districtId);
        if(cached != null)
        {
            result.setError(Err.SUCCESS);
            result.setMessage("Lấy dữ liệu thành công");
            result.setValue(new TDistrict(cached));
            return result;
        }
        
        ValueResult<TDistrict> ret = _dao.getDistrictById(districtId);
        if(Err.isNetworkError(ret.error)) return new TDistrictResult((int) ret.error, "Lỗi kết nối mạng");
        if(Err.isNotFound(ret.error)) return new TDistrictResult((int) Err.NOT_FOUND, "Không tìm thấy quận huyện");
        if(Err.isSuccess(ret.error)) 
        {
            _cache.put((int)districtId, new TDistrict(ret.value));
            result.setError(Err.SUCCESS);
            result.setMessage("Lấy dữ liệu thành công");
            result.setValue(new TDistrict(ret.value));
        }
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
        TListDistrictResult result = new TListDistrictResult();
        ValueResult<List<TDistrict>> ret = _dao.getDistrict(provinceId, count, offset);
        if(Err.isFail(ret.error) && !Err.isNotFound(ret.error)) return new TListDistrictResult((int) ret.error, "");
        List<TDistrict> value = ret.value != null ? ret.value : new ArrayList<TDistrict>();
        result.setError(Err.SUCCESS);
        result.setMessage("Lấy dữ liệu thành công");
        result.setValue(value);
        return result;
    }
}
