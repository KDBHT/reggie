package com.itheima.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;


/*检查用户是否已经完成登录
* */
@WebFilter(filterName = "LoginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    //路径匹配器，支持通配符
    public static  final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        log.info("拦截到请求：{}",request.getRequestURI());
        //1.获取本次请求的URI
        String requestURI = request.getRequestURI();
        //定义不需要处理的请求
        String[] urls= new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",//移动端发送短信
                "/user/login",//移动端登录请求
                "/doc.html",
                "/webjars/**",
                "/swagger-resources",
                "/v2/api-docs"
//                "/employee/page"
        };
        //2.判断本次请求是否需要处理
        boolean check = check(urls, requestURI);
        //3.如果不需要处理，则直接放行
        if (check){
            log.info("本次请求{}不需要处理",requestURI);
            filterChain.doFilter(request,response);
            return;
        }
        //4-1判断登录状态，如果已经登陆，则直接放行
        if (request.getSession().getAttribute("Employee") != null){
            log.info("用户已登录，用户id为{}",request.getSession().getAttribute("Employee"));
            //向threadLocal中添加当前登录用户的id，方便后续mybatisplus公共字段填充用户id等信息
            Long userid = (Long) request.getSession().getAttribute("Employee");
            BaseContext.setCurrentId(userid);
            filterChain.doFilter(request,response);
            return;
        }
        //4-2判断手机端用户登录状态，如果已经登陆，则直接放行
        if (request.getSession().getAttribute("user") != null){
            log.info("用户已登录，用户id为{}",request.getSession().getAttribute("user"));
            //向threadLocal中添加当前登录用户的id，方便后续mybatisplus公共字段填充用户id等信息
            Long phoneuserid = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(phoneuserid);
            filterChain.doFilter(request,response);
            return;
        }
        log.info("用户未登录");
        //5.如果未登录则返回未登录结果，通过输出流方式向客户端页面响应数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));

//        filterChain.doFilter(request,response);
    }
    /*
    * 路径匹配，检查本次请求是否需要放行
    * */
    public boolean check(String[] urls,String requestURI){
    for (String url:urls){
        boolean match = PATH_MATCHER.match(url, requestURI);
        if (match){
            return true;
        }
    }
    return false;
    }
}
