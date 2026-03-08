Review the notebook(s) that were recently edited: $ARGUMENTS

If no argument is given, identify which notebook files were modified in this session.

## Steps

1. **Paren repair**: Run `clj-paren-repair` on each edited notebook file.

2. **REPL verify**: Discover the dev nREPL port (check `.nrepl-port` or use
   `clj-nrepl-eval --discover-ports`). Load each notebook namespace with `:reload`.
   Report any errors immediately — do not proceed until they're fixed.

3. **Render GFM**: For each notebook, evaluate:
   ```clojure
   (do (require '[dev :as dev] :reload)
       (dev/make-gfm! "napkinsketch_book/<filename>.clj"))
   ```

4. **Run generated tests**: For each reviewed notebook, run only its generated
   test namespace. The mapping is:
   - Notebook: `notebooks/napkinsketch_book/<name>.clj`
   - Test ns: `napkinsketch_book.<name>-generated-test` (underscores → hyphens in ns)
   - Test file: `test/napkinsketch_book/<name>_generated_test.clj`

   Run with:
   ```bash
   clojure -M:dev:test -m cognitect.test-runner -n napkinsketch_book.<name>-generated-test
   ```
   If tests fail, diagnose the failure and suggest fixes.

5. **Read rendered markdown**: Read each generated file from `gfm/napkinsketch_book.<name>.md`.

6. **Critique**: Review the rendered output for:

   **Correctness**
   - Do computed values look right? Are assertions sensible?
   - Do `kind/test-last` assertions test meaningful properties (not just truthiness)?
   - Are there any `println` calls in functions? These produce noise in rendered output.

   **Clarity**
   - Is the prose clear for a Clojure developer unfamiliar with the domain?
   - Is jargon explained on first use?
   - Are transitions between sections smooth?

   **Pedagogy**
   - Does the chapter build concepts incrementally?
   - Does each code block have enough context?
   - Are there any "magic" steps that need explanation?

   **Visual flow**
   - Good rhythm of prose → code → output → prose?
   - Any long code blocks that should be broken up?

   **Conventions**
   - `kind/test-last` has no comments (invisible in output)
   - Values shown explicitly before assertions
   - Comments precede forms (`;;` above, not `; trailing`)
   - `(deref x)` not `@x` at top level

Report findings organized by severity (errors > suggestions > minor polish).
