-- ============================================================
-- CAMPUS SERVICE SCHEMA
-- DCIT 204/308 Joint DSA Project
-- Engine: PostgreSQL
-- ============================================================

-- ------------------------------------------------------------
-- Enum types
-- ------------------------------------------------------------
CREATE TYPE urgency_level        AS ENUM ('Low', 'Medium', 'High', 'Critical');
CREATE TYPE request_status       AS ENUM ('Pending', 'Assigned', 'InProgress', 'Completed', 'Cancelled');
CREATE TYPE location_type        AS ENUM ('hostel', 'lecture_hall', 'lab', 'shuttle_stop', 'admin', 'cafeteria');
CREATE TYPE resource_type        AS ENUM ('maintenance_technician', 'shuttle_bus', 'lab_assistant', 'security_officer', 'it_support_staff');
CREATE TYPE availability_status  AS ENUM ('available', 'busy', 'off_duty');

-- ------------------------------------------------------------
-- LOCATIONS  =
-- ------------------------------------------------------------
CREATE TABLE locations (
    location_id     SERIAL PRIMARY KEY,
    name            TEXT NOT NULL,
    area            TEXT NOT NULL,
    type            location_type NOT NULL,
    longitude       DOUBLE PRECISION,
    latitude        DOUBLE PRECISION,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ------------------------------------------------------------
-- CLIENT
-- ------------------------------------------------------------
CREATE TABLE client (
    client_id       SERIAL PRIMARY KEY,
    first_name      TEXT NOT NULL,
    last_name       TEXT NOT NULL,
    phone           TEXT NOT NULL,
    email           TEXT NOT NULL UNIQUE,
    location_id     INT REFERENCES locations(location_id),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ------------------------------------------------------------
-- SERVICE_TYPE
-- ------------------------------------------------------------
CREATE TABLE service_type (
    service_type_id SERIAL PRIMARY KEY,
    name            TEXT NOT NULL UNIQUE,
    description     TEXT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ------------------------------------------------------------
-- SERVICE_PROVIDER
-- ------------------------------------------------------------
CREATE TABLE service_provider (
    provider_id       SERIAL PRIMARY KEY,
    name              TEXT NOT NULL,
    description       TEXT,
    service_type_id   INT NOT NULL REFERENCES service_type(service_type_id),
    location_id       INT NOT NULL REFERENCES locations(location_id),
    created_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at        TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ------------------------------------------------------------
-- ROUTES  (weighted edges between locations)
-- ------------------------------------------------------------
CREATE TABLE routes (
    route_id                SERIAL PRIMARY KEY,
    from_location_id         INT NOT NULL REFERENCES locations(location_id),
    to_location_id            INT NOT NULL REFERENCES locations(location_id),
    distance_km               NUMERIC(6,2) NOT NULL CHECK (distance_km > 0),
    travel_time_min            NUMERIC(6,2) NOT NULL CHECK (travel_time_min > 0),
    road_condition_weight       NUMERIC(4,2) NOT NULL DEFAULT 1.0,
    CHECK (from_location_id <> to_location_id)
);

-- ------------------------------------------------------------
-- RESOURCES 
-- ------------------------------------------------------------
CREATE TABLE resources (
    resource_id           SERIAL PRIMARY KEY,
    name                   TEXT NOT NULL,
    type                   resource_type NOT NULL,
    quantity                INT NOT NULL DEFAULT 0 CHECK (quantity >= 0),
    home_location           INT NOT NULL REFERENCES locations(location_id),
    availability_status     availability_status NOT NULL DEFAULT 'available',
    service_provider_id      INT NOT NULL REFERENCES service_provider(provider_id),
    created_at                TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at                TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ------------------------------------------------------------
-- SERVICE_REQUESTS
-- ------------------------------------------------------------
CREATE TABLE service_requests (
    request_id               SERIAL PRIMARY KEY,
    category                  TEXT NOT NULL,
    urgency                    urgency_level NOT NULL,
    time_submitted               TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at                    TIMESTAMPTZ NOT NULL DEFAULT now(),
    deadline                      TIMESTAMPTZ,
    status                        request_status NOT NULL DEFAULT 'Pending',
    priority_weight                NUMERIC(6,3),
    client_id                      INT NOT NULL REFERENCES client(client_id),
    source_location_id              INT NOT NULL REFERENCES locations(location_id),
    destination_location_id          INT REFERENCES locations(location_id),   -- nullable: not all requests have a destination
    service_provider_id               INT REFERENCES service_provider(provider_id)  -- nullable until assigned
);

-- ------------------------------------------------------------
-- RATING  (traceable to client + request)
-- ------------------------------------------------------------
CREATE TABLE rating (
    rating_id            SERIAL PRIMARY KEY,
    service_provider_id   INT NOT NULL REFERENCES service_provider(provider_id),
    client_id              INT REFERENCES client(client_id),
    request_id              INT REFERENCES service_requests(request_id),
    description              TEXT,
    rating                    INT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    created_at                 TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at                 TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ------------------------------------------------------------
-- ALGORITHM_RUNS  (performance-experiment log)
-- ------------------------------------------------------------
CREATE TABLE algorithm_runs (
    run_id           SERIAL PRIMARY KEY,
    algorithm_name   TEXT NOT NULL,
    input_size       INT NOT NULL,
    time_ns          BIGINT NOT NULL,
    memory_kb        NUMERIC(10,2),
    date_run         TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- ------------------------------------------------------------
-- AUDIT_EVENTS  (undo / audit trail)
-- ------------------------------------------------------------
CREATE TABLE audit_events (
    event_id             SERIAL PRIMARY KEY,
    event_type           TEXT NOT NULL,
    related_request_id    INT REFERENCES service_requests(request_id),
    description             TEXT,
    event_time               TIMESTAMPTZ NOT NULL DEFAULT now(),
    performed_by              TEXT
);

-- ------------------------------------------------------------
-- Indexes to support the required search/scheduling/graph workloads
-- ------------------------------------------------------------
CREATE INDEX idx_routes_from             ON routes(from_location_id);
CREATE INDEX idx_routes_to               ON routes(to_location_id);
CREATE INDEX idx_requests_status         ON service_requests(status);
CREATE INDEX idx_requests_urgency        ON service_requests(urgency);
CREATE INDEX idx_requests_source         ON service_requests(source_location_id);
CREATE INDEX idx_resources_availability  ON resources(availability_status);
CREATE INDEX idx_resources_home_location ON resources(home_location);
CREATE INDEX idx_locations_name          ON locations(name);
CREATE INDEX idx_provider_location       ON service_provider(location_id);