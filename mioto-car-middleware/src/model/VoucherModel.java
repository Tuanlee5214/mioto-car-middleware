/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import cache.SimpleCache;
import dao.VoucherDao;
import error.Err;
import error.ValueResult;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import thrift.TListVoucherResult;
import thrift.TVoucher;
import thrift.TVoucherResult;

/**
 *
 * @author tuanlee
 */
public class VoucherModel {
    private static final Logger _Logger = Logger.getLogger(VoucherModel.class);
    public static final VoucherModel Instance = new VoucherModel();
    private final VoucherDao _dao = new VoucherDao("mioto");
    private final SimpleCache<Integer, TVoucher> _cache = new SimpleCache<Integer, TVoucher>("common");
    private VoucherModel() {}
    
    private VoucherDao getDao()
    {
        return _dao;
    }
    
    private SimpleCache<Integer, TVoucher> getCache()
    {
        return _cache;
    }
    
    public long createVoucher(TVoucher voucher)
    {
        long result = _dao.createVoucher(voucher);
        if(Err.isFail(result)) return result;
        
        _cache.remove((int) result);
        return result;
    }
    
    public long updateVoucher(TVoucher voucher)
    {
        long result = _dao.updatedVoucher(voucher);
        if(Err.isFail(result)) return result;
        _cache.remove((int)voucher.getVoucherId());
        return result;
    }
    
    public long deleteVoucher(long voucherId)
    {
        long result = _dao.deleteVoucher(voucherId);
        if(result == 0) return Err.NOT_FOUND;
        if(result < 0) return result;
        _cache.remove((int)voucherId);
        return result;
    }
    
    public TVoucherResult getVoucherById(long voucherId)
    {
        TVoucherResult result = new TVoucherResult(Err.FAIL, "");
        TVoucher cached = _cache.get((int)voucherId);
        if(cached != null)
        {
            result.setError(Err.SUCCESS);
            result.setMessage("Lấy dữ liệu thành công");
            result.setValue(new TVoucher(cached));
            return result;
        }
        
        ValueResult<TVoucher> ret = _dao.getVoucherById(voucherId);
        if(Err.isNetworkError(ret.error)) return new TVoucherResult((int) ret.error, "Lỗi kết nối mạng");
        if(Err.isNotFound(ret.error)) return new TVoucherResult((int) ret.error, "Không tìm thấy dữ liệu");
        if(Err.isSuccess(ret.error))
        {
            _cache.put((int)voucherId, new TVoucher(ret.value));
            result.setError(Err.SUCCESS);
            result.setMessage("Lấy dữ liệu thành công");
            result.setValue(new TVoucher(ret.value));
        }
        return result;
    }
    
    public TListVoucherResult getAllVoucher(String title, int count, int offset)
    {
        TListVoucherResult result = new TListVoucherResult(Err.FAIL, "");
        ValueResult<List<TVoucher>> ret = _dao.getAllVoucher(title, count, offset);
        if(Err.isFail(ret.error)) return new TListVoucherResult((int) ret.error, "");
        result.setError(Err.SUCCESS);
        result.setMessage("Lấy dữ liệu thành công");
        result.setValue(new ArrayList<TVoucher>(ret.value));
        return result;
    }
}
