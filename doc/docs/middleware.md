Timestamp: 2016-01-26T16:43:00
Title: Generator middleware in λ-blog
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
