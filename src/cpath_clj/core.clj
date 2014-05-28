(ns cpath-clj.core
  (:require [clojure.java
             [io :as io]
             [classpath :as cp]])
  (:import [java.net URL URI]
           [java.util.jar JarFile]
           [java.io File]))

;; ## Read Directories from Classpath

(defmulti ^:private resource-file-uris
  "Create a seq of all files that are children of the given URI (e.g. files
   within a directory, files within a JAR, ...). The result will be a seq of
   pairs, the first element being the path relative to the base, the second
   one being a `java.net.URI`."
  (fn [^URL directory-url]
    (.getProtocol directory-url))
  :default nil)

(defmethod resource-file-uris nil
  [_]
  [])

;; ### Local Directory/File

(defn- recursive-directory-files
  "Recursively list the files in the given directory. Returns a single-element
   vector if the given File is not a directory."
  [prefix ^File f]
  (let [new-prefix (str prefix "/" (.getName f))]
    (cond (.isFile f) [[new-prefix f]]
          (.isDirectory f) (vec
                             (mapcat
                               #(recursive-directory-files new-prefix %)
                               (.listFiles f)))
          :else [])))

(defmethod resource-file-uris "file"
  [^URL url]
  (let [^File f (io/file url)]
    (when (.isDirectory f)
      (->> (.listFiles f)
           (mapcat #(recursive-directory-files nil %))
           (mapv
             (fn [[path ^File f]]
               [path (.toURI f)]))))))

;; ### JAR Files

(defn- jar-content-uri
  "Create URI pointing to a file within the given JAR."
  [^URL jar-location ^String path]
  (->> (str "jar:" (.toURI jar-location) "!/" path)
       (URL.)
       (.toURI)))

(defmethod resource-file-uris "jar"
  [^URL url]
  (let [^String path (.getPath url)
        idx (.lastIndexOf path "!")]
    (if (neg? idx)
      (cp/filenames-in-jar (JarFile. (io/file url)))
      (let [jar-location (URL. (subs path 0 idx))
            jar-file (JarFile. (io/file jar-location))
            prefix (subs path (+ idx 2))
            prefix-length (count prefix)]
        (->> (cp/filenames-in-jar jar-file)
             (filter #(.startsWith ^String % prefix))
             (map
               (juxt
                 #(subs % prefix-length)
                 #(jar-content-uri jar-location %))))))))

;; ## Classpath Inspection

(defn resources*
  "Find all resources with the given path on the classpath.
   Returns a seq of resource URLs."
  [path]
  (-> (Thread/currentThread)
      (.getContextClassLoader)
      (.getResources path)
      (enumeration-seq)))

(defn resources
  "Find all files on the classpath that are children of resources
   with the given path. The result will be a map associating the path
   relative to the given base with a seq of URIs pointing to the resources."
  [path]
  (->> (resources* path)
       (mapcat resource-file-uris)
       (reduce
         (fn [m [^String path ^URI uri]]
           (update-in m [path] (fnil conj []) uri))
         {})))
