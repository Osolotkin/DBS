--====================================--
--============= tables ===============--
--====================================--

-- doctor
CREATE TABLE doctor (
    id int NOT NULL IDENTITY(1,1),
    first_name varchar(255) NOT NULL,
    last_name varchar(255) NOT NULL,
    
    CONSTRAINT doctor_pk PRIMARY KEY (id)
);

-- drug
CREATE TABLE drug (
    id int NOT NULL IDENTITY(1,1),
    name varchar(255)  NOT NULL,
    description text  NOT NULL,
    quantity int  NOT NULL DEFAULT 0,
    
    CONSTRAINT drug_pk PRIMARY KEY  (id)
);

-- pet
CREATE TABLE pet (
    id int  NOT NULL IDENTITY(1,1),
    name varchar(255) NOT NULL,
    type varchar(255) NOT NULL,
    owner_id int  NOT NULL,
    
    CONSTRAINT pet_pk PRIMARY KEY (id)
);

-- pet_owner
CREATE TABLE pet_owner (
    id int  NOT NULL IDENTITY(1,1),
    first_name varchar(255) NOT NULL,
    last_name varchar(255) NOT NULL,
    
    CONSTRAINT pet_owner_pk PRIMARY KEY  (id)
);

-- procedure
CREATE TABLE "procedure" (
    id int NOT NULL IDENTITY(1,1),
    name varchar(255) NOT NULL,
    description text NOT NULL,
    duration int NOT NULL DEFAULT 0, -- duration is int, as there isn't time type that can cover interval from few minutes to few days, at least i haven't found
    price int NOT NULL DEFAULT 0, -- price is int, last two digits refers to the decimal part of a value
    
    CONSTRAINT procedure_pk PRIMARY KEY (id)
);

-- procedure_drugs
CREATE TABLE procedure_drug (
    drug_id int  NOT NULL,
    procedure_id int  NOT NULL,
    
    CONSTRAINT procedure_drugs_pk PRIMARY KEY  (drug_id,procedure_id)
);

-- treatment
CREATE TABLE treatment (
    pet_id int  NOT NULL,
    doctor_id int  NOT NULL,
    procedure_id int  NOT NULL,
    
    CONSTRAINT treatment_pk PRIMARY KEY  (pet_id,doctor_id,procedure_id)
);

--====================================--
--========== foreign keys ============--
--====================================--

-- Reference: doctor_treatment (table: treatment)
ALTER TABLE treatment ADD CONSTRAINT doctor_treatment
    FOREIGN KEY (doctor_id)
    REFERENCES doctor (id)
	ON DELETE CASCADE
    ON UPDATE CASCADE;

-- Reference: drug_procedure_drugs (table: procedure_drugs)
ALTER TABLE procedure_drug ADD CONSTRAINT drug_procedure_drug
    FOREIGN KEY (drug_id)
    REFERENCES drug (id)
	ON DELETE CASCADE
    ON UPDATE CASCADE;

-- Reference: pet_pet_owner (table: pet)
ALTER TABLE pet ADD CONSTRAINT pet_pet_owner
    FOREIGN KEY (owner_id)
    REFERENCES pet_owner (id)
	ON DELETE NO ACTION -- default has to be 0, it has to refer to organisation itself, as there could be pet's without owners, but has to be someone to be written into the final bill 
    ON UPDATE CASCADE;

-- Reference: procedure_drugs_procedure (table: procedure_drugs)
ALTER TABLE procedure_drug ADD CONSTRAINT procedure_drug_procedure
    FOREIGN KEY (procedure_id)
    REFERENCES "procedure" (id)
	ON DELETE CASCADE
    ON UPDATE CASCADE;

-- Reference: procedure_treatment (table: treatment)
ALTER TABLE treatment ADD CONSTRAINT procedure_treatment
    FOREIGN KEY (procedure_id)
    REFERENCES "procedure" (id)
	ON DELETE CASCADE
    ON UPDATE CASCADE;

-- Reference: treatment_pet (table: treatment)
ALTER TABLE treatment ADD CONSTRAINT treatment_pet
    FOREIGN KEY (pet_id)
    REFERENCES pet (id)
	ON DELETE CASCADE
    ON UPDATE CASCADE;

--====================================--
--=========== Other Stuff ============--
--====================================--

-- Including organisation as a default pet owner at index 0
SET IDENTITY_INSERT [pet_owner] ON

INSERT INTO pet_owner (first_name, last_name, id)
VALUES ('Our', 'Company', 0);

SET IDENTITY_INSERT [pet_owner] OFF

-- As auto increment doesn't allow to set default, so had to create the trigger
GO
CREATE TRIGGER [pet_owner_set_default]
ON [pet_owner]
FOR DELETE
AS
BEGIN
	DECLARE @id int
	SELECT @id = id from deleted

	SET IDENTITY_INSERT [pet_owner] ON
	
	UPDATE pet
	SET owner_id = 0
	WHERE pet.owner_id IN (@id);

	SET IDENTITY_INSERT [pet_owner] OFF
END
GO