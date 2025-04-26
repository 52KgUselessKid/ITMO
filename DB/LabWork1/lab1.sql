SET client_encoding = 'UTF8';

CREATE TABLE companies (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description TEXT
);

CREATE TABLE persons (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    role VARCHAR(50),
    company_id INTEGER REFERENCES companies(id)
);

CREATE TABLE fates (
id SERIAL PRIMARY KEY,
person_id INTEGER NOT NULL REFERENCES persons(id) ON DELETE CASCADE,
whatHappens TEXT NOT NULL,
UNIQUE (person_id)
);

CREATE TABLE projects (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description TEXT,
    start_date DATE,
    company_id INTEGER REFERENCES companies(id)
);

CREATE TABLE contracts (
    id SERIAL PRIMARY KEY,
    project_id INTEGER REFERENCES projects(id),
    person_id INTEGER REFERENCES persons(id),
    terms TEXT,
    price NUMERIC(10,2),
    UNIQUE (project_id, person_id)
);

CREATE TABLE requirements (
    id SERIAL PRIMARY KEY,
    project_id INTEGER REFERENCES projects(id),
    description TEXT NOT NULL,
    is_paid BOOLEAN DEFAULT FALSE,
    deadline DATE
);

CREATE TABLE incidents (
    id SERIAL PRIMARY KEY,
    project_id INTEGER REFERENCES projects(id),
    type VARCHAR(50) NOT NULL,
    description TEXT,
    date DATE NOT NULL
);

CREATE TABLE shifts (
    id SERIAL PRIMARY KEY,
    person_id INTEGER REFERENCES persons(id),
    project_id INTEGER REFERENCES projects(id),
    hours NUMERIC(5,2) NOT NULL,
    date DATE NOT NULL,
    is_overtime BOOLEAN DEFAULT FALSE
);

CREATE TABLE project_team (
    id SERIAL PRIMARY KEY,
    project_id INTEGER NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    person_id INTEGER NOT NULL REFERENCES persons(id) ON DELETE CASCADE,
    role_in_project VARCHAR(50),
    join_date DATE DEFAULT CURRENT_DATE,
    UNIQUE (project_id, person_id)
);

INSERT INTO companies (name, description) VALUES 
('ИнДжин', 'Компания-заказчик, владелец Парка юрского периода'),
('Парк юрского периода', 'Место работы Недри');

INSERT INTO persons (name, role, company_id) VALUES 
('Недри', 'Разработчик', 2),
('Хэммонд', 'Представитель заказчика', 1);

INSERT INTO fates (person_id, whatHappens) VALUES 
(1, 'Ну уволится походу'),
(2, 'Денюжки получит');

INSERT INTO projects (name, description, start_date, company_id) VALUES 
('Система для Парка юрского периода', 'Разработка системы управления парком', '2025-01-01', 1);

INSERT INTO contracts (project_id, person_id, terms, price) VALUES 
(1, 1, 'Разработка и поддержка системы', 100000);

INSERT INTO requirements (project_id, description, is_paid, deadline) VALUES 
(1, 'Модификация системы по новым требованиям', FALSE, '2025-11-30');

INSERT INTO incidents (project_id, type, description, date) VALUES 
(1, 'Письмо клиентам', 'Объявление Недри ненадежным', '2025-11-15'),
(1, 'Угроза иска', 'Угроза судебным иском за невыполнение требований', '2025-11-10');

INSERT INTO shifts (person_id, project_id, hours, date, is_overtime) VALUES 
(1, 1, 8, '2025-11-20', FALSE),
(1, 1, 4.5, '2025-11-21', TRUE);

INSERT INTO project_team (project_id, person_id, role_in_project) VALUES 
(1, 1, 'Архитектор системы'),
(1, 2, 'Биологический эксперт');