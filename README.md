# cpath-clj

__cpath-clj__ is a small library to transparently collect files on the classpath based on a
given base directory. Since that directory might be located inside a JAR this is not always
trivial.

[![Build Status](https://travis-ci.org/xsc/cpath-clj.svg?branch=master)](https://travis-ci.org/xsc/cpath-clj)
[![endorse](https://api.coderwall.com/xsc/endorsecount.png)](https://coderwall.com/xsc)

## Usage

__Leiningen ([via Clojars](https://clojars.org/cpath-clj))__

```clojure
[cpath-clj "0.1.2"]
```

__REPL__

```clojure
(require '[cpath-clj.core :as cp])

(cp/resources "static")
;; => {"/js/main.js" [#<URI file:/.../static/js/main.js>]
;;     "/js/lib.js"  [#<URI jar:file:/...!/static/js/lib.js>]}
```

`cpath-clj.core.resources` finds all locations on the classpath with the path `static`,
then collects the URIs of all resources residing under those locations.

You can have a more targeted collection by supplying either a `java.io.File`, `java.net.URI`
or `java.net.URL` - e.g. to traverse the directory `static` currently being the first match
on the classpath:

```clojure
(cp/resources (clojure.java.io/resource "static"))
;; => {"/js/main.js" [#<URI file:/.../static/js/main.js>]}
```

## License

Copyright &copy; 2014-2015 Yannick Scherer

This project is licensed under the [MIT License][license].

[license]: http://opensource.org/licenses/MIT

```
The MIT License (MIT)

Copyright (c) 2014-2015 Yannick Scherer

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
