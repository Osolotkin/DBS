-- pet owners
INSERT INTO pet_owner (first_name, last_name)
VALUES
    ('Segal',       'Telesphore'),
    ('Dominique',   'Traverse'),
    ('Tamara',      'Simmons'),
    ('Humphrey',    'Jenkins'),
    ('Efim',        'Vasiliev'),
    ('Zanuda',      'Svatagra'),
    ('Roadin',      'House'),
    ('Bogdan',      'Vavil'),
    ('Iluka',       'Madinsk'),
    ('Krabislav',   'Ionasii');

-- pets
INSERT INTO pet (name, type, owner_id) 
VALUES 
    ('Ramona',      'cat',      10),
    ('Waffle',      'hare',     2),
    ('Stinker',     'dog',      3),
    ('Harper',      'parrot',   5),
    ('Pandora',     'dog',      6),
    ('Clifford',    'pig',      4),
    ('Sabbath',     'turtle',   9),
    ('Rasputin',    'cat',      8),
    ('Ramona',      'dog',      6),
    ('Malloc',      'rabbit',   7),
    ('Ferrbi',      'cat',      1),
    ('Lizzina',     'pig',      8),
    ('Kolaii',      'rabbit',   9);
    
-- doctors
INSERT INTO doctor (first_name, last_name)
VALUES
    ('Gwynn',       'Nevitt'),
    ('Baha',        'Demir'),
    ('Hasnaa',      'Okonjo'),
    ('Tercero',     'Bracero'),
    ('Vikentiy',    'Popo'),
    ('Yao',         'Qingshan'),
    ('Sun',         'Jae-Hwa'),
    ('Yamagata',    'Torvald'),
    ('Emlin',       'Weiner'),
    ('Federigo',    'Ferrari');
    
-- drugs
INSERT INTO drug (name, description, quantity)
VALUES
    (
        'NIACOR',      
        'Each NIACOR® (niacin tablets) Tablet, for oral administration, contains 500 mg of nicotinic acid. In addition, each tablet contains the following inactive ingredients: croscarmellose sodium, hydrogenated vegetable oil, magnesium stearate and microcrystalline cellulose.',
        10
    ),
    (
        'LODOSYN',        
        'Lodosyn (carbidopa) is an inhibitor of aromatic amino acid decarboxylation used with levodopa to treat the stiffness, tremors, spasms, and poor muscle control of Parkinson''s disease. Lodosyn and levodopa are also used to treat the same muscular conditions when caused by drugs such as chlorpromazine, fluphenazine, perphenazine, and others. Levodopa is turned into dopamine in the body.',
        13
    ),
    (
        'COGNEX',
        'Tacrine hydrochloride is a white solid and is freely soluble in distilled water, 0.1N hydrochloric acid, acetate buffer (pH 4.0), phosphate buffer (pH 7.0 to 7.4), methanol, dimethylsulfoxide (DMSO), ethanol, and propylene glycol. The compound is sparingly soluble in linoleic acid and PEG 400.',
        14
    ),
    (
        'QINLOCK',
        'Ripretinib is used to treat tumors of the stomach and intestines. Ripretinib is for use in adults who have already been treated with at least 3 other cancer medicines. Ripretinib may also be used for purposes not listed in this medication guide.',
        88
    ),
    (
        'VABOMERE',    
        'Vabomere (meropenem and vaborbactam) for injection is a combination of a penem antibacterial and a beta-lactamase inhibitor, indicated for the treatment of patients 18 years and older with complicated urinary tract infections (cUTI) including pyelonephritis caused by designated susceptible bacteria.',
        76
    ),
    (
        'ACCOLATE',         
        'Accolate is a prescription medicine used to treat the symptoms of Asthma. Accolate may be used alone or with other medications. Accolate belongs to a class of drugs called Leukotriene Receptor Antagonists. It is not known if Accolate is safe and effective in children younger than 5 years of age.',
        66
    ),
    (
        'RECLAST',
        'Reclast is used to treat osteoporosis caused by menopause, steroid use, or gonadal failure. This medicine is for use when you have a high risk of bone fracture due to osteoporosis. Reclast is also used to treat Paget''s disease of bone.',
        9
    ),
    (
        'KYNMOBI',
        'Kynmobi is a prescription medicine used to treat short-term (acute), intermittent “off” episodes in people with Parkinson’s disease (PD). It is not known if Kynmobi is safe and effective in children.',
        17
    ),
    (
        'CABLIVI',
        'Caplacizumab is used to treat acquired thrombotic thrombocytopenic purpura (aTTP) in adults. Caplacizumab is given together with immunosuppressant medication and plasma exchange (transfusion). Caplacizumab may also be used for purposes not listed in this medication guide.',
        2
    ),
    (
        'PANCRECARB',
        'Pancrelipase is a combination of three enzymes (proteins): lipase, protease, and amylase. These enzymes are normally produced by the pancreas and are important in the digestion of fats, proteins, and sugars.',
        8
    );
    
-- procedures
INSERT INTO [procedure] (name, description, duration, price)
VALUES
    (
        'Super Duper One', 
        'Doing some magical stuff to make pet feel allright', 
        3600000, 
        66600
    ),
    (
        'Misdoubt Torque', 
        'Doing some magical stuff to make pet feel allright', 
        4200000, 
        148900
    ),
    (
        'Knight baronet', 
        'Doing some magical stuff to make pet feel allright', 
        1800000, 
        1112300
    ),
    (
        'Aberrational', 
        'Doing some magical stuff to make pet feel allright', 
        9600000, 
        2323100
    ),
    (
        'Tantalizingly', 
        'Doing some magical stuff to make pet feel allright', 
        4300000, 
        222300
    ),
    (
        'Introvenient', 
        'Doing some magical stuff to make pet feel allright', 
        5600000, 
        323200
    ),
    (
        'Nepenthes', 
        'Doing some magical stuff to make pet feel allright', 
        7800000, 
        333200
    ),
    (
        'Indictee Jellify', 
        'Doing some magical stuff to make pet feel allright',
        1100000, 
        66600
    ),
    (
        'Paramaleic Recollet', 
        'Doing some magical stuff to make pet feel allright', 
        200000, 
        66600
    ),
    (
        'Therapeutaelig', 
        'Doing some magical stuff to make pet feel allright', 
        2300000, 
        66600
    ),
    (
        'Cinnoline Adenalgia', 
        'Doing some magical stuff to make pet feel allright', 
        9900000, 
        66600
    );
    
-- procedures drugs
INSERT INTO procedure_drug (procedure_id, drug_id)
VALUES
    (1, 2),
    (1, 3),
    (2, 2),
    (2, 1),
    (2, 7),
    (3, 8),
    (4, 10),
    (4, 9),
    (6, 5),
    (7, 1),
    (8, 3),
    (8, 5),
    (8, 7),
    (8, 9),
    (9, 2),
    (10, 10),
    (10, 9);
    
-- treatments
INSERT INTO treatment (pet_id, doctor_id, procedure_id)
VALUES
    (1, 2, 1),
    (1, 1, 2),
    (2, 4, 3),
    (2, 6, 3),
    (2, 8, 3),
    (3, 9, 10),
    (4, 9, 1),
    (4, 9, 2),
    (4, 5, 5),
    (4, 8, 7),
    (5, 9, 9),
    (5, 1, 2),
    (6, 7, 5),
    (7, 8, 4),
    (8, 8, 3),
    (8, 8, 5),
    (9, 9, 1),
    (10, 9, 3),
    (11, 3, 4),
    (12, 2, 7),
    (12, 2, 3),
    (13, 1, 2);
    
    
    