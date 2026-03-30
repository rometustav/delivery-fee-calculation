-- Base fee rules
INSERT INTO base_fee_rule (city, vehicle_type, fee, valid_from) VALUES ('TALLINN', 'CAR', 4.0, '2026-01-01T00:00:00');
INSERT INTO base_fee_rule (city, vehicle_type, fee, valid_from) VALUES ('TALLINN', 'SCOOTER', 3.5, '2026-01-01T00:00:00');
INSERT INTO base_fee_rule (city, vehicle_type, fee, valid_from) VALUES ('TALLINN', 'BIKE', 3.0, '2026-01-01T00:00:00');
INSERT INTO base_fee_rule (city, vehicle_type, fee, valid_from) VALUES ('TARTU', 'CAR', 3.5, '2026-01-01T00:00:00');
INSERT INTO base_fee_rule (city, vehicle_type, fee, valid_from) VALUES ('TARTU', 'SCOOTER', 3.0, '2026-01-01T00:00:00');
INSERT INTO base_fee_rule (city, vehicle_type, fee, valid_from) VALUES ('TARTU', 'BIKE', 2.5, '2026-01-01T00:00:00');
INSERT INTO base_fee_rule (city, vehicle_type, fee, valid_from) VALUES ('PARNU', 'CAR', 3.0, '2026-01-01T00:00:00');
INSERT INTO base_fee_rule (city, vehicle_type, fee, valid_from) VALUES ('PARNU', 'SCOOTER', 2.5, '2026-01-01T00:00:00');
INSERT INTO base_fee_rule (city, vehicle_type, fee, valid_from) VALUES ('PARNU', 'BIKE', 2.0, '2026-01-01T00:00:00');

-- Extra fee rules (temperature)
INSERT INTO extra_fee_rule (vehicle_type, type, condition_min, condition_max, phenomenon_keyword, fee, valid_from) VALUES ('SCOOTER', 'TEMPERATURE', null, -10.0, null, 1.0, '2026-01-01T00:00:00');
INSERT INTO extra_fee_rule (vehicle_type, type, condition_min, condition_max, phenomenon_keyword, fee, valid_from) VALUES ('BIKE', 'TEMPERATURE', null, -10.0, null, 1.0, '2026-01-01T00:00:00');
INSERT INTO extra_fee_rule (vehicle_type, type, condition_min, condition_max, phenomenon_keyword, fee, valid_from) VALUES ('SCOOTER', 'TEMPERATURE', -10.0, 0.0, null, 0.5, '2026-01-01T00:00:00');
INSERT INTO extra_fee_rule (vehicle_type, type, condition_min, condition_max, phenomenon_keyword, fee, valid_from) VALUES ('BIKE', 'TEMPERATURE', -10.0, 0.0, null, 0.5, '2026-01-01T00:00:00');

-- Extra fee rules (wind speed)
INSERT INTO extra_fee_rule (vehicle_type, type, condition_min, condition_max, phenomenon_keyword, fee, valid_from) VALUES ('BIKE', 'WIND_SPEED', 10.0, 20.0, null, 0.5, '2026-01-01T00:00:00');
INSERT INTO extra_fee_rule (vehicle_type, type, condition_min, condition_max, phenomenon_keyword, fee, valid_from) VALUES ('BIKE', 'WIND_SPEED', 20.0, null, null, null, '2026-01-01T00:00:00');

-- Extra fee rules (phenomenon)
INSERT INTO extra_fee_rule (vehicle_type, type, condition_min, condition_max, phenomenon_keyword, fee, valid_from) VALUES ('SCOOTER', 'PHENOMENON', null, null, 'snow', 1.0, '2026-01-01T00:00:00');
INSERT INTO extra_fee_rule (vehicle_type, type, condition_min, condition_max, phenomenon_keyword, fee, valid_from) VALUES ('BIKE', 'PHENOMENON', null, null, 'snow', 1.0, '2026-01-01T00:00:00');
INSERT INTO extra_fee_rule (vehicle_type, type, condition_min, condition_max, phenomenon_keyword, fee, valid_from) VALUES ('SCOOTER', 'PHENOMENON', null, null, 'sleet', 1.0, '2026-01-01T00:00:00');
INSERT INTO extra_fee_rule (vehicle_type, type, condition_min, condition_max, phenomenon_keyword, fee, valid_from) VALUES ('BIKE', 'PHENOMENON', null, null, 'sleet', 1.0, '2026-01-01T00:00:00');
INSERT INTO extra_fee_rule (vehicle_type, type, condition_min, condition_max, phenomenon_keyword, fee, valid_from) VALUES ('SCOOTER', 'PHENOMENON', null, null, 'rain', 0.5, '2026-01-01T00:00:00');
INSERT INTO extra_fee_rule (vehicle_type, type, condition_min, condition_max, phenomenon_keyword, fee, valid_from) VALUES ('BIKE', 'PHENOMENON', null, null, 'rain', 0.5, '2026-01-01T00:00:00');
INSERT INTO extra_fee_rule (vehicle_type, type, condition_min, condition_max, phenomenon_keyword, fee, valid_from) VALUES ('SCOOTER', 'PHENOMENON', null, null, 'glaze', null, '2026-01-01T00:00:00');
INSERT INTO extra_fee_rule (vehicle_type, type, condition_min, condition_max, phenomenon_keyword, fee, valid_from) VALUES ('BIKE', 'PHENOMENON', null, null, 'glaze', null, '2026-01-01T00:00:00');
INSERT INTO extra_fee_rule (vehicle_type, type, condition_min, condition_max, phenomenon_keyword, fee, valid_from) VALUES ('SCOOTER', 'PHENOMENON', null, null, 'hail', null, '2026-01-01T00:00:00');
INSERT INTO extra_fee_rule (vehicle_type, type, condition_min, condition_max, phenomenon_keyword, fee, valid_from) VALUES ('BIKE', 'PHENOMENON', null, null, 'hail', null, '2026-01-01T00:00:00');
INSERT INTO extra_fee_rule (vehicle_type, type, condition_min, condition_max, phenomenon_keyword, fee, valid_from) VALUES ('SCOOTER', 'PHENOMENON', null, null, 'thunder', null, '2026-01-01T00:00:00');
INSERT INTO extra_fee_rule (vehicle_type, type, condition_min, condition_max, phenomenon_keyword, fee, valid_from) VALUES ('BIKE', 'PHENOMENON', null, null, 'thunder', null, '2026-01-01T00:00:00');