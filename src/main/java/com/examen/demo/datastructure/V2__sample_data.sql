-- Donateurs
INSERT INTO donor (email, full_name) VALUES
                                         ('donateur1@hei.school', 'Jean Dupont'),
                                         ('donateur2@hei.school', 'Marie Martin');

-- Bénéficiaires
INSERT INTO beneficiary (email, full_name) VALUES
                                               ('benef1@hei.school', 'Lucie Bernard'),
                                               ('benef2@hei.school', 'Thomas Petit');

-- Paiements
INSERT INTO payment (id, date, amount, method, status) VALUES
                                                           ('vola_pay_1', NOW(), 100.00, 'CARD', 'SUCCEEDED'),
                                                           ('vola_pay_2', NOW(), 50.00, 'MOBILE_MONEY', 'SUCCEEDED');

-- Dons
INSERT INTO donation (donor_email, payment_id) VALUES
                                                   ('donateur1@hei.school', 'vola_pay_1'),
                                                   ('donateur2@hei.school', 'vola_pay_2');

-- Aides
INSERT INTO help (beneficiary_email, payment_id, accident_description) VALUES
                                                                           ('benef1@hei.school', 'vola_pay_1', 'Accident de voiture'),
                                                                           ('benef2@hei.school', 'vola_pay_2', 'Frais médicaux urgents');