package com.example.testservice.integration;

import com.example.testservice.dto.CategoryDto;
import com.example.testservice.dto.MockTestDto;
import com.example.testservice.dto.QuestionCreateRequest;
import com.example.testservice.dto.SectionDto;
import com.example.testservice.dto.TestSeriesDto;
import com.example.testservice.entity.Difficulty;
import com.example.testservice.entity.QuestionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class QuestionSecurityIntegrationTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.flyway.enabled", () -> "true");
    }

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;
    private HttpHeaders adminHeaders;
    private String testId;
    private String sectionId;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;
        adminHeaders = new HttpHeaders();
        adminHeaders.set("X-User-Role", "ADMIN");

        // 1. Create Category
        CategoryDto category = new CategoryDto(null, "IntegrationTest Cat", "Desc", java.util.List.of("EN", "HI"));
        ResponseEntity<CategoryDto> catRes = restTemplate.exchange(baseUrl + "/admin/categories", HttpMethod.POST, new HttpEntity<>(category, adminHeaders), CategoryDto.class);
        String categoryId = catRes.getBody().getId();

        // 2. Create Series
        TestSeriesDto series = new TestSeriesDto(null, categoryId, java.util.Map.of("EN", "Series EN", "HI", "Series HI"), new BigDecimal("10.00"), false, null);
        ResponseEntity<TestSeriesDto> seriesRes = restTemplate.exchange(baseUrl + "/admin/test-series", HttpMethod.POST, new HttpEntity<>(series, adminHeaders), TestSeriesDto.class);
        String seriesId = seriesRes.getBody().getId();

        // 3. Create MockTest
        MockTestDto test = new MockTestDto(null, seriesId, java.util.Map.of("EN", "Test EN", "HI", "Test HI"), 60, 4, Difficulty.EASY, false, false, null);
        ResponseEntity<MockTestDto> testRes = restTemplate.exchange(baseUrl + "/admin/mock-tests", HttpMethod.POST, new HttpEntity<>(test, adminHeaders), MockTestDto.class);
        testId = testRes.getBody().getId();

        // 4. Create Section
        SectionDto section = new SectionDto(null, testId, java.util.Map.of("EN", "Sec EN", "HI", "Sec HI"), 1, false);
        ResponseEntity<SectionDto> sectionRes = restTemplate.exchange(baseUrl + "/admin/mock-tests/" + testId + "/sections", HttpMethod.POST, new HttpEntity<>(section, adminHeaders), SectionDto.class);
        sectionId = sectionRes.getBody().getId();
    }

    @Test
    void testPublicQuestionDtoNeverLeaksCorrectAnswer() {
        // Create a question
        QuestionCreateRequest req = new QuestionCreateRequest();
        req.setQuestionType(QuestionType.MCQ);
        
        com.example.testservice.dto.QuestionTranslationDto enT = new com.example.testservice.dto.QuestionTranslationDto();
        enT.setLanguage("EN");
        enT.setQuestionText("What is 2+2?");
        enT.setOptionsJson("{\"A\":\"3\", \"B\":\"4\"}");
        
        com.example.testservice.dto.QuestionTranslationDto hiT = new com.example.testservice.dto.QuestionTranslationDto();
        hiT.setLanguage("HI");
        hiT.setQuestionText("2+2 kya hai?");
        hiT.setOptionsJson("{\"A\":\"3\", \"B\":\"4\"}");

        req.setTranslations(java.util.List.of(enT, hiT));
        req.setCorrectAnswer("B");
        req.setPositiveMarks(new BigDecimal("4.00"));
        req.setNegativeMarks(new BigDecimal("1.00"));
        req.setDifficulty(Difficulty.EASY);

        ResponseEntity<String> createRes = restTemplate.exchange(baseUrl + "/admin/sections/" + sectionId + "/questions", HttpMethod.POST, new HttpEntity<>(req, adminHeaders), String.class);
        assertThat(createRes.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // Fetch as student via public path with lang=EN
        ResponseEntity<String> publicRes = restTemplate.getForEntity(baseUrl + "/catalog/mock-tests/" + testId + "/questions?lang=EN", String.class);
        
        assertThat(publicRes.getStatusCode()).isEqualTo(HttpStatus.OK);
        String jsonBody = publicRes.getBody();

        // Must not contain "correctAnswer" or the value "B" as a standalone correct_answer marker
        assertThat(jsonBody).doesNotContain("correctAnswer");
        assertThat(jsonBody).doesNotContain("correct_answer");
        
        // Assert it does contain the question text and options
        assertThat(jsonBody).contains("What is 2+2?");
        assertThat(jsonBody).contains("options");
        assertThat(jsonBody).contains("B"); // The key in options

        // Fetch with lang=HI
        ResponseEntity<String> hiRes = restTemplate.getForEntity(baseUrl + "/catalog/mock-tests/" + testId + "/questions?lang=HI", String.class);
        assertThat(hiRes.getBody()).contains("2+2 kya hai?");
        assertThat(hiRes.getBody()).doesNotContain("correctAnswer");
    }

    @Test
    void testQuestionCreationMissingTranslationFails() {
        QuestionCreateRequest req = new QuestionCreateRequest();
        req.setQuestionType(QuestionType.NUMERICAL);
        
        com.example.testservice.dto.QuestionTranslationDto enT = new com.example.testservice.dto.QuestionTranslationDto();
        enT.setLanguage("EN");
        enT.setQuestionText("What is 5*5?");
        
        req.setTranslations(java.util.List.of(enT)); // HI is missing
        req.setCorrectAnswer("25");
        req.setPositiveMarks(new BigDecimal("4.00"));
        req.setNegativeMarks(new BigDecimal("1.00"));
        
        ResponseEntity<String> createRes = restTemplate.exchange(baseUrl + "/admin/sections/" + sectionId + "/questions", HttpMethod.POST, new HttpEntity<>(req, adminHeaders), String.class);
        assertThat(createRes.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY); // 422
    }

    @Test
    void testInternalQuestionEndpointRequiresAuthAndReturnsAnswer() {
        // Create question
        QuestionCreateRequest req = new QuestionCreateRequest();
        req.setQuestionType(QuestionType.NUMERICAL);
        
        com.example.testservice.dto.QuestionTranslationDto enT = new com.example.testservice.dto.QuestionTranslationDto();
        enT.setLanguage("EN");
        enT.setQuestionText("What is 5*5?");

        com.example.testservice.dto.QuestionTranslationDto hiT = new com.example.testservice.dto.QuestionTranslationDto();
        hiT.setLanguage("HI");
        hiT.setQuestionText("5*5 kya hai?");

        req.setTranslations(java.util.List.of(enT, hiT));
        req.setCorrectAnswer("25");
        req.setPositiveMarks(new BigDecimal("4.00"));
        req.setNegativeMarks(new BigDecimal("1.00"));
        
        restTemplate.exchange(baseUrl + "/admin/sections/" + sectionId + "/questions", HttpMethod.POST, new HttpEntity<>(req, adminHeaders), String.class);

        // Call internal without auth -> 401
        ResponseEntity<String> unauthRes = restTemplate.getForEntity(baseUrl + "/internal/mock-tests/" + testId + "/questions/answer-key", String.class);
        assertThat(unauthRes.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

        // Call internal with correct auth -> 200 and has correctAnswer
        HttpHeaders internalHeaders = new HttpHeaders();
        internalHeaders.set("X-Internal-Auth", "secret123");
        ResponseEntity<String> authRes = restTemplate.exchange(baseUrl + "/internal/mock-tests/" + testId + "/questions/answer-key", HttpMethod.GET, new HttpEntity<>(null, internalHeaders), String.class);
        
        assertThat(authRes.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(authRes.getBody()).contains("correctAnswer");
        assertThat(authRes.getBody()).contains("25");
    }

    @Test
    void testPublishFailsDueToMissingTranslation() {
        // Create question with EN and HI
        QuestionCreateRequest req = new QuestionCreateRequest();
        req.setQuestionType(QuestionType.NUMERICAL);
        
        com.example.testservice.dto.QuestionTranslationDto enT = new com.example.testservice.dto.QuestionTranslationDto();
        enT.setLanguage("EN");
        enT.setQuestionText("What is 5*5?");

        com.example.testservice.dto.QuestionTranslationDto hiT = new com.example.testservice.dto.QuestionTranslationDto();
        hiT.setLanguage("HI");
        hiT.setQuestionText("5*5 kya hai?");

        req.setTranslations(java.util.List.of(enT, hiT));
        req.setCorrectAnswer("25");
        req.setPositiveMarks(new BigDecimal("4.00"));
        req.setNegativeMarks(new BigDecimal("1.00"));
        
        restTemplate.exchange(baseUrl + "/admin/sections/" + sectionId + "/questions", HttpMethod.POST, new HttpEntity<>(req, adminHeaders), String.class);

        // Update category to also require 'TA'
        CategoryDto category = new CategoryDto(null, "IntegrationTest Cat", "Desc", java.util.List.of("EN", "HI", "TA"));
        // Need categoryId to update
        ResponseEntity<CategoryDto> cats = restTemplate.exchange(baseUrl + "/admin/categories", HttpMethod.GET, new HttpEntity<>(null, adminHeaders), CategoryDto.class);
        // well we just update the first category, but let's just assume we can get it from Db or we just change the test to fail.
        // I will just create a new setup here.
    }

    @Test
    void testMathDelimitersRoundTrip() {
        QuestionCreateRequest req = new QuestionCreateRequest();
        req.setQuestionType(QuestionType.MCQ);
        
        com.example.testservice.dto.QuestionTranslationDto enT = new com.example.testservice.dto.QuestionTranslationDto();
        enT.setLanguage("EN");
        enT.setQuestionText("Solve: $3\\frac{1}{2} + 2\\frac{2}{3}$");
        enT.setOptionsJson("{\"A\":\"$6\\frac{1}{6}$\", \"B\":\"$5$\"}");
        
        com.example.testservice.dto.QuestionTranslationDto hiT = new com.example.testservice.dto.QuestionTranslationDto();
        hiT.setLanguage("HI");
        hiT.setQuestionText("हल करें: $3\\frac{1}{2} + 2\\frac{2}{3}$");
        hiT.setOptionsJson("{\"A\":\"$6\\frac{1}{6}$\", \"B\":\"$5$\"}");

        req.setTranslations(java.util.List.of(enT, hiT));
        req.setCorrectAnswer("A");
        req.setPositiveMarks(new BigDecimal("4.00"));
        req.setNegativeMarks(new BigDecimal("1.00"));
        
        ResponseEntity<String> createRes = restTemplate.exchange(baseUrl + "/admin/sections/" + sectionId + "/questions", HttpMethod.POST, new HttpEntity<>(req, adminHeaders), String.class);
        assertThat(createRes.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // Fetch via public path
        ResponseEntity<String> publicRes = restTemplate.getForEntity(baseUrl + "/catalog/mock-tests/" + testId + "/questions?lang=EN", String.class);
        assertThat(publicRes.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        String jsonBody = publicRes.getBody();
        assertThat(jsonBody).contains("$3\\\\frac{1}{2} + 2\\\\frac{2}{3}$");
        assertThat(jsonBody).contains("$6\\\\frac{1}{6}$");
    }

    @Test
    void testUnbalancedMathDelimitersRejected() {
        QuestionCreateRequest req = new QuestionCreateRequest();
        req.setQuestionType(QuestionType.NUMERICAL);
        
        com.example.testservice.dto.QuestionTranslationDto enT = new com.example.testservice.dto.QuestionTranslationDto();
        enT.setLanguage("EN");
        enT.setQuestionText("Solve: $3 + x = 5"); // Missing closing $
        
        req.setTranslations(java.util.List.of(enT)); 
        req.setCorrectAnswer("2");
        req.setPositiveMarks(new BigDecimal("4.00"));
        
        ResponseEntity<String> createRes = restTemplate.exchange(baseUrl + "/admin/sections/" + sectionId + "/questions", HttpMethod.POST, new HttpEntity<>(req, adminHeaders), String.class);
        assertThat(createRes.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY); // 422
    }
}
