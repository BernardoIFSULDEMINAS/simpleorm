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
                if(m.getName().equals(methodName) && m.getParameterCount() == params.length) {
                    Class[] theirParams = m.getParameterTypes();
                    boolean shouldReturnThis = true;
                    for(int i = 0; i < params.length; i++) {
                        if(!theirParams[i].isAssignableFrom(params[i])) {
                            shouldReturnThis = false;
                        }
                        if(shouldReturnThis) {
                            return m;
                        }
                    }
                }
            }
        }
        throw new NoSuchMethodException();
    }
}
