/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package thrift.handler;

import org.apache.thrift.TException;
import thrift.MiotoCarService;
import thrift.OpHandle;
import thrift.TLoginInfo;
import thrift.TLoginResult;
import thrift.TSessionResult;
import thrift.TUser;
import thrift.TUserResult;

/**
 *
 * @author tuanlee
 */
public class CarServiceHandler implements MiotoCarService.Iface {

    @Override
    public TLoginResult signup(OpHandle handle, String phone, String pwd, String displayName, TLoginInfo loginInfo) throws TException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public TLoginResult login(OpHandle handle, String phone, String pwd, TLoginInfo loginInfo) throws TException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public int logout(OpHandle handle, long sessionId) throws TException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public TSessionResult getSession(OpHandle handle, long sessionId) throws TException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public TUserResult getUser(OpHandle handle, int userId) throws TException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public int updateUser(OpHandle handle, TUser user) throws TException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
}
