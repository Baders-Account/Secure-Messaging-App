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

        
        if (httpRequest.getRequestURI().equals("/messages/send")) {
            String key = getClientKey(httpRequest);
            Bucket bucket = buckets.computeIfAbsent(key, k -> createNewBucket());

            if (bucket.tryConsume(1)) {
                chain.doFilter(request, response);
            } else {
                httpResponse.setStatus(429); // Too Many Requests
                httpResponse.getWriter().write("Too many requests - try again later");
            }
        } else {
            chain.doFilter(request, response);
        }
    }

    private String getClientKey(HttpServletRequest request) {
       
        return request.getRemoteAddr();
    }

    private Bucket createNewBucket() {
    return Bucket.builder()
        
        .addLimit(limit -> limit
            .capacity(5)
            .refillGreedy(5, Duration.ofSeconds(10))
        )
        
        .addLimit(limit -> limit
            .capacity(100)
            .refillGreedy(100, Duration.ofHours(1))
        )
        .build();
}
}
