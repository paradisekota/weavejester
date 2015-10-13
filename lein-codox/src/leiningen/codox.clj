(ns leiningen.codox
  (:require [leinjacker.deps :as deps]
            [leinjacker.eval :as eval]
            [leiningen.core.project :as project]
            [clojure.string :as str]))

(defn- get-options [project]
  (merge {:sources (:source-paths project ["src"])}
         (-> project :codox)
         {:name (str/capitalize (:name project))}
         (select-keys project [:root :version :description])
         (-> project :codox :project)))

(defn codox
  "Generate API documentation from source code."
  [project]
  (let [project (if (get-in project [:profiles :codox])
                  (project/merge-profiles project [:codox])
                  project)]
    (eval/eval-in-project
     (deps/add-if-missing project '[codox "0.9.0-SNAPSHOT"])
     `(codox.main/generate-docs
       (update-in '~(get-options project) [:src-uri-mapping] eval))
     `(require 'codox.main))))