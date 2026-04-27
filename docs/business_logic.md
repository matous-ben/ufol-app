# Popis byznys logiky (Business Logic Description)

Tento dokument popisuje klíčová doménová pravidla a byznys logiku informačního systému UFoL (Univerzitní fotbalová liga). Logika je implementována primárně v servisní vrstvě (Service layer) Spring Boot aplikace.

## 1. Výpočet ligové tabulky (Standings Calculation)
Základním prvkem celého systému je dynamický výpočet ligové tabulky. Tabulka se neukládá staticky do databáze, ale generuje se za běhu na základě odehraných zápasů v daném ročníku.

**Pravidla pro výpočet bodů (Match Scoring):**
*   **Výhra:** 3 body pro vítězný tým.
*   **Remíza:** 1 bod pro oba týmy.
*   **Prohra:** 0 bodů pro poražený tým.

**Pravidla pro řazení týmů v tabulce (Tie-breakers):**
Pokud mají dva nebo více týmů stejný počet bodů, aplikují se následující kritéria v přesném pořadí:
1.  **Počet bodů** (sestupně)
2.  **Rozdíl skóre** (vstřelené góly mínus obdržené góly, sestupně)
3.  **Počet vstřelených gólů** (sestupně)
4.  **Abecední pořadí** (pokud jsou všechna předchozí kritéria shodná, např. na začátku sezóny)

*Trigger:* K přepočtu tabulky dochází automaticky (v reálném čase) při každém uložení, úpravě nebo smazání výsledku zápasu administrátorem (dle FR-17).

## 2. Správa zápasů a výsledků
Zápas (Match) je centrální entitou, která propojuje týmy, místo konání a konkrétní ročník.

**Byznys pravidla pro zápasy:**
*   **Validace soupeřů:** Zápas musí být vždy přiřazen dvěma rozdílným týmům (Domácí vs. Hosté). Tým nemůže hrát sám se sebou (FR-19).
*   **Validace skóre:** Výsledek zápasu (počet gólů) musí být celé, nezáporné číslo (`>= 0`).
*   **Události zápasu (Match Events):** K zápasu lze vázat specifické události v konkrétní minutě. Systém rozeznává 4 pevně dané typy událostí (Gól, Asistence, Žlutá karta, Červená karta).
*   **Stav zápasu:** Zápas bez zadaného skóre je považován za "Naplánovaný". Jakmile je zadáno skóre, zápas přechází do stavu "Odehraný" a jeho výsledek se začne propisovat do ligové tabulky.

## 3. Organizace sezón (Ročníky)
Systém je navržen tak, aby dokázal historizovat data a nepomíchal výsledky z různých let.

*   Každý zápas a registrace hráče je vázána na konkrétní **Ročník** (např. "2025/2026").
*   Ročník má příznak `aktivni` (boolean). V jeden moment by měl být primárně jeden ročník označen jako aktuální/aktivní. 
*   Při načtení veřejné hlavní stránky systém automaticky vyhledá aktivní ročník a zobrazí jeho tabulku. Neaktivní ročníky jsou uloženy v archivu.

## 4. Týmy, Univerzity a Hráči
*   **Univerzita vs. Tým:** Univerzita (např. UPCE) je nadřazená entita, která obsahuje základní údaje a logo. Tým (např. UPCE Riders) je konkrétní sportovní reprezentace univerzity.
*   **Registrace hráče:** Hráč není vázán na tým trvale, ale prostřednictvím entity `Registrace`, která propojuje Hráče, Tým a Ročník. Toto řeší situace, kdy hráč přestoupí do jiného týmu nebo ukončí studium (historická data v předchozích ročnících zůstanou zachována).

## 5. Bezpečnost a Autentizace (Spring Security)
Vzhledem k tomu, že systém v první verzi nepočítá s přihlašováním fanoušků, je veškerá bezpečnostní logika zaměřena na administrátory.

*   **Ochrana cest (URL Protection):** Veškeré cesty začínající `/admin/**` jsou striktně chráněny a vyžadují platnou relaci (Session). Nepřihlášený uživatel je automaticky přesměrován na `/login`.
*   **Hesla:** Hesla administrátorů jsou v databázi uložena výhradně ve formě hashe vytvořeného algoritmem **BCrypt**. Systém nikdy nepracuje s hesly v plain-textu (ADR-003, NFR-03).
*   **Session Management:** Relace administrátora automaticky vyprší po 15 minutách neaktivity (Timeout), čímž se minimalizuje riziko zneužití opuštěného počítače (NFR-05).
*   **CSRF (Cross-Site Request Forgery):** Všechny formuláře v administrátorské sekci měnící stav systému (POST, PUT, DELETE) jsou chráněny automaticky generovaným CSRF tokenem.
