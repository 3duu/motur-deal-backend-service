package br.com.motur.dealbackendservice.core.finder;

import br.com.motur.dealbackendservice.core.model.common.TransmissionType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class TransmissionFinder {

    private static class TransmissionKeyword {
        private final TransmissionType type;
        private final List<String> keywords;

        public TransmissionKeyword(TransmissionType type, String... keywords) {
            this.type = type;
            this.keywords = Arrays.asList(keywords);
        }

        public boolean matches(String tipo) {
            return keywords.stream().anyMatch(tipo::contains);
        }
    }

    private static final List<TransmissionKeyword> TRANSMISSION_KEYWORDS = Arrays.asList(
            new TransmissionKeyword(TransmissionType.MANUAL, "manual", "stick shift", "standard", "normal", "padrão", "m/t", "manual"),
            new TransmissionKeyword(TransmissionType.SEMI_AUTOMATIC, "seqüencial", "sequencial", "sequential", "semi-auto", "semi-automatic", "semi automatic"),
            new TransmissionKeyword(TransmissionType.DIRECT_SHIFT, "dualogic", "dsg", "direct shift", "paddle shift", "direct shift"),
            new TransmissionKeyword(TransmissionType.TIPTRONIC, "tiptronic", "manumatic", "sport mode", "sportmatic", "steptronic", "shiftmatic", "shifttronic", "sportronic", "touchshift", "sportshift", "paddle shift", "direct shift"),
            new TransmissionKeyword(TransmissionType.AUTOMATIC, "automático", "automatico", "auto", "slushbox", "at", "a/t", "automatic", "tronic"),
            new TransmissionKeyword(TransmissionType.CVT, "cvt", "continuously variable", "varimatic", "multitronic"),
            new TransmissionKeyword(TransmissionType.DUAL_CLUTCH, "dupla embreagem", "dual clutch", "twin clutch", "dct", "powershift", "dual clutch"),
            new TransmissionKeyword(TransmissionType.ELECTRIC_VARIABLE, "eletrico", "elétrico", "electric", "ev transmission", "electric variable", "ev"),
            new TransmissionKeyword(TransmissionType.HYBRID, "híbrido", "hibrido", "hybrid", "eco", "regenerative", "hybrid")
    );

    public TransmissionType fromString(final String tipo) {

        if (StringUtils.isEmpty(tipo)) {
            return null;
        }

        final String lowerCaseTipo = tipo.toLowerCase();

        return TRANSMISSION_KEYWORDS.stream()
                .filter(keyword -> keyword.matches(lowerCaseTipo))
                .map(keyword -> keyword.type)
                .findFirst()
                .orElse(TransmissionType.NONE);
    }
}
