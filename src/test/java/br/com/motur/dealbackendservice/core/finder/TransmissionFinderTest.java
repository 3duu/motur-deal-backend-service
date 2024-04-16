package br.com.motur.dealbackendservice.core.finder;

import br.com.motur.dealbackendservice.core.model.common.TransmissionType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TransmissionFinderTest {

    @InjectMocks
    private TransmissionFinder transmissionFinder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void fromStringReturnsCorrectTransmissionTypeForValidInput() {
        assertEquals(TransmissionType.MANUAL, transmissionFinder.fromString("manual"));
        assertEquals(TransmissionType.SEMI_AUTOMATIC, transmissionFinder.fromString("seqüencial"));
        assertEquals(TransmissionType.DIRECT_SHIFT, transmissionFinder.fromString("dualogic"));
        assertEquals(TransmissionType.AUTOMATIC, transmissionFinder.fromString("automático"));
        assertEquals(TransmissionType.TIPTRONIC, transmissionFinder.fromString("tiptronic"));
        assertEquals(TransmissionType.CVT, transmissionFinder.fromString("cvt"));
        assertEquals(TransmissionType.DUAL_CLUTCH, transmissionFinder.fromString("dupla embreagem"));
        assertEquals(TransmissionType.ELECTRIC_VARIABLE, transmissionFinder.fromString("eletrico"));
        assertEquals(TransmissionType.HYBRID, transmissionFinder.fromString("híbrido"));
    }

    @Test
    void fromStringReturnsNoneForInvalidInput() {
        assertEquals(TransmissionType.NONE, transmissionFinder.fromString("invalid"));
    }

    @Test
    void fromStringReturnsNoneForEmptyInput() {
        assertEquals(null, transmissionFinder.fromString(""));
    }

    @Test
    void fromStringReturnsNullForNullInput() {
        assertNull(transmissionFinder.fromString(null));
    }
}