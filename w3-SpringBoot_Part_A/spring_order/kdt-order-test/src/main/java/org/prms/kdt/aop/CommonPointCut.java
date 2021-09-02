package org.prms.kdt.aop;

import org.aspectj.lang.annotation.Pointcut;

public class CommonPointCut {
    // public 메소드를 실행할때 jointPoint를 적용할때 Around로 적용
    // execution (메소드접근제한자 return값 패키지.클래스.메소드명)  ..은 하위 전체 적용할때 사용

    @Pointcut("execution(public * org.prms.kdt..*Service.*(..))")
    public void servicePublicMethodPointCut(){};


    @Pointcut("execution(* org.prms.kdt..*Repository.*(..))")
    public void repositoryMethodPointCut(){};

    @Pointcut("execution(* org.prms.kdt..*Repository.insert(..))")
    public void repositoryInsertPointCut(){};

}
