package br.com.motur.dealbackendservice.core.entrypoints.v1;

import br.com.motur.dealbackendservice.core.entrypoints.v1.request.AdDto;
import br.com.motur.dealbackendservice.core.service.AdPublicationService;
import br.com.motur.dealbackendservice.core.service.vo.PostResultsVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ad-publication")
public class AdPublicationEndpoints {

    private final AdPublicationService adPublicationService;

    @Autowired
    public AdPublicationEndpoints(AdPublicationService adPublicationService) {
        this.adPublicationService = adPublicationService;
    }

    @Operation(summary = "Publicar um anúncio")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ad published successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PostMapping(value = "/publish", consumes = "application/json", produces = "application/json")
    public ResponseEntity<PostResultsVo> publishAd(@RequestBody AdDto adDto) throws Exception {
        return ResponseEntity.ok(adPublicationService.publishAd(adDto));
    }

    @Operation(summary = "Obter um anúncio por id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the ad"),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied"),
            @ApiResponse(responseCode = "404", description = "Ad not found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AdDto> getAd(@PathVariable Long id) {
        AdDto adDto = adPublicationService.getAdDto(id);
        if (adDto != null) {
            return ResponseEntity.ok(adDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

