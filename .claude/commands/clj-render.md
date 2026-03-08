Re-render the full HTML book via Clay and Quarto.

## Steps

1. **Check chapters**: Read `notebooks/chapters.edn` and report the chapter list.

2. **Render**: Discover the dev nREPL port and evaluate with a long timeout
   (5+ minutes):
   ```clojure
   (do (require '[dev :as dev] :reload)
       (dev/make-book!))
   ```

3. **Report**: How many chapters were rendered and whether Quarto succeeded.
   If any chapter fails, identify the failing notebook and suggest fixes.
