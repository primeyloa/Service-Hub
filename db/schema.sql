-- CAMPUS SERVICE SCHEMA
-- DCIT 204/308 Joint DSA Project
-- Engine: SQLite

-- notes:
-- new column called external_code was added to tables to prevent
-- confusion with ids from csv such as R001 for resource and road entries

PRAGMA foreign_keys = ON;


-- LOCATIONS
 
CREATE TABLE locations (
    location_id     INTEGER PRIMARY KEY AUTOINCREMENT,
    external_code   TEXT NOT NULL UNIQUE,      -- 'L001' location_id in the CSV
    name            TEXT NOT NULL,
    area            TEXT NOT NULL,
    type            TEXT NOT NULL CHECK (type IN (
                        'Academic','Administrative','Banking','Health',
                        'Hostel','Library','Recreational','Research'
                    )),                         
    latitude        REAL,                       --  x_coord
    longitude       REAL,                       --  y_coord
    created_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

 
-- ROADS
 
CREATE TABLE roads (
    road_id                 INTEGER PRIMARY KEY AUTOINCREMENT,
    external_code           TEXT NOT NULL UNIQUE,     
    from_location_id        INTEGER NOT NULL REFERENCES locations(location_id),
    to_location_id          INTEGER NOT NULL REFERENCES locations(location_id),
    distance_km             REAL NOT NULL CHECK (distance_km > 0),
    travel_time_min         REAL NOT NULL CHECK (travel_time_min > 0),
    road_condition_weight   REAL NOT NULL DEFAULT 1.0,  
    CHECK (from_location_id <> to_location_id)
);

 
-- SERVICE_REQUESTS 
 
CREATE TABLE service_requests (
    request_id               INTEGER PRIMARY KEY AUTOINCREMENT,
    external_code             TEXT NOT NULL UNIQUE,   
    source_location_id        INTEGER NOT NULL REFERENCES locations(location_id),
    destination_location_id   INTEGER REFERENCES locations(location_id),  -- nullable
    category                  TEXT NOT NULL,          
    urgency                   INTEGER NOT NULL CHECK (urgency BETWEEN 1 AND 5),  -- 5 = most urgent
    time_submitted             DATETIME NOT NULL,
    updated_at                  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deadline                    DATETIME,
    status                      TEXT NOT NULL DEFAULT 'NEW' CHECK (status IN (
                                    'NEW','IN_PROGRESS','COMPLETED','CANCELLED'
                                 )),
    priority_weight              REAL  
);

 
-- RESOURCES 
 
CREATE TABLE resources (
    resource_id           INTEGER PRIMARY KEY AUTOINCREMENT,
    external_code         TEXT NOT NULL UNIQUE,   -- e.g. 'R001'/'V001',
    type                  TEXT NOT NULL CHECK (type IN ('Rider','Van')), 
    home_location_id      INTEGER NOT NULL REFERENCES locations(location_id),
    capacity               INTEGER NOT NULL DEFAULT 1,
    availability_status     TEXT NOT NULL DEFAULT 'AVAILABLE' CHECK (availability_status IN (
                               'AVAILABLE','BUSY','MAINTENANCE','OFFLINE'
                            )),
    created_at               DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

 
-- ALGORITHM_RUNS  (populated by the empirical benchmarking)
 
CREATE TABLE algorithm_runs (
    run_id           INTEGER PRIMARY KEY AUTOINCREMENT,
    algorithm_name   TEXT NOT NULL,
    input_size       INTEGER NOT NULL,
    time_ns          BIGINT NOT NULL,
    memory_kb        REAL,
    date_run         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

 
-- AUDIT_EVENTS  (populated by the stack-based undo log at runtime)
 
CREATE TABLE audit_events (
    event_id             INTEGER PRIMARY KEY AUTOINCREMENT,
    event_type           TEXT NOT NULL,
    related_request_id    INTEGER REFERENCES service_requests(request_id),
    description             TEXT,
    event_time               DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    performed_by              TEXT
);

 
-- Indexes
 
CREATE INDEX idx_roads_from              ON roads(from_location_id);
CREATE INDEX idx_roads_to                ON roads(to_location_id);
CREATE INDEX idx_requests_status         ON service_requests(status);
CREATE INDEX idx_requests_urgency        ON service_requests(urgency);
CREATE INDEX idx_requests_source         ON service_requests(source_location_id);
CREATE INDEX idx_resources_availability  ON resources(availability_status);
CREATE INDEX idx_resources_home_location ON resources(home_location_id);
CREATE INDEX idx_locations_name          ON locations(name);