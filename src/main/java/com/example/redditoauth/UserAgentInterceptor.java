package com.example.redditoauth;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import java.io.IOException;
public class UserAgentInterceptor implements ClientHttpRequestInterceptor {


        @Override
        public ClientHttpResponse intercept(
                HttpRequest request,
                byte[] body,
                ClientHttpRequestExecution execution) throws IOException {

            // Reddit requires a descriptive User-Agent
            request.getHeaders().add("User-Agent", "MyRedditApp/1.0 by Quick-Object-3095");
            return execution.execute(request, body);
        }
    }
