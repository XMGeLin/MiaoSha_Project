package com.imooc.miaosha.validator;

import com.imooc.miaosha.util.ValidatorUtil;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.annotation.Annotation;

public class IsMobileValitor implements ConstraintValidator<IsMobile,String> {

    private boolean required =false;

    @Override
    public void initialize(IsMobile isMobile) {
        required=isMobile.required();   //required()相当于是类里面的属性这样理解。
    }

    @Override          //这里翻译是约束强制上下文。
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if(required){
           return ValidatorUtil.isMobile(value);
        }else {
            if(StringUtils.isEmpty(value)){
                return true;
            }else{
                return ValidatorUtil.isMobile(value);
            }
        }
    }
}
