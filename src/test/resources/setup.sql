drop function date if exists;

create function date(ts timestamp) returns date return cast(ts as date);
