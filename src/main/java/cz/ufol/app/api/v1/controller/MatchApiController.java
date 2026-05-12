package cz.ufol.app.api.v1.controller;

import cz.ufol.app.api.v1.dto.request.MatchCreateRequest;
import cz.ufol.app.api.v1.dto.request.MatchPatchRequest;
import cz.ufol.app.api.v1.dto.request.MatchUpdateRequest;
import cz.ufol.app.api.v1.dto.response.MatchResponse;
import cz.ufol.app.match.ZapasService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/matches")
@RequiredArgsConstructor
@Tag(name = "Matches API", description = "REST endpoints for matches")
public class MatchApiController {

    private final ZapasService zapasService;

    @GetMapping
    @Operation(summary = "List matches")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Matches retrieved")
    })
    public ResponseEntity<List<MatchResponse>> list() {
        return ResponseEntity.ok(zapasService.findAllApi().stream().map(MatchResponse::from).toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get match by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Match retrieved"),
            @ApiResponse(responseCode = "404", description = "Match not found")
    })
    public ResponseEntity<MatchResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(MatchResponse.from(zapasService.findByIdApi(id)));
    }

    @PostMapping
    @Operation(summary = "Create match")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Match created"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Related entity not found")
    })
    public ResponseEntity<MatchResponse> create(@Valid @RequestBody MatchCreateRequest request) {
        var created = zapasService.createApi(
                request.rocnikId(),
                request.domaciTymId(),
                request.hosteTymId(),
                request.mistoKonaniId(),
                request.datumCas(),
                request.odehran(),
                request.domaciSkore(),
                request.hosteSkore()
        );
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(MatchResponse.from(created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update match")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Match updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Match or related entity not found")
    })
    public ResponseEntity<MatchResponse> update(@PathVariable Long id, @Valid @RequestBody MatchUpdateRequest request) {
        var updated = zapasService.updateApi(
                id,
                request.rocnikId(),
                request.domaciTymId(),
                request.hosteTymId(),
                request.mistoKonaniId(),
                request.datumCas(),
                request.odehran(),
                request.domaciSkore(),
                request.hosteSkore()
        );
        return ResponseEntity.ok(MatchResponse.from(updated));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Patch match")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Match patched"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Match or related entity not found")
    })
    public ResponseEntity<MatchResponse> patch(@PathVariable Long id, @Valid @RequestBody MatchPatchRequest request) {
        var updated = zapasService.patchApi(
                id,
                request.rocnikId(),
                request.domaciTymId(),
                request.hosteTymId(),
                request.mistoKonaniId(),
                request.datumCas(),
                request.odehran(),
                request.domaciSkore(),
                request.hosteSkore()
        );
        return ResponseEntity.ok(MatchResponse.from(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete match")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Match deleted"),
            @ApiResponse(responseCode = "404", description = "Match not found")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        zapasService.deleteApi(id);
        return ResponseEntity.noContent().build();
    }
}
