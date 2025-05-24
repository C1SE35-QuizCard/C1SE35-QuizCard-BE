package com.example.quizcards.helpers.SetFlashcardHelpers;

import com.example.quizcards.entities.AppUser;
import com.example.quizcards.entities.SetFlashcard;
import com.example.quizcards.entities.role.RoleName;
import com.example.quizcards.repository.ISetFlashcardRepository;
import com.example.quizcards.security.UserPrincipal;
import com.example.quizcards.utils.RedisUtils;
import com.github.benmanes.caffeine.cache.Cache;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.cache.CacheManager;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

import java.lang.annotation.Annotation;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;


@Aspect
@Component
@Order(1)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SetCardPassCheckAspect {

    ISetFlashcardRepository setRepo;
    RedisUtils redisUtils;
    PasswordEncoder passwordEncoder;
    CacheManager cacheManager;

    @Pointcut("@annotation(com.example.quizcards.helpers.SetFlashcardHelpers.SetCardPassCheck)")
    public void passwordCheckMethods() {
    }

    @Around("passwordCheckMethods()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes)
                RequestContextHolder.currentRequestAttributes())
                .getRequest();

        if (!isHttpRequest(request.getMethod())) {
            return pjp.proceed();
        }

        String userId = currentUserId();
        Long setId = parseSetId(pjp, request);
        long startTime = System.currentTimeMillis();
        SetFlashcard set = findSetOrThrow(setId);

        long endTimeFindSet = System.currentTimeMillis();
        System.out.println("Time taken to find set: " + (endTimeFindSet - startTime) + "ms");

        if (isUnprotected(set) || isOwner(set)) {
            return pjp.proceed();
        }

        @SuppressWarnings("unchecked")
        Cache<Object, Object> validateMetaCache =
                (Cache<Object, Object>) cacheManager.getCache("validatedSets").getNativeCache();

        String keyValid = formatKey(setId);

        @SuppressWarnings("unchecked")
        Set<Object> checkL1SetValid = (Set<Object>) validateMetaCache.getIfPresent(keyValid);

        if (checkL1SetValid == null) {
            checkL1SetValid = new HashSet<>();
        }

        if (checkL1SetValid.contains(userId)) {
            return pjp.proceed();
        }

        if (isAlreadyValidated(setId)) {
            checkL1SetValid.add(userId);
            validateMetaCache.put(keyValid, checkL1SetValid);
            return pjp.proceed();
        }

        long endTimeCheckValidSet = System.currentTimeMillis();
        System.out.println("Time taken to check valid set: " + (endTimeCheckValidSet - endTimeFindSet) + "ms");

        String rawPwd = requireHeader(request, "X-Set-Password", "Password");
        long validAt = parseValidAt(request.getHeader("X-Set-Password-Valid-At"));

        if (!passwordEncoder.matches(rawPwd, set.getHashPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "{\"type\":\"Password\"}"
            );
        }

        long endTimeCheckPassword = System.currentTimeMillis();
        System.out.println("Time taken to check password: " + (endTimeCheckPassword - endTimeCheckValidSet) + "ms");

        redisUtils.saveToSet(keyValid, userId, validAt, TimeUnit.SECONDS);

        long endTimeSaveToRedis = System.currentTimeMillis();
        System.out.println("Time taken to save to Redis: " + (endTimeSaveToRedis - endTimeCheckPassword) + "ms");

        long endTimeTotal = System.currentTimeMillis();
        System.out.println("Total time taken: " + (endTimeTotal - startTime) + "ms");

        checkL1SetValid.add(userId);
        validateMetaCache.put(keyValid, checkL1SetValid);
        return pjp.proceed();
    }

    // --- Helpers ---

    private boolean isHttpRequest(String method) {
        return (List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .contains(method));
    }

    private SetFlashcard findSetOrThrow(Long setId) {
        @SuppressWarnings("unchecked")
        Cache<Object, Object> setMetaCache =
                (Cache<Object, Object>) cacheManager.getCache("setPassInfo").getNativeCache();

        Object checkL1Set = setMetaCache.getIfPresent(setId);

        if (checkL1Set != null) {
            return (SetFlashcard) checkL1Set;
        }

//        return setRepo.findById(setId)
//                .orElseThrow(() -> new ResponseStatusException(
//                        HttpStatus.NOT_FOUND, "Set not found"
//                ));
        Object[] rawSetOptional = setRepo.findHashAndOwnerById(setId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Set not found"
                ));

        Object[] setOptional = (Object[]) rawSetOptional[0];

        SetFlashcard set = new SetFlashcard();
        set.setSetId(
                setOptional[0] == null ? null : Long.parseLong(setOptional[0].toString()));
        set.setHashPassword(setOptional[1] == null ? null : setOptional[1].toString());
        set.setUser(AppUser.builder().userId(
                        setOptional[2] == null ? null : Long.parseLong(setOptional[2].toString()))
                .build());

        setMetaCache.put(setId, set);

        return set;
    }

    private boolean isUnprotected(SetFlashcard set) {
        String hash = set.getHashPassword();
        return (hash == null || hash.isBlank());
    }

    private boolean isOwner(SetFlashcard set) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof UserPrincipal up)) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Unauthorized"
            );
        }
        if (up.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(RoleName.ROLE_ADMIN.name()))) {
            return true;
        }
        return Objects.equals(up.getId(), set.getUser().getUserId());
    }

    private boolean isAlreadyValidated(Long setId) {
        String userId = currentUserId();
        return redisUtils.isMember(formatKey(setId), userId);
    }

    private String currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return ((UserPrincipal) auth.getPrincipal()).getId().toString();
    }

    private String requireHeader(HttpServletRequest req, String name, String type) {
        String val = req.getHeader(name);
        if (val == null || val.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "{\"type\":\"" + type + "\"}"
            );
        }
        return val;
    }

    private long parseValidAt(String rawValid) {
        long max = 7 * 24 * 3600;
        if (rawValid == null) {
            return max;
        }
        try {
            long v = Long.parseLong(rawValid);
            if (v < 0 || v > max) {
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "{\"type\":\"Valid at\",\"reason\":\"Range exception\"}"
                );
            }
            return v;
        } catch (NumberFormatException ex) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "{\"type\":\"Valid at\",\"reason\":\"Number format exception\"}"
            );
        }
    }

    private Long parseSetId(ProceedingJoinPoint pjp, HttpServletRequest req) {
        Object raw = extractSetId(pjp, req);
        if (raw == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Missing setId in path or query"
            );
        }
        try {
            return raw instanceof Number
                    ? ((Number) raw).longValue()
                    : Long.parseLong(raw.toString());
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Invalid setId format"
            );
        }
    }

    private Object extractSetId(ProceedingJoinPoint pjp, HttpServletRequest req) {
        MethodSignature sig = (MethodSignature) pjp.getSignature();
        String[] names = sig.getParameterNames();
        Annotation[][] anns = sig.getMethod().getParameterAnnotations();
        Object[] args = pjp.getArgs();

        // 1) @PathVariable
        for (int i = 0; i < args.length; i++) {
            for (Annotation ann : anns[i]) {
                if (ann instanceof PathVariable pv) {
                    String var = !pv.name().isEmpty() ? pv.name() :
                            !pv.value().isEmpty() ? pv.value() :
                                    names[i];
                    if ("setId".equals(var) || "set_id".equals(var)) {
                        return args[i];
                    }
                }
            }
        }
        // 2) request param
        String rp = req.getParameter("setId");
        if (rp == null) rp = req.getParameter("set_id");
        if (rp != null) return rp;
        // 3) DTO property
        for (Object arg : args) {
            if (arg == null) continue;
            BeanWrapper bw = new BeanWrapperImpl(arg);
            if (bw.isReadableProperty("setId")) {
                return bw.getPropertyValue("setId");
            }
        }
        return null;
    }

    private String formatKey(Long setId) {
        return MessageFormat.format("set:{0}:pass_checked", setId);
    }
}