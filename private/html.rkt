#lang racket/base

(require scribble/html)

(define (hello)
  (xml->string (html "Hello.")))

(provide hello)
