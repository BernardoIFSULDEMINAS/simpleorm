package simpleorm;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
@Retention(RetentionPolicy.RUNTIME)
public @interface SQLField {
	String value() default "";
	String prefix() default "";
	boolean isId() default false;
}
