Update dev-notes and memory files to reflect the current session's work.
Run this before compacting or at the end of a working session.

## Steps

1. **Review session work**: Look at what was accomplished in this conversation.
   Identify: files modified, features added/changed, bugs fixed, lessons learned,
   decisions made, and any open questions remaining.

2. **Update MEMORY.md** (auto-memory file):
   This file is always loaded into context — keep it concise (under 200 lines).
   - Add new lessons learned, gotchas discovered, or patterns that worked
   - Update status of in-progress work
   - Remove or correct entries that turned out to be wrong
   - Organize by topic, not chronologically
   - Do NOT duplicate information already in CLAUDE.md

3. **Update relevant dev-notes**: List all files in `dev-notes/`, read each one,
   and update any that are affected by the session's work. Common updates:
   - `PROJECT_SUMMARY.md` — version, status section
   - Plan/provenance files — progress checkboxes, open questions
   - Review files — issues addressed or newly discovered
   Only touch files that are relevant. Do not update files just for the sake of it.

4. **Summary**: List what was updated and why, so the user can review.
