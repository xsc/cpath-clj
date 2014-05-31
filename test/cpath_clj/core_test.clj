(ns cpath-clj.core-test
  (:require [midje.sweet :refer :all]
            [cpath-clj.core :as cp])
  (:import [java.io File]
           [java.net URL]))

(fact "about resources on the classpath"
      (let [r (cp/resources)]
        (set (keys r)) => #{"/test.txt"
                            "/cpath_clj/core.clj"
                            "/cpath_clj/core_test.clj"}
        (dissoc r "/test.txt") => (has every? #(= (count %) 1))
        (count (r "/test.txt")) => 2
        (map slurp (r "/test.txt")) => (has every? #{"Hello World!\n"})))

(let [jar-file (File. "test-resources/test.jar")
      jar-url (.toURL (.toURI jar-file))]
  (fact "about all resources inside of a JAR"
        (let [all-jar-resources (->> (URL. (str "jar:" jar-url "!/"))
                                     (cp/resources))]
          (count all-jar-resources) => 3
          (set (keys all-jar-resources)) => #{"/META-INF/MANIFEST.MF" "/a.txt" "/b.txt"}
          (vals all-jar-resources) => (has every? #(= (count %) 1))
          (slurp (first (all-jar-resources "/a.txt"))) => "a\n"
          (slurp (first (all-jar-resources "/b.txt"))) => "b\n"))
  (fact "about subdirectories in a JAR"
        (let [meta-resources (->> (URL. (str "jar:" jar-url "!/META-INF"))
                                  (cp/resources))]
          (count meta-resources) => 1
          (set (keys meta-resources)) => #{"/MANIFEST.MF"}
          (vals meta-resources) => (has every? #(= (count %) 1)))))

(fact "about resources given as java.io.File objects"
      (let [file-resources (cp/resources (File. "test-resources/test.jar"))]
        (count file-resources) => 1
        (key (first file-resources)) => "/test.jar"
        (count (val (first file-resources))) => 1)
      (let [dir-resources (cp/resources (File. "test-resources"))]
        (count dir-resources) => 3
        (set (keys dir-resources)) => #{"/test.jar"
                                        "/test1/test.txt"
                                        "/test2/test.txt"}
          (vals dir-resources) => (has every? #(= (count %) 1))))
