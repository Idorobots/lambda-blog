$(document).ready(function() {
    hljs.initHighlightingOnLoad();

    $('.tablesorter').tablesorter({
        theme: 'bootstrap',
        headerTemplate: '{content} {icon}',
        cssIconAsc: 'fa fa-sort-asc',
        cssIconDesc: 'fa fa-sort-desc',
        cssIconNone: 'fa fa-sort'
    });
});
