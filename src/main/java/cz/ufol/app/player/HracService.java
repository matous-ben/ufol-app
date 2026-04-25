package cz.ufol.app.player;

import cz.ufol.app.match.UcastVZapaseRepository;
import cz.ufol.app.season.Rocnik;
import cz.ufol.app.team.Tym;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HracService {

	private final HracRepository hracRepository;
	private final RegistraceRepository registraceRepository;
	private final UcastVZapaseRepository ucastVZapaseRepository;

	@Transactional
	public void createHracSRegistraci(String jmeno,
									  String prijmeni,
									  LocalDate datumNarozeni,
									  Tym tym,
									  Rocnik rocnik) {
		Hrac hrac = Hrac.builder()
				.jmeno(jmeno.trim())
				.prijmeni(prijmeni.trim())
				.datumNarozeni(datumNarozeni)
				.build();

		Hrac ulozenyHrac = hracRepository.save(hrac);

		Registrace registrace = Registrace.builder()
				.hrac(ulozenyHrac)
				.tym(tym)
				.rocnik(rocnik)
				.build();

		registraceRepository.save(registrace);
	}

	@Transactional
	public void smazatHraceVcetneHistorie(Long hracId) {
		List<Registrace> registrace = registraceRepository.findByHracId(hracId);
		if (!registrace.isEmpty()) {
			ucastVZapaseRepository.deleteByRegistraceIn(registrace);
			registraceRepository.deleteAll(registrace);
		}
		hracRepository.deleteById(hracId);
	}

	@Transactional(readOnly = true)
	public List<HracStatView> najdiStatistikyTymuProRocnik(Tym tym, Rocnik rocnik) {
		List<Registrace> registrace = registraceRepository
				.findByRocnikAndTymOrderByHracPrijmeniAscHracJmenoAsc(rocnik, tym);

		if (registrace.isEmpty()) {
			return List.of();
		}

		List<cz.ufol.app.match.UcastVZapase> ucasti = ucastVZapaseRepository.findByRegistraceIn(registrace);

		Map<Long, Long> odehraneZapasyMap = ucasti.stream()
				.collect(Collectors.groupingBy(u -> u.getRegistrace().getId(), Collectors.counting()));

		Map<Long, Long> golyMap = ucasti.stream()
				.collect(Collectors.groupingBy(
						u -> u.getRegistrace().getId(),
						Collectors.summingLong(u -> u.getGoly() == null ? 0 : u.getGoly())
				));

		return registrace.stream()
				.map(r -> new HracStatView(
						r,
						odehraneZapasyMap.getOrDefault(r.getId(), 0L),
						golyMap.getOrDefault(r.getId(), 0L)
				))
				.toList();
	}
}
