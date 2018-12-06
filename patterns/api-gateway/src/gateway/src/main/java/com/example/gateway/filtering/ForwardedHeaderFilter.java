package com.example.gateway.filtering;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.POST_TYPE;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.SEND_RESPONSE_FILTER_ORDER;

@Slf4j
public class ForwardedHeaderFilter extends ZuulFilter {

    public final String GATEWAY_HEADER_KEY = "X-Gateway-Forwarded";

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        if (ctx.getRouteHost() != null) {
            ctx.addZuulResponseHeader(GATEWAY_HEADER_KEY, ctx.getRouteHost().getAuthority());
        } else {
            ctx.addZuulResponseHeader(GATEWAY_HEADER_KEY, "service-discovery");
        }
        return null;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        return !ctx.containsKey(GATEWAY_HEADER_KEY);
    }

    @Override
    public String filterType() {
        return POST_TYPE;
    }

    @Override
    public int filterOrder() {
        return SEND_RESPONSE_FILTER_ORDER - 1;
    }

}