package com.km66.wechatrobot.support.filter;

import com.km66.framework.core.filter.BaseCorsFilter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

@WebFilter(filterName = "corsFilter", urlPatterns = "/*")
public class CorsFilter extends BaseCorsFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        setAllowHeaders("id");
        super.doFilter(request, response, chain);

    }
}
