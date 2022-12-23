package com.itheima.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.Controller;

import java.sql.SQLIntegrityConstraintViolationException;

/*全局异常处理器*/
@ControllerAdvice(annotations = {RestController.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> ExceptionHandler(SQLIntegrityConstraintViolationException exception){
        log.error(exception.getMessage());
        if (exception.getMessage().contains("Duplicate entry")){
            String[] strings = exception.getMessage().split(" ");
            String msg = strings[2]+"已存在";
            return R.error(msg);
        }
        return R.error("未知错误！");
    }
    @ExceptionHandler(CustomException.class)
    public R<String> ExceptionHandler(CustomException exception){
        log.error(exception.getMessage());
        return R.error(exception.getMessage());
    }

}
