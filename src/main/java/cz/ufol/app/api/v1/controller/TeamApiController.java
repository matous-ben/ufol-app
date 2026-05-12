package cz.ufol.app.api.v1.controller;

import cz.ufol.app.api.v1.dto.request.TeamCreateRequest;
import cz.ufol.app.api.v1.dto.request.TeamPatchRequest;
import cz.ufol.app.api.v1.dto.request.TeamUpdateRequest;
import cz.ufol.app.api.v1.dto.response.TeamResponse;
import cz.ufol.app.team.TymService;
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
@RequestMapping("/api/v1/teams")
@RequiredArgsConstructor
@Tag(name = "Teams API", description = "REST endpoints for teams")
public class TeamApiController {

    private final TymService tymService;

    @GetMapping
    @Operation(summary = "List teams")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Teams retrieved")
    })
    public ResponseEntity<List<TeamResponse>> list() {
        return ResponseEntity.ok(tymService.findAllApi().stream().map(TeamResponse::from).toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get team by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Team retrieved"),
            @ApiResponse(responseCode = "404", description = "Team not found")
    })
    public ResponseEntity<TeamResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(TeamResponse.from(tymService.findByIdApi(id)));
    }

    @PostMapping
    @Operation(summary = "Create team")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Team created"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Related entity not found")
    })
    public ResponseEntity<TeamResponse> create(@Valid @RequestBody TeamCreateRequest request) {
        var created = tymService.createApi(request.nazev(), request.univerzitaId(), request.aktivni());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(TeamResponse.from(created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update team")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Team updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Team or related entity not found")
    })
    public ResponseEntity<TeamResponse> update(@PathVariable Long id, @Valid @RequestBody TeamUpdateRequest request) {
        var updated = tymService.updateApi(id, request.nazev(), request.univerzitaId(), request.aktivni());
        return ResponseEntity.ok(TeamResponse.from(updated));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Patch team")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Team patched"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Team or related entity not found")
    })
    public ResponseEntity<TeamResponse> patch(@PathVariable Long id, @Valid @RequestBody TeamPatchRequest request) {
        var updated = tymService.patchApi(id, request.nazev(), request.univerzitaId(), request.aktivni());
        return ResponseEntity.ok(TeamResponse.from(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete team")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Team deleted"),
            @ApiResponse(responseCode = "404", description = "Team not found")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tymService.deleteApi(id);
        return ResponseEntity.noContent().build();
    }
}
