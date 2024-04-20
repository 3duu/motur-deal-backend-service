package br.com.motur.dealbackendservice.core.entrypoints.v1;

import br.com.motur.dealbackendservice.core.entrypoints.v1.request.DealerDto;
import br.com.motur.dealbackendservice.core.service.DealerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dealers")
public class DealerEndpoints {

    private final DealerService dealerService;

    public DealerEndpoints(DealerService dealerService) {
        this.dealerService = dealerService;
    }

    @Operation(summary = "Criar um revendedor")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Revendedor criado com sucesso"),
            @ApiResponse(responseCode = "422", description = "Erro de validação"),
            @ApiResponse(responseCode = "500", description = "Erro no servidor")
    })
    @PostMapping(consumes = "application/json", produces = "application/json")
    public Integer createDealer(final @RequestBody DealerDto dealerDto) {
        return dealerService.createDealer(dealerDto);
    }
}
