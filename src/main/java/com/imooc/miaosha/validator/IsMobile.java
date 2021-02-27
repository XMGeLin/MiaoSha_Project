package com.imooc.miaosha.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {IsMobileValitor.class}) //调用IsMobileValitor校验器校验。
//是一个注解
public @interface IsMobile {   //只有这一个注解还不行。

    boolean required() default true;    //默认必须要有

    String message() default "手机号码格式有误。";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
