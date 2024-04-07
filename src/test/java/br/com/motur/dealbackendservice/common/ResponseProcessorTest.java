package br.com.motur.dealbackendservice.common;

import br.com.motur.dealbackendservice.core.model.common.DataType;
import br.com.motur.dealbackendservice.core.model.common.ResponseMapping;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.slf4j.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ResponseProcessorTest {

    @InjectMocks
    private ResponseProcessor responseProcessor;

    @Spy
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Testa se o método processAsHashMap retorna um mapa vazio quando o JSON de entrada é uma string vazia")
    void processAsHashMapReturnsCorrectMap() {
        Object jsonResponse = new HashMap<>();
        ResponseMapping.Config config = new ResponseMapping.Config();
        config.setOriginDatatype(DataType.STRING);
        config.setDestination(ResponseMapping.FieldMapping.NAME);
        EnumMap<ResponseMapping.FieldMapping, Object> expectedResult = new EnumMap<>(ResponseMapping.FieldMapping.class);
        expectedResult.put(ResponseMapping.FieldMapping.NAME, "{}");

        Map<ResponseMapping.FieldMapping, Object> result = responseProcessor.processAsHashMap(jsonResponse, config);

        assertEquals(expectedResult, result);
    }

    @Test
    void getMappingValuesReturnsCorrectMap() {
        Object jsonResponse = new HashMap<>();
        List<ResponseMapping.Config> configs = List.of(new ResponseMapping.Config());
        Map<ResponseMapping.FieldMapping, Object> expectedResult = new HashMap<>();

        Map<ResponseMapping.FieldMapping, Object> result = responseProcessor.getMappingValues(jsonResponse, configs);

        assertEquals(expectedResult, result);
    }

    @Test
    void getValueFromNestedMapReturnsCorrectValue() {
        ResponseMapping mapping = new ResponseMapping();
        Map<Object, Object> origin = new HashMap<>();
        Logger logger = mock(Logger.class);
        Object expectedResult = new HashMap<>();

        Object result = responseProcessor.getValueFromNestedMap(mapping, origin, logger);

        assertEquals(expectedResult, result);
    }

    @Test
    void getStringFieldFromNestedMapReturnsCorrectString() {
        ResponseMapping.FieldMapping fieldMapping = ResponseMapping.FieldMapping.NAME;
        ResponseMapping mapping = new ResponseMapping();
        Map<Object, Object> origin = new HashMap<>();
        Logger logger = mock(Logger.class);
        String expectedResult = "";

        String result = responseProcessor.getStringFieldFromNestedMap(fieldMapping, mapping, origin, logger);

        assertEquals(expectedResult, result);
    }
}