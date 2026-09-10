# Plan

## Current status
- Auth, profile, and global error handling are implemented across the backend and frontend.
- Patient identity is modeled around a stable record with a unique, memorable MRN.
- Patient arrival and encounter creation are implemented on both the backend and frontend.
- Patient detail view, visit history, MRN display, and triage/admission status workflow are in place.
- Frontend and backend validation for the patient workflow pass with targeted compile/build checks.

## Recommended next steps
- Integrate department and doctor assignment more deeply into the patient detail and triage workflow.
- Add explicit admission/discharge tracking for inpatients beyond the encounter status update flow.
- Expand patient history to show lab, pharmacy, and billing activity on the patient detail page.
- Continue validating with targeted frontend build and backend compile before broader test sweeps.

## Validation command
- .\mvnw.cmd -q -DskipTests compile
- cd frontend; npm run build
