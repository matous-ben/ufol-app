package cz.ufol.app.team;

import cz.ufol.app.university.University;
import cz.ufol.app.university.UniversityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamService {
    private final TeamRepository teamRepository;
    private final UniversityRepository universityRepository;

    public record ServiceResult(String redirectPath, String flashType, String flashMessage) {}
    public record AdminTeamsFormData(Team team, List<University> universities, String formAction) {}

    @Transactional(readOnly = true)
    public List<Team> findAllActive() {
        return teamRepository.findByActiveTrue();
    }

    @Transactional(readOnly = true)
    public List<Team> findAllAdminTeams() {
        return teamRepository.findAllByOrderByNameAsc();
    }

    @Transactional(readOnly = true)
    public AdminTeamsFormData getCreateFormData() {
        return new AdminTeamsFormData(new Team(), universityRepository.findAllByOrderByNameAsc(), "/admin/teams/novy");
    }

    @Transactional(readOnly = true)
    public AdminTeamsFormData getEditFormData(Long id) {
        return new AdminTeamsFormData(
                teamRepository.findById(id).orElseThrow(),
                universityRepository.findAllByOrderByNameAsc(),
                "/admin/teams/" + id + "/edit"
        );
    }

    @Transactional
    public ServiceResult createAdminTeam(String name, Long universityId, boolean active) {
        String trimmedName = name == null ? "" : name.trim();
        if (teamRepository.existsByNameIgnoreCase(trimmedName)) {
            return new ServiceResult("/admin/teams/novy", "error", "Tým s názvem '" + trimmedName + "' již existuje.");
        }

        var university = universityRepository.findById(universityId).orElseThrow();
        teamRepository.save(Team.builder()
                .name(trimmedName)
                .university(university)
                .active(active)
                .build());
        return new ServiceResult("/admin/teams", "success", "Tým byl úspěšně přidán.");
    }

    @Transactional
    public ServiceResult updateAdminTeam(Long id, String name, Long universityId, boolean active) {
        String trimmedName = name == null ? "" : name.trim();
        if (teamRepository.existsByNameIgnoreCaseAndIdNot(trimmedName, id)) {
            return new ServiceResult("/admin/teams/" + id + "/edit", "error", "Tým s názvem '" + trimmedName + "' již existuje.");
        }

        var team = teamRepository.findById(id).orElseThrow();
        var university = universityRepository.findById(universityId).orElseThrow();
        team.setName(trimmedName);
        team.setUniversity(university);
        team.setActive(active);
        teamRepository.save(team);

        return new ServiceResult("/admin/teams", "success", "Tým byl upraven.");
    }

    @Transactional
    public ServiceResult deleteAdminTeam(Long id) {
        teamRepository.deleteById(id);
        return new ServiceResult("/admin/teams", "success", "Tým byl smazán.");
    }
}
