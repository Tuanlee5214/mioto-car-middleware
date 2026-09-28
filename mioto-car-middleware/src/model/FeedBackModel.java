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
    
    public long createFeedBack(TFeedBack feedback)
    {
        long result = _dao.createFeedBack(feedback);
        if(Err.isFail(result)) return result;
        _cache.remove((int)result);
        return result;
    }
    
    public long updateFeedBack(TFeedBack feedback)
    {
        long result = _dao.updateFeedBack(feedback);
        if(Err.isFail(result)) return result;
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
        TListFeedBackResult result = new TListFeedBackResult(Err.FAIL, "");
        ValueResult<List<TFeedBack>> ret = _dao.getFeedBackByReceiverId(receiverId, count, offset);
        if(Err.isNetworkError(ret.error)) return new TListFeedBackResult((int) ret.error, "Lỗi kết nối mạng");
        result.setError(Err.SUCCESS);
        result.setMessage("Lấy dữ liệu thành công");
        result.setValue(new ArrayList<TFeedBack>(ret.value));
        return result;
    }
            
}
