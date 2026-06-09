package cz.ufol.app.match;

import cz.ufol.app.match.dto.CreateMatchRequest;
import cz.ufol.app.match.dto.MatchResponse;
import cz.ufol.app.match.dto.RecordMatchResultRequest;
import cz.ufol.app.match.dto.UpdateMatchLogisticsRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/matches")
@RequiredArgsConstructor
@Tag(name = "Matches", description = "Operations related to football matches program, logistics and results")
public class MatchController {

    private final MatchService matchService;

    // PUBLIC
    @GetMapping
    @Operation(summary = "Get all matches", description = "Returns a list of all upcoming and played matches.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of matches")
    public ResponseEntity<List<MatchResponse>> getMatches() {
        return ResponseEntity.ok(matchService.getMatches());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get match details by ID", description = "Returns detailed information about a specific match.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved match details")
    @ApiResponse(responseCode = "404", description = "Match not found")
    public ResponseEntity<MatchResponse> getMatchById(@PathVariable Long id) {
        return ResponseEntity.ok(matchService.getMatchById(id));
    }

    // ADMIN
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new match", description = "Schedules a new match between two teams for a specific season. Restricted to admins.")
    @ApiResponse(responseCode = "201", description = "Match successfully created")
    @ApiResponse(responseCode = "400", description = "Invalid request data or team playing against itself")
    public ResponseEntity<MatchResponse> createMatch(@Valid @RequestBody CreateMatchRequest request) {
        MatchResponse createdMatch = matchService.createMatch(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMatch);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update match logistics", description = "Partially updates match logistics (date/time or venue) before it is played. Restricted to admins.")
    @ApiResponse(responseCode = "200", description = "Match logistics successfully updated")
    @ApiResponse(responseCode = "404", description = "Match or venue not found")
    public ResponseEntity<MatchResponse> updateMatchLogistics(
            @PathVariable Long id,
            @Valid @RequestBody UpdateMatchLogisticsRequest request) {
        return ResponseEntity.ok(matchService.updateMatchLogistics(id, request));
    }

    @PutMapping("/{id}/result")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Record match result", description = "Saves the final score and player statistics for a specific match. Restricted to admins.")
    @ApiResponse(responseCode = "200", description = "Match result successfully recorded")
    @ApiResponse(responseCode = "400", description = "Invalid scores or goal stats mismatch")
    @ApiResponse(responseCode = "404", description = "Match or player registration not found")
    public ResponseEntity<MatchResponse> recordMatchResult(
            @PathVariable Long id,
            @Valid @RequestBody RecordMatchResultRequest matchResult) {
        return ResponseEntity.ok(matchService.recordMatchResult(id, matchResult));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a match", description = "Deletes a specific match from the system. Restricted to admins.")
    @ApiResponse(responseCode = "204", description = "Match successfully deleted")
    @ApiResponse(responseCode = "404", description = "Match not found")
    public ResponseEntity<Void> deleteMatch(@PathVariable Long id) {
        matchService.deleteMatch(id);
        return ResponseEntity.noContent().build();
    }
}
