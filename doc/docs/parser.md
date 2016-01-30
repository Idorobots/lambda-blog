Timestamp: #inst "2016-01-26T16:43:00"
Title: "Content parsers"
ID: parsers

# Content parsers

**λ-blog** uses a very simple scheme of content management - you supply paths to files containing your content, and tell how to process them by specifying a *parser*.

A parser is simply a Clojure function that takes a single argument - the text contents of a file, and returns a data structure representing it. The returned data structure can be arbitrary as long as your generation pipeline knows how to handle it. A sensible idea is to return a Clojure map containing processed file contents & some metadata.

Here's a simple definition of such a parser:

```clojure
(defn hello-parser [contents]
  {:metadata {:length (count contents)}
   :contents (str "<h1>Hello world!</h1><p>" contents "</p>")})
```

## Builtin parsers

Out of the box, **λ-blog** comes bundled with a Markdown parser based on [markdown-clj](https://github.com/yogthos/markdown-clj). To use it, simply require its namespace and add it to your generation pipeline:

```clojure
(require '[lambda-blog.parsers.md :as md])

;; Elswhere in the code:
(-> filename
    slurp
    md/parse)
```

The value returned is a Clojure map containing at least `:contents` & `:metadata` keys:

```clojure-repl
user=> (md/parse "Some: \"Metadata\"\n\n# Some\nMarkdown\n## Contents")
{:metadata {:some "Metadata"}
 :contents "<h1>Some</h1>Markdown<h2>Contents</h2>"}
```
