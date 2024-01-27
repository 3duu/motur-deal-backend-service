package br.com.motur.dealbackendservice.core.finder;

import br.com.motur.dealbackendservice.core.model.common.TransmissionType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class TransmissionFinder {

    public TransmissionType fromString(String tipo) {

        if (StringUtils.isEmpty(tipo)) {
            return null;
        }

        tipo = tipo.toLowerCase();

        // Manual
        if (!tipo.contains("autom") && !tipo.contains("sequ") && (tipo.contains("manual") || tipo.contains("stick shift") || tipo.contains("standard") || tipo.contains("normal") || tipo.contains("padrão") /*|| tipo.equals(" mt ")*/ || tipo.contains("m/t") || tipo.contains(TransmissionType.MANUAL.getDisplayName().toLowerCase()))) {
            return TransmissionType.MANUAL;
        }
        // Automático
        else if (tipo.contains("automático") || tipo.contains("automatico") || tipo.contains("auto") || tipo.contains("slushbox") || tipo.equals("at") || tipo.contains("a/t") || tipo.contains(TransmissionType.AUTOMATIC.getDisplayName().toLowerCase())) {
            // Semi-Automático
            if (tipo.contains("seqüencial") || tipo.contains("sequencial") || tipo.contains("sequential") || tipo.contains("semi-auto") || tipo.contains("semi-automatic") || tipo.contains(TransmissionType.SEMI_AUTOMATIC.getDisplayName().toLowerCase())) {
                return TransmissionType.SEMI_AUTOMATIC;
            }
            // Tiptronic
            else if (tipo.contains("tiptronic") || tipo.contains("manumatic") || tipo.contains("sport mode") || tipo.contains("sportmatic") || tipo.contains("steptronic") || tipo.contains("shiftmatic") || tipo.contains("shifttronic") || tipo.contains("sportronic") || tipo.contains("touchshift") || tipo.contains("sportshift") || tipo.contains("paddle shift")  || tipo.contains("paddle shift")  || tipo.contains(TransmissionType.DIRECT_SHIFT.getDisplayName().toLowerCase())) {
                return TransmissionType.TIPTRONIC;
            }
            // Direct Shift
            else if (tipo.contains("dualogic") || tipo.contains("dsg") || tipo.contains("direct shift") || tipo.contains("paddle shift") || tipo.contains(TransmissionType.DIRECT_SHIFT.getDisplayName().toLowerCase())) {
                return TransmissionType.DIRECT_SHIFT;
            }
            // Automático Padrão
            else {
                System.out.println(tipo);
                return TransmissionType.AUTOMATIC;
            }
        }
        else if (tipo.contains("tiptronic") || tipo.contains("manumatic") || tipo.contains("sport mode") || tipo.contains("sportmatic") || tipo.contains("steptronic") || tipo.contains("shiftmatic") || tipo.contains("shifttronic") || tipo.contains("sportronic") || tipo.contains("touchshift") || tipo.contains("sportshift") || tipo.contains("paddle shift")  || tipo.contains("paddle shift")  || tipo.contains(TransmissionType.DIRECT_SHIFT.getDisplayName().toLowerCase())) {
            return TransmissionType.TIPTRONIC;
        }
        else if (tipo.contains("automático") || tipo.contains("automatico") || tipo.contains("auto") || tipo.contains("slushbox") || tipo.equals("at") || tipo.contains("a/t") || tipo.equals(TransmissionType.AUTOMATIC.getDisplayName().toLowerCase())) {
            return TransmissionType.AUTOMATIC;
        }
        // CVT
        else if (tipo.contains("cvt") || tipo.contains("continuously variable") || tipo.contains("varimatic") || tipo.contains("multitronic") || tipo.contains(TransmissionType.CVT.getDisplayName().toLowerCase())) {
            return TransmissionType.CVT;
        }
        // Dupla Embreagem
        else if (tipo.contains("dupla embreagem") || tipo.contains("dual clutch") || tipo.contains("twin clutch") || tipo.contains("dct") || tipo.contains("powershift") || tipo.contains(TransmissionType.DUAL_CLUTCH.getDisplayName().toLowerCase())) {
            return TransmissionType.DUAL_CLUTCH;
        }
        // Elétrico Variável
        else if (tipo.contains("eletrico") || tipo.contains("elétrico") || tipo.contains("electric") || tipo.contains("ev transmission") || tipo.contains(TransmissionType.ELECTRIC_VARIABLE.getDisplayName().toLowerCase())) {
            return TransmissionType.ELECTRIC_VARIABLE;
        }
        // Híbrido
        else if (tipo.contains("híbrido") || tipo.contains("hibrido") || tipo.contains("hybrid") || tipo.contains("eco") || tipo.contains("regenerative") || tipo.contains(TransmissionType.HYBRID.getDisplayName().toLowerCase())) {
            return TransmissionType.HYBRID;
        }

        return TransmissionType.NONE; // Padrão se nenhuma correspondência for encontrada
    }
}
