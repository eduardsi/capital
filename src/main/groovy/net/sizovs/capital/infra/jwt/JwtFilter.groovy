package net.sizovs.capital.infra.jwt

import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.JWTVerifyException
import org.apache.commons.codec.binary.Base64
import org.springframework.web.filter.GenericFilterBean

import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

import static org.springframework.http.HttpMethod.OPTIONS

class JwtFilter extends GenericFilterBean {

    final jwtVerifier

    JwtFilter(String clientId, String clientSecret, String issuer) {
        def base64 = new Base64(true).decodeBase64(clientSecret)
        jwtVerifier = new JWTVerifier(base64, clientId, issuer)
    }

    @Override
    void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) {

        def request = req as HttpServletRequest

        if (isOptions(request) || request.getHeaders("Accept").any { "text/html" }) {
            chain.doFilter(req, res)
            return
        }

        def authHeader = request.getHeader("Authorization")

        if (!authHeader?.startsWith("Bearer ")) {
            throw new ServletException("Missing or invalid Authorization header.")
        }

        def token = authHeader.substring(7)
        try {
            jwtVerifier.verify(token)
        }
        catch (JWTVerifyException ignored) {
            throw new ServletException("Invalid token.")
        }

        chain.doFilter(req, res)
    }

    private isOptions(HttpServletRequest request) {
        OPTIONS.matches(request.method)
    }
}
