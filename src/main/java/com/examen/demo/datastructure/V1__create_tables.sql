-- Table Donor (Donateur)
CREATE TABLE donor (
                       email VARCHAR(255) PRIMARY KEY,
                       full_name VARCHAR(255) NOT NULL
);

-- Table Beneficiary (Bénéficiaire)
CREATE TABLE beneficiary (
                             email VARCHAR(255) PRIMARY KEY,
                             full_name VARCHAR(255) NOT NULL
);

-- Table Payment (Paiement)
CREATE TABLE payment (
                         id VARCHAR(255) PRIMARY KEY,
                         date TIMESTAMP NOT NULL,
                         amount DECIMAL(10,2) NOT NULL,
                         method VARCHAR(50) NOT NULL,
                         status VARCHAR(20) NOT NULL
);

-- Table Donation (Don)
CREATE TABLE donation (
                          id BIGSERIAL PRIMARY KEY,
                          donor_email VARCHAR(255) NOT NULL REFERENCES donor(email),
                          payment_id VARCHAR(255) NOT NULL REFERENCES payment(id)
);

-- Table Help (Aide)
CREATE TABLE help (
                      id BIGSERIAL PRIMARY KEY,
                      beneficiary_email VARCHAR(255) NOT NULL REFERENCES beneficiary(email),
                      payment_id VARCHAR(255) NOT NULL REFERENCES payment(id),
                      accident_description TEXT NOT NULL
);