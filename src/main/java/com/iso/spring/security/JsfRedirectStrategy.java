package com.iso.spring.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.session.InvalidSessionStrategy;
import org.springframework.util.StringUtils;
 
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
 
/**
 * Inspired by <a href="http://stackoverflow.com/questions/10143539/jsf-2-spring-security-3-x-and-richfaces-4-redirect-to-login-page-on-session-tim">StackOverflow.com</a>
 * and by <a href="http://www.icesoft.org/wiki/display/ICE/Spring+Security#SpringSecurity-Step4%3AConfigureYourSpringSecurityredirectStrategy">Spring Security 3 and ICEfaces 3 Tutorial</a>.
 *
 * @author banterCZ
 */
public class JsfRedirectStrategy implements InvalidSessionStrategy {
 
    private Logger logger = LoggerFactory.getLogger(getClass());
 
    private static final String FACES_REQUEST_HEADER = "faces-request";
 
    private String invalidSessionUrl;
    private String loginUrl;
    private String installationUrl;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onInvalidSessionDetected(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {        
 
        boolean ajaxRedirect = "partial/ajax".equals(request.getHeader(FACES_REQUEST_HEADER));
        
        String contextPath = request.getContextPath();
        String timeoutUrl = contextPath + this.invalidSessionUrl;
        String loginPageUrl = contextPath + this.loginUrl;
        String requestURI = request.getRequestURI();
        		
        if(ajaxRedirect) {
            logger.debug("Session expired due to ajax request, redirecting to '{}'", timeoutUrl);
            
            String ajaxRedirectXml = createAjaxRedirectXml(timeoutUrl);
            logger.debug("Ajax partial response to redirect: {}", ajaxRedirectXml);
 
            response.setContentType("text/xml");
            response.getWriter().write(ajaxRedirectXml);
        } else {
        	logger.debug("Session expired due to non-ajax request, redirecting to '{}'", timeoutUrl);
        	if (requestURI.equalsIgnoreCase(timeoutUrl) || requestURI.equalsIgnoreCase(loginPageUrl) || requestURI.equalsIgnoreCase(this.installationUrl)) {
        		request.getSession(true);
        		response.sendRedirect(requestURI);
        	}else {
        		request.getSession(false);
        		response.sendRedirect(loginPageUrl);
        	}
        }
 
    }
 
    /*private String getRequestUrl(HttpServletRequest request) {
        StringBuffer requestURL = request.getRequestURL();
 
        String queryString = request.getQueryString();
        if (StringUtils.hasText(queryString)) {
            requestURL.append("?").append(queryString);
        }
 
        return requestURL.toString();
    }*/
 
    private String createAjaxRedirectXml(String redirectUrl) {
        return new StringBuilder()
                        .append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
                        .append("<partial-response><redirect url=\"")
                        .append(redirectUrl)
                        .append("\"></redirect></partial-response>")
                        .toString();
    }
 
    public void setInvalidSessionUrl(String invalidSessionUrl) {
        this.invalidSessionUrl = invalidSessionUrl;
    }

	public void setLoginUrl(String loginUrl) {
		this.loginUrl = loginUrl;
	}

	public String getInstallationUrl() {
		return installationUrl;
	}

	public void setInstallationUrl(String installationUrl) {
		this.installationUrl = installationUrl;
	}
 
}
