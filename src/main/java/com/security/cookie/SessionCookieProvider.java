package com.security.cookie;

import com.security.cookie.impl.SessionCookie;
import com.security.exception.MalformedCookieException;

public interface SessionCookieProvider {
    String getCookie(String principal, String role);
    String getLogoutCookie();
    SessionCookie decodeCookie(String encoded) throws MalformedCookieException;
}
