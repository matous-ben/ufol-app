package cz.ufol.app.api.v1.controller;

import cz.ufol.app.api.v1.dto.request.SeasonCreateRequest;
import cz.ufol.app.api.v1.dto.request.SeasonPatchRequest;
import cz.ufol.app.api.v1.dto.request.SeasonUpdateRequest;
import cz.ufol.app.api.v1.dto.response.SeasonResponse;
import cz.ufol.app.season.RocnikService;
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
@RequestMapping("/api/v1/seasons")
@RequiredArgsConstructor
@Tag(name = "Seasons API", description = "REST endpoints for seasons")
public class SeasonApiController {

    private final RocnikService rocnikService;

    @GetMapping
    @Operation(summary = "List seasons")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Seasons retrieved")
    })
    public ResponseEntity<List<SeasonResponse>> list() {
        return ResponseEntity.ok(rocnikService.findAllApi().stream().map(SeasonResponse::from).toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get season by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Season retrieved"),
            @ApiResponse(responseCode = "404", description = "Season not found")
    })
    public ResponseEntity<SeasonResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(SeasonResponse.from(rocnikService.findByIdApi(id)));
    }

    @PostMapping
    @Operation(summary = "Create season")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Season created"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    public ResponseEntity<SeasonResponse> create(@Valid @RequestBody SeasonCreateRequest request) {
        var created = rocnikService.createApi(request.nazev(), request.rokOd(), request.rokDo(), request.aktivni());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(SeasonResponse.from(created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update season")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Season updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Season not found")
    })
    public ResponseEntity<SeasonResponse> update(@PathVariable Long id, @Valid @RequestBody SeasonUpdateRequest request) {
        var updated = rocnikService.updateApi(id, request.nazev(), request.rokOd(), request.rokDo(), request.aktivni());
        return ResponseEntity.ok(SeasonResponse.from(updated));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Patch season")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Season patched"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Season not found")
    })
    public ResponseEntity<SeasonResponse> patch(@PathVariable Long id, @Valid @RequestBody SeasonPatchRequest request) {
        var updated = rocnikService.patchApi(id, request.nazev(), request.rokOd(), request.rokDo(), request.aktivni());
        return ResponseEntity.ok(SeasonResponse.from(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete season")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Season deleted"),
            @ApiResponse(responseCode = "404", description = "Season not found")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        rocnikService.deleteApi(id);
        return ResponseEntity.noContent().build();
    }
}
