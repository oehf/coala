create or replace procedure grantTo(pstrOwner    in varchar2,
                                    pstrUser     in varchar2,
                                    pfSelectOnly in boolean := false) as
  lstrAccessRights varchar2(40);
  lstrStmt         varchar2(500);
begin
  for obj in (
    select o.object_name, o.object_type
    from all_objects o
    where o.owner = upper(pstrOwner)
      and o.object_type in ('TABLE', 'SEQUENCE', 'VIEW')
      and o.object_name not like 'BIN%'
      and not exists(
        select -777 from all_external_tables ex_t
        where ex_t.owner = upper(pstrOwner)
          and ex_t.table_name = o.object_name
      )
  ) loop
    if (obj.object_name <> 'REFCUSTOMERSTATISTICS_CURRENT') then
      lstrAccessRights := 'select, insert, update, delete';
      lstrStmt := 'grant ' || lstrAccessRights || ' on ' || obj.object_name || ' to ' || pstrUser;
      dbms_output.put_line(lstrStmt || ';');
      execute immediate lstrStmt;
    end if;
  end loop;

  dbms_output.put_line('Access to ' || pstrOwner || ' tables granted to user ' || pstrUser);
end;
/

