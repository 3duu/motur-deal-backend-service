package br.com.motur.dealbackendservice.config.exception;

public enum DefaultErrorCode {

    VALIDACAO_REGRAS_NEGOCIO(1, "erro.inclusao.anuncio.regras.negocio"),
    ANUNCIO_COM_IMAGEM_INVALIDA(2, "erro.inclusao.anuncio.imagem.invalida"),
    ANUNCIO_COM_IMAGEM_FRAUDULENTA(3, "erro.inclusao.anuncio.imagem.fraudulenta"),
    ANUNCIO_FRAUDULENTO(4, "erro.inclusao.anuncio.fraudulento"),
    FALHA_INTERNA_PROCESSAMENTO_MS_ANUNCIO(5, "erro.inclusao.anuncio.msanuncio.erro.interno"),
    ERRO_REMOVER_ANUNCIO(6, "erro.remocao.anuncio.id.nao.informado"),
    ERRO_SISTEMA_MS_FRAUDE(7, "erro.inclusao.anuncio.msfraude.erro.interno"),
    ERRO_SISTEMA_MS_IMAGEM_ANUNCIO(8, "erro.inclusao.anuncio.msimagem.erro.interno"),
    ERRO_MONOLITO_GRAVAR_ANUNCIO_BANCO_DE_DADOS(9, "erro.inclusao.anuncio.monolito.gravar.anuncio.base.dados"),
    ERRO_MONOLITO_GRAVAR_ANUNCIO_EXCLUIDO_BANCO_DE_DADOS(10, "erro.inclusao.anuncio.monolito.gravar.anuncio.excluido.base.dados"),
    ERRO_WEBAPP_BUSCAR_CIDADE(11, "erro.inclusao.anuncio.mscatalogo.buscar.cidade"),
    ERRO_MONOLITO_ATUALIZAR_ANUNCIO_COMO_FRAUDE(12, "erro.inclusao.anuncio.monolito.atualizar.anuncio.com.fraude"),
    ERRO_MONOLITO_CRIAR_CLONES(13, "erro.inclusao.anuncio.monolito.criar.clones"),
    ERRO_MONOLITO_BUSCAR_ANUNCIO(14, "erro.inclusao.anuncio.monolito.buscar.anuncio"),
    ERRO_MONOLITO_REMOVER_ANUNCIO(15, "erro.inclusao.anuncio.monolito.remover.anuncio"),
    ERRO_MONOLITO_BUSCAR_JFIGSECTION_FIPE(16, "erro.inclusao.anuncio.monolito.busca.jfig.fipe"),
    ERRO_MONOLITO_BUSCAR_JFIGSECTION_APPLICATION(17, "erro.inclusao.anuncio.monolito.busca.jfig.application"),
    ERRO_WEBAPP_ATUALIZAR_CACHE(18, "erro.inclusao.anuncio.monolito.atualizar.cache"),
    ERRO_MONOLITO_BUSCAR_PLANOS(19, "erro.inclusao.anuncio.monolito.buscar.plano"),
    ERRO_SISTEMA_MS_CATALOGO(20, "erro.inclusao.anuncio.mscatalogo.buscar.catalogo"),
    ERRO_SISTEMA_CRM_DISPATCHER(21, "erro.inclusao.anuncio.mscrmdispatcher.erro.interno"),
    ERRO_SISTEMA_MS_ANUNCIANTE(22, "erro.inclusao.anuncio.msanunciante.erro.interno"),
    ERRO_INCLUSAO_ANUNCIO_ANUNCIANTE_NAO_ENCONTRADO(22, "erro.inclusao.anuncio.anunciante.nao.encontrado"),
    ERRO_INCLUSAO_ANUNCIO_VERSAO_NAO_ENCONTRADA(22, "erro.inclusao.anuncio.versao.nao.encontrada"),
    ERRO_OBTER_TOKEN_ACESSO_MONOLITO(23, "erro.inclusao.anuncio.msauth.buscar.token"),
    ERRO_GERAL_NAO_TRATADO(24, "erro.inclusao.anuncio.erro.geral.nao.tratado"),
    ERRO_SISTEMA_MS_AUTH(25, "erro.inclusao.anuncio.msauth.erro.interno"),
    ERRO_MONOLITO_REMOVER_CLONES(26, "erro.inclusao.anuncio.monolito.remover.clones"),
    ERRO_WEBAPP_REMOVER_CLONES_CACHE(26, "erro.inclusao.anuncio.monolito.remover.clones.cache"),
    MS_IMAGEM_ANUNCIO_BUCKETS3_INVALIDO(27, "bucket.invalido"),
    ERRO_ATUALIZAR_ANUNCIO(28, "erro.atualizar.anuncio.monolito.inexistente"),
    ERRO_PLANO_NAO_PREENCHIDO(29, "erro.inclusao.anuncio.plano.vazio"),
    ERRO_ANUNCIO_JA_CADASTRADO_UUID(30, "erro.anuncio.ja.cadastrado.uuid"),
    ERRO_SISTEMA_MS_INTEGRADOR_GATEWAY(31, "erro.inclusao.anuncio.mscatalogo.buscar.integrador-gateway"),
    ERRO_HEADER_INCONSISTENTE_ANUNCIANTE(32, "erro.inconsistencia.header.anunciante"),
    ERRO_HEADER_INCONSISTENTE_CALLER(33, "erro.inconsistencia.header.caller"),
    ERRO_HEADER_INCONSISTENTE_VERSAO(34, "erro.inconsistencia.header.versao"),
    ERRO_SALVAR_IMAGENS_ANUNCIO(35, "erro.inclusao.anuncio.imagem.salvar"),
    ERRO_ANUNCIO_SEM_IMAGEM(36, "erro.inclusao.anuncio.sem.imagem"),
    ERRO_INTEGRACAO_MS(37, "erro.integracao.interno"),
    ERRO_SISTEMA_MS_CATALOGO_CORES(38, "erro.integracao.mscatalogo.buscar.catalogo.cores.inexistentes"),
    ERRO_CHATBOT_BUSCAR_INFORMACOES_ANUNCIO(39, "erro.chatbot.busca.informacoes.anuncio"),
    HEADERS_CORRELATION_NAO_INFORMADOS(40, "headers.correlation.nao.informado"),
    ERRO_CHATBOT_ANUNCIO_NAO_ENCONTRADO(41, "erro.chatbot.anuncio.nao.encontrado"),
    ERRO_REMOVER_ANUNCIO_CACHE_MONOLITO(42, "erro.remocao.anuncio.monolito.cache"),
    ERRO_INCONSISTENTE_ANUNCIO(43, "erro.inclusao.anuncio.inconsistente");

    public Integer getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

    private final int code;
    private final String message;

    DefaultErrorCode(int code, String msg) {
        this.code = code;
        this.message = msg;
    }

}
