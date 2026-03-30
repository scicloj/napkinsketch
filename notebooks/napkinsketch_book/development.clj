;; # Development
;;
;; How to set up a development environment, run tests,
;; author notebooks, and release new versions.

(ns napkinsketch-book.development)

;; ## REPL Setup
;;
;; Start an nREPL server with the `:dev` alias, which adds
;; notebooks, tests, Clay, and related dependencies to the classpath:
;;
;; ```bash
;; clojure -M:dev -m nrepl.cmdline
;; ```
;;
;; Connect to the REPL from your editor (Calva, CIDER, Cursive, etc.)
;; using the port printed to the console (also written to `.nrepl-port`).
;;
;; When reloading namespaces, always use `:reload`:
;;
;; ```clojure
;; (require '[scicloj.napkinsketch.api :as sk] :reload)
;; ```
;;
;; Without `:reload`, you may get stale definitions.

;; ## Running Tests
;;
;; The test suite runs via [cognitect test-runner](https://github.com/cognitect-labs/test-runner):
;;
;; ```bash
;; ./run_tests.sh
;; ```
;;
;; This is equivalent to:
;;
;; ```bash
;; clojure -M:dev:test -m cognitect.test-runner
;; ```
;;
;; There are two kinds of tests:
;;
;; - **Hand-written tests** in `test/scicloj/napkinsketch/` — unit tests for
;;   core logic (view resolution, sketching, rendering, etc.)
;;
;; - **Generated tests** in `test/napkinsketch_book/` — produced from notebooks
;;   via Clay GFM rendering. Every `kind/test-last` form in a notebook becomes
;;   an assertion in the corresponding `*_generated_test.clj` file.

;; ## Notebook Authoring
;;
;; Notebooks live in `notebooks/napkinsketch_book/` and are Clojure source
;; files with `;;` comment blocks for prose (rendered as Markdown by Clay).
;;
;; To add assertions that verify computed values, use `kind/test-last`:
;;
;; ```clojure
;; (+ 1 2)
;;
;; (kind/test-last [(fn [v] (= 3 v))])
;; ```
;;
;; `kind/test-last` forms are invisible in rendered output — they only
;; generate test assertions. Do not add comments to them.
;;
;; See the **Notebook Style Conventions** section of `CLAUDE.md` for the
;; full set of authoring guidelines.

;; ## Regenerating Tests
;;
;; After editing a notebook, regenerate its test file by rendering to
;; GitHub-flavored Markdown:
;;
;; ```clojure
;; (require '[dev :as dev] :reload)
;; (dev/make-gfm! "napkinsketch_book/scatter.clj")
;; ```
;;
;; This updates `test/napkinsketch_book/scatter_generated_test.clj`.
;; The generated test files are checked into version control.
;;
;; To regenerate all notebooks at once:
;;
;; ```clojure
;; (dev/make-gfm!)
;; ```

;; ## Building the Book
;;
;; The full HTML book is rendered through [Clay](https://scicloj.github.io/clay/)
;; and [Quarto](https://quarto.org/):
;;
;; ```clojure
;; (require '[dev :as dev] :reload)
;; (dev/make-book!)
;; ```
;;
;; Chapter ordering is defined in `notebooks/chapters.edn`. The output
;; goes to the `docs/` directory and is published to
;; [scicloj.github.io/napkinsketch](https://scicloj.github.io/napkinsketch/).

;; ## Building a JAR
;;
;; The build uses [tools.build](https://clojure.org/guides/tools_build)
;; with [deps-deploy](https://github.com/slipset/deps-deploy):
;;
;; ```bash
;; clojure -T:build ci             # run tests + build JAR
;; clojure -T:build ci :snapshot true  # build snapshot JAR
;; ```
;;
;; The JAR is written to `target/`.

;; ## Deploying to Clojars
;;
;; Two convenience scripts handle the full build-and-deploy workflow:
;;
;; ```bash
;; ./release.sh    # test + build + deploy release
;; ./snapshot.sh   # test + build + deploy snapshot
;; ```
;;
;; Both run tests first, then build the JAR, then deploy to
;; [Clojars](https://clojars.org/org.scicloj/napkinsketch).
;; Deployment requires `CLOJARS_USERNAME` and `CLOJARS_PASSWORD`
;; environment variables (or a `~/.m2/settings.xml` with credentials).

;; ## Continuous Integration
;;
;; GitHub Actions runs the test suite on every push and pull request
;; to `main` (see `.github/workflows/tests.yml`). The workflow uses
;; Java 21 (Temurin) and the latest Clojure CLI.
