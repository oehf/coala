REM ----------------------------------------------------------------------------
REM Title        eHF user privileges master file
REM Version      03/2008
REM Copyright    InterComponentWare AG
REM Author       Thomas Skariah
REM Company      InterComponentWare AG
REM Description  Recreate eHF user with all privileges
REM ----------------------------------------------------------------------------


disconnect;

define adminName=@@connection.system.username@@;
define adminPass=@@connection.system.password@@;
define SID=@@database.oracle.sid@@;

define logFile=/tmp/recreate-user.log;
define datasourceUserName=@@datasource.username@@;
define datasourceUserPass=@@datasource.password@@;
define tsTemporaryData=TEMP;

spool &logFile

set verify off

@@@db.oracle10g.schema.permissions.fragment@@@

disconnect;
