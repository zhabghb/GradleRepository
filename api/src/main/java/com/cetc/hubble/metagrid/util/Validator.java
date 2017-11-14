package com.cetc.hubble.metagrid.util;

import com.cetc.hubble.metagrid.exception.AppException;
import com.cetc.hubble.metagrid.exception.ErrorCode;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.BindingResult;

/**
 * Created by jinyi on 17-5-25.
 */
@Aspect
@Configuration
public class Validator {

    @Pointcut("execution(* com.cetc.hubble.metagrid.controller.DataStandardController.create(..)) " +
            "|| execution(* com.cetc.hubble.metagrid.controller.DataStandardController.update(..))" +
            "|| execution(* com.cetc.hubble.metagrid.controller.StructuredDataViewController.saveDataSetAttr(..))" +
            "|| execution(* com.cetc.hubble.metagrid.controller.StructuredDataViewController.updateDataSetAttr(..))")
    public void Validate(){}

    @Around(value = "Validate()")
    public void doAround(ProceedingJoinPoint pjp) throws Throwable {

        Object[] args = pjp.getArgs();
        for(int i=0; i<args.length; i++) {
            if(args[i] instanceof BindingResult) {
                this.handleBindingResult((BindingResult)(args[i]));
            }
        }

        pjp.proceed();
    }

    /**
     * Handle field validation by @Valid.
     *
     * @return
     */
    private void handleBindingResult(BindingResult result) {
        if (result.hasFieldErrors()) {
            throw new AppException(result.getFieldError().getDefaultMessage(), ErrorCode.BAD_REQUEST);
        }
    }

}
