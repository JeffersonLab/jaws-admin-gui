alter session set container = XEPDB1;

-- Special characters such as the ampersand will result in prompt without this directive.
SET DEFINE OFF;

-- Priorities
INSERT INTO JAWS_OWNER.PRIORITY (PRIORITY_ID, WEIGHT, NAME) VALUES (1, 1, 'P1_CRITICAL');
INSERT INTO JAWS_OWNER.PRIORITY (PRIORITY_ID, WEIGHT, NAME) VALUES (2, 2, 'P2_MAJOR');
INSERT INTO JAWS_OWNER.PRIORITY (PRIORITY_ID, WEIGHT, NAME) VALUES (3, 3, 'P3_MINOR');
INSERT INTO JAWS_OWNER.PRIORITY (PRIORITY_ID, WEIGHT, NAME) VALUES (4, 4, 'P4_INCIDENTAL');

DROP SEQUENCE JAWS_OWNER.PRIORITY_ID;
CREATE SEQUENCE JAWS_OWNER.PRIORITY_ID
    INCREMENT BY 1
    START WITH 5
    NOCYCLE
    NOCACHE
    ORDER;

-- Teams
INSERT INTO JAWS_OWNER.TEAM (TEAM_ID, NAME) VALUES (1, 'Cryo');
INSERT INTO JAWS_OWNER.TEAM (TEAM_ID, NAME) VALUES (2, 'DC Power');
INSERT INTO JAWS_OWNER.TEAM (TEAM_ID, NAME) VALUES (3, 'Facilities');
INSERT INTO JAWS_OWNER.TEAM (TEAM_ID, NAME) VALUES (4, 'Gun (CIS)');
INSERT INTO JAWS_OWNER.TEAM (TEAM_ID, NAME) VALUES (5, 'Injector');
INSERT INTO JAWS_OWNER.TEAM (TEAM_ID, NAME) VALUES (6, 'Optics');
INSERT INTO JAWS_OWNER.TEAM (TEAM_ID, NAME) VALUES (7, 'High Level Apps');
INSERT INTO JAWS_OWNER.TEAM (TEAM_ID, NAME) VALUES (8, 'I&C Hardware');
INSERT INTO JAWS_OWNER.TEAM (TEAM_ID, NAME) VALUES (9, 'Low Level Apps');
INSERT INTO JAWS_OWNER.TEAM (TEAM_ID, NAME) VALUES (10, 'Ops');
INSERT INTO JAWS_OWNER.TEAM (TEAM_ID, NAME) VALUES (11, 'RADCON');
INSERT INTO JAWS_OWNER.TEAM (TEAM_ID, NAME) VALUES (12, 'RF');
INSERT INTO JAWS_OWNER.TEAM (TEAM_ID, NAME) VALUES (13, 'SSG');
INSERT INTO JAWS_OWNER.TEAM (TEAM_ID, NAME) VALUES (14, 'Vacuum');

DROP SEQUENCE JAWS_OWNER.TEAM_ID;
CREATE SEQUENCE JAWS_OWNER.TEAM_ID
    INCREMENT BY 1
    START WITH 18
    NOCYCLE
    NOCACHE
    ORDER;

-- Categories
INSERT INTO JAWS_OWNER.SYSTEM (SYSTEM_ID, TEAM_ID, NAME) VALUES (1, 5, 'Aperture');
INSERT INTO JAWS_OWNER.SYSTEM (SYSTEM_ID, TEAM_ID, NAME) VALUES (2, 8, 'BCM');
INSERT INTO JAWS_OWNER.SYSTEM (SYSTEM_ID, TEAM_ID, NAME) VALUES (3, 13, 'BELS');
INSERT INTO JAWS_OWNER.SYSTEM (SYSTEM_ID, TEAM_ID, NAME) VALUES (4, 13, 'BLM');
INSERT INTO JAWS_OWNER.SYSTEM (SYSTEM_ID, TEAM_ID, NAME) VALUES (5, 8, 'Beam Dump');
INSERT INTO JAWS_OWNER.SYSTEM (SYSTEM_ID, TEAM_ID, NAME) VALUES (6, 2, 'Box PS');
INSERT INTO JAWS_OWNER.SYSTEM (SYSTEM_ID, TEAM_ID, NAME) VALUES (7, 8, 'BPM');
INSERT INTO JAWS_OWNER.SYSTEM (SYSTEM_ID, TEAM_ID, NAME) VALUES (8, 8, 'CAMAC');
INSERT INTO JAWS_OWNER.SYSTEM (SYSTEM_ID, TEAM_ID, NAME) VALUES (9, 8, 'Crate');
INSERT INTO JAWS_OWNER.SYSTEM (SYSTEM_ID, TEAM_ID, NAME) VALUES (10, 1, 'Cryo');
INSERT INTO JAWS_OWNER.SYSTEM (SYSTEM_ID, TEAM_ID, NAME) VALUES (11, 3, 'Demand Power');
INSERT INTO JAWS_OWNER.SYSTEM (SYSTEM_ID, TEAM_ID, NAME) VALUES (12, 4, 'Gun');
INSERT INTO JAWS_OWNER.SYSTEM (SYSTEM_ID, TEAM_ID, NAME) VALUES (13, 8, 'Harp');
INSERT INTO JAWS_OWNER.SYSTEM (SYSTEM_ID, TEAM_ID, NAME) VALUES (14, 2, 'Helicity');
INSERT INTO JAWS_OWNER.SYSTEM (SYSTEM_ID, TEAM_ID, NAME) VALUES (15, 9, 'IOC');
INSERT INTO JAWS_OWNER.SYSTEM (SYSTEM_ID, TEAM_ID, NAME) VALUES (16, 13, 'Ion Chamber');
INSERT INTO JAWS_OWNER.SYSTEM (SYSTEM_ID, TEAM_ID, NAME) VALUES (17, 3, 'LCW');
INSERT INTO JAWS_OWNER.SYSTEM (SYSTEM_ID, TEAM_ID, NAME) VALUES (18, 4, 'Laser');
INSERT INTO JAWS_OWNER.SYSTEM (SYSTEM_ID, TEAM_ID, NAME) VALUES (19, 12, 'MO');
INSERT INTO JAWS_OWNER.SYSTEM (SYSTEM_ID, TEAM_ID, NAME) VALUES (20, 9, 'Misc');
INSERT INTO JAWS_OWNER.SYSTEM (SYSTEM_ID, TEAM_ID, NAME) VALUES (21, 13, 'ODH');
INSERT INTO JAWS_OWNER.SYSTEM (SYSTEM_ID, TEAM_ID, NAME) VALUES (22, 12, 'RF');
INSERT INTO JAWS_OWNER.SYSTEM (SYSTEM_ID, TEAM_ID, NAME) VALUES (23, 12, 'RF Separators');
INSERT INTO JAWS_OWNER.SYSTEM (SYSTEM_ID, TEAM_ID, NAME) VALUES (24, 11, 'RadCon');
INSERT INTO JAWS_OWNER.SYSTEM (SYSTEM_ID, TEAM_ID, NAME) VALUES (25, 2, 'Trim');
INSERT INTO JAWS_OWNER.SYSTEM (SYSTEM_ID, TEAM_ID, NAME) VALUES (26, 2, 'Trim Rack');
INSERT INTO JAWS_OWNER.SYSTEM (SYSTEM_ID, TEAM_ID, NAME) VALUES (27, 14, 'Vacuum Pump');
INSERT INTO JAWS_OWNER.SYSTEM (SYSTEM_ID, TEAM_ID, NAME) VALUES (28, 5, 'Warm RF');

DROP SEQUENCE JAWS_OWNER.SYSTEM_ID;
CREATE SEQUENCE JAWS_OWNER.SYSTEM_ID
    INCREMENT BY 1
    START WITH 29
    NOCYCLE
    NOCACHE
    ORDER;

-- Locations
INSERT INTO JAWS_OWNER.LOCATION (LOCATION_ID, WEIGHT, PARENT_ID, NAME) VALUES (0, 0, null, 'JLAB');
INSERT INTO JAWS_OWNER.LOCATION (LOCATION_ID, WEIGHT, PARENT_ID, NAME) VALUES (1, 1, 0, 'CEBAF');
INSERT INTO JAWS_OWNER.LOCATION (LOCATION_ID, WEIGHT, PARENT_ID, NAME) VALUES (2, 2, 0, 'CHL');
INSERT INTO JAWS_OWNER.LOCATION (LOCATION_ID, WEIGHT, PARENT_ID, NAME) VALUES (3, 3, 0, 'LERF');
INSERT INTO JAWS_OWNER.LOCATION (LOCATION_ID, WEIGHT, PARENT_ID, NAME) VALUES (4, 4,  0, 'UITF');
INSERT INTO JAWS_OWNER.LOCATION (LOCATION_ID, WEIGHT, PARENT_ID, NAME) VALUES (5, 5, 1, 'Injector');
INSERT INTO JAWS_OWNER.LOCATION (LOCATION_ID, WEIGHT, PARENT_ID, NAME) VALUES (6, 6,  1, 'North Linac');
INSERT INTO JAWS_OWNER.LOCATION (LOCATION_ID, WEIGHT, PARENT_ID, NAME) VALUES (7, 7, 1, 'South Linac');
INSERT INTO JAWS_OWNER.LOCATION (LOCATION_ID, WEIGHT, PARENT_ID, NAME) VALUES (8, 8, 1, 'East Arc');
INSERT INTO JAWS_OWNER.LOCATION (LOCATION_ID, WEIGHT, PARENT_ID, NAME) VALUES (9, 9, 1, 'West Arc');
INSERT INTO JAWS_OWNER.LOCATION (LOCATION_ID, WEIGHT, PARENT_ID, NAME) VALUES (10, 10, 1, 'BSY');
INSERT INTO JAWS_OWNER.LOCATION (LOCATION_ID, WEIGHT, PARENT_ID, NAME) VALUES (11, 11, 1, 'HallA');
INSERT INTO JAWS_OWNER.LOCATION (LOCATION_ID, WEIGHT, PARENT_ID, NAME) VALUES (12, 12, 1, 'HallB');
INSERT INTO JAWS_OWNER.LOCATION (LOCATION_ID, WEIGHT, PARENT_ID, NAME) VALUES (13, 13, 1, 'HallC');
INSERT INTO JAWS_OWNER.LOCATION (LOCATION_ID, WEIGHT, PARENT_ID, NAME) VALUES (14, 14, 1, 'HallD');
INSERT INTO JAWS_OWNER.LOCATION (LOCATION_ID, WEIGHT, PARENT_ID, NAME) VALUES (15, 15, 5, '1D');
INSERT INTO JAWS_OWNER.LOCATION (LOCATION_ID, WEIGHT, PARENT_ID, NAME) VALUES (16, 16, 5, '2D');
INSERT INTO JAWS_OWNER.LOCATION (LOCATION_ID, WEIGHT, PARENT_ID, NAME) VALUES (17, 17, 5, '3D');
INSERT INTO JAWS_OWNER.LOCATION (LOCATION_ID, WEIGHT, PARENT_ID, NAME) VALUES (18, 18, 5, '4D');
INSERT INTO JAWS_OWNER.LOCATION (LOCATION_ID, WEIGHT, PARENT_ID, NAME) VALUES (19, 19, 5, '5D');
INSERT INTO JAWS_OWNER.LOCATION (LOCATION_ID, WEIGHT, PARENT_ID, NAME) VALUES (20, 20, 8, 'ARC1');
INSERT INTO JAWS_OWNER.LOCATION (LOCATION_ID, WEIGHT, PARENT_ID, NAME) VALUES (21, 21, 8, 'ARC3');
INSERT INTO JAWS_OWNER.LOCATION (LOCATION_ID, WEIGHT, PARENT_ID, NAME) VALUES (22, 22, 8, 'ARC5');
INSERT INTO JAWS_OWNER.LOCATION (LOCATION_ID, WEIGHT, PARENT_ID, NAME) VALUES (23, 23, 8, 'ARC7');
INSERT INTO JAWS_OWNER.LOCATION (LOCATION_ID, WEIGHT, PARENT_ID, NAME) VALUES (24, 24, 8, 'ARC9');
INSERT INTO JAWS_OWNER.LOCATION (LOCATION_ID, WEIGHT, PARENT_ID, NAME) VALUES (25, 25, 9, 'ARC2');
INSERT INTO JAWS_OWNER.LOCATION (LOCATION_ID, WEIGHT, PARENT_ID, NAME) VALUES (26, 26, 9, 'ARC4');
INSERT INTO JAWS_OWNER.LOCATION (LOCATION_ID, WEIGHT, PARENT_ID, NAME) VALUES (27, 27, 9, 'ARC6');
INSERT INTO JAWS_OWNER.LOCATION (LOCATION_ID, WEIGHT, PARENT_ID, NAME) VALUES (28, 28, 9, 'ARC8');
INSERT INTO JAWS_OWNER.LOCATION (LOCATION_ID, WEIGHT, PARENT_ID, NAME) VALUES (29, 29, 9, 'ARCA');
INSERT INTO JAWS_OWNER.LOCATION (LOCATION_ID, WEIGHT, PARENT_ID, NAME) VALUES (30, 30, 10, 'BSY Dump');
INSERT INTO JAWS_OWNER.LOCATION (LOCATION_ID, WEIGHT, PARENT_ID, NAME) VALUES (31, 31, 10, 'BSY2');
INSERT INTO JAWS_OWNER.LOCATION (LOCATION_ID, WEIGHT, PARENT_ID, NAME) VALUES (32, 32, 10, 'BSY4');
INSERT INTO JAWS_OWNER.LOCATION (LOCATION_ID, WEIGHT, PARENT_ID, NAME) VALUES (33, 33, 10, 'BSY6');
INSERT INTO JAWS_OWNER.LOCATION (LOCATION_ID, WEIGHT, PARENT_ID, NAME) VALUES (34, 34, 10, 'BSY8');
INSERT INTO JAWS_OWNER.LOCATION (LOCATION_ID, WEIGHT, PARENT_ID, NAME) VALUES (35, 35, 10, 'BSYA');
INSERT INTO JAWS_OWNER.LOCATION (LOCATION_ID, WEIGHT, PARENT_ID, NAME) VALUES (36, 36, 6, 'Linac1');
INSERT INTO JAWS_OWNER.LOCATION (LOCATION_ID, WEIGHT, PARENT_ID, NAME) VALUES (37, 37, 6, 'Linac3');
INSERT INTO JAWS_OWNER.LOCATION (LOCATION_ID, WEIGHT, PARENT_ID, NAME) VALUES (38, 38, 6, 'Linac5');
INSERT INTO JAWS_OWNER.LOCATION (LOCATION_ID, WEIGHT, PARENT_ID, NAME) VALUES (39, 39, 6, 'Linac7');
INSERT INTO JAWS_OWNER.LOCATION (LOCATION_ID, WEIGHT, PARENT_ID, NAME) VALUES (40, 40, 6, 'Linac9');
INSERT INTO JAWS_OWNER.LOCATION (LOCATION_ID, WEIGHT, PARENT_ID, NAME) VALUES (41, 41, 7, 'Linac2');
INSERT INTO JAWS_OWNER.LOCATION (LOCATION_ID, WEIGHT, PARENT_ID, NAME) VALUES (42, 42, 7, 'Linac4');
INSERT INTO JAWS_OWNER.LOCATION (LOCATION_ID, WEIGHT, PARENT_ID, NAME) VALUES (43, 43, 7, 'Linac6');
INSERT INTO JAWS_OWNER.LOCATION (LOCATION_ID, WEIGHT, PARENT_ID, NAME) VALUES (44, 44, 7, 'Linac8');
INSERT INTO JAWS_OWNER.LOCATION (LOCATION_ID, WEIGHT, PARENT_ID, NAME) VALUES (45, 45, 1, 'MCC');
INSERT INTO JAWS_OWNER.LOCATION (LOCATION_ID, WEIGHT, PARENT_ID, NAME) VALUES (46, 46, 1, 'ESR');

DROP SEQUENCE JAWS_OWNER.LOCATION_ID;
CREATE SEQUENCE JAWS_OWNER.LOCATION_ID
    INCREMENT BY 1
    START WITH 47
    NOCYCLE
    NOCACHE
    ORDER;