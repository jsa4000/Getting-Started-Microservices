package com.example.gateway.filters;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.RIBBON_ROUTING_FILTER_ORDER;

@Slf4j
public class DebugRoutingFilter extends ZuulFilter {

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        if (ctx.getRouteHost() != null) {
            log.info("Routing request from origin: {}", ctx.getRouteHost().getAuthority());
        } else {
            log.info("There is no default routing. Using service discovery.");
        }
        return null;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public String filterType() {
        return PRE_TYPE;
    }

    @Override
    //public int filterOrder() { return DEBUG_FILTER_ORDER - 1; }
    public int filterOrder() { return RIBBON_ROUTING_FILTER_ORDER - 1; }

}