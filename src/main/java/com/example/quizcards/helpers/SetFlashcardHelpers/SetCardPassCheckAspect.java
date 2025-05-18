package com.example.quizcards.helpers.SetFlashcardHelpers;

import com.example.quizcards.entities.SetFlashcard;
import com.example.quizcards.repository.ISetFlashcardRepository;
import com.example.quizcards.security.UserPrincipal;
import com.example.quizcards.utils.RedisUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

import java.lang.annotation.Annotation;
import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


@Aspect
@Component
@Order(1)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SetCardPassCheckAspect {
    ISetFlashcardRepository setRepo;

    RedisUtils redisUtils;

    HttpServletRequest request;

    PasswordEncoder pwdEncoder = new BCryptPasswordEncoder();

    @Pointcut("@annotation(com.example.quizcards.helpers.SetFlashcardHelpers.SetCardPassCheck)")
    public void passwordCheckMethods() {
    }

    @Around("passwordCheckMethods()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes)
                RequestContextHolder.currentRequestAttributes())
                .getRequest();
        String method = request.getMethod();
        if (!List.of("GET", "POST", "PUT", "DELETE", "PATCH").contains(method)) {
            return pjp.proceed();
        }
        Object rawSetId = extractSetId(pjp);
        if (rawSetId == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Missing setId in path or query"
            );
        }

        // Cast tại đây nếu repo cần Long
        Long setId;
        try {
            setId = rawSetId instanceof Number
                    ? ((Number) rawSetId).longValue()
                    : Long.parseLong(rawSetId.toString());
        } catch (Exception ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Invalid setId format"
            );
        }

        // 3) Load SetFlashcard
        SetFlashcard set = setRepo.findById(setId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Set not found"
                ));


        // 4) Nếu không có password → proceed
        if (set.getHashPassword() == null || set.getHashPassword().isBlank()) {
            return pjp.proceed();
        }

        // 5) Kiểm auth
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Unauthorized"
            );
        }

        // Get information of authenticated user
        UserPrincipal up = (UserPrincipal) auth.getPrincipal();

        // Check if the user is the owner of the set
        if (Objects.equals(up.getId(), set.getUser().getUserId())) {
            return pjp.proceed();
        }

        // Check if the user has previously confirmed the password entry
        String key = MessageFormat.format(
                "set_{0}_user_{1}_pass_checked",
                setId, up.getId());

        if (redisUtils.hasKey(key)) {
            return pjp.proceed();
        }

        // Extract "set password" and "do not authenticate set next time" from headers
        String rawPwd = request.getHeader("X-Set-Password");
        String rawValid = request.getHeader("X-Set-Password-Valid-At");

        // Throw if password is not provided
        if (rawPwd == null) {
            throwForbidden();
        }

        // Parse validAt and throw error if parse failed or range is invalid
        // 9) Parse validAt
        long validAt = 3L * 24 * 3600;
        try {
            if (rawValid != null) {
                validAt = Long.parseLong(rawValid);
            }
        } catch (Exception ex) {
            String reason = ex instanceof NumberFormatException
                    ? "Number format exception"
                    : "Range/Null exception";
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    String.format("{\"type\":\"Valid at\",\"reason\":\"%s\"}", reason)
            );
        }

        // Check if validAt is in range (1 second - 3 days)
        if (validAt < 1 || validAt > 3 * 24 * 3600) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "{\"type\":\"Valid at\",\"reason\":\"Range exception\"}"
            );
        }

        // 10) Check password
        if (!pwdEncoder.matches(rawPwd, set.getHashPassword())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "{\"type\":\"Password\"}"
            );
        }

        // Save password check result to Redis with valid at
        redisUtils.saveToRedis(key, true, validAt, TimeUnit.SECONDS);

        // Bypass to next interceptor (or controller endpoint)
        return pjp.proceed();
    }

    private void throwForbidden() {
        throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "{\"type\":\"Password\"}"
        );
    }

    private void forbidden(HttpServletResponse res) throws Exception {
        res.setStatus(HttpStatus.FORBIDDEN.value());
        res.getWriter().write("{\"type\":\"Password\"}");
    }

    private void invalidValidAt(HttpServletResponse res, Exception e) throws Exception {
        boolean isNumberFormatException = e instanceof NumberFormatException;
        boolean isNullPointerException = e instanceof NullPointerException;
        boolean isRangeException = e instanceof IllegalArgumentException;
        res.setStatus(HttpStatus.FORBIDDEN.value());
        res.getWriter().write("{\"type\":\"Valid at\", \"reason\":\"" +
                (isNumberFormatException ? "Number format exception" :
                        (isNullPointerException ? "Null pointer exception" :
                                (isRangeException ? "Range exception" : "Unknown"))) + "\"}");
    }

    private Object extractSetId(ProceedingJoinPoint pjp) {
        MethodSignature sig = (MethodSignature) pjp.getSignature();
        String[] paramNames = sig.getParameterNames();
        Annotation[][] annArr = sig.getMethod().getParameterAnnotations();
        Object[] args = pjp.getArgs();

        // 1) Duyệt từng tham số có @PathVariable
        for (int i = 0; i < args.length; i++) {
            for (Annotation ann : annArr[i]) {
                if (ann instanceof PathVariable pv) {
                    // Xác định tên biến trên URL:
                    // ưu tiên pv.name()/pv.value(), nếu trống thì dùng tên param
                    String varName = !pv.name().isEmpty() ? pv.name()
                            : !pv.value().isEmpty() ? pv.value()
                            : paramNames[i];

                    // Chỉ match chính xác "setId" hoặc "set_id"
                    if ("setId".equals(varName) || "set_id".equals(varName)) {
                        return args[i];  // giữ nguyên type gốc
                    }
                }
            }
        }

        String rp = request.getParameter("setId");
        if (rp == null) {
            rp = request.getParameter("set_id");
        }
        if (rp != null) {
            return rp;  // vẫn là String, xử lý cast bên ngoài
        }


        // 2) Nếu không tìm thấy path-variable, fallback: bất kỳ DTO nào có property "setId"
        for (Object arg : args) {
            if (arg == null) continue;
            BeanWrapper wrapper = new BeanWrapperImpl(arg);
            if (wrapper.isReadableProperty("setId")) {
                return wrapper.getPropertyValue("setId");
            }
        }

        // 3) Không tìm thấy
        return null;
    }
}
