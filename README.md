# devcards-om-next [![Clojars Project](https://img.shields.io/clojars/v/devcards-om-next.svg)](https://clojars.org/devcards-om-next)

Devcards extension for writing fully reloadable Om Next cards.

## Installation
Leiningen dependency information:

```clojure
[devcards-om-next "0.1.1"]
```

Maven dependency information:

```xml
<dependency>
  <groupId>devcards-om-next</groupId>
  <artifactId>devcards-om-next</artifactId>
  <version>0.1.1</version>
</dependency>
```

## Usage

Require the macros along with devcards's.

```clojure
(ns my-ns.core
  (:require-macros [devcards.core :as devcards :refer [defcard]])
  (:require [devcards-om-next.core :as don :refer-macros [om-next-root defcard-om-next]]
            [om.next :as om :refer-macros [defui]]))
```

## Documentation

In progress. See [this blog post](http://anmonteiro.com/2016/02/om-next-meets-devcards-the-full-reloadable-experience/) for a quick start.


## License

Copyright © 2016 António Nuno Monteiro

Distributed under the Eclipse Public License either version 1.0.
