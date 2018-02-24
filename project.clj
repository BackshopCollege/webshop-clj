(defproject webshop "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [ring "1.6.3"]]

  :main webshop.core

  :profiles {:dev {:source-paths ["dev"]
                   :repl-options {:init-ns user}
                   :plugins  [[com.jakemccrary/lein-test-refresh "0.22.0"]]
                   :dependencies [[ring/ring-devel "1.6.3"]
                                  [ring/ring-mock "0.3.2"]
                                  ]}})
