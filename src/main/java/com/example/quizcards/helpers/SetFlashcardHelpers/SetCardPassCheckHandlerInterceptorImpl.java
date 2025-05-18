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
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.text.MessageFormat;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SetCardPassCheckHandlerInterceptorImpl implements HandlerInterceptor {
    ISetFlashcardRepository setRepo;

    RedisUtils redisUtils;

    @Override
    public boolean preHandle(HttpServletRequest req,
                             HttpServletResponse res,
                             Object handler) throws Exception {
        PasswordEncoder pwdEncoder = new BCryptPasswordEncoder();

        // Check method name
        String method = req.getMethod();
        if (!"GET".equals(method)
                && !"POST".equals(method)
                && !"PUT".equals(method)
                && !"DELETE".equals(method)
                && !"PATCH".equals(method)) {
            return true;
        }

        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod hm = (HandlerMethod) handler;

        // Check method that have ISetCardPassCheckHandlerInterceptor annotation
        if (!hm.getMethod().isAnnotationPresent(ISetCardPassCheckHandlerInterceptor.class)) {
            return true;
        }

        // Get data from path variables
        @SuppressWarnings("unchecked")
        Map<String, String> vars =
                (Map<String, String>) req.getAttribute(
                        HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        // Parse setId and throw error if parse failed
        Object setIdObj = vars.getOrDefault("setId", vars.get("set_id"));
        Long setId = null;
        if (setIdObj != null) {
            try {
                setId = Long.parseLong(setIdObj.toString());
            } catch (NumberFormatException e) {
                // nothing to do
            }
        } else {
            setIdObj = req.getParameter("setId");
            if (setIdObj != null) {
                try {
                    setIdObj = req.getParameter("set_id");
                    setId = Long.parseLong(setIdObj.toString());
                } catch (NumberFormatException e) {
                    // nothing to do
                }
            }
        }
        if (setIdObj == null || setId == null) {
            res.sendError(HttpStatus.BAD_REQUEST.value(),
                    "Missing setId in path or body");
            return false;
        }

        // Find set by setId and throw error if not found
        SetFlashcard set = setRepo.findById(setId).orElse(null);
        if (set == null) {
            res.sendError(HttpStatus.NOT_FOUND.value(),
                    "Set not found");
            return false;
        }

        // Bypass to next interceptor (or controller endpoint) if set does not have password
        if (set.getHashPassword() == null || set.getHashPassword().isBlank()) {
            return true;
        }

        // Check if user is authenticated
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            res.sendError(HttpStatus.UNAUTHORIZED.value(),
                    "Unauthorized");
            return false;
        }

        // Get information of authenticated user
        UserPrincipal up = (UserPrincipal) auth.getPrincipal();

        // Check if the user is the owner of the set
        if (Objects.equals(up.getId(), set.getUser().getUserId())) {
            return true;
        }

        // Check if the user has previously confirmed the password entry
        String key = MessageFormat.format(
                "set_{0}_user_{1}_pass_checked",
                setId, up.getId());

        if (redisUtils.hasKey(key)) {
            return true;
        }

        // Extract "set password" and "do not authenticate set next time" from headers
        String rawPwd = req.getHeader("X-Set-Password");
        String rawValid = req.getHeader("X-Set-Password-Valid-At");

        // Throw if password is not provided
        if (rawPwd == null) {
            forbidden(res);
            return false;
        }

        // Parse validAt and throw error if parse failed or range is invalid
        Long validAt;
        try {
            if (rawValid == null) {
                invalidValidAt(res, new NullPointerException());
                return false;
            }
            validAt = Long.parseLong(rawValid);
        } catch (NumberFormatException e) {
            invalidValidAt(res, e);
            return false;
        }

        // Check if validAt is in range (1 second - 3 days)
        if (validAt < 1 || validAt > 3 * 24 * 3600) {
            invalidValidAt(res, new IllegalArgumentException());
            return false;
        }

        // Check if password is correct
        if (!pwdEncoder.matches(rawPwd, set.getHashPassword())) {
            forbidden(res);
            return false;
        }

        // Save password check result to Redis with valid at
        redisUtils.saveToRedis(key, true, validAt, TimeUnit.MINUTES);

        // Bypass to next interceptor (or controller endpoint)
        return true;
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
}
