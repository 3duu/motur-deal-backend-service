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

    /*@Test*/
    /*@DisplayName("Teste de download de catálogo de dados")*/
    /*public void testDownloadCatalogData() {*/
    /*    ProviderEntity providerEntity = new ProviderEntity();*/
    /*    providerEntity.setName("Test Provider");*/
/**/
    /*    when(providerRepository.findAllAutoDownloadCatalog()).thenReturn(Collections.singletonList(providerEntity));*/
    /*    when(endpointConfigRepository.findByCategoryAndProvider(EndpointCategory.AUTHENTICATION, providerEntity)).thenReturn(Collections.emptyList());*/
/**/
    /*    catalogDownloadService.downloadCatalogData();*/
/**/
    /*    verify(providerRepository, times(1)).findAllAutoDownloadCatalog();*/
    /*    verify(endpointConfigRepository, times(3)).findByCategoryAndProvider(EndpointCategory.AUTHENTICATION, providerEntity);*/
    /*}*/

    @Test
    @DisplayName("Teste de download de catálogo de marcas")
    public void testDownloadBrandsCatalog() {
        // Arrange
        final ProviderEntity providerEntity = new ProviderEntity();
        providerEntity.setName("Test Provider");

        final EndpointConfigEntity endpointConfigEntity = new EndpointConfigEntity();
        endpointConfigEntity.setCategory(EndpointCategory.CATALOG_BRANDS);
        endpointConfigEntity.setResponseMapping(new ResponseMapping());
        endpointConfigEntity.getResponseMapping().setFieldMappings(new ArrayList<>());
        endpointConfigEntity.getResponseMapping().getFieldMappings().add(new ResponseMapping.Config());
        endpointConfigEntity.getResponseMapping().getFieldMappings().get(0).setDestination(ResponseMapping.FieldMapping.RETURNS);
        final List<EndpointConfigEntity> endpointConfigEntities = new ArrayList<>();
        endpointConfigEntities.add(endpointConfigEntity);

        final Map<Object, Object> mockResult = new HashMap<>();
        mockResult.put("brand1", "Brand 1");

        when(providerRepository.findAllAutoDownloadCatalog()).thenReturn(Collections.singletonList(providerEntity));
        when(endpointConfigRepository.findByCategoryAndProvider(EndpointCategory.CATALOG_BRANDS, providerEntity)).thenReturn(endpointConfigEntities);
        when(requestRestService.getAsMap(providerEntity, endpointConfigEntity, null)).thenReturn(mockResult);

        // Act
        catalogDownloadService.downloadBrandsCatalog(providerEntity, null);

        // Assert
        //verify(providerRepository, times(1)).findAllAutoDownloadCatalog();
        verify(endpointConfigRepository, times(1)).findByCategoryAndProvider(EndpointCategory.CATALOG_BRANDS, providerEntity);
        verify(requestRestService, times(1)).getAsMap(providerEntity, endpointConfigEntity, null);
    }

    @Test
    @DisplayName("Teste de download de catálogo de modelos")
    public void testDownloadModelsCatalog() {
        // Arrange
        ProviderEntity providerEntity = new ProviderEntity();
        providerEntity.setName("Test Provider");

        EndpointConfigEntity endpointConfigEntity = new EndpointConfigEntity();
        endpointConfigEntity.setCategory(EndpointCategory.CATALOG_MODELS);

        Map<Object, Object> mockResult = new HashMap<>();
        mockResult.put("model1", "Model 1");

        when(providerRepository.findAllAutoDownloadCatalog()).thenReturn(Collections.singletonList(providerEntity));
        when(endpointConfigRepository.findByCategoryAndProvider(EndpointCategory.CATALOG_MODELS, providerEntity)).thenReturn(Collections.singletonList(endpointConfigEntity));
        when(requestRestService.getAsMap(providerEntity, endpointConfigEntity, null)).thenReturn(mockResult);

        // Act
        catalogDownloadService.downloadModelsCatalog(providerEntity, null);

        // Assert
        verify(providerRepository, times(1)).findAllAutoDownloadCatalog();
        verify(endpointConfigRepository, times(1)).findByCategoryAndProvider(EndpointCategory.CATALOG_MODELS, providerEntity);
        verify(requestRestService, times(1)).getAsMap(providerEntity, endpointConfigEntity, null);
    }

    @Test
    @DisplayName("Teste de download de catálogo de versões")
    public void testDownloadTrimsCatalog() {
        // Arrange
        ProviderEntity providerEntity = new ProviderEntity();
        providerEntity.setName("Test Provider");

        EndpointConfigEntity endpointConfigEntity = new EndpointConfigEntity();
        endpointConfigEntity.setCategory(EndpointCategory.CATALOG_TRIMS);

        Map<Object, Object> mockResult = new HashMap<>();
        mockResult.put("trim1", "Trim 1");

        when(providerRepository.findAllAutoDownloadCatalog()).thenReturn(Collections.singletonList(providerEntity));
        when(endpointConfigRepository.findByCategoryAndProvider(EndpointCategory.CATALOG_TRIMS, providerEntity)).thenReturn(Collections.singletonList(endpointConfigEntity));
        when(requestRestService.getAsMap(providerEntity, endpointConfigEntity, null)).thenReturn(mockResult);

        // Act
        catalogDownloadService.downloadTrimsCatalog(providerEntity, null);

        // Assert
        verify(providerRepository, times(1)).findAllAutoDownloadCatalog();
        verify(endpointConfigRepository, times(1)).findByCategoryAndProvider(EndpointCategory.CATALOG_TRIMS, providerEntity);
        verify(requestRestService, times(1)).getAsMap(providerEntity, endpointConfigEntity, null);
    }
}
