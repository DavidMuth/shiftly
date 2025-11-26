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

    JwtService jwtService = new JwtService();


    public void doFilter(@RequestHeader ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String token = httpRequest.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // Gets rid of the "Bearer" and leaves the token
            if (jwtService.validateToken(token)) {
                HttpServletResponse httpResponse = (HttpServletResponse)  response;
                chain.doFilter(request, response);
            } else {
                ((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Token");
            }
        } else {
            ((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authorization header is missing");
        }
    }
}
