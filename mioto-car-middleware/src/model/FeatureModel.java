/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import cache.SimpleCache;
import dao.FeatureDao;
import error.Err;
import error.ValueResult;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import thrift.TFeature;
import thrift.TFeatureResult;
import thrift.TListFeatureResult;

/**
 *
 * @author tuanlee
 */
public class FeatureModel {
        private static final Logger _Logger = Logger.getLogger(FeatureModel.class);
    
    public static final FeatureModel Instance = new FeatureModel();
    private final FeatureDao _dao = new FeatureDao("mioto");
    private final SimpleCache<Integer, TFeature> _cache = new SimpleCache<Integer, TFeature>("common");
    
    private FeatureModel() {}
    
    public FeatureDao getDao()
    {
        return _dao;
    }
    
    public SimpleCache<Integer, TFeature> getCache()
    {
        return _cache;
    }
    
    public TFeatureResult createFeature(TFeature feature)
    {
        TFeatureResult result = new TFeatureResult();
        long ret = _dao.createFeature(feature);
        if(Err.isFail(ret)) return new TFeatureResult((int)ret, "");
        
        result.setError(Err.SUCCESS);
        result.setMessage("");
        result.setValue(new TFeature(feature));
        _cache.remove((int)ret);
        return result;
    }
    
    public TFeatureResult updateFeature(TFeature feature)
    {
        TFeatureResult result = new TFeatureResult();
        long ret = _dao.updatedFeature(feature);
        if(ret == 0) return new TFeatureResult(Err.NOT_FOUND, "");
        if(Err.isFail(ret)) return new TFeatureResult((int)ret, "");
        TFeatureResult returnValue = this.getFeatureById(feature.getFeatureId());
        if(Err.isFail(returnValue.getError())) return returnValue;
        
        result.setError(Err.SUCCESS);
        result.setMessage("");
        result.setValue(returnValue.getValue());
        
        _cache.remove((int)feature.getFeatureId());
        return result;
    }
    
    public long deleteFeature(long featureId)
    {
        long result = _dao.deleteFeature(featureId);
        if(result == 0) return Err.NOT_FOUND;
        if(result < 0) return result;
        _cache.remove(Integer.SIZE);
        return result;
    }
    
    public TFeatureResult getFeatureById(long featureId)
    {
        TFeatureResult result = new TFeatureResult();
        TFeature cached = _cache.get((int)featureId);
        if(cached != null) 
        {
            result.setError(Err.SUCCESS);
            result.setMessage("Lấy dữ liệu thành công");
            result.setValue(new TFeature(cached));
            return result;
        }
        
        ValueResult<TFeature> ret = _dao.getFeatureById(featureId);
        if(Err.isNetworkError(ret.error)) return new TFeatureResult((int) ret.error, "");
        if(Err.isNotFound(ret.error)) return new TFeatureResult(Err.NOT_FOUND, "");
        if(Err.isSuccess(ret.error))
        {
            _cache.put((int)featureId, new TFeature(ret.value));
            result.setError(Err.SUCCESS);
            result.setMessage("Lấy dữ liệu thành công");
            result.setValue(new TFeature(ret.value));
        }
        return result;
    }
    
    public TListFeatureResult getAllFeature(String nameFeature, int count, int offset)
    {
        TListFeatureResult result = new TListFeatureResult();
        ValueResult<List<TFeature>> ret = _dao.getAllFeature(nameFeature, count, offset);
        if(Err.isFail(ret.error) && !Err.isNotFound(ret.error)) return new TListFeatureResult((int) ret.error, "Lỗi kết nối mạng");
        List<TFeature> value = ret.value != null ? ret.value : new ArrayList<TFeature>();
        result.setError(Err.SUCCESS);
        result.setMessage("Lấy dữ liệu thành công");
        result.setValue(value);
        return result;
    }
}
