# Nefunkční / kvalitativní požadavky (Non-Functional Requirements)

## Výkon (Performance)

| ID     | Požadavek                                                                                | Priorita    |
|--------|------------------------------------------------------------------------------------------|-------------|
| NFR-01 | Všechny stránky aplikace se musí načíst do 3 sekund při běžném provozu                   | Should Have |
| NFR-02 | Výpočet ligové tabulky musí proběhnout automaticky bez znatelného zpoždění pro uživatele | Should Have |

## Bezpečnost (Security)

| ID     | Požadavek                                                                          | Priorita    |
|--------|------------------------------------------------------------------------------------|-------------|
| NFR-03 | Hesla musí být ukládána výhradně v hashované podobě, nikdy jako prostý text        | Must Have   |
| NFR-04 | Administrátorská sekce (/admin/**) musí být přístupná pouze přihlášeným uživatelům | Must Have   |
| NFR-05 | Relace přihlášeného admina musí automaticky vypršet po 30 minutách neaktivity      | Should Have |
| NFR-06 | Systém musí validovat vstupní data na straně serveru i na straně klienta           | Must Have   |

## Použitelnost (Usability)

| ID     | Požadavek                                                                                | Priorita  |
|--------|------------------------------------------------------------------------------------------|-----------|
| NFR-07 | Aplikace musí být plně responzivní a funkční na mobilních zařízeních                     | Must Have |
| NFR-08 | Admin dashboard musí poskytovat zpětnou vazbu po každé operaci (úspěch, chyba, validace) | Must Have |

## Kompatibilita (Compatibility)

| ID     | Požadavek                                                                                | Priorita    |
|--------|------------------------------------------------------------------------------------------|-------------|
| NFR-09 | Aplikace musí být funkční v aktuálních verzích prohlížečů Chrome, Firefox, Safari a Edge | Should Have |

## Udržovatelnost (Maintainability)

| ID     | Požadavek                                                                           | Priorita    |
|--------|-------------------------------------------------------------------------------------|-------------|
| NFR-10 | Aplikace musí dodržovat třívrstvou architekturu (prezentace, business logika, data) | Must Have   |
| NFR-11 | Databázové operace musí být prováděny v rámci transakcí pro zajištění integrity dat | Should Have |