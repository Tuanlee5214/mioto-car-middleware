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
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import thrift.TFeedBack;

/**
 *
 * @author tuanlee
 */
public class FeedBackDao {
    private static final Logger _Logger = Logger.getLogger(FeedBackDao.class);
    
    private static final String TABLE = "FeedBacks";
    private static final String KEY   = "id";
    private static final String COLS  = "id,comment,pointStar,senderId,receiverId,createdAt,updatedAt";
    
    private final MysqlClient _cli;
    public FeedBackDao(String name)
    {
        _cli = new MysqlClient(name);
    }
    
    public MysqlClient getClient()
    {
        return _cli;
    }
    
    public long createFeedBack(TFeedBack feedback)
    {
        String sql = "INSERT INTO " + TABLE
                + " (comment,pointStar,senderId,receiverId,createdAt,updatedAt)"
                + " VALUES (?,?,?,?,?,?)";
        return _cli.executeInsertAndReturnKey(sql, feedback.getComment(), feedback.getPointStar(), feedback.getSenderId(), feedback.getReceiverId(), 
                feedback.getCreatedAt(), feedback.getUpdatedAt());
    }
    
    public long updateFeedBack(TFeedBack feedback)
    {
        String sql = "UPDATE " + TABLE
                + " SET comment=?,pointStar=?,updatedAt=?"
                + " WHERE " + KEY + "=?";
        return _cli.executeUpdate(sql, feedback.getComment(), feedback.getPointStar(), feedback.getUpdatedAt(), feedback.getFeedbackId());
    }
    
    public long deleteFeedBack(long feedbackId)
    {
        return _cli.executeUpdate("DELETE FROM " + TABLE + " WHERE " + KEY + "=?", feedbackId);
    }
    
    public ValueResult<List<TFeedBack>> getFeedBackByReceiverId(long receiverId, int count, int offset)
    {
        ValueResult<List<TFeedBack>> ret = new ValueResult<List<TFeedBack>>(Err.FAIL);
        String sql = "SELECT " + COLS + " FROM " + TABLE + " WHERE receiverId=? ORDER BY updatedAt DESC LIMIT ? OFFSET ?";
        List<Object> params = new ArrayList<Object>();
        params.add(receiverId);
        params.add(count <= 0 || count > 100 ? 20 : count);
        params.add(Math.max(0, offset));
        ret.error = _cli.executeQuery(new MysqlClient.IRowListener() {
            @Override
            public void onRow(ResultSet rs) throws SQLException {
                if(ret.value == null) ret.value = new ArrayList<TFeedBack>();
                ret.value.add(map(rs));
            }
        }, sql, params.toArray());
        return ret;
    }
    
    private TFeedBack map(ResultSet rs) throws SQLException
    {
        TFeedBack feedback = new TFeedBack();
        feedback.setFeedbackId(rs.getInt("id"));
        feedback.setComment(rs.getString("comment"));
        feedback.setCreatedAt(rs.getLong("createdAt"));
        feedback.setUpdatedAt(rs.getLong("updatedAt"));
        feedback.setPointStar(rs.getInt("pointStar"));
        feedback.setSenderId(rs.getInt("senderId"));
        feedback.setReceiverId(rs.getInt("receiverId"));
        return feedback;
    }
}
