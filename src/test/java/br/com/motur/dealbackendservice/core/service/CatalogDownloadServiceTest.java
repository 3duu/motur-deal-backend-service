package br.com.motur.dealbackendservice.core.service;

import br.com.motur.dealbackendservice.common.ResponseProcessor;
import br.com.motur.dealbackendservice.core.model.common.EndpointCategory;
import br.com.motur.dealbackendservice.core.dataproviders.repository.*;
import br.com.motur.dealbackendservice.core.model.*;
import br.com.motur.dealbackendservice.core.model.common.ResponseMapping;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@DisplayName("Testes de unidade para o serviço de download de catálogos")
public class CatalogDownloadServiceTest {
    
    @Mock
    private ProviderRepository providerRepository;

    @Mock
    private EndpointConfigRepository endpointConfigRepository;

    @Mock
    private RequestRestService requestRestService;

    @Mock
    private RequestSoapService requestSoapService;

    @Mock
    private ProviderBrandsRepository providerBrandsRepository;

    @Mock
    private ProviderModelsRepository providerModelsRepository;

    @Mock
    private ProviderTrimsRepository providerTrimsRepository;

    @Mock
    private BrandRepository brandRepository;

    @Mock
    private ModelRepository modelRepository;

    @Mock
    private TrimRepository trimRepository;

    @Mock
    private ResponseProcessor responseProcessor;

    @Spy
    private ObjectMapper objectMapper;

    @InjectMocks
    private CatalogDownloadService catalogDownloadService;

    @BeforeEach
    public void setup() {
        //MockitoAnnotations.openMocks(this);
    }

}
