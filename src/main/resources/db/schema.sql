CREATE TABLE ingredients (
    id INTEGER NOT NULL PRIMARY KEY,
    name TEXT NOT NULL,
    alpha_acid_content NUMERIC,
    origin text,
    attenuation NUMERIC,
    yeast_species INTEGER,
    optimal_temperature_low INTEGER,
    optimal_temperature_high INTEGER,
    alcohol_tolerance INTEGER,
    efficiency NUMERIC,
    color_influence NUMERIC,
    protein_addition TEXT
);

CREATE TABLE categories (
    id INTEGER NOT NULL PRIMARY KEY,
    name TEXT NOT NULL
);

CREATE TABLE flavors (
    id INTEGER NOT NULL PRIMARY KEY,
    name text NOT NULL,
    value INTEGER NOT NULL
);

CREATE VIEW v_flavors as
    select 
        id, 
        id/1000000 as category_id,
        id/100000 as subcategory_id,
        id/1000 as ingredient_id, 
        iif(id/100 % 10==0, 'std', 'note') as type,
        name, 
        value 
    from flavors;
