package org.fuin.cqrs4j.example.shared;

import org.fuin.esc.api.SerializedDataType;

import java.lang.annotation.*;

/**
 * A class that has a public static constant of type {@link SerializedDataType}.
 * The expected default name of the constant is <b>SER_TYPE</b>.
 */
@Documented
@Target(ElementType.TYPE)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface SerializedDataTypeConstant {

    /**
     * Returns the name of a public static constant in the annotated class.
     *
     * @return Name of the public static constant.
     */
    String value() default "SER_TYPE";

}
