Timestamp: #inst "2016-02-23T15:07:00+01:00"
Title: "Basics"
ID: basic-info

# Basic Information

This page gives an overview of how **λ-blog** works internally. Let's jump straight in:

![operation]({{url}}/media/operation.svg)

**λ-blog** is a modular design with emphasis on hackability & customizability. There are three distinct phases of site generation in **λ-blog**:

- **Content parsing** - takes your content in a textual form and transforms it into a set of **Entities** that represent it in a convenient for processing way.

- **Generation** - is a pipeline of multiple **Middleware** functions that transform your *Entities* in various useful ways.

- **Templating** - is the final phase which results in HTML documents being generated; it uses an HTML templating engine.

The following sections contain short descriptions of mentioned concepts as well as pointers to more in-depth documentation.

## Entities

Every phase processes *Entities* in one way or another. *Entities* are simple nested Clojure data structures, such as maps and vectors, that are convenient for processing. The definition is intentionally left vague, giving you the most freedom of expression. A more detailed description can be found [here]({{url}}/entities.html).

## Content parsers

*Content parsers* take your data in a given format (be it Markdown or any format you wish to implement, for instance, [git commit history](https://github.com/Idorobots/lambda-blog/blob/1.1.1/lambda-blog/test/lambda_blog/fixtures.clj#L90-L115)) and coerce it to *Entities*. More details can be found [here]({{url}}/content-parsers.html).

## Generation pipeline

*Generation pipeline* is a multi-step process which transforms your *Entities* one *Middleware* at a time. Experimentation is encouraged. More details are available [here]({{url}}/generation-pipeline.html).

## Generaton middleware

*Generation middleware* are simple functions that modify your *Entities* in arbitrary ways. Again, experimentation is encouraged. More details are available [here]({{url}}/generator-middleware.html).

## HTML templates

*HTML templates* are responsible for the *actual* generation. They produce HTML documents and can easily be customized to your liking. More info [here]({{url}}/html-templates.html).
