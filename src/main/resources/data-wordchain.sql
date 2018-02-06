INSERT INTO player VALUES (1, 'english@freemail.hu', 'USER', '$2a$10$8Kre7KpW8MXDqWJ3uh/a.eNiQAoeT0ONaZtg38UDm.Lz4U7o.LZSm', 'not_Shakespeare');
INSERT INTO player VALUES (2, 'berci@freemail.hu', 'USER', '$2a$10$8Kre7KpW8MXDqWJ3uh/a.eNiQAoeT0ONaZtg38UDm.Lz4U7o.LZSm', 'Farkas_Bertalan');

SELECT pg_catalog.setval('player_id_seq', 2, true);
