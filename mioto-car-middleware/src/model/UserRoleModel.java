/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import cache.SimpleCache;
import dao.UserRoleDao;
import error.Err;
import error.ValueResult;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;
import thrift.TListRoleResult;
import thrift.TRole;
import thrift.TUserRole;
import thrift.TUserRoleResult;

/**
 *
 * @author tuanlee
 */
public class UserRoleModel {
    private static final Logger _Logger = Logger.getLogger(UserRoleModel.class);
    
    public static final UserRoleModel Instance = new UserRoleModel();
    private final UserRoleDao _dao = new UserRoleDao("mioto");
    private final SimpleCache<Integer, TUserRole> _cache = new SimpleCache<Integer, TUserRole>("common");
    
    private UserRoleModel() {}
        
    public TUserRoleResult getUserRoleByUserId(long userId) {
        TUserRoleResult result = new TUserRoleResult(Err.SUCCESS, "Lấy dữ liệu thành công");
        TUserRole cached = _cache.get((int)userId);
        if(cached != null)
        {
            result.setValue(new TUserRole(cached));
            return result;
        }
        
        ValueResult<TUserRole> ret = _dao.getUserRoleByUserId(userId);
        if (Err.isFail(ret.error) && !Err.isNotFound(ret.error)) {
            return new TUserRoleResult((int) ret.error, "Lỗi kết nối mạng");
        }

        TUserRole userRole = ret.value;
        if (userRole == null) {
            userRole = new TUserRole();
            userRole.setUserId((int) userId);
            userRole.setIsSuperAdmin(false);
            userRole.setRoleIds(new ArrayList<Integer>());
        }

        TListRoleResult roles = RoleModel.Instance.getRolesByIds(userRole.getRoleIds());
        if (Err.isFail(roles.getError())) {
            return new TUserRoleResult(roles.getError(), roles.getMessage());
        }
        List<Integer> existed = new ArrayList<Integer>();
        for (TRole r : roles.getValue()) {
            existed.add(r.getRoleId());
        }
        userRole.setRoleIds(existed);
        userRole.setRoles(roles.getValue());
        result.setValue(userRole);
        _cache.put((int)userId, new TUserRole(userRole));
        return result;
    }

    public TUserRoleResult createUserRole(int userId, List<Integer> roleIds) {
        TListRoleResult roles = RoleModel.Instance.getRolesByIds(roleIds);
        if (Err.isFail(roles.getError())) {
            return new TUserRoleResult(roles.getError(), roles.getMessage());
        }
        if (roles.getValue().size() != new HashSet<Integer>(roleIds).size()) {
            return new TUserRoleResult(Err.BAD_REQUEST, "Có quyền không tồn tại");
        }

        TUserRoleResult current = getUserRoleByUserId(userId);
        if (Err.isFail(current.getError())) {
            return current;
        }

        Set<Integer> merged = new LinkedHashSet<Integer>(current.getValue().getRoleIds());
        merged.addAll(roleIds);
        long ret = _dao.saveUserRole(userId, merged);
        if (Err.isFail(ret)) {
            return new TUserRoleResult((int) ret, "");
        }
        _cache.remove(userId);
        return getUserRoleByUserId(userId);
    }

    public TUserRoleResult deleteUserRole(int userId, List<Integer> roleIds) {
        TUserRoleResult current = getUserRoleByUserId(userId);
        if (Err.isFail(current.getError())) {
            return current;
        }

        List<Integer> remain = new ArrayList<Integer>(current.getValue().getRoleIds());
        if (!remain.removeAll(roleIds)) {
            return new TUserRoleResult(Err.NOT_FOUND, "Người dùng không có quyền này");
        }

        long ret = _dao.saveUserRole(userId, remain);
        if (Err.isFail(ret)) {
            return new TUserRoleResult((int) ret, "");
        }
        _cache.remove(userId);
        return getUserRoleByUserId(userId);
    }
}
