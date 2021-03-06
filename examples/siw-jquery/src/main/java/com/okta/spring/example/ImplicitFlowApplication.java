/*
 * Copyright 2017 Okta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.okta.spring.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.expression.OAuth2MethodSecurityExpressionHandler;

@EnableResourceServer
@SpringBootApplication
public class ImplicitFlowApplication {

    /**
     * Enable OAuth claim checking from @PreAuthorize annotation.
     * @see com.okta.spring.example.controllers.WelcomeController
     */
    @EnableGlobalMethodSecurity(prePostEnabled = true)
    protected static class GlobalSecurityConfiguration extends GlobalMethodSecurityConfiguration {
        @Override
        protected MethodSecurityExpressionHandler createExpressionHandler() {
            return new OAuth2MethodSecurityExpressionHandler();
        }
    }

    @Bean
    protected ResourceServerConfigurerAdapter securityConfigurer(ResourceServerProperties rsp) {
        return new ResourceSecurityConfigurer(rsp);
    }


    public static void main(String[] args) {
        SpringApplication.run(ImplicitFlowApplication.class, args);
    }


    private static class ResourceSecurityConfigurer
            extends ResourceServerConfigurerAdapter {

        private ResourceServerProperties resource;

        ResourceSecurityConfigurer(ResourceServerProperties resource) {
            this.resource = resource;
        }

        @Override
        public void configure(ResourceServerSecurityConfigurer resources)
                throws Exception {
            resources.resourceId(this.resource.getResourceId());
        }

        @Override
        public void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests()
                    .antMatchers("/", "/index.html", "/sign-in-widget-config").permitAll()
                    .anyRequest().authenticated();
        }
    }
}
