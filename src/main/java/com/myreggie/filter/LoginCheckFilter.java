package com.myreggie.filter;

import com.alibaba.fastjson.JSON;
import com.myreggie.common.BaseContext;
import com.myreggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@WebFilter(filterName = "LoginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter
{
    // 路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER=new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws ServletException, IOException
    {
        // 转换格式
        HttpServletRequest httpServletRequest=(HttpServletRequest)servletRequest;
        HttpServletResponse httpServletResponse=(HttpServletResponse)servletResponse;

        // 获取本次请求 URI
        String requestURI=httpServletRequest.getRequestURI();
        log.info("监测到请求：{}",requestURI);
        // 定义不需要处理的请求路径
        String[] urls=new String[]
                {
                        "/employee/login",
                        "/employee/logout",
                        "/backend/**",
                        "/front/**",
                        "/user/sendMsg",   // 发送短信验证码
                        "/user/login",    // 手机登录页面
                };

        // 判断本次的请求是否需要拦截
        boolean check=check(urls,requestURI);
        // 如果不需要处理，则直接放行
        if(check)
        {
            filterChain.doFilter(httpServletRequest,httpServletResponse);
            log.info("请求不需要处理：{}",requestURI);
            return;
        }

        // 判断电脑端登录状态，如果已登录，则直接放行
        if(httpServletRequest.getSession().getAttribute("employee")!=null)
        {
            log.info("用户已登录，请求放行：{}",requestURI);

            // 获取当前线程 id ，用于证明每一次的 http 请求，都是同一个新线程。
            long id =Thread.currentThread().getId();
            log.info("当前线程id:{}",id);

            // 获取员工用户 id
            Long empId=(Long)httpServletRequest.getSession().getAttribute("employee");
            // 将员工用户 id 存储到 ThreadLocal 中，方便自动填充公共字段的时候调用，获取对应的值
            BaseContext.setCurrentId(empId);

            filterChain.doFilter(httpServletRequest,httpServletResponse);


            return;
        }

        // 判断移动端登录状态，如果已登录，则直接放行
        if(httpServletRequest.getSession().getAttribute("user")!=null)
        {
            log.info("手机用户已登录，请求放行：{}",requestURI);

            // 获取当前线程 id ，用于证明每一次的 http 请求，都是同一个新线程。
            long id =Thread.currentThread().getId();
            log.info("当前线程id:{}",id);

            // 获取手机用户 id
            Long userId=(Long)httpServletRequest.getSession().getAttribute("user");
            // 将员工用户 id 存储到 ThreadLocal 中，方便自动填充公共字段的时候调用，获取对应的值
            BaseContext.setCurrentId(userId);

            filterChain.doFilter(httpServletRequest,httpServletResponse);

            return;
        }

        // 如果未登录则返回未登录结果，通过输出流方式向客户端页面响应数据，这里返回数据而不是直接跳转，是因为前端写了跳转代码，前端也存在拦截器
        httpServletResponse.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        log.info("用户未登录，请求拦截：{}",requestURI);
        return;
    }

    /**
     * 路径匹配，检查本次请求是否需要放行
     * @param urls
     * @param requestURI
     * @return
     */
    public boolean check(String[] urls,String requestURI)
    {
        for(String url:urls)
        {
            boolean match=PATH_MATCHER.match(url,requestURI);
            if(match)
            {
                return true;
            }
        }
        return false;
    }
}
