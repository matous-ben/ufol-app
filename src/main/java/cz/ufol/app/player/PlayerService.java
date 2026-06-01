package cz.ufol.app.player;

import cz.ufol.app.match.MatchParticipation;
import cz.ufol.app.match.MatchParticipationRepository;
import cz.ufol.app.season.Season;
import cz.ufol.app.season.SeasonRepository;
import cz.ufol.app.team.Team;
import cz.ufol.app.team.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlayerService {

	private final PlayerRepository playerRepository;
	private final RegistrationRepository registrationRepository;
	private final MatchParticipationRepository matchParticipationRepository;
	private final TeamRepository teamRepository;
	private final SeasonRepository seasonRepository;

	public record ServiceResult(String redirectPath, String flashType, String flashMessage) {}
	public record AdminPlayerListData(
			List<Team> teams,
			Long selectedTeamId,
			Season activeSeason,
			String error,
			List<PlayerStatView> playerStats
	) {}

	@Transactional
	public void createPlayerWithRegistration(String firstName,
	                                         String lastName,
	                                         LocalDate birthDate,
	                                         Team team,
	                                         Season season) {
		Player player = Player.builder()
				.firstName(firstName.trim())
				.lastName(lastName.trim())
				.birthDate(birthDate)
				.build();

		Player savePlayer = playerRepository.save(player);

		Registration registration = Registration.builder()
				.player(savePlayer)
				.team(team)
				.season(season)
				.build();

		registrationRepository.save(registration);
	}

	@Transactional
	public void deletePlayerIncludingHistory(Long playerId) {
		List<Registration> registration = registrationRepository.findByPlayerId(playerId);
		if (!registration.isEmpty()) {
			matchParticipationRepository.deleteByRegistrationIn(registration);
			registrationRepository.deleteAll(registration);
		}
		playerRepository.deleteById(playerId);
	}

	@Transactional(readOnly = true)
	public List<PlayerStatView> findTeamStatisticsForSeason(Team team, Season season) {
		List<Registration> registration = registrationRepository
				.findBySeasonAndTeamOrderByPlayerLastNameAscPlayerFirstNameAsc(season, team);

		if (registration.isEmpty()) {
			return List.of();
		}

		List<MatchParticipation> participations = matchParticipationRepository.findByRegistrationIn(registration);

		Map<Long, Long> playedMatchesMap = participations.stream()
				.collect(Collectors.groupingBy(u -> u.getRegistration().getId(), Collectors.counting()));

		Map<Long, Long> goalsMap = participations.stream()
				.collect(Collectors.groupingBy(
						u -> u.getRegistration().getId(),
						Collectors.summingLong(u -> u.getGoals() == null ? 0 : u.getGoals())
				));

		return registration.stream()
				.map(r -> new PlayerStatView(
						r,
						playedMatchesMap.getOrDefault(r.getId(), 0L),
						goalsMap.getOrDefault(r.getId(), 0L)
				))
				.toList();
	}

	@Transactional(readOnly = true)
	public AdminPlayerListData getAdminPlayerListData(Long teamId) {
		var activeSeason = seasonRepository.findByActiveTrue().orElse(null);
		var teams = teamRepository.findByActiveTrue();

		if (activeSeason == null) {
			return new AdminPlayerListData(teams, teamId, null, "Nejprve nastavte aktivní ročník.", List.of());
		}
		if (teamId == null) {
			return new AdminPlayerListData(teams, null, activeSeason, null, List.of());
		}

		var teamOpt = teamRepository.findById(teamId);
		if (teamOpt.isEmpty()) {
			return new AdminPlayerListData(teams, teamId, activeSeason, "Vybraný tým nebyl nalezen.", List.of());
		}

		return new AdminPlayerListData(
				teams,
				teamId,
				activeSeason,
				null,
				findTeamStatisticsForSeason(teamOpt.get(), activeSeason)
		);
	}

	@Transactional
	public ServiceResult createAdminPlayer(String firstName, String lastName, String birthDate, Long teamId) {
		String firstNameTrimmed = firstName == null ? "" : firstName.trim();
		String lastNameTrimmed = lastName == null ? "" : lastName.trim();
		if (firstNameTrimmed.isBlank() || lastNameTrimmed.isBlank()) {
			return new ServiceResult("/admin/hraci?teamId=" + teamId, "error", "Jméno i příjmení jsou povinné.");
		}

		var activeSeasonOpt = seasonRepository.findByActiveTrue();
		if (activeSeasonOpt.isEmpty()) {
			return new ServiceResult("/admin/hraci?teamId=" + teamId, "error", "Nejprve nastavte aktivní ročník.");
		}

		var teamOpt = teamRepository.findById(teamId);
		if (teamOpt.isEmpty()) {
			return new ServiceResult("/admin/hraci", "error", "Vybraný tým nebyl nalezen.");
		}

		LocalDate parsedBirthDate = null;
		if (birthDate != null && !birthDate.isBlank()) {
			try {
				parsedBirthDate = LocalDate.parse(birthDate);
			} catch (DateTimeParseException e) {
				return new ServiceResult("/admin/hraci?teamId=" + teamId, "error", "Neplatný formát data narození.");
			}
		}

		createPlayerWithRegistration(firstNameTrimmed, lastNameTrimmed, parsedBirthDate, teamOpt.get(), activeSeasonOpt.get());
		return new ServiceResult("/admin/hraci?teamId=" + teamId, "success", "Hráč byl přidán do aktivního ročníku.");
	}

	@Transactional
	public ServiceResult deleteAdminPlayer(Long PlayerId, Long teamId) {
		deletePlayerIncludingHistory(PlayerId);
		String redirectPath = teamId == null ? "/admin/hraci" : "/admin/hraci?teamId=" + teamId;
		return new ServiceResult(redirectPath, "success", "Hráč byl odebrán.");
	}
}
