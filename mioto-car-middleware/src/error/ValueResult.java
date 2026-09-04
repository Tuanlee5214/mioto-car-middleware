/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package error;

/**
 *
 * @author tuanlee
 */
public class ValueResult<V> {
    public long error;
    public V value;

    public ValueResult(long error) {
        this.error = error;
    }

    public ValueResult(long error, V value) {
        this.error = error;
        this.value = value;
    }

    public boolean isSuccess() {
        return Err.isSuccess(error);
    }

    public boolean isFail() {
        return Err.isFail(error);
    }

    @Override
    public String toString() {
        return "ValueResult{error=" + error + ", value=" + value + '}';
    }
}
