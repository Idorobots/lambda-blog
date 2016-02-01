Timestamp: #inst "2016-01-26T16:43:00"
Title: "Generation pipeline"
ID: generator

# Generation pipeline

To actually generate anything with **λ-blog** we need to define a computation that will successively transform your content through different states into a bunch of static pages. We need a *pipeline*, if you will.

The generation pipline is quite literarily whatever you want it to be. Its purpose is to take your content and generate your static site with a *bunch of magic* happening inbetween over which you have **total control**. In fact, this *the* guiding principle behind **λ-blog** - it is optimized for customizability, so you can build a static blog generator tailored specifically to your needs.

## The magic

**λ-blog** comes with a few useful functions that simplify generation pipeline creation. These can be divided into three categories:

- Filesystem related functions, for example:
  - `read-dir`
  - `copy-dir!`

- Entity manipulation functions, for example:
  - `update`
  - `whenever`

- Site generation functions, for example:
  - `generate!`

Detailed descriptions of these functions can be found in the [`lambda-blog.generator`](https://idorobots.github.io/lambda-blog/api/lambda-blog.generator.html) namespace.

## Building a pipeline

A sensible way to start building your generation pipeline is to define some sort of configuration entity (let's assume for a moment that we have `data/js` and `data/style` directories populated):

```clojure
(def config {:author "Idorobots"
             :title "My kickass blog!"
             :scripts ["js/script.js"]
             :stylesheets ["style/style.css"
                           "style/theme.css"]
             :output-dir "out"})
```

Next, we need a convenient way to repeatably generate the static site. A Clojure function is the way to go:

```clojure
(defn generate []
  (-> config
      ;; The pipeline goes here.
      ))
```

Lastly, we need to build the actual pipeline. Let's start by adding an entity representing the page index:

```clojure
(-> config
    (assoc :index {:title "Index"})
    ;; ...
    )
```

Next, read & parse some entries (again, assume that we have `data/entries` handy). Note, that we want the entries to appear in a sorted orded, hence the `update` call:

```clojure
(-> config
    (assoc :index {:title "Index"})
    (read-dir :entries "data/entries" hello-parser)
    (update :entries
            #(sort-by (comp :timestamp :metadata) %))
    ;; ...
    )
```

And finally, generate the site:

```clojure
(-> config
    (assoc :index {:title "Index"})
    (read-dir :entries "data/entries" hello-parser)
    (update :entries
            #(sort-by (comp :timestamp :metadata) %))
    clean-dir!
    (generate! :index some-index-template)
    (generate-all! :entries some-entry-template)
    (copy-dir! "data/js" "js")
    (copy-dir! "data/style" "style"))
```

Now, whenever `generate` is evaluated a fresh and (hopefully) repeatable instance of `My kickass blog!` will be generated.
