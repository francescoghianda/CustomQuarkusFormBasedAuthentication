package com.security.filters;

import com.security.cookie.SessionCookieProvider;
import com.security.config.SecurityConfiguration;

import javax.inject.Inject;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;

@Provider
public class AuthenticationFilterFeature implements DynamicFeature {

    private final SessionCookieProvider cookieProvider;
    private final SecurityConfiguration config;

    @Inject
    public AuthenticationFilterFeature(SessionCookieProvider cookieProvider, SecurityConfiguration config){
        this.cookieProvider = cookieProvider;
        this.config = config;
    }

    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext featureContext) {
        featureContext.register(new AuthenticationFilter(cookieProvider, config));
    }
}
