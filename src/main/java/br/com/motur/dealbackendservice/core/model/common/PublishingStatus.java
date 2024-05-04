package br.com.motur.dealbackendservice.core.model.common;

import lombok.Getter;

@Getter
public enum PublishingStatus {

    PUBLISHED("Publicado"),
    WAITING_FOR_APPROVAL("Aguardando Aprovação"),
    UNPUBLISHED("Não Publicado"),
    DELETED("Deletado"),;

    private final String displayName;

    PublishingStatus(String displayName) {
        this.displayName = displayName;
    }

}
