package com.jobportal.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import com.jobportal.service.JwtService;
import com.jobportal.utils.Role;

@Component
public class RoleInterceptor implements HandlerInterceptor {

    private final JwtService jwtService;

    public RoleInterceptor(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        // Not a controller method (static resources etc.)
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        // 1) Check for @Role on method
        Role roleAnnotation = handlerMethod.getMethodAnnotation(Role.class);

        // 2) If not on method, check on class
        if (roleAnnotation == null) {
            roleAnnotation = handlerMethod.getBeanType().getAnnotation(Role.class);
        }

        // 3) If no annotation => no role restriction
        if (roleAnnotation == null) {
            return true;
        }

        // 4) Extract token
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
            return false;
        }
        String token = authHeader.substring(7);

        // 5) Validate token
        if (!jwtService.isTokenValid(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        // 6) Get user role from token (you already said you have extractRole)
        String userRole = jwtService.extractRole(token); // e.g. "ADMIN"

        // 7) Check if user role is allowed
        String allowedRole = roleAnnotation.value();
        if (allowedRole.equalsIgnoreCase(userRole)) {
            return true; // access granted
        }

        response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403
        return false;
    }
}
