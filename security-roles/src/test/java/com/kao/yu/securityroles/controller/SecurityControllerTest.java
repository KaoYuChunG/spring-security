package com.kao.yu.securityroles.controller;

import com.kao.yu.securityroles.service.UsersDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityControllerTest {


    @Mock
    private UsersDetailsService usersDetailsService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void userEndpoint_ShouldAllowUserRole() throws Exception {
        MvcResult result = mockMvc.perform(formLogin()
                        .user("user")
                        .password("user123"))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        MockHttpSession session = (MockHttpSession) result.getRequest().getSession(false);

        // 3. 帶著 session 再請求受保護的 API
        mockMvc.perform(get("/api/user").session(session))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello, user!"));
    }

    @Test
    void shouldAllowPublicAccess() throws Exception {
        mockMvc.perform(get("/api/public"))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello, public!"));

        mockMvc.perform(get("/api/user"))
                .andExpect(status().isFound());
    }

    @Test
    void shouldAllowAccessToAdminWithAdminRole() throws Exception {
        MvcResult result = mockMvc.perform(formLogin().user("admin").password("admin123"))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        MockHttpSession session = (MockHttpSession) result.getRequest().getSession(false);

        mockMvc.perform(get("/api/admin").session(session))
                .andExpect(status().isOk())
                .andExpect(content().string("Hello, admin!"));
    }

    @Test
    void shouldRejectUserAccessingAdminEndpoint() throws Exception {
        MvcResult result = mockMvc.perform(formLogin().user("user").password("user123"))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        MockHttpSession session = (MockHttpSession) result.getRequest().getSession(false);

        mockMvc.perform(get("/api/admin").session(session))
                .andExpect(status().isForbidden());
    }
}
