package ir.tejaratBank.core.CoreBank.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TransactionPerformanceAspect {
    private final static Logger logger = LoggerFactory.getLogger(TransactionPerformanceAspect.class);

    @Around("execution(* ir.tejaratBank.core.CoreBank.service.TransactionService.*(..))")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        String methodName = joinPoint.getSignature().getName();
        try {
            Object result = joinPoint.proceed();
            long end  = System.currentTimeMillis();
            long duration = end - start;
            logger.info("Execution time for method " + methodName + " is " + duration + "ms");
            return result;

        }catch (Throwable e){
            logger.error("Exception in Method [{}]: {}", methodName, e.getMessage());
            throw e;
        }
    }
}
