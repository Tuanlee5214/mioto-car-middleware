/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import db.MysqlClient;
import error.Err;
import error.ValueResult;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.log4j.Logger;
import thrift.TLoginInfo;
import thrift.TSession;

/**
 *
 * @author tuanlee
 */
public class SessionDao {
    
    private static final Logger _Logger = Logger.getLogger(SessionDao.class);
    
    private static final String TABLE = "Sessions";
    private static final String KEY   = "sessionId";
    private static final String COLS  = "sessionId,userId,userAgent,userIP,timeCreated,timeExpired";
    
    private final MysqlClient _cli;
    
    public SessionDao(String name)
    {
        _cli = new MysqlClient(name);
    }
    
    public MysqlClient getClient()
    {
        return _cli;
    }
    
    public long createSession(TSession session, TLoginInfo loginInfo)
    {
        String sql = "INSERT INTO " + TABLE
                + " (sessionId,userId,userAgent,userIP,timeCreated,timeExpired)"
                + " VALUES (?,?,?,?,?,?)";
        return _cli.executeInsertAndReturnKey(sql, session.getSessionId(), session.getUserId(), loginInfo.getUserAgent(), loginInfo.getUserIP(), session.getTimeCreated(), session.getTimeExpired());
    }
    
    public ValueResult<TSession> getSession(long sessionId)
    {
        ValueResult<TSession> ret = new ValueResult<TSession>(Err.FAIL);
        String sql = "SELECT " + COLS + " FROM " + TABLE + " WHERE " + KEY + "=?";
        ret.error = _cli.executeQuery(new MysqlClient.IRowListener() {
            @Override
            public void onRow(ResultSet rs) throws SQLException {
                ret.value = map(rs);
            }
        }, sql, sessionId);
        return ret;
    }
    
    public ValueResult<Integer> deleteSession(long sessionId)
    {
        ValueResult<Integer> result = new ValueResult<Integer>(Err.FAIL, "");
        result.value = _cli.executeUpdate("DELETE FROM " + TABLE + " WHERE " + KEY + "=?", sessionId);
        return result;
    }
    
    private TSession map(ResultSet rs) throws SQLException
    {
        TSession session = new TSession();
        int i = 0;
        session.setSessionId(rs.getLong(++i));
        session.setUserId(rs.getInt(++i));
        session.setUserAgent(rs.getString(++i));
        session.setUserIP(rs.getString(++i));
        session.setTimeCreated(rs.getLong(++i));
        session.setTimeExpired(rs.getLong(++i));
        return session;
    }
}
