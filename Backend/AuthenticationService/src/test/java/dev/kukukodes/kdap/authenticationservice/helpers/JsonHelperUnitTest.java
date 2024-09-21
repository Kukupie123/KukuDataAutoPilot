package dev.kukukodes.kdap.authenticationservice.helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.assertj.core.api.Assertions;

@ExtendWith(MockitoExtension.class)
class JsonHelperUnitTest {

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private JsonHelper jsonHelper;

    @DisplayName("Convert Simple TestObject to JSON String")
    @Test
    void convertObjectToJSON() throws JsonProcessingException {

        Mockito.when(
                        objectMapper.writeValueAsString(Mockito.any(TestObject.class))
                )
                .thenReturn(TestObject.expectedJson);

        String actualJson = jsonHelper.convertObjectsToJSON(new TestObject("test", 123));
        Assertions.assertThat(actualJson).isEqualTo(TestObject.expectedJson);
    }
    @DisplayName("Convert JSON String to TestObject")
    @Test
    void convertJSONToObject() throws JsonProcessingException {
        Mockito.when(
                        objectMapper.readValue(Mockito.anyString(), Mockito.<Class<TestObject>>any())
                )
                .thenReturn(new TestObject("test", 123));

        var obj = jsonHelper.convertJsonToObj(TestObject.expectedJson, TestObject.class);
        Assertions.assertThat(obj)
                .hasFieldOrPropertyWithValue("name", "test")
                .hasFieldOrPropertyWithValue("value", 123);

    }

    @Setter
    @Getter
    class TestObject {
        public static String expectedJson = "{\"name\":\"test\",\"value\":123}";

        private String name;
        private int value;

        public TestObject(String name, int value) {
            this.name = name;
            this.value = value;
        }
    }
}
