/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import cache.SimpleCache;
import dao.FeePolicyDao;
import error.Err;
import error.ValueResult;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import thrift.TFeePolicy;
import thrift.TFeePolicyResult;
import thrift.TListFeePolicyResult;

/**
 *
 * @author tuanlee
 */
public class FeePolicyModel {
    private static final Logger _Logger = Logger.getLogger(FeePolicyModel.class);
    
    public static final FeePolicyModel Instance = new FeePolicyModel();
    private final FeePolicyDao _dao = new FeePolicyDao("mioto");
    private final SimpleCache<Integer, TFeePolicy> _cache = new SimpleCache<Integer, TFeePolicy>("common");
    private FeePolicyModel() {}
    
    public FeePolicyDao getDao()
    {
        return _dao;
    }
    
    public SimpleCache<Integer, TFeePolicy> getCache()
    {
        return _cache;
    }
    
    public long createFeePolicy(TFeePolicy feePolicy)
    {
        long result = _dao.createFeePolicy(feePolicy);
        if(Err.isFail(result)) return result;
        _cache.remove((int)result);
        return result;
    }
    
    public long updateFeePolicy(TFeePolicy feePolicy)
    {
        long result = _dao.updateFeePolicy(feePolicy);
        if(Err.isFail(result)) return result;
        
        _cache.remove((int) feePolicy.getFeePolicyId());
        return result;
    }
    
    public long deleteFeePolicy(long feePolicyId)
    {
        long result = _dao.deleteFeePolicy(feePolicyId);
        if(result == 0) return Err.NOT_FOUND;
        if(result < 0) return result;
        _cache.remove((int)feePolicyId);
        return result;
    }
    
    public TFeePolicyResult getFeePolicyById(long feePolicyId)
    {
        TFeePolicyResult result = new TFeePolicyResult(Err.FAIL, "");
        TFeePolicy cached = _cache.get((int)feePolicyId);
        if(cached != null)
        {
            result.setError(Err.SUCCESS);
            result.setMessage("Lấy dữ liệu thành công");
            result.setValue(new TFeePolicy(cached));
            return result;
        }
        
        ValueResult<TFeePolicy> ret = _dao.getFeePolicyById(feePolicyId);
        if(Err.isNetworkError(ret.error)) return new TFeePolicyResult((int) ret.error, "Lỗi kết nối mạng");
        if(Err.isNotFound(ret.error)) return new TFeePolicyResult(Err.NOT_FOUND, "Không tìm thấy dữ liệu");
        if(Err.isSuccess(ret.error))
        {
            _cache.put((int)feePolicyId, new TFeePolicy(ret.value));
            result.setError(Err.SUCCESS);
            result.setMessage("Lấy dữ liệu thành công");
            result.setValue(new TFeePolicy(ret.value));
        }
        return result;
    }
    
    public TListFeePolicyResult getAllFeePolicy(String name, int offset, int count)
    {
        TListFeePolicyResult result = new TListFeePolicyResult(Err.FAIL, "");
        ValueResult<List<TFeePolicy>> ret = _dao.getAllFeePolicy(name, offset, count);
        if(Err.isFail(ret.error)) return new TListFeePolicyResult((int) ret.error, "Lỗi kết nối mạng");
        result.setError(Err.SUCCESS);
        result.setMessage("Lấy dữ liệu thành công");
        result.setValue(new ArrayList<TFeePolicy>(ret.value));
        return result;
    }
}
