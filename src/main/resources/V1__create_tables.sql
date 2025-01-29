create table if not exists spring_resource (
    file_name  varchar(500) not null primary key,
    file_data  bytea not null
)
