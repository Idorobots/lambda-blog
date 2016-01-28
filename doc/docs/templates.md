Timestamp: 2016-01-26T16:43:00
Title: HTML templates in λ-blog
ID: templates

# HTML templates

The final step of a generation pipeline is the *actual* static page generation. It is usually achieved using an *HTML templating engine* of sorts. In **λ-blog**, default HTML templates are pretty spartan and defined using the [S-HTML](https://github.com/Idorobots/s-html) Clojure DSL. It so to encourage experimentation, after all *customizability* is the guiding principle behind **λ-blog**.

A template is just a function that takes an entity as an argument and returns processed HTML as a result:

```clojure
(defn template [{:keys [contents]}]
  (str "<div class=\"container\">" contents "</div>"))
```

Or in case of **S-HTML**:

```clojure
(defn template [{:keys [contents]}]
  (div {:class :container}
       contents))
```

## Using templates with nested entities

To make it easier to generate HTML from nested entities, **λ-blog** defines a bunch of handy functions:

- `(generate! entity key template & args)` - generates a page using `entity` **and** `(entity key)` values, by combining them and passing the result along with additional `args` to the `template`.

- `(generate-all! key template & args)` - same as `generate!`, but generates a sequence of pages from a sequence of subentities.

Detailed descriptions of these functions can be found in the [`lambda-blog.generator`](https://idorobots.github.io/lambda-blog/api/lambda-blog.generator.html) namespace.

## Defining custom HTML templates

**λ-blog** templates are built using **S-HTML**, so a sensible way of going about defining custom templates is to reuse existing templates and extend them with some more **S-HTML**:

```clojure
(require '[lambda-blog.templates.page :refer [page]])
(require '[s-html.tags :as html])

(defn my-custom-contents [{:keys [contents title]}]
  (html/div {:class :container}
            (html/h1 title)
            (html/p contents)))

(def my-template (partial page my-custom-contents))
```
A somewhat more detailed description of **S-HTML** can be found [here](https://github.com/Idorobots/s-html).

Of course, you can just as easily use any other HTML generation/templating library - just make sure to return stringified HTML from your template functions.
