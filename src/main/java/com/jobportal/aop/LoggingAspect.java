package com.jobportal.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

	@Before("execution(* com.jobportal.controller..*(..)) || execution(* com.jobportal.service..*(..)) || execution(* com.jobportal.repository..*(..))")
	public void logBefore(JoinPoint jp) {
		System.out.println("Before calling " + jp.getSignature() + " | args: " + Arrays.toString(jp.getArgs()));
	}

	@AfterReturning(value = "execution(* com.jobportal..*(..))", returning = "result")
	public void logAfterReturn(JoinPoint jp, Object result) {
		System.out.println("After returning" + jp.getSignature() + " | result: " + result);
	}

	@AfterThrowing(value = "execution(* com.jobportal..*(..))", throwing = "ex")
	public void logException(JoinPoint jp, Exception ex) {
		System.err.println("Exception occurred " + jp.getSignature() + " | message: " + ex.getMessage());
	}
}
