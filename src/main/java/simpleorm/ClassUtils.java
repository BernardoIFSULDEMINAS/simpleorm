/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simpleorm;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 *
 * @author 13828523633
 */
public class ClassUtils {
    public Method findLaxMethod(Class<?> classe, String methodName, Class<?>... params) throws NoSuchMethodException {
        try {
            return classe.getMethod(methodName, params);
        } catch (NoSuchMethodException e) {
            for(Method m : classe.getMethods()) {
                if(m.getName().equals(methodName) && Arrays.equals(m.getParameterTypes(), params)) {
                    return m;
                }
            }
        }
        throw new NoSuchMethodException();
    }
}
