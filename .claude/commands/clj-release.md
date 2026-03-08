Prepare a new release of the library. Do NOT commit or push — the user handles that.

## Steps

1. **Check git status**: Run `git status` and `git diff --stat` to understand
   what's being released. Report any uncommitted changes.

2. **Determine version**: If an argument is given (`$ARGUMENTS`), use it as
   the new version. Otherwise, read the current version from `build.clj` and
   ask what the new version should be.

3. **Update build.clj**: Change the `version` string.

4. **Update CHANGELOG.md**: Add a new section with the version and today's date.
   Only include **library API changes** (new features, bug fixes, breaking changes).
   Never mention notebook updates, documentation changes, or dev-notes cleanup.
   Keep entries concise — describe *what* changed, not *how*.
   Ask the user what changed if unclear.

5. **Run tests**: Execute `./run_tests.sh`. Do not proceed if tests fail.

6. **Re-render HTML docs**: Discover the dev nREPL port and evaluate with a
   long timeout (5+ minutes):
   ```clojure
   (do (require '[dev :as dev] :reload)
       (dev/make-book!))
   ```

7. **Report**: Show the version bump, changelog entry, test results, and
   doc render status. Remind the user to deploy with:
   ```
   clojure -T:build deploy
   ```
   Do NOT commit, push, or deploy — the user will do that.
