#lang racket/base

(require "../html.rkt")

(define (hello)
  (html "Hello."))

(provide hello)

(module+ test
  (require rackunit)

  (check-equal? (xml->string (hello))
                "<html>Hello.</html>"))
