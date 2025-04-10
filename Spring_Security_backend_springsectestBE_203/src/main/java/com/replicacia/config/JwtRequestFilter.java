package com.replicacia.config;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.replicacia.service.impl.UserDetailsServiceImpl;
import com.replicacia.rest.security.util.JwtTokenUtil;
import com.replicacia.web.ApiError;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {
	private final UserDetailsServiceImpl jwtUserDetailsService;
	private final JwtTokenUtil jwtTokenUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		final String requestTokenHeader = request.getHeader("Authorization");
		String username = null;
		String jwtToken = null;

		String path = request.getRequestURI();

		// Skip token validation for static resources and index page
		if (path.equals("/") || path.startsWith("/static/") || path.endsWith(".html") || path.endsWith(".css")
				|| path.endsWith(".js") || path.endsWith(".png") || path.endsWith(".jpg")) {
			chain.doFilter(request, response);
			return;
		}

		// Skip token validation if no Authorization header is present
		if (requestTokenHeader == null) {
			chain.doFilter(request, response);
			return;
		}

		// JWT Token is in the form "Bearer token". Extract the token
		if (requestTokenHeader.startsWith("Bearer ")) {
			jwtToken = requestTokenHeader.substring(7);
			try {
				username = jwtTokenUtil.getUsernameFromToken(jwtToken);
			} catch (IllegalArgumentException e) {
				log.error("Unable to get JWT Token");
				setErrorResponse(response, "Unable to get JWT Token");
				return;
			} catch (ExpiredJwtException e) {
				log.error("JWT Token has expired");
				setErrorResponse(response, "JWT Token has expired");
				return;
			}
		} else {
			log.error("JWT Token does not begin with Bearer String");
			setErrorResponse(response, "JWT Token does not begin with Bearer String");
			return;
		}

		// Once we get the token, validate it.
		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			UserDetails userDetails = this.jwtUserDetailsService.loadUserByUsername(username);

			// If token is valid, configure Spring Security to manually set authentication
			if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities());
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		}
		chain.doFilter(request, response);
	}

	private void setErrorResponse(HttpServletResponse response, String message) {
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		response.setContentType("application/json");

		List<String> details = new ArrayList<>();
		details.add(message);

		ApiError err = new ApiError(LocalDateTime.now(), HttpStatus.UNAUTHORIZED, "Authentication failed", details);
		try {
			String json = err.convertToJson();
			response.getWriter().write(json);
		} catch (IOException e) {
			log.error("Exception in parsing ApiError", e);
		}
	}
}