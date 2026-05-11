package cz.ufol.app.player;

import cz.ufol.app.match.UcastVZapaseRepository;
import cz.ufol.app.season.Rocnik;
import cz.ufol.app.season.RocnikRepository;
import cz.ufol.app.team.Tym;
import cz.ufol.app.team.TymRepository;
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
public class HracService {

	private final HracRepository hracRepository;
	private final RegistraceRepository registraceRepository;
	private final UcastVZapaseRepository ucastVZapaseRepository;
	private final TymRepository tymRepository;
	private final RocnikRepository rocnikRepository;

	public record ServiceResult(String redirectPath, String flashType, String flashMessage) {}
	public record AdminHracListData(
			List<Tym> tymy,
			Long selectedTymId,
			Rocnik aktivniRocnik,
			String error,
			List<HracStatView> hracStats
	) {}

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

	@Transactional(readOnly = true)
	public AdminHracListData getAdminHracListData(Long tymId) {
		var aktivniRocnik = rocnikRepository.findByAktivniTrue().orElse(null);
		var tymy = tymRepository.findByAktivniTrue();

		if (aktivniRocnik == null) {
			return new AdminHracListData(tymy, tymId, null, "Nejprve nastavte aktivní ročník.", List.of());
		}
		if (tymId == null) {
			return new AdminHracListData(tymy, null, aktivniRocnik, null, List.of());
		}

		var tymOpt = tymRepository.findById(tymId);
		if (tymOpt.isEmpty()) {
			return new AdminHracListData(tymy, tymId, aktivniRocnik, "Vybraný tým nebyl nalezen.", List.of());
		}

		return new AdminHracListData(
				tymy,
				tymId,
				aktivniRocnik,
				null,
				najdiStatistikyTymuProRocnik(tymOpt.get(), aktivniRocnik)
		);
	}

	@Transactional
	public ServiceResult createAdminHrac(String jmeno, String prijmeni, String datumNarozeni, Long tymId) {
		String jmenoTrim = jmeno == null ? "" : jmeno.trim();
		String prijmeniTrim = prijmeni == null ? "" : prijmeni.trim();
		if (jmenoTrim.isBlank() || prijmeniTrim.isBlank()) {
			return new ServiceResult("/admin/hraci?tymId=" + tymId, "error", "Jméno i příjmení jsou povinné.");
		}

		var aktivniRocnikOpt = rocnikRepository.findByAktivniTrue();
		if (aktivniRocnikOpt.isEmpty()) {
			return new ServiceResult("/admin/hraci?tymId=" + tymId, "error", "Nejprve nastavte aktivní ročník.");
		}

		var tymOpt = tymRepository.findById(tymId);
		if (tymOpt.isEmpty()) {
			return new ServiceResult("/admin/hraci", "error", "Vybraný tým nebyl nalezen.");
		}

		LocalDate parsedDatumNarozeni = null;
		if (datumNarozeni != null && !datumNarozeni.isBlank()) {
			try {
				parsedDatumNarozeni = LocalDate.parse(datumNarozeni);
			} catch (DateTimeParseException e) {
				return new ServiceResult("/admin/hraci?tymId=" + tymId, "error", "Neplatný formát data narození.");
			}
		}

		createHracSRegistraci(jmenoTrim, prijmeniTrim, parsedDatumNarozeni, tymOpt.get(), aktivniRocnikOpt.get());
		return new ServiceResult("/admin/hraci?tymId=" + tymId, "success", "Hráč byl přidán do aktivního ročníku.");
	}

	@Transactional
	public ServiceResult deleteAdminHrac(Long hracId, Long tymId) {
		smazatHraceVcetneHistorie(hracId);
		String redirectPath = tymId == null ? "/admin/hraci" : "/admin/hraci?tymId=" + tymId;
		return new ServiceResult(redirectPath, "success", "Hráč byl odebrán.");
	}
}
