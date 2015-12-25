#lang racket

(require "../html.rkt" "../entities.rkt"
         "banner.rkt" "footer.rkt" "header.rkt" "navigation.rkt"
         "utils.rkt")

(define (static-page entity)
  (list (doctype 'html)
        (html (page-header entity)
              (body (navigation entity)
                    (div 'class: 'body-wrap
                         (article 'id: 'page
                                  (div 'class: 'container
                                       (page-banner entity)
                                       (get 'contents entity)
                                       (page-footer entity))))))))

(provide static-page)
