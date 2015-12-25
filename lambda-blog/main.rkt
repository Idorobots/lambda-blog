#lang racket/base

(module+ test
  (require rackunit
           "private/html.rkt")

  (check-equal? (hello)
                "<html>Hello.</html>"))

(module+ main
  (require "private/html.rkt")
  (provide (all-from-out "private/html.rkt")))
