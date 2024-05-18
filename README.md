# Motur Deal Backend Service

Motur Deal Backend Service é uma API RESTful desenvolvida em Java 21 e Spring Boot 3.2.1 para a publicação de anúncios de carros em múltiplos marketplaces no Brasil.

## Objetivo

O objetivo deste serviço é integrar com diversos marketplaces de carros, permitindo a configuração dos integradores via banco de dados, sem a necessidade de códigos específicos para cada um. Isso proporciona flexibilidade e facilidade de manutenção.

## Principais Funcionalidades

- **Integração com Marketplaces**: Suporte para integração com OLX, Mercado Livre e WebMotors.
- **Configuração via Banco de Dados**: Todas as configurações dos integradores são gerenciadas através do banco de dados.
- **Serviço Genérico**: Utilização de uma camada de serviço genérica para interpretação e interação com as APIs dos integradores.

## Estrutura de Entidades

As principais entidades utilizadas no sistema são:

- `ProviderEntity`: Representa um fornecedor de anúncios.
- `EndpointConfigEntity`: Configurações de endpoint do fornecedor.
- `BrandEntity`: Marcas de veículos no catálogo local.
- `ProviderBrandsEntity`: Marcas no catálogo do fornecedor.
- `ModelEntity`: Modelos de veículos no fornecedor.
- `ProviderModelsEntity`: Modelos no fornecedor.
- `TrimEntity`: Versões de veículos.
- `ProviderTrimsEntity`: Versões no fornecedor.

## Classe de Serviço

A classe `CatalogDownloadService` é responsável por realizar o download dos dados do catálogo dos fornecedores.

## Integrações

### OLX
- [OLX API](https://developers.olx.com.br/anuncio/api/home.html)

### Mercado Livre
- [Mercado Livre API](https://developers.mercadolivre.com.br/pt_br/publicacao-de-automoveis)

### WebMotors
- [WebMotors SOAP API](https://integracao.webmotors.com.br/manualintegracao/index.html)

## Como Executar

Para executar o projeto, siga os passos abaixo:

1. Clone o repositório:
   ```bash
   git clone https://github.com/3duu/motur-deal-backend-service.git
