#lang racket

(require "../html.rkt" "../entities.rkt" "utils.rkt")

(define (page-banner entity)
  (header 'class: 'page-header
          (div 'class: 'row
               (div 'class: "hidden-xs col-sm-2 col-md-1"
                    (div 'class: "text-center"
                         (img 'class: "logo"
                              'src: (path (get 'path-to-root entity)
                                          "media/logo-main.png"))))
               (div 'class: "col-xs-12 col-sm-8 col-md-10"
                    (div 'class: "text-center"
                         (get 'banner-contents entity))))))

(provide page-banner)
