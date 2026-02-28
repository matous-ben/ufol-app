# UC-01: Zobrazit tabulku

## Metadata
| Položka        | Hodnota                                                                                                     |
|----------------|-------------------------------------------------------------------------------------------------------------|
| **UC ID**      | UC-01                                                                                                       |
| **Název**      | Zobrazit tabulku                                                                                            |
| **Aktér**      | Fanoušek                                                                                                    |
| **Popis**      | Fanoušek si zobrazí aktuální ligovou tabulku s pořadím týmů, body a statistikami pro zvolený ročník soutěže |
| **Předpoklad** | V systému existuje alespoň jeden ročník soutěže s přiřazenými týmy                                          |
| **Výsledek**   | Fanoušek vidí aktuální tabulku zvoleného ročníku seřazenou podle bodů sestupně                              |
| **Spouštěč**   | Fanoušek zvolí sekci "Tabulka" v navigaci                                                                   |

## Hlavní tok (Main Flow)
1. Fanoušek zvolí možnost zobrazení tabulky
2. Systém načte aktuální ročník soutěže
3. Systém vypočítá pořadí týmů podle získaných bodů, při stejné bodové hodnotě se týmy řadí podle skóre, poté podle počtu vstřelených gólů
4. Systém zobrazí tabulku s týmy seřazenými sestupně podle bodů, tabulku obsahuje: pořadí, logo a název týmu, počet odehraných zápasů, výhry, remízy, prohry, skóre a počet bodů
5. Fanoušek může filtrovat tabulku podle ročníku soutěže, pokud je k dispozici více ročníků

## Alternativní toky (Alternative Flows)
### A1: Žádné odehrané zápasy v aktuálním ročníku
- **Bod odbočení:** Krok 3 v hlavním toku
- **Podmínka:** V aktuálním ročníku soutěže nejsou žádné odehrané zápasy, tedy všechny týmy mají 0 bodů
- **Kroky:**
    1. Systém zobrazí tabulku se všemi týmy seřazenými abecedně podle názvu, protože všechny mají 0 bodů
    2. Systém zobrazí informaci "Sezóna právě začala, zatím nebyly odehrány žádné zápasy"
- **Návrat:** Krok 4 v hlavním toku

### A2: Neexistuje žádný aktuální ročník soutěže
- **Bod odbočení:** Krok 2 v hlavním toku
- **Podmínka:** V systému není definován žádný aktuální ročník soutěže
- **Kroky:**
    1. Systém zobrazí informaci "Žádný aktuální ročník soutěže není k dispozici"
    2. Systém nabídne možnost prohlédnout si archivy předchozích ročníků, pokud jsou k dispozici
- **Návrat:** Žádný pokud neexistují předchozí ročníky, jinak krok 4 v hlavním toku po výběru ročníku z archivu