/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import cache.SimpleCache;
import dao.UserDao;
import error.Err;
import error.ValueResult;
import org.apache.log4j.Logger;
import thrift.TLoginInfo;
import thrift.TSessionResult;
import thrift.TUpdateUserResult;
import thrift.TUser;
import thrift.TUserResult;

/**
 *
 * @author tuanlee
 */
public class UserModel {

    private static final Logger _Logger = Logger.getLogger(UserModel.class);

    public static final UserModel Instance = new UserModel();
    private final UserDao _dao = new UserDao("mioto");
    private final SimpleCache<Integer, TUser> _cacheId = new SimpleCache<Integer, TUser>("user");

    private UserModel() {
    }

    public UserDao getDao() {
        return _dao;
    }

    public SimpleCache<Integer, TUser> getCache() {
        return _cacheId;
    }

    public boolean isPhoneExisted(String phone) {
        return _dao.isPhoneExisted(phone);
    }

    public long createUser(TUser user) {
        long id = _dao.createUser(user);
        if (Err.isFail(id)) {
            return id;
        }
        _cacheId.remove((int) id);
        return id;
    }

    public TUserResult getUser(long userId) {
        TUserResult result = new TUserResult(Err.FAIL, "");
        TUser cached = _cacheId.get((int) userId);
        if (cached != null) {
            result.setError(Err.SUCCESS);
            result.setMessage("Lấy dữ liệu người dùng thành công");
            result.setValue(new TUser(cached));
            return result;
        }

        ValueResult<TUser> ret = _dao.getUser(userId);
        if (Err.isNetworkError(ret.error)) {
            return new TUserResult((int) ret.error, "Lỗi kết nối mạng");
        }

        if (Err.isNotFound(ret.error)) {
            return new TUserResult((int) ret.error, "Không tìm thấy người dùng");
        }

        if (ret.isSuccess() && ret.value != null) {
            _cacheId.put((int) userId, new TUser(ret.value));
            result.setError(Err.SUCCESS);
            result.setMessage("Lấy dữ liệu người dùng thành công");
            result.setValue(ret.value);
        }
        return result;
    }

    public TUserResult getUserByPhone(String phone) {
        String clearPhone = phone.trim();
        TUserResult result = new TUserResult(Err.FAIL, "");
        
        ValueResult<TUser> ret = _dao.getUserByPhone(clearPhone);
        if (Err.isNetworkError(ret.error)) {
            return new TUserResult((int) ret.error, "Lỗi kết nối mạng");
        }
        if (Err.isNotFound(ret.error)) {
            return new TUserResult((int) ret.error, "Không tìm thấy người dùng");
        }
        if (Err.isSuccess(ret.error)) {
            _cacheId.put((int) ret.value.getUserId(), new TUser(ret.value));
            result.setError(Err.SUCCESS);
            result.setMessage("Lấy thông tin người dùng thành công");
            result.setValue(new TUser(ret.value));
        }
        return result;
    }

    public TUserResult getUserBySessionId(long sessionId, TLoginInfo loginInfo) {
        long now = System.currentTimeMillis();
        TUserResult result = new TUserResult();
        TSessionResult sessionResult = SessionModel.Instance.getSession(sessionId);
        if(Err.isFail(sessionResult.getError()) || sessionResult.getValue() == null)
        {
            if(Err.isNetworkError(sessionResult.getError()))
                return new TUserResult(sessionResult.getError(), "Lỗi kết nối mạng");
            else return new TUserResult(sessionResult.getError(), "Phiên đăng nhập hết hạn");
        }
        long timeExpired = sessionResult.getValue().getTimeExpired();
        if(timeExpired < now) return new TUserResult(Err.NOT_FOUND, "Phiên đăng nhập hết hạn");
        String userAgentFromDB = sessionResult.getValue().getUserAgent() == null ? " " : sessionResult.getValue().getUserAgent();
        String userAgentFromCli = loginInfo.getUserAgent();
        if(!userAgentFromDB.equals(userAgentFromCli))
        {
            return new TUserResult(Err.NOT_FOUND, "Phiên đăng nhập hết hạn");
        }
        
        TUserResult ret = this.getUser(sessionResult.value.getUserId());
        if (Err.isNetworkError(ret.error)) {
            return new TUserResult((int) ret.error, "Lỗi kết nối mạng");
        }
        if (Err.isNotFound(ret.error)) {
            return new TUserResult((int) ret.error, "Không tìm thấy người dùng");
        }
        if (Err.isSuccess(ret.error)) {
            _cacheId.put((int) ret.value.getUserId(), new TUser(ret.value));
            result.setError(Err.SUCCESS);
            result.setMessage("Lấy thông tin người dùng thành công");
            result.setValue(new TUser(ret.value));
        }
        return result;
    }

    public TUpdateUserResult updateUser(TUser user) {
        ValueResult<Integer> ret = _dao.updateUser(user);
        TUpdateUserResult result = new TUpdateUserResult(Err.FAIL, "");
        if (Err.isNetworkError(ret.error)) {
            return new TUpdateUserResult((int) ret.error, "Lỗi kết nối mạng");
        }
        
        result.setError((int) ret.error);
        result.setMessage("Cập nhật thành công");
        result.setValue(new TUser(user));
        _cacheId.remove(user.getUserId());
        return result;
    }

}

