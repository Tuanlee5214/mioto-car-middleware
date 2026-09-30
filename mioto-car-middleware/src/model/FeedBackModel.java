/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import cache.SimpleCache;
import dao.FeedBackDao;
import error.Err;
import error.ValueResult;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import thrift.TFeedBack;
import thrift.TFeedBackResult;
import thrift.TListFeedBackResult;

/**
 *
 * @author tuanlee
 */
public class FeedBackModel {
    private static final Logger _Logger = Logger.getLogger(FeedBackModel.class);
    
    public static final FeedBackModel Instance = new FeedBackModel();
    private final FeedBackDao _dao = new FeedBackDao("mioto");
    private final SimpleCache<Integer, TFeedBack> _cache = new SimpleCache<Integer, TFeedBack>("common");
    private FeedBackModel(){}
    
    public FeedBackDao getDao()
    {
        return _dao;
    }
    
    public SimpleCache<Integer, TFeedBack> getCache()
    {
        return _cache;
    }
    
    public TFeedBackResult createFeedBack(TFeedBack feedback)
    {
        TFeedBackResult result = new TFeedBackResult();
        long ret = _dao.createFeedBack(feedback);
        if(Err.isFail(ret)) return new TFeedBackResult((int)ret, "");
        
        result.setError(Err.SUCCESS);
        result.setMessage("");
        result.setValue(new TFeedBack(feedback));
        _cache.remove((int)ret);
        return result;
    }
    
    public TFeedBackResult updateFeedBack(TFeedBack feedback)
    {
        TFeedBackResult result = new TFeedBackResult();
        long ret = _dao.updateFeedBack(feedback);
        if(ret == 0) return new TFeedBackResult(Err.NOT_FOUND, "");
        if(Err.isFail(ret)) return new TFeedBackResult((int)ret, "");
        
        result.setError(Err.SUCCESS);
        result.setMessage("");
        result.setValue(new TFeedBack(feedback));
        _cache.remove((int)feedback.getFeedbackId());
        return result;
    }
    
    public long deleteFeedBack(long feedbackId)
    {
        long result = _dao.deleteFeedBack(feedbackId);
        if(result == 0) return Err.NOT_FOUND;
        if(result < 0) return result;
        _cache.remove((int)feedbackId);
        return result;
    }
    
    public TListFeedBackResult getFeedBackByReceiverId(long receiverId, int count, int offset)
    {
        TListFeedBackResult result = new TListFeedBackResult();
        ValueResult<List<TFeedBack>> ret = _dao.getFeedBackByReceiverId(receiverId, count, offset);
        if(Err.isNetworkError(ret.error)) return new TListFeedBackResult((int) ret.error, "Lỗi kết nối mạng");
        List<TFeedBack> value = ret.value != null ? ret.value : new ArrayList<TFeedBack>();
        result.setError(Err.SUCCESS);
        result.setMessage("Lấy dữ liệu thành công");
        result.setValue(value);
        return result;
    }
            
}
