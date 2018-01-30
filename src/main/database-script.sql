create table Test.employee(
         	id int(11) NOT NULL AUTO_INCREMENT,
         	name varchar(255) NOT NULL,
         	age int(3) default null,
         	salary decimal(28,10) NOT NULL,
         	start_date datetime not null,
         	company_id int(11) not null,
         	primary key(id)
)

create table Test.company(
         	id int(11) NOT NULL AUTO_INCREMENT,
         	name varchar(255) NOT null,
         	staff_count int(3) not null default 0,
         	primary key(id)
)

