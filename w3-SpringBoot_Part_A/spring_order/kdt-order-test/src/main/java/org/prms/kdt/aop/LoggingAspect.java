package org.prms.kdt.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.prms.kdt.JdkProxyTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

// @Aspect : Asppect로 알려주고, @Componet 빈으로 등록
@Aspect
@Component
public class LoggingAspect {

    private static final Logger log= LoggerFactory.getLogger(LoggingAspect.class);

    // public 메소드를 실행할때 jointPoint를 적용할때 Around로 적용
    // execution (메소드접근제한자 return값 패키지.클래스.메소드명)  ..은 하위 전체 적용할때 사용
//    @Pointcut("execution(public * org.prms.kdt..*Service.*(..))")
//    public void servicePublicMethodPointCut(){};

//    @Around("org.prms.kdt.aop.CommonPointCut.repositoryInsertPointCut()")
    @Around("@annotation(org.prms.kdt.aop.TrackTime)") // 특정 annotation으로만 쓰겠다
    public Object log(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("Before method called {}",joinPoint.getSignature().toString());

        var startTime=System.nanoTime();
        var result=joinPoint.proceed();
        var endTime=System.nanoTime()-startTime;

        log.info("After method called {} and time taken by {} nanoseconds",result,endTime);
        return result;

    }
}
