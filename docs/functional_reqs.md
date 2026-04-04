# Funkční požadavky (Functional Requirements)

## Zobrazení dat (Veřejná část)

| ID    | Požadavek                                                                                                                      | Priorita    |
|-------|--------------------------------------------------------------------------------------------------------------------------------|-------------|
| FR-01 | Systém musí zobrazit aktuální ligovou tabulku s pořadím, názvem týmu, počtem zápasů, výhrami, remízami, prohrami, skóre a body | Must Have   |
| FR-02 | Systém musí automaticky vypočítat pořadí týmů podle bodů, při shodě podle skóre, poté podle vstřelených gólů                   | Must Have   |
| FR-03 | Systém musí zobrazit seznam zápasů s možností filtrování podle odehraných a naplánovaných                                      | Must Have   |
| FR-04 | Systém musí zobrazit detail zápasu včetně výsledku a statistik                                                                 | Must Have   |
| FR-05 | Systém musí zobrazit přehlednou úvodní stránku s aktualitami soutěže                                                           | Must Have   |
| FR-06 | Systém musí zobrazit detail týmu včetně soupisky hráčů                                                                         | Must Have   |
| FR-07 | Systém musí umožnit filtrování tabulky a zápasů podle ročníku soutěže                                                          | Should Have |

## Správa dat (Admin)

| ID    | Požadavek                                                                                           | Priorita    |
|-------|-----------------------------------------------------------------------------------------------------|-------------|
| FR-08 | Systém musí umožnit adminovi spravovat zápasy (vytvořit, upravit, smazat)                           | Must Have   |
| FR-09 | Systém musí umožnit adminovi zapsat výsledek zápasu včetně volitelných událostí (sestavy, události) | Must Have   |
| FR-10 | Systém musí umožnit adminovi spravovat týmy (vytvořit, upravit, smazat)                             | Must Have   |
| FR-11 | Systém musí umožnit adminovi spravovat hráče jednotlivých týmů                                      | Must Have   |
| FR-12 | Systém musí umožnit adminovi spravovat ročníky soutěže (vytvořit, archivovat)                       | Must Have   |
| FR-13 | Systém musí umožnit adminovi spravovat univerzity a místa konání                                    | Should Have |

## Autentizace a bezpečnost

| ID    | Požadavek                                                                                   | Priorita    |
|-------|---------------------------------------------------------------------------------------------|-------------|
| FR-14 | Systém musí umožnit adminovi přihlásit se pomocí uživatelského jména a hesla                | Must Have   |
| FR-15 | Systém musí umožnit adminovi odhlásit se z dashboardu                                       | Must Have   |
| FR-16 | Systém musí automaticky odhlásit admina po stanovené době neaktivity                        | Should Have |

## Systémová logika

| ID    | Požadavek                                                                                 | Priorita    |
|-------|-------------------------------------------------------------------------------------------|-------------|
| FR-17 | Systém musí automaticky přepočítat ligovou tabulku po uložení nebo úpravě výsledku zápasu | Must Have   |
| FR-18 | Systém musí validovat vstupní data (skóre musí být celé nezáporné číslo)                  | Must Have   |
| FR-19 | Systém musí zajistit, že každý zápas bude mít přiřazené dva různé týmy                    | Must Have   |
