/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import cache.SimpleCache;
import dao.RoleDao;
import error.Err;
import error.ValueResult;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import thrift.TListRoleResult;
import thrift.TRole;
import thrift.TRoleResult;

/**
 *
 * @author tuanlee
 */
public class RoleModel {
    private static final Logger _Logger = Logger.getLogger(RoleModel.class);
    
    public static final RoleModel Instance = new RoleModel();
    private final RoleDao _dao = new RoleDao("mioto");
    private final SimpleCache<Integer, TRole> _cache = new SimpleCache<Integer, TRole>("common");
    
    private RoleModel() {}
        
    public TRoleResult createRole(TRole role) { 
        TListRoleResult same = getAllRole(role.getName(), 1, 0);
        if (Err.isFail(same.getError())) {
            return new TRoleResult(same.getError(), same.getMessage());
        }
        if (!same.getValue().isEmpty()) {
            return new TRoleResult(Err.CONFLICT, "Tên quyền đã tồn tại");
        }

        long ret = _dao.createRole(role);
        if (Err.isFail(ret)) {
            return new TRoleResult((int) ret, "");
        }

        TRoleResult result = new TRoleResult(Err.SUCCESS, "");
        TRole created = new TRole(role);
        created.setRoleId((int) ret);
        result.setValue(created);
        _cache.remove((int) ret);
        return result;
    }

    public TRoleResult updateRole(TRole role) {
        TListRoleResult same = getAllRole(role.getName(), 1, 0);
        if (Err.isFail(same.getError())) {
            return new TRoleResult(same.getError(), same.getMessage());
        }
        if (!same.getValue().isEmpty() && same.getValue().get(0).getRoleId() != role.getRoleId()) {
            return new TRoleResult(Err.CONFLICT, "Tên quyền đã tồn tại");
        }

        long ret = _dao.updateRole(role);
        if (ret == 0) {
            return new TRoleResult(Err.NOT_FOUND, "Không tìm thấy quyền");
        }
        if (ret < 0) {
            return new TRoleResult((int) ret, "");
        }
        _cache.remove(role.getRoleId());
        return getRoleById(role.getRoleId());
    }

    public long deleteRole(long roleId) {
        long result = _dao.deleteRole(roleId);
        if (result == 0) {
            return Err.NOT_FOUND;
        }
        if (result < 0) {
            return result;
        }
        _cache.remove((int) roleId);
        return result;
    }

    public TListRoleResult getAllRole(String name, int count, int offset) {
        ValueResult<List<TRole>> ret = _dao.getAllRole(name, count, offset);
        if (Err.isFail(ret.error) && !Err.isNotFound(ret.error)) {
            return new TListRoleResult((int) ret.error, "Lỗi kết nối mạng");
        }

        TListRoleResult result = new TListRoleResult(Err.SUCCESS, "Lấy dữ liệu thành công");
        result.setValue(ret.value != null ? ret.value : new ArrayList<TRole>());
        return result;
    }

    public TRoleResult getRoleById(long roleId) {
        TRole cached = _cache.get((int) roleId);
        if (cached != null) {
            TRoleResult result = new TRoleResult(Err.SUCCESS, "Lấy dữ liệu thành công");
            result.setValue(new TRole(cached));
            return result;
        }
        ValueResult<TRole> ret = _dao.getRoleById(roleId);
        if (Err.isNetworkError(ret.error)) {
            return new TRoleResult((int) ret.error, "Lỗi kết nối mạng");
        }
        if (Err.isNotFound(ret.error)) {
            return new TRoleResult(Err.NOT_FOUND, "Không tìm thấy quyền");
        }
        if (Err.isFail(ret.error)) {
            return new TRoleResult((int) ret.error, "");
        }
        _cache.put((int) roleId, new TRole(ret.value));
        TRoleResult result = new TRoleResult(Err.SUCCESS, "Lấy dữ liệu thành công");
        result.setValue(new TRole(ret.value));
        return result;
    }

    public TListRoleResult getRolesByIds(List<Integer> roleIds) {
        TListRoleResult result = new TListRoleResult(Err.SUCCESS, "Lấy dữ liệu thành công");
        if (roleIds == null || roleIds.isEmpty()) {
            result.setValue(new ArrayList<TRole>());
            return result;
        }
        ValueResult<List<TRole>> ret = _dao.getRolesByIds(roleIds);
        if (Err.isFail(ret.error) && !Err.isNotFound(ret.error)) {
            return new TListRoleResult((int) ret.error, "Lỗi kết nối mạng");
        }
        result.setValue(ret.value != null ? ret.value : new ArrayList<TRole>());
        return result;
    }
}
