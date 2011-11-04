drop user @@datasource.username@@ cascade;
create user @@datasource.username@@ identified by @@datasource.password@@ temporary tablespace @@datasource.temp.tablespace@@;

grant create procedure to @@datasource.username@@;
grant create session   to @@datasource.username@@;
grant create synonym   to @@datasource.username@@;
grant create type      to @@datasource.username@@;
grant create any table to @@datasource.username@@;
grant drop any table to @@datasource.username@@;
grant create any index to @@datasource.username@@;
grant drop any index to @@datasource.username@@;
grant alter any table to @@datasource.username@@;
grant alter any index to @@datasource.username@@;
grant select any table to @@datasource.username@@;
GRANT ANALYZE ANY TO @@datasource.username@@;


