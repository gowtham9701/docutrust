create table if not exists documents (
    id uuid primary key,
    file_name varchar(255) not null,
    content varchar(10000) not null,
    status varchar(32) not null,
    submitted_at timestamp with time zone not null
);

create table if not exists findings (
    id uuid primary key,
    document_id uuid not null references documents(id) on delete cascade,
    category varchar(255) not null,
    severity varchar(32) not null,
    title varchar(255) not null,
    explanation varchar(4000) not null
);

create table if not exists audit_events (
    id uuid primary key,
    event_type varchar(255) not null,
    document_id uuid,
    occurred_at timestamp with time zone not null,
    actor varchar(255) not null
);

create index if not exists idx_findings_document on findings(document_id);
create index if not exists idx_audit_document on audit_events(document_id);
