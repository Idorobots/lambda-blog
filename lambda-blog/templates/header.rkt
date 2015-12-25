#lang racket

(require "../html.rkt" "../entities.rkt" "utils.rkt")

(define (page-header entity)
  (let ((p (get 'path-to-root entity)))
    (head (meta 'charset: 'utf-8)
          (title (get 'title entity))
          (link 'rel: 'stylesheet
                'type: 'text/css
                'href: (path p "style/theme.css"))
          (link 'rel: 'stylesheet
                'type: 'text/css
                'href: (path p "style/lambda-blog.css"))
          (link 'rel: 'alternate
                'type: 'application/rss+xhtml
                'tytle: "RSS Feed"
                'href: (path p "index.xml"))
          (link 'rel: 'icon
                'type: 'image/png
                'href: (path p "media/favicon.png"))
          (meta 'name: 'viewport
                'content: "width=device-width, initial-scale=1.0")
          (meta 'name: 'generator
                'content: "λ-blog"))))

(provide page-header)
