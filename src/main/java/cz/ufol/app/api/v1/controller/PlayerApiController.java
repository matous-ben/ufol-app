package cz.ufol.app.api.v1.controller;

import cz.ufol.app.api.v1.dto.request.PlayerCreateRequest;
import cz.ufol.app.api.v1.dto.request.PlayerPatchRequest;
import cz.ufol.app.api.v1.dto.request.PlayerUpdateRequest;
import cz.ufol.app.api.v1.dto.response.PlayerResponse;
import cz.ufol.app.player.HracService;
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
@RequestMapping("/api/v1/players")
@RequiredArgsConstructor
@Tag(name = "Players API", description = "REST endpoints for players")
public class PlayerApiController {

    private final HracService hracService;

    @GetMapping
    @Operation(summary = "List players")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Players retrieved")
    })
    public ResponseEntity<List<PlayerResponse>> list() {
        return ResponseEntity.ok(hracService.findAllApi().stream().map(PlayerResponse::from).toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get player by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Player retrieved"),
            @ApiResponse(responseCode = "404", description = "Player not found")
    })
    public ResponseEntity<PlayerResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(PlayerResponse.from(hracService.findByIdApi(id)));
    }

    @PostMapping
    @Operation(summary = "Create player")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Player created"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    public ResponseEntity<PlayerResponse> create(@Valid @RequestBody PlayerCreateRequest request) {
        var created = hracService.createApi(request.jmeno(), request.prijmeni(), request.datumNarozeni(), request.fotoUrl());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(PlayerResponse.from(created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update player")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Player updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Player not found")
    })
    public ResponseEntity<PlayerResponse> update(@PathVariable Long id, @Valid @RequestBody PlayerUpdateRequest request) {
        var updated = hracService.updateApi(id, request.jmeno(), request.prijmeni(), request.datumNarozeni(), request.fotoUrl());
        return ResponseEntity.ok(PlayerResponse.from(updated));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Patch player")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Player patched"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Player not found")
    })
    public ResponseEntity<PlayerResponse> patch(@PathVariable Long id, @Valid @RequestBody PlayerPatchRequest request) {
        var updated = hracService.patchApi(id, request.jmeno(), request.prijmeni(), request.datumNarozeni(), request.fotoUrl());
        return ResponseEntity.ok(PlayerResponse.from(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete player")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Player deleted"),
            @ApiResponse(responseCode = "404", description = "Player not found")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        hracService.deleteApi(id);
        return ResponseEntity.noContent().build();
    }
}
