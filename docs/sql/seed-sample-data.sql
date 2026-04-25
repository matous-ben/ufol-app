-- UFOL sample dataset for Docker/PostgreSQL (Massive Showcase Edition - Explicit Mapping)
-- Purpose:
-- 1) wipe app data and reset IDs,
-- 2) keep admin login,
-- 3) seed massive realistic test data without relying on sequence predictions.
--
-- Run with:
--   cat docs/sql/seed-sample-data.sql | docker exec -i ufol-postgres psql -U ufoluser -d ufoldb

BEGIN;

-- Keep only admin account and force predictable identity baseline.
DO $$
    BEGIN
        IF NOT EXISTS (SELECT 1 FROM users WHERE email = 'admin@ufol.cz') THEN
            RAISE EXCEPTION 'Admin account admin@ufol.cz does not exist. Create it first via app startup.';
        END IF;

        DELETE FROM users WHERE email <> 'admin@ufol.cz';

        -- Safe because all non-admin users were deleted.
        UPDATE users SET id = 1 WHERE email = 'admin@ufol.cz';
    END $$;

-- Remove domain data and reset identity counters to start from 1.
TRUNCATE TABLE
    udalosti_zapasu,
    ucasti_v_zapasech,
    zapasy,
    registrace,
    hraci,
    tymy,
    univerzity,
    rocniky,
    mista_konani,
    typy_udalosti
    RESTART IDENTITY CASCADE;

-- Ensure users sequence is aligned after forcing admin id = 1.
SELECT setval(pg_get_serial_sequence('users', 'id'), (SELECT MAX(id) FROM users), true);

-- Event types
INSERT INTO typy_udalosti (nazev, kod) VALUES
                                           ('Gol', 'GOL'),
                                           ('Zluta karta', 'ZK'),
                                           ('Cervena karta', 'CK'),
                                           ('Asistence', 'ASIST');

-- Real venues in CZ
INSERT INTO mista_konani (nazev, ulice, mesto, psc) VALUES
                                                        ('Epet Arena', 'Milady Horakove 1066', 'Praha', '17000'),
                                                        ('Stadion Strahov', 'Vanickova 2b', 'Praha', '16900'),
                                                        ('Fortuna Arena', 'U Slavie 1540/2a', 'Praha', '10000'),
                                                        ('Doosan Arena', 'Struncovy sady 3', 'Plzen', '30100'),
                                                        ('Malovicka Arena', 'Vita Nejedleho 1591', 'Hradec Kralove', '50003'),
                                                        ('CFIG Arena', 'Sukova trida 1735', 'Pardubice', '53002'),
                                                        ('Mestsky stadion Srbska', 'Srbska 47a', 'Brno', '61200'),
                                                        ('Stadion u Nisy', 'Sokolská 274', 'Liberec', '46001'),
                                                        ('Mestsky stadion Ostrava', 'Zavodni 2891/86', 'Ostrava', '70300'),
                                                        ('Andruv stadion', 'Legionarska 1165/12', 'Olomouc', '77900'),
                                                        ('Stadion Letna Zlin', 'Tyrsovo nabrezi 4381', 'Zlin', '76001'),
                                                        ('Strelacky ostrov', 'Strelacky ostrov 3', 'Ceske Budejovice', '37001');

-- 15 Universities
INSERT INTO univerzity (nazev, zkratka, logo_file) VALUES
                                                       ('Ceske vysoke uceni technicke v Praze', 'CVUT', 'cvut.svg'),
                                                       ('Ceska zemedelska univerzita v Praze', 'CZU', 'czu.svg'),
                                                       ('Jihoceska univerzita v Ceskych Budejovicich', 'JU', 'ju.svg'),
                                                       ('Masarykova univerzita', 'MUNI', 'muni.svg'),
                                                       ('Ostravska univerzita', 'OSU', 'osu.svg'),
                                                       ('Technicka univerzita v Liberci', 'TUL', 'tul.svg'),
                                                       ('Univerzita Hradec Kralove', 'UHK', 'uhk.svg'),
                                                       ('Univerzita J. E. Purkyne v Usti nad Labem', 'UJEP', 'ujep.svg'),
                                                       ('Univerzita Karlova', 'UK', 'uk.svg'),
                                                       ('Univerzita Pardubice', 'UPCE', 'upce.svg'),
                                                       ('Univerzita Tomase Bati ve Zline', 'UTB', 'utb.svg'),
                                                       ('Vysoka skola banska - TU Ostrava', 'VSB', 'vsb.webp'),
                                                       ('Vysoka skola technicka a ekonomicka', 'VSTE', 'vste.webp'),
                                                       ('Zapadoceska univerzita v Plzni', 'ZCU', 'zcu.svg'),
                                                       ('Vysoka skola ekonomicka v Praze', 'VSE', 'vse.svg');

-- Teams
INSERT INTO tymy (nazev, aktivni, univerzita_id)
SELECT v.nazev, true, u.id
FROM (VALUES
          ('CVUT Engineers', 'CVUT'), ('CZU Farmers', 'CZU'), ('JU Pelicans', 'JU'),
          ('MUNI Wolves', 'MUNI'), ('OSU Miners', 'OSU'), ('TUL Knights', 'TUL'),
          ('UHK Lions', 'UHK'), ('UJEP Pumas', 'UJEP'), ('CUNI Kings', 'UK'),
          ('UPCE Riders', 'UPCE'), ('UTB Bats', 'UTB'), ('VSB Steel', 'VSB'),
          ('VSTE Builders', 'VSTE'), ('ZCU Sparks', 'ZCU'), ('VSE Falcons', 'VSE')
     ) AS v(nazev, zkratka)
         JOIN univerzity u ON u.zkratka = v.zkratka;

-- Seasons
INSERT INTO rocniky (nazev, rok_od, rok_do, aktivni) VALUES
                                                         ('2023/2024', 2023, 2024, false),
                                                         ('2024/2025', 2024, 2025, false),
                                                         ('2025/2026', 2025, 2026, true);

-- Players (165 entirely unique individuals)
INSERT INTO hraci (jmeno, prijmeni, datum_narozeni, foto_url) VALUES
                                                                  ('Jan', 'Novak', '2002-05-15', null), ('Petr', 'Svoboda', '2001-11-20', null), ('Lukas', 'Dvorak', '2003-02-10', null),
                                                                  ('Tomas', 'Vesely', '2002-08-30', null), ('Martin', 'Cerny', '2001-12-01', null), ('Jakub', 'Prochazka', '2002-03-03', null),
                                                                  ('Adam', 'Kolar', '2003-09-19', null), ('David', 'Kral', '2001-01-26', null), ('Ondrej', 'Marek', '2002-06-14', null),
                                                                  ('Filip', 'Pokorny', '2001-04-22', null), ('Matej', 'Sedlak', '2003-10-07', null),
                                                                  ('Daniel', 'Benes', '2002-02-18', null), ('Michal', 'Fiala', '2001-07-29', null), ('Radek', 'Horak', '2002-09-12', null),
                                                                  ('Josef', 'Urban', '2000-05-09', null), ('Pavel', 'Sykora', '2003-11-11', null), ('Roman', 'Blaha', '2001-03-08', null),
                                                                  ('Simon', 'Jelinek', '2002-12-24', null), ('Patrik', 'Tichy', '2003-05-26', null), ('Viktor', 'Soukup', '2001-08-17', null),
                                                                  ('Marek', 'Zeman', '2002-01-13', null), ('Karel', 'Vanek', '2000-12-06', null),
                                                                  ('Dominik', 'Kucera', '2003-04-21', null), ('Robin', 'Hasek', '2001-10-03', null), ('Stepan', 'Ruzicka', '2004-02-02', null),
                                                                  ('Vojtech', 'Hlavac', '2004-06-16', null), ('Erik', 'Pospisil', '2004-09-28', null), ('Rene', 'Kadlec', '2004-01-30', null),
                                                                  ('Jiri', 'Barta', '2004-07-22', null), ('Denis', 'Sramek', '2004-11-05', null), ('Krystof', 'Hruby', '2001-06-11', null),
                                                                  ('Milan', 'Stepanek', '2002-10-18', null), ('Vaclav', 'Vacek', '2003-01-29', null),
                                                                  ('Ales', 'Bily', '2001-09-09', null), ('Richard', 'Maly', '2002-04-14', null), ('Ivan', 'Hajek', '2000-08-08', null),
                                                                  ('Bohumil', 'Dolezal', '2003-03-17', null), ('Zdenek', 'Krejci', '2002-07-04', null), ('Radim', 'Mach', '2001-12-22', null),
                                                                  ('Dalibor', 'Mika', '2004-05-13', null), ('Borek', 'Stastny', '2003-08-25', null), ('Lubomir', 'Bures', '2002-11-02', null),
                                                                  ('Eduard', 'Kratochvil', '2001-02-28', null), ('Igor', 'Hruska', '2000-09-15', null),
                                                                  ('Stanislav', 'Zima', '2003-07-30', null), ('Jindrich', 'Mraz', '2002-01-15', null), ('Tomas', 'Zeleny', '2001-11-03', null),
                                                                  ('Jan', 'Vlcek', '2004-08-22', null), ('Petr', 'Kriz', '2003-12-11', null), ('Lukas', 'Polak', '2002-05-05', null),
                                                                  ('Martin', 'Smejkal', '2001-09-20', null), ('Jakub', 'Rysavy', '2004-03-18', null), ('Adam', 'Toman', '2003-07-07', null),
                                                                  ('David', 'Vagner', '2002-10-30', null), ('Ondrej', 'Macura', '2001-02-14', null),
                                                                  ('Filip', 'Valenta', '2004-06-25', null), ('Matej', 'Vavra', '2003-11-09', null), ('Daniel', 'Broz', '2002-04-01', null),
                                                                  ('Michal', 'Matuska', '2001-08-16', null), ('Radek', 'Holub', '2004-12-05', null), ('Josef', 'Janda', '2003-03-22', null),
                                                                  ('Pavel', 'Slama', '2002-09-14', null), ('Roman', 'Kocourek', '2001-01-29', null), ('Simon', 'Hrubec', '2004-07-11', null),
                                                                  ('Patrik', 'Kohout', '2003-10-27', null), ('Viktor', 'Zidek', '2002-05-18', null),
                                                                  ('Marek', 'Vitek', '2001-11-30', null), ('Karel', 'Jares', '2004-04-15', null), ('Dominik', 'Vlasak', '2003-08-08', null),
                                                                  ('Robin', 'Krizek', '2002-12-21', null), ('Stepan', 'Bartos', '2001-05-06', null), ('Vojtech', 'Pavlik', '2004-09-19', null),
                                                                  ('Erik', 'Musil', '2003-02-02', null), ('Rene', 'Havlicek', '2002-06-17', null), ('Jiri', 'Zemanek', '2001-10-10', null),
                                                                  ('Denis', 'Klement', '2004-01-25', null), ('Krystof', 'Svec', '2003-07-04', null),
                                                                  ('Milan', 'Janecek', '2002-11-18', null), ('Vaclav', 'Strnad', '2001-04-03', null), ('Ales', 'Vacek', '2004-08-28', null),
                                                                  ('Richard', 'Martinek', '2003-12-12', null), ('Ivan', 'Sova', '2002-05-27', null), ('Bohumil', 'Kovar', '2001-09-10', null),
                                                                  ('Zdenek', 'Hrdlicka', '2004-02-23', null), ('Radim', 'Ptacek', '2003-07-08', null), ('Dalibor', 'Cizek', '2002-10-21', null),
                                                                  ('Borek', 'Beran', '2001-03-07', null), ('Lubomir', 'Jelinek', '2004-06-20', null),
                                                                  ('Eduard', 'Masek', '2003-11-04', null), ('Igor', 'Koci', '2002-04-19', null), ('Stanislav', 'Zikmund', '2001-08-02', null),
                                                                  ('Jindrich', 'Hromada', '2004-12-16', null), ('Tomas', 'Vondra', '2003-03-31', null), ('Jan', 'Zboril', '2002-09-13', null),
                                                                  ('Petr', 'Kolarik', '2001-01-26', null), ('Lukas', 'Zavadil', '2004-07-10', null), ('Martin', 'Mraz', '2003-10-24', null),
                                                                  ('Jakub', 'Kucera', '2002-05-09', null), ('Adam', 'Dusek', '2001-11-22', null),
                                                                  ('David', 'Krecek', '2004-04-06', null), ('Ondrej', 'Picha', '2003-08-20', null), ('Filip', 'Zezula', '2002-12-03', null),
                                                                  ('Matej', 'Kvapil', '2001-05-17', null), ('Daniel', 'Tomanec', '2004-09-29', null), ('Michal', 'Horka', '2003-02-11', null),
                                                                  ('Radek', 'Zeman', '2002-06-26', null), ('Josef', 'Rychly', '2001-10-09', null), ('Pavel', 'Zvona', '2004-01-22', null),
                                                                  ('Roman', 'Koutny', '2003-07-06', null), ('Simon', 'Hanzal', '2002-11-19', null),
                                                                  ('Patrik', 'Kanka', '2001-04-04', null), ('Viktor', 'Votava', '2004-08-17', null), ('Marek', 'Zalesky', '2003-12-30', null),
                                                                  ('Karel', 'Kratky', '2002-05-14', null), ('Dominik', 'Zlaty', '2001-09-27', null), ('Robin', 'Pospichal', '2004-02-09', null),
                                                                  ('Stepan', 'Kysela', '2003-07-24', null), ('Vojtech', 'Vanecek', '2002-10-07', null), ('Erik', 'Zamecnik', '2001-03-22', null),
                                                                  ('Rene', 'Kostal', '2004-06-04', null), ('Jiri', 'Smetana', '2003-11-17', null),
                                                                  ('Denis', 'Kvapil', '2002-04-01', null), ('Krystof', 'Zrubec', '2001-08-14', null), ('Milan', 'Hradil', '2004-12-28', null),
                                                                  ('Vaclav', 'Maly', '2003-03-12', null), ('Ales', 'Zizka', '2002-09-25', null), ('Richard', 'Kment', '2001-01-08', null),
                                                                  ('Ivan', 'Hrncar', '2004-07-22', null), ('Bohumil', 'Zvonek', '2003-10-05', null), ('Zdenek', 'Prazak', '2002-05-19', null),
                                                                  ('Radim', 'Kouba', '2001-11-01', null), ('Dalibor', 'Zbozinek', '2004-04-15', null),
                                                                  ('Borek', 'Koudelka', '2003-08-29', null), ('Lubomir', 'Vondracek', '2002-12-12', null), ('Eduard', 'Zahrada', '2001-05-26', null),
                                                                  ('Igor', 'Hubacek', '2004-09-08', null), ('Stanislav', 'Mleziva', '2003-02-20', null), ('Jindrich', 'Zikan', '2002-06-04', null),
                                                                  ('Tomas', 'Prokop', '2001-10-18', null), ('Jan', 'Vykoukal', '2004-01-31', null), ('Petr', 'Hradecky', '2003-07-15', null),
                                                                  ('Lukas', 'Kysely', '2002-11-27', null), ('Martin', 'Zavadil', '2001-04-11', null),
                                                                  ('Jakub', 'Kren', '2004-08-24', null), ('Adam', 'Voracek', '2003-12-07', null), ('David', 'Zitek', '2002-05-21', null),
                                                                  ('Ondrej', 'Kopecky', '2001-09-03', null), ('Filip', 'Moravec', '2004-02-16', null), ('Matej', 'Turek', '2003-07-01', null),
                                                                  ('Daniel', 'Vrbka', '2002-10-14', null), ('Michal', 'Zemanek', '2001-03-28', null), ('Radek', 'Tuma', '2004-06-10', null),
                                                                  ('Josef', 'Kriz', '2003-11-23', null), ('Pavel', 'Zima', '2002-04-06', null),
                                                                  ('Roman', 'Kaspar', '2001-08-19', null), ('Simon', 'Vacek', '2004-12-02', null), ('Patrik', 'Zlesak', '2003-03-16', null),
                                                                  ('Viktor', 'Patek', '2002-09-29', null), ('Marek', 'Hrdina', '2001-01-12', null), ('Karel', 'Zoubek', '2004-07-26', null),
                                                                  ('Dominik', 'Dvoracek', '2003-10-09', null), ('Robin', 'Vlk', '2002-05-23', null), ('Stepan', 'Kucera', '2001-11-05', null),
                                                                  ('Vojtech', 'Hruska', '2004-04-18', null), ('Erik', 'Kovar', '2003-08-31', null);


-- Registrations mapped explicitly by exact name (Immune to PostgreSQL ID offsets)
INSERT INTO registrace (hrac_id, tym_id, rocnik_id)
SELECT h.id, t.id, r.id
FROM (VALUES
          ('Jan', 'Novak', 'CVUT Engineers'), ('Petr', 'Svoboda', 'CVUT Engineers'), ('Lukas', 'Dvorak', 'CVUT Engineers'),
          ('Tomas', 'Vesely', 'CVUT Engineers'), ('Martin', 'Cerny', 'CVUT Engineers'), ('Jakub', 'Prochazka', 'CVUT Engineers'),
          ('Adam', 'Kolar', 'CVUT Engineers'), ('David', 'Kral', 'CVUT Engineers'), ('Ondrej', 'Marek', 'CVUT Engineers'),
          ('Filip', 'Pokorny', 'CVUT Engineers'), ('Matej', 'Sedlak', 'CVUT Engineers'),
          ('Daniel', 'Benes', 'CZU Farmers'), ('Michal', 'Fiala', 'CZU Farmers'), ('Radek', 'Horak', 'CZU Farmers'),
          ('Josef', 'Urban', 'CZU Farmers'), ('Pavel', 'Sykora', 'CZU Farmers'), ('Roman', 'Blaha', 'CZU Farmers'),
          ('Simon', 'Jelinek', 'CZU Farmers'), ('Patrik', 'Tichy', 'CZU Farmers'), ('Viktor', 'Soukup', 'CZU Farmers'),
          ('Marek', 'Zeman', 'CZU Farmers'), ('Karel', 'Vanek', 'CZU Farmers'),
          ('Dominik', 'Kucera', 'JU Pelicans'), ('Robin', 'Hasek', 'JU Pelicans'), ('Stepan', 'Ruzicka', 'JU Pelicans'),
          ('Vojtech', 'Hlavac', 'JU Pelicans'), ('Erik', 'Pospisil', 'JU Pelicans'), ('Rene', 'Kadlec', 'JU Pelicans'),
          ('Jiri', 'Barta', 'JU Pelicans'), ('Denis', 'Sramek', 'JU Pelicans'), ('Krystof', 'Hruby', 'JU Pelicans'),
          ('Milan', 'Stepanek', 'JU Pelicans'), ('Vaclav', 'Vacek', 'JU Pelicans'),
          ('Ales', 'Bily', 'MUNI Wolves'), ('Richard', 'Maly', 'MUNI Wolves'), ('Ivan', 'Hajek', 'MUNI Wolves'),
          ('Bohumil', 'Dolezal', 'MUNI Wolves'), ('Zdenek', 'Krejci', 'MUNI Wolves'), ('Radim', 'Mach', 'MUNI Wolves'),
          ('Dalibor', 'Mika', 'MUNI Wolves'), ('Borek', 'Stastny', 'MUNI Wolves'), ('Lubomir', 'Bures', 'MUNI Wolves'),
          ('Eduard', 'Kratochvil', 'MUNI Wolves'), ('Igor', 'Hruska', 'MUNI Wolves'),
          ('Stanislav', 'Zima', 'OSU Miners'), ('Jindrich', 'Mraz', 'OSU Miners'), ('Tomas', 'Zeleny', 'OSU Miners'),
          ('Jan', 'Vlcek', 'OSU Miners'), ('Petr', 'Kriz', 'OSU Miners'), ('Lukas', 'Polak', 'OSU Miners'),
          ('Martin', 'Smejkal', 'OSU Miners'), ('Jakub', 'Rysavy', 'OSU Miners'), ('Adam', 'Toman', 'OSU Miners'),
          ('David', 'Vagner', 'OSU Miners'), ('Ondrej', 'Macura', 'OSU Miners'),
          ('Filip', 'Valenta', 'TUL Knights'), ('Matej', 'Vavra', 'TUL Knights'), ('Daniel', 'Broz', 'TUL Knights'),
          ('Michal', 'Matuska', 'TUL Knights'), ('Radek', 'Holub', 'TUL Knights'), ('Josef', 'Janda', 'TUL Knights'),
          ('Pavel', 'Slama', 'TUL Knights'), ('Roman', 'Kocourek', 'TUL Knights'), ('Simon', 'Hrubec', 'TUL Knights'),
          ('Patrik', 'Kohout', 'TUL Knights'), ('Viktor', 'Zidek', 'TUL Knights'),
          ('Marek', 'Vitek', 'UHK Lions'), ('Karel', 'Jares', 'UHK Lions'), ('Dominik', 'Vlasak', 'UHK Lions'),
          ('Robin', 'Krizek', 'UHK Lions'), ('Stepan', 'Bartos', 'UHK Lions'), ('Vojtech', 'Pavlik', 'UHK Lions'),
          ('Erik', 'Musil', 'UHK Lions'), ('Rene', 'Havlicek', 'UHK Lions'), ('Jiri', 'Zemanek', 'UHK Lions'),
          ('Denis', 'Klement', 'UHK Lions'), ('Krystof', 'Svec', 'UHK Lions'),
          ('Milan', 'Janecek', 'UJEP Pumas'), ('Vaclav', 'Strnad', 'UJEP Pumas'), ('Ales', 'Vacek', 'UJEP Pumas'),
          ('Richard', 'Martinek', 'UJEP Pumas'), ('Ivan', 'Sova', 'UJEP Pumas'), ('Bohumil', 'Kovar', 'UJEP Pumas'),
          ('Zdenek', 'Hrdlicka', 'UJEP Pumas'), ('Radim', 'Ptacek', 'UJEP Pumas'), ('Dalibor', 'Cizek', 'UJEP Pumas'),
          ('Borek', 'Beran', 'UJEP Pumas'), ('Lubomir', 'Jelinek', 'UJEP Pumas'),
          ('Eduard', 'Masek', 'CUNI Kings'), ('Igor', 'Koci', 'CUNI Kings'), ('Stanislav', 'Zikmund', 'CUNI Kings'),
          ('Jindrich', 'Hromada', 'CUNI Kings'), ('Tomas', 'Vondra', 'CUNI Kings'), ('Jan', 'Zboril', 'CUNI Kings'),
          ('Petr', 'Kolarik', 'CUNI Kings'), ('Lukas', 'Zavadil', 'CUNI Kings'), ('Martin', 'Mraz', 'CUNI Kings'),
          ('Jakub', 'Kucera', 'CUNI Kings'), ('Adam', 'Dusek', 'CUNI Kings'),
          ('David', 'Krecek', 'UPCE Riders'), ('Ondrej', 'Picha', 'UPCE Riders'), ('Filip', 'Zezula', 'UPCE Riders'),
          ('Matej', 'Kvapil', 'UPCE Riders'), ('Daniel', 'Tomanec', 'UPCE Riders'), ('Michal', 'Horka', 'UPCE Riders'),
          ('Radek', 'Zeman', 'UPCE Riders'), ('Josef', 'Rychly', 'UPCE Riders'), ('Pavel', 'Zvona', 'UPCE Riders'),
          ('Roman', 'Koutny', 'UPCE Riders'), ('Simon', 'Hanzal', 'UPCE Riders'),
          ('Patrik', 'Kanka', 'UTB Bats'), ('Viktor', 'Votava', 'UTB Bats'), ('Marek', 'Zalesky', 'UTB Bats'),
          ('Karel', 'Kratky', 'UTB Bats'), ('Dominik', 'Zlaty', 'UTB Bats'), ('Robin', 'Pospichal', 'UTB Bats'),
          ('Stepan', 'Kysela', 'UTB Bats'), ('Vojtech', 'Vanecek', 'UTB Bats'), ('Erik', 'Zamecnik', 'UTB Bats'),
          ('Rene', 'Kostal', 'UTB Bats'), ('Jiri', 'Smetana', 'UTB Bats'),
          ('Denis', 'Kvapil', 'VSB Steel'), ('Krystof', 'Zrubec', 'VSB Steel'), ('Milan', 'Hradil', 'VSB Steel'),
          ('Vaclav', 'Maly', 'VSB Steel'), ('Ales', 'Zizka', 'VSB Steel'), ('Richard', 'Kment', 'VSB Steel'),
          ('Ivan', 'Hrncar', 'VSB Steel'), ('Bohumil', 'Zvonek', 'VSB Steel'), ('Zdenek', 'Prazak', 'VSB Steel'),
          ('Radim', 'Kouba', 'VSB Steel'), ('Dalibor', 'Zbozinek', 'VSB Steel'),
          ('Borek', 'Koudelka', 'VSTE Builders'), ('Lubomir', 'Vondracek', 'VSTE Builders'), ('Eduard', 'Zahrada', 'VSTE Builders'),
          ('Igor', 'Hubacek', 'VSTE Builders'), ('Stanislav', 'Mleziva', 'VSTE Builders'), ('Jindrich', 'Zikan', 'VSTE Builders'),
          ('Tomas', 'Prokop', 'VSTE Builders'), ('Jan', 'Vykoukal', 'VSTE Builders'), ('Petr', 'Hradecky', 'VSTE Builders'),
          ('Lukas', 'Kysely', 'VSTE Builders'), ('Martin', 'Zavadil', 'VSTE Builders'),
          ('Jakub', 'Kren', 'ZCU Sparks'), ('Adam', 'Voracek', 'ZCU Sparks'), ('David', 'Zitek', 'ZCU Sparks'),
          ('Ondrej', 'Kopecky', 'ZCU Sparks'), ('Filip', 'Moravec', 'ZCU Sparks'), ('Matej', 'Turek', 'ZCU Sparks'),
          ('Daniel', 'Vrbka', 'ZCU Sparks'), ('Michal', 'Zemanek', 'ZCU Sparks'), ('Radek', 'Tuma', 'ZCU Sparks'),
          ('Josef', 'Kriz', 'ZCU Sparks'), ('Pavel', 'Zima', 'ZCU Sparks'),
          ('Roman', 'Kaspar', 'VSE Falcons'), ('Simon', 'Vacek', 'VSE Falcons'), ('Patrik', 'Zlesak', 'VSE Falcons'),
          ('Viktor', 'Patek', 'VSE Falcons'), ('Marek', 'Hrdina', 'VSE Falcons'), ('Karel', 'Zoubek', 'VSE Falcons'),
          ('Dominik', 'Dvoracek', 'VSE Falcons'), ('Robin', 'Vlk', 'VSE Falcons'), ('Stepan', 'Kucera', 'VSE Falcons'),
          ('Vojtech', 'Hruska', 'VSE Falcons'), ('Erik', 'Kovar', 'VSE Falcons')
     ) AS ta(jmeno, prijmeni, t_nazev)
         JOIN hraci h ON h.jmeno = ta.jmeno AND h.prijmeni = ta.prijmeni
         JOIN tymy t ON t.nazev = ta.t_nazev
         CROSS JOIN rocniky r;


-- Matches
INSERT INTO zapasy (rocnik_id, domaci_tym_id, hoste_tym_id, misto_konani_id, datum_cas, odehran, domaci_skore, hoste_skore)
SELECT r.id, dt.id, ht.id, mk.id, v.datum_cas, v.odehran, v.domaci_skore, v.hoste_skore
FROM (VALUES
          ('2023/2024','CVUT Engineers','CUNI Kings','Stadion Strahov',TIMESTAMP '2023-09-10 18:00:00', true, 3, 1),
          ('2023/2024','CZU Farmers','JU Pelicans','Epet Arena',TIMESTAMP '2023-09-17 18:00:00', true, 0, 2),
          ('2023/2024','MUNI Wolves','VSB Steel','Mestsky stadion Srbska',TIMESTAMP '2023-09-24 17:30:00', true, 2, 2),
          ('2023/2024','OSU Miners','TUL Knights','Mestsky stadion Ostrava',TIMESTAMP '2023-10-01 17:00:00', true, 1, 0),
          ('2023/2024','UHK Lions','CUNI Kings','Malovicka Arena',TIMESTAMP '2023-10-08 18:00:00', true, 0, 3),
          ('2023/2024','UJEP Pumas','UPCE Riders','Stadion u Nisy',TIMESTAMP '2023-10-15 16:00:00', true, 1, 1),
          ('2023/2024','UTB Bats','VSTE Builders','Stadion Letna Zlin',TIMESTAMP '2023-10-22 17:00:00', true, 4, 0),
          ('2023/2024','ZCU Sparks','VSE Falcons','Doosan Arena',TIMESTAMP '2023-10-29 18:00:00', true, 2, 3),
          ('2023/2024','CUNI Kings','CZU Farmers','Stadion Strahov',TIMESTAMP '2023-11-05 18:00:00', true, 1, 1),
          ('2023/2024','JU Pelicans','CVUT Engineers','Strelacky ostrov',TIMESTAMP '2023-11-12 16:30:00', true, 0, 2),
          ('2023/2024','CVUT Engineers','OSU Miners','Stadion Strahov',TIMESTAMP '2023-11-19 18:00:00', true, 4, 1),
          ('2023/2024','TUL Knights','UHK Lions','Stadion u Nisy',TIMESTAMP '2023-11-26 17:30:00', true, 2, 0),
          ('2023/2024','UPCE Riders','UTB Bats','CFIG Arena',TIMESTAMP '2024-03-05 17:30:00', true, 3, 2),
          ('2023/2024','VSTE Builders','ZCU Sparks','Strelacky ostrov',TIMESTAMP '2024-03-12 16:30:00', true, 1, 1),
          ('2023/2024','CUNI Kings','CVUT Engineers','Epet Arena',TIMESTAMP '2024-03-19 19:00:00', true, 2, 2),

          ('2024/2025','VSE Falcons','CZU Farmers','Fortuna Arena',TIMESTAMP '2024-09-08 18:00:00', true, 2, 0),
          ('2024/2025','JU Pelicans','OSU Miners','Strelacky ostrov',TIMESTAMP '2024-09-15 17:00:00', true, 1, 3),
          ('2024/2025','UHK Lions','VSB Steel','Malovicka Arena',TIMESTAMP '2024-09-22 18:00:00', true, 0, 0),
          ('2024/2025','MUNI Wolves','TUL Knights','Mestsky stadion Srbska',TIMESTAMP '2024-09-29 17:30:00', true, 2, 1),
          ('2024/2025','VSB Steel','VSE Falcons','Mestsky stadion Ostrava',TIMESTAMP '2024-10-06 17:30:00', true, 1, 4),
          ('2024/2025','CZU Farmers','JU Pelicans','Stadion Strahov',TIMESTAMP '2024-10-13 18:00:00', true, 2, 2),
          ('2024/2025','UPCE Riders','CVUT Engineers','CFIG Arena',TIMESTAMP '2024-10-20 18:00:00', true, 1, 3),
          ('2024/2025','CUNI Kings','ZCU Sparks','Stadion Strahov',TIMESTAMP '2024-10-27 16:30:00', true, 3, 0),
          ('2024/2025','VSTE Builders','UTB Bats','Strelacky ostrov',TIMESTAMP '2024-11-03 15:00:00', true, 0, 2),
          ('2024/2025','UJEP Pumas','MUNI Wolves','Stadion u Nisy',TIMESTAMP '2024-11-10 17:00:00', true, 1, 1),
          ('2024/2025','VSE Falcons','CVUT Engineers','Fortuna Arena',TIMESTAMP '2024-11-17 19:00:00', true, 2, 2),
          ('2024/2025','OSU Miners','CUNI Kings','Mestsky stadion Ostrava',TIMESTAMP '2024-11-24 17:30:00', true, 0, 1),
          ('2024/2025','TUL Knights','UPCE Riders','Stadion u Nisy',TIMESTAMP '2025-03-09 16:00:00', true, 1, 2),
          ('2024/2025','UTB Bats','JU Pelicans','Stadion Letna Zlin',TIMESTAMP '2025-03-16 17:00:00', true, 3, 1),
          ('2024/2025','VSB Steel','VSTE Builders','Mestsky stadion Ostrava',TIMESTAMP '2025-03-23 18:00:00', true, 2, 0),

          ('2025/2026','CVUT Engineers','CUNI Kings','Stadion Strahov',TIMESTAMP '2025-09-10 18:00:00', true, 2, 1),
          ('2025/2026','MUNI Wolves','VSE Falcons','Mestsky stadion Srbska',TIMESTAMP '2025-09-17 17:30:00', true, 3, 3),
          ('2025/2026','CZU Farmers','UPCE Riders','Epet Arena',TIMESTAMP '2025-09-24 18:00:00', true, 1, 2),
          ('2025/2026','VSTE Builders','CVUT Engineers','Strelacky ostrov',TIMESTAMP '2025-10-01 16:30:00', true, 0, 4),
          ('2025/2026','CUNI Kings','MUNI Wolves','Fortuna Arena',TIMESTAMP '2025-10-08 19:00:00', true, 1, 1),
          ('2025/2026','UPCE Riders','VSTE Builders','CFIG Arena',TIMESTAMP '2025-10-15 17:00:00', true, 2, 0),
          ('2025/2026','VSE Falcons','CZU Farmers','Epet Arena',TIMESTAMP '2025-10-22 18:00:00', true, 5, 1),
          ('2025/2026','JU Pelicans','TUL Knights','Strelacky ostrov',TIMESTAMP '2025-10-29 16:00:00', true, 1, 0),
          ('2025/2026','OSU Miners','UTB Bats','Mestsky stadion Ostrava',TIMESTAMP '2025-11-05 17:00:00', true, 2, 2),
          ('2025/2026','UJEP Pumas','ZCU Sparks','Stadion u Nisy',TIMESTAMP '2025-11-12 18:00:00', true, 1, 0),
          ('2025/2026','MUNI Wolves','UPCE Riders','Mestsky stadion Srbska',TIMESTAMP '2026-04-10 16:00:00', false, 0, 0),
          ('2025/2026','CUNI Kings','VSE Falcons','Stadion Strahov',TIMESTAMP '2026-04-17 18:00:00', false, 0, 0),
          ('2025/2026','CVUT Engineers','CZU Farmers','Epet Arena',TIMESTAMP '2026-04-24 18:30:00', false, 0, 0),
          ('2025/2026','UTB Bats','VSB Steel','Stadion Letna Zlin',TIMESTAMP '2026-05-01 17:00:00', false, 0, 0),
          ('2025/2026','UHK Lions','UJEP Pumas','Malovicka Arena',TIMESTAMP '2026-05-08 18:00:00', false, 0, 0)
     ) AS v(rocnik_nazev, domaci_tym, hoste_tym, misto_nazev, datum_cas, odehran, domaci_skore, hoste_skore)
         JOIN rocniky r ON r.nazev = v.rocnik_nazev
         JOIN tymy dt ON dt.nazev = v.domaci_tym
         JOIN tymy ht ON ht.nazev = v.hoste_tym
         JOIN mista_konani mk ON mk.nazev = v.misto_nazev;


-- Participations
INSERT INTO ucasti_v_zapasech (zapas_id, registrace_id, goly)
SELECT v.zapas_id, reg.id, v.goly
FROM (VALUES
          (1, 'Jan', 'Novak', '2023/2024', 2), (1, 'Lukas', 'Dvorak', '2023/2024', 1), (1, 'Eduard', 'Masek', '2023/2024', 1),
          (7, 'Patrik', 'Kanka', '2023/2024', 2), (7, 'Marek', 'Zalesky', '2023/2024', 1), (7, 'Dominik', 'Zlaty', '2023/2024', 1),
          (16, 'Roman', 'Kaspar', '2024/2025', 1), (16, 'Viktor', 'Patek', '2024/2025', 1),
          (20, 'Denis', 'Kvapil', '2024/2025', 1), (20, 'Simon', 'Vacek', '2024/2025', 2), (20, 'Marek', 'Hrdina', '2024/2025', 2),
          (31, 'Tomas', 'Vesely', '2025/2026', 1), (31, 'Martin', 'Cerny', '2025/2026', 1), (31, 'Stanislav', 'Zikmund', '2025/2026', 1),
          (37, 'Dominik', 'Dvoracek', '2025/2026', 3), (37, 'Stepan', 'Kucera', '2025/2026', 2), (37, 'Michal', 'Fiala', '2025/2026', 1)
     ) AS v(zapas_id, jmeno, prijmeni, rocnik_nazev, goly)
         JOIN hraci h ON h.jmeno = v.jmeno AND h.prijmeni = v.prijmeni
         JOIN rocniky r ON r.nazev = v.rocnik_nazev
         JOIN registrace reg ON reg.hrac_id = h.id AND reg.rocnik_id = r.id;


-- Match Events
INSERT INTO udalosti_zapasu (ucast_v_zapase_id, typ_udalosti_id, minuta)
SELECT uvz.id, tu.id, v.minuta
FROM (VALUES
          ('Jan', 'Novak', '2023/2024', 1, 'GOL', 12), ('Jan', 'Novak', '2023/2024', 1, 'GOL', 44),
          ('Lukas', 'Dvorak', '2023/2024', 1, 'GOL', 78), ('Eduard', 'Masek', '2023/2024', 1, 'GOL', 33),
          ('Jan', 'Novak', '2023/2024', 1, 'ZK', 88), ('Patrik', 'Kanka', '2023/2024', 7, 'GOL', 5),
          ('Patrik', 'Kanka', '2023/2024', 7, 'GOL', 19), ('Marek', 'Zalesky', '2023/2024', 7, 'GOL', 60),
          ('Dominik', 'Zlaty', '2023/2024', 7, 'GOL', 85), ('Roman', 'Kaspar', '2024/2025', 16, 'GOL', 45),
          ('Viktor', 'Patek', '2024/2025', 16, 'GOL', 90), ('Denis', 'Kvapil', '2024/2025', 20, 'GOL', 14),
          ('Simon', 'Vacek', '2024/2025', 20, 'GOL', 22), ('Simon', 'Vacek', '2024/2025', 20, 'GOL', 39),
          ('Marek', 'Hrdina', '2024/2025', 20, 'GOL', 55), ('Marek', 'Hrdina', '2024/2025', 20, 'GOL', 81),
          ('Tomas', 'Vesely', '2025/2026', 31, 'GOL', 20), ('Stanislav', 'Zikmund', '2025/2026', 31, 'GOL', 41),
          ('Martin', 'Cerny', '2025/2026', 31, 'GOL', 89), ('Tomas', 'Vesely', '2025/2026', 31, 'ZK', 25),
          ('Dominik', 'Dvoracek', '2025/2026', 37, 'GOL', 8), ('Dominik', 'Dvoracek', '2025/2026', 37, 'GOL', 28),
          ('Stepan', 'Kucera', '2025/2026', 37, 'GOL', 35), ('Michal', 'Fiala', '2025/2026', 37, 'GOL', 44),
          ('Dominik', 'Dvoracek', '2025/2026', 37, 'GOL', 67), ('Stepan', 'Kucera', '2025/2026', 37, 'GOL', 82),
          ('Michal', 'Fiala', '2025/2026', 37, 'CK', 50)
     ) AS v(jmeno, prijmeni, rocnik_nazev, zapas_id, kod_udalosti, minuta)
         JOIN hraci h ON h.jmeno = v.jmeno AND h.prijmeni = v.prijmeni
         JOIN rocniky r ON r.nazev = v.rocnik_nazev
         JOIN registrace reg ON reg.hrac_id = h.id AND reg.rocnik_id = r.id
         JOIN ucasti_v_zapasech uvz ON uvz.registrace_id = reg.id AND uvz.zapas_id = v.zapas_id
         JOIN typy_udalosti tu ON tu.kod = v.kod_udalosti;

COMMIT;
