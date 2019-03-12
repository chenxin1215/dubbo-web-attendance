package com.cx.login.aop;

import java.util.List;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

/**
 * 
 * <p>
 * Title:PageInterceptor
 * </p>
 * <p>
 * Description: 分页切面
 * </p>
 * 
 * @author cx
 * @date 2018年11月25日
 *
 */

@Aspect
@Component
public class PageInterceptor {

    /**
     * 
     * @Title: process
     * @Description: (把所有controller后面带有Paging的方法加上分页功能) 规定：方法后面两个必须是 当前页和每页数量
     * @realization: (AOP编程)
     * @author: cx
     * @param point
     * @return
     * @throws Throwable
     */
    @Around("execution(* com.cx.login.controller..*.*Paging(..))")
    public Object process(ProceedingJoinPoint point) throws Throwable {
        // System.out.println("分页aop执行。。");
        Object[] args = point.getArgs();
        if (args.length < 2) {
            // 规定原方法的参数最后两个是当前页和每页条数
            throw new Exception("参数不够分页");
        }
        PageHelper.startPage((Integer)args[args.length - 2], (Integer)args[args.length - 1]);
        List list = (List)point.proceed();
        PageInfo pageInfo = new PageInfo(list);
        return pageInfo;
    }
}
