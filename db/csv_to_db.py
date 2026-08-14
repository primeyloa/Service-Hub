import csv
import sqlite3
from pathlib import Path
from datetime import datetime


DB_PATH = "campus_service.db"
SCHEMA_PATH = "schema.sql"
 
LOCATIONS_CSV = "locations_template.csv"
ROADS_CSV = "roads_template.csv"
REQUESTS_CSV = "service_requests_template.csv"
RESOURCES_CSV = "resources_template.csv"

URGENCY_COEFFICIENT= 10 #most common two digit in our various ids

#function to create new databse with defined schema
def fresh_database(conn: sqlite3.Connection):
    schema_sql = Path(SCHEMA_PATH).read_text()
    conn.executescript(schema_sql)

#function to parse time format in current seed data
def parse_dt(value: str):
    if not value: 
        return None
    return datetime.fromisoformat(value)
 
def compute_priority_weight(urgency: int, time_submitted: datetime, deadline: datetime) -> float:
    
    if deadline is None:
        hours_to_deadline = 24.0  # no deadline given: treat as low time-pressure
    else:
        hours_to_deadline = max((deadline - time_submitted).total_seconds() / 3600.0, 0.1)
    return round((urgency * URGENCY_COEFFICIENT) - hours_to_deadline, 2)
 
 
def import_locations(conn):
    code_to_id = {}
    with open(LOCATIONS_CSV, newline="", encoding="utf-8") as f:
        for row in csv.DictReader(f):
            cur = conn.execute(
                """INSERT INTO locations (external_code, name, area, type, latitude, longitude)
                   VALUES (?, ?, ?, ?, ?, ?)""",
                (row["location_id"], row["name"], row["area"], row["location_type"],
                 float(row["x_coord"]), float(row["y_coord"]))
            )
            code_to_id[row["location_id"]] = cur.lastrowid
    print(f"locations: {len(code_to_id)} rows imported")
    return code_to_id
 
 
def import_roads(conn, location_ids: dict):
    count = 0
    with open(ROADS_CSV, newline="", encoding="utf-8") as f:
        for row in csv.DictReader(f):
            from_id = location_ids.get(row["from_location_id"])
            to_id = location_ids.get(row["to_location_id"])
            if from_id is None or to_id is None:
                print(f"  SKIPPED road {row['road_id']}: unknown location code "
                      f"({row['from_location_id']} -> {row['to_location_id']})")
                continue
            conn.execute(
                """INSERT INTO roads (external_code, from_location_id, to_location_id,
                                       distance_km, travel_time_min, road_condition_weight)
                   VALUES (?, ?, ?, ?, ?, ?)""",
                (row["road_id"], from_id, to_id,
                 float(row["distance_km"]), float(row["travel_time_min"]),
                 float(row["condition_weight"]))
            )
            count += 1
    print(f"roads: {count} rows imported")
 
 
def import_service_requests(conn, location_ids: dict):
    count = 0
    with open(REQUESTS_CSV, newline="", encoding="utf-8") as f:
        for row in csv.DictReader(f):
            source_id = location_ids.get(row["source_location_id"])
            dest_id = location_ids.get(row["destination_location_id"]) if row["destination_location_id"] else None
            if source_id is None:
                print(f"  SKIPPED request {row['request_id']}: unknown source location")
                continue
 
            submitted = parse_dt(row["time_submitted"])
            deadline = parse_dt(row["deadline"])
            urgency = int(row["urgency"])
            weight = compute_priority_weight(urgency, submitted, deadline)
 
            conn.execute(
                """INSERT INTO service_requests
                       (external_code, source_location_id, destination_location_id,
                        category, urgency, time_submitted, deadline, status, priority_weight)
                   VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)""",
                (row["request_id"], source_id, dest_id, row["category"], urgency,
                 submitted.isoformat(), deadline.isoformat() if deadline else None,
                 row["status"], weight)
            )
            count += 1
    print(f"service_requests: {count} rows imported")
 
 
def import_resources(conn, location_ids: dict):
    count = 0
    with open(RESOURCES_CSV, newline="", encoding="utf-8") as f:
        for row in csv.DictReader(f):
            home_id = location_ids.get(row["home_location_id"])
            if home_id is None:
                print(f"  SKIPPED resource {row['resource_id']}: unknown home location")
                continue
            conn.execute(
                """INSERT INTO resources (external_code, type, home_location_id,
                                           capacity, availability_status)
                   VALUES (?, ?, ?, ?, ?)""",
                (row["resource_id"], row["resource_type"], home_id,
                 int(row["capacity"]), row["availability_status"])
            )
            count += 1
    print(f"resources: {count} rows imported")
 
 
def check_minimums(conn):
    """Warn if the brief's minimum record counts (Section 4) aren't met yet."""
    minimums = {
        "locations": 50, "roads": 100,
        "service_requests": 300, "resources": 30
    }
    print("\n--- Minimum record count check (brief Section 4) ---")
    for table, minimum in minimums.items():
        actual = conn.execute(f"SELECT COUNT(*) FROM {table}").fetchone()[0]
        status = "OK" if actual >= minimum else "SHORT"
        print(f"  {table:<20} {actual:>5} / {minimum:<5} [{status}]")
 
 
def save_sample_queries(conn):
    """Runs and saves 3 sample queries, required as a Data Generation deliverable."""
    queries = {
        "top_5_urgent_pending.sql":
            """SELECT external_code, category, urgency, status, priority_weight
               FROM service_requests
               WHERE status IN ('NEW','IN_PROGRESS')
               ORDER BY priority_weight DESC
               LIMIT 5;""",
        "resources_by_location.sql":
            """SELECT l.name AS location, r.type, COUNT(*) AS resource_count
               FROM resources r JOIN locations l ON r.home_location_id = l.location_id
               GROUP BY l.name, r.type
               ORDER BY resource_count DESC;""",
        "roads_from_balme_library.sql":
            """SELECT l2.name AS destination, rd.distance_km, rd.travel_time_min
               FROM roads rd
               JOIN locations l1 ON rd.from_location_id = l1.location_id
               JOIN locations l2 ON rd.to_location_id = l2.location_id
               WHERE l1.name = 'Balme Library';"""
    }
    out_dir = Path("sample_queries")
    out_dir.mkdir(exist_ok=True)
    for filename, sql in queries.items():
        rows = conn.execute(sql).fetchall()
        cols = [d[0] for d in conn.execute(sql).description]
        with open(out_dir / filename, "w") as f:
            f.write(f"-- Query:\n{sql}\n\n-- Sample result ({len(rows)} rows):\n")
            f.write(", ".join(cols) + "\n")
            for r in rows:
                f.write(", ".join(str(v) for v in r) + "\n")
    print(f"\n3 sample queries saved to ./{out_dir}/")
 
 
def main():
    conn = sqlite3.connect(DB_PATH)
    conn.execute("PRAGMA foreign_keys = ON")
 
    fresh_database(conn)
    location_ids = import_locations(conn)
    import_roads(conn, location_ids)
    import_service_requests(conn, location_ids)
    import_resources(conn, location_ids)
    conn.commit()
 
    check_minimums(conn)
    save_sample_queries(conn)
    conn.close()
    print(f"\nDone. Database written to {DB_PATH}")
 
 
if __name__ == "__main__":
    main()