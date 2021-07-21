package com.security.filters;

import com.security.config.SecurityConfiguration;

import javax.inject.Inject;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;

@Provider
public class AuthorizationFilterFeature implements DynamicFeature {

    private final SecurityConfiguration config;

    @Inject
    public AuthorizationFilterFeature(SecurityConfiguration config){
        this.config = config;
    }

    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext featureContext) {
        featureContext.register(new AuthorizationFilter(config));
    }
}
