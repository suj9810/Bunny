package sparta.bunny.common.aop;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Aspect
@Component
public class LogAop {

	private final ObjectMapper objectMapper;

	@Pointcut("execution(* sparta.bunny.domain..*Controller.*(..)))")
	private void loggingPointcut() {
	}

	@Around("loggingPointcut()")
	public Object logging(ProceedingJoinPoint joinPoint) throws Throwable {
		ServletRequestAttributes attributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
		if (attributes == null) {
			log.warn("No request attributes available.");
			return joinPoint.proceed();
		}
		HttpServletRequest request = attributes.getRequest();

		MethodSignature signature = (MethodSignature)joinPoint.getSignature();
		Method method = signature.getMethod();
		String[] parameterNames = signature.getParameterNames();
		Object[] args = joinPoint.getArgs();
		String requestUri = request.getRequestURI();
		LocalDateTime requestTime = LocalDateTime.now();
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();

		Map<String, Object> pathVariables = new LinkedHashMap<>();
		Map<String, Object> requestBodies = new LinkedHashMap<>();
		Map<String, Object> requestParams = new LinkedHashMap<>();

		for (int i = 0; i < args.length; i++) {
			Object value = (args[i] == null) ? "null" : args[i];

			for (Annotation annotation : parameterAnnotations[i]) {
				// PathVariable 분리
				if (annotation.annotationType() == PathVariable.class) {
					pathVariables.put(parameterNames[i], value);
					// RequestBody 분리
				} else if (annotation.annotationType() == RequestBody.class) {
					requestBodies.put(parameterNames[i], value);
					// RequestParam 분리
				} else if (annotation.annotationType() == RequestParam.class) {
					requestParams.put(parameterNames[i], value);
				}
			}
		}

		String path = objectMapper.writeValueAsString(pathVariables);
		String body = objectMapper.writeValueAsString(requestBodies);
		String param = objectMapper.writeValueAsString(requestParams);

		log.info("[Request] | time = {} | URL = {}", requestTime, requestUri);
		log.info("[Method Name] = {}", method.getName());
		log.info("[RequestBody] = {}", body);
		log.info("[PathVariable] = {}", path);
		log.info("[RequestParam] = {}", param);

		Object response = null;
		try {
			response = joinPoint.proceed();
			log.info("[ResponseBody] = {}", objectMapper.writeValueAsString(response));
			return response;
		} catch (Throwable ex) {
			log.error("[Exception] | time = {} | URL = {}", requestTime, requestUri);
			log.error("[Exception Message] = {}", ex.getMessage(), ex);
			throw ex;
		}
	}
}
