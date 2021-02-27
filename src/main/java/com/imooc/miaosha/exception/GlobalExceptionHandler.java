package com.imooc.miaosha.exception;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.imooc.miaosha.result.CodeMsg;
import com.imooc.miaosha.result.Result;

@ControllerAdvice   //拦截全局异常，该注解使用@Component注解，这样的话当我们使用<context:component-scan>扫描时也能扫描到。
@ResponseBody
public class GlobalExceptionHandler {

	@ExceptionHandler(value=Exception.class)  //所有异常,方法的参数跟controller一样。
	public Result<String> exceptionHandler(HttpServletRequest request, Exception e){
		e.printStackTrace();   //将异常打印出来，也就是输出台
		if(e instanceof GlobalException) {
			GlobalException ex = (GlobalException)e;
			return Result.error(ex.getCm());  //而这个结果是返回前端的response
		}else if(e instanceof BindException) {   //绑定异常
			BindException ex = (BindException)e;
			List<ObjectError> errors = ex.getAllErrors();  //获得所有的参数异常
			ObjectError error = errors.get(0);
			String msg = error.getDefaultMessage();
			return Result.error(CodeMsg.BIND_ERROR.fillArgs(msg));
		}else {
			return Result.error(CodeMsg.SERVER_ERROR);
		}
	}
}
