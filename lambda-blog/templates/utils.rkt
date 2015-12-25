#lang racket

(define (path . parts)
  (foldl (lambda (p acc)
           (string-append acc "/" p))
         "."
         (filter (compliment (partial equal? ""))
                 (flatten (map (partial parse "/")
                               parts)))))

(define (parse separator path)
  (regexp-split separator path))

(define (partial f . args)
  (lambda more-args
    (apply f (append args more-args))))

(define (compliment f)
  (lambda args
    (not (apply f args))))

(provide path)
