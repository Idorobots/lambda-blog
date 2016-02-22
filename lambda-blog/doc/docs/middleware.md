Timestamp: #inst "2016-01-26T16:43:00"
Title: "Generator middleware"
ID: middleware

# Generator middleware

Often times it is desirable to transform an entity in one way or another during processing in the generation pipeline. A simple yet effective way to achieve that is to use the *middleware pattern*.

In **λ-blog** a middleware is a Clojure function that takes an entity as a parameter and returns an entity as a result:

```clojure
(defn hello-world-middleware [entity]
  (assoc entity :hello :world))
```

On the other hand, it might be useful to pass some additional argumentsx to a middleware. In this case, define a middleware builder:

```clojure
(defn hello-*-middleware [what]
  (fn [entity]
    (assoc entity :hello what)))
```

## Using middleware

By design, the middleware pattern is quite straightforward. All you need to do is to pipe some middleware functions in order you want them to execute:

```clojure
(-> entity
    logging-middleware
    hello-world-middleware
    ;; ...
    )
```

Things get tricky when we want to process nested entities. Fortunately, **λ-blog** has us covered with the [`lambda-blog.generator`](https://idorobots.github.io/lambda-blog/api/lambda-blog.generator.html) namespace:

- `(update entity key & middleware)` - applies each `middleware` function in order on the `(entity key)` subentity,

- `(update-all entity key & middleware)` - same as `update`, but applies `middleware` functions to each element of a sequence of subentities under `(entity key)`,

- `(whenever entity predicate? & middleware)` - applies each `middleware` function on the `entity`

## Builtin middleware functions

A list of middleware available out of the box, as well as their descriptions can be found [here](https://idorobots.github.io/lambda-blog/api/lambda-blog.middleware.html).

### Text substitutions

`(substitute :where)` is a useful middleware that will substitute all occurences of `{{key}}` with corresponding `:key`'s of the entity. For example:

```clojure-repl
user=> (-> {:contents "{​{fill-me-in}}"
            :fill-me-in "like-a so"}
          ((substitute :contents)))
{:contents "like-a so"
 :fill-me-in "like-a so"}
```

Another example:

```markdown
To insert site title into a Markdown document, simply write "{​{title}}" and use the `substitute` middleware in your generation pipeline.
```

Turns into:

To insert site title into a Markdown document, simply write "{{title}}" and use the `substitute` middleware in your generation pipeline.

Alternatively, you can use the `(substitute-by :where)` version of this middleware which evaluates each occurence of `{{expression}}` and applies it like a function to the entity:

```clojure-repl
user=> (-> {:contents "{​{(comp clojure.string/upper-case :fill-me-in)}}"
            :fill-me-in "like-a so"}
          ((substitute-by :contents)))
{:contents "LIKE-A SO"
 :fill-me-in "like-a so"}
```

Of course, substitutions can also be used in the embedded HTML code.
