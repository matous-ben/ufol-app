# UC-02: Zapsat výsledek zápasu

## Metadata
| Položka        | Hodnota                                                                                                                                                                                                    |
|----------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **UC ID**      | UC-02                                                                                                                                                                                                      |
| **Název**      | Zapsat výsledek zápasu                                                                                                                                                                                     |
| **Aktér**      | Admin                                                                                                                                                                                                      |
| **Popis**      | Po přihlášení do administrátorského systému vybere admin kategorii zápasu, následně zapíše a uloží výsledek konkrétního zápasu, díky čemuž budou data soutěže aktuální a automaticky se vypočítá a tabulka |
| **Předpoklad** | Admin je přihlášený v systému / dashboardu a existuje alespoň jeden naplánovaný zápas v aktuálním ročníku.                                                                                                 |
| **Výsledek**   | Admin úspěšně uloží výsledek zápasu do databáze, a aktualizuje se tabulka a statistiky zápasu                                                                                                              |
| **Spouštěč**   | Admin zadá v interním dashboardu výsledek a zvolí "Uložit výsledek"                                                                                                                                        |

## Hlavní tok (Main Flow)
1. Admin zvolí sekci Zápasy v interním dashboardu.
2. Admin vybere ze seznamu konkrétní zápas, pro který chce zapsat výsledek.
3. Admin zadá výsledek obou týmu, volitelně může přidat i další informace (minutu gólu a střelce, sestavy, karty nebo střídání).
4. Admin klikne na tlačítko "Uložit výsledek".
5. Systém uloží výsledek zápasu do databáze. Systém automaticky přepočítá ligovou tabulku na základě všech odehraných zápasů v ročníku (body, skóre, pořadí).
6. Systém zobrazí potvrzení o úspěšném zapsání výsledku zápasu.

## Alternativní toky (Alternative Flows)
### A1: Zadání neplatného výsledku
- **Bod odbočení:** Krok 4 hlavního toku
  - **Podmínka:** Admin nezadá žádný anebo neplatný (záporný/nečíselný) výsledek alespoň pro jeden z týmů.
- **Kroky:**
    1. Systém zobrazí chybovou zprávu "Neplatný výsledek. Zadejte celé nezáporné číslo."
    2. Admin opraví výsledek a znovu klikne na "Uložit výsledek".
- **Návrat:** Krok 5 hlavního toku

### A2: Zadání výsledku pro zápas, který již má zapsaný výsledek
- **Bod odbočení:** Krok 4 hlavního toku
- **Podmínka:** Admin se pokusí zapsat výsledek pro zápas, který již má zapsaný výsledek.
- **Kroky:**
    1. Systém zobrazí varování "Tento zápas již má zapsaný výsledek. Opravdu chcete přepsat existující výsledek?"
    2. Admin potvrdí, že chce přepsat výsledek.
- **Návrat:** Krok 5 hlavního toku

### A3: Žádné naplánované zápasy
- **Bod odbočení:** Krok 2 hlavního toku
- **Podmínka:** Neexistují žádné naplánované zápasy pro aktuální ročník.
- **Kroky:**
    1. Systém zobrazí zprávu "Žádné naplánované zápasy k dispozici."
    2. Systém nabídne možnost přejít zpět na hlavní dashboard nebo vytvořit nový zápas.
- **Návrat:** Žádný návrat nebo přechod na vytvoření zápasu