#lang racket/base

(require "../html.rkt")

(define (hello)
  (xml->string (html "Hello.")))

(provide hello)

(module+ test
  (require rackunit)

  (check-equal? (hello)
                "<html>Hello.</html>"))
