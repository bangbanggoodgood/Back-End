package home.bangbanggoodgood.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthFilter extends GenericFilterBean {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;  // HttpServletRequest로 형변환
        String uri = request.getRequestURI();  // 요청 URI를 가져옴

        // /oauth/kakao 경로는 필터를 통과시킴
        if (uri.startsWith("/oauth/kakao")) {
            filterChain.doFilter(servletRequest, servletResponse);  // 필터를 건너뛰고 요청을 통과시킴
            return;
        }

        String token = resolveToken((HttpServletRequest) servletRequest);
        System.out.println("do 필터 내부 토큰 : " + token);
        if(token != null) {
            boolean isValid = jwtTokenProvider.validateToken(token);
            System.out.println("isValid = " + isValid);
            if (isValid) {
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
