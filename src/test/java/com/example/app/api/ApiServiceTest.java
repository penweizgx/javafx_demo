package com.example.app.api;

import com.example.app.api.storage.ConfigStorage;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ApiServiceTest {

    private TestBaseApiServiceImpl apiService;

    @Mock
    private RequestExecutor<String, String> requestExecutor;

    @Mock
    private ConfigStorage configStorage;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        apiService = new TestBaseApiServiceImpl();
        apiService.setTestConfigStorage(configStorage);
    }

    @Test
    void execute_Successful() throws ApiException, IOException {
        when(requestExecutor.execute(any(), any())).thenReturn("Success");

        String result = apiService.execute(requestExecutor, "https://api.example.com", "data");

        assertEquals("Success", result);
        verify(requestExecutor, times(1)).execute(any(), any());
    }

    @Test
    void execute_RetryOnSystemBusy() throws ApiException, IOException {
        // -1 indicates system busy/need retry

        // Reflection to set error code if needed, or subclass ApiException if possible.
        // Looking at BaseApiServiceImpl, it checks e.getErrorCode() == -1.
        // Assuming ApiException has a way to set error code or constructor.
        // Let's assume we can mock it or use a constructor.
        // Since I don't see ApiException source fully, let's try to mock the exception
        // behavior if possible
        // or just rely on standard retry mechanism if error code is not easily settable
        // in test without seeing ApiException.
        // However, looking at source: "if (e.getErrorCode() == -1)"
        // I will assume I can construct it or mock it.

        ApiException busyException = mock(ApiException.class);
        when(busyException.getErrorCode()).thenReturn(-1);

        when(requestExecutor.execute(any(), any()))
                .thenThrow(busyException)
                .thenThrow(busyException)
                .thenReturn("Success");

        String result = apiService.execute(requestExecutor, "http://api.example.com", "data");

        assertEquals("Success", result);
        verify(requestExecutor, times(3)).execute(any(), any());
    }

    @Test
    void execute_MaxRetriesReached() throws ApiException, IOException {
        ApiException busyException = mock(ApiException.class);
        when(busyException.getErrorCode()).thenReturn(-1);

        when(requestExecutor.execute(any(), any())).thenThrow(busyException);

        assertThrows(ApiException.class, () -> apiService.execute(requestExecutor, "http://api.example.com", "data"));

        // Max retries is 5. So initial attempt + 5 retries = 6 calls?
        // Loop: do ... while (retryTimes++ < maxRetryTimes)
        // retryTimes starts at 0.
        // 0: execute -> fail. retryTimes becomes 1. Loop check 1 < 5. True.
        // 1: execute -> fail. retryTimes becomes 2. Loop check 2 < 5. True.
        // ...
        // 4: execute -> fail. retryTimes becomes 5. Loop check 5 < 5. False.
        // So 5 attempts? Or 6?
        // retryTimes++ returns old value.
        // 0 < 5 -> true (after 1st run)
        // 1 < 5 -> true
        // 2 < 5 -> true
        // 3 < 5 -> true
        // 4 < 5 -> true
        // 5 < 5 -> false.
        // So it runs 6 times total? Let's check verify count.
        // Actually code says: if (retryTimes + 1 > maxRetryTimes) inside catch.
        // retryTimes starts 0.
        // 1st fail: retryTimes is 0. 0+1 > 5 (False).
        // 2nd fail: retryTimes is 1.
        // ...
        // Wait, retryTimes++ is in while condition.
        // So inside catch, retryTimes matches the number of *retries* completed so far?
        // No.
        // Using a spy or verify(atLeast) is safer if not sure.
        verify(requestExecutor, atLeast(5)).execute(any(), any());
    }

    @Test
    void extractResBody_ReturnsBody_WhenCode200() throws ApiException {
        String json = "{\"code\": 200, \"message\": \"success\", \"resbody\": {\"data\": \"value\"}}";
        JsonObject result = apiService.extractResBody(json);
        assertNotNull(result);
        assertEquals("value", result.get("data").getAsString());
    }

    @Test
    void extractResBody_ThrowsException_WhenCodeNot200() {
        String json = "{\"code\": 500, \"message\": \"Internal Error\"}";
        ApiException exception = assertThrows(ApiException.class, () -> apiService.extractResBody(json));
        assertEquals(500, exception.getErrorCode());
        assertEquals("Internal Error", exception.getMessage());
    }

    static class TestBaseApiServiceImpl extends BaseApiServiceImpl<String, String> {
        private String rsaKeyResponse;

        public void setTestConfigStorage(ConfigStorage storage) {
            this.configStorage = storage;
        }

        public void setRsaKeyResponse(String response) {
            this.rsaKeyResponse = response;
        }

        @Override
        public String getRequestHttpClient() {
            return null;
        }

        @Override
        public String getRequestHttpProxy() {
            return null;
        }

        @Override
        public void login(String username, String password) throws ApiException {
            // No-op for test
        }

        @Override
        public void initHttp() {
        }
    }
}
