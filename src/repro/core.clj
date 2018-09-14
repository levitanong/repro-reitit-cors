(ns repro.core
  (:require [clojure.tools.cli :refer [parse-opts]]
            [org.httpkit.server :as httpkit]
            [reitit.core :as r]
            [reitit.ring :as ring]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.middleware.parameters :as parameters]
            [ring.middleware.cors :refer [wrap-cors]]))

(def cli-options
  [["-w" "--activate-workaround" "Activate CORS middleware workaround"]])

(defn ok
  [body]
  {:status 200
   :body   body})

(defn cors-middleware
  "Convenience, since we're using this in two places with the same args"
  [handler]
  (wrap-cors handler
             :access-control-allow-origin [#"http://localhost:5000"]
             :access-control-allow-methods [:get :put]))

(def app-with-problem
  "cors middleware never activates"
  (ring/ring-handler
   (ring/router
    ["/v1" {:middleware [cors-middleware]}
     ["/foo" {:get {:handler (constantly (ok "bar"))}
              :put {:handler (constantly (ok "baz"))}}]])))

(def app-with-workaround
  "Makes it work, but ring-handler loses metadata,
  we have to do the `with-meta` and `meta` dance."
  (-> (ring/ring-handler
       (ring/router
        ["/v1"
         ["/foo" {:get {:handler (constantly (ok "bar"))}
                  :put {:handler (constantly (ok "baz"))}}]]))
      (cors-middleware)))

(defn -main
  [& args]
  (let [{:keys [options]}             (parse-opts args cli-options)
        {:keys [activate-workaround]} options
        handler                       (if activate-workaround
                                        app-with-workaround
                                        app-with-problem)]
    (if activate-workaround
      (println "Starting server with workaround")
      (println "Starting server with problematic handler"))
    (httpkit/run-server handler {:port 5000})
    (println "Server started")))
