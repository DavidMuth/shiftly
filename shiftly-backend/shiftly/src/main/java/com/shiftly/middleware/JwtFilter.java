package com.shiftly.middleware;

import com.shiftly.services.JwtService;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RequestHeader;

import java.io.IOException;

@WebFilter(filterName = "JWTFilter", urlPatterns = "/api/shiftly/*")
public class JwtFilter implements Filter {

    private final JwtService jwtService = new JwtService();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String token = httpRequest.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // Gets rid of the "Bearer" and leaves the token
            if (jwtService.validateToken(token)) {
                int userId = jwtService.extractUserId(token);
                String email = jwtService.extractEmail(token);
                httpRequest.setAttribute("userId", userId);
                httpRequest.setAttribute("email", email);

                chain.doFilter(request, response);
            } else {
                httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Token");
            }
        } else {
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authorization header is missing");
        }
    }
}
