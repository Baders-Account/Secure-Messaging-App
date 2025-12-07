package j16.secure.securemessage.filters;

import io.github.bucket4j.Bucket;
import org.springframework.stereotype.Component;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Component
public class RateLimitingFilter implements Filter {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

   @Override
public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {

    HttpServletRequest httpRequest = (HttpServletRequest) request;
    HttpServletResponse httpResponse = (HttpServletResponse) response;

    String uri = httpRequest.getRequestURI();
    
    if (uri.equals("/login") || uri.equals("/messages/send")) {
        String clientIp = getClientKey(httpRequest);
        String bucketKey = clientIp + ":" + uri;
        
        // DEBUG: Print bucket info
        System.out.println("🔍 [DEBUG] IP: " + clientIp + " | Bucket Key: " + bucketKey);
        
        Bucket bucket = buckets.computeIfAbsent(bucketKey, k -> {
            System.out.println("🆕 [DEBUG] Creating NEW bucket for: " + k);
            return createNewBucket();
        });
        
        // Check available tokens
        long availableTokens = bucket.getAvailableTokens();
        System.out.println("🪙 [DEBUG] Available tokens: " + availableTokens);

        if (bucket.tryConsume(1)) {
            System.out.println("✅ [FILTER] Request allowed: " + uri + " (tokens left: " + (availableTokens - 1) + ")");
            chain.doFilter(request, response);
        } else {
            System.out.println("🚨 [SECURITY] Rate limit triggered: " + uri + " from " + clientIp);
            
            httpResponse.setStatus(429);
            httpResponse.setContentType("application/json");
            httpResponse.getWriter().write(
                "{\"error\":\"Too many login attempts - brute force protection activated\",\"rateLimit\":true}"
            );
        }
    } else {
        chain.doFilter(request, response);
    }
}

private String getClientKey(HttpServletRequest request) {
    // Get real IP address (handles proxies)
    String ip = request.getHeader("X-Forwarded-For");
    if (ip == null || ip.isEmpty()) {
        ip = request.getRemoteAddr();
    }
    System.out.println("🌐 [DEBUG] Client IP: " + ip);
    return ip;
}

private Bucket createNewBucket() {
    System.out.println("🪣 Creating bucket: 5 attempts per 2 minutes");
    return Bucket.builder()
        .addLimit(limit -> limit
            .capacity(5)// Max 5 attempts
            .refillIntervally(5, Duration.ofSeconds(10))   // Refill after 2 minutes
        )
        .build();
}


}
