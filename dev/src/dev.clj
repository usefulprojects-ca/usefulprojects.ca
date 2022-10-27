(ns dev
  (:require
   [ca.usefulprojects :as up]
   [ca.usefulprojects.config :as config]
   [clojure.java.shell :as shell]
   [clojure.string :as str]
   [hawk.core :as hawk]
   [taoensso.timbre :as log]))

;; --------------------------------------------------------------- tailwind css
;; TODO: consider moving css compilation into babashka tasks

(defonce hawk-watcher (atom nil))

(defn compile-tailwind []
  (let [result (shell/sh "tailwindcss"
                         "-i" "resources/tailwind/tailwind.css"
                         "-c" "resources/tailwind/config.js"
                         "-o" "resources/public/main.css"
                         "--minify")]
    ;; Tailwind may kick out warnings we want to see so can't rely on exit code
    (if (str/starts-with? (:err result) "\nDone in")
      (log/debug "Tailwindcss recompiled.")
      (log/warn "Tailwindcss error:" result))))

(defn start-tailwind-auto-compiler []
  (log/debug "Starting tailwindcss watcher...")
  (if (some? @hawk-watcher)
    (log/warn "Tailwindcss compiler already started")
    (reset! hawk-watcher
            (hawk/watch!
             [{:paths ["src"]
               ;; filters out things like emacs tmp files/undo trees
               :filter (fn [_ctx event]
                         (let [filename (.getName (:file event))]
                           (and (str/ends-with? filename ".clj")
                                (not (str/starts-with? filename ".")))))
               :handler (fn [_ctx _e] (#'compile-tailwind))}]))))

(defn stop-tailwind-auto-compiler []
  (when @hawk-watcher
    (log/debug "Stopping tailwind auto compiler...")
    (hawk/stop! @hawk-watcher)
    (reset! hawk-watcher nil)))

(comment
  ;; If you are working on CSS, you'll want to start the tailwind compiler
  (stop-tailwind-auto-compiler)
  (start-tailwind-auto-compiler))

;; --------------------------------------------------------------------- system

(defonce system (atom nil))

(defn restart []
  (when @system
    (up/stop! @system))
  (reset! system (up/start (config/read-config :local))))

(comment
  (restart)
  (up/stop! @system))

;; ----------------------------------------------------------------------- misc

(defn spy>
  "Like tap>, but returns tapped value instead of true/false."
  [x]
  (doto x tap>))

(comment
  ;; Useful startup commands for Reveal users
  (do
    (require '[vlaaad.reveal :as r]
             'xtdb.node)

    ;; Alter the output of an XtdbNode to avoid flooding Reveal with data
    #_{:clj-kondo/ignore [:unused-import]}
    (import '(xtdb.node XtdbNode))

    (r/defstream XtdbNode [_]
      (r/raw-string "<XtdbNode>"))))
