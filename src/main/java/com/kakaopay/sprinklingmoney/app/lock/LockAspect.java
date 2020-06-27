package com.kakaopay.sprinklingmoney.app.lock;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.Ordered;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import com.kakaopay.sprinklingmoney.app.common.aspect.AspectOrder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class LockAspect implements Ordered, InitializingBean {
	private static final ThreadLocal<Boolean> isLocked = new ThreadLocal<>();
	private static final long THREAD_WAITING_TIME = 100; // 100ms
	private static ConcurrentHashMap<String, Expression> expCache;
	private static ExpressionParser parser;

	private final LockService lockService;

	@Override
	public void afterPropertiesSet() throws Exception {
		this.parser = new SpelExpressionParser();
		this.expCache = new ConcurrentHashMap<>(16);
	}

	@Around("@annotation(com.kakaopay.sprinklingmoney.app.lock.RedisLock)")
	public Object checkCreatePaymentLock(ProceedingJoinPoint joinPoint) throws Throwable {
		final String lockKey = getKeyValue(joinPoint);
		boolean isBeginPoint = false;
		final boolean serviceLocked = lockService.isLocked(lockKey);
		if(isLocked.get() == null) {
			isLocked.set(serviceLocked);
			isBeginPoint = true;
		}

		while (serviceLocked) {
			Thread.sleep(THREAD_WAITING_TIME);
			if(!lockService.isLocked(lockKey)) {
				break;
			}
		}

		if(isBeginPoint) {
			lockService.setLock(lockKey, true);
		}

		Object result = null;
		try {
			result = joinPoint.proceed();
		} catch (Throwable throwable) {
			throw throwable;
		} finally {
			lockService.removeLock(lockKey);
			if(isBeginPoint) {
				isLocked.remove();
			}
		}

		return result;
	}

	/**
	 * 애노테이션 정보로부터 지정한 key 조회
	 * @param joinPoint
	 * @return
	 */
	private String getKeyValue(ProceedingJoinPoint joinPoint) {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		RedisLock annotation = signature.getMethod().getAnnotation(RedisLock.class);
		final StandardEvaluationContext standardEvaluationContext = makeContext(joinPoint, signature);

		return (String) getKeyByExp(annotation.key(), standardEvaluationContext);
	}


	private Object getKeyByExp(String exp, EvaluationContext context) {
		return getKey(exp, expression -> expression.getValue(context));
	}

	private StandardEvaluationContext makeContext(ProceedingJoinPoint joinPoint, MethodSignature signature) {
		final StandardEvaluationContext evaluationContext = new StandardEvaluationContext();
		String[] paramNames = signature.getParameterNames();
		Object[] methodArgs = joinPoint.getArgs();

		for (int i = 0, len = methodArgs.length; i < len; i++) {
			evaluationContext.setVariable(paramNames[i], methodArgs[i]);
		}

		return evaluationContext;
	}

	/**
	 * Expression 을 통해서 객체를 얻어올 때 예외를 처리하기 위한 메소드입니다.
	 * 추가로 Expression 을 캐싱하여 같은 표현식으로 새로운 표현식 객체를 생성하지 않도록 합니다.
	 *
	 * @param exp SpEL 표현식
	 * @param action expression 으로 객체를 얻어올 action
	 * @return action 의 return 값
	 */
	private Object getKey(String exp, Function<Expression, Object> action) {

		Expression expression = expCache.get(exp);

		if (expression == null) {
			expCache.putIfAbsent(exp, parser.parseExpression(exp));
			expression = expCache.get(exp);
		}

		Object key = null;
		try {
			key = action.apply(expression);
		} catch (SpelEvaluationException ex) {
			throw new IllegalArgumentException(ex.getMessage(), ex);
		}

		if (key == null) {
			throw new IllegalArgumentException("표현식에 해당하는 key 를 찾을 수 없거나 값이 null 입니다.");
		}

		return key;
	}


	@Override
	public int getOrder() {
		return AspectOrder.REDIS_LOCK.getOrder();
	}
}
