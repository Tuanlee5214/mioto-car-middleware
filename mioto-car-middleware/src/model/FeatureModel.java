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
    
    public long createFeature(TFeature feature)
    {
        long result = _dao.createFeature(feature);
        if(Err.isFail(result)) return result;
        _cache.remove((int)result);
        return result;
    }
    
    public long updateFeature(TFeature feature)
    {
        long result = _dao.updatedFeature(feature);
        if(Err.isFail(result)) return result;
        
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
        TListFeatureResult result = new TListFeatureResult(Err.FAIL, "");
        ValueResult<List<TFeature>> ret = _dao.getAllFeature(nameFeature, count, offset);
        if(Err.isFail(ret.error)) return new TListFeatureResult((int) ret.error, "Lỗi kết nối mạng");
        result.setError(Err.SUCCESS);
        result.setMessage("Lấy dữ liệu thành công");
        result.setValue(new ArrayList<TFeature>(ret.value));
        return result;
    }
}
