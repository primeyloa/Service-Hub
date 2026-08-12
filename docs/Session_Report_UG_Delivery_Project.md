# Work Report — UG Delivery/Logistics Project Data
**Date:** August 6, 2026
**Prepared by:** DZANDU RICHARD EYRAM

## Summary

Today's work focused on segregating the consolidated  data files provided by the group leader, organized Excel worksheet, and correcting an earlier assumption once the real location template was received.

## What Was Done

### 1.  four datasets in one excel sheet each
Built `UG_Delivery_Locations_and_Requests.xlsx` with four linked tabs:
- **Locations** — campus locations (name, area, type, coordinates)
- **Requests** — delivery request log (source, destination, category, urgency, timing, status)
- **Roads** — road segments between locations (distance, travel time, condition)
- **Resources** — delivery vehicles/riders (type, home base, capacity, availability)

### 2. Filled in each template against its own sample-row pattern
Each file was reviewed for the criteria implied by its example rows, then extended:
- `roads_template.csv` — derived that travel time = distance ÷ 9 km/h (consistent across all 3 sample rows); extended the road network using real coordinates for campus locations.
- `resources_template.csv` — derived that resource IDs are typed by prefix (V = Van, R = Rider); built out a fleet based at the main hub locations.
- `locations_template.csv` — filled out the full set of campus locations to match the template's schema (`name`, `area`, `location_type`, `x_coord`, `y_coord`).
- `service_requests_template.csv` — confirmed this matched the request log already built; no changes needed.

### 3. Caught and corrected a mapping error
The real `locations_template.csv` showed that **L001 = Balme Library, L002 = Computer Science Department, L003 = UG Hospital** — different from an earlier assumption (that L001–L003 were a medical facilities cluster). The Locations tab was corrected to match the actual template exactly, with the rest of the campus locations (L004–L038) re-expressed in the same schema.

### 4. Confirmed one working assumption
The Service Requests template confirmed that a request's `category` (e.g. Medical, Document) does **not** need to match the source location's `location_type` — category describes what's being delivered, not the kind of building it's coming from.

## Open Item — Needs a Decision

Because L001–L003 changed identity, the **Roads** entries touching those 3 IDs (and possibly a few **Resources** home locations) were built against the earlier assumption and may not fully match the corrected Locations data. Everything involving L004–L038 is unaffected.

**Decision needed:** should these specific Roads/Resources entries be rebuilt for full consistency, or is the current level of detail sufficient?

## Next Steps

- [ ] Group leader to review  and confirm overall structure/approach
- [ ] Resolve the Roads/Resources consistency question above
- [ ] Incorporate any further feedback
