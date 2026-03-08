Quick verification of recently edited Clojure files: $ARGUMENTS

If no argument is given, identify which `.clj` files were modified in this session.

## Steps

1. **Paren repair**: Run `clj-paren-repair` on each edited file.
   If repair fails, report the error — do not attempt manual fixes.

2. **REPL load**: Discover the dev nREPL port (check `.nrepl-port` or use
   `clj-nrepl-eval --discover-ports`). Require each edited namespace with `:reload`.
   Report any compilation errors immediately.

3. **Check for stale tests**: If any notebook files were edited, warn that
   GFM must be regenerated before `./run_tests.sh` will test the latest code.
   Generated test files in `test/napkinsketch_book/` are only updated when GFM is
   rendered. Suggest running `/clj-review` for a full notebook verification.

4. **Run tests**: Execute `./run_tests.sh` and report results.

5. **Summary**: Report success or list any issues found.
