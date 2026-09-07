/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import cache.SimpleCache;
import dao.SessionDao;
import error.Err;
import error.ValueResult;
import org.apache.log4j.Logger;
import thrift.TLoginInfo;
import thrift.TSession;
import thrift.TSessionResult;

/**
 *
 * @author tuanlee
 */
public class SessionModel {
    private static final Logger _Logger = Logger.getLogger(SessionModel.class);
    
    public static final SessionModel Instance = new SessionModel();
    private final SessionDao _dao = new SessionDao("mioto");
    private final SimpleCache<Integer, TSession> _cache = new SimpleCache<Integer, TSession>("session");

    private SessionModel() {}
    
    public SessionDao getDao() { return _dao; }
    public SimpleCache<Integer, TSession> getCache() { return _cache; }
    
    public long createSession(TSession session, TLoginInfo loginInfo)
    {
        long id = _dao.createSession(session, loginInfo);
        if(Err.isFail(id)) return id;
        _cache.remove((int) id);
        return id;
    }
    
    public TSessionResult getSession(long sessionId)
    {
        long now = System.currentTimeMillis();
        TSessionResult result = new TSessionResult(Err.FAIL, "");
        TSession cached = _cache.get((int) sessionId);
        
        if(cached != null)
        {
            if(cached.getTimeExpired() < now)
            {
                _cache.remove((int)sessionId);
                return new TSessionResult(Err.NOT_FOUND, "Phiên đăng nhập hết hạn");
            }
            result.setError(Err.SUCCESS);
            result.setMessage("Lấy dữ liệu thành công");
            result.setValue(new TSession(cached));
            return result;
        }   
        
        ValueResult<TSession> ret = _dao.getSession(sessionId);
        if(Err.isNetworkError(ret.error)) 
        {
            return new TSessionResult((int) ret.error, "Lỗi kết nối mạng");
        }
        if(Err.isNotFound(ret.error)) 
        {
            return new TSessionResult((int) ret.error ,"Không tìm thấy phiên đăng nhập");
        }
        
        if (ret.value.getTimeExpired() < now) 
        {
            return new TSessionResult((int) ret.error, "Phiên đăng nhập đã hết hạn");
        }
        
        if(ret.isSuccess() && ret.value != null)
        {
            _cache.put((int)sessionId, new TSession(ret.value));
            result.setError(Err.SUCCESS);
            result.setMessage("Lấy dữ liệu thành công");
            result.setValue(new TSession(ret.value));
        }
        return result;
    }
}
