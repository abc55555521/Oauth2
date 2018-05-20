package demo.oauth2.server.controller;

import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.oltu.oauth2.as.issuer.MD5Generator;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuerImpl;
import org.apache.oltu.oauth2.as.request.OAuthAuthzRequest;
import org.apache.oltu.oauth2.as.response.OAuthASResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.error.OAuthError;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.message.OAuthResponse;
import org.apache.oltu.oauth2.common.message.types.ResponseType;
import org.apache.oltu.oauth2.common.utils.OAuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import demo.oauth2.server.model.front.FrontUser;
import demo.oauth2.server.model.oauth2.Oauth2Client;
import demo.oauth2.server.model.oauth2.Oauth2Code;
import demo.oauth2.server.service.front.FrontUserService;
import demo.oauth2.server.service.oauth2.Oauth2ClientService;
import demo.oauth2.server.service.oauth2.Oauth2CodeService;

@Controller
public class AuthorizeController {
	protected final transient Log logger = LogFactory.getLog(AuthorizeController.class);
	
    @Autowired
    private Oauth2ClientService oauth2ClientService;
    @Autowired
    private Oauth2CodeService oauth2CodeService;
    @Autowired
    private FrontUserService frontUserService;
    
    @RequestMapping("/oauth2/authorize")
    public ModelAndView authorize(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            OAuthAuthzRequest oauthRequest = new OAuthAuthzRequest(request);
            String clientId = oauthRequest.getClientId();
            //构建OAuth 授权请求
            Oauth2Client client = oauth2ClientService.selectByClientId(clientId);
            
            //检查传入的客户端id是否正确
            if (client == null) {
            	String respString = Oauth2Utils.errMsgToJson(OAuthError.TokenResponse.INVALID_CLIENT, "invalid clientId", "10101");
                
                PrintWriter pw = response.getWriter();
            	response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                pw.print(respString);
                pw.flush();
                pw.close();
                
                return null;
            }

            HttpSession session = request.getSession();
            String isLogin = (String) session.getAttribute("isLogin");
            
            if(!"true".equals(isLogin)) {
                String username = request.getParameter("username");
                if(StringUtils.isEmpty(username)) {
                	if(!clientId.startsWith("mobile")) {
                		request.getRequestDispatcher("/oauth2/login").forward(request, response);
                		return null;
                	} else {
                		String respString = Oauth2Utils.errMsgToJson(OAuthError.TokenResponse.INVALID_CLIENT, "invalid username", "10103");
                        
                        PrintWriter pw = response.getWriter();
                    	response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        pw.print(respString);
                        pw.flush();
                        pw.close();
                        
                        return null;
                	}
                }
                
                FrontUser frontUser = frontUserService.selectByUsername(username);
                if(frontUser == null) {
                	if(!clientId.startsWith("mobile")) {
                		request.getRequestDispatcher("/oauth2/login").forward(request, response);
                	} else {
                		String respString = Oauth2Utils.errMsgToJson(OAuthError.TokenResponse.INVALID_CLIENT, "invalid username", "10103");
                        
                        PrintWriter pw = response.getWriter();
                    	response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        pw.print(respString);
                        pw.flush();
                        pw.close();
                        
                        return null;
                	}
                } else {
                	//校验密码,传过来已经是公钥加密的
                    String password = request.getParameter("password");
                    
                    //等会按密码加密方式修改
                    if(frontUser.getPassword().equals(password)) {
                    	if(!clientId.startsWith("mobile")) {
                    		request.getRequestDispatcher("/oauth2/login").forward(request, response);
                    		return null;
                    	} else {
                    		String respString = Oauth2Utils.errMsgToJson(OAuthError.TokenResponse.INVALID_CLIENT, "invalid password", "10104");
                    		
                    		PrintWriter pw = response.getWriter();
                    		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    		pw.print(respString);
                    		pw.flush();
                    		pw.close();
                    		
                    		return null;
                    	}
                    }
                }
            }
            
            //生成授权码
            String authorizationCode = null;
            //responseType目前仅支持CODE，另外还有TOKEN
            String responseType = oauthRequest.getParam(OAuth.OAUTH_RESPONSE_TYPE);

            if (responseType.equals(ResponseType.CODE.toString())) {
            	String username = (String) session.getAttribute("username");
            	Oauth2Code oauth2Code = oauth2CodeService.selectByUsernameClientId(username, clientId);
            	
                OAuthIssuerImpl oauthIssuerImpl = new OAuthIssuerImpl(new MD5Generator());
                authorizationCode = oauthIssuerImpl.authorizationCode();
                long timeStamp = new Date().getTime() + 600000L;//转成Unix时间戳，并加600秒
                
                if(oauth2Code == null) {
                    oauth2Code = new Oauth2Code();
                    oauth2Code.setClientId(clientId);
                    oauth2Code.setUsername(username);
                }
                oauth2Code.setAuthCode(authorizationCode);
                oauth2Code.setExpireIn(new Timestamp(timeStamp));
                oauth2CodeService.save(oauth2Code);
            }

            //进行OAuth响应构建
            OAuthASResponse.OAuthAuthorizationResponseBuilder builder =
                    OAuthASResponse.authorizationResponse(request, HttpServletResponse.SC_FOUND);
            //设置授权码
            builder.setCode(authorizationCode);
            
            //得到到客户端重定向地址
            String redirectURI = oauthRequest.getParam(OAuth.OAUTH_REDIRECT_URI);

            //构建响应
            final OAuthResponse oauthResponse = builder.location(redirectURI).buildQueryMessage();

        	response.setStatus(oauthResponse.getResponseStatus());
        	
        	/*if(client.getTrusted() != null && client.getTrusted().intValue() == 1) {
        		if(clientId.startsWith("mobile")) {
            		PrintWriter pw = response.getWriter();
                    pw.print(oauthResponse.getBody());
                    pw.flush();
                    pw.close();
            	} else {
            		//常规跳转，手机端不用这个
            		response.sendRedirect(oauthResponse.getLocationUri());
            	}
        	} else {
        		//应该跳转授权页，而手机端不用
        		request.getRequestDispatcher("/oauth2/approval").forward(request, response);
        		return null;
        	}*/
        	response.sendRedirect(oauthResponse.getLocationUri());
        } catch (OAuthProblemException e) {
            //出错处理
            String redirectUri = e.getRedirectUri();
            if (OAuthUtils.isEmpty(redirectUri)) {
            	String respString = Oauth2Utils.errMsgToJson(OAuthError.TokenResponse.INVALID_REQUEST, "missing parameter redirect_uri", "10102");
                
            	PrintWriter pw = response.getWriter();
            	response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                pw.print(respString);
                pw.flush();
                pw.close();
                
                return null;
            }

            //返回错误消息（如?error=）
            final OAuthResponse oauthResponse =
                    OAuthASResponse.errorResponse(HttpServletResponse.SC_FOUND)
                            .error(e).location(redirectUri).buildQueryMessage();
            
        	response.setStatus(oauthResponse.getResponseStatus());
        	
        	String clientId = request.getParameter("client_id");
        	if( clientId != null && clientId.startsWith("mobile")) {
        		PrintWriter pw = response.getWriter();
                pw.print(oauthResponse.getBody());
                pw.flush();
                pw.close();
        	} else {
        		//常规跳转，手机端不用这个
        		response.sendRedirect(oauthResponse.getLocationUri());
        	}
        }
		return null;
    }

    @RequestMapping("/oauth2/login")
    public ModelAndView login(HttpServletRequest request, HttpServletResponse response) throws Exception {
    	ModelAndView modelAndView = new ModelAndView();
    	modelAndView.setViewName("oauth2/login");

    	modelAndView.addObject("response_type", request.getParameter("response_type"));
    	modelAndView.addObject("client_id", request.getParameter("client_id"));
    	modelAndView.addObject("redirect_uri", request.getParameter("redirect_uri"));
    	modelAndView.addObject("scope", request.getParameter("scope"));
    	modelAndView.addObject("state", request.getParameter("state"));

    	String username = request.getParameter("username");
    	if(username != null && !"".equals(username)) {    		
    		FrontUser frontUser = frontUserService.selectByUsername(username);
    		if(null == frontUser) {
    			modelAndView.addObject("error", "帐号不存在");
    		} else {
    			String password = request.getParameter("password");
    			if(!password.equals(frontUser.getPassword())) {
    				modelAndView.addObject("error", "用户名或密码错误");
    			} else {
    				request.getSession().setAttribute("isLogin", "true");
    				request.getSession().setAttribute("username", username);
    				request.getRequestDispatcher("/oauth2/authorize").forward(request, response);
    				return null;
    			}
    		}
    	}

		return modelAndView;
    }
}