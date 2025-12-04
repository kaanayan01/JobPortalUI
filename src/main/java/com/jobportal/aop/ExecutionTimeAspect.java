package com.jobportal.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ExecutionTimeAspect {

	@Around("execution(* com.jobportal.service..*(..)) || execution(* com.jobportal.repository..*(..))")
	public Object measureExecutionTime(ProceedingJoinPoint pjp) throws Throwable {
		long start = System.currentTimeMillis();
		Object result = pjp.proceed();
		long duration = System.currentTimeMillis() - start;
		System.out.println("⏱️ [EXECUTION TIME] " + pjp.getSignature() + " executed in " + duration + "ms");
		return result;
	}
}
