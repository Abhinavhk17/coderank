package com.coderank.api.ratelimit;

import com.coderank.api.domain.User;
import com.coderank.api.domain.UserRole;
import com.coderank.api.exception.RateLimitExceededException;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof User) {
            User user = (User) authentication.getPrincipal();
            String key = user.getUsername();

            Bucket bucket = cache.computeIfAbsent(key, k -> createBucket(user.getRole()));

            if (!bucket.tryConsume(1)) {
                throw new RateLimitExceededException("Rate limit exceeded. Please try again later.");
            }
        }

        return true;
    }

    private Bucket createBucket(UserRole role) {
        Bandwidth limit = switch (role) {
            case ADMIN -> Bandwidth.classic(1000, Refill.intervally(1000, Duration.ofMinutes(1)));
            case PREMIUM -> Bandwidth.classic(100, Refill.intervally(100, Duration.ofMinutes(1)));
            case USER -> Bandwidth.classic(10, Refill.intervally(10, Duration.ofMinutes(1)));
        };

        return Bucket.builder()
            .addLimit(limit)
            .build();
    }
}

