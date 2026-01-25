package com.khi.onboardingservice.security.filter;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class SecurityGatewayFilterTest {

    @Test
    @DisplayName("특정 요청 메서드 타입만 필터링 하는지 여부 확인")
    void shouldNotFilterTest() throws Exception {

        SecurityContextGatewayFilter filter = new SecurityContextGatewayFilter();

        MockHttpServletRequest r1 = new MockHttpServletRequest("POST", "/onboarding/enroll");
        MockHttpServletRequest r2 = new MockHttpServletRequest("GET", "/onboarding/enroll");
        MockHttpServletRequest r3 = new MockHttpServletRequest("POST", "/onboarding/etc");

        assertThat(filter.shouldNotFilter(r1)).isFalse();
        assertThat(filter.shouldNotFilter(r2)).isTrue();
        assertThat(filter.shouldNotFilter(r3)).isTrue();
    }

    @Test
    @DisplayName("정상 동작 여부 확인")
    void doFilterTest() throws Exception {

        SecurityContextGatewayFilter filter = new SecurityContextGatewayFilter();

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/onboarding/enroll");

        request.addHeader("Uid", "0");
        request.addHeader("Role", "ROLE_USER");

        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        // SecurityGatewayFilter 내에서 filterChain.doFilter(request, response)가 호출 됐는지 검증
        verify(chain).doFilter(request, response);
    }
}
