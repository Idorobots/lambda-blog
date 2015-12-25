#lang racket

(require "../html.rkt" "../entities.rkt" "utils.rkt")

(define (page-footer entity)
  (let ((pt (get 'path-to-root entity)))
    (list (footer (div 'class: 'row
                       (div 'class: 'text-center
                            (get 'footer-contents entity)
                            (p "Powered by "
                               (a 'href: "https://github.com/idorobots/lambda-blog"
                                  'λ-blog)
                               "."))))
          (script 'type: 'text/javascript
                  'src: (path pt "js/jquery-1.11.0.min.js"))
          (script 'type: 'text/javascript
                  'src: (path pt "js/bootstrap.min.js"))
          (script 'type: 'text/javascript
                  'src: (path pt "js/lambda-blog.js")))))

(provide page-footer)
