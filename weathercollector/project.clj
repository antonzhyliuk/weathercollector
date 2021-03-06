(defproject weathercollector "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [clojurewerkz/quartzite "2.1.0"]
                 [clj-http "3.10.3"]
                 [nrepl "0.8.3"]
                 [metosin/jsonista "0.2.7"]
                 [seancorfield/next.jdbc "1.1.613"]
                 [org.postgresql/postgresql "42.2.10"]
                 [ragtime "0.8.0"]
                 [amazonica "0.3.153"]
                 [com.taoensso/timbre "5.1.0"]
                 [com.fzakaria/slf4j-timbre "0.3.20"]]
  :repl-options {:init-ns weathercollector.core}
  :main weathercollector.core
  :aot [weathercollector.core])
