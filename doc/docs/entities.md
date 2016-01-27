Timestamp: 2016-01-26T16:43:00
Title: Entities in λ-blog
ID: entities

# Entities

Throughout this documentation & the **λ-blog** codebase you will run into a somewhat vaguely named concept of *entities*.  An entity is any data structure that represents your content throughout **λ-blog** generation pipline. For example, the following Clojure map representing a parsed blog entry is an entity:

```clojure
(def entry {:metadata {:author "Idorobots"
                       :title "Entry title"}
            :contents "<p>Some contents</p>"})
```

Similarily, your blog configuration can be considered an entity if it happens to contribute to the page generation of another entity:

```clojure
(def config {:author "Idorobots"
             :stylesheets ["style/style.css"
                           "style/theme.css"]})

;; Elswhere in the code:
(->> entry
     (merge config)
     ;; ... process & generate a page
     )
```

In fact, this is so useful that some of the **λ-blog** internals will do just that for you automatically, so you don't have to worry about data duplication when building your generation pipline. This is, however, a topic for another time.

## Nested entities

Since **λ-blog** imposes very few restrictions on entity format, entities can be nested:

```clojure
(def config {:author "Idorobots"
             :stylesheets ["style/style.css"
                           "style/theme.css"]
             :entries [{:metadata {:title "Entry 1"}
                        :url "www.example.com/entry-1"
                        :contents "<p>Some contents</p>"}
                       {:metadata {:title "Entry 2"}
                        :url "www.example.com/entry-2"
                        :contents "<p>Some other contents</p>"}
                       ; ...
                      ]})
```

In such a case, `:entries` can be used as additional metadata while generating some pages of the blog making it easy, for example, to link to more content. Just `(map (juxt (comp :title :metadata) :url) entries)` to get a list of `:title` - `:url` pairs and you're set.

**λ-blog** provides many convenient ways to process nested entities & entity internals, but this is, again, a topic for another time.
