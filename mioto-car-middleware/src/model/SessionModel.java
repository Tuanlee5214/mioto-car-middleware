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

/**
 *
 * @author tuanlee
 */
public class SessionModel {
    private static final Logger _Logger = Logger.getLogger(SessionModel.class);
    
    public static final SessionModel Instance = new SessionModel();
    private SessionModel() {}
    private final SessionDao _dao = new SessionDao("mioto");
    private final SimpleCache<Integer, TSession> _cache = new SimpleCache<Integer, TSession>("session");
    
    public SessionDao getDao() { return _dao; }
    public SimpleCache<Integer, TSession> getCache() { return _cache; }
    
    public long createSession(TSession session, TLoginInfo loginInfo)
    {
        long id = _dao.createSession(session, loginInfo);
        if(Err.isFail(id)) return id;
        _cache.remove((int) id);
        return id;
    }
    
    public ValueResult<TSession> getSession(long sessionId)
    {
        TSession cached = _cache.get((int) sessionId);
        if(cached != null)
        {
            return new ValueResult<TSession>(Err.SUCCESS, new TSession(cached));
        }
        
        ValueResult<TSession> ret = _dao.getSession(sessionId);
        if(ret.isSuccess() && ret.value != null)
        {
            _cache.put((int)sessionId, new TSession(ret.value));
        }
        return ret;
    }
}
