package com.kao.yu.singlesessionauthredis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kao.yu.singlesessionauthredis.dto.AuthRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SingleSessionAuthRedisApplicationTests {

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private StringRedisTemplate redisTemplate;

	private static final String USERNAME = "admin";
	private static final String PASSWORD = "admin123";

	@Test
	void shouldEnforceSingleSessionPerUser() throws Exception {
		// Step 1 - 登入獲得 Token1
		String token1 = performLogin(USERNAME, PASSWORD);

		// Step 2 - 使用 Token1 呼叫保護 API，應該成功
		mockMvc.perform(get("/api/admin")
						.header("Authorization", "Bearer " + token1))
				.andExpect(status().isOk());

		// Step 3 - 模擬另一台裝置登入獲得 Token2
		String token2 = performLogin(USERNAME, PASSWORD);

		// 確認 Redis 中儲存的是 Token2，不是 Token1
		String redisToken = redisTemplate.opsForValue().get("login:" + USERNAME);
		assertThat(redisToken).isEqualTo(token2);

		// Step 4 - 使用 Token1 呼叫保護 API，應該失敗（因為 Redis 被覆蓋）
		mockMvc.perform(get("/api/admin")
						.header("Authorization", "Bearer " + token1))
				.andExpect(status().isUnauthorized());

		// Token2 呼叫應該成功
		mockMvc.perform(get("/api/admin")
						.header("Authorization", "Bearer " + token2))
				.andExpect(status().isOk());
	}

	private String performLogin(String username, String password) throws Exception {
		AuthRequest request = new AuthRequest(username, password);

		String response = mockMvc.perform(post("/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		return objectMapper.readTree(response).get("token").asText();
	}

}
