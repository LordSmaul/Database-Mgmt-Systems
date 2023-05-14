create database dvdrental;

use dvdrental;

show tables;

-- ------------------ branch --------------------
create table BRANCH
(street varchar(20),
city varchar(20),
-- checks that state abbreviation is one of the US states
state varchar(2) check (state in ('AK', 'AL', 'AR', 'AZ', 'CA', 'CO', 'CT', 'DE', 'FL', 'GA', 'HI', 'IA', 'ID', 'IL', 'IN', 'KS', 'KY', 'LA', 'MA', 'MD', 'ME', 'MI', 'MN', 'MO', 'MS', 'MT', 'NC', 'ND', 'NE', 'NH', 'NJ', 'NM', 'NV', 'NY', 'OH', 'OK', 'OR', 'PA', 'RI', 'SC', 'SD', 'TN', 'TX', 'UT', 'VA', 'VT', 'WA', 'WI', 'WV', 'WY')),
zip numeric(5,0),
phone_no numeric (12,0) not null,
branch_id numeric(5,0), -- uniquely identifies each branch
primary key (branch_no)
);

-- ------------------ staff --------------------
create table STAFF
(
staff_no numeric(5,0), -- uniquely identifies each employee
branch_no numeric(5,0), -- identifies which branch they work at
first_name varchar(20),
last_name varchar(20),
position varchar(20), -- position at branch
salary numeric (8,2),
primary key (staff_no),
foreign key (branch_no) references BRANCH(branch_no) on delete cascade
);

-- ------------------ branch stock --------------------
create table BRANCH_STOCK
(
catalog_no numeric(10,0), -- uniquely identifies each item in the store
branch_no numeric(5,0), -- identifies the branch where the stock is held
primary key(catalog_no),
foreign key(branch_no) references BRANCH(branch_no) on delete cascade
);

-- ------------------ DVD --------------------
create table DVD
(
catalog_no numeric(10,0), -- uniquely identifies dvd in store catalog
dvd_no numeric (20,0), -- uniquely identifies each individual DVD
title varchar(50),
category varchar(10),
daily_rental numeric(3, 2),
status numeric(1,0), -- 1 = available, 0 = checked out
director varchar(20), -- "list" of diretor(s) names
actors varchar(200), -- "list" of actor(s) names
primary key(catalog_no, dvd_no),
foreign key (catalog_no) references BRANCH_STOCK(catalog_no) on delete cascade,
foreign key (dvd_no) references COPY(dvd_no) on delete cascade
);

-- ------------------ copy --------------------
create table COPY
(
dvd_no numeric(20,0), -- uniquely identifies each DVD
catalog_no numeric(10,0), -- identifies DVD within store catalog
copy_condition varchar(200), -- any comments on the quality of the copy
primary key(dvd_no),
foreign key(catalog_no) references BRANCH_STOCK(catalog_no) on delete cascade
);

-- ------------------ rental --------------------
create table RENTAL
(
rental_no numeric(10,0), -- uniquely identifies each rental
member_no numeric(10,0), -- identifies member of rental
first_name varchar(20), -- member first name
last_name varchar(20), -- member last name
dvd_no numeric(20,0), -- DVD being rented
title varchar(50), -- DVD title
daily_rental numeric(3,2), -- daily rental cost of DVD
date_rented datetime,
dete_returned datetime,
primary key(rental_no),
foreign key(dvd_no) references COPY(dvd_no) on delete cascade,
foreign key(title) references DVD(title) on delete cascade,
foreign key(daily_rental) references DVD(daily_rental) on delete cascade,
foreign key(member_no) references MEMBER(member_no) on delete cascade
);

-- ------------------ member --------------------
create table MEMBER
(
member_no numeric(10,0), -- uniquely identifies each member
first_name varchar(20),
last_name varchar(20),
address varchar(100), -- address stored in one long string rather than separate variables
date_registered date,
expiration_date date,
current_rentals varchar(100), -- "list" of current rentals
primary key(member_no)
);
